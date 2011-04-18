package com.taobao.top.notify.monitor;

import java.util.List;

import com.taobao.monitor.Keys;

/**
 * 日志。
 * 
 * @author fengsheng
 * @since 1.0, Dec 31, 2009
 */
public abstract class NotifyLog {

	// 程序执行时间
	private long responseTime;
	// 程序执行次数
	private long invokeCount = 1L;

	public long getResponseTime() {
		return this.responseTime;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	public long getInvokeCount() {
		return this.invokeCount;
	}

	public void setInvokeCount(long invokeCount) {
		this.invokeCount = invokeCount;
	}

	/**
	 * 获取哈勃认识的打点关键字列表。
	 */
	public Keys getKeys() {
		return new Keys(getLogKeys());
	}

	/**
	 * 获取日志类型。
	 */
	protected abstract String getLogType();

	/**
	 * 获取日志打点关键字列表。
	 */
	protected abstract List<String> getLogKeys();

}
