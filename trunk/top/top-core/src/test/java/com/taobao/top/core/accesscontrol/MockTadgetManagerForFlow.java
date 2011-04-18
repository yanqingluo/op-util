package com.taobao.top.core.accesscontrol;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.taobao.top.tim.domain.FlowRuleV2DO;
import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.tim.service.TIMServiceException;
import com.taobao.top.timwrapper.manager.TadgetManager;

public class MockTadgetManagerForFlow extends TadgetManager {
	private Map<Long, FlowRuleV2DO> flowRules = new HashMap<Long, FlowRuleV2DO>();

	@Override
	public TinyApiDO getValidApiByApiName(String apiname)
			throws TIMServiceException {
		TinyApiDO api = new TinyApiDO();
		api.setId(100L);
		return api;
	}
	
	@Override
	public TinyAppDO getTadgetByKey(String appKey) throws TIMServiceException {
		TinyAppDO app = new TinyAppDO();
		app.setAppKey("200App");
		return app;
	}
	
	@Override
	public FlowRuleV2DO getFlowRule(Long flowRuleId) throws TIMServiceException {
		FlowRuleV2DO flr = flowRules.get(flowRuleId);
		return (flr != null) ? flr : defaultFlowRuleV2DO(flowRuleId);
		
	}

	private FlowRuleV2DO defaultFlowRuleV2DO(Long flowRuleId) {
		FlowRuleV2DO flowRuleTmp = new FlowRuleV2DO();
		flowRuleTmp.setId(flowRuleId);
		flowRuleTmp.setApiIdList(Arrays.asList(new Long[]{1000L}));
		flowRuleTmp.setBanDuration(0L);
		flowRuleTmp.setInterval(1L);
		flowRuleTmp.setMaxfreq(100L);
		flowRuleTmp.setType(FlowRuleV2DO.TYPE_APP_API_FREQ);
		return flowRuleTmp;
	}
	
	public void addFlowRuleV2DO(FlowRuleV2DO flowRule) {
		flowRules.put(flowRule.getId(), flowRule);
	}
	
}
