package com.taobao.top.core.framework.pipe;


import static com.taobao.top.core.ProtocolConstants.P_FORMAT;
import static com.taobao.top.core.ProtocolConstants.P_METHOD;
import static com.taobao.top.core.ProtocolConstants.P_TIMESTAMP;
import static com.taobao.top.core.ProtocolConstants.P_VERSION;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.taobao.top.common.TOPConstants;
import com.taobao.top.core.Api;
import com.taobao.top.core.ApiConfigException;
import com.taobao.top.core.ApiFactory;
import com.taobao.top.core.ApiType;
import com.taobao.top.core.DefaultApi;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.FullErrorInfo;
import com.taobao.top.core.framework.AbstractTopPipeManager;
import com.taobao.top.core.framework.TopPipeData;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeManager;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.log.TopLog;
import com.taobao.top.log.asyn.AsynWriterTemplate;
import com.taobao.top.log.asyn.IAsynWriter;
import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.tim.service.TIMServiceException;
import com.taobao.top.timwrapper.manager.TadgetManager;

/**
 * 
 * @author zhenzi
 *
 */
public class ProtocolMustParamCheckPipeTest {
	private ProtocolMustParamCheckPipe correctCheckPipe ;
	private TopPipeManager topPipeManager;
	private IAsynWriter<TopLog> asynLogWriter;
	@Before
	public void setUp() throws Exception {
		//准备pipe
		correctCheckPipe = new ProtocolMustParamCheckPipe();
		//
		topPipeManager = new TopPipeManager();
		topPipeManager.register(correctCheckPipe);
		asynLogWriter = new AsynWriterTemplate<TopLog>();
		topPipeManager.setAsynLogWriter(asynLogWriter);
	}

	@After
	public void tearDown() throws Exception {
		topPipeManager.unregister(correctCheckPipe);
	}
	/**
	 * 测试checkIsvPermission方法
	 */
	@Test
	public void testCheckIsvPermission()throws Exception{
		Class<ProtocolMustParamCheckPipe> clazz = (Class<ProtocolMustParamCheckPipe>) correctCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("checkIsvPermission", TinyAppDO.class,TinyApiDO.class);
		method.setAccessible(true);
		//验证没通过
		correctCheckPipe.setTadgetManager(new TadgetManager(){
			@Override
			public boolean appCanAccessApi(TinyAppDO app, TinyApiDO api)
					throws TIMServiceException {
				return false;
			}
		});	
		ErrorCode errCode = (ErrorCode)method.invoke(correctCheckPipe, null,null);
		assertEquals(ErrorCode.INSUFFICIENT_ISV_PERMISSIONS,errCode);
		//TIM异常
		correctCheckPipe.setTadgetManager(new TadgetManager(){
			@Override
			public boolean appCanAccessApi(TinyAppDO app, TinyApiDO api)
					throws TIMServiceException {
				throw new TIMServiceException("tim exception");
			}
		});
		errCode = (ErrorCode)method.invoke(correctCheckPipe, null,null);
		assertEquals(null,errCode);
		//检查通过了
		correctCheckPipe.setTadgetManager(new TadgetManager(){
			@Override
			public boolean appCanAccessApi(TinyAppDO app, TinyApiDO api)
					throws TIMServiceException {
				return true;
			}
		});
		errCode = (ErrorCode)method.invoke(correctCheckPipe, null,null);
		assertNull(errCode);
	}
	/**
	 * 测试checkAppCorrect
	 */
	@Test
	public void testCheckAppCorrect()throws Exception{
		Class clazz = correctCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("checkAppCorrect", TinyAppDO.class);
		method.setAccessible(true);
		//appDO null
		TinyAppDO appDO = null;
		FullErrorInfo errInfo = (FullErrorInfo)method.invoke(correctCheckPipe, appDO);
		assertEquals(ErrorCode.INVALID_APP_KEY,errInfo.getErrorCode());
		assertEquals(TOPConstants.APPKEY_INVALID_NOT_EXIST,errInfo.getSubErrCode());
		//app的状态不对
		appDO = new TinyAppDO();
		appDO.setAppStatus(TinyAppDO.APP_STATUS_DEVELOPING);
		errInfo = (FullErrorInfo)method.invoke(correctCheckPipe, appDO);
		assertEquals(ErrorCode.INVALID_APP_KEY,errInfo.getErrorCode());
		assertEquals(TOPConstants.APPKEY_INVALID_INVALID_STATUS,errInfo.getSubErrCode());
	}
	/**
	 * 测试checkTimestamp
	 */
	@Test
	public void testCheckTimestamp()throws Exception{
		Class clazz = correctCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("checkTimestamp",String.class);
		method.setAccessible(true);
		//时间戳的格式不对
		ErrorCode errCode = (ErrorCode)method.invoke(correctCheckPipe, "20100202f");
		assertEquals(ErrorCode.INVALID_TIMESTAMP,errCode);
		//时间戳的格式正确
		errCode = (ErrorCode)method.invoke(correctCheckPipe, "2010-02-02");
		assertNull(errCode);
	}
	/**
	 * 测试checkFormat
	 */
	@Test
	public void testCheckFormat()throws Exception{
		Class clazz = correctCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("checkFormat",String.class);
		method.setAccessible(true);
		//正确的格式
		ErrorCode errCode = (ErrorCode)method.invoke(correctCheckPipe, "xml");
		assertNull(errCode);
		errCode = (ErrorCode)method.invoke(correctCheckPipe, "json");
		assertNull(errCode);
		errCode = (ErrorCode)method.invoke(correctCheckPipe, "html");
		assertNull(errCode);
		//错误的格式
		errCode = (ErrorCode)method.invoke(correctCheckPipe, "XML");
		assertEquals(ErrorCode.INVALID_FORMAT,errCode);
		
		errCode = (ErrorCode)method.invoke(correctCheckPipe, "RSS");
		assertEquals(ErrorCode.INVALID_FORMAT,errCode);
	}
	/**
	 * 测试checkVersion
	 */
	@Test
	public void testCheckVersion()throws Exception{
		Class clazz = correctCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("checkVersion",Api.class,String.class);
		method.setAccessible(true);
		Api api = new DefaultApi(){
			@Override
			public String[] getSupportedVersions(){
				return new String[]{"1.0"};
			}
		};
		//传递的版本错误
		ErrorCode errCode = (ErrorCode)method.invoke(correctCheckPipe, api,"3.0");
		assertEquals(ErrorCode.INVALID_VERSION,errCode);
		//不支持的版本
		errCode = (ErrorCode)method.invoke(correctCheckPipe, api,"2.0");
		assertEquals(ErrorCode.UNSUPPORTED_VERSION,errCode);
		//找到了匹配的，则返回null
		errCode = (ErrorCode)method.invoke(correctCheckPipe, api,"1.0");
		assertNull(errCode);
		//默认版本为1.0
		api = new DefaultApi(){
			@Override
			public String[] getSupportedVersions(){
				return null;
			}
		};
		errCode = (ErrorCode)method.invoke(correctCheckPipe, api,"2.0");
		assertEquals(ErrorCode.UNSUPPORTED_VERSION,errCode);
	}
	
	
	/*
	 * 测试doPipe
	 */
	@Test
	public void testDoPipe()throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		//api null
		correctCheckPipe.setApiFactory(new ApiFactory(){
			@Override
			public Api getApi(String method) throws ApiConfigException {
				return null;
			}
			
		});
		correctCheckPipe.setTadgetManager(new TadgetManager(){
			@Override
			public TinyApiDO getValidApiByApiName(String apiname)
					throws TIMServiceException {
				TinyApiDO api = new TinyApiDO();
				api.setName(apiname);
				return api;
			}
		});
		TopPipeInput input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
				topPipeManager).inputInstance(request, response, null);
		TopPipeResult result = topPipeManager.doPipes(input);
		assertEquals(ErrorCode.INVALID_METHOD, result.getErrorCode());
		//http动作检查
		correctCheckPipe.setApiFactory(new ApiFactory(){
			@Override
			public Api getApi(String method) throws ApiConfigException {
				return new DefaultApi(){
					@Override
					public ApiType getApiType(){
						return ApiType.UPDATE;
					}
				};
			}
			
		});
		request.setMethod("GET");
		input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
				topPipeManager).inputInstance(request, response, null);
		result = topPipeManager.doPipes(input);
		assertEquals(ErrorCode.HTTP_ACTION_NOT_ALLOWED,result.getErrorCode());
		//检验都通过
		TadgetManager manager = new TadgetManager(){
			@Override
			public TinyAppDO getTadgetByKey(String appKey) throws TIMServiceException {
				TinyAppDO appDO = new TinyAppDO();
				appDO.setAppStatus(TinyAppDO.APP_STATUS_RUNNING);
				return appDO;
			}
			@Override
			public TinyApiDO getValidApiByApiName(String apiname)
					throws TIMServiceException {
				TinyApiDO api = new TinyApiDO();
				api.setName(apiname);
				return api;
			}
			@Override
			public boolean appCanAccessApi(TinyAppDO app, TinyApiDO api)
					throws TIMServiceException {
				return true;
			}
		};
	
		correctCheckPipe.setTadgetManager(manager);
		request.setMethod("POST");
		request.setParameter(P_VERSION, "1.0");
		request.setParameter(P_FORMAT, "xml");
		request.setParameter(P_TIMESTAMP, "2010-02-02");
		request.setParameter(P_METHOD, "taobao.user.get");
		input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
				topPipeManager).inputInstance(request, response, null);
		result = topPipeManager.doPipes(input);
	}
	
	@Test
	public void testcheckAppIpWhite()throws Exception{
		Class clazz = correctCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("checkAppIpWhite",String.class,String.class);
		method.setAccessible(true);
		{
			boolean result = (Boolean)method.invoke(correctCheckPipe, (String)null,(String)null);
			assertTrue(result);
		}
		{
			boolean result = (Boolean)method.invoke(correctCheckPipe, "192.168.207.127,192.168.207.222","192.168.207.107");
			assertFalse(result);
		}
		{
			boolean result = (Boolean)method.invoke(correctCheckPipe, "192.168.207.127,192.168.207.222","192.168.207.222");
			assertTrue(result);
		}
	}
}
