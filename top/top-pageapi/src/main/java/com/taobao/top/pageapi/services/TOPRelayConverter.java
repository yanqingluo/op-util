package com.taobao.top.pageapi.services;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.taobao.loadbalance.http.PatternRelayConverter;
import com.taobao.top.core.Api;
import com.taobao.top.core.ApiConfigException;
import com.taobao.top.core.ApiFactory;
import com.taobao.top.pageapi.common.EncryptException;
import com.taobao.top.pageapi.common.SignUtils;
/**
 * 处理访问请求path，转换为业务方提供的path
 * 
 * @version 2009-08-16
 * @author <a href="mailto:yueqian@taobao.com">yueqian</a>
 * 
 */
public class TOPRelayConverter extends PatternRelayConverter {

	private ApiFactory apiFactory;
	
	private static final transient Log logger = LogFactory.getLog(TOPRelayConverter.class);
	
	public ApiFactory getApiFactory() {
		return apiFactory;
	}
	
	private static final String SIGN_MAP_QUERY_STRING = "query_string";
	
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
    /**
	 * 构造传入软负载url参数
	 * url模板，例如{0}/{1}/{2}/trade.jsp{3} 
	 * {0}: serverAddress {1}:
	 * HttpServletRequest.getContextPath() {2}：HttpServletRequest.getPathInfo()
	 * {3}：? + HttpServletRequest.getQueryString()
	 * {4}及更多，为用户自定义参数。通过extraParams传入
	 */
	protected List<? extends Object> getPatternArgs(HttpServletRequest req,
			String serverAddress) {
		List<Object> res = new ArrayList<Object>();
		res.add(serverAddress); // {0}
		String contextPath = req.getContextPath();
		res.add(contextPath); // {1}
        String pathInfo = getPathInfo(req);
        res.add(pathInfo); //{2}
        String queryStr = req.getQueryString();
        String page = req.getPathInfo();
        
        queryStr = queryStr==null? "": parseQueryString(queryStr,page);
		String parsedQueryStr = queryStr == null ? "" : "?"
				+ queryStr; // {3}
		res.add(parsedQueryStr);
		return res;
	}

	private static String parseQueryString(String queryStr ,String path) {
		String parsedString = null ;
		HashMap<String, String> map = getQueryMap(queryStr,path);
		parsedString = map.get(SIGN_MAP_QUERY_STRING);
		return parsedString;
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
	/**
	 * 解析queryString 参数为Map
	 * @param queryString
	 * @return
	 */
	private static HashMap<String, String> getQueryMap(String queryString,String pathInfo) {
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer ps = new StringBuffer();
		String[] params = queryString.split("\\&");
		Arrays.sort(params);
		try {
			for (String p : params) {				
				int equalPos = p.indexOf("=");
				if (equalPos == -1)
					continue; // 不正确的参数会被丢弃
				String pname = p.substring(0, equalPos);
				String pvalue = p.substring(equalPos + 1);
				if (ps.length() > 0)
					ps.append("&");
				if ("nick".equals(pname)){
					
				}else if (pvalue.indexOf("%") != -1) { // 已经encode 过，不需要再encode
					ps.append(pname).append("=").append(pvalue);
				} else {
					ps.append(pname).append("=").append(
							URLEncoder.encode(pvalue, "GBK"));// TM 使用GBK 编码
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("URL参数编码[" + queryString + "]转换出错:" + e);
			map.put(SIGN_MAP_QUERY_STRING, "");
			return map;
		}
		map.put(SIGN_MAP_QUERY_STRING, ps.toString());
		return map;
	}

}
