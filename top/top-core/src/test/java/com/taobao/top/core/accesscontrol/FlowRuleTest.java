package com.taobao.top.core.accesscontrol;

import java.util.Calendar;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FlowRuleTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFlowRuleOneMinute() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 1);
		calendar.set(Calendar.SECOND, 0);
		long secondToMinuteEnd = (int)((calendar.getTimeInMillis() - System.currentTimeMillis())/1000);
		FlowRule flowRule = new FlowRule(
				"aa", 
				IFlowRulesConstructor.CHECK_INTERVAL_ONE_MINUTE, 
				200L,
				null);
		Assert.assertTrue(Math.abs(flowRule.getBanDuration() - secondToMinuteEnd) <= 1);	

	}
	
	@Test
	public void testFlowRuleOneDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		long secondToDayEnd = (int)((calendar.getTimeInMillis() - System.currentTimeMillis())/1000);
		FlowRule flowRule = new FlowRule(
				"aa", 
				IFlowRulesConstructor.CHECK_INTERVAL_ONE_DAY, 
				200L,
				null);
		Assert.assertTrue(Math.abs(flowRule.getBanDuration() - secondToDayEnd) <= 1);	

	}
	
	@Test
	public void testFlowRuleOneHour() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		long secondToHourEnd = (int)((calendar.getTimeInMillis() - System.currentTimeMillis())/1000);

		FlowRule flowRule = new FlowRule(
				"aa", 
				3600, 
				200L,
				null);
		Assert.assertTrue(Math.abs(flowRule.getBanDuration() - secondToHourEnd) <= 1);	

	}
	
	@Test
	public void testFlowContrlUnit() {
		Calendar calendarDayBegin = Calendar.getInstance();
		calendarDayBegin.set(Calendar.HOUR_OF_DAY, 0);
		calendarDayBegin.set(Calendar.MINUTE, 0);
		calendarDayBegin.set(Calendar.SECOND, 0);
		
		Calendar calendarDayEnd = (Calendar) calendarDayBegin.clone();
		calendarDayEnd.add(Calendar.DAY_OF_MONTH, 1);
		calendarDayEnd.add(Calendar.SECOND, -1);
		
		int timeBegin = (int) (calendarDayBegin.getTimeInMillis()/1000);
		int timeEnd = (int) (calendarDayEnd.getTimeInMillis()/1000);
		int timeUnit = (timeBegin + 3600 * 8)/24/3600;
		for(int i = timeBegin; i < timeEnd; i+=100) {
			Assert.assertEquals(timeUnit, (i + 3600 * 8)/3600/24);
		}
	}

}
