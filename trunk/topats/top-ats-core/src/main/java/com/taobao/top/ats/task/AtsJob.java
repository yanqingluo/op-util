package com.taobao.top.ats.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.taobao.common.dao.persistence.exception.DAOException;
import com.taobao.common.tfs.TfsManager;
import com.taobao.top.ats.AtsException;
import com.taobao.top.ats.dao.TaskDao;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.LogDO;
import com.taobao.top.ats.domain.SubAtsTaskDO;
import com.taobao.top.ats.engine.ApiEngine;
import com.taobao.top.ats.engine.ApiRequest;
import com.taobao.top.ats.engine.ApiResponse;
import com.taobao.top.ats.monitor.TaskMonitor;
import com.taobao.top.ats.task.api.ApiManager;
import com.taobao.top.ats.util.AttributeUtil;
import com.taobao.top.ats.util.ModelKeyConstants;
import com.taobao.top.ats.util.StatusConstants;
import com.taobao.top.ats.util.TokenUtil;
import com.taobao.top.ats.util.ZipUtils;
import com.taobao.top.mq.MQException;
import com.taobao.top.mq.TopMQService;
import com.taobao.top.mq.TopMessage;
import com.taobao.top.tim.domain.SubscribeDO;
import com.taobao.top.tim.service.SamService;
import com.taobao.top.tim.service.TIMServiceException;
import com.taobao.top.xbox.threadpool.Job;

/**
 * 工作者线程。
 * 
 * @author carver.gu
 * @since 1.0, Nov 22, 2010
 */
public class AtsJob implements Job {

	private static final Log log = LogFactory.getLog(AtsJob.class);
	
	private static final String TOPATS_PARTNER_ID = "208";
	private static final String MSG_TASKID_TXT = "task_id";
	private static final String MSG_CREATED_TXT = "created";
	private static final String MSG_TASK_TXT = "task";

	public static final String SEPERATOR = "^=]=!={=@=*=(=%=)=-=+=<=&=>=.=}='=[=:=~=$";

	private SamService samService;
	private AtsTaskDO atsTask;
	private TaskDao taskDao;
	private TopMQService service;
	private TfsManager tfsManager;
	private ApiEngine apiEngine;
	private ApiManager apiManager;
	private String localPath; // 本地临时文件存储路径
	private String remotePath; // 远程临时文件存储路径
	private TaskMonitor taskMonitor;
	private LogDO taskLog;

	private List<SubAtsTaskDO> retrySubTasks = new ArrayList<SubAtsTaskDO>();

	public AtsJob(SamService samService, AtsTaskDO atsTask, TaskDao taskDao, TopMQService service, TfsManager tfsManager,
			ApiEngine apiEngine, ApiManager apiManager, String localPath, String remotePath, TaskMonitor taskMonitor) {
		this.samService = samService;
		this.atsTask = atsTask;
		this.taskDao = taskDao;
		this.service = service;
		this.tfsManager = tfsManager;
		this.apiEngine = apiEngine;
		this.apiManager = apiManager;
		this.localPath = localPath;
		this.remotePath = remotePath;
		this.taskMonitor = taskMonitor;
		this.taskLog = getTaskLogDO();
	}

	public AtsTaskDO getAtsTask() {
		return atsTask;
	}

	public void run() {
		try {
			long begin = System.currentTimeMillis();
			doJob(); // 执行任务
			long end = System.currentTimeMillis();
			taskLog.setTaskTime(end - begin);
			taskMonitor.logTask(taskLog);
		} catch (Throwable e) {
			log.error("任务处理失败: " + atsTask.getId(), e);
		}
	}

	private void doJob() throws Exception {
		// 判断任务是否已发送，如果是，则不执行此任务，仅将对应的本地文件删除
		if (StatusConstants.TASK_STATUS_SENT.getStatus() != atsTask.getStatus()) {
			// 判断任务是否已完成，如果是，则直接将任务完成的消息发送至TOPMQ
			if (StatusConstants.TASK_STATUS_DONE.getStatus() != atsTask.getStatus()) {
				List<SubAtsTaskDO> subTasks = atsTask.getSubTasks();
				if (null == subTasks || subTasks.isEmpty()) {
					log.error("执行的子任务列表为空：" + atsTask.getId());
					return;
				}

				// 判断任务是否正在执行中，如果是，则需判断子任务状态
				if (StatusConstants.TASK_STATUS_DOING.getStatus() != atsTask.getStatus()) {
					updateTaskStatus(StatusConstants.TASK_STATUS_DOING);
				}

				long abegin = System.currentTimeMillis();

				for (SubAtsTaskDO subTask : subTasks) {
					LogDO subTaskLog = getSubTaskLogDO(subTask);
					long sbegin = System.currentTimeMillis();

					// 如果子任务已完成，则直接执行下一个子任务
					if (StatusConstants.SUBTASK_STATUS_DONE.getStatus() == subTask.getStatus()) {
						continue;
					}

					// 如果子任务失败，则进入重试队列，等待重试
					if (StatusConstants.SUBTASK_STATUS_FAIL.getStatus() == subTask.getStatus()) {
						retrySubTasks.add(subTask);
						continue;
					}

					ApiRequest req = getSubTaskApiReq(subTask);
					ApiResponse rsp = executeSubTask(req);
					if (rsp.isError()) {
						// 如果执行结果出错，且为可忽略错误，则直接获得可忽略错误结果
						if (apiManager.isIgnoreError(atsTask.getApiName(), rsp.getErrCode())) {
							rsp.setResponse(apiManager.getIngoreErrorResponse(atsTask.getApiName(), req.getParameters()));
						} else {
							// 如果执行结果为可重试错误，则进入重试队列，等待重试
							if (apiManager.isRetryError(atsTask.getApiName(), rsp.getErrCode())) {
								updateSubTaskStatus(subTask, StatusConstants.SUBTASK_STATUS_FAIL);
								retrySubTasks.add(subTask);
								continue;
							} else {
								// 如果执行结果即不是可忽略错误也不是可重试错误，则直接获得错误结果
								rsp.setResponse(apiManager.getErrorResponse(rsp.getErrCode(), rsp.getErrMsg(), req.getParameters()));
							}
						}
						subTaskLog.setErrorCode(rsp.getErrCode());
					}

					saveSubTask2Local(subTask, req, rsp);
					updateSubTaskStatus(subTask, StatusConstants.SUBTASK_STATUS_DONE);

					long send = System.currentTimeMillis();
					subTaskLog.setCostTime(send - sbegin);
					if (rsp.isError()) {
						taskMonitor.logError(subTaskLog);
					} else {
						taskMonitor.logSuccess(subTaskLog);
					}
				}

				long aend = System.currentTimeMillis();
				taskLog.setSubTaskTime(aend - abegin);
				taskLog.setSuccessCount(atsTask.getSubTasks().size() - retrySubTasks.size());

				// 如果存在子任务失败，则重试
				if (!retrySubTasks.isEmpty()) {
					retryFailedSubTasks();
				}

				String path;
				if (AttributeUtil.isBigResult(atsTask.getAttributes())) {
					File srcDir = getLocalResultDir();
					String subDir = getRemoteResultSubDir();
					File destDir = getRemoteResultDir(subDir);
					String zipName = atsTask.getId() + ".zip";
					ZipUtils.zipDir(zipName, srcDir, destDir);
					path = subDir + "/" + zipName;
				} else {
					// 将任务执行结果存入TFS
					File file = getLocalResultFile();
					path = tfsManager.saveFile(file.getAbsolutePath(), tfsManager.newTfsFileName(file.getName()), null);
					if (StringUtils.isNotBlank(path)) {
						FileUtils.deleteQuietly(file);
					} else {
						log.error("存TFS失败:" + file.getName());
						return;
					}
				}

				updateTaskResponseAndStatus(path, StatusConstants.TASK_STATUS_DONE);
			}

			// 向TOPMQ发送任务执行完毕消息
			if (StatusConstants.TASK_STATUS_DONE.getStatus() == atsTask.getStatus().intValue()) {
				sendDoneMessage2TopMQ();
				updateTaskStatus(StatusConstants.TASK_STATUS_SENT);
			}
		}

		// 清除临时文件或文件夹
		if (StatusConstants.TASK_STATUS_SENT.getStatus() == atsTask.getStatus()) {
			FileUtils.deleteQuietly(getLocalFileOrDir());
		}
	}

	private void updateTaskResponseAndStatus(String tfsPath, StatusConstants sc) throws DAOException {
		atsTask.setResponse(tfsPath);
		updateTaskStatus(sc);
	}

	private void updateTaskStatus(StatusConstants sc) throws DAOException {
		atsTask.setStatus(sc.getStatus());
		taskDao.updateParentTask(atsTask);
	}

	private void sendDoneMessage2TopMQ() throws MQException, TIMServiceException {
		SubscribeDO sub = samService.getSubscribe(atsTask.getAppKey());
		if (sub != null && StringUtils.isNotBlank(sub.getNotifyUrl())) {
			TopMessage message = new TopMessage();
			message.setPartnerId(TOPATS_PARTNER_ID);
			message.setMsgId(atsTask.getId().toString());
			message.setAppkey(atsTask.getAppKey());
			message.setFormat("json");
			message.setMethod(atsTask.getApiName());
			message.setVersion("2.0");
			message.setMsgBody(buildAtsMessageBody());
			service.put(message);
		}
	}

	private String buildAtsMessageBody() {
		Map<String, Object> resultBody = new HashMap<String, Object>();
		resultBody.put(MSG_TASKID_TXT, atsTask.getId());
		resultBody.put(MSG_CREATED_TXT, atsTask.getGmtCreated());

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(MSG_TASK_TXT, new JSONObject(resultBody));

		return new JSONObject(resultMap).toString();
	}

	private void retryFailedSubTasks() throws IOException, JSONException, AtsException, DAOException {
		atsTask.setRetries(1);
		for (SubAtsTaskDO subTask : retrySubTasks) {
			ApiRequest request = getSubTaskApiReq(subTask);
			ApiResponse result = executeSubTask(request);
			// 如果结果仍出错，则直接获得错误结果
			if (result.isError()) {
				if (apiManager.isIgnoreError(atsTask.getApiName(), result.getErrCode())) {
					result.setResponse(apiManager.getIngoreErrorResponse(atsTask.getApiName(), request.getParameters()));
				} else {
					result.setResponse(apiManager.getErrorResponse(result.getErrCode(), result.getErrMsg(), request.getParameters()));
				}
				taskLog.setErrorCount(taskLog.getErrorCount() + 1);
			} else {
				taskLog.setOneErrorCount(taskLog.getOneErrorCount() + 1);
			}
			saveSubTask2Local(subTask, request, result);
			updateSubTaskStatus(subTask, StatusConstants.SUBTASK_STATUS_DONE);
		}
	}

	/**
	 * 把子任务结果写入到本地临时文件系统中。
	 */
	private void saveSubTask2Local(SubAtsTaskDO subTask, ApiRequest req, ApiResponse rsp) throws IOException, AtsException {
		if (AttributeUtil.isBigResult(atsTask.getAttributes())) {
			if (!rsp.isError()) { // 忽略错误的响应
				File taskDir = getLocalResultDir();
				File subFile = new File(taskDir, subTask.getId() + ".txt");
				String txtRsp = getBigResponse(rsp);
				if (StringUtils.isNotBlank(txtRsp)) {
					FileUtils.writeStringToFile(subFile, getBigResponse(rsp), "GBK");
				}
			}
		} else {
			StringBuilder out = new StringBuilder();
			out.append(!rsp.isError());
			out.append("\r\n").append(SEPERATOR).append("\r\n");
			out.append(apiManager.getRetrunRequest(atsTask.getApiName(), req.getParameters()));
			out.append("\r\n").append(SEPERATOR).append("\r\n");
			out.append(getBigResponse(rsp));
			out.append("\r\n").append(SEPERATOR).append("\r\n");
			OutputStream fos = null;
			try {
				fos = new FileOutputStream(getLocalResultFile(), true);
				fos.write(out.toString().getBytes("GBK"));
				fos.flush();
			} finally {
				IOUtils.closeQuietly(fos);
			}
		}
	}

	protected String getApiRootTag(String apiName) {
		if (StringUtils.isBlank(apiName)) {
			return "response";
		}

		apiName = apiName.replaceFirst("taobao\\.topats\\.", "");
		StringBuffer tag = new StringBuffer();
		for (int i = 0; i < apiName.length(); i++) {
			char c = apiName.charAt(i);
			if (c == '.') {
				tag.append("_");
			} else {
				tag.append(Character.toLowerCase(c));
			}
		}
		tag.append("_response");
		return tag.toString();
	}

	private String getBigResponse(ApiResponse rsp) {
		if (StringUtils.isBlank(rsp.getResponse()) || "{}".equals(rsp.getResponse())) {
			return null;
		}

		StringBuilder result = new StringBuilder();
		String format = atsTask.getAttributes().get("format");
		String tag = getApiRootTag(atsTask.getApiName());
		if ("xml".equals(format)) {
			result.append("<").append(tag).append(">");
			result.append(rsp.getResponse());
			result.append("</").append(tag).append(">");
		} else {
			result.append("{\"").append(tag).append("\":");
			result.append(rsp.getResponse());
			result.append("}");
		}

		return result.toString();
	}

	protected File getLocalFileOrDir() throws IOException {
		if (AttributeUtil.isBigResult(atsTask.getAttributes())) {
			return getLocalResultDir();
		} else {
			return getLocalResultFile();
		}
	}

	private File getLocalResultDir() {
		File dir = new File(localPath, atsTask.getId() + "");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	private File getLocalResultFile() throws IOException {
		File file = new File(localPath, atsTask.getId() + "_task.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	private File getRemoteResultDir(String subPath) throws Exception {
		File dir = new File(remotePath, subPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	private String getRemoteResultSubDir() throws Exception {
		String md5 = TokenUtil.getMD5Str(atsTask.getId() + "");
		int l = md5.length();
		String dir = md5.substring(0, 2) + "/" + md5.substring(l - 2, l);
		return dir.toLowerCase();
	}

	private String decorateRequest(String request) throws JSONException {
		JSONObject json = new JSONObject(request);
		json.remove(ModelKeyConstants.SERVICE_INTERFACE);
		json.remove(ModelKeyConstants.SERVICE_METHOD);
		json.remove(ModelKeyConstants.SERVICE_TYPE);
		json.remove(ModelKeyConstants.SERVICE_VERSION);
		return json.toString();
	}

	private void updateSubTaskStatus(SubAtsTaskDO subTask, StatusConstants status) throws DAOException {
		subTask.setStatus(status.getStatus());
		taskDao.updateSubTask(subTask);
	}

	private ApiResponse executeSubTask(ApiRequest req) throws JSONException {
		ApiResponse res = apiEngine.invokeApi(req);
		return res;
	}

	private ApiRequest getSubTaskApiReq(SubAtsTaskDO subTask) throws JSONException {
		JSONObject json = new JSONObject(subTask.getRequest());
		ApiRequest req = new ApiRequest();
		req.setInterfaceName(json.getString(ModelKeyConstants.SERVICE_INTERFACE));
		req.setInterfaceMethod(json.getString(ModelKeyConstants.SERVICE_METHOD));
		req.setInterfaceVersion(json.getString(ModelKeyConstants.SERVICE_VERSION));
		// FIXME: 多重JSON与字符串转换，需要优化
		String reqWithoutHsf = decorateRequest(subTask.getRequest());
		Map<String, String> params = jsonString2Map(reqWithoutHsf);
		req.setParameters(params);
		return req;
	}

	private Map<String, String> jsonString2Map(String jsonStr) throws JSONException {
		JSONObject json = new JSONObject(jsonStr);
		Map<String, String> result = new HashMap<String, String>();
		Iterator<?> keys = json.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			result.put(key, json.get(key).toString());
		}
		return result;
	}

	private LogDO getTaskLogDO() {
		LogDO logDO = new LogDO();
		logDO.setMethod(atsTask.getApiName());
		logDO.setAppkey(atsTask.getAppKey());
		logDO.setTaskId(atsTask.getId());
		return logDO;
	}

	private LogDO getSubTaskLogDO(SubAtsTaskDO subTask) {
		LogDO logDO = new LogDO();
		logDO.setMethod(atsTask.getApiName());
		logDO.setAppkey(atsTask.getAppKey());
		logDO.setSubTaskId(subTask.getId());
		return logDO;
	}

	public String getKey() {
		return atsTask.getPriority() + "";
	}

}