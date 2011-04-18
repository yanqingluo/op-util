package com.taobao.top.ats.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.alibaba.common.lang.io.ByteArrayOutputStream;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.ResultSet;
import com.taobao.top.ats.domain.SubAtsTaskDO;
import com.taobao.top.ats.service.mock.TaskDaoMock;
import com.taobao.top.ats.service.mock.TfsManagerMock;
import com.taobao.top.ats.util.StatusConstants;

public class TaskServiceTest {

	private static TaskServiceImpl service;

	static {
		service = new TaskServiceImpl();
		service.setTaskDao(new TaskDaoMock());
		service.setTfsManager(new TfsManagerMock());
	}

	@Test
	public void testGetResultMismatchTaskApp() {
		ResultSet result = service.getResult(TaskDaoMock.MISMATCH_TASK_APP, "Jerry");
		printResult(result);
	}

	@Test
	public void testGetResultTaskNotExist() {
		ResultSet result = service.getResult(TaskDaoMock.TASK_NOT_EXIST, "Jerry");
		printResult(result);
	}

	@Test
	public void testGetResultTaskStatusNew() {
		ResultSet result = service.getResult(TaskDaoMock.TASK_STATUS_NEW, "Jerry");
		printResult(result);
	}

	@Test
	public void testGetResultTaskStatusDoing() {
		ResultSet result = service.getResult(TaskDaoMock.TASK_STATUS_DOING, "Jerry");
		printResult(result);
	}

	@Test
	public void testGetResultTaskStatusFail() {
		ResultSet result = service.getResult(TaskDaoMock.TASK_STATUS_FAIL, "Jerry");
		printResult(result);
	}

	@Test
	public void testGetResultTaskStatusDone() {
		ResultSet result = service.getResult(TaskDaoMock.TASK_STATUS_DONE, "Jerry");
		printResult(result);
	}

	@Test
	public void testGetResultTaskStatusSent() {
		ResultSet result = service.getResult(TaskDaoMock.TASK_STATUS_SENT, "Jerry");
		printResult(result);
	}

	private void printResult(ResultSet result) {
		if (result.isError()) {
			System.out.println(result.getErrorMsg());
			return;
		}
		AtsTaskDO atsTask = result.getResult();

		if (StatusConstants.TASK_STATUS_DONE.getStatus() != atsTask.getStatus()) {
			System.out.printf("Status: %s\n", getStatusName(atsTask.getStatus()));
			return;
		}

		System.out.printf("Status: %s\n", getStatusName(atsTask.getStatus()));
		for (SubAtsTaskDO subTask : atsTask.getSubTasks()) {
			System.out.printf("Request: %s\n", subTask.getRequest());
			System.out.printf("Response: %s\n", subTask.getResponse());
		}
	}

	private String getStatusName(int status) {
		StatusConstants[] ss = StatusConstants.values();
		for (StatusConstants s : ss) {
			if (status == s.getStatus()) {
				return s.getStatusName();
			}
		}
		return null;
	}

	@Test
	public void parseResult() throws IOException {
		InputStream input = TaskServiceTest.class.getResourceAsStream("/TaskResult.txt");
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		IOUtils.copy(input, output);
		service.readTasksFromStream(output);
	}

}
