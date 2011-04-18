package com.taobao.top.common.server;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * 
 * @author zhenzi
 *String url = perform("img.daily.taobaocdn.net/","T1KXdXXi5yt0L1upjX.jpg");
		String url1 = perform("http://img.daily.taobaocdn.net","T1KXdXXi5yt0L1upjX.jpg");
		String url2 = perform("http://img.daily.taobaocdn.net/","T1KXdXXi5yt0L1upjX.jpg");
		String url3 = perform("","T1KXdXXi5yt0L1upjX.jpg");
 */
public class RefundMessageUrlKitTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testPerform(){
		assertNotNull(RefundMessageUrlKit.perform("img.daily.taobaocdn.net/","T1KXdXXi5yt0L1upjX.jpg"));
		assertNull(RefundMessageUrlKit.perform("prefixUrl", null));
		assertNull(RefundMessageUrlKit.perform("prefixUrl", "im"));
	}
}
