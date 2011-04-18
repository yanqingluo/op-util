package com.taobao.top.core.framework.pipe;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.accesscontrol.FlowControlResult;
import com.taobao.top.core.accesscontrol.FlowGuard;
import com.taobao.top.core.accesscontrol.FlowRule;
import com.taobao.top.core.accesscontrol.MockCallRecorder;
import com.taobao.top.core.accesscontrol.MockFlowRulesConstructor;
import com.taobao.top.core.accesscontrol.MockTadgetManagerForFlow;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
@Ignore
public class NewFlowControlPipeTest {
	NewFlowControlPipe pipe;
	MockFlowRulesConstructor ruleConstuctor;
	boolean testBan;

	@Before
	public void setUp() throws Exception {
		pipe = new NewFlowControlPipe();
		pipe.setTadgetManager(new MockTadgetManagerForFlow());
		pipe.setCallRecorder(new MockCallRecorder());
		ruleConstuctor = new MockFlowRulesConstructor();
		pipe.setFlowRulesConstructor(ruleConstuctor);
		pipe.setFlowGuard(new FlowGuard() {
			
			@Override
			public FlowControlResult getResult(String appKey, Long apiId, Collection<FlowRule> flowRules) {
				// TODO Auto-generated method stub
				return testBan ? new FlowControlResult(
						"10:10", 200)
						: FlowControlResult.NOT_FORBIDDEN;
			}
		});
	}

	@After
	public void tearDown() throws Exception {
	}

	@Ignore
	public void testDoPipe() {
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		
		TopPipeInput input = new TopPipeInput(req, resp, new MockTadgetManagerForFlow());
		TopPipeResult result = new TopPipeResult();
		input.setApiName("aaaa");
		testBan = false;
		pipe.doPipe(input, result);
		Assert.assertEquals(null, result.getErrorCode());
		testBan = true;
		pipe.doPipe(input, result);
		Assert.assertEquals(ErrorCode.CALL_LIMITED_APP_API, result.getErrorCode());
		
		FlowRule rule = new FlowRule("test:test", 60, 0L, null);
		ruleConstuctor.setFlowRule(Arrays.asList(new FlowRule[]{rule}));
		pipe.doPipe(input, result);
		Assert.assertEquals(ErrorCode.INSUFFICIENT_ISV_PERMISSIONS, result.getErrorCode());		
	}

}
