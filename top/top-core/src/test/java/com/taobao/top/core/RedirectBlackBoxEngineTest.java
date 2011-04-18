package com.taobao.top.core;

import static com.taobao.top.core.ProtocolConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.taobao.top.common.fileupload.FileItem;
import com.taobao.top.common.fileupload.disk.DiskFileItem;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.mock.MockedHttpServletRequest;
import com.taobao.top.core.mock.MockedHttpURLConnection;
import com.taobao.top.tim.domain.TinyAppDO;

/**
 * 
 * @author haishi
 * 
 */
public class RedirectBlackBoxEngineTest {
	RedirectBlackBoxEngine engine = new RedirectBlackBoxEngine();

	@Test
	public void testExecute() throws Exception {
		// it's not possible to Unit-test the execute() method
		// since the HttpConnecton attained from URL could not
		// be replaced by mocked one.
		
	}
	
	@Test
	public void testToDestinationUrl() throws MalformedURLException {
		// without '?'
		URL url = engine.toDestinationUrl("http://192.168.206.110/router/rest", "name=value&name1=value1");
		assertEquals("http://192.168.206.110/router/rest?name=value&name1=value1", url.toString());
		
		// already has '?'
		url = engine.toDestinationUrl("http://192.168.206.110/router/rest?action=XX", "name=value&name1=value1");
		assertEquals("http://192.168.206.110/router/rest?action=XX&name=value&name1=value1", url.toString());
		
	}

	@Test
	public void testInitRequestHeaders_Multipart() throws ProtocolException {
		
		MockedHttpServletRequest request = new MockedHttpServletRequest();
		request.setContentType("multipart/form-data; boundary=---------------------------7d92508a1102");
		request.setMethod("POST");
		MockedHttpURLConnection conn = new MockedHttpURLConnection(null);
		
		//////////////////////////////
		// Testing target
		engine.initRequestHeaders(request, conn);
		assertEquals("multipart/form-data; boundary=---------------------------7d92508a1102", conn.getRequestProperty("Content-Type"));
		assertEquals("POST", conn.getRequestMethod());
		
	
	}
	
	@Test
	public void testInitRequestHeaders_Post() throws ProtocolException {
		
		MockedHttpServletRequest request = new MockedHttpServletRequest();
		request.setContentType("application/x-www-form-urlencoded");
		request.setMethod("POST");
		MockedHttpURLConnection conn = new MockedHttpURLConnection(null);
		
		//////////////////////////////
		// Testing target
		engine.initRequestHeaders(request, conn);
		assertEquals("application/x-www-form-urlencoded", conn.getRequestProperty("Content-Type"));
		assertEquals("POST", conn.getRequestMethod());
		
	
	}

	@Test
	public void testCopyRequestParameters_Multipart() throws Exception {
		MockedHttpServletRequest request = new MockedHttpServletRequest();
		request.setCharacterEncoding("UTF-8");
		request
				.setContentType("multipart/form-data; boundary=---------------------------7d92508a1102");
		Map<String, Object> params = new HashMap<String, Object>();

		MockedHttpURLConnection conn = new MockedHttpURLConnection(null);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		conn.setOutputStream(outputStream);
		// String fieldName,
		// String contentType, boolean isFormField, String fileName,
		// int sizeThreshold, File repository)
		FileItem fileItem = new DiskFileItem("uploadFile", "text/plain", false,
				"D:\\test.jpg", 100000, null) {

			private static final long serialVersionUID = 1L;

			public byte[] get() {
				return "ABCDEFG\n1234567".getBytes();

			}

		};
		// Must invoke the getOutputStream to initialize fileItem;
		// otherwise, the NullPointerException would be thrown
		fileItem.getOutputStream();

		params.put("name", "value=");
		params.put("name1", "value1=");
		
		//////////////////////////////
		// Testing target
		engine.copyRequestParameters(request, params, conn,
				fileItem);
		String firstLine = "-----------------------------7d92508a1102";

		// Not use assertTrue(XXXX.startWith(YYY)) to give a
		// better view of string while something going wrong.
		String outputText = outputStream.toString();
		assertEquals(firstLine, outputText.substring(0,
				firstLine.length()));
		
		assertTrue(outputText.contains("value="));
		assertTrue(outputText.contains("value1="));
		assertTrue(outputText.contains("ABCDEFG\n1234567"));

		//System.out.println(outputStream.toString());
	}
	
	@Test
	public void testCopyRequestParameters_Post() throws Exception {
		MockedHttpServletRequest request = new MockedHttpServletRequest();
		request.setCharacterEncoding("UTF-8");
		request
				.setContentType("application/x-www-form-urlencoded");
		Map<String, Object> params = new HashMap<String, Object>();

		MockedHttpURLConnection conn = new MockedHttpURLConnection(null);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		conn.setOutputStream(outputStream);

		params.put("name", "value=");
		params.put("name1", "value1=");
		engine.copyRequestParameters(request, params, conn,
				(FileItem)null);

		// Not use assertTrue(XXXX.startWith(YYY)) to give a
		// better view of string while something going wrong.
		String outputText = outputStream.toString();
		assertEquals("name=value%3D&name1=value1%3D", outputText);

		//System.out.println(outputStream.toString());
	}

	@Test
	public void testIsMultiPartContent() {
		MockedHttpServletRequest request = new MockedHttpServletRequest();
		request.setContentType("multipart/form-data");
		assertTrue(engine.isMultiPartContent(request));

	}

	@Test
	public void testGetBoundary() {
		String boundary = engine
				.getBoundary("multipart/form-data; boundary=---------------------------7d92508a1102");
		assertEquals("---------------------------7d92508a1102", boundary);
	}

	@Test
	public void testToUrlParamString() {
		Map<String, Object> params = new HashMap<String, Object>();
		// Empty map, empty string
		String string = engine.toUrlParamString(params);

		assertEquals("", string);

		// one parameter
		params.put("name", "value");
		string = engine.toUrlParamString(params);
		assertEquals("name=value", string);

		// another parameter
		params.put("nameX", "valueX");
		string = engine.toUrlParamString(params);
		// Since the order of map is arbitrary,
		// we need to perform the permutation.
		assertTrue("name=value&nameX=valueX".equals(string)
				|| "nameX=valueX&name=value".equals(string));

		// Encoding Test
		params = new HashMap<String, Object>();

		// one parameter
		params.put("name", ",");
		string = engine.toUrlParamString(params);
		assertEquals("name=%2C", string);

		// another parameter
		params.put("nameX=", "valueX=");
		string = engine.toUrlParamString(params);
		// Since the order of map is arbitrary,
		// we need to perform the permutation.
		assertTrue("name=%2C&nameX=value%3D".equals(string)
				|| "nameX%3D=valueX%3D&name=%2C".equals(string));

	}
	
	@Test
	public void testExtractSystemParam() {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> systemParams = new HashMap<String, Object>();
		
		paramMap.put("name", "value");
		engine.extractSystemParam(paramMap, systemParams, "name");
		assertTrue(paramMap.isEmpty());
		
		assertEquals("value", systemParams.get("name"));
		
	}
	
	@Test
	public void testExtractSystemParams() {
		Map<String, Object> paramMap = new HashMap<String, Object>();

		// parameters should be extracted.
		paramMap.put(P_APP_KEY, "P_APPKEY");
		paramMap.put(P_API_KEY, "P_APIKEY");
		paramMap.put(P_FORMAT, "P_FORMAT");
		paramMap.put(P_METHOD, "P_METHOD");
		paramMap.put(P_CALLBACK, "P_CALLBACK");
		paramMap.put(P_VERSION, "P_VERSION");
		paramMap.put(P_COMPRESS, "P_COMPRESS");
		paramMap.put(P_STYLE, "P_STYLE");
		paramMap.put(P_SESSION_UID, "P_SESSION_UID");
		paramMap.put(P_SESSION_NICK, "P_SESSION_NICK");
		paramMap.put(P_TOP_TAG, "P_TOP_TAG");
		paramMap.put(P_TOP_BIND_NICK, "P_TOP_SESSION_BOUND");
		paramMap.put(P_TOP_ISV_ID, "P_TOP_ISV_ID");
		
		// parameter(s) should not be extracted.
		paramMap.put("SomethingElseName", "SomethingElseValue");
		Map<String, Object> result = engine.extractSystemParams(paramMap);
		
		assertEquals("P_APPKEY", result.get(P_APP_KEY));
		assertEquals("P_APIKEY", result.get(P_API_KEY));
		assertEquals("P_FORMAT", result.get(P_FORMAT));
		assertEquals("P_METHOD", result.get(P_METHOD));
		assertEquals("P_CALLBACK", result.get(P_CALLBACK));
		assertEquals("P_VERSION", result.get(P_VERSION));
		assertEquals("P_COMPRESS", result.get(P_COMPRESS));
		assertEquals("P_STYLE", result.get(P_STYLE));
		assertEquals("P_SESSION_UID", result.get(P_SESSION_UID));
		assertEquals("P_SESSION_NICK", result.get(P_SESSION_NICK));
		assertEquals("P_TOP_TAG", result.get(P_TOP_TAG));
		assertEquals("P_TOP_SESSION_BOUND", result.get(P_TOP_BIND_NICK));
		assertEquals("P_TOP_ISV_ID", result.get(P_TOP_ISV_ID));
		
		assertEquals(1, paramMap.size());
		assertEquals(13, result.size());
		
		assertEquals("SomethingElseValue", paramMap.get("SomethingElseName"));
		
	}
	
	
	public void testInjectBusinessProperties() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		TopPipeInput input = new TopPipeInput(request,response,null){

			private static final long serialVersionUID = 1L;

			@Override
			public TinyAppDO getAppDO() {
				TinyAppDO appDO = new TinyAppDO();
				appDO.setAppTag(1L);
				appDO.setBindNick("bindNick");
				appDO.setIsvId(123L);
				
				return appDO;
			}
		};
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		engine.injectBusinessProperties(input, paramMap);
		
		assertEquals("1", paramMap.get(P_TOP_TAG));
		assertEquals("bindNick", paramMap.get(P_TOP_BIND_NICK));
		assertEquals("123", paramMap.get(P_TOP_ISV_ID));
		
		
		
		//------------------  TAG NOT EXISTS
		input = new TopPipeInput(request,response,null){

			private static final long serialVersionUID = 1L;

			@Override
			public TinyAppDO getAppDO() {
				TinyAppDO appDO = new TinyAppDO();
				appDO.setBindNick("bindNick");
				
				return appDO;
			}
		};
		
		paramMap = new HashMap<String, Object>();
		
		engine.injectBusinessProperties(input, paramMap);
		
		assertEquals(null, paramMap.get(P_TOP_TAG));
		assertEquals("bindNick", paramMap.get(P_TOP_BIND_NICK));
		
		//------------------  TOP SESSION BOUND NOT EXISTS
		input = new TopPipeInput(request,response,null){

			private static final long serialVersionUID = 1L;

			@Override
			public TinyAppDO getAppDO() {
				TinyAppDO appDO = new TinyAppDO();
				appDO.setAppTag(1L);
				
				return appDO;
			}
		};
		
		paramMap = new HashMap<String, Object>();
		
		engine.injectBusinessProperties(input, paramMap);
		
		assertEquals("1", paramMap.get(P_TOP_TAG));
		assertEquals(null, paramMap.get(P_TOP_BIND_NICK));
		assertEquals(null, paramMap.get(P_TOP_ISV_ID));
		
	}
	
	
}
