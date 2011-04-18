package com.taobao.top.notify.util;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.taobao.item.constant.ItemUpdateChangedField;
import com.taobao.item.domain.ItemDO;
import com.taobao.item.domain.ItemUpdateDO;
import com.taobao.tc.domain.dataobject.BizOrderDO;
import com.taobao.tc.refund.domain.RefundDO;
import com.taobao.top.notify.domain.Notify;
import com.taobao.top.tim.domain.AttributeDO;
import com.taobao.upp.rating.client.dto.billing.BillResourceImpactDTO;
import com.taobao.upp.rating.client.dto.billing.BillingImpactPackage;
import com.taobao.upp.rating.client.dto.billing.BillingImpactPackage.EventInfo;
import com.taobao.upp.switching.notify.bean.ServiceOpen;
import com.taobao.util.Money;

/**
 * 内容填充工具类。
 * 
 * @author moling
 * @since 1.0, 2009-12-15
 */
public class NotifyFillUtils {
	public static final String DOUBLE_EQUAL = "==";
	public static final String DOUBLE_SEM = ";;";
	public static final String DEFAULT_EVENT_ID = "0";
	public static final String MSG_ID_MARK = "msg_id==";
	public static final String NICK_MARK = "NICK";
	public static final String AMOUNT_MARK = "SERV_AMOUNT";
	public static final String START_TIME_MARK = "START_TIME";
	public static final String END_TIME_MARK = "END_TIME";

	/**
	 * 将交易订单中的信息转换为content字段
	 */
	public static Map<String, Object> fillTrade(BizOrderDO bizOrder) {
		if (null == bizOrder) {
			return null;
		}

		Map<String, Object> content = new HashMap<String, Object>();

		content.put(NotifyConstants.TID, bizOrder.getPayOrderId());
		content.put(NotifyConstants.OID, bizOrder.getBizOrderId());
		content.put(NotifyConstants.SELLER_NICK, bizOrder.getSellerNick());
		content.put(NotifyConstants.BUYER_NICK, bizOrder.getBuyerNick());

		if (!bizOrder.isMain()) {
			// 子订单的实付金额通过计算得到
			content.put(NotifyConstants.PAYMENT, new Money(bizOrder.getActualTotalFee()).toString());
		} else {
			// 主订单的实付金额通过attribute的字段里面拿到
			String strCent = bizOrder.getAttribute(BizOrderDO.ATTRIBUTE_ACTUAL_TOTAL_FEE);
			if (StringUtils.isNotBlank(strCent) && StringUtils.isNumeric(strCent)) {
				long cent = Long.valueOf(strCent);
				content.put(NotifyConstants.PAYMENT, new Money(cent).toString());
			}
		}
		
		//将bizOrder放入content中，提供后面根据attributes解析用，取出后记得移掉 moling 2010-12-03
		content.put(NotifyConstants.BIZ_ORDER, bizOrder);

		//3D单独解析取消，需要走attributes订阅
//		if (NotifyConstants.IS_3D_VALUE.equals(bizOrder.getAttribute(BizOrderDO.ATTRIBUTE_ORDER_FROM))) {
//			content.put(NotifyConstants.IS_3D, true);
//		} else {
//			content.put(NotifyConstants.IS_3D, false);
//		}

		return content;
	}

	/**
	 * 将退款记录中的信息转换为content字段
	 */
	public static Map<String, Object> fillRefund(RefundDO refund) {
		if (null == refund) {
			return null;
		}

		Map<String, Object> content = new HashMap<String, Object>();
		content.put(NotifyConstants.TID, refund.getPayOrderId());
		content.put(NotifyConstants.OID, refund.getBizOrderId());
		content.put(NotifyConstants.SELLER_NICK, refund.getSellerNick());
		content.put(NotifyConstants.BUYER_NICK, refund.getBuyerNick());
		content.put(NotifyConstants.REFUND_ID, refund.getRefundId());
		content.put(NotifyConstants.REFUND_FEE, new Money(refund.getReturnFee()).toString());

		return content;
	}

	/**
	 * 将新增商品的字段转换为content字段
	 */
	public static Map<String, Object> fillItem(ItemDO item) {
		if (null == item) {
			return null;
		}

		Map<String, Object> content = new HashMap<String, Object>();
		content.put(NotifyConstants.IID, item.getItemIdStr());
		content.put(NotifyConstants.NUM_IID, item.getItemId());
		content.put(NotifyConstants.TITLE, item.getTitle());
		content.put(NotifyConstants.PRICE, item.getReservePrice().toString());
		content.put(NotifyConstants.NUM, item.getQuantity());
		
		return content;
	}

	/**
	 * 将编辑商品的字段转换为content字段
	 */
	public static Map<String, Object> fillItem(ItemUpdateDO item) {
		if (null == item) {
			return null;
		}

		Map<String, Object> content = new HashMap<String, Object>();
		content.put(NotifyConstants.IID, item.getItemIdStr());
		content.put(NotifyConstants.NUM_IID, item.getItemId());
		content.put(NotifyConstants.TITLE, item.getTitle());
		if (item.getReservePrice() != null) {
			content.put(NotifyConstants.PRICE, new Money(item.getReservePrice()).toString());
		}
		content.put(NotifyConstants.NUM, item.getQuantity());
		content.put(NotifyConstants.CHANGED_FIELDS, getChangedFields(item));

		return content;
	}

	/**
	 * 设置被更改的字段列表
	 */
	private static String getChangedFields(ItemUpdateDO item) {
		StringBuilder changedFields = new StringBuilder();
		if (item.isChangedSomeField(ItemUpdateChangedField.TITLE)) {
			changedFields.append(NotifyConstants.TITLE).append(",");
		}

		if (item.isChangedSomeField(ItemUpdateChangedField.PRICE)) {
			changedFields.append(NotifyConstants.PRICE).append(",");
		}

		if (item.isChangedSomeField(ItemUpdateChangedField.QUANTITY)) {
			changedFields.append(NotifyConstants.NUM).append(",");
		}

		//IC暂时没发，代码保留以后待用
		if (item.isChangedSomeField(ItemUpdateChangedField.KEY_PROPERTY)) {
			changedFields.append(NotifyConstants.PROPS).append(",");
		}

		if (item.isChangedSomeField(ItemUpdateChangedField.IMAGE)) {
			changedFields.append(NotifyConstants.IMG).append(",");
		}

		if (item.isChangedSomeField(ItemUpdateChangedField.LOCATION)) {
			changedFields.append(NotifyConstants.LOCATION).append(",");
		}
		
		//IC暂时没发，代码保留以后待用
		if (item.isChangedSomeField(ItemUpdateChangedField.SKU)) {
			changedFields.append(NotifyConstants.SKU).append(",");
		}

		if (item.isChangedSomeField(ItemUpdateChangedField.CATEGORY)) {
			changedFields.append(NotifyConstants.CID).append(",");
		}

		if (item.isChangedSomeField(ItemUpdateChangedField.EDIT_UPSHELF)
				|| item.isChangedSomeField(ItemUpdateChangedField.EDIT_DOWNSHELF)) {
			changedFields.append(NotifyConstants.APPROVE_STATUS).append(",");
		}

		if (item.isChangedSomeField(ItemUpdateChangedField.EDIT_TIMINGSTART)) {
			changedFields.append(NotifyConstants.LIST_TIME).append(",");
		}

		if (changedFields.length() > 0) {
			changedFields.deleteCharAt(changedFields.length() - 1);
		}

		return changedFields.toString();
	}
	
	/**
	 * 根据订阅的attributes填充交易信息。返回true,表示填充完成，此消息需要。返回false表示此消息不需要
	 * @param notify
	 * @param bizOrder
	 * @param attributes
	 * @return
	 */
	public static boolean fillTradeAttributes(Notify notify, BizOrderDO bizOrder, List<AttributeDO> attributes) {
		//基本信息缺乏报错误
		if (null == notify) {
			return false;
		}
		
		//不是合法需要填充的消息默认正确存储
		if (null == bizOrder || null == attributes || attributes.isEmpty()) {
			return true;
		}
		
		//默认不存储，只要有一个标记满足就存储
		boolean needStore = false;
		StringBuffer tradeMark = new StringBuffer();
		for (AttributeDO attribute : attributes) {
			//此处不做空指针控制，由tim控制完整性
			if (attribute.getValue().equals(bizOrder.getAttribute(attribute.getKey()))) {
				if (tradeMark.length() > 0) {
					tradeMark.append(";");
				}
				tradeMark.append(attribute.getKeyName()).append(":").append(attribute.getValueName());
				needStore = true;
			}
		}
		
		if (needStore) {
			notify.addContent(NotifyConstants.TRADE_MARK, tradeMark.toString());
		}
		
		return needStore;
	}
	
	public static Map<String, Object> fillVasBillingMsg(BillingImpactPackage billingMsg) {
		if (null == billingMsg) {
			return null;
		}

		String nick;
		Long userId;
		//桑志表示一条消息的所有操作一定属于同一个用户，取列表中第一条的用户信息为准。且默认nick和userId不会有空的情况
		if (null != billingMsg.getResImpactDto() && billingMsg.getResImpactDto().size() > 0) {
			nick =  billingMsg.getResImpactDto().get(0).getUserNick();
			userId = billingMsg.getResImpactDto().get(0).getUserId();
			if (StringUtils.isBlank(nick) || null == userId || userId <= 0) {
				//对nick和userId进行空判断，不合法的直接不接收
				return null;
			}
		} else {
			//如果没有详细的扣款流程此消息不收，此处不对content进行解析，外面对service_code的过滤会抛弃此消息
			return null;
		}
		
		//编辑memo和amounts信息
		StringBuffer memo = new StringBuffer();
		StringBuffer amounts = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<Long, EventInfo> eventInfos = billingMsg.getEventInfoHash();
		for (BillResourceImpactDTO billing : billingMsg.getResImpactDto()) {
			//是短信服务的扣款记录才解析
			if (NotifyConstants.SERVICE_CODE_DXFW.equals(billing.getServiceCode())) {
				memo.append(billing.getEventId()).append(DOUBLE_EQUAL).append(billing.getSubscriptionId()).append(DOUBLE_SEM);
				
				//amounts里面增加msg_id的消息
				String markId = null;
				if (null != billing.getEventId() && null != eventInfos) {
					EventInfo eventInfo = eventInfos.get(billing.getEventId());
					if (null != eventInfo && StringUtils.isNotBlank(eventInfo.getRaBillExtends())) {
						String[] marks = eventInfo.getRaBillExtends().split(DOUBLE_SEM);
						if (null != marks && marks.length > 0) {
							for (int i = 0; i < marks.length; ++i) {
								if (marks[i].startsWith(MSG_ID_MARK)) {
									String[] messages = marks[i].split(DOUBLE_EQUAL);
									if (null != messages && messages.length > 1) {
										markId = messages[1];
										break;
									}
								}
							}
						}
					}
				}
				amounts.append(billing.getEventId()).append(DOUBLE_EQUAL).append(markId).append(DOUBLE_EQUAL)
				.append(billing.getAmount()).append(DOUBLE_EQUAL).append(billing.getQuantity()).append(DOUBLE_EQUAL)
				.append(sdf.format(billing.getStartTime())).append(DOUBLE_EQUAL).append(sdf.format(billing.getEndTime())).append(DOUBLE_SEM);
			
			}
		}
		
		//amounts是必要选项，如果为空表示此笔订单不用保存下来
		if (amounts.length() <= 0) {
			return null;
		}
		
		Map<String, Object> content = new HashMap<String, Object>();
		content.put(NotifyConstants.NICK, nick);
		content.put(NotifyConstants.USER_ID, userId);
		//编辑memo和amounts信息
		if (memo.length() > 0) {
			content.put(NotifyConstants.MEMO, memo.substring(0, memo.length() - 2));
		}
		content.put(NotifyConstants.AMOUNTS, amounts.substring(0, amounts.length() - 2));
		content.put(NotifyConstants.SERVICE_CODE, NotifyConstants.SERVICE_CODE_DXFW);
		return content;
	}
	
	public static Map<String, Object> fillVasServiceOpenMsg(ServiceOpen serviceOpenMsg) {
		if (null == serviceOpenMsg) {
			return null;
		}
		
		if (!NotifyConstants.SERVICE_CODE_DXFW.equals(serviceOpenMsg.getServCode())) {
			return null;
		}
		
		if (null == serviceOpenMsg.getUserId() || serviceOpenMsg.getUserId() <= 0) {
			//对userId进行空判断，不合法的直接不接收
			return null;
		}
		
		Map<String, Object> content = new HashMap<String, Object>();
		Map<String, String> rspMap = serviceOpenMsg.getResponseMap();
		//解析用户nick
		if (null != rspMap) {
			content.put(NotifyConstants.NICK, rspMap.get(NICK_MARK));
			
			//组装amounts字段
			StringBuffer amounts = new StringBuffer();
			amounts.append(DEFAULT_EVENT_ID).append(DOUBLE_EQUAL).append(DEFAULT_EVENT_ID).append(DOUBLE_EQUAL)
			.append(DEFAULT_EVENT_ID).append(DOUBLE_EQUAL).append(rspMap.get(AMOUNT_MARK)).append(DOUBLE_EQUAL)
			.append(rspMap.get(START_TIME_MARK))
			.append(DOUBLE_EQUAL).append(rspMap.get(END_TIME_MARK));
			content.put(NotifyConstants.AMOUNTS, amounts.toString());
		}
		
		content.put(NotifyConstants.USER_ID, serviceOpenMsg.getUserId());
		//编辑memo信息组装
		StringBuffer memo = new StringBuffer();
		memo.append(DEFAULT_EVENT_ID).append(DOUBLE_EQUAL).append(serviceOpenMsg.getProdSubId());
		content.put(NotifyConstants.MEMO, memo.toString());
		content.put(NotifyConstants.SERVICE_CODE, NotifyConstants.SERVICE_CODE_DXFW);
		
		return content;
	}
	
}
