package com.taobao.console;


import com.taobao.top.console.client.handler.ConsoleClientPipeInput;
import com.taobao.top.console.client.handler.ConsoleClientPipeResult;
import com.taobao.top.console.client.service.BaseConsoleClientPipe;
import com.taobao.top.console.client.util.ConsoleClientErrorCode;
/**
 * 用来操作缓存
 * @author zhenzi
 * 
 */
public class CacheOperationPipe extends BaseConsoleClientPipe{
	private ICacheOpt sessionCache;
	private ICacheOpt blackListCache;
	private ICacheOpt apiCache;
	private ICacheOpt otherCache;
	
	public void setSessionCache(ICacheOpt sessionCache) {
		this.sessionCache = sessionCache;
	}

	public void setBlackListCache(ICacheOpt blackListCache) {
		this.blackListCache = blackListCache;
	}

	public void setApiCache(ICacheOpt apiCache) {
		this.apiCache = apiCache;
	}

	public void setOtherCache(ICacheOpt otherCache) {
		this.otherCache = otherCache;
	}

	@Override
	public void doPipe(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		String type = input.getString(ConsoleConstants.CACHE_TYPE);
		if(type.equalsIgnoreCase(ConsoleConstants.CACHE_TYPE_SESSION)){//session类型
			sessionCache.cacheOpt(input, result);
		}else if(type.equalsIgnoreCase(ConsoleConstants.CACHE_TYPE_BLACK_LIST)){//黑名单
			blackListCache.cacheOpt(input, result);
		}else if(type.equalsIgnoreCase(ConsoleConstants.CACHE_TYPE_API)){//api缓存
			apiCache.cacheOpt(input, result);
		}else if(type.equalsIgnoreCase(ConsoleConstants.CACHE_TYPE_OTHER)){
			otherCache.cacheOpt(input, result);
		}else{
			result.setErrorCode(ConsoleClientErrorCode.INVALID_ARGUMENTS);
			result.setErr_msg("type is only allowed session,blacklist,api,all");
		}
	}
	
	@Override
	public boolean ignoreIt(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		String direct = input.getRequest().getParameter("direct");
		if ("CacheCenter".equalsIgnoreCase(direct)) {
			if (result.isSuccess())
				return false;
		}
		return true;
	}
}
