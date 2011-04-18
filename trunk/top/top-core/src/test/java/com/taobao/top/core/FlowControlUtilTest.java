package com.taobao.top.core;


import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FlowControlUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testCreateKey(){
		String k = FlowControlUtil.createKey("key");
		assertTrue(k.indexOf("key:") != -1);
		Date d = new Date(new Long(k.split(":")[1]));
		SimpleDateFormat sdf = new SimpleDateFormat("HH时 mm分 ss秒");
		String date = sdf.format(d);
		assertEquals("00时 00分 00秒", date);
	}
}
