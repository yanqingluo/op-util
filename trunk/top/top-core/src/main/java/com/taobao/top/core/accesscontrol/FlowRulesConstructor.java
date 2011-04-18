package com.taobao.top.core.accesscontrol;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.tim.domain.FlowRuleV2DO;
import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.timwrapper.manager.TadgetManager;
import com.taobao.util.CollectionUtil;

public class FlowRulesConstructor implements IFlowRulesConstructor {

	private static final Log logger = LogFactory.getLog(FlowRulesConstructor.class);
	private static final int MAX_FLOW_RULE = 20;
	private static final int CHECK_INTERVAL_ONE_MINUTE = 60;
	private static final int CHECK_INTERVAL_ONE_DAY = 3600 * 24;
	
	private static final long TESTING_APP_FLOW_LIMIT = 5000;

	private TadgetManager tadgetManager;
	
	public void setTadgetManager(TadgetManager tadgetManager) {
		this.tadgetManager = tadgetManager;
	}
	
	/* 
	 * rules are constructed from two ways.
	 * First is from the flowRules got from appDO,
	 * Second is from the api's definition.
	 * 
	 * Use Set to fill the flowRules.
	 * The reason is:
	 * the app_api rules in api's definition are the default one.
	 * The default means if such rule with same intevel has already 
	 * be defined in flowRules from appDO,
	 * then the ones in api's definition will be ignored, 
	 * or they should take effect. So, the order of construct rules from 
	 * should not be broken: construct from app's flowRules first, then 
	 * from the api's definition.
	 * 
	 * 
	 * @see FlowRule
	 * @see TinyApiDO#appAccessCountPerMin
	 * @see TinyApiDO#appAccessCountPerDay
	 * @see getFlowRulesFromApi
	 */
	public Collection<FlowRule> constructRules(TinyAppDO app, TinyApiDO api) {
		
		// 如果是正式环境下测试状态，则每天固定访问5000次
		//FIXME: only APP_STATUS_TESTING?
		if (TinyAppDO.APP_STATUS_TESTING.equals(app.getAppStatus())) {
			return getRulesForTestingApp(app);
		}
		
		Set<FlowRule> flowRules = new HashSet<FlowRule>();

		List<Long> flowRuleIds = app.getFlowRuleIds();
		if(CollectionUtil.isNotEmpty(flowRuleIds)) {
			fillFlowRulesFromIds(flowRules, flowRuleIds, app, api);			
		}
		
		if(api != null) {
			fillFlowRulesFromApi(flowRules, api, app.getAppKey());			
		}
		if(flowRules.size() > MAX_FLOW_RULE) {
			logger.warn("flowRules count " + flowRules.size() + ", app is "
					+ app.getAppKey() + ", api is " + api == null ? "null" : api.getId());
			flowRules.clear();
		}
		return flowRules;
	}

	private Collection<FlowRule> getRulesForTestingApp(TinyAppDO app) {
		FlowRule flowRule = new FlowRule(
				CheckKeyGen.genAppKey(app.getAppKey()), CHECK_INTERVAL_ONE_DAY,
				TESTING_APP_FLOW_LIMIT, null);
		return Arrays.asList(new FlowRule[]{flowRule});
	}

	private void fillFlowRulesFromIds(Set<FlowRule> flowRules, List<Long> flowRuleIds, TinyAppDO appDO,
			TinyApiDO api) {
		for(Long flowRuleId : flowRuleIds) {
			try {
				FlowRuleV2DO flowRuleDO = tadgetManager.getFlowRule(flowRuleId);
				if(flowRuleDO == null || flowRuleDO.getInterval() == null
					|| flowRuleDO.getMaxfreq() == null || flowRuleDO.getMaxfreq() < 0) {
					logger.warn("FlowRuleV2DO " + flowRuleId + " null or inteval null or " +
							"maxFreq < 0");
					continue;
				}
				String key = genKey(flowRuleDO, appDO.getAppKey(),
						(api != null) ? api.getId() : null);
				if(key != null) {
					Integer banDurationSec = (flowRuleDO.getBanDuration() == null) 
											? null 
											: (int) (flowRuleDO.getBanDuration() * 60);
					FlowRule flowRule = new FlowRule(
							key, 
							(int) (flowRuleDO.getInterval() * 60), 
							flowRuleDO.getMaxfreq(), 
							banDurationSec);
					flowRules.add(flowRule);
				}
			} catch (Exception e) {
				logger.error("TIM Exception:" + e,e);
				continue;
			}
		}
	}


	private void fillFlowRulesFromApi(Set<FlowRule> flowRules, TinyApiDO api, String appKey) {
		if(api.getAccessCountPerDay() != null && api.getAccessCountPerDay() >= 0) {
			flowRules.add(new FlowRule(CheckKeyGen.genApiKey(api.getId()), 
					CHECK_INTERVAL_ONE_DAY,
					(long) api.getAccessCountPerDay(), null));
		}
		if(api.getAccessCountPerMin() != null && api.getAccessCountPerMin() >= 0) {
			flowRules.add(new FlowRule(CheckKeyGen.genApiKey(api.getId()), 
					CHECK_INTERVAL_ONE_MINUTE,
					(long) api.getAccessCountPerMin(), null));
			
		}
		if(api.getAppAccessCountPerDay() != null && api.getAppAccessCountPerDay() >= 0) {
			flowRules.add(new FlowRule(CheckKeyGen.genAppApiKey(appKey, api.getId()), 
					CHECK_INTERVAL_ONE_DAY,
					(long) api.getAppAccessCountPerDay(), null));	
		}
		if(api.getAppAccessCountPerMin() != null && api.getAppAccessCountPerMin() >= 0) {
			flowRules.add(new FlowRule(CheckKeyGen.genAppApiKey(appKey, api.getId()), 
					CHECK_INTERVAL_ONE_MINUTE,
					(long) api.getAppAccessCountPerMin(), null));	
		}
		
	}
	
	private String genKey(FlowRuleV2DO flowRule, String appKey,
			Long apiId) {
		if(FlowRuleV2DO.TYPE_APP_FREQ.equals(flowRule.getType())) {
			return CheckKeyGen.genAppKey(appKey);
		} else if(FlowRuleV2DO.TYPE_APP_API_FREQ.equals(flowRule.getType())) {
			if(apiId != null && flowRule.getApiIdList().contains(apiId)) {
				return CheckKeyGen.genAppApiKey(appKey, apiId);
			} 
		}
		
		return null;
		
	}

}
