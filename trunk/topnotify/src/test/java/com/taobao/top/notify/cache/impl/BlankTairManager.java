package com.taobao.top.notify.cache.impl;

import java.io.Serializable;
import java.util.List;

import com.taobao.common.tair.DataEntry;
import com.taobao.common.tair.Result;
import com.taobao.common.tair.ResultCode;
import com.taobao.common.tair.TairManager;

public class BlankTairManager implements TairManager {

	public Result<Integer> decr(int namespace, Object key, int value, int defaultValue) {
		return null;
	}

	public ResultCode delete(int namespace, Object key) {
		return null;
	}

	public Result<DataEntry> get(int namespace, Object key) {
		return null;
	}

	public String getVersion() {
		return null;
	}

	public Result<Integer> incr(int namespace, Object key, int value, int defaultValue) {
		return null;
	}

	public ResultCode invalid(int namespace, Object key) {
		return null;
	}

	public ResultCode mdelete(int namespace, List<Object> keys) {
		return null;
	}

	public Result<List<DataEntry>> mget(int namespace, List<Object> keys) {
		return null;
	}

	public ResultCode minvalid(int namespace, List<? extends Object> keys) {
		return null;
	}

	public ResultCode put(int namespace, Object key, Serializable value) {
		return null;
	}

	public ResultCode put(int namespace, Object key, Serializable value, int version) {
		return null;
	}

	public ResultCode put(int namespace, Object key, Serializable value, int version, int expireTime) {
		return null;
	}

}
