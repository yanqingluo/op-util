package com.taobao.top.pageapi.services;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.taobao.loadbalance.http.PatternRelayConverter;
import com.taobao.top.core.Api;
import com.taobao.top.core.ApiConfigException;
import com.taobao.top.core.ApiFactory;

public class DomainNameRelayConverter extends PatternRelayConverter {

	private ApiFactory apiFactory;
	
	public ApiFactory getApiFactory() {
		return apiFactory;
	}
	
    private String domainName ;

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public void setApiFactory(ApiFactory apiFactory) {
		this.apiFactory = apiFactory;
	}

	private static final Logger logger = Logger.getLogger(DomainNameRelayConverter.class.getName());

	protected List<? extends Object> getPatternArgs(HttpServletRequest req,
			String serverAddress) {
		List<Object> res = new ArrayList<Object>();
		//res.add(serverAddress); // {0}
		res.add(domainName);
		String contextPath = req.getContextPath();
		res.add(contextPath); // {1}
        String pathInfo = getPathInfo(req);
        res.add(pathInfo); //{2}
		String queryStr = req.getQueryString() == null ? "" : "?"
				+ req.getQueryString(); // {3}
		res.add(queryStr);
		
		logger.debug("contextPath is "+contextPath);
		logger.debug("pathInfo is "+pathInfo);
		
		return res;
	}

	private String getPathInfo(HttpServletRequest req) {
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
				// log.error("", e1);
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
