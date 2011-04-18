package com.taobao.top.notify.receive.builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.taobao.hsf.notify.client.message.BytesMessage;
import com.taobao.hsf.notify.client.message.Message;

import com.taobao.hsf.notify.extend.TypedMessageConverter;

public class ObjectMessageConverter extends TypedMessageConverter<Object> {
	private static final Logger logger = LoggerFactory.getLogger(ObjectMessageConverter.class);

	private String messageTopic;
    private String messageType;

    @Override
    public String getMessageTopic()
    {
        return messageTopic;
    }

    @Override
    public String getMessageType()
    {
        return messageType;
    }

    public void setMessageTopic(String messageTopic)
    {
        this.messageTopic = messageTopic;
    }

    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }

	/**
	 * 将message转成Object
	 */
	public Object fromMessage(Message message) {
		BytesMessage byteMessage = (BytesMessage) message;
		byte[] messagebody = byteMessage.getBody();
		try {
			return getObjectFromBytes(messagebody);
		} catch (IOException e) {
			logger.error("ObjecMessageConverter IOException：", e);
		} catch (ClassNotFoundException e) {
			logger.error("ObjecMessageConverter ClassNotFoundException：", e);
		}
		return null;
	}

	/**
	 * 将Object转成Message
	 */
	@Override
	protected Message toTypedMessage(Object t) {
		return null;
	}

	public static Object getObjectFromBytes(byte[] objBytes) throws IOException,
			ClassNotFoundException {
		if (objBytes == null || objBytes.length == 0) {
			return null;
		}
		ByteArrayInputStream bi = new ByteArrayInputStream(objBytes);
		ObjectInputStream oi = new ObjectInputStream(bi);
		Object outObject = oi.readObject();
		return outObject;
	}

	public static byte[] getBytesFromObject(Serializable obj)
			throws IOException {
		if (obj == null) {
			return null;
		}
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		oo.writeObject(obj);
		return bo.toByteArray();
	}
}
