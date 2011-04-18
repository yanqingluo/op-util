package com.taobao.top.common.test;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.taobao.top.common.TOPConfig;

public class TOPConfigTest {
	private TOPConfig topConfig = TOPConfig.getInstance();
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testTopConfig(){
		assertEquals(1,topConfig.getFlushInterval());
		assertEquals(2,topConfig.getCreateBundleServiceThreadCount());
		assertEquals(3,topConfig.getBundleMaxCount());
		assertEquals(4,topConfig.getWriterMaxCount());
	}
}
