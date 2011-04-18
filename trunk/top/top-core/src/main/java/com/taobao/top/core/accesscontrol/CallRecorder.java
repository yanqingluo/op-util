package com.taobao.top.core.accesscontrol;

import java.util.Collection;

/**
 * increment the counter of flowRules, and check if these 
 * rules are reached, return the result
 * @author <a href="mailto:huaisu@taobao.com">huaisu</a>
 * @since 2010-7-7
 */
public interface CallRecorder {
	
	/**
	 * Record the app api access this time into flow control.
	 * when the access has reached the limit of rule, that key
	 * will be added into blacklist to prevent access next time.
	 * 
	 * 
	 * Note that during this method, the access won't be forbidden.
	 * It only add the key into blacklist this time, thus forbid 
	 * the call next time of this key.
	 * @return the flowControlResult of this call
	 */
	public FlowControlResult recordAndCanCall(Collection<FlowRule> flowRules);
	
}
