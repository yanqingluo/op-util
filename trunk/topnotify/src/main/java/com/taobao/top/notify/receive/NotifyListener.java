package com.taobao.top.notify.receive;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import com.taobao.hsf.notify.client.MessageListener;
import com.taobao.hsf.notify.client.MessageStatus;
import com.taobao.hsf.notify.client.message.BytesMessage;
import com.taobao.hsf.notify.client.message.Message;
import com.taobao.monitor.alert.client.AlertAgent;
import com.taobao.tc.domain.dataobject.BizOrderDO;
import com.taobao.top.notify.cache.SubscriptionManager;
import com.taobao.top.notify.domain.Notify;
import com.taobao.top.notify.domain.NotifyInfo;
import com.taobao.top.notify.domain.SubscriberNode;
import com.taobao.top.notify.log.RunTimeNotifyLog;
import com.taobao.top.notify.log.RunTimeNotifyLogMonitor;
import com.taobao.top.notify.log.TopNotifyExceptionLog;
import com.taobao.top.notify.log.TopNotifyExceptionLogMonitor;
import com.taobao.top.notify.persist.NotifyWriter;
import com.taobao.top.notify.receive.builder.ItemNotifyBuilder;
import com.taobao.top.notify.receive.builder.RefundNotifyBuilder;
import com.taobao.top.notify.receive.builder.TradeNotifyBuilder;
import com.taobao.top.notify.receive.builder.TradePlusNotifyBuilder;
import com.taobao.top.notify.receive.builder.VasNotifyBuilder;
import com.taobao.top.notify.send.NotifySender;
import com.taobao.top.notify.util.NotifyBuilderUtils;
import com.taobao.top.notify.util.NotifyConstants;
import com.taobao.top.notify.util.NotifyFillUtils;
import com.taobao.top.tim.service.TIMServiceException;

/**
 * 消息监听器。
 * 
 * @author fengsheng
 * @since 1.0, Dec 14, 2009
 */
public class NotifyListener implements MessageListener {

	private static final Log log = LogFactory.getLog(NotifyListener.class);

	// 交易类型列表100，200……
	private List<Integer> bizTypes;

	// 订阅消息列表
	private Map<String, SubscriberNode> tradeMessages;
	private Map<String, SubscriberNode> refundMessages;
	private Map<String, SubscriberNode> tradePlusMessages;
	private Map<String, SubscriberNode> itemMessages;
	private Map<String, SubscriberNode> vasMessages;

	// 消息创建者
	private ItemNotifyBuilder itemNotifyBuilder;
	private RefundNotifyBuilder refundNotifyBuilder;
	private TradePlusNotifyBuilder tradePlusNotifyBuilder;
	private TradeNotifyBuilder tradeNotifyBuilder;
	private VasNotifyBuilder vasNotifyBuilder;

	// 订阅关系管理
	private SubscriptionManager subscriptionManager;

	// 数据存储
	private NotifyWriter notifyWriter;
	
	//发送消息至TopMQ
	private NotifySender sender;
	private boolean enableAsyncNotify = true;

	// 报警
	private AlertAgent alertAgent;
	
	private RunTimeNotifyLogMonitor runTimeNotifyLogMonitor;
	
	private TopNotifyExceptionLogMonitor topNotifyExceptionLogMonitor;

	public void setBizTypes(List<Integer> bizTypes) {
		this.bizTypes = bizTypes;
	}

	public void setTradeMessages(Map<String, SubscriberNode> tradeMessages) {
		this.tradeMessages = tradeMessages;
	}

	public void setRefundMessages(Map<String, SubscriberNode> refundMessages) {
		this.refundMessages = refundMessages;
	}

	public void setTradePlusMessages(Map<String, SubscriberNode> tradePlusMessages) {
		this.tradePlusMessages = tradePlusMessages;
	}

	public void setItemMessages(Map<String, SubscriberNode> itemMessages) {
		this.itemMessages = itemMessages;
	}
	
	public void setVasMessages(Map<String, SubscriberNode> vasMessages) {
		this.vasMessages = vasMessages;
	}

	public void setItemNotifyBuilder(ItemNotifyBuilder itemNotifyBuilder) {
		this.itemNotifyBuilder = itemNotifyBuilder;
	}

	public void setRefundNotifyBuilder(RefundNotifyBuilder refundNotifyBuilder) {
		this.refundNotifyBuilder = refundNotifyBuilder;
	}

	public void setTradePlusNotifyBuilder(TradePlusNotifyBuilder tradePlusNotifyBuilder) {
		this.tradePlusNotifyBuilder = tradePlusNotifyBuilder;
	}

	public void setTradeNotifyBuilder(TradeNotifyBuilder tradeNotifyBuilder) {
		this.tradeNotifyBuilder = tradeNotifyBuilder;
	}
	
	public void setVasNotifyBuilder(VasNotifyBuilder vasNotifyBuilder) {
		this.vasNotifyBuilder = vasNotifyBuilder;
	}

	public void setSubscriptionManager(SubscriptionManager subscriptionManager) {
		this.subscriptionManager = subscriptionManager;
	}

	public void setNotifyWriter(NotifyWriter notifyWriter) {
		this.notifyWriter = notifyWriter;
	}
	
	public void setSender(NotifySender sender) {
		this.sender = sender;
	}

	public boolean isEnableAsyncNotify() {
		return this.enableAsyncNotify;
	}

	public void setEnableAsyncNotify(boolean enableAsyncNotify) {
		this.enableAsyncNotify = enableAsyncNotify;
	}

	public void setAlertAgent(AlertAgent alertAgent) {
		this.alertAgent = alertAgent;
	}

	public void setRunTimeNotifyLogMonitor(RunTimeNotifyLogMonitor runTimeNotifyLogMonitor) {
		this.runTimeNotifyLogMonitor = runTimeNotifyLogMonitor;
	}

	public RunTimeNotifyLogMonitor getRunTimeNotifyLogMonitor() {
		return runTimeNotifyLogMonitor;
	}

	public void setTopNotifyExceptionLogMonitor(
			TopNotifyExceptionLogMonitor topNotifyExceptionLogMonitor) {
		this.topNotifyExceptionLogMonitor = topNotifyExceptionLogMonitor;
	}

	public TopNotifyExceptionLogMonitor getTopNotifyExceptionLogMonitor() {
		return topNotifyExceptionLogMonitor;
	}

	public void receiveMessage(Message message, MessageStatus status) {
//		long begin = System.currentTimeMillis();
		if (message == null) { // 空消息不做处理
			return;
		}

//		ReceiveLog receiveLog = new ReceiveLog();
//		receiveLog.setTopic(message.getTopic());
//		receiveLog.setMsgType(message.getMessageType());
		Long buildTime = 0L;
		Long storeTime = 0L;
		Long sendTime = 0L;
		try {
			List<Notify> notifys = null;
			Long buildBeginTime = System.currentTimeMillis();
			if (NotifySubscriber.TRADE_TOPIC.equals(message.getTopic())) {
				notifys = buildTradeNotifys(message);
			} else if (NotifySubscriber.TRADE_PLUS_TOPIC.equals(message.getTopic())) {
				notifys = buildTradePlusNotifys(message);
			} else if (NotifySubscriber.ITEM_TOPIC.equals(message.getTopic())) {
				notifys = buildItemNotifys(message);
			} else if (NotifySubscriber.VAS_TOPIC.equals(message.getTopic())) {
				notifys = buildVasNotifys(message);
			} else {
				// 非订阅消息主题不处理(但是这是不正常的情况)
			}
			
			buildTime = System.currentTimeMillis() - buildBeginTime;

			if (notifys != null && !notifys.isEmpty()) {
				Long storeBeginTime = System.currentTimeMillis();
				notifyWriter.write(notifys);
				storeTime = System.currentTimeMillis() - storeBeginTime;
				//为了所有的消息都记录，是否需要发送在发送模块中判断 moling 2010-12-08
//				if (this.enableAsyncNotify) {
				Long sendBeginTime = System.currentTimeMillis();
				sender.send(notifys, this.enableAsyncNotify);
				sendTime = System.currentTimeMillis() - sendBeginTime;
//				}
//				receiveLog.setPersisted(true);
			}
			runTimeNotifyLogMonitor.logRecieveMessage(populateRunTimeMessageLog(message, notifys, buildTime, storeTime, sendTime));
		} catch (Exception e) {
			status.setRollbackOnly();

			if (message instanceof BytesMessage) {
				BytesMessage bytesMessage = (BytesMessage) message;
				log.warn("原始消息内容：" + NotifyBuilderUtils.bytesMessage2String(bytesMessage));
			}
			if (e instanceof TIMServiceException) {
				log.error("获取AppKey发生异常：", e);
				topNotifyExceptionLogMonitor.logReadSubscribeException(populateTopNotifyExceptionLog(message, e));
			} else{
				log.error("数据存储发生异常：", e);
				topNotifyExceptionLogMonitor.logStoreMsgException(populateTopNotifyExceptionLog(message, e));
			}

			if (!(e instanceof DataAccessException)) {
				alertAgent.alert("NOTIFY_WRITE_ERROR");
			}
		}
	}
	
	private RunTimeNotifyLog populateRunTimeMessageLog(Message message, List<Notify> notifys, 
			Long buildTime, Long storeTime, Long sendTime) {
		RunTimeNotifyLog log = new RunTimeNotifyLog();
		log.setId(message.getMessageId());
		log.setType(message.getMessageType());
		log.setDivNum(notifys==null?0:notifys.size());
		log.setParseServiceTime(buildTime);
		log.setStoreServiceTime(storeTime);
		log.setPutMsgQueueTime(sendTime);
		return log;
	}
	
	private TopNotifyExceptionLog populateTopNotifyExceptionLog(Message message, Exception e) {
		TopNotifyExceptionLog log = new TopNotifyExceptionLog();
		log.setMsgId(message.getMessageId());
		log.setExceptionMsg(e.getMessage());
		
		return log;
	}

	public List<Notify> buildTradeNotifys(Message message) throws TIMServiceException {
		// 根据消息的msgType来找到对应关系,如果msgType为空，不做处理
		if (StringUtils.isBlank(message.getMessageType())) {
			return null;
		}

		// 找到第一个"-"出现的地方。如果不存在这个字符，表示这个消息类型不合法，不处理了
		int position = message.getMessageType().indexOf('-');
		if (-1 == position) {
			return null;
		}

		String prefix = message.getMessageType().substring(0, position);
		String key = null;
		Integer type = null;

		if (StringUtils.isNumeric(prefix) && bizTypes.contains(Integer.valueOf(prefix))) {
			// 如果前缀是数字,且前缀在订阅的前缀列表中，解析出前缀和对应关系的key
			type = Integer.valueOf(prefix);
			key = message.getMessageType().substring(position, message.getMessageType().length());
		} else {
			// 否则直接用msgType做为Key
			key = message.getMessageType();
		}

		// 根据得到的对应关系key找到build消息的路径，并且从消息中获得相应的信息
		List<Notify> notifys = null;
		if (tradeMessages.containsKey(key)) {
			notifys = tradeNotifyBuilder.build(message, type, tradeMessages.get(key).getStatus());
		} else if (refundMessages.containsKey(key)) {
			notifys = refundNotifyBuilder.build(message, type, refundMessages.get(key).getStatus());
		} else {
			return null;
		}

		// 查询订阅者，生成最终消息
		List<Notify> result = fillAppKey(notifys, message.getBornTime());

		return result;
	}

	public List<Notify> buildTradePlusNotifys(Message message) throws TIMServiceException {
		// 根据消息的msgType来找到对应关系,如果msgType为空，不做处理
		if (StringUtils.isBlank(message.getMessageType())) {
			return null;
		}

		// 找到第一个"-"出现的地方。如果不存在这个字符，表示这个消息类型不合法，不处理了
		int position = message.getMessageType().indexOf('-');
		if (-1 == position) {
			return null;
		}

		String prefix = message.getMessageType().substring(0, position);
		String key = null;
		Integer type = null;

		if (StringUtils.isNumeric(prefix) && bizTypes.contains(Integer.valueOf(prefix))) {
			// 如果前缀是数字,且前缀在订阅的前缀列表中，解析出前缀和对应关系的key
			type = Integer.valueOf(prefix);
			key = message.getMessageType().substring(position, message.getMessageType().length());
		} else {
			return null;
		}

		// 根据得到的对应关系key找到build消息的路径，并且从消息中获得相应的信息
		List<Notify> notifys = null;
		if (tradePlusMessages.containsKey(key)) {
			notifys = tradePlusNotifyBuilder.build(message, type, tradePlusMessages.get(key).getStatus());
		} else {
			return null;
		}

		// 查询订阅者，生成最终消息
		List<Notify> result = fillAppKey(notifys, message.getBornTime());

		return result;
	}

	public List<Notify> buildItemNotifys(Message message) throws TIMServiceException {
		// 根据消息的msgType来找到对应关系,如果msgType为空，不做处理
		if (StringUtils.isBlank(message.getMessageType())) {
			return null;
		}

		// 直接用msgType做为Key
		String key = message.getMessageType();

		// 根据得到的订阅关系key找到build消息的路径，并且从消息中获得相应的信息
		List<Notify> notifys = null;
		if (itemMessages.containsKey(key)) {
			notifys = itemNotifyBuilder.build(message, null, itemMessages.get(key).getStatus());
		} else {
			return null;
		}

		// 查询订阅者，生成最终消息
		List<Notify> result = fillAppKey(notifys, message.getBornTime());

		return result;
	}

	public List<Notify> buildVasNotifys(Message message) throws TIMServiceException {
		// 根据消息的msgType来找到对应关系,如果msgType为空，不做处理
		if (StringUtils.isBlank(message.getMessageType())) {
			return null;
		}

		// 直接用msgType做为Key
		String key = message.getMessageType();

		// 根据得到的订阅关系key找到build消息的路径，并且从消息中获得相应的信息
		List<Notify> notifys = null;
		if (vasMessages.containsKey(key)) {
			notifys = vasNotifyBuilder.build(message, null, vasMessages.get(key).getStatus());
		} else {
			return null;
		}

		// 查询订阅者，生成最终消息
		List<Notify> result = fillAppKey(notifys, message.getBornTime());

		return result;
	}
	
	
	/**
	 * 根据订阅关系得到最终存入数据库的消息列表
	 */
	private List<Notify> fillAppKey(List<Notify> preNotifys, long bornTime) throws TIMServiceException {
		if (null == preNotifys || preNotifys.isEmpty()) {
			return null;
		}

		List<Notify> notifys = null;
		Date modified = new Date(bornTime);
		for (Notify notify : preNotifys) {
			//清除notify内容中的bizOrder字段
			BizOrderDO bizOrder = (BizOrderDO)notify.getContent().get(NotifyConstants.BIZ_ORDER);
			if (null != bizOrder) {
				notify.getContent().remove(NotifyConstants.BIZ_ORDER);
			}
			
			// 查询订阅的AppKey列表
			List<NotifyInfo> nInfos = subscriptionManager.getSubscription(notify.getUserId(), notify
					.getCategory(), notify.getBizType(), notify.getStatus(), notify.getUserRole());

			// 如果没有App订阅这个消息，跳过此次循环
			if (null == nInfos || nInfos.isEmpty()) {
				continue;
			}
			
			if (null == notifys) {
				notifys = new ArrayList<Notify>();
			}

			NotifyInfo info;
			// 根据得到的AppKey列表填充消息,默认将bornTime设置为消息的Modified时间
			if (nInfos.size() == 1) {
				info = nInfos.get(0);
				notify.setAppKey(info.getAppKey());
				notify.setModified(modified);
				notify.setIsNotify(info.getIsNotify());
				notify.setNotifyUrl(info.getNotifyUrl());
				notify.setSubscriptions(info.getSubscriptions());
				//如果有订阅attribute，需要特殊处理
				if (null != bizOrder && null != info.getAttributes() && !info.getAttributes().isEmpty()) {
					//只有解析成功的才要存下来
					if (NotifyFillUtils.fillTradeAttributes(notify, bizOrder, info.getAttributes())) {
						notifys.add(notify);
					}
				} else {
					notifys.add(notify);
				}
				
			} else {
				for(NotifyInfo nInfo : nInfos) {
					Notify cloneNotify = notify.clone();
					cloneNotify.setAppKey(nInfo.getAppKey());
					cloneNotify.setIsNotify(nInfo.getIsNotify());
					cloneNotify.setNotifyUrl(nInfo.getNotifyUrl());
					cloneNotify.setSubscriptions(nInfo.getSubscriptions());
					cloneNotify.setModified(modified);
					
					//如果有订阅attribute，需要特殊处理
					if (null != bizOrder && null != nInfo.getAttributes() && !nInfo.getAttributes().isEmpty()) {
						//只有解析成功的才要存下来
						if (NotifyFillUtils.fillTradeAttributes(cloneNotify, bizOrder, nInfo.getAttributes())) {
							notifys.add(cloneNotify);
						}
					} else {
						notifys.add(cloneNotify);
					}
				}
			}
			
		}
		return notifys;
	}

}
