package com.taobao.top.ats.engine;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.hsf.app.spring.util.SuperHSFSpringConsumerBeanTop;
import com.taobao.hsf.exception.HSFException;
import com.taobao.remoting.RemotingException;
import com.taobao.top.ats.engine.hsf.ConsumerFactory;
import com.taobao.top.ats.util.ErrorCode;
import com.taobao.top.traffic.mapping.OperationCodeException;

/**
 * API调用引擎默认实现。
 * 
 * @author carver.gu
 * @since 1.0, Nov 18, 2010
 */
public class ApiEngineImpl implements ApiEngine {

	private static final Log log = LogFactory.getLog(ApiEngineImpl.class);

	private ConsumerFactory consumerFactory;
	private String hsfSuffix;

	public void setConsumerFactory(ConsumerFactory consumerFactory) {
		this.consumerFactory = consumerFactory;
	}

	public void setHsfSuffix(String hsfSuffix) {
		if (!"${top.ats.hsf.suffix}".equals(hsfSuffix)) {
			this.hsfSuffix = hsfSuffix;
		}
	}

	public ApiResponse invokeApi(ApiRequest request) {
		ApiResponse rsp = new ApiResponse();

		try {
			String hsfVersion = getHsfVersion(request.getInterfaceVersion(), hsfSuffix);
			SuperHSFSpringConsumerBeanTop consumer = consumerFactory.getConsumer(request
					.getInterfaceName(), hsfVersion);

			if (request.getParameters() == null) {
				request.setParameters(new HashMap<String, String>());
			}

			Map<String, String> params = request.getParameters();
			params.put("v", "2.0");
			String[] argTypes = new String[] { "f", null };
			Object[] argValues = new Object[] { getResponseFormat(params), params };

			Object result = consumer.invoke(request.getInterfaceMethod(), argTypes, argValues);
			if (result instanceof Object[]) {
				Object[] objs = (Object[]) result;
				Object data = objs[0];
				if (data instanceof OperationCodeException) {
					OperationCodeException oce = (OperationCodeException) data;
					rsp.setErrCode(oce.getCode());
					rsp.setErrMsg(oce.getMsg());
				} else if (data instanceof Exception) {
					processException(rsp, (Exception) data);
				} else {
					rsp.setResponse(String.valueOf(data));
				}
			} else {
				log.warn("未知返回结果：" + result);
				rsp.setResponse(String.valueOf(result));
			}
		} catch (Throwable e) {
			processException(rsp, e);
		}

		return rsp;
	}

	private int getResponseFormat(Map<String, String> params) {
		String format = params.get("format");
		if ("xml".equals(format)) {
			return 1;
		} else {
			return 0;
		}
	}

	private void processException(ApiResponse rsp, Throwable e) {
		String errorCode = ErrorCode.ATS_SERVICE_UNAVAILABLE;
		String errorMsg = null;
		if (e == null) {
			errorMsg = "API调用服务异常";
		} else if (e instanceof UndeclaredThrowableException) {
			Throwable oe = ((UndeclaredThrowableException) e).getUndeclaredThrowable();
			if (isTimeOutException(oe)) {
				errorCode = ErrorCode.REMOTE_SERVICE_TIMEOUT;
				errorMsg = "API调用服务超时";
			} else if (oe instanceof HSFException || oe instanceof RemotingException) {
				Throwable cause = oe.getCause();
				if (cause != null && isTimeOutException(cause)) {
					errorCode = ErrorCode.REMOTE_SERVICE_TIMEOUT;
					errorMsg = "API调用服务超时";
				} else {
					errorCode = ErrorCode.REMOTE_CONNECTION_ERROR;
					errorMsg = "API调用远程连接错误";
				}
			} else {
				errorCode = ErrorCode.REMOTE_SERVICE_ERROR;
				errorMsg = "API调用远程服务错误";
			}
		} else if (e instanceof NullPointerException) {
			errorCode = ErrorCode.NULL_POINTER_EXCEPTION;
			errorMsg = "API空指针异常:" + e.getMessage();
		} else if (e instanceof IllegalArgumentException || e instanceof NumberFormatException) {
			errorCode = ErrorCode.API_PARSE_ERROR;
			errorMsg = "API参数解析错误:" + e.getClass().getName() + ":" + e.getMessage();
		} else {
			errorMsg = e.getClass().getName() + ":" + e.getMessage();
		}

		if (log.isErrorEnabled()) {
			log.error(errorCode, e);
		}

		rsp.setErrCode(errorCode);
		rsp.setErrMsg(errorMsg);
	}

	private boolean isTimeOutException(Throwable e) {
		String errMsg = e.toString();
		if (StringUtils.isNotEmpty(errMsg)) {
			return errMsg.indexOf("com.taobao.remoting.TimeoutException") >= 0
					|| errMsg.indexOf("com.taobao.hsf.exception.HSFTimeOutException") >= 0;
		} else {
			return false;
		}
	}

	private String getHsfVersion(String interfaceVersion, String hsfSuffix) {
		if (StringUtils.isNotBlank(interfaceVersion) && StringUtils.isNotBlank(hsfSuffix)) {
			char last = interfaceVersion.charAt(interfaceVersion.length() - 1);
			if (Character.isDigit(last)) {
				return interfaceVersion + hsfSuffix;
			}
		}
		return interfaceVersion;
	}

}
