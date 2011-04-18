package com.taobao.top.common;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import com.taobao.top.common.cache.ICache;
import com.taobao.top.common.cache.OperationTimeoutException;
import com.taobao.top.common.cache.aop.LocalCacheImpl;
import com.taobao.top.common.cache.tair.DefaultDuplicateClient;

/**
 * mock DefualtDuplicateClient
 * 
 * @author zhenzi
 * 
 */
public class MockDuplicateCacheClient extends DefaultDuplicateClient {
	private ICache<String,Object> cache = new LocalCacheImpl();
	private ConcurrentHashMap<String, AtomicInteger> count = new ConcurrentHashMap<String,AtomicInteger>();
	private Object o = new Object();
	
	public MockDuplicateCacheClient() {

	}

	@Override
	public void put(final int namespace, final Object key,
			final Serializable value, int expireTime)
			throws OperationTimeoutException, ExecutionException {
		cache.put((String)key, value, expireTime);
	}

	@Override
	public void put(int namespace, Object key, Serializable value)
			throws OperationTimeoutException, ExecutionException {
		cache.put((String)key, value);
	}

	@Override
	public void putWithoutCheck(int namespace, Object key, Serializable value,
			int expireTime) throws InterruptedException, ExecutionException,
			OperationTimeoutException {
		cache.put((String)key, value, expireTime);
	}

	@Override
	public void putWithoutCheck(int namespace, Object key, Serializable value)
			throws InterruptedException, ExecutionException,
			OperationTimeoutException {
		cache.put((String)key, value);
	}

	@Override
	public void deleteWithoutCheck(int namespace, Object key)
			throws InterruptedException, ExecutionException,
			OperationTimeoutException {
		cache.remove((String)key);
	}

	@Override
	public Object get(int namespace, Object key) throws ExecutionException,
			OperationTimeoutException {
		return cache.get((String)key);
	}

	@Override
	public int increment(int namespace, String key)
			throws OperationTimeoutException, ExecutionException,
			InterruptedException {
		return inc(key,1);
	}

	@Override
	public int increment(int namespace, String key, int value)
			throws OperationTimeoutException, ExecutionException,
			InterruptedException {
		return inc(key,value);
	}

	@Override
	public int getIncrement(int namespace, String key)
			throws OperationTimeoutException, ExecutionException,
			InterruptedException {
		return inc(key,0);
	}
	private int inc(String key,int value){
		int t = 0;
		try{
			t = count.get(key).addAndGet(value);
		}catch(Exception e){
			synchronized (o) {
				if(count.get(key) == null){
					count.put(key, new AtomicInteger(value));
					t = value;
				}else{
					t = count.get(key).addAndGet(value);
				}
			}
		}
		return t;
	}
	@Override
	public void delete(int namespace, Object key) throws ExecutionException,
			OperationTimeoutException {
		cache.remove((String)key);
	}
}
