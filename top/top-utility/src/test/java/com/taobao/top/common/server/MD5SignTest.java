/**
 * 
 */
package com.taobao.top.common.server;


import java.io.UnsupportedEncodingException;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @version 2008-11-7
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 *
 */
public class MD5SignTest {

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
	
	@Test
	public void testSignature() throws UnsupportedEncodingException {
		String url = "http://sip.alisoft.com/sip/token?&sip_appkey=13581&sip_apiname=taobao.items.onsale.get&sip_sessionid=4144637&sip_tokenttl=3600&sip_apptype=1&sip_username=kenvin9";
		Map<String, String> map = UrlKit.getMapFromParameters(url);
		String sign = MD5Sign.signature(map, null, "12b52a101ffb11dd68b6f0a50cc9900b");
		Assert.assertFalse("816C186276248AF9C33C981770F67D4E".equals(sign));
	}
	
	@Test
	public void testSignature2() throws UnsupportedEncodingException {
		String url = "http://10.2.224.46:8180/sip/token?&sip_appkey=100&sip_apiname=taobao.item.add&sip_sessionid=tiehuaTest&sip_apptype=1&sip_username=tbtest5&sip_tokenttl=3600";
		Map<String, String> map = UrlKit.getMapFromParameters(url);
		String sign = MD5Sign.signature(map, null, "12b52a101ffb11dd68b6f0a50cc9900b");
		Assert.assertEquals("12A7A91FE49BA0FF9C4C76D5FEE2F525", sign);
	}

}
