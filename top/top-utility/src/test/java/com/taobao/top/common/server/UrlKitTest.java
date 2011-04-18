/**
 * 
 */
package com.taobao.top.common.server;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.taobao.top.common.encrypt.EncryptUtil;

/**
 * @version 2008-8-12
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class UrlKitTest {

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
	 * {@link com.taobao.top.common.server.UrlKit#getMapFromParameters(java.lang.String)}
	 * .
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testGetMapFromParameters() throws UnsupportedEncodingException {
		Map<String, String> map = UrlKit
				.getMapFromParameters("http://localhost:9900/item/bought?sipCall=lwL3Rva2Vu&");
		assertEquals("lwL3Rva2Vu", map.get("sipCall"));

		map = UrlKit.getMapFromParameters("sipCall=abc&a=1");
		assertEquals("abc", map.get("sipCall"));
	}
	
	@Test
	public void testCreateUrl() throws UnsupportedEncodingException{
		Map<String, String> map = new TreeMap<String, String>();
		map.put("a", "1");
		map.put("b", "a@");
		String path = "http://www.taobao.com/a";
		assertEquals(path + '?' + "a=1&b=a%40", UrlKit.createUrl(path, map, "utf-8"));
	}
	
	public static void main(String[] args) throws Exception {
		String[] a = new String[]{
				"http://container.api.taobao.com/container/connect?app_key=4272&app_user_id=test13xxx33&app_user_mobile=13858046625&app_user_email=xiajiaxnlan%40tt.com&timestamp=2010-06-28+16:55:00&sign=A2E455BF214130FCEADD9F88C3BFCBCD",
				"http://container.api.taobao.com/container/connect?app_key=10011201&app_user_id=test25&app_user_mobile=13852246688&app_user_email=xiajianlan%40tt.com&timestamp=2010-06-28+16:55:00&sign=A2E455BF214130FCEADD9F88C3BFCBCD",
				"http://container.api.taobao.com/container/connect?app_key=10011201&app_user_id=test25&app_user_mobile=13852246688&app_user_email=xiajianlan88hot%40tt.com&timestamp=2010-06-28+16:55:00&sign=A2E455BF214130FCEADD9F88C3BFCBCD",
				"http://container.api.taobao.com/container/connect?app_key=10011201&app_user_id=test25&app_user_mobile=13152246688&app_user_email=xiajianlan%40tt.com&timestamp=2010-06-28+16:55:00&sign=A2E455BF214130FCEADD9F88C3BFCBCD",
				"http://container.api.taobao.com/container/connect?app_key=10011201&app_user_id=test02&app_user_mobile=13852246688&app_user_email=xiajianlan60%40tt.com&timestamp=2010-06-28+16:55:00&sign=A2E455BF214130FCEADD9F88C3BFCBCD",
				"http://container.api.daily.taobao.net/container/register?app_key=4272&app_user_nick=zixue&app_user_mobile=13867451622&sign=xxx&sign_method=md5",
				"http://container.api.taobao.com/container/connect?sign=50ECE00ED3BC03FA492E5A461E0F8D80&timestamp=2010-06-28+19%3A55%3A00&app_user_email=xiajianlan%40tt.com&app_user_mobile=13152246688&app_user_id=test2525&app_key=10011201"};
		for (String url: a) {
			System.out.println(getUrl(url));
		}
	}
	
	private static String getUrl(String url) throws Exception {
		Map<String, String> maps = UrlKit.getMapFromParameters(url);
		int index = url.indexOf('?');
		if (index >= 0) {
			url = url.substring(0,index);
		}
		maps.put("timestamp", DateKit.date2ymdhms(new Date()));
		String sign = EncryptUtil.signature2(maps, "InYQcoRSjNqszqMOMEsAdJDHbewbCAMJ", true, false, "sign");
		maps.put("sign", sign);
		return UrlKit.createUrl(url, maps, "utf-8");
	}

}
