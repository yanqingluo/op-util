package com.taobao.top.common.cache;

/**
 * Top缓存的命名空间
 * 
 * @version 2009-3-2
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public enum CacheNamespace {

	/**
	 * 会话
	 */
	SESSION(100),
	
	/**
	 * 频率黑名单
	 * 
	 */
	FREQ_BLACK_LIST(200),
	
	/**
	 * app黑名单
	 */
	APP_BLACK_LIST(201),
	
	
	/**
	 * 新流控黑名单，对app, app+api, api不做区分 
	 */
	NEW_FLOW_BLACK_LIST(202),
	
	/**
	 * 新的计数器
	 * 
	 */
	COUNTER(49),
	
	/**
	 * 授权码
	 */
	AUTH_CODE(50),

	/**
	 * 频率计数器
	 */
	FREQ_TIMES_COUNTER(51),
	
	/**
	 * app每api调用成功计数器
	 */
	APP_SUCCESS_TIMES_COUNTER(52),	
	
	/**
	 * 插件
	 */
	APP(53),

	/**
	 * 插件角色
	 */

	ROLE(54),

	/**
	 * API
	 */
	API(55),
	
	/**
	 * 新流控计数器
	 */
	NEW_FLOW_COUNTER(56),


	/**
	 * APP_API_LIMIT
	 */
	FLOW_RULE(60),
	//TIM的命令空间
	/**
	 * APP流量规则
	 */	
	TADGET_FLOW_RULE(62);
	/**
	 * cache 命名空间
	 */
	private int value;

	/**
	 * @return the namespace
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	private CacheNamespace(int value) {
		this.value = value;
	}

}