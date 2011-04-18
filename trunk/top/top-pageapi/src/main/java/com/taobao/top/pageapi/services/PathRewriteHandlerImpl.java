package com.taobao.top.pageapi.services;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.loadbalance.http.api.PathRewriteHandler;
import com.taobao.top.core.Api;
import com.taobao.top.core.ApiConfigException;
import com.taobao.top.core.ApiFactory;

public class PathRewriteHandlerImpl implements PathRewriteHandler {
	private static final transient Log logger = LogFactory.getLog(PathRewriteHandlerImpl.class);
	private ApiFactory apiFactory;
	
	public ApiFactory getApiFactory() {
		return apiFactory;
	}

	public void setApiFactory(ApiFactory apiFactory) {
		this.apiFactory = apiFactory;
	}

	@Override
	public String processAndreturnPath(HttpServletRequest req) {
		
		String pathInfo = null;
		String method = req.getParameter("method");
		if (method != null) {
			Api api = null;
			try {
				api = apiFactory.getApi(method);
				if (api != null) {
					pathInfo = api.getRequestURL();
				}
				int i = pathInfo.lastIndexOf("/");
				pathInfo = pathInfo.substring(i);
			} catch (ApiConfigException e1) {
				logger.error("failed to get api",e1);
			}
		} else {
			pathInfo = req.getPathInfo() == null ? "" : req
					.getPathInfo();
			if (pathInfo != null) {
				int i = pathInfo.lastIndexOf("/");
				pathInfo = pathInfo.substring(i);
			}
		}
		return pathInfo;
	}

}
