/**
 * 
 */
package com.taobao.top.core;
import static com.taobao.top.sm.Session.valueSplit;
import static com.taobao.top.sm.Session.version;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.top.common.cache.tair.DefaultDuplicateClient;
import com.taobao.top.sm.DefaultSessionGenerator;
import com.taobao.top.sm.Session;
import com.taobao.top.sm.SessionGenerateException;
import com.taobao.top.sm.SessionNotExistException;
import com.taobao.top.sm.SessionRevertException;
import com.taobao.top.sm.SessionValueInvalidException;

/**
 * @version 2009-2-28
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public class DefaultSessionGeneratorTest {
	private DefaultSessionGenerator dsg = new DefaultSessionGenerator();
	private DefaultDuplicateClient cacheClient = null;
	/**sessionCacheClient
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-session-test.xml");
		cacheClient = (DefaultDuplicateClient)ctx.getBean("sessionCacheClient");
		dsg.setCacheClient(cacheClient);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.taobao.top.sm.DefaultSessionGenerator#generateOneTimeSession(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testGenerateOneTimeSession() throws Exception{
		dsg.setCacheClient(cacheClient);
		String oneTimeSession = dsg.generateOneTimeSession("all", "tbtest561", "123456", "appsecret", "zhenzi");
		assertEquals('3',oneTimeSession.charAt(0));
	}

	/**
	 * Test method for
	 * {@link com.taobao.top.sm.DefaultSessionGenerator#generateKeepAliveSession(java.lang.String, java.lang.String, java.lang.String, long, long, boolean)}
	 * .
	 */
	@Test
	public void testGenerateKeepAliveSession() throws Exception{
		//固定有效时间的
		String aliveSession = dsg.generateKeepAliveSession("all", "tbtest561", "123456",100,true, "appsecret", "zhenzi");
		assertEquals('1',aliveSession.charAt(0));
		//延长有效时间的
		aliveSession = dsg.generateKeepAliveSession("all", "tbtest561", "123456",100,false, "appsecret", "zhenzi");
		assertEquals('2',aliveSession.charAt(0));
	}

	/**
	 * Test method for
	 * {@link com.taobao.top.sm.DefaultSessionGenerator#revertSession(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testRevertSession() {
		String sessionId = null;
		char[] c = new char[37];
		for(int i = 0;i<c.length;i++){
			c[i] = 0;
		}
		sessionId = new String(c);
		
		Session session = null;
		try {
			session = dsg.revertSession(sessionId);
		} catch (SessionRevertException e) {
			assertTrue(e instanceof SessionNotExistException);
		}
		assertNull(session);
	}
	/*
	 * 测试generateSessionId
	 */
	@Test
	public void testGenerateSessionId()throws Exception{
		String tipSecret = "taobao1234";
		dsg.setTipSecret(tipSecret);
		Class clazz = dsg.getClass();
		Method method = clazz.getDeclaredMethod("generateSessionId", char.class,String.class,String.class,String[].class);
		method.setAccessible(true);
		//userId的长度大于等于4位
		{
			String userId = "214637";
			String appSecret = "taobao123";
			String appkey = "zhenzi";
			String[] appkeys = new String[]{appkey};
			
			StringBuilder md5 = new StringBuilder().append(appSecret).
			append(System.currentTimeMillis() / (24 * 60 * 60 * 1000)).append(appkey).append(userId).append(tipSecret);
			
			StringBuilder sessionKey = new StringBuilder("2");
			sessionKey.append("4637");
			sessionKey.append(DigestUtils.md5Hex(md5.toString().getBytes("utf-8")));
			
			String result = (String)method.invoke(dsg, '2',userId,appSecret,appkeys);
			assertEquals(sessionKey.toString(),result);
		}
		//userId的长度小于4位
		{
			String userId = "37";
			String appSecret = "taobao123";
			String appkey = "zhenzi";
			String[] appkeys = new String[]{appkey};
			
			StringBuilder md5 = new StringBuilder().append(appSecret).
			append(System.currentTimeMillis() / (24 * 60 * 60 * 1000)).append(appkey).append(userId).append(tipSecret);
			
			StringBuilder sessionKey = new StringBuilder("2");
			sessionKey.append("0037");
			sessionKey.append(DigestUtils.md5Hex(md5.toString().getBytes("utf-8")));
			
			String result = (String)method.invoke(dsg, '2',userId,appSecret,appkeys);
			assertEquals(sessionKey.toString(),result);
		}
	}
	
}
