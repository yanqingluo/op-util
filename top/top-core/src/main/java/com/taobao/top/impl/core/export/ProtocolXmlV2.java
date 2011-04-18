/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.impl.core.export;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.common.lang.SystemUtil;

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
import com.taobao.util.CollectionUtil;

/**
 * @version 2009-9-22
 * @author jiangyongyuan.tw
 */
public class ProtocolXmlV2 extends ProtocolBase implements ApiExporter {
	private static final String xmlHeader = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>";

	protected final transient Log log = LogFactory.getLog(this.getClass());
	private TopPipeConfig config = TopPipeConfig.getInstance();
	/** 得到MemberMapping的辅助类 */
	MemberMappingHelper mappingHelper = new MemberMappingHelper();
	
	/** mapping 类装载器 */
	MappingClassReader mappingReader = new MappingClassReader();

	/** 存储ClassName,MemberMapping */
	HashMap<String, MemberMapping<?>> mappingFactory = new HashMap<String, MemberMapping<?>>();

	/** 获得MemberMapping做解析 
	 * @throws MappingException */
	private MemberMapping<?> getMemberMapping(Object clazz) throws MappingException {
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
			MemberMapping<?> memberMapping = getMemberMapping(source.getClass());
			boolean isFromArray = false;
			memberMapping.write(writer, source, ProtocolConstants.FORMAT_XML,
					isFromArray);
		} catch (MappingException e) {
			//TODO export error handler
			log.error("export xml error", e);
		} catch (OperationCodeException e) {
			log.error("export xml error", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void mappingWrite(ResultSet result, Writer writer) throws MappingException, OperationCodeException {
		List list = result.getResult();
		if(result.isSingleResult()){
			mappingHelper.writeSingleResponse(list.get(0), writer, ProtocolConstants.FORMAT_XML);
		} else
			try {
				mappingHelper.writeListResponse(list, writer, ProtocolConstants.FORMAT_XML);
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

	void export(Writer writer, TopPipeInput input, TopPipeResult apiResult, ErrorCodeTransform errorCodeTransform)
			throws IOException, MappingException, OperationCodeException, TopException {
		writer.write(xmlHeader);
		if (apiResult.isSuccess()) {

			writer.write(StringKit.concatenate("<", toResponseRootNameV2(input
					.getApiName()), ">"));

			Object black = apiResult.getBlack();
			if (null != black) {
				if (black instanceof String)// 如果是字符串
					writer.write((String) black);
				else
					mappingWriteBlackBoxResponse(black, writer);
			} else {
				ResultSet result = apiResult.getResultSet();
				if (null != result) {
					super.writeSystemResponseXml(writer, apiResult, true);
					if (null != result
							&& CollectionUtil.isNotEmpty(result.getResult())) {
						mappingWrite(result, writer);
					}

				}
			}
			writer.write(StringKit.concatenate("</", toResponseRootNameV2(input
					.getApiName()), ">"));
		} else {
			wirteXmlErrorResponseV2(writer, input, apiResult, errorCodeTransform);
		}
		// 写入集群机器名
		writer.write("<!--");
		writer.write(SystemUtil.getHostInfo().getName());
		writer.write("-->");
	}

	void wirteXmlErrorResponseV2(Writer writer, TopPipeInput input,
			TopPipeResult apiResult, ErrorCodeTransform errorCodeTransform) throws IOException, TopException {
		String style = input.getString(ProtocolConstants.P_STYLE, true);
		boolean isCamel = (style != null && style.equals("camel"));

		if (isCamel) {
			writer.write("<errorResponse>");
		} else {
			writer.write("<error_response>");
		}
		writer.write("<args list=\"true\">");

		String[] names = input.getParameterNames().toArray(
				ArrayUtils.EMPTY_STRING_ARRAY);
		Arrays.sort(names);
		for (int i = 0; i < names.length; i++) {
			String name = names[i];

			if (isSensitiveField(name)) {
				continue;
			}
			if(config.isFilterMHTML() && isFilterValue(name, input.getString(name, false))){
				continue;
			}
			String value = StringUtils.trimToEmpty(input.getString(name, true));
			writer.write("<arg>");
			writer.write("<key>");
			// No need to perform camel conversion
			// since the input names have been converted accordingly
			writer.write(name);
			writer.write("</key>");
			writer.write("<value>");
			if (StringUtils.isNotEmpty(value)) {
				writer.write(EscapeKit.escapeHtml(value));
			}
			writer.write("</value>");
			writer.write("</arg>");
		}
		writer.write("</args>");
		writer.write("<code>");
		writer.write(String.valueOf(errorCodeTransform.getSpecificCode(input.getApiName(),
				apiResult.getCode())));
		writer.write("</code>");
		writer.write("<msg>");
		writer.write(EscapeKit.escapeHtml(apiResult.getMsg()));
		writer.write("</msg>");

		// Support for sub code.
		String subCode = apiResult.getSubCode();
		if (!StringUtils.isEmpty(subCode)) {
			if (isCamel) {
				writer.write("<subCode>");
				writer.write(subCode);
				writer.write("</subCode>");

			} else { // under line
				writer.write("<sub_code>");
				writer.write(subCode);
				writer.write("</sub_code>");

			}
		}

		String subMsg = apiResult.getSubMsg();

		if (!StringUtils.isEmpty(subMsg)) {
			if (isCamel) {
				writer.write("<subMsg>");
				writer.write(EscapeKit.escapeHtml(apiResult.getSubMsg()));
				writer.write("</subMsg>");

			} else {
				writer.write("<sub_msg>");
				writer.write(EscapeKit.escapeHtml(apiResult.getSubMsg()));
				writer.write("</sub_msg>");
			}
		}

		if (isCamel) {
			writer.write("</errorResponse>");
		} else {
			writer.write("</error_response>");
		}
	}
}
