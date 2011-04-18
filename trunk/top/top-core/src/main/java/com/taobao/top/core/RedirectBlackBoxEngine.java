package com.taobao.top.core;

import static com.taobao.top.core.ProtocolConstants.*;


import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.taobao.top.common.encrypt.EncryptUtil;
import com.taobao.top.common.fileupload.FileItem;
import com.taobao.top.common.fileupload.FileUpload;
import com.taobao.top.common.fileupload.ParameterParser;
import com.taobao.top.common.fileupload.RequestContext;
import com.taobao.top.common.fileupload.servlet.ServletRequestContext;
import com.taobao.top.common.lang.StringKit;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;

/**
 * 支持透传
 * 
 * @author haishi
 * 
 */
public class RedirectBlackBoxEngine implements BlackBoxEngine {
	/*
	 * tip secret
	 */
	private String tipSecret = "";
	
	public void setTipSecret(String tipSecret) {
		this.tipSecret = tipSecret;
	}

	public RedirectBlackBoxEngine() {

	}
	void removeNonRedirectParams(Map<String, Object> paramMap) {
		paramMap.remove(P_SESSION);
		paramMap.remove(P_SIGN);
		paramMap.remove(P_SIGN_METHOD);
//		paramMap.remove(P_TIMESTAMP);//透传的ISP需要timestatm
	}

	/**
	 * Extract system parameters out of paramMap.
	 * 
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> extractSystemParams(Map<String, Object> paramMap) {

		Map<String, Object> systemParams = new HashMap<String, Object>();

		extractSystemParam(paramMap, systemParams, P_APP_KEY);
		extractSystemParam(paramMap, systemParams, P_API_KEY);
		extractSystemParam(paramMap, systemParams, P_TIMESTAMP);
		extractSystemParam(paramMap, systemParams, P_TOP_SIGN);
		extractSystemParam(paramMap, systemParams, P_TOP_TAG);
		extractSystemParam(paramMap, systemParams, P_TOP_BIND_NICK);
		extractSystemParam(paramMap, systemParams, P_TOP_ISV_ID);

		extractSystemParam(paramMap, systemParams, P_FORMAT);
		extractSystemParam(paramMap, systemParams, P_METHOD);
		extractSystemParam(paramMap, systemParams, P_CALLBACK);
		extractSystemParam(paramMap, systemParams, P_VERSION);
		extractSystemParam(paramMap, systemParams, P_COMPRESS);
		extractSystemParam(paramMap, systemParams, P_STYLE);

		extractSystemParam(paramMap, systemParams, P_SESSION_UID);
		extractSystemParam(paramMap, systemParams, P_SESSION_NICK);

		// --- Stop handling the compatible logic for APIKEY since 20091112
		// release. ---

		// // Special handling for APP/API key.
		// Object appKey = systemParams.get(P_APPKEY);
		//
		// if (appKey == null) {
		// // If no APP key, use API key to set APP key.
		// appKey = systemParams.get(P_APIKEY);
		// if (appKey != null) {
		// systemParams.put(P_APPKEY, appKey);
		// }
		// }

		return systemParams;
	}

	/**
	 * Remove parameter from paramMap, insert it to systemParams.
	 * 
	 * @param paramMap
	 * @param systemParams
	 * @param name
	 */
	void extractSystemParam(Map<String, Object> paramMap,
			Map<String, Object> systemParams, String name) {
		Object value = paramMap.remove(name);

		if (value != null) {
			systemParams.put(name, value);
		}
	}

	URL toDestinationUrl(String url, String urlParamString)
			throws MalformedURLException {

		if (StringUtils.isEmpty(urlParamString)) {
			return new URL(url);
		}

		String dest = null;

		// perform the url parameter assembling.
		if (url.indexOf("?") > 0) {
			// It's very important to check '?' since the url may
			// contains one e.g. http://192.168.206.110/router/rest?action=XX
			dest = new StringBuilder().append(url).append("&").append(
					urlParamString).toString();
		} else {
			dest = new StringBuilder().append(url).append("?").append(
					urlParamString).toString();
		}

		return new URL(dest);
	}
	@SuppressWarnings("unchecked")
	void initRequestHeaders(HttpServletRequest request, HttpURLConnection conn)
			throws ProtocolException {
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(5000); // approximately as response time.
		conn.setRequestMethod(request.getMethod());

		// The content type would contains all things we
		// need, including content type, charset, boundary.
		conn.setRequestProperty("Content-Type", new StringBuilder().append(
				request.getContentType()).toString());

		// 透传Request Header中的内容
		Enumeration<String> heads = request.getHeaderNames();

		if (heads != null) {
			while (heads.hasMoreElements()) {
				String key = heads.nextElement();

				if (conn.getRequestProperty(key) == null) {
					// Only the header not already been set
					// in connection would we set that header
					if (!key.equalsIgnoreCase("user-agent")
							&& !key.equalsIgnoreCase("accept")
							&& !key.equalsIgnoreCase("connection")
							&& !key.equalsIgnoreCase("content-length")
							&& !key.equalsIgnoreCase("host")
							&& !key.equalsIgnoreCase("accept-encoding"))
						/*
						 * Remove the accept-encoding to prevent the
						 * Accept-encoding=gzip to be passed to third party,
						 * which would cause the output being zipped twice (one
						 * by TIP, one by third party.
						 */

						conn.setRequestProperty(key, request.getHeader(key));
				}

			}
		}
	}

	@SuppressWarnings("unchecked")
	void copyRequestParameters(HttpServletRequest request,
			Map<String, Object> params, HttpURLConnection conn,
			FileItem dataFileItem) throws Exception {

		String encoding = request.getCharacterEncoding();
		if (StringUtils.isEmpty(encoding)) {
			encoding = "UTF-8";
		}

		if (isMultiPartContent(request)) {

			String boundary = getBoundary(request.getContentType());
			byte[] keyboundary = new StringBuilder("--").append(boundary)
					.append("\r\n").toString().getBytes(encoding);

			int num = params.size();
			if (dataFileItem != null) {
				num++;
			}

			if (num == 0) {
				// Must end with --XXXX--\r\n
				conn.getOutputStream()
						.write(
								new StringBuilder().append("--").append(
										boundary).append("--\r\n").toString()
										.getBytes(encoding));

				return; // nothing to pass. Theoretically impossible.

			}

			for (Object item : params.entrySet()) {
				Entry entry = (Entry) item;

				conn.getOutputStream().write(keyboundary);

				conn.getOutputStream().write(
						new StringBuilder(
								"Content-Disposition: form-data; name=\"")
								.append(entry.getKey()).append("\"\r\n\r\n")
								.toString().getBytes(encoding));

				// Since we can't assume value is String,
				// use "" + value to convert it to String.
				// It doesn't make sense to use StringBuilder here to
				// concatenate two strings.
				String value = entry.getValue() == null ? "" : ""
						+ entry.getValue();

				conn.getOutputStream().write(value.getBytes(encoding));
				conn.getOutputStream().write("\r\n".getBytes(encoding));

			}

			if (dataFileItem != null) {
				conn.getOutputStream().write(keyboundary);

				conn
						.getOutputStream()
						.write(
								new StringBuilder(
										"Content-Disposition: form-data; name=\"")
										.append(dataFileItem.getFieldName())
										.append("\"; filename=\"").append(
												dataFileItem.getName()).append(
												"\"\r\n").toString().getBytes(
												encoding));

				conn.getOutputStream().write(
						new StringBuilder("Content-Type: ").append(
								dataFileItem.getContentType()).append(
								"\r\n\r\n").toString().getBytes(encoding));
				// Write the file itself
				conn.getOutputStream().write(dataFileItem.get());
				conn.getOutputStream().write("\r\n".getBytes(encoding));

			}

			// Must end with --XXXX--\r\n
			conn.getOutputStream().write(
					new StringBuilder().append("--").append(boundary).append(
							"--\r\n").toString().getBytes(encoding));

		} else { // Simple Post case
			String urlParamString = toUrlParamString(params);

			if (!StringUtils.isEmpty(urlParamString))
				conn.getOutputStream().write(urlParamString.getBytes(encoding));
		}
	}

	/**
	 * Check whether the request is of multipart data form.
	 * 
	 * @param request
	 * @return
	 */
	boolean isMultiPartContent(HttpServletRequest request) {
		RequestContext requestContext = new ServletRequestContext(request);
		return FileUpload.isMultipartContent(requestContext);
	}

	@SuppressWarnings("unchecked")
	String getBoundary(String contentType) {
		ParameterParser parser = new ParameterParser();
		parser.setLowerCaseNames(true);
		Map params = parser.parse(contentType, new char[] { ';', ',' });
		String boundaryStr = (String) params.get("boundary");

		return boundaryStr;
	}

	String toUrlParamString(Map<String, Object> params) {
		if (params == null || params.isEmpty()) {
			return ""; // impossible to be here.
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;

		Set<Entry<String, Object>> entries = params.entrySet();
		for (Entry<String, Object> entry : entries) {
			// Since we are not sure whether all the gotten value
			// is string, use '+' to ensure that
			Object value = entry.getValue();
			if (first) {
				first = false;
			} else {
				sb.append("&");
			}
			sb.append(StringKit.urlEncode(entry.getKey())).append("=").append(
					StringKit.urlEncode(value == null ? "" : "" + value));

		}

		return sb.toString();
	}
	/**
	 * 将Map<String,Object> 转化为Map<String,String>
	 * @param paramMap
	 * @return
	 */
	private Map<String,String> toStringMap(Map<String,Object> paramMap){
		Map<String,String> stringMap = new HashMap<String,String>();
		Set<Entry<String,Object>> entries = paramMap.entrySet();
		for(Entry<String,Object> entry:entries){
			stringMap.put(StringKit.urlEncode(entry.getKey()),StringKit.urlEncode(entry.getValue() != null? entry.getValue().toString():""));
		}
		return stringMap;
	}
	@Override
	public void execute(TopPipeResult pipeResult, TopPipeInput pipeInput, Api api) throws Exception {
		Map<String, Object> paramMap = toMap(pipeInput);
		injectSessionProperties(pipeInput, paramMap);
		injectBusinessProperties(pipeInput, paramMap);
		
		removeNonRedirectParams(paramMap);
		
		// Add sign here.
		Map<String,String> stringParamMap = toStringMap(paramMap);
		String top_sign = null;
		try{//进行保护，以免签名出错影响正常的流程。
			top_sign = EncryptUtil.signature2(stringParamMap, tipSecret, true, false, null);
		}catch(Exception e){
			top_sign = null;
		}
		paramMap.put(P_TOP_SIGN, top_sign);
		
		String url = pipeInput.getApi().getRedirectUrl();
		String httpOperation = pipeInput.getHttpMethod();

		URL dest = null;
		if (httpOperation.equalsIgnoreCase("GET")) {
			dest = toDestinationUrl(url, toUrlParamString(paramMap));
		} else {
			// Only set system parameters into url.
			dest = toDestinationUrl(url,
					toUrlParamString(extractSystemParams(paramMap)));
		}

		HttpURLConnection conn = (HttpURLConnection) dest.openConnection();
		HttpServletRequest request = pipeInput.getRequest();

		initRequestHeaders(request, conn);

		if (httpOperation.equalsIgnoreCase("GET")) {
			conn.connect();
		} else {
			conn.setDoOutput(true);
			conn.connect();

			copyRequestParameters(request, paramMap, conn, pipeInput
					.getFileData());

		}
		pipeResult.setBlack(conn);
		return ;
	}
	Map<String, Object> toMap(TopPipeInput input) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		Set<String> parameterNames = input.getParameterNames();
		for (String name : parameterNames) {
			result.put(name, input.getString(name, false));
		}

		return result;
	}
	void injectSessionProperties(TopPipeInput input, Map<String, Object> paramMap) {
		String sessionNick = input.getSessionNick();

		if (sessionNick != null) {
			paramMap.put(ProtocolConstants.P_SESSION_NICK, input
					.getSessionNick());
		}

		String sessionUid = input.getSessionUid();

		if (sessionUid != null) {
			paramMap
					.put(ProtocolConstants.P_SESSION_UID, input.getSessionUid());
		}
	}

	void injectBusinessProperties(TopPipeInput input,
			Map<String, Object> paramMap) {
		String tag = input.getTag();
		fillBusinessParam(paramMap, ProtocolConstants.P_TOP_TAG, tag);
		
		String sessionBound = input.getBindNick();
		fillBusinessParam(paramMap, ProtocolConstants.P_TOP_BIND_NICK, sessionBound);
		
		String isvId = input.getIsvId();
		fillBusinessParam(paramMap, ProtocolConstants.P_TOP_ISV_ID, isvId);
	}
	
	private void fillBusinessParam(Map<String, Object> paramMap, String key, String value) {
		if (value != null) {
			paramMap.put(key, value);
		} else {
			// make sure the isv won't use the backdoor if we can't fetch the value.
			paramMap.remove(key);
		}
	}
	
	@SuppressWarnings("unchecked")
	void copyRequestParameters(HttpServletRequest request,
			Map<String, Object> params, HttpURLConnection conn,
			com.taobao.top.core.framework.FileItem dataFileItem) throws Exception {

		String encoding = request.getCharacterEncoding();
		if (StringUtils.isEmpty(encoding)) {
			encoding = "UTF-8";
		}

		if (isMultiPartContent(request)) {

			String boundary = getBoundary(request.getContentType());
			byte[] keyboundary = new StringBuilder("--").append(boundary)
					.append("\r\n").toString().getBytes(encoding);

			int num = params.size();
			if (dataFileItem != null) {
				num++;
			}

			if (num == 0) {
				// Must end with --XXXX--\r\n
				conn.getOutputStream()
						.write(
								new StringBuilder().append("--").append(
										boundary).append("--\r\n").toString()
										.getBytes(encoding));

				return; // nothing to pass. Theoretically impossible.

			}

			for (Object item : params.entrySet()) {
				Entry entry = (Entry) item;

				conn.getOutputStream().write(keyboundary);

				conn.getOutputStream().write(
						new StringBuilder(
								"Content-Disposition: form-data; name=\"")
								.append(entry.getKey()).append("\"\r\n\r\n")
								.toString().getBytes(encoding));

				// Since we can't assume value is String,
				// use "" + value to convert it to String.
				// It doesn't make sense to use StringBuilder here to
				// concatenate two strings.
				String value = entry.getValue() == null ? "" : ""
						+ entry.getValue();

				conn.getOutputStream().write(value.getBytes(encoding));
				conn.getOutputStream().write("\r\n".getBytes(encoding));

			}

			if (dataFileItem != null) {
				conn.getOutputStream().write(keyboundary);

				conn
						.getOutputStream()
						.write(
								new StringBuilder(
										"Content-Disposition: form-data; name=\"")
										.append(dataFileItem.getName())
										.append("\"; filename=\"").append(
												dataFileItem.getFileName()).append(
												"\"\r\n").toString().getBytes(
												encoding));

				conn.getOutputStream().write(
						new StringBuilder("Content-Type: ").append(
								dataFileItem.getContentType()).append(
								"\r\n\r\n").toString().getBytes(encoding));
				// Write the file itself
				conn.getOutputStream().write(dataFileItem.getBout().toByteArray());
				conn.getOutputStream().write("\r\n".getBytes(encoding));

			}

			// Must end with --XXXX--\r\n
			conn.getOutputStream().write(
					new StringBuilder().append("--").append(boundary).append(
							"--\r\n").toString().getBytes(encoding));

		} else { // Simple Post case
			String urlParamString = toUrlParamString(params);

			if (!StringUtils.isEmpty(urlParamString))
				conn.getOutputStream().write(urlParamString.getBytes(encoding));
		}
	}
}
