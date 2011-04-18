package com.taobao.top.core.accesscontrol;

import java.util.Collection;

import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.domain.TinyAppDO;

/**
 * Construct the flowrules of the app and api
 * @author <a href="mailto:huaisu@taobao.com">huaisu</a>
 * @since 2010-7-7
 */
public interface IFlowRulesConstructor {

	static final int CHECK_INTERVAL_ONE_MINUTE = 60;
	static final int CHECK_INTERVAL_ONE_DAY = 3600 * 24;
	
	static final long FLOW_LIMIT_FOR_APP_IN_TEST = 5000;

	public Collection<FlowRule> constructRules(TinyAppDO appDO, TinyApiDO api);

}
