package com.taobao.top.ats.task;

import java.util.Map;

import com.taobao.top.ats.AtsException;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.ResultSet;

/**
 * 任务管理器。
 * 
 * @author carver.gu
 * @since 1.0, Aug 18, 2010
 */
public interface TaskManager {

	/**
	 * 通知工作线程处理任务。
	 */
	public void notifyTask(AtsTaskDO task) throws AtsException;

	/**
	 * 把TIP请求转换为异步任务。
	 */
	public ResultSet createTask(Map<String, String> params) throws AtsException;
	
	/**
	 * 手动执行失败任务
	 */
	public void executeManualAtsTask(long taskId) throws AtsException;

}
