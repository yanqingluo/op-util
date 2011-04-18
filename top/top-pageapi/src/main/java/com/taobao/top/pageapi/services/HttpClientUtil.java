package com.taobao.top.pageapi.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.log4j.Logger;

import com.taobao.loadbalance.http.PoolingRelayTunnel;

public class HttpClientUtil {
	private static final Logger logger = Logger.getLogger(PoolingRelayTunnel.class);
	   
	public static StringBuilder dump(HttpMethodBase m) throws IOException {
		String str = m.getResponseBodyAsString();
		StringBuilder builder = new StringBuilder();
		builder.append(str);
		return builder;
	}

	/**
	 * TODO ���������header�Ĵ���
	 */
	public static void handleHeader(HttpServletRequest req, HttpMethod m) {
		Enumeration<String> e = req.getHeaderNames();
		while (e.hasMoreElements()) {
			String headerName = e.nextElement();
			m.setRequestHeader(headerName, req.getHeader(headerName));
		}
	}

	public static List<Header> getHeaders(HttpServletRequest req) {
		List<Header> headers = new ArrayList<Header>();
		Enumeration<String> e = req.getHeaderNames();
		while (e.hasMoreElements()) {
			String headerName = e.nextElement();
			headers.add(new Header(headerName, req.getHeader(headerName)));
		}
		return headers;
	}

	public static org.apache.commons.httpclient.Cookie convert(Cookie c) {
		return new org.apache.commons.httpclient.Cookie(c.getDomain(), c.getName(), c.getValue(), c.getPath(), c
				.getMaxAge(), c.getSecure());
	}

	public static org.apache.commons.httpclient.Cookie[] convert(Cookie[] cookies) {
		org.apache.commons.httpclient.Cookie[] apacheCookies = new org.apache.commons.httpclient.Cookie[cookies.length];
		for (int i = 0; i < cookies.length; i++) {
			apacheCookies[i] = convert(cookies[i]);
		}
		return apacheCookies;
	}
}
