package com.taobao.top.notify.receive;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.hsf.notify.extend.SubscriptionHelper;
import com.taobao.item.constant.ItemNotifyConstants;
import com.taobao.top.notify.domain.SubscriberNode;

/**
 * 消息订阅者。
 * @author moling
 * @since 1.0, 2009-12-14
 */
public class NotifySubscriber {

	private static final Log log = LogFactory.getLog(NotifySubscriber.class);

	// 订阅的主题列表
	public static final String TRADE_TOPIC = "TRADE-SUB";	//TRADE 的镜像消息
	public static final String TRADE_PLUS_TOPIC = "TRADE-PLUS";
	public static final String ITEM_TOPIC = ItemNotifyConstants.topic;
	public static final String VAS_TOPIC = "UPP-SERVICE";	//汇金的消息

	private SubscriptionHelper subscribeHelper;

	// 交易类型列表100，200……
	private List<Integer> bizTypes;

	// 订阅消息列表
	private Map<String, SubscriberNode> tradeMessages;
	private Map<String, SubscriberNode> refundMessages;
	private Map<String, SubscriberNode> tradePlusMessages;
	private Map<String, SubscriberNode> itemMessages;
	private Map<String, SubscriberNode> vasMessages;

	public void setSubscribeHelper(SubscriptionHelper subscribeHelper) {
		this.subscribeHelper = subscribeHelper;
	}

	public List<Integer> getBizTypes() {
		return this.bizTypes;
	}

	public void setBizTypes(List<Integer> bizTypes) {
		this.bizTypes = bizTypes;
	}

	public Map<String, SubscriberNode> getTradeMessages() {
		return this.tradeMessages;
	}

	public void setTradeMessages(Map<String, SubscriberNode> tradeMessages) {
		this.tradeMessages = tradeMessages;
	}

	public Map<String, SubscriberNode> getRefundMessages() {
		return this.refundMessages;
	}

	public void setRefundMessages(Map<String, SubscriberNode> refundMessages) {
		this.refundMessages = refundMessages;
	}

	public Map<String, SubscriberNode> getTradePlusMessages() {
		return this.tradePlusMessages;
	}

	public void setTradePlusMessages(Map<String, SubscriberNode> tradePlusMessages) {
		this.tradePlusMessages = tradePlusMessages;
	}

	public Map<String, SubscriberNode> getItemMessages() {
		return this.itemMessages;
	}

	public void setItemMessages(Map<String, SubscriberNode> itemMessages) {
		this.itemMessages = itemMessages;
	}
	
	public Map<String, SubscriberNode> getVasMessages() {
		return this.vasMessages;
	}

	public void setVasMessages(Map<String, SubscriberNode> vasMessages) {
		this.vasMessages = vasMessages;
	}

	public void init() throws Exception {
		log.warn("正在初始化Notify订阅者...");
		// 这填充订阅相关信息
		fillSubscribeType();
		subscribeHelper.init();
	}

	/**
	 * 填充订阅消息列表
	 */
	public void fillSubscribeType() {
		// 订阅 交易类型消息
		for (Entry<String, SubscriberNode> type : tradeMessages.entrySet()) {
			for (Integer bizType : bizTypes) {
				if (type.getValue().isBizTypeAble()) {
					subscribeHelper.addSubscription(TRADE_TOPIC, bizType + type.getKey(), true, -1);
					log.warn("订阅交易消息: " + bizType + type.getKey());
				} else {
					subscribeHelper.addSubscription(TRADE_TOPIC, type.getKey(), true, -1);
					log.warn("订阅交易消息: " + type.getKey());
				}
			}
		}

		// 订阅退款类型消息
		for (Entry<String, SubscriberNode> type : refundMessages.entrySet()) {
			subscribeHelper.addSubscription(TRADE_TOPIC, type.getKey(), true, -1);
			log.warn("订阅退款消息: " + type.getKey());
		}

		// 订阅交易扩展类型消息
		for (Entry<String, SubscriberNode> type : tradePlusMessages.entrySet()) {
			for (Integer bizType : bizTypes) {
				subscribeHelper.addSubscription(TRADE_PLUS_TOPIC, bizType + type.getKey(), true, -1);
				log.warn("订阅交易扩展消息: " + bizType + type.getKey());
			}
		}

		// 订阅商品类型消息
		for (Entry<String, SubscriberNode> type : itemMessages.entrySet()) {
			subscribeHelper.addSubscription(ITEM_TOPIC, type.getKey(), true, -1);
			log.warn("订阅商品消息: " + type.getKey());
		}
		
		// 订阅收费类型消息
		for (Entry<String, SubscriberNode> type : vasMessages.entrySet()) {
			subscribeHelper.addSubscription(VAS_TOPIC, type.getKey(), true, -1);
			log.warn("订阅收费消息: " + type.getKey());
		}
	}

}
