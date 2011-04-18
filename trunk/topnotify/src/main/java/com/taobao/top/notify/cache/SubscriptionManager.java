package com.taobao.top.notify.cache;

import java.util.List;

import com.taobao.top.notify.domain.NotifyInfo;
import com.taobao.top.tim.service.TIMServiceException;

/**
 * 订阅关系管理。
 * 
 * @author fengsheng
 * @since 1.0, Dec 14, 2009
 */
public interface SubscriptionManager {

	/**
	 * 根据消息的买家/卖家，消息类型，交易类型，状态查询订阅了此消息的应用
	 * 
	 * @param userId 卖家/卖家用户ID
	 * @param category 消息的主题(交易，商品等等)
	 * @param type 交易的类型（100，200等）
	 * @param status 消息对应的操作状态
	 * @param userRole 用户角色（0:所有角色; 1:卖家；2：买家）
	 * @return appKey的列表
	 */
	public List<NotifyInfo> getSubscription(Long userId, Integer category, Integer type, Integer status, Long userRole) throws TIMServiceException;

}
