package com.taobao.top.notify.send;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.taobao.top.mq.TopMQService;
import com.taobao.top.mq.TopMessage;
import com.taobao.top.notify.domain.Notify;
import com.taobao.top.notify.domain.NotifyEnum;
import com.taobao.top.notify.log.TopNotifyExceptionLog;
import com.taobao.top.notify.log.TopNotifyExceptionLogMonitor;
import com.taobao.top.notify.util.MapUtil;
import com.taobao.top.notify.util.NotifyConstants;
import com.taobao.top.notify.util.TopMQConstants;

public class NotifySendTask implements Runnable {
	private static final Log log = LogFactory.getLog(NotifySendTask.class);
	
	private Notify notify;
	
	private TopMQService service;
	private TopNotifyExceptionLogMonitor exceptionLogMonitor;

	public NotifySendTask(Notify notify, TopMQService service, TopNotifyExceptionLogMonitor logMonitor){
		this.notify = notify;
		this.service = service;
		this.exceptionLogMonitor = logMonitor;
	}
	
	public Notify getNotify(){
		return notify;
	}

	public void run() {
		TopMessage msg = new TopMessage();
		msg.setPartnerId(TopMQConstants.TOPNOTIFY_PARTNER_ID);
		msg.setMsgId(notify.getId().toString());
		msg.setAppkey(notify.getAppKey());
		msg.setMsgBody(buildMsgBody());
		msg.setMethod("incrementmessage");
		msg.setVersion("2.0");
		msg.setFormat("json");
		//增加mq tag分类传入，
		msg.setTag(NotifyEnum.getInstance(notify.getCategory(), notify.getStatus()).getMessage());
		try {
			service.put(msg);
		} catch (Exception e) {
			log.error("消息发送至TopMQ失败", e);
			exceptionLogMonitor.logSendMsgException(populateTopNotifyExceptionLog(notify,e));
		}
	}
	
	
	private TopNotifyExceptionLog populateTopNotifyExceptionLog(Notify notify, Exception e) {
		TopNotifyExceptionLog log = new TopNotifyExceptionLog();
		log.setNotifyId(notify.getId());
		log.setExceptionMsg(e.getMessage());
		
		return log;
	}
	
	private String buildMsgBody(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String status = NotifyEnum.getInstance(notify.getCategory(), notify.getStatus()).getMessage();
		String topic = NotifyEnum.getInstance(notify.getCategory(), 0).getMessage();
		Map<String, Object> content = MapUtil.normalizeMap(notify.getContent());
		content.put(NotifyConstants.TOPIC, topic);
		content.put(NotifyConstants.STATUS, status);
		//modified时间加入发送消息中，之前没有此消息 moling 2011-01-06
		content.put(NotifyConstants.MODIFIED, sdf.format(notify.getModified()));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("notify_" + topic, new JSONObject(content));
		return new JSONObject(resultMap).toString();
	}
}
