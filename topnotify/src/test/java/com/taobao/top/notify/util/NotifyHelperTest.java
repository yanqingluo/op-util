package com.taobao.top.notify.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author fengsheng
 * @since 1.0, Jan 29, 2010
 */
public class NotifyHelperTest {

	@Test
	public void convertSubscribeCache() {
		String subs = "1:336;3:196";
		Map<Integer, Long> result = NotifyHelper.convertSubscribeCache(subs);
		Assert.assertEquals(2, result.size());
	}

	@Test
	public void convertSubscribeInput() {
		Map<String, List<String>> subs = new HashMap<String, List<String>>();
		List<String> types1 = new ArrayList<String>();
		types1.add("TradeBuyerPay");
		types1.add("TradePartlyConfirmPay");
		types1.add("TradeDelayConfirmPay");
		subs.put("trade", types1);

		List<String> types2 = new ArrayList<String>();
		types2.add("ItemDownshelf");
		types2.add("ItemRecommendDelete");
		types2.add("ItemRecommendAdd");
		subs.put("item", types2);
		String result = NotifyHelper.convertSubscribeInput(subs);
		NotifyHelper.convertSubscribeCache(result);
		System.out.println(result);
	}

	@Test
	public void convertSubscribeOutput() {
		String subs = "1:337;2:3;3:196";
		Map<String, List<String>> result = NotifyHelper.convertSubscribeOutput(subs);
		System.out.println(result);
	}

	@Test
	public void convertSubscribeAllOutput() {
		String subs = "1:0";
		Map<String, List<String>> result = NotifyHelper.convertSubscribeOutput(subs);
		System.out.println(result);
	}

}
