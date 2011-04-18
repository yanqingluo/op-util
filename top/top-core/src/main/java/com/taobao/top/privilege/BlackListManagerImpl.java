package com.taobao.top.privilege;


import org.apache.commons.logging.Log;

import com.alibaba.common.logging.LoggerFactory;
import com.taobao.top.common.cache.CacheClient;
import com.taobao.top.common.cache.CacheNamespace;

/**
 * 名单操作实现类
 * 
 * @author huaisu
 */
public class BlackListManagerImpl implements BlackListManager {
	private static final Log logger = LoggerFactory
			.getLog(BlackListManagerImpl.class);
	private CacheClient cacheClient = null;

	public void setCacheClient(CacheClient cacheClient) {
		this.cacheClient = cacheClient;
	}
	
	/* 
	 * 该实现需要将value作为过期时间点加入tair缓存（这样可以在各台tip app
	 * 中共享该过期时间信息，否则其它app即使发现该key在黑名单中，却不知道
	 * 应该在本地关多长时间。该改进针对原流量控制）
	 * @see com.taobao.top.common.cache.aop.CacheClientInterceptor.CacheClientIntercept
	 */
	@Override
	public void addNewFlowBlack(String key, int banDuration) {
		try {
			cacheClient.putWithoutCheck(
					CacheNamespace.NEW_FLOW_BLACK_LIST.getValue(), 
					key, 
					banDuration + (int) (System.currentTimeMillis() / 1000), 
					banDuration);
		} catch (Exception e) {
			logger.warn("add new flow control into black error id=" + key);
		}
		
	}
	
	@Override
	public Integer getBanEndTimeOfNewFlow(String keyId) {
		try {
			Integer expiredTime = (Integer) cacheClient.get(CacheNamespace.NEW_FLOW_BLACK_LIST.getValue(), keyId);
			return expiredTime;
		} catch (Exception e) {
			logger.warn("cacheClient.get("
					+ CacheNamespace.NEW_FLOW_BLACK_LIST.getValue() + "," + keyId
					+ ") error");
			return null;
		}
	}
	
}
