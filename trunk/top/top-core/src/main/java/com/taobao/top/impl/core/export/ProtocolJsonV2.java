/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.impl.core.export;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.taobao.top.common.TopPipeConfig;
import com.taobao.top.common.lang.EscapeKit;
import com.taobao.top.common.lang.StringKit;
import com.taobao.top.core.ApiExporter;

import com.taobao.top.core.ErrorCodeTransform;
import com.taobao.top.core.ProtocolConstants;
import com.taobao.top.core.ResultSet;
import com.taobao.top.core.TopException;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.traffic.mapping.MappingException;
import com.taobao.top.traffic.mapping.MemberMapping;
import com.taobao.top.traffic.mapping.OperationCodeException;
import com.taobao.top.traffic.mapping.config.MappingClassReader;
import com.taobao.top.traffic.mapping.config.MemberMappingHelper;
import com.taobao.top.traffic.util.Utils;
import com.taobao.util.CollectionUtil;

/**
 * @author jiangyongyuan.tw
 */
public class ProtocolJsonV2 extends ProtocolBase implements ApiExporter {

	protected final transient Log log = LogFactory.getLog(this.getClass());
	private TopPipeConfig config = TopPipeConfig.getInstance();
	/** 得到MemberMapping的辅助类 */
	MemberMappingHelper mappingHelper = new MemberMappingHelper();

	/** mapping 类装载器 */
	MappingClassReader mappingReader = new MappingClassReader(
			Utils.UNDERLINE_STYLE);

	/** 存储ClassName,MemberMapping */
	HashMap<String, MemberMapping<?>> mappingFactory = new HashMap<String, MemberMapping<?>>();

	/**
	 * 获得MemberMapping做解析
	 * 
	 * @throws MappingException
	 * @throws OperationCodeException 
	 */
	private MemberMapping<?> getMemberMapping(Object clazz)
			throws MappingException, OperationCodeException {
		if(clazz instanceof Map){
			MemberMapping<?> mapMapping = mappingHelper.getMapMapping((Map)clazz);
			return mapMapping;
		}
		
		String key = clazz.getClass().getName();
		MemberMapping<?> memberMapping = mappingFactory.get(key);
		if (memberMapping == null) {
			memberMapping = mappingReader.getMemberMapping(clazz);
			mappingFactory.put(key, memberMapping);
		}
		return memberMapping;
	}


	/**
	 * 使用mapping输出
	 * 
	 * @param source
	 * @param writer
	 * @throws MappingException
	 */
	private void mappingWriteBlackBoxResponse(Object source, Writer writer) {
		try {
			MemberMapping<?> memberMapping = getMemberMapping(source);
			boolean isFromArray = false;
			memberMapping.write(writer, source, ProtocolConstants.FORMAT_JSON,
					isFromArray);
		} catch (MappingException e) {
			// TODO exception handler
			log.error("export xml error", e);
		} catch (OperationCodeException e) {
			log.error("export xml error", e);
		}
	}


	/**
	 * 使用mapping输出
	 * 
	 * @param result
	 * @param writer
	 * @throws MappingException
	 * @throws OperationCodeException
	 */
	@SuppressWarnings("unchecked")
	void mappingWrite(ResultSet result, Writer writer) throws MappingException,
			OperationCodeException {
		List list = result.getResult();

		if (result.isSingleResult()) {
			mappingHelper.writeSingleResponse(list.get(0), writer,
					ProtocolConstants.FORMAT_JSON);
		} else
			try {
				mappingHelper.writeListResponse(list, writer,
						ProtocolConstants.FORMAT_JSON);
			} catch (IOException e) {
				log.error("输出过程中出现IO异常", e);
			}
	}
	@Override
	public void export(HttpServletResponse response, TopPipeInput pipeInput,
			TopPipeResult apiResult, ErrorCodeTransform errorCodeTransform) throws IOException, TopException {
		try {
			export(response.getWriter(), pipeInput, apiResult, errorCodeTransform);
		} catch (MappingException e) {
			// TODO Auto-generated catch block
			log.error("", e);
		} catch (OperationCodeException e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
	}
	@SuppressWarnings("unchecked")
	void export(Writer writer, TopPipeInput input, TopPipeResult apiResult, ErrorCodeTransform errorCodeTransform)
			throws IOException, MappingException, OperationCodeException, TopException {
		String callback = input.getString(ProtocolConstants.P_CALLBACK, true);// FIXME 需要进一步确认是否还有用 
		if (StringUtils.isNotEmpty(callback)) {
			writer.write(callback);
			writer.write("(");
		}
		if (apiResult.isSuccess()) {
			String rootElement = StringKit.concatenate("{\"",
					toResponseRootNameV2(input.getApiName()), "\":");
			writer.write(rootElement);

			Object black = apiResult.getBlack();
			if (black != null) {
				if (black instanceof String)// 如果是字符串
					writer.write((String) black);
				else {
					//此处是个object可以处理,如果返回是个Map,暂时不支持,可能需要处理
					mappingWriteBlackBoxResponse(black, writer);
				}
				writer.write("}");
			} else {
				writer.write("{");
				ResultSet result = apiResult.getResultSet();

				super.writeSystemResponseJson(writer, apiResult,true);

				if (null != result
						&& CollectionUtil.isNotEmpty(result.getResult())) {
					mappingWrite(result, writer);
				}
				writer.write("}}");// Mapping已经输出{},故添加一个即可 writer.write("}}");
			}
		} else {
			writeJsonErrorResonponseV2(writer, input, apiResult, errorCodeTransform);
		}

		if (StringUtils.isNotEmpty(callback)) {
			writer.write(")");
		}
		// 写入集群机器名
		// writer.write("##");
		// writer.write(SystemUtil.getHostInfo().getName());
		// writer.write("##");
	}
	void writeJsonErrorResonponseV2(Writer writer, TopPipeInput input,
			TopPipeResult apiResult, ErrorCodeTransform errorCodeTransform) throws IOException, TopException {
		String style = input.getString(ProtocolConstants.P_STYLE, true);
		boolean isCamel = (style != null && style.equals("camel"));

		if (isCamel) {
			writer.write("{\"errorResponse\":");
		} else { // underline
			writer.write("{\"error_response\":");
		}
		writer.write("{\"args\":{");
		writer.write("\"arg\":[");
		String[] names = input.getParameterNames().toArray(
				ArrayUtils.EMPTY_STRING_ARRAY);
		Arrays.sort(names);
		boolean first = true;
		for (int i = 0; i < names.length; i++) {
			String name = names[i];

			if (isSensitiveField(name)) {
				continue;
			}
			if(config.isFilterMHTML() && isFilterValue(name, input.getString(name,false))){
				continue;
			}
			if (!first) {
				writer.write(",");
			}
			String value = StringUtils.trimToEmpty(input.getString(name, true));
			writer.write("{\"key\":\"");
			// Add Html escape, preventing XSS attacking.
			writer.write(EscapeKit.escapeHtml(name));
			writer.write("\",\"value\":\"");
			writer.write(EscapeKit.escapeAfterTrimJson(EscapeKit.escapeHtml(value)));
			writer.write("\"}");

			first = false;
		}

		writer.write("]},");
		writer.write("\"code\":");
		writer.write(String.valueOf(errorCodeTransform.getSpecificCode(input.getApiName(), apiResult.getCode())));
		writer.write(",\"msg\":\"");
		writer.write(EscapeKit.escapeAfterTrimJson(EscapeKit.escapeHtml((apiResult.getMsg()))));

		// Add support for sub code
		String subCode = apiResult.getSubCode();
		if (!StringUtils.isEmpty(subCode)) {
			if (isCamel) {
				writer.write("\",\"subCode\":\"");
			} else {
				writer.write("\",\"sub_code\":\"");
			}
			writer.write(subCode);
		}
		
		String subMsg = apiResult.getSubMsg();
		
		if (!StringUtils.isEmpty(subMsg)) {
			if (isCamel) {
				writer.write("\",\"subMsg\":\"");
			} else {
				writer.write("\",\"sub_msg\":\"");
			}
			writer.write(EscapeKit.escapeAfterTrimJson(EscapeKit.escapeHtml(subMsg)));
		}
		

		writer.write("\"}}");
	}
}
