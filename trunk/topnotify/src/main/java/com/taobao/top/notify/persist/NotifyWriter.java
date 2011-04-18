package com.taobao.top.notify.persist;

import java.net.UnknownHostException;
import java.util.List;

import com.taobao.top.notify.NotifyException;
import com.taobao.top.notify.domain.Notify;

/**
 * 消息持久器。
 * 
 * @author fengsheng
 * @since 1.0, Dec 14, 2009
 */
public interface NotifyWriter {

	/**
	 * 把消息写入到持久设备。
	 */
	public void write(Notify notify) throws NotifyException;

	/**
	 * 把消息写入到持久设备。
	 */
	public void write(List<Notify> notifys) throws NotifyException;

	/**
	 * 初始化计数器和ip值
	 */
	public void init() throws UnknownHostException;
}
