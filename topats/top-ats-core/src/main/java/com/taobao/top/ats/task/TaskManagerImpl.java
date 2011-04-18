package com.taobao.top.ats.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.common.dao.persistence.exception.DAOException;
import com.taobao.common.tfs.TfsManager;
import com.taobao.top.ats.AtsException;
import com.taobao.top.ats.dao.TaskDao;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.PageList;
import com.taobao.top.ats.domain.QueryTaskDO;
import com.taobao.top.ats.domain.ResultSet;
import com.taobao.top.ats.engine.ApiEngine;
import com.taobao.top.ats.monitor.TaskMonitor;
import com.taobao.top.ats.task.api.ApiManager;
import com.taobao.top.ats.task.cleaner.CleanerManager;
import com.taobao.top.ats.util.AttributeUtil;
import com.taobao.top.ats.util.StatusConstants;
import com.taobao.top.mq.TopMQService;
import com.taobao.top.tim.service.SamService;
import com.taobao.top.xbox.threadpool.JobDispatcher;

/**
 * 任务管理器默认实现。
 * 
 * @author carver.gu
 * @since 1.0, Aug 19, 2010
 */
public class TaskManagerImpl implements TaskManager{

	private static final Log log = LogFactory.getLog(TaskManagerImpl.class);

	private static final Pattern TASK_REGEX = Pattern.compile("(\\d+)_task\\.txt");
	private static final String RESULT_FILE_POSTFIX = "_task.txt";

	private SamService samService;
	private ApiManager apiManager;
	private ApiEngine apiEngine;
	private CleanerManager cleanerManager;
	private JobDispatcher jobDispatcher;
	private TaskDao taskDao;
	private TopMQService topMQService;
	private TfsManager tfsManager;
	private String localPath;
	private String remotePath;
	private TaskMonitor taskMonitor;
	private long taskExecutePeriod = 1000L * 60 * 60 * 1;
	private long taskCleanupPeriod = 1000L * 60 * 60 * 24;

	public void setSamService(SamService samService) {
		this.samService = samService;
	}

	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}

	public void setJobDispatcher(JobDispatcher jobDispatcher) {
		this.jobDispatcher = jobDispatcher;
	}

	public void setApiManager(ApiManager apiManager) {
		this.apiManager = apiManager;
	}

	public void setCleanerManager(CleanerManager cleanerManager) {
		this.cleanerManager = cleanerManager;
	}

	public void setTopMQService(TopMQService topMQService) {
		this.topMQService = topMQService;
	}

	public void setTfsManager(TfsManager tfsManager) {
		this.tfsManager = tfsManager;
	}

	public void setApiEngine(ApiEngine apiEngine) {
		this.apiEngine = apiEngine;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public void setTaskMonitor(TaskMonitor taskMonitor) {
		this.taskMonitor = taskMonitor;
	}

	public long getTaskExecutePeriod() {
		return this.taskExecutePeriod;
	}

	public void setTaskExecutePeriod(long taskExecutePeriod) {
		this.taskExecutePeriod = taskExecutePeriod;
	}

	public long getTaskCleanupPeriod() {
		return this.taskCleanupPeriod;
	}

	public void setTaskCleanupPeriod(long taskCleanupPeriod) {
		this.taskCleanupPeriod = taskCleanupPeriod;
	}

	/**
	 * 处理数据库中未完成的任务
	 */
	public void init() {
		prepare();
		jobDispatcher.start();

		// 执行意外出错的历史任务
		processHistoryFiles();

		// 重新执行未被处理的任务
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				try {
					processHistoryTasks();
				} catch (Throwable e) {
					log.error("定时执行历史任务失败", e);
				}
			}
		}, 1000L, taskExecutePeriod);

		// 清理历史任务文件
		timer.schedule(new TimerTask() {
			public void run() {
				try {
					cleanerManager.cleanLocalFile();
				} catch (Throwable e) {
					log.error("定时清理历史任务文件失败", e);
				}
			}
		}, 2000L, taskCleanupPeriod);

		// 清理历史任务结果
		if (cleanerManager.isCleanerAvailable()) {
			timer.schedule(new TimerTask() {
				public void run() {
					try {
						cleanerManager.cleanSharedData();
					} catch (Throwable e) {
						log.error("定时清理历史任务结果失败", e);
					}
				}
			}, 3000L, taskCleanupPeriod);
		}
	}

	/**
	 * 准备工作。
	 */
	private void prepare() {
		File dir = new File(localPath);
		if (!dir.exists()) {
			boolean ok = dir.mkdirs();
			if (!ok) {
				throw new RuntimeException("无法创建本地临时文件夹！");
			}
		}
	}

	private void processHistoryFiles() {
		File dir = new File(localPath);
		Collection<?> files = FileUtils.listFiles(dir, new String[] { "txt" }, true);
		for (Object tmp : files) {
			File file = (File) tmp;
			Matcher matcher = TASK_REGEX.matcher(file.getName());
			if (matcher.find()) {
				Long taskId = Long.valueOf(matcher.group(1));
				try {
					AtsTaskDO task = taskDao.getTask(taskId);
					if (task != null) {
						executeAtsTask(task);
					} else {
						log.warn("数据库中不存在对就的TASK记录：" + taskId);
					}
				} catch (Exception e) {
					log.error("处理历史文件时发生异常", e);
				}
			}
		}
	}

	private void processHistoryTasks() throws Exception {
		log.warn("定时执行历史任务开始...");

		// 查询NEW, DOING, DONE三种状态的任务
		Date now = new Date();
		QueryTaskDO query = new QueryTaskDO();
		query.setPageSize(300);
		query.setTaskStatus(StatusConstants.TASK_STATUS_NEW.getStatus());
		query.setStartCreated(DateUtils.addDays(now, -1));
		query.setEndCreated(DateUtils.addMinutes(now, -15));
		PageList<AtsTaskDO> newTaskResult = taskDao.queryTasks(query);

		query.setTaskStatus(StatusConstants.TASK_STATUS_DOING.getStatus());
		query.setEndCreated(DateUtils.addMinutes(now, -30));
		PageList<AtsTaskDO> doingTaskResult = taskDao.queryTasks(query);

		query.setTaskStatus(StatusConstants.TASK_STATUS_DONE.getStatus());
		query.setEndCreated(DateUtils.addMinutes(now, -15));
		PageList<AtsTaskDO> doneTaskResult = taskDao.queryTasks(query);

		List<AtsTaskDO> atsTasks = new ArrayList<AtsTaskDO>();
		if (!newTaskResult.isEmpty()) {
			atsTasks.addAll(newTaskResult.getData());
		}
		if (!doingTaskResult.isEmpty()) {
			atsTasks.addAll(doingTaskResult.getData());
		}
		if (!doneTaskResult.isEmpty()) {
			atsTasks.addAll(doneTaskResult.getData());
		}

		for (AtsTaskDO atsTask : atsTasks) {
			if (apiManager.isApiIntime(atsTask.getApiName(), new Date())) {
				Long taskId = atsTask.getId();
				File file;
				if (AttributeUtil.isBigResult(atsTask.getAttributes())) {
					file = new File(localPath, atsTask.getId() + "");
				} else {
					file = new File(localPath, taskId + RESULT_FILE_POSTFIX);
				}
				try {
					if (file.exists()) {
						AtsTaskDO taskDO = taskDao.getTask(taskId);
						executeAtsTask(taskDO);
					}
				} catch (DAOException e) {
					log.error("操作数据库时发生异常：" + taskId, e);
				}
			}
		}
		log.warn("定时执行历史任务结束。");
	}

	public ResultSet createTask(Map<String, String> params) throws AtsException {
		ResultSet judge = apiManager.isRequestRight(params);
		if(judge.isError()){
			return judge;
		}
		
		ResultSet check = apiManager.isPreCheckOK(params);
		if(check.isError()){
			return check;
		}
		ResultSet result = new ResultSet();
		result.setResult(apiManager.getTask(params));
		return result;
	}

	public void notifyTask(AtsTaskDO task) throws AtsException {
		try {
			task = saveTask2DB(task);
		} catch (DAOException e) {
			throw new AtsException(e);
		}

		try {
			executeAtsTask(task);
		} catch (IOException e) {
			// 创建任务文件失败，向用户返回失败
			throw new AtsException(e);
		} catch (Exception e) {
			// 执行任务失败，向用户返回成功
			log.error("", e);
		}
	}

	private void executeAtsTask(AtsTaskDO task) throws IOException {
		AtsJob job = new AtsJob(samService, task, taskDao, topMQService, tfsManager, apiEngine,
				apiManager, localPath, remotePath, taskMonitor);
		job.getLocalFileOrDir();

		// 当前任务是否在可执行时段内
		if (apiManager.isApiIntime(task.getApiName(), new Date())) {
			log.warn("提交异步任务到执行队列中：" + task.getId());
			jobDispatcher.submitJob(job);
		}
	}

	private AtsTaskDO saveTask2DB(AtsTaskDO task) throws DAOException {
		return taskDao.addParentTask(task);
	}

	/**
	 * 执行此方法前，手动判断所有服务器上是否存在此任务的本地文件，如果存在，且是24小时以内的任务，则不执行；否则执行
	 */
	public void executeManualAtsTask(long taskId) throws AtsException{
		try {
			AtsTaskDO atsTask = taskDao.getTask(taskId);
			if (null == atsTask || StatusConstants.TASK_STATUS_SENT.getStatus() == atsTask.getStatus()) {
				return;
			}
			executeAtsTask(atsTask);
		} catch (Exception e) {
			throw new AtsException(e);
		}
	}

}
