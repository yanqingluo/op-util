package com.taobao.top.notify.receive;

import java.util.List;

import com.taobao.hsf.notify.client.message.Message;
import com.taobao.top.notify.domain.Notify;

/**
 * 消息构造器。
 * 
 * @author fengsheng
 * @since 1.0, Dec 14, 2009
 */
public interface NotifyBuilder {
	/**
	 * 消息转化填充。
	 */
	public List<Notify> build(Message message, Integer bizType, Integer status);
}
