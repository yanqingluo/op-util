package com.taobao.top.sm;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.top.tim.domain.AppAccountDO;
import com.taobao.top.tim.domain.AuthDO;
import com.taobao.top.tim.domain.AuthorizeDO;
import com.taobao.top.tim.domain.AuthorizeResultDO;
import com.taobao.top.tim.domain.ExPropertyDO;
import com.taobao.top.tim.domain.PlatformDO;
import com.taobao.top.tim.domain.RuleDO;
import com.taobao.top.tim.domain.SubscribeDO;
import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.tim.service.SamService;
import com.taobao.top.tim.service.TIMServiceException;
import com.taobao.top.timwrapper.manager.TadgetManager;

/**
 * 
 * @author zhnezi
 *
 */
public class TairSessionManagerImplTest {
	private TairSessionManagerImpl sessionManager;
	private DefaultSessionGenerator sessionGenerator;
	@Before
	public void setUp() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-session-test.xml");
		sessionGenerator = (DefaultSessionGenerator) ctx.getBean("sessionGenerator");
		sessionManager = new TairSessionManagerImpl();
		sessionManager.setSessionGenerator(sessionGenerator);
		TadgetManager tadgetManager = new TadgetManager();
		tadgetManager.setTimService(new SamService() {
			
			@Override
			public int unbindAppAccount(String arg0, String arg1, Long arg2)
					throws TIMServiceException {
				return 0;
			}
			
			@Override
			public TinyApiDO getValidApiByApiName(String arg0)
					throws TIMServiceException {
				return null;
			}
			
			@Override
			public RuleDO getRuleByIdAndType(Long arg0, Long arg1)
					throws TIMServiceException {
				return null;
			}
			
			@Override
			public PlatformDO getPlatformById(Long arg0) throws TIMServiceException {
				return null;
			}
			
			@Override
			public Date getExpiryDate(String arg0, Long arg1)
					throws TIMServiceException {
				return null;
			}
			
			@Override
			public AppAccountDO getBindedAppAccountByAppKeyAppUidTaobaoUid(String arg0,
					String arg1, Long arg2) throws TIMServiceException {
				return null;
			}
			
			@Override
			public List<String> getAppkeysByPlatformIdUserId(Long arg0, Long arg1)
					throws TIMServiceException {
				return null;
			}
			
			@Override
			public TinyAppDO getAppByKey(String arg0) throws TIMServiceException {
				return null;
			}
			
			@Override
			public TinyAppDO getAppById(Long arg0) throws TIMServiceException {
				return null;
			}
			
			@Override
			public Long createChildApp(String arg0, String arg1, String arg2,
					String arg3, String arg4) throws TIMServiceException {
				return null;
			}
			
			@Override
			public boolean checkUserPermitForApp(Long arg0, Long arg1)
					throws TIMServiceException {
				return false;
			}
			
			@Override
			public Long bindAppAccount(String arg0, String arg1, Long arg2)
					throws TIMServiceException {
				return null;
			}

			@Override
			public Long addAuthorize(AuthorizeDO arg0)
					throws TIMServiceException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Long addSubscribe(SubscribeDO arg0)
					throws TIMServiceException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public AuthorizeResultDO getAuthorize(String arg0, List<Long> arg1,
					Long arg2, Long arg3) throws TIMServiceException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<String> getAuthorizedAppKeys(Long arg0)
					throws TIMServiceException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<TinyAppDO> getAuthorizedApps(Long arg0, Integer arg1,
					Integer arg2) throws TIMServiceException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<SubscribeDO> getAuthorizedSubscribes(Long arg0,
					Integer arg1, Integer arg2, Long arg3)
					throws TIMServiceException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<ExPropertyDO> getExtPropertyByKeyAndType(String arg0,
					Long arg1) throws TIMServiceException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public AuthDO getPersistentAuthBySessionKey(String arg0)
					throws TIMServiceException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SubscribeDO getSubscribe(String arg0)
					throws TIMServiceException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void insertOrUpdateAuth(AuthDO arg0)
					throws TIMServiceException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void removePersistentAuth(long arg0, long arg1)
					throws TIMServiceException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public int updateAuthorize(AuthorizeDO arg0)
					throws TIMServiceException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int updateSubscribe(SubscribeDO arg0)
					throws TIMServiceException {
				// TODO Auto-generated method stub
				return 0;
			}
		});
		sessionManager.setTadgetManager(tadgetManager);
	}

	@After
	public void tearDown() throws Exception {
	}
	/*
	 * 测试getSubUid
	 */
	@Test
	public void testGetSubUID()throws Exception{
		Class clazz = sessionManager.getClass();
		Method method = clazz.getDeclaredMethod("getSubUID", String.class);
		method.setAccessible(true);
		String result = null;
		//userId null
		result = (String)method.invoke(sessionManager, (String)null);
		assertEquals("",result);
		//userId 长度大于4
		result = (String)method.invoke(sessionManager, "14444");
		assertEquals("4444",result);
		//userid 长度小于4
		result = (String)method.invoke(sessionManager, "12");
		assertEquals("0012",result);
	}
	/*
	 * 测试revertValidSession
	 */
	@Test
	public void testRevertValidSession_异常情况()throws Exception{
		//
		Session session = null;
		try{
			session = sessionManager.revertValidSession(null, null);
		}catch(Exception e){
			assertTrue(e instanceof SessionNotExistException);
		}
		assertNull(session);
		//sessionId不存在
		try{
			session = sessionManager.revertValidSession("not_exists", "4272");
		}catch(Exception e){
			assertTrue(e instanceof SessionNotExistException);
		}
		assertNull(session);
		//生成sessionId的appkey与传递的appkey不一样
		String sessionId = sessionGenerator.generateKeepAliveSession("all", "tbtest561", "123", 1000,false, "appSecret", "4272");
		try{
			session = sessionManager.revertValidSession(sessionId, "zhenzi");
		}catch(Exception e){
			assertTrue(e instanceof SessionOwnerUnmatchException);
			assertEquals("zhenzi no privilege!",e.getMessage());
		}
		assertNull(session);
		//超过deadline
		{
			DefaultSessionGenerator temp = new DefaultSessionGenerator(){
				@Override
				public Session revertSession(String sessionId){
					Session s = new Session();
					s.setSessionId(sessionId);
					s.setUserId("1232");
					Set<String> appkeys  = new HashSet<String>();
					appkeys.add("zhenzi");
					s.setAppKeys(appkeys);
					s.setValidEnd(new Date(System.currentTimeMillis() + 10000));
					s.setProperty(Session.SESSION_DEAD_LINE, String.valueOf(System.currentTimeMillis()-1000L));
					return s;
					
				}
				@Override
				public void deleteSessionId(String sessionId) throws SessionRevertException{
					
				}
			};
			sessionManager.setSessionGenerator(temp);
			
			try{
				session = sessionManager.revertValidSession("expired", "zhenzi");
			}catch(Exception e){
				assertTrue(e instanceof SessionExpiredException);
				assertEquals("Session invalidation be fired by session deadline",e.getMessage());
			}
		}
		//已经过期了
		{
			DefaultSessionGenerator temp = new DefaultSessionGenerator(){
				@Override
				public Session revertSession(String sessionId){
					Session s = new Session();
					Set<String> appkeys  = new HashSet<String>();
					appkeys.add("zhenzi");
					s.setAppKeys(appkeys);
					s.setValidEnd(new Date(System.currentTimeMillis() - 10000));
					return s;
					
				}
				@Override
				public void deleteSessionId(String sessionId) throws SessionRevertException{
					
				}
			};
			sessionManager.setSessionGenerator(temp);
			try{
				session = sessionManager.revertValidSession("expired", "zhenzi");
			}catch(Exception e){
				assertTrue(e instanceof SessionExpiredException);
				assertEquals("Session expired!",e.getMessage());
			}
		}
		//sessionkey对应的Session的uid后四位和sessionKey中的不匹配
		{
			DefaultSessionGenerator temp = new DefaultSessionGenerator(){
				@Override
				public Session revertSession(String sessionId){
					Session s = new Session();
					Set<String> appkeys  = new HashSet<String>();
					appkeys.add("zhenzi");
					s.setAppKeys(appkeys);
					s.setValidEnd(new Date(System.currentTimeMillis() + 10 * 60 * 1000 * 1000));
					s.setSessionId("246379f2e6ea4c9316a0241dfbb0b974a9c22");
					return s;
					
				}
			};
			sessionManager.setSessionGenerator(temp);
			try{
				session = sessionManager.revertValidSession("200009f2e6ea4c9316a0241dfbb0b974a9c22", "zhenzi");
			}catch(Exception e){
				assertTrue(e instanceof SessionOwnerUnmatchException);
				assertEquals("200009f2e6ea4c9316a0241dfbb0b974a9c22 no privilege!",e.getMessage());
			}
		}
		//更新时间
		{
			DefaultSessionGenerator temp = new DefaultSessionGenerator(){
				@Override
				public Session revertSession(String sessionId){
					Session s = new Session();
					Set<String> appkeys  = new HashSet<String>();
					appkeys.add("zhenzi");
					s.setAppKeys(appkeys);
					s.setType(2);
					s.setUserId("4637");
					s.setValidEnd(new Date(System.currentTimeMillis() + 10 * 60 * 1000 * 1000));
					s.setSessionId("246379f2e6ea4c9316a0241dfbb0b974a9c22");
					return s;
				}
				@Override
				public void updateSession(Session session) throws SessionRevertException,
				SessionGenerateException{
					session.setMethod("change");
				}
			};
			sessionManager.setSessionGenerator(temp);
			session = sessionManager.revertValidSession("246379f2e6ea4c9316a0241dfbb0b974a9c22", "zhenzi");
			assertEquals("change",session.getMethod());
		}
		//最后把sessionManager关联的对象还原
		sessionManager.setSessionGenerator(sessionGenerator);
	}

}
