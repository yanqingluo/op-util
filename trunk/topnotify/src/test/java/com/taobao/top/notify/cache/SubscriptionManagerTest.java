package com.taobao.top.notify.cache;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.taobao.top.notify.TestBase;
import com.taobao.top.notify.domain.NotifyEnum;
import com.taobao.top.tim.service.TIMServiceException;

/**
 * 
 * @author moling
 * @since 1.0, 2009-12-17
 */
public class SubscriptionManagerTest extends TestBase {

	private SubscriptionManager sub = (SubscriptionManager) ctx.getBean("subscriptionManager");

	@Test
	public void getSubscriptionTest() {
		try {
			assertEquals(2, sub.getSubscription(111111L, NotifyEnum.TRADE.getCategory(), 200, 0, 0L).size());
			assertEquals(null, sub.getSubscription(222222L, NotifyEnum.TRADE.getCategory(), 200, 0, 0L));
			assertEquals(2, sub.getSubscription(333333L, NotifyEnum.TRADE.getCategory(), 200, 0, 0L).size());
			assertEquals(true, sub.getSubscription(111111L, NotifyEnum.TRADE.getCategory(), 200, 0, 0L).contains("appKey1"));
			assertEquals(true, sub.getSubscription(111111L, NotifyEnum.TRADE.getCategory(), 200, 0, 0L).contains("appKey2"));
		} catch (TIMServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
