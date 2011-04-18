package com.taobao.top.core;

import static com.taobao.top.sm.Session.valueSplit;
import static com.taobao.top.sm.Session.version;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;

import com.taobao.top.sm.Session;
import com.taobao.top.sm.SessionGenerateException;
import com.taobao.top.sm.SessionValueInvalidException;

public class SessionTest {
	
	
	
	private String generateValue(String method,String userNick,String userId,long validStart,long validThru,String... apiKey ) throws SessionGenerateException{
		
		Session session = new Session();
		session.setV(Session.version);
		session.setMethod(method);
		session.setNick(userNick);
		session.setUserId(userId);
		Date date = new Date();
		date.setTime(validStart);
		session.setValidFrom(date);
		Date endDate = new Date();
		endDate.setTime(date.getTime()+validThru);
		session.setValidEnd(endDate);
		session.setAppKeys(apiKey);
		return session.convertToStr();
		
	}
	@Test
	/**
	 * Test method for
	 * {@link com.taobao.top.sm.Session#init(String, String)}
	 * {@link com.taobao.top.sm.Session#convertToStr()}
	 * .
	 * 
	 */
	public void testSessionConvert() throws SessionGenerateException, SessionValueInvalidException {
		long validStart = 1234567;
		String method = "method1";
		String sessionKey = "23421435463464";
		String userNick = "nick1";
		String userId = "uid1";
		String apiKey = "100100";
		Session session = new Session();
		session.setSessionId(sessionKey);
		session.setV(Session.version);
		session.setMethod(method);
		session.setNick(userNick);
		session.setUserId(userId);
		Date date = new Date();
		date.setTime(validStart);
		session.setValidFrom(date);
		Date endDate = new Date();
		endDate.setTime(date.getTime()+3600);
		session.setValidEnd(endDate);
		session.setAppKeys(apiKey);
		session.setProperty("abc", "testPropeties");
		String tempValue = session.convertToStr();
		
		Session b_session = new Session();
		b_session.init(sessionKey, tempValue);
		assertEquals(session.getProperty("abc"), b_session.getProperty("abc"));
	}
	
	/**
	 * Test method for
	 * {@link com.taobao.top.sm.DefaultSessionGenerator#generateValue(java.lang.String, java.lang.String, java.lang.String, long)}
	 * .
	 * 
	 * @throws SessionValueInvalidException
	 * @throws SessionGenerateException
	 */
	@Test
	public void testGenerateValue_正常情况() throws SessionValueInvalidException,
			SessionGenerateException {
		long validStart = 1234567;
		String method = "method1";
		String userNick = "nick1";
		String userId = "uid1";
		String apiKey = "100100";
		String value = generateValue(method, userNick, userId, validStart,
				3600, apiKey);
		
		assertEquals("" + version + valueSplit + method + valueSplit + apiKey
				+ valueSplit + userId + valueSplit + userNick + valueSplit
				+ validStart + valueSplit + (validStart + 3600), value);
	}
	@Test
	public void testGenerateValue_异常情况(){
		String sessionValue = null;
		//sessionNick null
		try{
			sessionValue = generateValue("all", null, "uid", 1234567, 100, "4272");
		}catch(Exception e){
			assertTrue(e instanceof SessionGenerateException);
			assertEquals("Need nick!",e.getMessage());
		}
		assertNull(sessionValue);
		//apiKeys null
		try{
			sessionValue = generateValue("all", "tbtest561", "uid", 1234567, 100, (String[])null);
		}catch(Exception e){
			assertTrue(e instanceof SessionGenerateException);
			assertEquals("Need apiKeys!",e.getMessage());
		}
		assertNull(sessionValue);
		//validThru < 0
		//apiKeys null
		try{
			sessionValue = generateValue("all", "tbtest561", "uid", 1234567, -1, "4272");
		}catch(Exception e){
			assertTrue(e instanceof SessionGenerateException);
			assertEquals("Invalid validThru:-1",e.getMessage());
		}
		assertNull(sessionValue);
		//method null userId null
		try {
			sessionValue = generateValue(null, "tbtest561", null, 1234567, 100, "4272");
		} catch (SessionGenerateException e) {
			fail();
		}
		String[] v = sessionValue.split("\n");
		assertEquals("all",v[1]);
		assertEquals("",v[3]);
	}

	/**
	 * Test method for
	 * {@link com.taobao.top.sm.DefaultSessionGenerator#generateSession(java.lang.String, java.lang.String)}
	 * .
	 * 
	 * @throws SessionValueInvalidException
	 * @throws SessionGenerateException
	 */
	@Test
	public void testGenerateSession_正常情况() throws SessionValueInvalidException,
			SessionGenerateException {
		String apiKey = "abcd";
		String sessionId = "2qwertyuiopasdfghjklzxcvbnmasdfqw";
		long validStart = new Date().getTime();
		String method = "method1";
		String userNick = "nick1";
		String userId = "uid1";
		Session session = generateSession(sessionId, generateValue(
				method, userNick, userId, validStart, 3600, apiKey));
		assertEquals(1, session.getV());
		assertEquals(2, session.getType());
		assertTrue(session.getAppKeys().contains(apiKey));
		assertEquals(sessionId, session.getSessionId());
		assertEquals(validStart, session.getValidFrom().getTime());
		assertEquals(validStart + 3600, session.getValidEnd().getTime());
		assertEquals(userId, session.getUserId());
		assertEquals(userNick, session.getNick());
		assertEquals(method, session.getMethod());

		session = generateSession(sessionId, generateValue(method,
				userNick, null, validStart, 3600, apiKey));
		assertEquals("", session.getUserId());
	}
	@Test
	public void testGenerateSession_异常情况(){
		Session session = null;
		String sessionId = "2qwertyuiopasdfghjklzxcvbnmasdfqw";
		//value为空
		try{
			session = generateSession(sessionId, null);
		}catch(Exception e){
			assertTrue(e instanceof SessionValueInvalidException);
			assertEquals(sessionId + ":" + null,e.getMessage());
		}
		assertNull(session);
		//value的长度不等于7
		try{
			session = generateSession(sessionId, "1\nall\n4272\n175754637\ntbtest561");
		}catch(Exception e){
			assertTrue(e instanceof SessionValueInvalidException);
			assertEquals("1\nall\n4272\n175754637\ntbtest561",e.getMessage());
		}
		assertNull(session);
		//没有apikey
		try{
			session = generateSession(sessionId, "1\nall\n\n175754637\ntbtest561\n1273710737084\n1273793537084");
		}catch(Exception e){
			assertTrue(e instanceof SessionValueInvalidException);
			assertEquals("Need apiKeys!",e.getMessage());
		}
		assertNull(session);
		//没有nick
		try{
			session = generateSession(sessionId, "1\nall\n4272\n175754637\n\n1273710737084\n1273793537084");
		}catch(Exception e){
			assertTrue(e instanceof SessionValueInvalidException);
			assertEquals("Need nick!",e.getMessage());
		}
		assertNull(session);
	}


	private Session generateSession(String sessionId, String value) throws SessionValueInvalidException {
		Session s = new Session();
		s.init(sessionId, value);
		return s;
	}

}
