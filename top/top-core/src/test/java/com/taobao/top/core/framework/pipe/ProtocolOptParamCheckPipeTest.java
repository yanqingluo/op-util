package com.taobao.top.core.framework.pipe;


import static com.taobao.top.core.ProtocolConstants.P_SESSION;
import static com.taobao.top.core.ProtocolConstants.P_SESSION_NICK;
import static com.taobao.top.core.ProtocolConstants.P_SESSION_UID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.taobao.top.core.Api;
import com.taobao.top.core.DefaultApi;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.framework.TopPipeContext;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeManager;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.log.TopLog;
import com.taobao.top.log.asyn.AsynWriterTemplate;
import com.taobao.top.log.asyn.IAsynWriter;
import com.taobao.top.privilege.WhiteListManagerImpl;
import com.taobao.top.sm.DefaultSessionGenerator;
import com.taobao.top.sm.TairSessionManagerImpl;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.xbox.framework.IPipeContext;
import com.taobao.top.xbox.framework.PipeContextManager;


/**
 * 
 * @author zhenzi
 * TODO： 非常奇怪的类路径问题，说不能实例化com.taobao.top.common.MockDuplicateCacheClient
 * 但是，类明显是在的。
 */
@Ignore
public class ProtocolOptParamCheckPipeTest {
	private ProtocolOptParamCheckPipe optParamCheckPipe ;
	private TopPipeManager topPipeManager;
	private IAsynWriter<TopLog> asynLogWriter;
	private TairSessionManagerImpl topSessionManager;
	private DefaultSessionGenerator sessionGenerator;
	private String sessionId = null;
	private WhiteListManagerImpl whiteList;
	@Before
	public void setUp() throws Exception {
		optParamCheckPipe = new ProtocolOptParamCheckPipe();
		topPipeManager = new TopPipeManager();
		topPipeManager.register(optParamCheckPipe);
		asynLogWriter = new AsynWriterTemplate<TopLog>();
		topPipeManager.setAsynLogWriter(asynLogWriter);
		whiteList = new WhiteListManagerImpl();
		optParamCheckPipe.setWhiteListManager(whiteList);
		//
		ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-test.xml");
		topSessionManager = (TairSessionManagerImpl)ctx.getBean("topSessionManager");
		sessionGenerator = (DefaultSessionGenerator) ctx.getBean("sessionGenerator");
		optParamCheckPipe.setSessionManager(topSessionManager);
		sessionId = sessionGenerator.generateKeepAliveSession("all", "tbtest561", "100", 30 * 60,false, "gaoerrong", new String[]{"zhenzi"});
		
	}

	@After
	public void tearDown() throws Exception {
		topPipeManager.unregister(optParamCheckPipe);
	}
	/*
	 * 测试getSelfUseAppBindNick_UserId
	 */
	@Test
	public void testGetSelfUseAppBindNick_UserId()throws Exception{
		Class clazz = optParamCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("getSelfUseAppBindNick_UserId", TinyAppDO.class);
		method.setAccessible(true);
		//appDO == null
		String[] result = (String[])method.invoke(optParamCheckPipe, (TinyAppDO)null);
		assertNull(result);
		//bindNick = null && userId = null
		TinyAppDO appDO = new TinyAppDO();
		result = (String[])method.invoke(optParamCheckPipe, appDO);
		assertNull(result);
		//bindNick != null && userId = null
		appDO.setBindNick("tbtest561");
		appDO.setUserId(null);
		result = (String[])method.invoke(optParamCheckPipe, appDO);
		assertNull(result);
		//bindNick == null && userId != null
		appDO.setBindNick(null);
		appDO.setUserId(1000l);
		result = (String[])method.invoke(optParamCheckPipe, appDO);
		assertNull(result);		
		//bindNick != null && userId != null
		appDO.setBindNick("tbtest561");
		appDO.setUserId(1000l);
		result = (String[])method.invoke(optParamCheckPipe, appDO);
		assertEquals(2,result.length);
		assertEquals("tbtest561",result[0]);
		assertEquals("1000",result[1]);
	}
	/*
	 *测试checkSession方法
	 */
	@Test
	public void testCheckSession_需要sessionkey的校验()throws Exception{
		Class clazz = optParamCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("checkSession", TopPipeInput.class);
		method.setAccessible(true);
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();		
		
		TopPipeInput input = new TopPipeInput(request,response,null);
		/*
		 * 需要进行sessionkey的校验
		 */
		//session不存在
		input.setSession("not_exists");
		ErrorCode errCode = (ErrorCode)method.invoke(optParamCheckPipe, input);
		assertEquals(ErrorCode.INVALID_SESSION,errCode);
		//session过期了
//		String tempSession = topSessionManager.generateActiveSession("all", "tbtest561", "1", 100, "gaoerrong", new String[]{"zhenzi"});
//		input.setSession(tempSession);
//		errCode = (ErrorCode)method.invoke(optParamCheckPipe, input);
//		assertEquals(ErrorCode.INVALID_SESSION,errCode);
		//生成session的appkey与传递的appkey不匹配
		input.setSession(sessionId);
		input.setAppKey("4272");
		errCode = (ErrorCode)method.invoke(optParamCheckPipe, input);
		assertEquals(ErrorCode.INVALID_SESSION,errCode);
		//正确情况下
		input.setSession(sessionId);
		input.setAppKey("zhenzi");
		errCode = (ErrorCode)method.invoke(optParamCheckPipe,input);
		assertNull(errCode);
		assertEquals("tbtest561",input.getSessionNick());
		assertEquals("100",input.getSessionUid());		
	}
	@Test
	public void testCheckSession_不需要sessionkey的校验()throws Exception{
		Class clazz = optParamCheckPipe.getClass();
		Method method = clazz.getDeclaredMethod("checkSession", TopPipeInput.class);
		method.setAccessible(true);
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();		
		TopPipeInput input = new TopPipeInput(request,response,null);
		/*
		 * 以下测试在白名单里，但是非自用型的
		 */
		List<String> appkeyList = new ArrayList<String>();
		appkeyList.add("zhenzi");
		whiteList.setWhiteAppList(appkeyList);
		List<String> ipList = new ArrayList<String>();
		ipList.add("127.0.0.1");
		whiteList.setWhiteIpList(ipList);
		input.setAppKey("zhenzi");
		ErrorCode errCode = (ErrorCode)method.invoke(optParamCheckPipe, input);
		assertEquals(ErrorCode.INVALID_SESSION,errCode);
		//正确
		request.setParameter(P_SESSION_UID, "uid");
		request.setParameter(P_SESSION_NICK, "nick");
		errCode = (ErrorCode)method.invoke(optParamCheckPipe, input);
		assertNull(errCode);
		assertEquals("uid",input.getSessionUid());
		assertEquals("nick",input.getSessionNick());
		/*
		 * 以下测试自用型的appkey
		 */
		input = new TopPipeInput(request,response,null){
			@Override
			public TinyAppDO getAppDO() {
				TinyAppDO appDO = new TinyAppDO();
				appDO.setBindNick("tbtest561");
				appDO.setUserId(100l);
				return appDO;
			}
		};
		input.setAppKey("zhenzi");
		whiteList.setWhiteAppList(null);
		whiteList.setWhiteIpList(null);
		errCode = (ErrorCode)method.invoke(optParamCheckPipe, input);
		assertNull(errCode);
		assertEquals("tbtest561",input.getSessionNick());
		assertEquals("100",input.getSessionUid());
	}
	/*
	 * 以下代码测试doPipe
	 */
	@Ignore
	@Test
	public void testDoPipe()throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		TopPipeInput input = new TopPipeInput(request,response,null);
		Api api = new DefaultApi(){
			@Override
			public List<String> getProtocolMustParams() {
				List<String> list = new ArrayList<String>();
				list.add(P_SESSION);
				return list;
			}
		};
		input.setApi(api);
		
		TopPipeResult result = topPipeManager.doPipes(input);
		assertEquals(ErrorCode.MISSING_SESSION,result.getErrorCode());
	}
	/*
	 * 以下代码测试ignoreIt
	 */
	@Test
	public void testIgnoreIt()throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		TopPipeResult result = null;
		
		IPipeContext context = new TopPipeContext();
		PipeContextManager.setContext(context);
		
		
		//上一个 pipe生成了错误码
		{
			result = new TopPipeResult();
			result.setErrorCode(ErrorCode.INVALID_APP_KEY);
			boolean r = optParamCheckPipe.ignoreIt(null, result);
			assertTrue(r);
		}
		//input中的Api对象为null
		{
			result = new TopPipeResult();
			TopPipeInput input = new TopPipeInput(request,response,null);
			input.setApi(null);
			boolean r = optParamCheckPipe.ignoreIt(input, result);
			assertTrue(r);
		}
		//api的必填协议中要求必须传递session
		{
			result = new TopPipeResult();
			TopPipeInput input = new TopPipeInput(request,response,null);
			Api api = new DefaultApi(){
				@Override
				public List<String> getProtocolMustParams() {
					List<String> list = new ArrayList<String>();
					list.add(P_SESSION);
					return list;
				}
			};
			input.setApi(api);
			boolean r = optParamCheckPipe.ignoreIt(input, result);
			assertTrue(!r);
		}
		//api的必填协议中没有session，可选参数中也没有
		{
			result = new TopPipeResult();
			TopPipeInput input = new TopPipeInput(request,response,null);
			Api api = new DefaultApi(){
				@Override
				public List<String> getProtocolPrivateParams() {
					List<String> list = new ArrayList<String>();
					list.add("no_session");
					return list;
				}
			};
			input.setApi(api);
			boolean r = optParamCheckPipe.ignoreIt(input, result);
			assertTrue(r);
		}
		//api的必填协议中没有session，可选参数中包括session，1，是自用型的
		{
			result = new TopPipeResult();
			TopPipeInput input = new TopPipeInput(request,response,null){
				@Override
				public TinyAppDO getAppDO() {
					TinyAppDO appDO = new TinyAppDO();
					appDO.setBindNick("tbtest561");
					appDO.setUserId(100l);
					return appDO;
				}
			};
			Api api = new DefaultApi(){
				@Override
				public List<String> getProtocolPrivateParams() {
					List<String> list = new ArrayList<String>();
					list.add(P_SESSION);
					return list;
				}
			};
			input.setApi(api);
			boolean r = optParamCheckPipe.ignoreIt(input, result);
			assertTrue(!r);
		}
		//api的必填协议参数中没有session,可选参数中包括session，2，非自用型的，用户传递了session
		{
			request.setParameter(P_SESSION, "session");
			result = new TopPipeResult();
			TopPipeInput input = new TopPipeInput(request,response,null);
			Api api = new DefaultApi(){
				@Override
				public List<String> getProtocolPrivateParams() {
					List<String> list = new ArrayList<String>();
					list.add(P_SESSION);
					return list;
				}
			};
			input.setApi(api);
			boolean r = optParamCheckPipe.ignoreIt(input, result);
			assertTrue(!r);
		}
	}
}
