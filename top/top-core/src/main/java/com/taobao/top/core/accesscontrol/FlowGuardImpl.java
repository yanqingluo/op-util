package com.taobao.top.core.accesscontrol;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.privilege.BlackListManager;

public class FlowGuardImpl implements FlowGuard {
	private static final Log logger = LogFactory.getLog(FlowGuardImpl.class);
	
	private BlackListManager blackListManager;
	

	/**
	 * 
	 * check if the app can access the api by the flow control rules 
	 * @param appKey can't be null
	 * @param apiId can be null
	 * @param flowRules should not be null
	 * @return
	 */
	@Override
	public FlowControlResult getResult(String appKey, Long apiId, Collection<FlowRule> flowRules) {
		Collection<String> keyIds = getCheckKeyIds(flowRules);
		for(String keyId : keyIds) {
			Integer banEndTime = blackListManager.getBanEndTimeOfNewFlow(keyId);
			int lastLength = 0;
			if(banEndTime != null 
					&& (lastLength = banEndTime - (int)(System.currentTimeMillis() / 1000)) > 0) {
				if(logger.isInfoEnabled()) {
					logger.info(appKey + ":" 
							+ apiId  
							+ " forbidden by blackList key " + keyId
							+ ", will last " + lastLength + " more seconds");							
				}
				return new FlowControlResult(keyId, lastLength);
			}
		}
		return FlowControlResult.NOT_FORBIDDEN;
	}

	private Collection<String> getCheckKeyIds(Collection<FlowRule> flowRules) {
		/*
		 * one app api pair may have several different control rules, 
		 * but the check keys may be the same.
		 * like app 24 hours and app 1 minute has one same check key. 
		 */
		Set<String> keyIds = new HashSet<String>();
		for(FlowRule rule : flowRules) {
			keyIds.add(rule.getCheckKey());
		}
		return keyIds;
	}

	public void setBlackListManager(BlackListManager blackListManager) {
		this.blackListManager = blackListManager;
	}
	
}