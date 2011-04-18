package com.taobao.top.ats.dao;

import com.taobao.common.dao.persistence.exception.DAOException;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.PageList;
import com.taobao.top.ats.domain.QueryTaskDO;
import com.taobao.top.ats.domain.SubAtsTaskDO;

/**
 * 任务数据访问接口。
 * 
 * @author moling
 * @since 1.0, 2010-8-20
 */
public interface TaskDao {

	/**
	 * 创建一个父任务记录
	 */
	public AtsTaskDO addParentTask(AtsTaskDO task) throws DAOException;

	/**
	 * 创建一个子任务记录
	 */
	public SubAtsTaskDO addSubTask(SubAtsTaskDO subTask) throws DAOException;

	/**
	 * 更新一条父任务记录
	 */
	public AtsTaskDO updateParentTask(AtsTaskDO task) throws DAOException;

	/**
	 * 更新一条子任务记录
	 */
	public SubAtsTaskDO updateSubTask(SubAtsTaskDO subTask) throws DAOException;

	/**
	 * 获取一条父任务记录
	 */
	public AtsTaskDO getParentTask(long taskId) throws DAOException;

	/**
	 * 获取一条子任务记录
	 */
	public SubAtsTaskDO getSubTask(long subTaskId) throws DAOException;

	/**
	 * 获取一个完整的父子任务
	 */
	public AtsTaskDO getTask(long taskId) throws DAOException;

	/**
	 * 根据条件查询任务列表
	 */
	public PageList<AtsTaskDO> queryTasks(QueryTaskDO query) throws DAOException;

	/**
	 * 根据条件查询任务记录数
	 */
	public Integer queryTaskCount(QueryTaskDO query) throws DAOException;

	/**
	 * 根据条件查询可清理的历史任务列表
	 */
	public PageList<AtsTaskDO> queryCleanableTasks(QueryTaskDO query) throws DAOException;

	/**
	 * 根据条件查询可清理的任务记录数
	 */
	public Integer queryCleanableTaskCount(QueryTaskDO query) throws DAOException;

	/**
	 * 根据给定的ID删除父任务
	 */
	public int deleteParentTask(long taskId) throws DAOException;

	/**
	 * 根据给定的子任务ID删除其所有子任务
	 */
	public int deleteSubTasks(long parentTaskId) throws DAOException;

}
