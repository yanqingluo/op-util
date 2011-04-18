package com.taobao.top.common.server;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ImageUrlKitTest {

	@Before
	public void setUp() throws Exception {
	}
	@Test
    public void imgUrl(){
    	String lackUrl="http://*.taobaocdn.com/bao/uploaded/";
    	String shortUrl="i8/1231313/THFJDSHFJKS.gif";
    	String url=ImageUrlKit.perform(shortUrl, lackUrl);
    	System.out.println(url);
    }
	@Test
	public void test_getDefailUrl(){
		String s = ImageUrlKit.getDetailUrl("itemId", "xid", "lackUrl");
		assertTrue(s.indexOf("&x_id=0") != -1);
	}
	@Test
	public void test_getDomainFromUrl(){
		String s = ImageUrlKit.getDomainFromUrl("url");
		assertNull(s);
		s = ImageUrlKit.getDomainFromUrl("http://*.baidu.com/");
		assertEquals("baidu.com",s);
	}
	@Test
	public void test_isNormalTfsPicture(){
		assertFalse(ImageUrlKit.isNormalTfsPicture("logo.png"));
		assertTrue(ImageUrlKit.isNormalTfsPicture("i5/T1kQdpXjpdXXXXXXXX-120-120.gif"));
	}
	@Test
	public void test_perform(){
		String s = ImageUrlKit.perform(null, "lackUrl");
		assertNull(s);
		s = ImageUrlKit.perform("aa", "lackUrl");
		assertNull(s);
		s = ImageUrlKit.perform("T1kQdpXjpdXXXXXXXX-120-120.gif", "http://www.taobao.com/");
		assertEquals("http://www.taobao.com/T1kQdpXjpdXXXXXXXX-120-120.gif",s);
	}
}
