package com.taobao.top.core.framework;

import javax.servlet.http.HttpServletRequest;

public final class TopPipeUtil {
	private static final String MULTIPART = "multipart/";
	
	private static final String POST = "post";
	
	/**
	 * 判断请求的URI中是否是页面流程化的请求
	 */
	private static String topPageUrl = "/router/page";
	/**
	 * 决定是否忽略管道的执行
	 * @param pipeInput
	 * @return true忽略 
	 */
	public static boolean isIgnore(TopPipeInput pipeInput){
		HttpServletRequest request = pipeInput.getRequest();
		String url = request.getServletPath();
		if(url != null && url.startsWith(topPageUrl)){
			if(isInProcessPage(pipeInput.getString("pageUrl", true), request)){
				return true;
			}
		}
		return false;
	}
	private static boolean isInProcessPage(String pageUrl, HttpServletRequest request){
		boolean isInProcess = false;
		if (pageUrl != null && "true".equals(pageUrl)) {
			isInProcess = true;
			return isInProcess;
		}
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
