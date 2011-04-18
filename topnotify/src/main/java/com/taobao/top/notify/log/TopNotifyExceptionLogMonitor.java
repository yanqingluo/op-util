package com.taobao.top.notify.log;

public interface TopNotifyExceptionLogMonitor {
	public void logParseException(TopNotifyExceptionLog log);
	public void logReadSubscribeException(TopNotifyExceptionLog log);
	public void logStoreMsgException(TopNotifyExceptionLog log);
	public void logSendMsgException(TopNotifyExceptionLog log);
}
