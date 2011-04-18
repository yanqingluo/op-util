package com.taobao.top.notify.util;

import org.junit.Test;

public class ThreadPoolPropertyTest {
	
	@Test
	public void testGetProperty(){
		System.out.println(ThreadPoolProperty.getIntProperty(ThreadPoolProperty.EXECUTOR_POOL_CORE_SIZE));
		System.out.println(ThreadPoolProperty.getIntProperty(ThreadPoolProperty.EXECUTOR_POOL_MAX_SIZE));
		System.out.println(ThreadPoolProperty.getIntProperty(ThreadPoolProperty.EXECUTOR_POOL_ALIVE_TIME));
		System.out.println(ThreadPoolProperty.getIntProperty(ThreadPoolProperty.EXECUTOR_POOL_QUEUE_SIZE));
		
	}

}
