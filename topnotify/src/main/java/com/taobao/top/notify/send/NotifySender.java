package com.taobao.top.notify.send;

import java.util.List;

import com.taobao.top.notify.domain.Notify;

public interface NotifySender {
	
	public void send(Notify notify, boolean enableAsyncNotify);
	
	public void send(List<Notify> notifys, boolean enableAsyncNotify);

}
