package com.taobao.top.notify.receive.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.hsf.notify.client.message.Message;
import com.taobao.tc.refund.client.message.RefundInfoMessageConverter;
import com.taobao.tc.refund.domain.RefundDO;
import com.taobao.tc.refund.domain.message.RefundInfoTO;
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
public class RefundNotifyBuilder implements NotifyBuilder {

	private static final Log log = LogFactory.getLog(RefundNotifyBuilder.class);

	private TopNotifyExceptionLogMonitor topNotifyExceptionLogMonitor;
	
	public List<Notify> build(Message message, Integer bizType, Integer status){
		RefundInfoMessageConverter converter = new RefundInfoMessageConverter(message.getMessageType());
		RefundInfoTO refundMsg = null;
		try {
			refundMsg = converter.fromMessage(message);
		} catch (Exception e) {
//			log.error(NotifyBuilderUtils.logMessage(message, "Refund消息体转换异常："));
			topNotifyExceptionLogMonitor.logParseException(populateTopNotifyExceptionLog(message, e));
			return null;
		}

		// 判断消息内容是否为空
		if (null == refundMsg || null == refundMsg.getRefundDO()) {
			log.warn(NotifyBuilderUtils.logMessage(message, "Refund消息体中RefundDO内容为空："));
			return null;
		}

		RefundDO refund = refundMsg.getRefundDO();
		Notify sellerNotify = new Notify();

		// 设置消息类别
		sellerNotify.setCategory(NotifyEnum.REFUND.getCategory());

		// 无交易类别

		// 设置消息状态
		sellerNotify.setStatus(status);

		// 设置操作产生的时间，默认在listener里面统一用bornTime设置
//		sellerNotify.setModified(refund.getGmtModified());

		// 设置交易具体内容（部分）
		sellerNotify.setContent(NotifyFillUtils.fillRefund(refund));

		// 复制消息
		Notify buyerNotify = sellerNotify.clone();

		// 设置用户的昵称
		sellerNotify.setUserName(refund.getSellerNick());
		buyerNotify.setUserName(refund.getBuyerNick());

		// 设置用户id
		sellerNotify.setUserId(refund.getSellerId());
		buyerNotify.setUserId(refund.getBuyerId());
		
		//设置消息归属方 moling 2010-12-03
		sellerNotify.setUserRole(NotifyConstants.USER_ROLE_SELLER);
		buyerNotify.setUserRole(NotifyConstants.USER_ROLE_BUYER);

		List<Notify> notifyList = new ArrayList<Notify>();
		notifyList.add(sellerNotify);
		notifyList.add(buyerNotify);

		return notifyList;
	}
	
	private TopNotifyExceptionLog populateTopNotifyExceptionLog(Message message, Exception e) {
		TopNotifyExceptionLog log = new TopNotifyExceptionLog();
		log.setMsgId(message.getMessageId());
		log.setExceptionMsg("Refund消息体转换异常：" + e.getMessage());
		
		return log;
	}

	public TopNotifyExceptionLogMonitor getTopNotifyExceptionLogMonitor() {
		return topNotifyExceptionLogMonitor;
	}

	public void setTopNotifyExceptionLogMonitor(
			TopNotifyExceptionLogMonitor topNotifyExceptionLogMonitor) {
		this.topNotifyExceptionLogMonitor = topNotifyExceptionLogMonitor;
	}

}
