package com.taobao.top.notify.log;

import com.taobao.notify.utils.UniqId;
import com.taobao.top.xbox.asynlog.data.TopLog;

/**
 * <b>TopNotify 异常日志</b>
 * @author <a href="mailto:yibiao@taobao.com">yibiao</a>
 * <p>日志类型type：</p>
 * <p>0->消息解析异常</p>
 * <p>1->读取订阅关系异常</p>
 * <p>2->存储消息异常</p>
 * <p>3->发送消息异常</p>
 */
public class TopNotifyExceptionLog extends TopLog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6377248427127873544L;
	
	public static final String PARSER_EXCEPTION = "^_^parser-error^_^";
	public static final String READSUB_EXCEPTION = "^_^readsub-error^_^";
	public static final String STORE_EXCEPTION = "^_^store-error^_^";
	public static final String SEND_EXCEPTION = "^_^send-error^_^";
	
	
	public TopNotifyExceptionLog() {
		setClassName("topNotifyExceptionLog");
		setLogTime(System.currentTimeMillis());
	}
	
	private String type;
	
	private byte[] msgId;
	
	private Long notifyId;
	
	private String exceptionMsg;
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		append(sb, UniqId.getInstance().bytes2string(msgId), true);
		append(sb, notifyId, true);
		append(sb, type, true);
		append(sb, exceptionMsg, false);
		
		return sb.toString();
	}
	
	private void append(StringBuffer sb, Object content, boolean needAppend) {
		if (null == sb) {
			return;
		}
		
		if (null != content) {
			sb.append(content);
		}
		
		if (needAppend) {
			sb.append(",");
		}
		
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setMsgId(byte[] msgId) {
		this.msgId = msgId;
	}

	public byte[] getMsgId() {
		return msgId;
	}

	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}

	public String getExceptionMsg() {
		return exceptionMsg;
	}

	public void setNotifyId(Long notifyId) {
		this.notifyId = notifyId;
	}

	public Long getNotifyId() {
		return notifyId;
	}

}
