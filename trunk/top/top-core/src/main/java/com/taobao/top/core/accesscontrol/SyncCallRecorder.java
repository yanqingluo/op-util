package com.taobao.top.core.accesscontrol;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.core.CallLimitManager;
import com.taobao.top.privilege.BlackListManager;

/**
 * record call asynchronously.
 * @author huaisu
 *
 */
public class SyncCallRecorder implements CallRecorder {
	private static final Log logger = LogFactory.getLog(SyncCallRecorder.class);
	
	private BlackListManager blackListManager;
	private CallLimitManager callLimitManager;
	
	public void setBlackListManager(BlackListManager blackListManager) {
		this.blackListManager = blackListManager;
	}

	public void setCallLimitManager(CallLimitManager callLimitManager) {
		this.callLimitManager = callLimitManager;
	}

	/* 
	 * Record each access based on flow rules from the app and api, 
	 * if the access has reached the threshold, then that key will be added into blackList.
	 */
	@Override
	public FlowControlResult recordAndCanCall(Collection<FlowRule> flowRules) {
				
		for(FlowRule flowRule : flowRules) {
			try {
				int accessTimes = callLimitManager.incrNewFlowTimes(flowRule.getFlowRuleKey());
				if (accessTimes >= flowRule.getThreshold()) {
					blackListManager.addNewFlowBlack(flowRule.getCheckKey(), flowRule.getBanDuration());
					logger.warn("Add new freq black,flowRule is:" + flowRule + ", callTime:" + accessTimes);
					if(accessTimes > flowRule.getThreshold()) {
						return new FlowControlResult(flowRule.getCheckKey(), flowRule.getBanDuration());						
					}
				}
			} catch (Exception e) {
				logger.error("IncrAppFreqTimes error,flow rule:" + flowRule.getFlowRuleKey(),e);
			}
		}
		return FlowControlResult.NOT_FORBIDDEN;
	}

}
