package com.taobao.top.ats.monitor;

import com.taobao.top.ats.domain.LogDO;
import com.taobao.top.ats.util.StringKit;
import com.taobao.top.xbox.asynlog.data.TopLog;

/**
 * 
 * @author moling
 * @since 1.0, 2010-11-16
 */
public class SubTaskLog extends TopLog {
	private static final long serialVersionUID = -1154783444801522607L;
	
	private LogDO logDO;
	private boolean isSuccess;
	
	public SubTaskLog() {
		setClassName("subTask");
		setLogTime(System.currentTimeMillis());
	}
	
	public LogDO getLogDO() {
		return this.logDO;
	}

	public void setLogDO(LogDO logDO) {
		this.logDO = logDO;
	}

	public boolean isSuccess() {
		return this.isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		StringKit.append(result, logDO.getMethod(), true);
		StringKit.append(result, logDO.getAppkey(), true);
		StringKit.append(result, logDO.getCostTime(), true);
		StringKit.append(result, logDO.getSubTaskId(), true);
		StringKit.append(result, logDO.getErrorCode(), true);
		StringKit.append(result, isSuccess, false);
		
		return result.toString();
	}
	
	
}
