package com.taobao.top.core.framework.pipe;


import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.accesscontrol.CallRecorder;
import com.taobao.top.core.accesscontrol.FlowControlResult;
import com.taobao.top.core.accesscontrol.FlowGuard;
import com.taobao.top.core.accesscontrol.FlowRule;
import com.taobao.top.core.accesscontrol.IFlowRulesConstructor;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.core.framework.TopPipeUtil;
import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.timwrapper.manager.TadgetManager;

public class NewFlowControlPipe extends TopPipe<TopPipeInput, TopPipeResult>{
	private static final Log logger = LogFactory.getLog(NewFlowControlPipe.class);
	
	
	private TadgetManager tadgetManager;
	private FlowGuard flowGuard;
	private CallRecorder callRecorder;
	private IFlowRulesConstructor flowRulesConstructor;
	
	/**
	 * 是否进行访问限制
	 */
	private boolean isForceLimit = true;
	

	/* 
	 * First check whether it can access via the blacklist.
	 * then increment the counter, it the counter said it already exceed the limit,
	 * also block the access.
	 * 
	 * To do as check-->add-->check is a protection. 
	 */
	@Override
	public void doPipe(TopPipeInput pipeInput, TopPipeResult pipeResult) {
		TinyApiDO apiDO = null;
		try {
			apiDO = tadgetManager.getValidApiByApiName(pipeInput.getApiName());
		} catch (Exception e) {
			logger.error("TIM Exception:" + e,e);
		}
		
		try {
			
			Collection<FlowRule> flowRules = flowRulesConstructor.constructRules(pipeInput.getAppDO(), apiDO);
			
			for(FlowRule rule : flowRules) {
				if(rule.getThreshold() == 0) {
					//first check if some rule's access threshold is 0. which means it should 
					//never be called. to put it here is because if the callRecorder is the 
					//asynchronous way, this access will be allowed. 
					//TODO: Refactor all this into flowGuard. too short time for now--huaisu
					//It should not happen here indeed. so not yet refactor into flowGuard now
					pipeResult.setErrorCode(ErrorCode.INSUFFICIENT_ISV_PERMISSIONS);
					pipeResult.setSubMsg(rule.getCheckKey() + " can not be accessed");
					logger.info("api forbidden by zero access rule: " + rule);
					return;
				}
			}
			FlowControlResult result = flowGuard.getResult(
							pipeInput.getAppDO().getAppKey(),
							apiDO == null ? null : apiDO.getId(), 
							flowRules);
			
			if(result.canAccess()) {
				result = callRecorder.recordAndCanCall(flowRules);
			}
			
			if(!result.canAccess()) {
				if(isForceLimit) {
					if(logger.isInfoEnabled()) {
						logger.info(pipeInput.getAppDO().getAppKey() + ":"
								+ (apiDO == null ? null : apiDO.getId()) + " call limit reached, " 
								+ "detail: " + result);								
					}
					pipeResult.setErrorCode(ErrorCode.CALL_LIMITED_APP_API);
					pipeResult.setSubCode(result.getResult().getDetail());
					pipeResult.setSubMsg("This ban will last for " + result.getTimeToLive()  + " more seconds ");
					return;				
				}
			}
		} catch (Exception e) { // to protect the main process line
			logger.error("Error in new flow", e);
		}
	}


	@Override
	public boolean ignoreIt(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		
		if (pipeResult.getErrorCode() != null || TopPipeUtil.isIgnore(pipeInput)) {
			return true;
		} else {
			return false;			
		}

	}
	
	public boolean isForceLimit() {
		return isForceLimit;
	}

	public void setForceLimit(boolean isForceLimit) {
		this.isForceLimit = isForceLimit;
	}

	public void setTadgetManager(TadgetManager tadgetManager) {
		this.tadgetManager = tadgetManager;
	}

	public void setFlowGuard(FlowGuard flowGuard) {
		this.flowGuard = flowGuard;
	}

	public void setCallRecorder(CallRecorder callRecorder) {
		this.callRecorder = callRecorder;
	}

	public void setFlowRulesConstructor(IFlowRulesConstructor flowRulesConstructor) {
		this.flowRulesConstructor = flowRulesConstructor;
	}
	
}
