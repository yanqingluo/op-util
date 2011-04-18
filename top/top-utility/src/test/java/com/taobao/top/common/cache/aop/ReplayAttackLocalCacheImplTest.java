package com.taobao.top.common.cache.aop;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class ReplayAttackLocalCacheImplTest {
	
	private ReplayAttackLocalCacheImpl replayAttackLocalCacheImpl;
	
	@Before
	public void setUp(){
		replayAttackLocalCacheImpl = new ReplayAttackLocalCacheImpl(20000, 20*1000L);
	}

	
	@Test
	public void testCacheEfficiency(){
		CountDownLatch latch = new CountDownLatch(20);
		AtomicBoolean error = new AtomicBoolean(false);
		for (int i = 0; i < 20; i++) {
			InnerTestWork thread = new InnerTestWork("thread_"+i+" ");
			thread.setLatch(latch);
			thread.setError(error);
			thread.start();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
		}
		Assert.assertEquals(false, error.get());
		
	}
	
	public class InnerTestWork extends Thread{
		private InnerTestWork(String name) {
			super(name);
		}

		private CountDownLatch latch;
		private AtomicBoolean error ;

		public CountDownLatch getLatch() {
			return latch;
		}

		public void setLatch(CountDownLatch latch) {
			this.latch = latch;
		}

		public AtomicBoolean getError() {
			return error;
		}

		public void setError(AtomicBoolean error) {
			this.error = error;
		}

		@Override
		public void run() {
			for (int i = 0; i < 1000; i++) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
				Long value = System.currentTimeMillis();
				String key = getName()+i;
				replayAttackLocalCacheImpl.put(key, value);
				Long myValue = replayAttackLocalCacheImpl.get(key);
				if(myValue.longValue()!=value.longValue()){
					error.set(true);
				}
			}
			latch.countDown();
		}
		
	}
	


}
