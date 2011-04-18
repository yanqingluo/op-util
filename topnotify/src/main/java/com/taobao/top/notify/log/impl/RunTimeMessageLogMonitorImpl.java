package com.taobao.top.notify.log.impl;

import com.taobao.top.notify.log.RunTimeMessageLog;
import com.taobao.top.notify.log.RunTimeMessageLogMonitor;
import com.taobao.top.xbox.asynlog.AsynWriter;
import com.taobao.top.xbox.asynlog.FileWriterFactory;
import com.taobao.top.xbox.asynlog.IWriterScheduleFactory;
/**
 * 存储和发送日志的打点控制器
 * @author <a href="mailto:yibiao@taobao.com">yibiao</a>
 * <p>isDBStoreLog:控制是否打点存储日志</p>
 * <p>isSendLog:控制是否打点发送日志</p>
 */
public class RunTimeMessageLogMonitorImpl implements RunTimeMessageLogMonitor {

	private AsynWriter<RunTimeMessageLog> runTimeMessageLog;
	private Boolean isDBStoreLog;
	private Boolean isSendLog;

	public Boolean getIsDBStoreLog() {
		return isDBStoreLog;
	}

	public void setIsDBStoreLog(Boolean isDBStoreLog) {
		this.isDBStoreLog = isDBStoreLog;
	}

	public Boolean getIsSendLog() {
		return isSendLog;
	}

	public void setIsSendLog(Boolean isSendLog) {
		this.isSendLog = isSendLog;
	}

	public void init() throws Exception {
		//默认初始化为可以打log
		isDBStoreLog = Boolean.TRUE;
		isSendLog = Boolean.TRUE;
		
		//初始化taskLog
		runTimeMessageLog = new AsynWriter<RunTimeMessageLog>();
		IWriterScheduleFactory<RunTimeMessageLog> writerFactory
			= new FileWriterFactory<RunTimeMessageLog>();
		writerFactory.setLoggerClass(RunTimeMessageLog.class.getName());
		writerFactory.init();
		runTimeMessageLog.setWriterFactory(writerFactory);
		runTimeMessageLog.init();
	}
	
	public void stop() throws Exception {
		isDBStoreLog = Boolean.FALSE;
		isSendLog = Boolean.FALSE;
		
		runTimeMessageLog.destroy();
	}
	public void logDBStoreMessage(RunTimeMessageLog log) {
		if(isDBStoreLog) {
			log.setLogType(0L);
			runTimeMessageLog.write(log);
		}
	}
	public void logSendMessage(RunTimeMessageLog log) {
		if(isSendLog) {
			log.setLogType(1L);
			runTimeMessageLog.write(log);
		}
	}
}
