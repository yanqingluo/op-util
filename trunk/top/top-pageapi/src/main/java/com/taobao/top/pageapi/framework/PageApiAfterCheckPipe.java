package com.taobao.top.pageapi.framework;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.pipe.TopPipe;
import com.taobao.top.pageapi.common.TopCookieManager;
import com.taobao.top.pageapi.core.impl.TopPageAPIResult;
/***
 * 页面流程after处理
 * @author zhenzi
 *
 */
public class PageApiAfterCheckPipe extends TopPipe<TopPagePipeInput, TopPageAPIResult> {
	private static final transient Log log = LogFactory.getLog(PageApiAfterCheckPipe.class); 
	
	private static final String MULTIPART = "multipart/";

	private static final String POST = "post";
	@Override
	public void doPipe(TopPagePipeInput pipeInput, TopPageAPIResult pipeResult) {
		if (!isInProcessPage(pipeInput)) {
			TopCookieManager.setTOPAuthCookie(pipeInput.getAppKey(),pipeInput.getRequest(),pipeInput.getResponse());
		}
	}
	
	private boolean isInProcessPage(TopPipeInput pipeInput) {
		boolean isInProcess = false;
		String pageUrl = pipeInput.getString("pageUrl", true);
		if (pageUrl != null && "true".equals(pageUrl)) {
			isInProcess = true;
			if (log.isDebugEnabled()){
				log.debug("It is a in-proces page,so only need to check cooki and call limitation");
			}
			return isInProcess;
		}
		HttpServletRequest request = pipeInput.getRequest();
		String method = request.getMethod();
		String contentType = request.getContentType();
		if ((method != null && method.toLowerCase().equals(POST)
				&& contentType != null && contentType.toLowerCase().startsWith(
				MULTIPART))) {
			isInProcess = true;
			return isInProcess;
		}
		return isInProcess;
	}

}
