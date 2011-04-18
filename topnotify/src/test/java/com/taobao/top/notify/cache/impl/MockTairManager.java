package com.taobao.top.notify.cache.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.common.tair.DataEntry;
import com.taobao.common.tair.Result;
import com.taobao.common.tair.ResultCode;
import com.taobao.common.tair.TairManager;

/**
 * 
 * @author moling
 * @since 1.0, 2010-1-7
 */
public class MockTairManager implements TairManager {

	Map<Object, Serializable> cacheMap = new HashMap<Object, Serializable>();

	public Result<Integer> decr(int namespace, Object key, int value, int defaultValue) {
		return null;
	}

	public ResultCode delete(int namespace, Object key) {
		if (cacheMap.containsKey(key)) {
			cacheMap.remove(key);
			return ResultCode.SUCCESS;
		}
		return ResultCode.DATANOTEXSITS;
	}

	public Result<DataEntry> get(int namespace, Object key) {
		if (cacheMap.containsKey(key)) {
			DataEntry data = new DataEntry(key, cacheMap.get(key));
			Result<DataEntry> result = new Result<DataEntry>(ResultCode.SUCCESS, data);
			return result;
		}
		return new Result<DataEntry>(ResultCode.DATANOTEXSITS);
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
		cacheMap.put(key, value);
		return ResultCode.SUCCESS;
	}

	public ResultCode put(int namespace, Object key, Serializable value, int version) {
		cacheMap.put(key, value);
		return ResultCode.SUCCESS;
	}

	public ResultCode put(int namespace, Object key, Serializable value, int version, int expireTime) {
		cacheMap.put(key, value);
		return ResultCode.SUCCESS;
	}

	public static ArrayList<String> getSellerAuthorize() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("appKey1");
		list.add("appKey2");
		return list;
	}

	public static ArrayList<String> getOtherAuthorize() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("appKey1");
		list.add("appKey3");
		return list;
	}

	public static ArrayList<String> getBuyerAuthorize() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("appKey3");
		return list;
	}

	public static String getAppKey1Subscribe() {
		return "1:1;2:0;3:0";
	}

	public static String getAppKey2Subscribe() {
		return "1:0;2:1;3:0";
	}

	public static String getAppKey3Subscribe() {
		return "1:0;2:0;3:1";
	}
}
