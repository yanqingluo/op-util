package com.taobao.console;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.taobao.top.common.cache.CacheClient;
import com.taobao.top.common.cache.ICache;
import com.taobao.top.common.cache.aop.CacheClientInterceptor;
import com.taobao.top.console.client.handler.ConsoleClientPipeInput;
import com.taobao.top.console.client.handler.ConsoleClientPipeResult;

/**
 * http://192.168.207.127/top/services/inner?direct=CacheCenter&cmd=delete&type=blacklist 删除本地黑名单缓存
 *                                                                         type=blacklist&ns=xx&key=xx 删除远端的缓存
 * 
 * 黑名单缓存实现接口
 * @author zhenzi
 * 2010-12-24 下午12:17:53
 */
public class BlackListCacheOpt extends AbstractCacheOptImpl{
	private List<CacheClient> cacheClients;
	
	public void setCacheClients(List<CacheClient> cacheClients) {
		this.cacheClients = cacheClients;
	}

	@Override
	public void deleteCache(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		String ns = input.getString("ns");
		String key = input.getString("key");
		if(StringUtils.isBlank(ns) && StringUtils.isBlank(key)){
			ICache<String, Object> blackListLocalCache = CacheClientInterceptor.getCache();
			if(blackListLocalCache == null || (blackListLocalCache != null && blackListLocalCache.clear())){
				result.setBlack(ConsoleConstants.OPT_TRUE);
			}else{
				result.setErr_msg("delete local blaclist error");
			}
		}else{
			try{
				for (CacheClient cacheClient : cacheClients) {
					cacheClient.delete(Integer.valueOf(ns), key);
				}
				result.setBlack(ConsoleConstants.OPT_TRUE);
			}catch(Exception e){
				logger.error(e, e);
				result.setErr_msg("exception:" + e.getMessage());
			}
		}
	}

	@Override
	public void getCache(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		//do nothing;
	}

	@Override
	public void updateCache(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		//do nothing;
	}
}
