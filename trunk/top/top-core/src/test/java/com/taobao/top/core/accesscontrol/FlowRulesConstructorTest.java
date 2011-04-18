package com.taobao.top.core.accesscontrol;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.taobao.top.tim.domain.FlowRuleV2DO;
import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.domain.TinyAppDO;

public class FlowRulesConstructorTest {
	
	FlowRulesConstructor flowRulesConstructor;
	MockTadgetManagerForFlow tadgetManager;

	@Before
	public void setUp() throws Exception {
		flowRulesConstructor = new FlowRulesConstructor();
		tadgetManager = new MockTadgetManagerForFlow();
		flowRulesConstructor.setTadgetManager(tadgetManager);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAppInTest() {
		TinyAppDO app = new TinyAppDO();
		TinyApiDO api = null;
		
		app.setAppKey("200app");
		app.setAppStatus(TinyAppDO.APP_STATUS_TESTING);

		Collection<FlowRule> flowRules = flowRulesConstructor.constructRules(app, api); 
		Assert.assertEquals(1, flowRules.size());
		FlowRule rule = (flowRules.toArray(new FlowRule[]{}))[0];
		Assert.assertEquals(CheckKeyGen.genAppKey(app.getAppKey()), rule.getCheckKey());
		Assert.assertEquals(IFlowRulesConstructor.FLOW_LIMIT_FOR_APP_IN_TEST, rule.getThreshold());
		Assert.assertEquals(IFlowRulesConstructor.CHECK_INTERVAL_ONE_DAY, rule.getCheckInteval());
		Assert.assertTrue(rule.getBanDuration() >= 0
						&& rule.getBanDuration() <= IFlowRulesConstructor.CHECK_INTERVAL_ONE_DAY);	
	}
	
	@Test
	public void testApiRule() {
		TinyAppDO app = new TinyAppDO();
		TinyApiDO api = new TinyApiDO();
		
		app.setAppKey("200app");
		api.setId(100L);
		
		api.setAppAccessCountPerDay(1000L);
		Collection<FlowRule> flowRules = flowRulesConstructor.constructRules(app, api); 
		Assert.assertEquals(1, flowRules.size());
		FlowRule rule = (flowRules.toArray(new FlowRule[]{}))[0];
		Assert.assertEquals(CheckKeyGen.genAppApiKey(app.getAppKey(), api.getId()), rule.getCheckKey());
		Assert.assertEquals(1000L, rule.getThreshold());
		Assert.assertEquals(IFlowRulesConstructor.CHECK_INTERVAL_ONE_DAY, rule.getCheckInteval());
		Assert.assertTrue(rule.getBanDuration() >= 0
						&& rule.getBanDuration() <= IFlowRulesConstructor.CHECK_INTERVAL_ONE_DAY);
	}
	
	@Test
	public void testAppRule() {
		TinyAppDO app = new TinyAppDO();
		TinyApiDO api = new TinyApiDO();
		
		app.setAppKey("200app");
		api.setId(100L);
		
		FlowRuleV2DO flow10 = new FlowRuleV2DO();
		flow10.setId(10L);
		flow10.setApiIdList(Arrays.asList(new Long[]{100L}));
		flow10.setType(FlowRuleV2DO.TYPE_APP_API_FREQ);
		flow10.setInterval(1L);
		flow10.setMaxfreq(100L);
		flow10.setBanDuration(2L);
		
		tadgetManager.addFlowRuleV2DO(flow10);
		
		app.setFlowRuleIds(Arrays.asList(new Long[]{10L}));
		Collection<FlowRule> flowRules = flowRulesConstructor.constructRules(app, api); 
		Assert.assertEquals(1, flowRules.size());
		FlowRule rule = (flowRules.toArray(new FlowRule[]{}))[0];
		Assert.assertEquals(CheckKeyGen.genAppApiKey(app.getAppKey(), api.getId()), rule.getCheckKey());
		Assert.assertEquals(flow10.getMaxfreq().longValue(), rule.getThreshold());
		Assert.assertEquals((flow10.getInterval() * 60), rule.getCheckInteval());
		Assert.assertTrue(rule.getBanDuration() >= 2 * 60);
	}
	
	@Test
	public void testApiRules() {
		TinyAppDO app = new TinyAppDO();
		TinyApiDO api = new TinyApiDO();
		
		app.setAppKey("200app");
		api.setId(100L);
		
		set4ApiRules(api);
		Collection<FlowRule> flowRules = flowRulesConstructor.constructRules(app, api); 
		Assert.assertEquals(4, flowRules.size());
	}

	private void set4ApiRules(TinyApiDO api) {
		api.setAccessCountPerDay(1000L);
		api.setAppAccessCountPerDay(100L);
		api.setAccessCountPerMin(10L);
		api.setAppAccessCountPerMin(1L);
	}
	
	@Test
	public void testAppRules() {
		TinyAppDO app = new TinyAppDO();
		TinyApiDO api = new TinyApiDO();
		app.setAppKey("200app");
		api.setId(100L);
		
		add3appRules(app, api);
		Collection<FlowRule> flowRules = flowRulesConstructor.constructRules(app, api); 
		Assert.assertEquals(3, flowRules.size());
	}

	@Test
	public void TestAppAndApiRules() {
		TinyAppDO app = new TinyAppDO();
		TinyApiDO api = new TinyApiDO();
		app.setAppKey("200app");
		api.setId(100L);
		
		add3appRules(app, api);
		set4ApiRules(api);
		Collection<FlowRule> flowRules = flowRulesConstructor.constructRules(app, api); 
		//6 is because the app+api of one minute is duplicate, use the app one.
		Assert.assertEquals(6, flowRules.size());
		for(FlowRule flowRule : flowRules) {
			if(flowRule.getCheckInteval() == IFlowRulesConstructor.CHECK_INTERVAL_ONE_MINUTE
					&& flowRule.getCheckKey().equals(CheckKeyGen.genAppApiKey(app.getAppKey(), api.getId()))) {
				//100L is from the app rule
				Assert.assertEquals(100L, flowRule.getThreshold());
			}
		}
	}
	
	private void add3appRules(TinyAppDO app, TinyApiDO api) {

		
		FlowRuleV2DO flow10 = new FlowRuleV2DO();
		flow10.setId(10L);
		flow10.setApiIdList(Arrays.asList(new Long[]{100L}));
		flow10.setType(FlowRuleV2DO.TYPE_APP_API_FREQ);
		flow10.setInterval(1L);
		flow10.setMaxfreq(100L);
		flow10.setBanDuration(2L);
		
		FlowRuleV2DO flow20 = new FlowRuleV2DO();
		flow20.setId(20L);
		flow20.setApiIdList(Arrays.asList(new Long[]{100L}));
		flow20.setType(FlowRuleV2DO.TYPE_APP_API_FREQ);
		flow20.setInterval(10L);
		flow20.setMaxfreq(100L);
		flow20.setBanDuration(2L);
		
		FlowRuleV2DO flow30 = new FlowRuleV2DO();
		flow30.setId(30L);
		flow30.setType(FlowRuleV2DO.TYPE_APP_FREQ);
		flow30.setInterval(10L);
		flow30.setMaxfreq(100L);
		flow30.setBanDuration(2L);
		
		tadgetManager.addFlowRuleV2DO(flow10);
		tadgetManager.addFlowRuleV2DO(flow20);
		tadgetManager.addFlowRuleV2DO(flow30);
		
		app.setFlowRuleIds(Arrays.asList(new Long[]{10L, 20L, 30L}));
	}
	


}
