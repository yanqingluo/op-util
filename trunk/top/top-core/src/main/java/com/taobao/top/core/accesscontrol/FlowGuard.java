package com.taobao.top.core.accesscontrol;

import java.util.Collection;


/**
 * Manage the flow control, judge whether app can access api 
 * from flow control's view and record access to do the judge.
 *  
 * @author <a href="mailto:huaisu@taobao.com">huaisu</a>
 * @since 2010-7-6
 */
public interface FlowGuard {
	/**
	 * get the flow control result of the app access the api 
	 * @param appKey can't be null
	 * @param apiId can be null
	 * @return if forbid, the detail info is returned, null if not forbidden
	 */
	public FlowControlResult getResult(String appKey, Long apiId, Collection<FlowRule> flowRules);
	
}
