package com.taobao.top.notify.receive.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.hsf.notify.client.message.Message;
import com.taobao.tc.domain.dataobject.ModifyOrderDO;
import com.taobao.tc.domain.dataobject.ModifyOrderInfoTO;
import com.taobao.tc.message.convertor.ModifyOrderInfoMessageConverter;
import com.taobao.top.notify.domain.TradeMsgType;
import com.taobao.top.notify.domain.Notify;
import com.taobao.top.notify.domain.NotifyEnum;
import com.taobao.top.notify.log.TopNotifyExceptionLog;
import com.taobao.top.notify.log.TopNotifyExceptionLogMonitor;
import com.taobao.top.notify.receive.NotifyBuilder;
import com.taobao.top.notify.util.NotifyConstants;
import com.taobao.top.notify.util.NotifyFillUtils;
import com.taobao.top.notify.util.NotifyBuilderUtils;

/**
 * 
 * @author moling
 * @since 1.0, 2009-12-14
 */
public class TradePlusNotifyBuilder implements NotifyBuilder {

	private static final Log log = LogFactory.getLog(TradePlusNotifyBuilder.class);

	private TopNotifyExceptionLogMonitor topNotifyExceptionLogMonitor;
	
	public List<Notify> build(Message message, Integer bizType, Integer status) {
		ModifyOrderInfoMessageConverter converter = new ModifyOrderInfoMessageConverter(new TradeMsgType(bizType));
		ModifyOrderInfoTO modifyInfo = null;
		try {
			modifyInfo = converter.fromMessage(message);
		} catch (Exception e) {
//			log.error(NotifyBuilderUtils.logMessage(message, "TradePlus消息体转换异常："));
			topNotifyExceptionLogMonitor.logParseException(populateTopNotifyExceptionLog(message, e));
			return null;
		}

		// 如果没有修改的交易列表就不生成消息
		if (null == modifyInfo || null == modifyInfo.getModifyOrderList()
				|| modifyInfo.getModifyOrderList().isEmpty()) {
			log.warn(NotifyBuilderUtils.logMessage(message, "TradePlus消息体中ModifyOrderList内容为空："));
			return null;
		}

		List<ModifyOrderDO> modifiedList = modifyInfo.getModifyOrderList();
		List<Notify> notifyList = new ArrayList<Notify>();
		for (ModifyOrderDO modifyOrder : modifiedList) {
			// 如果消息体中无订单信息为异常现象，此处不处理此条记录
			if (null == modifyOrder.getBizOrderDO()) {
				log.warn(NotifyBuilderUtils.logMessage(message, "TradePlus消息体中BizOrderDO为空："));
				continue;
			}

			Notify sellerNotify = new Notify();
			Notify buyerNotify;

			// 设置消息类别
			sellerNotify.setCategory(NotifyEnum.TRADE.getCategory());

			// 设置交易类别
			sellerNotify.setBizType(bizType);

			// 设置消息状态
			sellerNotify.setStatus(status);

			// 设置操作产生的时间，默认在listener里面统一用bornTime设置
//			sellerNotify.setModified(modifyOrder.getBizOrderDO().getGmtModified());

			// 设置交易具体内容（部分）
			sellerNotify.setContent(NotifyFillUtils.fillTrade(modifyOrder.getBizOrderDO()));

			// 复制消息
			buyerNotify = sellerNotify.clone();

			// 设置用户的昵称
			sellerNotify.setUserName(modifyOrder.getBizOrderDO().getSellerNick());
			buyerNotify.setUserName(modifyOrder.getBizOrderDO().getBuyerNick());

			// 设置用户id
			sellerNotify.setUserId(modifyOrder.getBizOrderDO().getSellerId());
			buyerNotify.setUserId(modifyOrder.getBizOrderDO().getBuyerId());
			
			//设置消息归属方 moling 2010-12-03
			sellerNotify.setUserRole(NotifyConstants.USER_ROLE_SELLER);
			buyerNotify.setUserRole(NotifyConstants.USER_ROLE_BUYER);

			notifyList.add(sellerNotify);
			notifyList.add(buyerNotify);
		}

		return notifyList;
	}
	
	private TopNotifyExceptionLog populateTopNotifyExceptionLog(Message message, Exception e) {
		TopNotifyExceptionLog log = new TopNotifyExceptionLog();
		log.setMsgId(message.getMessageId());
		log.setExceptionMsg("TradePlus消息体转换异常：" + e.getMessage());
		
		return log;
	}

	public void setTopNotifyExceptionLogMonitor(
			TopNotifyExceptionLogMonitor topNotifyExceptionLogMonitor) {
		this.topNotifyExceptionLogMonitor = topNotifyExceptionLogMonitor;
	}

	public TopNotifyExceptionLogMonitor getTopNotifyExceptionLogMonitor() {
		return topNotifyExceptionLogMonitor;
	}

}
