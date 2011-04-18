package com.taobao.top.ats.monitor;

import com.taobao.top.ats.domain.LogDO;

/**
 * 
 * @author moling
 * @since 1.0, 2010-11-16
 */
public interface TaskMonitor {
	/**
	 * 记录成功访问的子任务执行时间
	 * @param logDO
	 */
	public void logSuccess(LogDO logDO);
	
	/**
	 * 记录失败访问的子任务的执行时间和错误原因
	 * @param logDO
	 */
	public void logError(LogDO logDO);
	
	/**
	 * 记录整个任务的执行情况
	 * @param logDO
	 */
	public void logTask(LogDO logDO);
}
