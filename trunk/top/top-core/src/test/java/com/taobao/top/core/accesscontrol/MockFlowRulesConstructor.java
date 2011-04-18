package com.taobao.top.core.accesscontrol;

import java.util.ArrayList;
import java.util.Collection;

import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.domain.TinyAppDO;

public class MockFlowRulesConstructor implements IFlowRulesConstructor {
	private Collection<FlowRule> flowRules;
	
	@Override
	public Collection<FlowRule> constructRules(TinyAppDO app, TinyApiDO api) {
		return flowRules != null ? flowRules : new ArrayList<FlowRule>();
	}
	
	public void setFlowRule(Collection<FlowRule> flowRules) {
		this.flowRules = flowRules;
	}

}
