package com.taobao.top.ats.monitor;

import com.taobao.top.ats.domain.LogDO;
import com.taobao.top.ats.util.StringKit;
import com.taobao.top.xbox.asynlog.data.TopLog;

/**
 * 
 * @author moling
 * @since 1.0, 2010-11-16
 */
public class TaskLog extends TopLog {

	private static final long serialVersionUID = 1514932570030266713L;
	
	private LogDO logDO;
	private String localIp;
	
	public TaskLog() {
		setClassName("task");
		setLogTime(System.currentTimeMillis());
	}
	
	public LogDO getLogDO() {
		return this.logDO;
	}

	public void setLogDO(LogDO logDO) {
		this.logDO = logDO;
	}

	public String getLocalIp() {
		return this.localIp;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		StringKit.append(result, logDO.getMethod(), true);
		StringKit.append(result, logDO.getAppkey(), true);
		StringKit.append(result, logDO.getTaskId(), true);
		StringKit.append(result, logDO.getTaskTime(), true);
		StringKit.append(result, logDO.getSubTaskTime(), true);
		StringKit.append(result, logDO.getSuccessCount(), true);
		StringKit.append(result, logDO.getOneErrorCount(), true);
		StringKit.append(result, logDO.getErrorCount(), true);
		StringKit.append(result, localIp, false);
		
		return result.toString();
	}
	

}
