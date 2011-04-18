package com.taobao.top.core.framework.pipe;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.common.TOPConfig;
import com.taobao.top.common.TOPConstants;
import com.taobao.top.core.ApiExporter;
import com.taobao.top.core.ProtocolConstants;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;

/**
 * 把最终结果输出
 * @author  zhenzi
 *
 */
public class ResultExporterPipe extends TopPipe<TopPipeInput, TopPipeResult>{
	private static final Log logger = LogFactory.getLog(ResultExporterPipe.class);
	
	private static final String XML_CONTENT_TYPE = "text/xml;charset=UTF-8";
	private static final String JS_CONTENT_TYPE = "text/javascript;charset=UTF-8";
	private static final String HTML_CONTENT_TYPE = "text/html;charset=UTF-8";
	
	private ApiExporter apiExporter = null;
	public void setApiExporter(ApiExporter apiExporter) {
		this.apiExporter = apiExporter;
	}
	@Override
	public void doPipe(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		//------------准备输出----------
		HttpServletResponse response = pipeInput.getResponse();
		if (ProtocolConstants.FORMAT_JSON.equals(pipeInput.getFormat())) {
			response.setContentType(JS_CONTENT_TYPE);
		} else if (ProtocolConstants.FORMAT_STR.equals(pipeInput.getFormat())) {
			response.setContentType(HTML_CONTENT_TYPE);
		} else { // default format is xml
			response.setContentType(XML_CONTENT_TYPE);
		}
		//增加内部服务器ip地址，便于定位
		response.addHeader(TOPConstants.TOP_INNERIP, TOPConfig.getInstance().getLocalAddress());
		try {
			apiExporter.export(pipeInput.getResponse(), pipeInput, pipeResult, null);
		} catch (Exception e) {
			logger.error("export result error:", e);
		}
	}

}
