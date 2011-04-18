/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.taobao.top.core.exception.TopException;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.traffic.mapping.MethodMapping;
import com.taobao.top.traffic.mapping.OperationCodeException;

/**
 * @version 2008-3-13
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */

public class DefaultApiExecutorTest {
	private DefaultApiExecutor executor;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private HttpServletRequest getMockRequest() {
		HttpServletRequest mock = new HttpServletRequest() {

			public String getAuthType() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getContextPath() {
				// TODO Auto-generated method stub
				return null;
			}

			public Cookie[] getCookies() {
				// TODO Auto-generated method stub
				return null;
			}

			public long getDateHeader(String name) {
				// TODO Auto-generated method stub
				return 0;
			}

			public String getHeader(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			public Enumeration getHeaderNames() {
				// TODO Auto-generated method stub
				return null;
			}

			public Enumeration getHeaders(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			public int getIntHeader(String name) {
				// TODO Auto-generated method stub
				return 0;
			}

			public String getMethod() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getPathInfo() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getPathTranslated() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getQueryString() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getRemoteUser() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getRequestURI() {
				// TODO Auto-generated method stub
				return null;
			}

			public StringBuffer getRequestURL() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getRequestedSessionId() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getServletPath() {
				// TODO Auto-generated method stub
				return null;
			}

			public HttpSession getSession() {
				// TODO Auto-generated method stub
				return null;
			}

			public HttpSession getSession(boolean create) {
				// TODO Auto-generated method stub
				return null;
			}

			public Principal getUserPrincipal() {
				// TODO Auto-generated method stub
				return null;
			}

			public boolean isRequestedSessionIdFromCookie() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isRequestedSessionIdFromURL() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isRequestedSessionIdFromUrl() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isRequestedSessionIdValid() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isUserInRole(String role) {
				// TODO Auto-generated method stub
				return false;
			}

			public Object getAttribute(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			public Enumeration getAttributeNames() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getCharacterEncoding() {
				// TODO Auto-generated method stub
				return null;
			}

			public int getContentLength() {
				// TODO Auto-generated method stub
				return 0;
			}

			public String getContentType() {
				// TODO Auto-generated method stub
				return null;
			}

			public ServletInputStream getInputStream() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			public String getLocalAddr() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getLocalName() {
				// TODO Auto-generated method stub
				return null;
			}

			public int getLocalPort() {
				// TODO Auto-generated method stub
				return 0;
			}

			public Locale getLocale() {
				// TODO Auto-generated method stub
				return null;
			}

			public Enumeration getLocales() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getParameter(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			public Map getParameterMap() {
				// TODO Auto-generated method stub
				return null;
			}

			public Enumeration getParameterNames() {
				// TODO Auto-generated method stub
				return null;
			}

			public String[] getParameterValues(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			public String getProtocol() {
				// TODO Auto-generated method stub
				return null;
			}

			public BufferedReader getReader() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			public String getRealPath(String path) {
				// TODO Auto-generated method stub
				return null;
			}

			public String getRemoteAddr() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getRemoteHost() {
				// TODO Auto-generated method stub
				return null;
			}

			public int getRemotePort() {
				// TODO Auto-generated method stub
				return 0;
			}

			public RequestDispatcher getRequestDispatcher(String path) {
				// TODO Auto-generated method stub
				return null;
			}

			public String getScheme() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getServerName() {
				// TODO Auto-generated method stub
				return null;
			}

			public int getServerPort() {
				// TODO Auto-generated method stub
				return 0;
			}

			public boolean isSecure() {
				// TODO Auto-generated method stub
				return false;
			}

			public void removeAttribute(String name) {
				// TODO Auto-generated method stub

			}

			public void setAttribute(String name, Object o) {
				// TODO Auto-generated method stub

			}

			public void setCharacterEncoding(String env)
					throws UnsupportedEncodingException {
				// TODO Auto-generated method stub

			}
		};
		return mock;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	@Ignore
	public void setUp() throws Exception {
		executor = new DefaultApiExecutor();
		DefaultApi userGet = new DefaultApi();
		userGet.addProtocolMustParameter("api_key");
		userGet.addProtocolMustParameter("timestamp");
		userGet.addProtocolMustParameter("sign");
		userGet.addProtocolPrivateParameter("session");
		ApiApplicationParameter fieldsParam = new ApiApplicationParameter(
				"fields", ApiApplicationParameter.Type.STR_LIST);
		userGet.addApplicationMustParameter(fieldsParam);
		ApiApplicationParameter nickParam = new ApiApplicationParameter("nick",
				ApiApplicationParameter.Type.STRING);
		userGet.addApplicationMustParameter(nickParam);
		userGet.setName("taobao.user.get");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.taobao.top.core.DefaultApiExecutor#execute(com.taobao.top.core.ApiInput)}
	 * .
	 * 
	 * @throws TopException
	 */
	@Test
	public void testExecuteNullTadgetManager() throws TopException {
	}
	
	@Test
	public void testNewExecute() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();		
		
		TopPipeInput input = new TopPipeInput(request, response, null);
		TopPipeResult result = new TopPipeResult();
		
		input.setApi(new Api (){


			@Override
			public ErrorCode checkCombine(TopPipeInput pipeInput) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<String> getAliases() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ApiType getApiType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<ApiApplicationParameter> getApplicationCombineParams() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<ApiApplicationParameter> getApplicationMustParams() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<ApiApplicationParameter> getApplicationOptionalParams() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getHsfInterfaceName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getHsfInterfaceVersion() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getHsfMethodName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getLastCheckedForUpdate() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public long getLastModified() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getLocalPath() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public MethodMapping<Object> getMethodMapping() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<String> getProtocolMustParams() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<String> getProtocolPrivateParams() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getRedirectUrl() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getRequestURL() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String[] getSupportedVersions() {
				// TODO Auto-generated method stub
				return null;
			}

			

			@Override
			public boolean isCallable(String version) {
				return true;
			}

			@Override
			public boolean isRedirect() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void setLastCheckedForUpdate(long lastCheckedForUpdate) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setLastModified(long lastModified) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setMethodMapping(MethodMapping<Object> mm) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setRequestURL(String url) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Long getHsfTimeout() {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
		input.setVersion(ProtocolConstants.VERSION_1);
		executor.setBlackBoxEngine(new BlackBoxEngine() {
			
			@Override
			public void execute(TopPipeResult pipeResult, TopPipeInput pipeInput,
					Api api) throws Exception {
				throw new OperationCodeException("580", "remote service erro");
			}
		});
		executor.execute(input, result);
	}
}
