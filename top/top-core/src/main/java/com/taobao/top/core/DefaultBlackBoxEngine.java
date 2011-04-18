/**
 * 
 */
package com.taobao.top.core;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.hsf.app.spring.util.SuperHSFSpringConsumerBeanTop;
import com.taobao.hsf.exception.HSFException;
import com.taobao.top.common.lang.StringKit;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.traffic.mapping.MemberMapping;
import com.taobao.top.traffic.mapping.MethodCallMapping;
import com.taobao.top.traffic.mapping.MethodMapping;
import com.taobao.top.traffic.mapping.MethodResponseMapping;
import com.taobao.top.traffic.mapping.OperationCodeException;
import com.taobao.util.CollectionUtil;

/**
 * @version 2009-2-9
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public class DefaultBlackBoxEngine implements BlackBoxEngine {
	private static final Log log = LogFactory
			.getLog(DefaultBlackBoxEngine.class);

	private ConsumerFactory consumerFactory;

	/**
	 * @param consumerFactory
	 *            the consumerFactory to set
	 */
	public void setConsumerFactory(ConsumerFactory consumerFactory) {
		this.consumerFactory = consumerFactory;
	}

	private static final String resultFormat = "f";

	

	private Object callHsf(String api, String method, Long timeout,
			SuperHSFSpringConsumerBeanTop consumer, String[] argTypes,
			Object[] argValues, TopPipeResult pipeResult) throws InterruptedException, HSFException,
			Exception {
		Object invoke = null;
		// FIXME zixue, try 10 times
		try {
			// FIXME zixue, hsf hack
			if (timeout != null) {
				Field f = SuperHSFSpringConsumerBeanTop.class.getDeclaredField("metadata");
				f.setAccessible(true);
				// hack hsf classloader
				Class<?> msClz = SuperHSFSpringConsumerBeanTop.class.getClassLoader().loadClass("com.taobao.hsf.model.metadata.MethodSpecial");
				Object ms = msClz.newInstance();
				msClz.getMethod("setMethodName", String.class).invoke(ms, method);
				msClz.getMethod("setClientTimeout", long.class).invoke(ms, timeout);
				Object array = Array.newInstance(msClz, 1);
				Array.set(array, 0, ms);
				Class<? extends Object> arrayClz = array.getClass();
				Object sm = f.get(consumer);
				Class<?> smClz = SuperHSFSpringConsumerBeanTop.class.getClassLoader().loadClass("com.taobao.hsf.model.metadata.ServiceMetadata");
				smClz.getMethod("setMethodSpecials", arrayClz).invoke(sm, array);
				// ms.setMethodName(method);
				// ms.setClientTimeout(timeout);
				// metadata.setMethodSpecials(new MethodSpecial[]{ms});
			}
			invoke = consumer.invoke(method, argTypes, argValues);
		} catch (OperationCodeException e) {
			log.error(new StringBuilder("method=").append(method).append(",抛出业务异常:").append(e.getMessage()).toString());
			throw e;
		} catch (HSFException e) {
			log.error(new StringBuilder("method=").append(method).append(",抛出HSF异常:").toString(), e);
			throw e;
		} catch (Exception e) {
			log.error(new StringBuilder("method=").append(method).append(",抛出其他异常:").toString(), e);
			throw e;
		}
		// 结果如果是异常,直接抛出(兼容hsf1.3.3,zixue)
		if (invoke instanceof Object[]) {
			Object[] results = (Object[]) invoke;
			if (null != pipeResult) {
				long responseMappingTime = results[1] == null ? 0L
						: (Long) results[1];
				pipeResult.setResponseMappingTime(responseMappingTime);
			}
			//set true invoke result
			invoke = results[0];
		}
		if (invoke instanceof Exception) {
			if (invoke instanceof OperationCodeException) {
				log.error( method + "返回业务异常:" + ((Exception) invoke).getMessage());
			} else {
				log.error("返回其他异常", (Exception) invoke);
			}
			throw (Exception) invoke;
		}
		return invoke;
	}

	private final static String DAILY_POSTFIX = ".daily";
	private final static String SANDBOX_POSTFIX = ".sandbox";

	private boolean enableHsfVersionDailyPostfix = false;
	private boolean sandbox = false;

	public void setEnableHsfVersionDailyPostfix(
			boolean enableHsfVersionDailyPostfix) {
		this.enableHsfVersionDailyPostfix = enableHsfVersionDailyPostfix;
	}

	String appendPostfix(String version, String postfix) {

		if (version.endsWith(postfix)) {
			return version;
		} else {
			int pos = version.lastIndexOf('.');
			if (pos == -1) { // no dot. add post fix
				return version + postfix;

			}
			String lastVersion = version.substring(pos + 1, version.length());
			try {
				Long.parseLong(lastVersion);
			} catch (Exception e) { // failed to parse, not a number, return
				// original version.
				return version;

			}

			// last sub-version is number, safe to append the daily postfix
			return version + postfix;

		}
	}

	String processVersion(String version) {
		if (version == null) {
			return null;
		}

		if (sandbox) { // sandbox processing first.

			return this.appendPostfix(version, SANDBOX_POSTFIX);

		} else if (this.enableHsfVersionDailyPostfix) { // then, daily

			return this.appendPostfix(version, DAILY_POSTFIX);

		} else {
			return version;
		}
	}


	public boolean isSandbox() {
		return sandbox;
	}

	public void setSandbox(boolean sandbox) {
		this.sandbox = sandbox;
	}

	@Override
	public void execute(TopPipeResult pipeResult, TopPipeInput pipeInput, Api api) throws Exception {
		String hsfInterfaceName = api.getHsfInterfaceName();
		String hsfInterfaceVersion = api.getHsfInterfaceVersion();
		hsfInterfaceVersion = processVersion(hsfInterfaceVersion);
		String hsfMethodName = api.getHsfMethodName();
		Long timeout = api.getHsfTimeout();

		// 是否存在,判断是否执行v1
		if (hsfInterfaceName == null || hsfInterfaceVersion == null) {
			executeV1(pipeResult, pipeInput, api);
			return;
		}

		String[] argTypes = new String[2];
		Object[] argValues = new Object[2];
		int formatType = ProtocolConstants.FORMAT_XML.equals(pipeInput
				.getFormat()) ? 1 : 0;
		argTypes[0] = resultFormat;
		argValues[0] = formatType;
		argValues[1] = ApiInputMapping.getHsfMappingParam(pipeInput, api);
		if (argValues.length != argTypes.length) {
			throw new IllegalStateException("Values and Types unmatch!");
		}
		if (log.isDebugEnabled()) {
			log.debug("Invoke parameters value:\n"
					+ ReflectionToStringBuilder.toString(argValues));
			log.debug("Invoke parameters type:\n"
					+ ReflectionToStringBuilder.toString(argTypes));
		}
		SuperHSFSpringConsumerBeanTop consumer = consumerFactory.getConsumer(
				hsfInterfaceName, hsfInterfaceVersion);
		Object invoke = callHsf(api.getName(), hsfMethodName, timeout, consumer, argTypes, argValues, pipeResult);
		if (log.isDebugEnabled()) {
			log.debug("Invoke result:\n" + StringKit.dump(invoke));
		}
		pipeResult.setBlack(invoke);
	}

	public void executeV1(TopPipeResult pipeResult, TopPipeInput input, Api api) throws Exception {
		MethodMapping<Object> mm = api.getMethodMapping();
		MethodCallMapping mcm = mm.getMethodCallMapping();
		MethodResponseMapping<Object> mrm = mm.getMethodResponseMapping();
		List<MemberMapping<?>> pms = mcm.getParameterMappings();

		String[] argTypes = null;
		Object[] argValues = null;
		int formatType = 0;
		if (ProtocolConstants.FORMAT_XML.equals(input.getFormat())) {
			formatType = 1;
		}

		if (CollectionUtil.isEmpty(pms)) {
			argTypes = new String[] { resultFormat };
			argValues = new Object[1];
		} else {
			argTypes = new String[pms.size() + 1];
			argValues = new Object[argTypes.length];
			int i = 1;
			for (MemberMapping<?> param : pms) {
				argTypes[i] = param.getMappingType();
				Object value = ApiInputMapping.mapping(input, param);
				argValues[i] = value;
				i++;
			}
		}
		// 多了一个响应类型
		argTypes[0] = resultFormat;
		argValues[0] = formatType;
		if (argValues.length != argTypes.length) {
			throw new IllegalStateException("Values and Types unmatch!");
		}
		if (log.isDebugEnabled()) {
			log.debug("Invoke parameters value:\n"
					+ ReflectionToStringBuilder.toString(argValues));
			log.debug("Invoke parameters type:\n"
					+ ReflectionToStringBuilder.toString(argTypes));
		}
		SuperHSFSpringConsumerBeanTop consumer = consumerFactory.getConsumer(mm
				.getInterfaceName(), processVersion(mm.getInterfaceVersion()));
		Object invoke = callHsf(api.getName(), mcm.getName(), null, consumer, argTypes, argValues, null);

		if (log.isDebugEnabled()) {
			log.debug("Invoke result:\n" + StringKit.dump(invoke));
		}
		Object black = invoke;
		MemberMapping<Object> resultMapping = mrm.getParameterMapping();

		// 如果响应值有一个apiName,那么top这边惯例需要包一层标签
		if (resultMapping.getMappingName() != null) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(resultMapping.getMappingName(), invoke);
			black = map;
		}

		pipeResult.setBlack(black);
	}
}
