package com.taobao.top.common;

import java.util.ArrayList;
import java.util.List;

/**
 * pipe使用到的配置
 * @author zhenzi
 *
 */
public final class TopPipeConfig {	
	/**
	 * 是否使用lazyparser在普通的请求上
	 */
	private boolean useLazyParser = false;
		
	/*
	 * 在请求TIM的时候是否启用本地缓存，默认需要启用
	 */
	private boolean userCache = true;
	
		
	private List<String> illegalKeys = new ArrayList<String>();
	/**
	 * 是否启用TIM富客户端的缓存，默认启用
	 */
	private boolean userRichClientCache = true;
	private static final TopPipeConfig pipeConfig = new TopPipeConfig();
	
	//是否检查域名在白名单中，page api
	private boolean checkDomain = true;
	/**
	 * 在记录日志的时候，可以动态指定不同的分隔符，可以测试那些分隔符是比较理想的
	 */
	private String logSplitStr = null;
	private boolean filterMHTML = true;
	/**
	 * 是否使用权重线程池
	 */
	private boolean useWeightThreadPool = false;	

	public boolean isUseWeightThreadPool() {
		return useWeightThreadPool;
	}

	public void setUseWeightThreadPool(boolean useWeightThreadPool) {
		this.useWeightThreadPool = useWeightThreadPool;
	}

	public boolean isUseLazyParser() {
		return useLazyParser;
	}

	public void setUseLazyParser(boolean useLazyParser) {
		this.useLazyParser = useLazyParser;
	}

	public List<String> getIllegalKeys() {
		return illegalKeys;
	}

	public void setIllegalKeys(List<String> illegalKeys) {
		this.illegalKeys = illegalKeys;
	}

	private TopPipeConfig(){
	}

	public boolean isUserCache() {
		return userCache;
	}

	public void setUserCache(boolean userCache) {
		this.userCache = userCache;
	}
		
	public static TopPipeConfig getInstance(){
		return pipeConfig;
	}

    public void setCheckDomain(boolean checkDomain) {
        this.checkDomain = checkDomain;
    }

    public boolean isCheckDomain() {
        return checkDomain;
    }

	public boolean isUserRichClientCache() {
		return userRichClientCache;
	}

	public void setUserRichClientCache(boolean userRichClientCache) {
		this.userRichClientCache = userRichClientCache;
	}

	public String getLogSplitStr() {
		return logSplitStr;
	}

	public void setLogSplitStr(String logSplitStr) {
		this.logSplitStr = logSplitStr;
	}

	public boolean isFilterMHTML() {
		return filterMHTML;
	}

	public void setFilterMHTML(boolean filterMHTML) {
		this.filterMHTML = filterMHTML;
	}
	
}
