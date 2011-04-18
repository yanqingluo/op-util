package com.taobao.top.notify.receive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.taobao.top.notify.TestBase;

/**
 * 
 * @author moling
 * @since 1.0, 2009-12-14
 */
public class NotifySubscriberTest extends TestBase {

	private NotifySubscriber sub = (NotifySubscriber) ctx.getBean("notifySubscriber");

	@Test
	public void testBuildSubscribeMsg() {
		sub.fillSubscribeType();

		assertEquals(12, sub.getBizTypes().size());
		assertEquals(2, sub.getTradeMessages().size());
		assertEquals(1, sub.getItemMessages().size());
		assertEquals(1, sub.getRefundMessages().size());
		assertEquals(1, sub.getTradePlusMessages().size());
		assertEquals(1, sub.getVasMessages().size());

		assertEquals(1, sub.getTradeMessages().get("-trade-test-biztype").getStatus());
		assertEquals(true, sub.getTradeMessages().get("-trade-test-biztype").isBizTypeAble());
		assertEquals(2, sub.getTradeMessages().get("trade-test-nobiztype").getStatus());
		assertEquals(false, sub.getTradeMessages().get("trade-test-nobiztype").isBizTypeAble());
		assertEquals(3, sub.getRefundMessages().get("refund-test").getStatus());
		assertEquals(false, sub.getRefundMessages().get("refund-test").isBizTypeAble());
		assertEquals(4, sub.getTradePlusMessages().get("-tradeplust-test").getStatus());
		assertEquals(true, sub.getTradePlusMessages().get("-tradeplust-test").isBizTypeAble());
		assertEquals(5, sub.getItemMessages().get("item-test").getStatus());
		assertEquals(false, sub.getItemMessages().get("item-test").isBizTypeAble());
		assertEquals(1, sub.getVasMessages().get("vas-test").getStatus());
		assertEquals(false, sub.getVasMessages().get("vas-test").isBizTypeAble());
	}

}
