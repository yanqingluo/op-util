package com.taobao.top.common.cache.aop;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

public class ReplayAttackInnerCacheTest {
	
	
	
	private ReplayAttackLocalCacheImpl.InnerCache<String, Long> replayAttackLocalCacheImpl;
	private int ThreadCore = 100;
	private AtomicLong  cacheCost = new AtomicLong(); 
	private ReplayAttackLocalCacheImpl impl = new ReplayAttackLocalCacheImpl();
	
	
	@Test
	public void testCache(){
		
		for (int size = 1; size < 30; size++) {
			replayAttackLocalCacheImpl = impl.new InnerCache<String,Long>(size);
			
			CountDownLatch latch = new CountDownLatch(ThreadCore);
			for (int i = 0; i < ThreadCore; i++) {
				InnerTestWork thread1 = new InnerTestWork("thread_job1_"+i+" ");
				thread1.setLatch(latch);
				thread1.start();
			}
			try {
				latch.await();
				System.out.println(cacheCost.get()/ThreadCore);
			} catch (InterruptedException e) {
			}
			cacheCost.set(0);
		}
	}

	
	public class InnerTestWork extends Thread{
		private InnerTestWork(String name) {
			super(name);
		}
		private CountDownLatch latch;
		public CountDownLatch getLatch() {
			return latch;
		}
		public void setLatch(CountDownLatch latch) {
			this.latch = latch;
		}
		@Override
		public void run() {
			long start = System.currentTimeMillis();
			for (int i = 0; i < 1000; i++) {
				Long value = System.currentTimeMillis();
				String key = getName()+i;
				replayAttackLocalCacheImpl.put(key, value);
				Long myValue = replayAttackLocalCacheImpl.get(key);
			}
			long costTime = System.currentTimeMillis()-start;
//			System.out.println(getName()+" cache:"+costTime);
			cacheCost.addAndGet(costTime);
			latch.countDown();
		}
	}
	
}
