package com.taobao.top.core.accesscontrol;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.domain.TinyAppDO;

public class SyncCallRecorderTest {
	private SyncCallRecorder recorder;
	private MockBlackListManager blackListManager = new MockBlackListManager();
	
	@Before
	public void setUp() throws Exception {
		recorder = new SyncCallRecorder();
		recorder.setCallLimitManager(new MockCallLimitManager());
		recorder.setBlackListManager(blackListManager);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRecord() {
		TinyAppDO app = new TinyAppDO();
		app.setAppKey("appTest");
		TinyApiDO api = new TinyApiDO();
		api.setId(1000L);
		
		String checkKey = CheckKeyGen.genAppKey(app.getAppKey()); 
		
		FlowRule flowRule = new FlowRule(
				checkKey, IFlowRulesConstructor.CHECK_INTERVAL_ONE_MINUTE,
				60L, null);
		
		Collection<FlowRule> flowRules = Arrays.asList(new FlowRule[]{flowRule});
		for(int i = 1; i <= flowRule.getThreshold(); i++) {
			Assert.assertNull(blackListManager.getBanEndTimeOfNewFlow(checkKey));
			recorder.recordAndCanCall(flowRules);
		}
		
		recorder.recordAndCanCall(flowRules);
		Assert.assertTrue(blackListManager.getBanEndTimeOfNewFlow(checkKey) > System.currentTimeMillis() / 1000);
		
	}

}
