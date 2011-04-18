/**
 * 
 */
package com.taobao.top.common.encrypt.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.taobao.top.common.encrypt.EncryptUtil;

/**
 * @version 2008-12-1
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class EncryptUtilTest {

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

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.taobao.api.util.EncryptUtil#signature(java.util.Map, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testSignature() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("iid", "c8900672b653983d330701993306508e");
		params.put("sip_apiname", "taobao.item.update");
		params.put("sip_appkey", "16565");
		params.put("sip_format", "xml");
		params.put("sip_sessionid", "ed3f6caee67805e33ffa2d90e80ca27f");
		params.put("sip_timestamp", "2008-12-01 13:04:39");
		params.put("v", "1.0");
		params.put("nick", "中文昵称");
		String sign = EncryptUtil.signature(params,
				"512cfd603c11dd9e349131b27826fb", null);
		assertEquals("979B39445821C5ECEF16A6D2A42C5346", sign);
	}

	@Test
	public void testSignature2() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("iid", "c8900672b653983d330701993306508e");
		params.put("sip_apiname", "taobao.item.update");
		params.put("sip_appkey", "16565");
		params.put("sip_format", "xml");
		params.put("sip_sessionid", "ed3f6caee67805e33ffa2d90e80ca27f");
		params.put("sip_timestamp", "2008-12-01 13:04:39");
		params.put("v", "1.0");
		params.put("nick", "中文昵称");
		String sign = EncryptUtil.signature2(params,
				"512cfd603c11dd9e349131b27826fb", false, false, null);
		assertEquals("979B39445821C5ECEF16A6D2A42C5346", sign);
	}
	
	@Test
	public void testCheckMD5Sign() throws Exception {
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("app_key", new String[]{"4272"});
		params.put("app_user_id", new String[]{"zixue"});
		params.put("app_user_email", new String[]{"zixue@taobao.com"});
		params.put("timestamp", new String[]{"2010-06-09 16:55:00"});
		Map<String, String> params2 = new HashMap<String, String>();
		params2.put("app_key", "4272");
		params2.put("app_user_id", "zixue");
		params2.put("app_user_email", "zixue@taobao.com");
		params2.put("timestamp", "2010-06-09 16:55:00");
		String sign = EncryptUtil.signature2(params2,
				"0ebbcccfee18d7ad1aebc5b135ffa906", true, false, "sign");
		boolean rs = EncryptUtil.checkMD5Sign(params, "0ebbcccfee18d7ad1aebc5b135ffa906", sign, "sign");
		System.out.println("create sign: " + sign);
		assertTrue(rs);
	}

}
