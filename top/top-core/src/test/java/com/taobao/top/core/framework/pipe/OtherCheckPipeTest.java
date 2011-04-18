package com.taobao.top.core.framework.pipe;


import static com.taobao.top.core.ProtocolConstants.P_SIGN;
import static com.taobao.top.core.ProtocolConstants.P_SIGN_METHOD;
import static com.taobao.top.core.ProtocolConstants.P_VERSION;
import static com.taobao.top.core.ProtocolConstants.SIGN_METHOD_HMAC;
import static com.taobao.top.core.ProtocolConstants.SIGN_METHOD_MD5;
import static com.taobao.top.core.ProtocolConstants.VERSION_1;
import static com.taobao.top.core.ProtocolConstants.VERSION_2;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.ProtocolConstants;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeManager;
import com.taobao.top.log.TopLog;
import com.taobao.top.log.asyn.AsynWriterTemplate;
import com.taobao.top.log.asyn.IAsynWriter;
import com.taobao.top.tim.domain.TinyAppDO;
/**
 * 
 * @author zhenzi
 *
 */
public class OtherCheckPipeTest {
	private OtherCheckPipe otherCheckPipe;
	private TopPipeManager topPipeManager;
	private IAsynWriter<TopLog> asynLogWriter;
	@Before
	public void setUp() throws Exception {
		otherCheckPipe = new OtherCheckPipe();
		topPipeManager = new TopPipeManager();
		topPipeManager.register(otherCheckPipe);
		asynLogWriter = new AsynWriterTemplate<TopLog>();
		topPipeManager.setAsynLogWriter(asynLogWriter);
	}

	@After
	public void tearDown() throws Exception {
	}
	/*
	 * 以下方法测试checkSignAndApiInput
	 */
	@Test
	public void testCheckSignAndApiInput() throws Exception {
		Class clazz = otherCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("checkSignAndApiInput", TopPipeInput.class,String.class,boolean.class,boolean.class);
		method.setAccessible(true);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();		
		TopPipeInput input = new TopPipeInput(request,response,null);
		
		String secret = "secret001";
		request.setParameter("v", "1.0");
		//没有传递sign
		{
			boolean r = false; 
			try{
				r = (Boolean)method.invoke(otherCheckPipe, input,secret,false,false);
			}catch(Exception e){
				assertTrue(e.getCause() instanceof IllegalStateException);
			}
			assertFalse(r);
		}
		{
			request.setParameter(ProtocolConstants.P_SIGN, "errorSign");
			boolean r = (Boolean)method.invoke(otherCheckPipe, input,secret,false,false);
			assertFalse(r);
		}
		{
			MockHttpServletRequest request2 = new MockHttpServletRequest();
			MockHttpServletResponse response2 = new MockHttpServletResponse();
			TopPipeInput input2 = new TopPipeInput(request2,response2,null);
			request2.setParameter("v", "1.0");
			request2.setParameter(ProtocolConstants.P_SIGN,"26A136464B59367FF92FBE2BB5C7D480");
			boolean r = (Boolean)method.invoke(otherCheckPipe, input2,secret,false,false);
			assertTrue(r);

			request2.setParameter("nullkey", (String)null);
			r = (Boolean)method.invoke(otherCheckPipe, input2,secret,false,false);
			assertTrue(r);
		}
	}
	/*
	 * 以下代码是测试checkSign
	 */

	@Test
	public void testCheckV2Md5Sign() throws Exception{
		Class clazz = otherCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("checkSign", TopPipeInput.class);
		method.setAccessible(true);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		TopPipeInput input = new TopPipeInput(request,response,null){
			@Override
			public TinyAppDO getAppDO() {
				TinyAppDO appDO = new TinyAppDO();
				appDO.setSecret("test secret");
				return appDO;
			}
		};
		
		request.setParameter("v", "2.0");
		request.setParameter("method", "testMethod");
		request.setParameter(ProtocolConstants.P_SIGN_METHOD, ProtocolConstants.SIGN_METHOD_MD5);
		request.setParameter(ProtocolConstants.P_SIGN, "BC4C39E03CA95879748C0961FF582648");
		
		ErrorCode errCode = (ErrorCode)method.invoke(otherCheckPipe, input);
		assertNull(errCode);
	}
	
	@Test
	public void testCheckV2DefaultSign()throws Exception {
		Class clazz = otherCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("checkSign", TopPipeInput.class);
		method.setAccessible(true);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();	
		TopPipeInput input = new TopPipeInput(request,response,null){
			@Override
			public TinyAppDO getAppDO() {
				TinyAppDO appDO = new TinyAppDO();
				appDO.setSecret("test secret");
				return appDO;
			}
		};
		request.setParameter("v", "2.0");
		request.setParameter("method", "testMethod");
		request.setParameter(ProtocolConstants.P_SIGN, "B465CFDCB28FFE4D4BC83175B20F5E26");
		ErrorCode errCode = (ErrorCode)method.invoke(otherCheckPipe, input);
		assertNull(errCode);
	}
	
	@Test
	public void testCheckV1DefaultSign() throws Exception{
		Class clazz = otherCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("checkSign", TopPipeInput.class);
		method.setAccessible(true);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();	
		TopPipeInput input = new TopPipeInput(request,response,null){
			@Override
			public TinyAppDO getAppDO() {
				TinyAppDO appDO = new TinyAppDO();
				appDO.setSecret("test secret");
				return appDO;
			}
		};
		
		request.setParameter("v", "1.0");
		request.setParameter("method", "testMethod");
		request.setParameter(ProtocolConstants.P_SIGN, "E33FFA032757B994D9654DA1C6E283B3");
		ErrorCode errCode = (ErrorCode)method.invoke(otherCheckPipe, input);
		assertNull(errCode);
	}
	
	@Test
	public void testCheckV1Md5Sign() throws Exception{
		Class clazz = otherCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("checkSign", TopPipeInput.class);
		method.setAccessible(true);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();	
		TopPipeInput input = new TopPipeInput(request,response,null){
			@Override
			public TinyAppDO getAppDO() {
				TinyAppDO appDO = new TinyAppDO();
				appDO.setSecret("test secret");
				return appDO;
			}
		};
		
		request.setParameter(P_VERSION, VERSION_1);
		request.setParameter("method", "testMethod");
		request.setParameter(P_SIGN_METHOD, SIGN_METHOD_MD5);
		request.setParameter(P_SIGN, "620D6729667014FBAFA5618386A72799");
		ErrorCode errCode = (ErrorCode)method.invoke(otherCheckPipe, input);
		assertNull(errCode);
	}
	
	@Test
	public void testCheckV2HMacSign() throws Exception{
		Class clazz = otherCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("checkSign", TopPipeInput.class);
		method.setAccessible(true);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();	
		TopPipeInput input = new TopPipeInput(request,response,null){
			@Override
			public TinyAppDO getAppDO() {
				TinyAppDO appDO = new TinyAppDO();
				appDO.setSecret("test secret");
				return appDO;
			}
		};
		
		request.setParameter(P_VERSION, VERSION_2);
		request.setParameter("method", "testMethod");
		request.setParameter(P_SIGN_METHOD, SIGN_METHOD_HMAC);
		request.setParameter(P_SIGN, "A5D2BFD86D5B68E93941E094975BB173");
		ErrorCode errCode = (ErrorCode)method.invoke(otherCheckPipe, input);
		assertNull(errCode);
	}
	
	@Test
	public void testCheckV1HMacSign() throws Exception{
		Class clazz = otherCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("checkSign", TopPipeInput.class);
		method.setAccessible(true);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();	
		TopPipeInput input = new TopPipeInput(request,response,null){
			@Override
			public TinyAppDO getAppDO() {
				TinyAppDO appDO = new TinyAppDO();
				appDO.setSecret("test secret");
				return appDO;
			}
		};
		request.setParameter(P_VERSION, VERSION_1);
		request.setParameter("method", "testMethod");
		request.setParameter(P_SIGN_METHOD, SIGN_METHOD_HMAC);
		request.setParameter(P_SIGN, "8FD365F87F4E62B1DDCFE48FC171C174");
		ErrorCode errCode = (ErrorCode)method.invoke(otherCheckPipe, input);
		assertNull(errCode);
	}
}
