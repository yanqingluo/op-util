package com.taobao.top.notify.log;

public interface RunTimeMessageLogMonitor {
	
	public void logDBStoreMessage(RunTimeMessageLog log);
	
	public void logSendMessage(RunTimeMessageLog log);

}
