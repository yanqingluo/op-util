/**
 * 
 */
package com.taobao.top.common.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @version 2008-7-27
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class UrlKit {
	public static Map<String, String> getMapFromParameters(String url) throws UnsupportedEncodingException {
		return getMapFromParameters(url, "utf-8");
	}
	
	public static Map<String, String> getMapFromParameters(String url, String encoding) throws UnsupportedEncodingException {
		int index = url.indexOf('?');
		if (index >= 0) {
			url = url.substring(index + 1);
		}
		String[] array = StringUtils.split(url, "&");
		HashMap<String, String> retMap = new HashMap<String, String>();
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				String item = array[i];
				String value = null;
				String key = null;
				int equalId = item.indexOf('=');
				if (equalId > 0) {
					key = item.substring(0, equalId);
					value = item.substring(equalId + 1);
				}
				if (value != null) {
					retMap.put(key, URLDecoder.decode(value, encoding));
				}
			}
		}
		return retMap;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> convertRequestMap(
			HttpServletRequest request) {
		Map<String, String[]> paramMap = request.getParameterMap();
		Map<String, String> params = new HashMap<String, String>(paramMap
				.size());
		for (Entry<String, String[]> entry : paramMap.entrySet()) {
			String name = entry.getKey();
			String[] values = entry.getValue();
			String value = null;
			if (!ArrayUtils.isEmpty(values)) {
				value = values[0];
			}
			if (value != null) {
				params.put(name, value);
			}
		}
		return params;
	}

	public static String createUrl(String path, Map<String, String> params,
			String encode) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder(path.length() + 32 * params.size());
		sb.append(path);
		if (StringUtils.contains(path, '?')) {
			sb.append('&');
		} else {
			sb.append('?');
		}
		for (Iterator<Entry<String, String>> itt = params.entrySet().iterator(); itt
				.hasNext();) {
			Entry<String, String> entry = itt.next();
			String value = entry.getValue();
			if (value != null) {
				sb.append(entry.getKey()).append('=').append(
						URLEncoder.encode(value, encode));
			}
			if (itt.hasNext()) {
				sb.append('&');
			}
		}
		return sb.toString();
	}

}
