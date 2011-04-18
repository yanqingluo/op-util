package com.taobao.top.ats.service.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.taobao.common.dao.persistence.exception.DAOException;
import com.taobao.top.ats.dao.TaskDao;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.PageList;
import com.taobao.top.ats.domain.QueryTaskDO;
import com.taobao.top.ats.domain.SubAtsTaskDO;
import com.taobao.top.ats.util.StatusConstants;

public class TaskDaoMock implements TaskDao {
	
	public static final long MISMATCH_TASK_APP = 1;
	
	public static final long TASK_NOT_EXIST = 2;
	
	public static final long TASK_STATUS_NEW = 3;
	
	public static final long TASK_STATUS_DOING = 4;
	
	public static final long TASK_STATUS_FAIL = 5;
	
	public static final long TASK_STATUS_DONE = 6;
	
	public static final long TASK_STATUS_SENT = 7;

	public AtsTaskDO addParentTask(AtsTaskDO task) {
		// TODO Auto-generated method stub
		return null;
	}

	public SubAtsTaskDO addSubTask(SubAtsTaskDO subTask) {
		// TODO Auto-generated method stub
		return null;
	}

	public AtsTaskDO getParentTask(long taskId) {
		if(TASK_NOT_EXIST == taskId){
			return null;
		}
		List<SubAtsTaskDO> subTasks = new ArrayList<SubAtsTaskDO>();
		SubAtsTaskDO subTask;
		for(int i = 0; i < 10; i++){
			subTask = new SubAtsTaskDO();
			subTasks.add(subTask);
		}
		
		AtsTaskDO atsTask = new AtsTaskDO();
		if(MISMATCH_TASK_APP == taskId){
			atsTask.setAppKey("4272");
		}else{
			atsTask.setAppKey("Jerry");
		}
		atsTask.setSubTasks(subTasks);
		if(TASK_STATUS_NEW == taskId){
			atsTask.setStatus(StatusConstants.TASK_STATUS_NEW.getStatus()); 
		}else if(TASK_STATUS_DOING == taskId){
			atsTask.setStatus(StatusConstants.TASK_STATUS_DOING.getStatus());
		}else if(TASK_STATUS_FAIL == taskId){
			atsTask.setStatus(StatusConstants.TASK_STATUS_FAIL.getStatus());
		}else if(TASK_STATUS_DONE == taskId){
			atsTask.setStatus(StatusConstants.TASK_STATUS_DONE.getStatus());
		}else if(TASK_STATUS_SENT == taskId){
			atsTask.setStatus(StatusConstants.TASK_STATUS_SENT.getStatus());
		}
		
		return atsTask;
	}

	public SubAtsTaskDO getSubTask(long subTaskId) {
		// TODO Auto-generated method stub
		return null;
	}

	public AtsTaskDO getTask(long taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	public PageList<AtsTaskDO> queryTasks(QueryTaskDO query) {
		// TODO Auto-generated method stub
		return null;
	}

	public AtsTaskDO updateParentTask(AtsTaskDO task) {
		// TODO Auto-generated method stub
		return null;
	}

	public SubAtsTaskDO updateSubTask(SubAtsTaskDO subTask) {
		// TODO Auto-generated method stub
		return null;
	}

	public int cleanParentTask(Date endCreated) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int deleteParentTask(long taskId) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int deleteSubTasks(long taskId) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Integer queryTaskCount(QueryTaskDO query) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer queryCleanableTaskCount(QueryTaskDO query)
			throws DAOException {
		return 10;
	}

	public PageList<AtsTaskDO> queryCleanableTasks(QueryTaskDO query)
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}	
}
