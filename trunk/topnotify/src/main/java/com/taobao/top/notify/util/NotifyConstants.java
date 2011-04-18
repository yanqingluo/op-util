package com.taobao.top.notify.util;

/**
 * 消息字段常量。
 * 
 * @author moling
 * @since 1.0, 2009-12-19
 */
public class NotifyConstants {
	// 商品常量
	public static final String IID = "iid";
	public static final String NUM_IID = "num_iid";
	public static final String CHANGED_FIELDS = "changed_fields";
	public static final String TITLE = "title";
	public static final String DESC = "desc";
	public static final String PRICE = "price";
	public static final String NUM = "num";
	public static final String PROPS = "props";
	public static final String IMG = "item_img,prop_img";
	public static final String LOCATION = "location";
	public static final String SKU = "sku";
	public static final String CID = "cid";
	public static final String APPROVE_STATUS = "approve_status";
	public static final String LIST_TIME = "list_time";
	public static final String MODIFIED = "modified";

	// 交易常量
	public static final String TID = "tid";
	public static final String OID = "oid";
	public static final String SELLER_NICK = "seller_nick";
	public static final String BUYER_NICK = "buyer_nick";
	public static final String PAYMENT = "payment";
	public static final String IS_3D = "is_3D";
	public static final String IS_3D_VALUE = "3d";

	// 退款常量
	public static final String REFUND_ID = "refund_id";
	public static final String REFUND_FEE = "refund_fee";
	
	//用户常量
	public static final String NICK = "nick";
	public static final String USER_ID = "user_id";
	
	//消息分类常量
	public static final String TOPIC = "topic";
	public static final String STATUS = "status";
	
	//收费常量
	public static final String MEMO = "memo";
	public static final String SERVICE_CODE = "service_code";
	public static final String SERVICE_CODE_DXFW = "SERV_DXFW";
	public static final String AMOUNTS = "amounts";
	
	//用户角色常量
	//不区分角色
	public static final Long USER_ROLE_ALL = 0L;
	//卖家角色
	public static final Long USER_ROLE_SELLER = 1L;
	//买家角色
	public static final Long USER_ROLE_BUYER = 2L;
	
	//attributes字段解析后对外的标记
	public static final String TRADE_MARK = "trade_mark";
	
	//临时放置于content众的bizOrderDO的key
	public static final String BIZ_ORDER = "biz_order";
}
