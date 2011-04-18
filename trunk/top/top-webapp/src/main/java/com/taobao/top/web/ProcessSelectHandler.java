package com.taobao.top.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;
/**
 * 在新旧apiRestHandler中选择用那个执行
 * @author zhenzi
 *
 */
public class ProcessSelectHandler implements HttpRequestHandler {
	private NewApiRestHandler newPageApiRestHandler = null;
	private NewApiRestHandler newPageApiUrlRequestHandler = null;

	public void setNewPageApiRestHandler(NewApiRestHandler newPageApiRestHandler) {
		this.newPageApiRestHandler = newPageApiRestHandler;
	}

	public void setNewPageApiUrlRequestHandler(
			NewApiRestHandler newPageApiUrlRequestHandler) {
		this.newPageApiUrlRequestHandler = newPageApiUrlRequestHandler;
	}

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String servletPath = request.getRequestURI();
		if (servletPath != null && servletPath.endsWith(".htm")) {
			newPageApiUrlRequestHandler.handleRequest(request, response);
		} else {
			newPageApiRestHandler.handleRequest(request, response);
		}
	}

}
