package com.taobao.top.privilege;


/**
 * 黑名单操作接口
 * 
 * @author linxuan
 */
public interface BlackListManager {		
	/**
	 * 将新访问控制规则控制的key加入到黑名单，并指定过期时间
	 * @param key
	 * @param banDuration 过期时间长度，秒为单位
	 */
	void addNewFlowBlack(String key, int banDuration);
	
	
	/**
	 * 获取新流控的封禁时间末尾，以反馈用户
	 * @param keyId
	 * @return the value in tair, the value is the ban endTime. null if not exists 
	 * @see #addNewFlowBlack(String, int)
	 */
	public Integer getBanEndTimeOfNewFlow(String keyId) ;
}
