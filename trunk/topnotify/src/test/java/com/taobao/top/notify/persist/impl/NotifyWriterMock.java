package com.taobao.top.notify.persist.impl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.taobao.top.notify.NotifyException;
import com.taobao.top.notify.domain.Notify;
import com.taobao.top.notify.persist.NotifyWriter;

/**
 * 
 * @author moling
 * @since 1.0, 2010-1-29
 */
public class NotifyWriterMock implements NotifyWriter {
	
	List<Notify> notifys;
	
	public List<Notify> getNotifys() {
		return this.notifys;
	}

	public void write(Notify notify) throws NotifyException {
		if (null == notifys) {
			notifys = new ArrayList<Notify>();
		}
		notifys.add(notify);
		
	}

	public void write(List<Notify> notifies) throws NotifyException {
		for (Notify notify : notifies) {
			if (null == notifys) {
				notifys = new ArrayList<Notify>();
			}
			notify.setId(8L);
			notifys.add(notify);
		}
		
	}

	public void init() throws UnknownHostException {
		// TODO Auto-generated method stub
		
	}

}
