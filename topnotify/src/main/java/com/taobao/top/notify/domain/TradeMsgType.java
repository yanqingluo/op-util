package com.taobao.top.notify.domain;

import com.taobao.tc.message.msgtype.MsgType;

/**
 * 
 * @author moling
 * @since 1.0, 2009-12-15
 */
public class TradeMsgType extends MsgType {

	public static final String msgTypeSuffix = "-top-notify-mock-msgType";

	public TradeMsgType(Integer bizType) {
		super(bizType == null ? 0 : bizType.intValue());
	}

	public String getMsgTypeSuffix() {
		return msgTypeSuffix;
	}

}
