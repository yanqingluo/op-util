package com.taobao.top.notify.send;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.taobao.top.notify.domain.Notify;
import com.taobao.top.notify.log.TopNotifyExceptionLogMonitor;
import com.taobao.top.notify.log.impl.TopNotifyExceptionLogMonitorImpl;

public class NotifySendTaskTest {
	
	private TopNotifyExceptionLogMonitor exceptionLogMonitor;
	
	@Before
	public void initTest() {
		exceptionLogMonitor = new TopNotifyExceptionLogMonitorImpl(); 
		
	}
	
	@Test
	public void testNeedNotify(){
		
		Notify notify = mockNotify();
		NotifySendTask task = new NotifySendTask(notify, null ,exceptionLogMonitor);
		Method method;
		try {
			method = task.getClass().getDeclaredMethod("needNotify", new Class[0]);
			method.setAccessible(true);
			System.out.println(method.invoke(task));
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@Test
	public void testBuildMsgBody(){
		Notify notify = mockNotify();
		NotifySendTask task = new NotifySendTask(notify, null, exceptionLogMonitor);
		Method method;
		try {
			method = task.getClass().getDeclaredMethod("buildMsgBody", new Class[0]);
			method.setAccessible(true);
			System.out.println(method.invoke(task));
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private Notify mockNotify(){
		Notify notify = new Notify();
		notify.setCategory(3);
		notify.setStatus(2);
		notify.setContent(mockContent());
		notify.setSubscriptions("1:3;2:0;3:1");
		notify.setIsNotify("1:3;2:0;3:5");
		return notify;
	}
	
	private Map<String, Object> mockContent(){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("tid", 123);
		result.put("payment", 108);
		return result;
	}

}
