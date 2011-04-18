package com.taobao.top.util;


import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class ConcurrentInitializerTest {

	private final ConcurrentInitializer<Integer, Integer, RuntimeException> initializer = 
		new ConcurrentInitializer<Integer, Integer, RuntimeException>(
			new ResourceHolder<Integer, Integer, RuntimeException>() {
				private ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<Integer, Integer>();
				private ConcurrentHashMap<Integer, Lock> lockMap = new ConcurrentHashMap<Integer, Lock>();
				@Override
				public Integer currentData(Integer key) {
					return map.get(key);
				}

				@Override
				public Integer initializeDate(Integer key)
						throws RuntimeException {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					map.put(key, key);
					return key;
				}

				
			});

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	@Ignore
	public void testGetMul() throws InterruptedException, ExecutionException {
		for(int i = 0; i < 100; i++) {
//			testGet(200 + i);
		}
	}
	
	@Test
	public void testGet() throws InterruptedException, ExecutionException {
		Integer threadCount = 1000;
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch end = new CountDownLatch(threadCount);
		ExecutorService service = Executors.newFixedThreadPool(threadCount);
		Set<Future<Boolean>> results = new HashSet<Future<Boolean>>();
		for(Integer i = 0; i < threadCount; i++) {
			results.add(service.submit(new GetRunnable(start, end, initializer, i)));
		}
		start.countDown();
		end.await();
		for(Future<Boolean> result : results) {
			if(result.isCancelled() || !result.get()) {
				Assert.fail();
			}
		}
	}
	
	private static class GetRunnable implements Callable<Boolean> {
		CountDownLatch start;
		CountDownLatch end;
		ConcurrentInitializer<Integer, Integer, RuntimeException> initializer;
		int threadNumber;
		
		public GetRunnable(CountDownLatch start, CountDownLatch end, 
				ConcurrentInitializer<Integer, Integer, RuntimeException> initializer, 
				int threadNumber) {
			this.start = start;
			this.end = end;
			this.initializer = initializer;
			this.threadNumber = threadNumber;
		}

		public Boolean call() throws Exception {
			try {
				start.await();
				Integer result = initializer.getData(new Integer(threadNumber%100));
				if(result == null) {
					throw new RuntimeException();
				}
			} finally {
				end.countDown();
			}
			return true;
		}
	}

}
