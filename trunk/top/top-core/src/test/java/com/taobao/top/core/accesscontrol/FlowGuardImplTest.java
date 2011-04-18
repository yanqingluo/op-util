package com.taobao.top.core.accesscontrol;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FlowGuardImplTest {
	private FlowGuardImpl flowManager;
	String appKey = "123456";
	Long apiId = 1234L;
	
	
	@Before
	public void setUp() throws Exception {
		flowManager = new FlowGuardImpl();
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testExceedFlowLimit() {
		final String checkKeyId = "10:100";
		flowManager.setBlackListManager(new MockBlackListManager() {
			@Override
			public Integer getBanEndTimeOfNewFlow(String keyId) {
				if(checkKeyId.equalsIgnoreCase(keyId)) {
					return (int) (System.currentTimeMillis() / 1000) + 300;					
				} else {
					return null;
				}
			}
		});
		Assert.assertTrue(flowManager.getResult("10", 100L, Collections.<FlowRule>emptySet()).canAccess());

		FlowRule flowRule = new FlowRule(
				checkKeyId, IFlowRulesConstructor.CHECK_INTERVAL_ONE_MINUTE,
				60L, null);
		
		Collection<FlowRule> flowRules = Arrays.asList(new FlowRule[]{flowRule});

		Assert.assertFalse(flowManager.getResult("10", 100L, flowRules).canAccess());
		
	}

}
