package com.taobao.top.notify.log;

import com.taobao.notify.utils.UniqId;
import com.taobao.top.xbox.asynlog.data.TopLog;
/**
 * <b>运行时收到消息日志</b>
 * <br />
 * @author <a href="mailto:yibiao@taobao.com">yibiao</a>
 * <p>日志内容：notifyId, 消息category, 消息status, 消息分裂条数,<br /> 
 * 消息解析耗时, 消息存储耗时, 消息进入发送队列耗时</p>
 */
public class RunTimeNotifyLog extends TopLog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8038420007118464232L;

	public RunTimeNotifyLog() {
		setClassName("runTimeNotifyLog");
		setLogTime(System.currentTimeMillis());
	}
	
	private byte[] id;
	
	private String type;
	
	private Integer divNum;
	
	private Long parseServiceTime;
	
	private Long storeServiceTime;
	
	private Long putMsgQueueTime;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		append(sb, UniqId.getInstance().bytes2string(id), true);
		append(sb, type, true);
		append(sb, divNum, true);
		append(sb, parseServiceTime, true);
		append(sb, storeServiceTime, true);
		append(sb, putMsgQueueTime, false);
		
		return sb.toString();
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
	
	public byte[] getId() {
		return id;
	}

	public void setId(byte[] id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getDivNum() {
		return divNum;
	}

	public void setDivNum(Integer divNum) {
		this.divNum = divNum;
	}

	public Long getParseServiceTime() {
		return parseServiceTime;
	}

	public void setParseServiceTime(Long parseServiceTime) {
		this.parseServiceTime = parseServiceTime;
	}

	public Long getStoreServiceTime() {
		return storeServiceTime;
	}

	public void setStoreServiceTime(Long storeServiceTime) {
		this.storeServiceTime = storeServiceTime;
	}

	public Long getPutMsgQueueTime() {
		return putMsgQueueTime;
	}

	public void setPutMsgQueueTime(Long putMsgQueueTime) {
		this.putMsgQueueTime = putMsgQueueTime;
	}
}
