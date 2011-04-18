package com.taobao.top.notify.monitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 接收日志。
 * 
 * @author fengsheng
 * @since 1.0, Dec 31, 2009
 */
public class ReceiveLog extends NotifyLog {

	private String topic;
	private String msgType;
	private boolean persisted;

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getMsgType() {
		return this.msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public boolean isPersisted() {
		return this.persisted;
	}

	public void setPersisted(boolean persisted) {
		this.persisted = persisted;
	}

	protected String getLogType() {
		return "TOP-NOTIFY-RECEIVE";
	}

	protected List<String> getLogKeys() {
		List<String> keys = new ArrayList<String>();
		keys.add(this.topic);
		keys.add(this.msgType);
		keys.add(this.persisted ? "Y" : "N");
		return keys;
	}

}
