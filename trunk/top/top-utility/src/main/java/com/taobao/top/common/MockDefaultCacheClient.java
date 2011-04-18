package com.taobao.top.common;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import com.taobao.top.common.cache.OperationTimeoutException;
import com.taobao.top.common.cache.tair.DefaultClient;

/**
 * mock DefaultClient
 * 
 * @author zhenzi
 * 
 */
public class MockDefaultCacheClient extends DefaultClient {
	public void delete(int namespace, Object key) throws ExecutionException,
			OperationTimeoutException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.common.cache.CacheClient#deleteWithoutCheck(int,
	 * java.lang.Object)
	 */
	public void deleteWithoutCheck(int namespace, Object key) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.common.cache.CacheClient#get(int, java.lang.Object)
	 */
	public Object get(int namespace, Object key) throws ExecutionException,
			OperationTimeoutException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.common.cache.CacheClient#put(int, java.lang.Object,
	 * java.io.Serializable, int)
	 */
	public void put(int namespace, Object key, Serializable value,
			int expireTime) throws OperationTimeoutException,
			ExecutionException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.common.cache.CacheClient#put(int, java.lang.Object,
	 * java.io.Serializable)
	 */
	public void put(int namespace, Object key, Serializable value)
			throws OperationTimeoutException, ExecutionException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.common.cache.CacheClient#putWithoutCheck(int,
	 * java.lang.Object, java.io.Serializable, int)
	 */
	public void putWithoutCheck(int namespace, Object key, Serializable value,
			int expireTime) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.common.cache.CacheClient#putWithoutCheck(int,
	 * java.lang.Object, java.io.Serializable)
	 */
	public void putWithoutCheck(int namespace, Object key, Serializable value) {
	}

	public int increment(int namespace, String key)
			throws OperationTimeoutException, ExecutionException {
		return 0;
	}

	public int increment(int namespace, String key, int value)
			throws OperationTimeoutException, ExecutionException {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.common.cache.CacheClient#getIncrement(int,
	 * java.lang.String)
	 */
	public int getIncrement(int namespace, String key)
			throws OperationTimeoutException, ExecutionException {
		return 0;
	}
}
