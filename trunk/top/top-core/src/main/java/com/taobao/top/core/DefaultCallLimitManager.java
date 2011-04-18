/**
 * 
 */
package com.taobao.top.core;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.common.lang.StringUtil;
import com.taobao.top.common.cache.CacheClient;
import com.taobao.top.common.cache.CacheNamespace;

/**
 * @author alin
 * 
 */
public class DefaultCallLimitManager implements CallLimitManager {
	private static final Log log = LogFactory
	.getLog(DefaultCallLimitManager.class);
	private CacheClient cacheClient;

	public void setCacheClient(CacheClient cacheClient) {
		this.cacheClient = cacheClient;
	}
	
	@Override
	public int incrNewFlowTimes(String key) throws Exception {
		if (StringUtil.isEmpty(key)) {
			return 0;
		}
		return cacheClient.increment(
				CacheNamespace.NEW_FLOW_COUNTER.getValue(), key);
	}

}
