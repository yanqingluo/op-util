package com.taobao.top.notify.log;

import com.taobao.top.xbox.asynlog.data.TopLog;
/**
 * <b>TOPNotify 运行时日志</b>
 * <br />
 * @author <a href="mailto:yibiao@taobao.com">yibiao</a>
 * 
 * <p>日志内容：消息id, 类型, 消息category, 消息status, 商品id/交易id,<br /> 
 * 消息分裂条数, 交易金额, 用户id, appKey, 消息创建时间, 调用mq服务耗时,<br />
 * 消息解析耗时, 消息存储耗时, 消息进入发送队列耗时</p>
 * <p>类型分为：存储DB打点->0，发送消息打点->1</p>
 */
public class RunTimeMessageLog extends TopLog{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4013715603063799976L;
	
	public RunTimeMessageLog() {
		setClassName("runTimeMessageLog");
		setLogTime(System.currentTimeMillis());
	}
	/**
	 * 消息id
	 */
	private Long id;
	
	/**
	 * 日志类型
	 */
	private Long logType;
	
	/**
	 * 消息category
	 */
	private Integer category;
	
	/**
	 * 消息status
	 */
	private Integer status;
	
	/**
	 * 商品id/交易id
	 */
	private Long itemOrTradeId;
	
	/**
	 * 交易金额
	 */
	private String tradeFee;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * appKey
	 */
	private String appKey;
	
	/**
	 * 消息创建时间
	 */
	private Long created;
	
	private Long callMQServiceTime;
	
//	private Date modified;
//	private String isNotify;
//	private String notifyUrl;
//	private String subscriptions;
//	private Integer bizType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getItemOrTradeId() {
		return itemOrTradeId;
	}

	public void setItemOrTradeId(Long itemOrTradeId) {
		this.itemOrTradeId = itemOrTradeId;
	}

	public String getTradeFee() {
		return tradeFee;
	}

	public void setTradeFee(String tradeFee) {
		this.tradeFee = tradeFee;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}
	
	public Long getLogType() {
		return logType;
	}

	public void setLogType(Long logType) {
		this.logType = logType;
	}
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		append(result, id, true);
		append(result, logType, true);
		append(result, category, true);
		append(result, status, true);
		append(result, itemOrTradeId, true);
		
		append(result, tradeFee, true);
		append(result, userId, true);
		append(result, appKey, true);
		append(result, created, true);
		append(result, callMQServiceTime, false);
		return result.toString();
	}
	
	private void append(StringBuffer sb, Object content, boolean needAppend) {
		if (null == sb) {
			return;
		}
		
		if (null != content) {
			sb.append(content);
		}
		
		if (needAppend) {
			sb.append(",");
		}
		
	}

	public void setCallMQServiceTime(Long callMQServiceTime) {
		this.callMQServiceTime = callMQServiceTime;
	}

	public Long getCallMQServiceTime() {
		return callMQServiceTime;
	}
	
}
