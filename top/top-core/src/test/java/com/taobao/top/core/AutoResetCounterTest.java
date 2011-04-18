package com.taobao.top.core;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class AutoResetCounterTest {
	/**
	 * 测试周期性自动重置计数器
	 */
	@Ignore
	public void testCounter(){
		//周期为1秒
		long periods = 500L;
		AutoResetCounter counter = new AutoResetCounter(periods);
		//计数+10
		for (int i = 0; i < 10; i++) {
			counter.incrementAndGet();
		}
		//休眠一个周期
		try {
			Thread.sleep(periods+1L);
		} catch (InterruptedException e) {
		
		}
		//查看是否重置
		int size = counter.incrementAndGet();
		Assert.assertEquals(1, size);
	}

}
