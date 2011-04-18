package com.taobao.top.ats.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.common.lang.io.ByteArrayOutputStream;
import com.taobao.common.dao.persistence.exception.DAOException;
import com.taobao.common.tfs.TfsManager;
import com.taobao.top.ats.AtsException;
import com.taobao.top.ats.dao.TaskDao;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.ResultSet;
import com.taobao.top.ats.domain.SubAtsTaskDO;
import com.taobao.top.ats.task.AtsJob;
import com.taobao.top.ats.task.TaskManager;
import com.taobao.top.ats.url.UrlGenerator;
import com.taobao.top.ats.util.AttributeUtil;
import com.taobao.top.ats.util.ErrorCode;
import com.taobao.top.ats.util.StatusConstants;

/**
 * ATS服务接口默认实现。
 * 
 * @author carver.gu
 * @since 1.0, Aug 19, 2010
 */
public class TaskServiceImpl implements TaskService {

	private static final Log log = LogFactory.getLog(TaskServiceImpl.class);

	private TaskDao taskDao;
	private TfsManager tfsManager;
	private TaskManager taskManager;
	private UrlGenerator urlGenerator;

	public void setTaskManager(TaskManager taskManager) {
		this.taskManager = taskManager;
	}

	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}

	public void setTfsManager(TfsManager tfsManager) {
		this.tfsManager = tfsManager;
	}
	
	public void setUrlGenerator(UrlGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	public ResultSet addTask(Map<String, String> params) {
		if (params == null) {
			params = new HashMap<String, String>();
		}

		ResultSet result = null;
		try {
			result = taskManager.createTask(params);
			if (!result.isError()) {
				taskManager.notifyTask(result.getResult());
			}
		} catch (Throwable e) {
			result = new ResultSet();
			result.setErrorCode(ErrorCode.ATS_SERVICE_UNAVAILABLE);
			result.setErrorMsg("创建异步服务出错");
			log.error(result.getErrorMsg(), e);
		}

		return result;
	}

	public ResultSet getResult(Long taskId, String appKey) {
		ResultSet result = new ResultSet();
		try {
			getResult(taskId, appKey, result);
		} catch (DAOException e) {
			result.setErrorCode(ErrorCode.REMOTE_SERVICE_ERROR);
			result.setErrorMsg("获取异步任务出错：" + taskId);
		} catch (IOException e) {
			result.setErrorCode(ErrorCode.READ_RESULT_ERROR);
			result.setErrorMsg("读取异步任务出错：" + taskId);
		} catch (Throwable e) {
			result.setErrorCode(ErrorCode.ATS_SERVICE_UNAVAILABLE);
			result.setErrorMsg("获取异步任务结果出错");
			log.error("", e);
		}
		return result;
	}

	public void getResult(Long taskId, String appKey, ResultSet result) throws DAOException, IOException {
		AtsTaskDO atsTask = taskDao.getParentTask(taskId);
		if (null == atsTask) {
			result.setErrorCode(ErrorCode.TASK_NOT_EXIST);
			result.setErrorMsg("该任务不存在");
			return;
		}

		if (!atsTask.getAppKey().equalsIgnoreCase(appKey)) {
			result.setErrorCode(ErrorCode.MISMATCH_TASK_APP);
			result.setErrorMsg("此任务不属于当前APP");
			return;
		}

		// 如果任务状态为Failed，则将状态改为Doing
		if (StatusConstants.TASK_STATUS_FAIL.getStatus() == atsTask.getStatus()) {
			atsTask.setStatus(StatusConstants.TASK_STATUS_DOING.getStatus());
			// 如果任务状态为Sent，则将状态改为Done
		} else if (StatusConstants.TASK_STATUS_SENT.getStatus() == atsTask.getStatus()
				|| StatusConstants.TASK_STATUS_DONE.getStatus() == atsTask.getStatus()) {
			atsTask.setStatus(StatusConstants.TASK_STATUS_DONE.getStatus());

			// 区分大任务结果还是小任务结果
			if (AttributeUtil.isBigResult(atsTask.getAttributes())) {
				try {
					String downloadUrl = urlGenerator.getAtsDownloadUrl(atsTask);
					atsTask.setDownloadUrl(downloadUrl);
				} catch (AtsException e) {
					result.setErrorCode(ErrorCode.GENERATE_DOWNLOAD_URL_ERROR);
					result.setErrorMsg("生成下载地址异常");
					log.error(result.getErrorMsg(), e);
					return;
				}
			} else {
				// 从TFS上获得任务执行结果
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				boolean fetchResult = tfsManager.fetchFile(atsTask.getResponse(), null, bos);
				if (!fetchResult) {
					result.setErrorCode(ErrorCode.READ_RESULT_ERROR);
					result.setErrorMsg("获取异步任务结果出错");
					return;
				}

				atsTask.setSubTasks(readTasksFromStream(bos));
			}
		}

		result.setResult(atsTask);
	}

	protected List<SubAtsTaskDO> readTasksFromStream(ByteArrayOutputStream out) throws IOException {
		List<SubAtsTaskDO> subTasks = new ArrayList<SubAtsTaskDO>();
		if (out == null) {
			return subTasks;
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(out.toInputStream()));
			String line = null;
			int i = 1;
			SubAtsTaskDO subTask = null;
			while (StringUtils.isNotBlank(line = reader.readLine())) {
				// 直接跳过分隔行
				if (AtsJob.SEPERATOR.equals(line)) {
					i++;

					// 进入下一个子任务结果获取
					if (i == 4) {
						i = 1;
						subTask = null;
					}
					continue;
				}

				if (subTask == null) {
					subTask = new SubAtsTaskDO();
					subTasks.add(subTask);
				}

				if (i == 1) {
					subTask.setIsSuccess(Boolean.valueOf(line));
				} else if (i == 2) {
					if (StringUtils.isNotBlank(subTask.getRequest())) {
						subTask.setRequest(subTask.getRequest() + "\r\n" + line.trim());
					} else {
						subTask.setRequest(line.trim());
					}
				} else if (i == 3) {
					if (StringUtils.isNotBlank(subTask.getResponse())) {
						subTask.setResponse(subTask.getResponse() + "\r\n" + line.trim());
					} else {
						subTask.setResponse(line.trim());
					}
				}
			}
		} finally {
			IOUtils.closeQuietly(reader);
		}

		return subTasks;
	}

}
