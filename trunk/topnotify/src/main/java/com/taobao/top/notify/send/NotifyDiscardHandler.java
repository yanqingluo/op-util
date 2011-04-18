package com.taobao.top.notify.send;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.notify.domain.Notify;

public class NotifyDiscardHandler implements RejectedExecutionHandler {
	
	private static final Log log = LogFactory.getLog(NotifyDiscardHandler.class);

	public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
		if(!e.isShutdown()){
			Notify notify = ((NotifySendTask)e.getQueue().poll()).getNotify();
			if(null != notify){
				log.error("缓存队列已满，此Notify无法发送至TopMQ " + notify.getAppKey());
			}
			e.execute(r);
		}
	}

}
