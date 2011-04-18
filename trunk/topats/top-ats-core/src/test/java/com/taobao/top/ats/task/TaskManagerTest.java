package com.taobao.top.ats.task;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.taobao.top.ats.AtsException;
import com.taobao.top.ats.AtsTestBase;
import com.taobao.top.ats.domain.ResultSet;
import com.taobao.top.ats.util.KeyConstants;

public class TaskManagerTest extends AtsTestBase {

	private TaskManager taskManager = (TaskManager) ctx.getBean("taskManager");

	@Test
	public void createTask() throws AtsException {
		Map<String, String> params = new HashMap<String, String>();
		params.put(KeyConstants.METHOD, "Jerry");
		ResultSet result = taskManager.createTask(params);
		Assert.assertEquals("Jerry", result.getResult().getApiName());
	}

}
