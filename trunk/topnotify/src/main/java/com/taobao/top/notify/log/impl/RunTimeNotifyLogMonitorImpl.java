package com.taobao.top.notify.log.impl;

import com.taobao.top.notify.log.RunTimeNotifyLog;
import com.taobao.top.notify.log.RunTimeNotifyLogMonitor;
import com.taobao.top.xbox.asynlog.AsynWriter;
import com.taobao.top.xbox.asynlog.FileWriterFactory;
import com.taobao.top.xbox.asynlog.IWriterScheduleFactory;
/**
 * 接受消息日志打点控制器
 * @author <a href="mailto:yibiao@taobao.com">yibiao</a>
 * <p>isLog:控制是否打点接受消息日志</p>
 */
public class RunTimeNotifyLogMonitorImpl implements RunTimeNotifyLogMonitor {

	private AsynWriter<RunTimeNotifyLog> runTimeNotifyLog;
	private Boolean isLog;
	
	public void init() throws Exception {
		//默认初始化为可以打log
		isLog = Boolean.FALSE;
		//初始化taskLog
		runTimeNotifyLog = new AsynWriter<RunTimeNotifyLog>();
		IWriterScheduleFactory<RunTimeNotifyLog> writerFactory
			= new FileWriterFactory<RunTimeNotifyLog>();
		writerFactory.setLoggerClass(RunTimeNotifyLog.class.getName());
		writerFactory.init();
		runTimeNotifyLog.setWriterFactory(writerFactory);
		runTimeNotifyLog.init();
	}
	
	public void stop() throws Exception {
		isLog = Boolean.FALSE;
		runTimeNotifyLog.destroy();
	}
	
	public void logRecieveMessage(RunTimeNotifyLog log) {
		if(isLog) {
			runTimeNotifyLog.write(log);
		}
	}
}
