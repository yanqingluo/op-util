package com.taobao.top.notify.monitor;

import com.taobao.monitor.MonitorLog;

/**
 * 消息监控。
 * 
 * @author fengsheng
 * @since 1.0, Dec 31, 2009
 */
public final class NotifyMonitor {

	public static void log(NotifyLog log) {
		log.getLogKeys().add(0, log.getLogType());
		MonitorLog.addStat(log.getKeys(), log.getResponseTime(), log.getInvokeCount());
	}

}
