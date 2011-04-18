package com.taobao.top.ats.monitor;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.taobao.top.ats.domain.LogDO;
import com.taobao.top.xbox.asynlog.AsynWriter;
import com.taobao.top.xbox.asynlog.FileWriterFactory;
import com.taobao.top.xbox.asynlog.IWriterScheduleFactory;

/**
 * 
 * @author moling
 * @since 1.0, 2010-11-16
 */
public class TaskMonitorImpl implements TaskMonitor {
	private AsynWriter<TaskLog> taskLog;
	private AsynWriter<SubTaskLog> subTaskLog;
	
	//执行子任务的机器ip
	public static String localIp;
	
	static {
        try {
        	localIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			//取失败不处理
		}
    }
	
	public void init() throws Exception {
		//初始化taskLog
		taskLog = new AsynWriter<TaskLog>();
		IWriterScheduleFactory<TaskLog> writerFactoryTask
			= new FileWriterFactory<TaskLog>();
		writerFactoryTask.setLoggerClass(TaskLog.class.getName());
		writerFactoryTask.init();
		taskLog.setWriterFactory(writerFactoryTask);
		taskLog.init();
		
		//初始化subTaskLog
		subTaskLog = new AsynWriter<SubTaskLog>();
		IWriterScheduleFactory<SubTaskLog> writerFactorySubTask
			= new FileWriterFactory<SubTaskLog>();
		writerFactorySubTask.setLoggerClass(SubTaskLog.class.getName());
		writerFactorySubTask.init();
		subTaskLog.setWriterFactory(writerFactorySubTask);
		subTaskLog.init();
	}
	
	public void stop() throws Exception {
		taskLog.destroy();
		subTaskLog.destroy();
	}

	public void logError(LogDO logDO) {
		SubTaskLog log = new SubTaskLog();
		log.setLogDO(logDO);
		log.setSuccess(false);
		
		subTaskLog.write(log);
	}

	public void logSuccess(LogDO logDO) {
		SubTaskLog log = new SubTaskLog();
		log.setLogDO(logDO);
		//以防万一将errorCode置空
		logDO.setErrorCode(null);
		log.setSuccess(true);
		
		subTaskLog.write(log);
	}

	public void logTask(LogDO logDO) {
		TaskLog log = new TaskLog();
		log.setLogDO(logDO);
		log.setLocalIp(localIp);
		
		taskLog.write(log);
	}

}
