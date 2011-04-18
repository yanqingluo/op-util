package com.taobao.top.notify.send;

import com.taobao.top.mq.MQException;
import com.taobao.top.mq.MessageStatus;
import com.taobao.top.mq.TopMQService;
import com.taobao.top.mq.TopMessage;

public class TopMQServiceMock implements TopMQService {

	public void put(TopMessage message) throws MQException {
		// TODO Auto-generated method stub

	}

	public MessageStatus getMessageStatus(String msgId) throws MQException {
		// TODO Auto-generated method stub
		return null;
	}

	public TopMessage getTopMessage(String partnerId, String msgId)
			throws MQException {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getMessageStatus(String arg0, String arg1)
			throws MQException {
		// TODO Auto-generated method stub
		return null;
	}

}
