package com.taobao.top.notify.send.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.taobao.top.mq.TopMQService;
import com.taobao.top.notify.domain.Notify;
import com.taobao.top.notify.log.RunTimeMessageLog;
import com.taobao.top.notify.log.RunTimeMessageLogMonitor;
import com.taobao.top.notify.log.TopNotifyExceptionLogMonitor;
import com.taobao.top.notify.send.NotifyDiscardHandler;
import com.taobao.top.notify.send.NotifySendTask;
import com.taobao.top.notify.send.NotifySender;
import com.taobao.top.notify.util.NotifyConstants;
import com.taobao.top.notify.util.NotifyHelper;
import com.taobao.top.notify.util.ThreadPoolProperty;

public class NotifySenderImpl implements NotifySender {
	
	private final TimeUnit EXECUTOR_POOL_TIME_UNIT = TimeUnit.SECONDS;
	
	private ThreadPoolExecutor executor;
	
	private TopMQService service;
	private RunTimeMessageLogMonitor runTimeMessageLogMonitor;
	private TopNotifyExceptionLogMonitor topNotifyExceptionLogMonitor;
	
	public void setService(TopMQService service) {
		this.service = service;
	}

	public void init(){
		executor = new ThreadPoolExecutor(ThreadPoolProperty.getIntProperty(ThreadPoolProperty.EXECUTOR_POOL_CORE_SIZE)
										, ThreadPoolProperty.getIntProperty(ThreadPoolProperty.EXECUTOR_POOL_MAX_SIZE)
										, ThreadPoolProperty.getIntProperty(ThreadPoolProperty.EXECUTOR_POOL_ALIVE_TIME)
										, EXECUTOR_POOL_TIME_UNIT
										, new ArrayBlockingQueue<Runnable>(ThreadPoolProperty.getIntProperty(ThreadPoolProperty.EXECUTOR_POOL_QUEUE_SIZE))
										, new NotifyDiscardHandler());
	}

	public void send(Notify notify, boolean enableAsyncNotify) {
		if (enableAsyncNotify) {
			if(needNotify(notify)){
				long beginTime = System.currentTimeMillis();
				
				executor.execute(new NotifySendTask(notify, service, topNotifyExceptionLogMonitor));
				
				long serviceTime = System.currentTimeMillis() - beginTime;
				RunTimeMessageLog log = populateRunTimeLog(notify);
				log.setCallMQServiceTime(serviceTime);
				runTimeMessageLogMonitor.logSendMessage(log);
			} else {
				runTimeMessageLogMonitor.logDBStoreMessage(populateRunTimeLog(notify));
			}
		} else {
			runTimeMessageLogMonitor.logDBStoreMessage(populateRunTimeLog(notify));
		}
		
		
	}

	public void send(List<Notify> notifys, boolean enableAsyncNotify) {
		for(Notify notify : notifys){
			send(notify, enableAsyncNotify);
		}
	}

	private boolean needNotify(Notify notify){
		String isNotify = notify.getIsNotify();
		String notifyUrl = notify.getNotifyUrl();
		
		
		if(StringUtils.isBlank(notifyUrl) || StringUtils.isBlank(isNotify) || 
				!isNotified(notify.getCategory(), notify.getStatus(), isNotify)){
			return false;
		}
		
		return true;
	}
	
	private boolean isNotified(Integer category, Integer status, String isNotify) {
		Map<Integer, Long> sub = NotifyHelper.convertSubscribeCache(isNotify);

		if (sub != null && !sub.isEmpty() && sub.containsKey(category)) {
			if (0 == sub.get(category).longValue()) {
				return true;
			}

			long option = 1 << (status.longValue() - 1);
			if ((sub.get(category) & option) == option) {
				return true;
			}
		}

		return false;
	}

	public void setRunTimeMessageLogMonitor(RunTimeMessageLogMonitor runTimeMessageLogMonitor) {
		this.runTimeMessageLogMonitor = runTimeMessageLogMonitor;
	}

	public RunTimeMessageLogMonitor getRunTimeMessageLogMonitor() {
		return runTimeMessageLogMonitor;
	}
	
	public void setTopNotifyExceptionLogMonitor(
			TopNotifyExceptionLogMonitor topNotifyExceptionLogMonitor) {
		this.topNotifyExceptionLogMonitor = topNotifyExceptionLogMonitor;
	}

	public TopNotifyExceptionLogMonitor getTopNotifyExceptionLogMonitor() {
		return topNotifyExceptionLogMonitor;
	}

	private RunTimeMessageLog populateRunTimeLog(Notify notify) {
		RunTimeMessageLog log = new RunTimeMessageLog();
		
		log.setId(notify.getId());
		log.setCategory(notify.getCategory());
		log.setStatus(notify.getStatus());
		if(notify.getContent()!=null) {
			if(notify.getContent().containsKey(NotifyConstants.NUM_IID)) {
				log.setItemOrTradeId((Long)notify.getContent().get(NotifyConstants.NUM_IID));
			}
			if(notify.getContent().containsKey(NotifyConstants.TID)) {
				log.setItemOrTradeId((Long)notify.getContent().get(NotifyConstants.TID));
			}
			if(notify.getContent().containsKey(NotifyConstants.PAYMENT)) {
				log.setTradeFee((String)notify.getContent().get(NotifyConstants.PAYMENT));
			}
		}
		log.setUserId(notify.getUserId());
		log.setAppKey(notify.getAppKey());
		log.setCreated(notify.getModified().getTime());
		
		return log;
	}

}
