package com.taobao.top.ats.dao;

import java.util.List;

import com.taobao.common.dao.persistence.DBRoute;
import com.taobao.common.dao.persistence.SqlMapBaseDAO;
import com.taobao.common.dao.persistence.exception.DAOException;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.PageList;
import com.taobao.top.ats.domain.QueryTaskDO;
import com.taobao.top.ats.domain.SubAtsTaskDO;

/**
 * 异步任务数据访问接口实现。
 * 
 * @author carver.gu
 * @since 1.0, Aug 24, 2010
 */
public class TaskDaoImpl extends SqlMapBaseDAO implements TaskDao {

	public AtsTaskDO addParentTask(AtsTaskDO task) throws DAOException {
		Long id = (Long) executeInsert("addParentTask", task, DBRoute.getDBCRoute());
		task.setId(id);

		List<SubAtsTaskDO> subTasks = task.getSubTasks();
		if (subTasks != null && !subTasks.isEmpty()) {
			for (SubAtsTaskDO subTask : subTasks) {
				subTask.setPid(id);
				addSubTask(subTask);
			}
		}

		return task;
	}

	public SubAtsTaskDO addSubTask(SubAtsTaskDO subTask) throws DAOException {
		Long id = (Long) executeInsert("addSubTask", subTask, DBRoute.getDBCRoute());
		subTask.setId(id);
		return subTask;
	}

	public AtsTaskDO getParentTask(long taskId) throws DAOException {
		return (AtsTaskDO) executeQueryForObject("getParentTask", taskId, DBRoute.getDBCRoute());
	}

	public SubAtsTaskDO getSubTask(long subTaskId) throws DAOException {
		return (SubAtsTaskDO) executeQueryForObject("getSubTask", subTaskId, DBRoute.getDBCRoute());
	}

	@SuppressWarnings("unchecked")
	public AtsTaskDO getTask(long taskId) throws DAOException {
		AtsTaskDO task = getParentTask(taskId);
		if (task != null) {
			List<SubAtsTaskDO> subTasks = executeQueryForList("getSubTasks", taskId, DBRoute.getDBCRoute());
			task.setSubTasks(subTasks);
		}
		return task;
	}

	@SuppressWarnings("unchecked")
	public PageList<AtsTaskDO> queryTasks(QueryTaskDO query) throws DAOException {
		PageList<AtsTaskDO> result = new PageList<AtsTaskDO>();
		result.setData(executeQueryForList("queryTasks", query, DBRoute.getDBCRoute()));
		result.setTotal((Integer) executeQueryForObject("queryTasksTotal", query, DBRoute.getDBCRoute()));
		return result;
	}

	public AtsTaskDO updateParentTask(AtsTaskDO task) throws DAOException {
		executeUpdate("updateParentTask", task, DBRoute.getDBCRoute());
		return task;
	}

	public SubAtsTaskDO updateSubTask(SubAtsTaskDO subTask) throws DAOException {
		executeUpdate("updateSubTask", subTask, DBRoute.getDBCRoute());
		return subTask;
	}

	public Integer queryTaskCount(QueryTaskDO query) throws DAOException {
		return (Integer) executeQueryForObject("queryTasksTotal", query, DBRoute.getDBCRoute());
	}

	@SuppressWarnings("unchecked")
	public PageList<AtsTaskDO> queryCleanableTasks(QueryTaskDO query) throws DAOException {
		PageList<AtsTaskDO> result = new PageList<AtsTaskDO>();
		result.setData(executeQueryForList("queryCleanableTasks", query, DBRoute.getDBCRoute()));
		return result;
	}

	public Integer queryCleanableTaskCount(QueryTaskDO query) throws DAOException {
		return (Integer) executeQueryForObject("queryCleanableTasksTotal", query, DBRoute.getDBCRoute());
	}

	public int deleteParentTask(long taskId) throws DAOException {
		return executeUpdate("deleteParentTask", taskId, DBRoute.getDBCRoute());
	}

	public int deleteSubTasks(long parentTaskId) throws DAOException {
		return executeUpdate("deleteSubTasks", parentTaskId, DBRoute.getDBCRoute());
	}

}
