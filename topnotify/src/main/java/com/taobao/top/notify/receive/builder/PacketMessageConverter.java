package com.taobao.top.notify.receive.builder;

import org.apache.mina.common.ByteBuffer;

import com.taobao.common.cs.common.packet.Packet;
import com.taobao.common.cs.common.packet.PacketBuilder;
import com.taobao.hsf.notify.client.message.BytesMessage;
import com.taobao.hsf.notify.client.message.Message;
import com.taobao.hsf.notify.extend.TypedMessageConverter;

/**
 * @author yunshu
 *
 */
public class PacketMessageConverter extends TypedMessageConverter<Packet>{

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


    @Override
    protected Message toTypedMessage(Packet t)
    {
        BytesMessage msg = new BytesMessage();
        msg.setBody(t.toByteArray());
        return msg;
    }


    public Packet fromMessage(Message message)
    {
        BytesMessage msg = (BytesMessage) message;
        return PacketBuilder.getPacketBuilder().parsePacket(ByteBuffer.wrap(msg.getBody()));
    }
}
