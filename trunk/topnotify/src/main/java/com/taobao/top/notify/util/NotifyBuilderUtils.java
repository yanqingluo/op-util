package com.taobao.top.notify.util;

import java.io.ByteArrayInputStream;
import java.util.Date;

import org.apache.mina.common.ByteBuffer;

import com.caucho.hessian.io.HessianInput;
import com.taobao.common.cs.common.packet.AbstractByteBufferSupport;
import com.taobao.hsf.notify.client.message.BytesMessage;
import com.taobao.hsf.notify.client.message.Message;
import com.taobao.notify.utils.UniqId;
import com.taobao.remoting.serialize.DefaultSerialization;

/**
 * 消息创建工具类。
 * 
 * @author fengsheng
 * @since 1.0, Dec 22, 2009
 */
public final class NotifyBuilderUtils {

	private static final String[] HEX_TABLE = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

	public static String logMessage(Message message, String errMsg) {
		StringBuilder result = new StringBuilder(errMsg);
		result.append("MsgId=").append(UniqId.getInstance().bytes2string(message.getMessageId()));
		result.append(", MsgType=").append(message.getMessageType());
		result.append(", HostName=").append(message.getPushlisherHostName());
		result.append(", GmtCreate=").append(DateUtils.formatDate(new Date(message.getGMTCreate())));
		return result.toString();
	}

	public static String bytesMessage2String(BytesMessage message) {
		byte[] body = message.getBody();
		StringBuilder content = new StringBuilder(100);

		try {
			AbstractByteBufferSupport.dumpData(ByteBuffer.wrap(body), content);
		} catch (Throwable t) {
			try {
				HessianInput hin = DefaultSerialization.createHessianInput(new ByteArrayInputStream(body));
				content.append(hin.readObject());
			} catch (Exception e) {
			}
		}

		if (content.toString().indexOf("NonPacketContent:") > 0) {
			content = new StringBuilder(100);
			try {
				HessianInput hin = DefaultSerialization.createHessianInput(new ByteArrayInputStream(body));
				content.append(hin.readObject());
			} catch (Throwable t) {
				return hex2String(body);
			}
		}

		return content.toString();
	}

	public static String hex2String(byte[] hex) {
		StringBuilder sb = new StringBuilder(hex.length);
		for (int i = 0; i < hex.length; i++) {
			int value = hex[i] < 0 ? 256 + hex[i] : hex[i];
			sb.append(HEX_TABLE[value >> 4]);
			sb.append(HEX_TABLE[value % 16]);
		}
		return sb.toString();
	}

}
