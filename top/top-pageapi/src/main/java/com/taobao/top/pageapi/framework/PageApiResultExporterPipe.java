package com.taobao.top.pageapi.framework;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.core.ApiExporter;
import com.taobao.top.core.framework.pipe.TopPipe;
import com.taobao.top.pageapi.core.impl.TopPageAPIResult;
/**
 * 页面流程化的输出
 * @author zhenzi
 *
 */
public class PageApiResultExporterPipe extends TopPipe<TopPagePipeInput, TopPageAPIResult> {
	private static final transient Log log = LogFactory.getLog(PageApiResultExporterPipe.class);
	
	private static final String STR_CONTENT_TYPE = "text/html;charset=UTF-8";
	
	private ApiExporter apiExporter;

	public void setApiExporter(ApiExporter apiExporter) {
		this.apiExporter = apiExporter;
	}
	@Override
	public void doPipe(TopPagePipeInput pipeInput, 
			TopPageAPIResult pipeResult) {
		HttpServletResponse response = pipeInput.getResponse();
		response.setContentType(STR_CONTENT_TYPE);
		
		try {
			apiExporter.export(response, pipeInput, pipeResult, null);
		} catch (Exception e) {
			log.error(e,e);
		}
	}
	
}
