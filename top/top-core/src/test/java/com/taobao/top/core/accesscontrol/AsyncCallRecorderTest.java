package com.taobao.top.core.accesscontrol;


import java.util.Collections;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AsyncCallRecorderTest {
	AsyncCallRecorder callRecorder;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRecord() throws InterruptedException {
		int count = 3;
		
		MockCallRecorder mockRecorder = new MockCallRecorder();
		callRecorder = new AsyncCallRecorder(mockRecorder, 1, 3);
		for(int i = 0; i < count; i++) {
			callRecorder.recordAndCanCall(Collections.<FlowRule>emptySet());			
		}
		Thread.sleep(100 * count + 200);
		Assert.assertEquals(count, mockRecorder.getSent());
	}
}
