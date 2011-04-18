package com.taobao.top.notify.log.impl;

import com.taobao.top.notify.log.TopNotifyExceptionLog;
import com.taobao.top.notify.log.TopNotifyExceptionLogMonitor;
import com.taobao.top.xbox.asynlog.AsynWriter;
import com.taobao.top.xbox.asynlog.FileWriterFactory;
import com.taobao.top.xbox.asynlog.IWriterScheduleFactory;
/**
 * 异常日志打点控制器
 * @author <a href="mailto:yibiao@taobao.com">yibiao</a>
 * <p>isLog:控制是否打点异常日志</p>
 */
public class TopNotifyExceptionLogMonitorImpl implements TopNotifyExceptionLogMonitor {
	
	private AsynWriter<TopNotifyExceptionLog> topNotifyExceptionLog;
	private Boolean isLog;
	
	public Boolean getIsLog() {
		return this.isLog;
	}

	public void setIsLog(Boolean isLog) {
		this.isLog = isLog;
	}

	public void init() throws Exception {
		//默认初始化为可以打log
		isLog = Boolean.TRUE;
		
		//初始化taskLog
		topNotifyExceptionLog = new AsynWriter<TopNotifyExceptionLog>();
		IWriterScheduleFactory<TopNotifyExceptionLog> writerFactory
			= new FileWriterFactory<TopNotifyExceptionLog>();
		writerFactory.setLoggerClass(TopNotifyExceptionLog.class.getName());
		writerFactory.init();
		topNotifyExceptionLog.setWriterFactory(writerFactory);
		topNotifyExceptionLog.init();
	}
	
	public void stop() throws Exception {
		isLog = Boolean.FALSE;
		
		topNotifyExceptionLog.destroy();
	}

	public void logParseException(TopNotifyExceptionLog log) {
		if(isLog) {
			log.setType(TopNotifyExceptionLog.PARSER_EXCEPTION);
			topNotifyExceptionLog.write(log);
		}
		
	}

	public void logReadSubscribeException(TopNotifyExceptionLog log) {
		if(isLog) {
			log.setType(TopNotifyExceptionLog.READSUB_EXCEPTION);
			topNotifyExceptionLog.write(log);
		}
	}

	public void logSendMsgException(TopNotifyExceptionLog log) {
		if(isLog) {
			log.setType(TopNotifyExceptionLog.SEND_EXCEPTION);
			topNotifyExceptionLog.write(log);
		}
	}

	public void logStoreMsgException(TopNotifyExceptionLog log) {
		if(isLog) {
			log.setType(TopNotifyExceptionLog.STORE_EXCEPTION);
			topNotifyExceptionLog.write(log);
		}
	}
}
