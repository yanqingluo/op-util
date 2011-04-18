/**
 * 
 */
package com.taobao.top.core;
/**
 * 呼叫量限制管理
 * 
 * @author alin
 * 
 */
public interface CallLimitManager {

	int incrNewFlowTimes(String key) throws Exception;;
}
