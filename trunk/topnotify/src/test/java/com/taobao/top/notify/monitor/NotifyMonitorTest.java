package com.taobao.top.notify.monitor;

import java.util.Random;

import org.junit.Test;

/**
 * 
 * @author fengsheng
 * @since 1.0, Jan 4, 2010
 */
public class NotifyMonitorTest {

	@Test
	public void log() throws Exception {
		ReceiveLog log = new ReceiveLog();
		for (int i = 0; i < 100; i++) {
			log.setResponseTime(new Random().nextInt(500));
			log.setTopic("topic" + i % 3);
			log.setMsgType("msg-type" + i % 10);
			log.setPersisted(i % 2 == 0 ? true : false);
			NotifyMonitor.log(log);
			Thread.sleep(new Random().nextInt(30));
		}
	}

}
