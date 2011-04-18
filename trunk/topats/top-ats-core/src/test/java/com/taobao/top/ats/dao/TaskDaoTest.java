package com.taobao.top.ats.dao;

import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.taobao.common.dao.persistence.exception.DAOException;
import com.taobao.top.ats.AtsTestBase;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.PageList;
import com.taobao.top.ats.domain.QueryTaskDO;
import com.taobao.top.ats.domain.SubAtsTaskDO;

public class TaskDaoTest extends AtsTestBase {

	private AtsTaskDO _task;
	private SubAtsTaskDO _subTask;

	@Before
	public void before() throws Exception {
		addParentTask();
		addSubTask();
	}

	private void addParentTask() throws DAOException {
		AtsTaskDO task = new AtsTaskDO();
		task.setApiName("taobao.toptas.delivery.send");
		task.setAppKey("1000120");
		task.setStatus(0);
		task.setRetries(0);
		task.setPriority(0);
		task.setGmtCreated(new Date());
		task.addAttributes("token", "123");
		_task = taskDao.addParentTask(task);
	}

	private void addSubTask() throws DAOException {
		SubAtsTaskDO subTask = new SubAtsTaskDO();
		subTask.setPid(_task.getId());
		subTask.setRequest("request");
		subTask.setResponse("response");
		subTask.setStatus(0);
		_subTask = taskDao.addSubTask(subTask);
	}
	
	@Test
	public void getParentTask() throws DAOException {
		AtsTaskDO task = taskDao.getParentTask(_task.getId());
		Assert.assertEquals(_task.getId(), task.getId());
		Assert.assertEquals(_task.getAttributesString(), task.getAttributesString());
	}

	@Test
	public void getSubTask() throws DAOException {
		SubAtsTaskDO subTask = taskDao.getSubTask(_subTask.getId());
		Assert.assertEquals(_subTask.getId(), subTask.getId());
	}

	@Test
	public void getTask() throws DAOException {
		AtsTaskDO task = taskDao.getTask(_task.getId());
		Assert.assertNotNull(task.getSubTasks());
	}

	@Test
	public void updateParentTask() throws DAOException {
		_task.setStatus(2);
		_task.setRetries(3);
		_task.addAttributes("token", "456");
		AtsTaskDO task = taskDao.updateParentTask(_task);
		Assert.assertEquals(_task.getStatus(), task.getStatus());
		Assert.assertEquals(_task.getRetries(), task.getRetries());
		Assert.assertEquals(_task.getAttributesString(), task.getAttributesString());
	}

	@Test
	public void updateSubTask() throws DAOException {
		_subTask.setStatus(1);
		_subTask.setResponse("hello world");
		SubAtsTaskDO subTask = taskDao.updateSubTask(_subTask);
		Assert.assertEquals(_subTask.getStatus(), subTask.getStatus());
		Assert.assertEquals(_subTask.getResponse(), subTask.getResponse());
	}

	@Test
	public void queryTasks() throws DAOException {
		QueryTaskDO query = new QueryTaskDO();
		query.setTaskStatus(0);
		query.setEndCreated(new Date());
		query.setStartCreated(DateUtils.addDays(query.getEndCreated(), -1));
		PageList<AtsTaskDO> result = taskDao.queryTasks(query);
		System.out.println(result.getTotal());
	}

}
