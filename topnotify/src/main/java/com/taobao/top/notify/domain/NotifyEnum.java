package com.taobao.top.notify.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息位枚举。
 * 
 * @author fengsheng
 * @since 1.0, Dec 18, 2009
 */
public enum NotifyEnum {

	// 消息类别
	TRADE(1, 0, "trade", "交易"),
	REFUND(2, 0, "refund", "退款"),
	ITEM(3, 0, "item", "商品"),
	VAS(4, 0, "vas", "收费"),

	// 交易消息状态
	TRADE_CREATE(1, 1, "TradeCreate", "创建交易"),
	TRADE_MODIFY_FEE(1, 2, "TradeModifyFee", "修改交易费用"),
	TRADE_CLOSE_AND_MODIFY_DETAIL_ORDER(1, 3, "TradeCloseAndModifyDetailOrder", "关闭或修改子订单"),
	TRADE_CLOSE(1, 4, "TradeClose", "关闭交易"),
	TRADE_BUYER_PAY(1, 5, "TradeBuyerPay", "买家付款"),
	TRADE_SELLER_SHIP(1, 6, "TradeSellerShip", "卖家发货"),
	TRADE_DELAY_CONFIRM_PAY(1, 7, "TradeDelayConfirmPay", "延长收货时间"),
	TRADE_PARTLY_REFUND(1, 8, "TradePartlyRefund", "子订单退款成功"),
	TRADE_PARTLY_CONFIRM_PAY(1, 9, "TradePartlyConfirmPay", "子订单打款成功"),
	TRADE_SUCCESS(1, 10, "TradeSuccess", "交易成功"),
	TRADE_TIMEOUT_REMIND(1, 11, "TradeTimeoutRemind", "交易超时提醒"),
	TRADE_RATED(1, 12, "TradeRated", "交易评价变更"),
	TRADE_MEMO_MODIFIED(1, 13, "TradeMemoModified", "交易备注修改"),
	TRADE_LOGISTICS_ADDRESS_CHANGED(1, 14, "TradeLogisticsAddressChanged", "修改交易收货地址"),
	TRADE_CHANGED(1, 15, "TradeChanged", "修改订单信息"),

	// 退款消息状态
	REFUND_SUCCESS(2, 1, "RefundSuccess", "退款成功"),
	REFUND_CLOSED(2, 2, "RefundClosed", "退款关闭"),
	REFUND_CREATED(2, 3, "RefundCreated", "退款创建"),
	REFUND_SELLER_AGREE_AGREEMENT(2, 4, "RefundSellerAgreeAgreement", "卖家同意退款协议"),
	REFUND_SELLER_REFUSE_AGREEMENT(2, 5, "RefundSellerRefuseAgreement", "卖家拒绝退款协议"),
	REFUND_BUYER_MODIFIY_AGREEMENT(2, 6, "RefundBuyerModifyAgreement", "买家修改退款协议"),
	REFUND_BUYER_RETURN_GOODS(2, 7, "RefundBuyerReturnGoods", "买家退货给卖家"),
	REFUND_CREATE_MESSAGE(2, 8, "RefundCreateMessage", "发表退款留言"),
	REFUND_BLOCK_MESSAGE(2, 9, "RefundBlockMessage", "屏蔽退款留言"),
	REFUND_TIMEOUT_REMIND(2, 10, "RefundTimeoutRemind", "退款超时提醒"),

	// 商品消息状态
	ITEM_ADD(3, 1, "ItemAdd", "新增商品"),
	ITEM_UPSHELF(3, 2, "ItemUpshelf", "上架商品"),
	ITEM_DOWNSHELF(3, 3, "ItemDownshelf", "下架商品"),
	ITEM_DELETE(3, 4, "ItemDelete", "删除商品"),
	ITEM_UPDATE(3, 5, "ItemUpdate", "更新商品"),
	//ITEM_FEATURE_CHANGE(3, 6, "ItemFeatureChange"),
	ITEM_RECOMMEND_DELETE(3, 7, "ItemRecommendDelete", "取消橱窗推荐商品"),
	ITEM_RECOMMEND_ADD(3, 8, "ItemRecommendAdd", "橱窗推荐商品"),
	ITEM_ZERO_STOCK(3, 9, "ItemZeroStock", "商品卖空"),
	ITEM_PUNISH_DELETE(3, 10, "ItemPunishDelete", "小二删除商品"),
	
	//收费消息状态
	VAS_BILLING_SUCCESS(4, 1, "VasBillingSuccess", "淘宝增值服务扣款成功"),
	VAS_SERVICE_OPEN(4, 2, "VasServiceOpen", "淘宝增值服务开通");

	private int category;
	private int status;
	private String message;
	private String desc;

	private NotifyEnum(int category, int status, String message, String desc) {
		this.category = category;
		this.status = status;
		this.message = message;
		this.desc = desc;
	}

	/**
	 * 根据类别和状态获取枚举
	 */
	public static NotifyEnum getInstance(int category, int status) {
		NotifyEnum[] notifyEnums = NotifyEnum.values();
		for (NotifyEnum notifyEnum : notifyEnums) {
			if (notifyEnum.category == category && notifyEnum.status == status) {
				return notifyEnum;
			}
		}
		throw new IllegalArgumentException("非法的类别名称或状态名称!");
	}

	/**
	 * 根据类别和描述获取枚举
	 */
	public static NotifyEnum getInstance(int category, String message) {
		NotifyEnum[] notifyEnums = NotifyEnum.values();
		for (NotifyEnum notifyEnum : notifyEnums) {
			if (notifyEnum.category == category && notifyEnum.message.equals(message)) {
				return notifyEnum;
			}
		}
		throw new IllegalArgumentException("非法的类别名称或状态名称!");
	}

	/**
	 * 根据描述获取枚举
	 */
	public static NotifyEnum getInstance(String message) {
		NotifyEnum[] notifyEnums = NotifyEnum.values();
		for (NotifyEnum notifyEnum : notifyEnums) {
			if (notifyEnum.message.equals(message)) {
				return notifyEnum;
			}
		}
		throw new IllegalArgumentException("非法的状态名称!");
	}

	/**
	 * 根据描述获取类别枚举
	 */
	public static NotifyEnum getCategoryInstance(String message) {
		NotifyEnum[] notifyEnums = NotifyEnum.values();
		for (NotifyEnum notifyEnum : notifyEnums) {
			if (notifyEnum.status == 0 && notifyEnum.message.equals(message)) {
				return notifyEnum;
			}
		}
		throw new IllegalArgumentException("非法的状态名称!");
	}

	/**
	 * 根据位和获取描述列表
	 */
	public static List<String> getMessages(int category, long subscriptions) {
		List<String> msgs = new ArrayList<String>();
		NotifyEnum[] notifyEnums = NotifyEnum.values();
		for (NotifyEnum notifyEnum : notifyEnums) {
			if (notifyEnum.category == category && notifyEnum.status != 0) {
				int p = (1 << (notifyEnum.status - 1));
				if (subscriptions == 0L || (subscriptions & p) == p) {
					msgs.add(notifyEnum.message);
				}
			}
		}
		return msgs;
	}

	public int getCategory() {
		return this.category;
	}

	public int getStatus() {
		return this.status;
	}

	public String getMessage() {
		return this.message;
	}

	public String getDesc() {
		return this.desc;
	}

}
