package com.taobao.top.notify.monitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储日志。
 * 
 * @author fengsheng
 * @since 1.0, Dec 31, 2009
 */
public class PersistLog extends NotifyLog {

	private String appKey;
	private String status;

	public String getAppKey() {
		return this.appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	protected String getLogType() {
		return "TOP-NOTIFY-PERSIST";
	}

	protected List<String> getLogKeys() {
		List<String> keys = new ArrayList<String>();
		keys.add(this.appKey);
		keys.add(this.status);
		return keys;
	}

}
