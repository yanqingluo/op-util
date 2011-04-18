package com.taobao.top.notify.send;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.taobao.top.notify.TestBase;
import com.taobao.top.notify.domain.Notify;
import com.taobao.top.notify.send.impl.NotifySenderImpl;

public class NotifySenderTest extends TestBase{
	
	private NotifySender sender = (NotifySender)ctx.getBean("notifySender");
	
	//private NotifyListener listener = (NotifyListener)ctx.getBean("notifyListener");
	
	@Test
	public void testSend(){
		List<Notify> notifys = createNotifys();
//		NotifySenderImpl sender = new NotifySenderImpl();
//		sender.init();
		((NotifySenderImpl)sender).setService(new TopMQServiceMock());
		long begin = System.currentTimeMillis();
		sender.send(notifys, true);
//		listener.getSender().send(notifys);
		System.out.println(System.currentTimeMillis() - begin);
	}
	 
	@Test
	public void testInit(){
		((NotifySenderImpl)sender).init();
	}
	
	private List<Notify> createNotifys(){
		List<Notify> result = new ArrayList<Notify>();
		for(int i = 0; i < 650; i++){
			Notify notify = new Notify();
			notify.setId(123L);
			notify.setCategory(3);
			notify.setStatus(2);
			notify.setContent(mockContent());
			notify.setSubscriptions("1:3;2:0;3:1");
			notify.setIsNotify("1:3;2:0;3:5");
			notify.setNotifyUrl("http");
			notify.setAppKey("App" + i);
			result.add(notify);
		}
		return result;
	}
	
	@Test
	public void testNeedNoitfy(){
		Method method;
		try {
			method = sender.getClass().getDeclaredMethod("needNotify", Notify.class);
			method.setAccessible(true);
			System.out.println(method.invoke(sender, mockNotify()));
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	protected Notify mockNotify(){
		Notify notify = new Notify();
		notify.setId(123L);
		notify.setCategory(3);
		notify.setStatus(2);
		notify.setContent(mockContent());
		notify.setSubscriptions("1:3;2:0;3:1");
		notify.setIsNotify("1:3;2:0;3:5");
		notify.setNotifyUrl("http");
		return notify;
	}

	private Map<String, Object> mockContent(){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("tid", 123L);
		result.put("payment", (double)108);
		return result;
	}

	public static void main(String[] args){
		NotifySenderTest test = new NotifySenderTest();
		long begin = System.currentTimeMillis();
		((NotifySenderImpl)test.sender).setService(new TopMQServiceMock());
		test.sender.send(test.createNotifys(), true);
		System.out.println(System.currentTimeMillis() - begin);
	}

}
