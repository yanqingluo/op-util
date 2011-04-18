package com.taobao.top.notify.util;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.taobao.top.notify.domain.NotifyEnum;

public class NotifyEnumTest {

	@Test
	public void getTradeMessages() {
		List<String> msgs = NotifyEnum.getMessages(1, 0L);
		Assert.assertEquals(15, msgs.size());
	}

	@Test
	public void getRefundMessages() {
		List<String> msgs = NotifyEnum.getMessages(2, 0L);
		Assert.assertEquals(10, msgs.size());
	}

	@Test
	public void getItemMessages() {
		List<String> msgs = NotifyEnum.getMessages(3, 0L);
		Assert.assertEquals(9, msgs.size());
	}

}
