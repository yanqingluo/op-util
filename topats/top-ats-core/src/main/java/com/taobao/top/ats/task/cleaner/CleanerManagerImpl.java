package com.taobao.top.ats.task.cleaner;

import java.io.File;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
import com.taobao.top.ats.url.UrlGenerator;
import com.taobao.top.ats.util.AttributeUtil;

public class CleanerManagerImpl implements CleanerManager {

	private static final Log log = LogFactory.getLog(CleanerManagerImpl.class);

	private static final int DEFAULT_PAGE_SIZE = 100;

	private TaskDao taskDao;
	private TfsManager tfsManager;
	private UrlGenerator urlGenerator;

	private String localPath;
	private String remotePath;
	private String tokenPathPre;
	private int taskExpiredDays = 6;
	private String ipAddress;

	public int getTaskExpiredDays() {
		return this.taskExpiredDays;
	}

	public void setTaskExpiredDays(int taskExpiredDays) {
		this.taskExpiredDays = taskExpiredDays;
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

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public void setTokenPathPre(String tokenPathPre) {
		this.tokenPathPre = tokenPathPre;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * 清除本地临时文件
	 */
	public void cleanLocalFile() {
		File dir = new File(localPath);
		Collection<?> files = FileUtils.listFiles(dir, null, true);

		log.warn("开始执行本地历史文件清理...");

		for (Object tmp : files) {
			File file = (File) tmp;
			Date oldDate = DateUtils.addDays(new Date(), -taskExpiredDays);
			if (FileUtils.isFileOlder(file, oldDate)) {
				FileUtils.deleteQuietly(file);
			}
		}

		log.warn("此次本地历史文件清理结束。");
	}

	/**
	 * 清除DB,TFS和资源服务器上的历史任务数据, 另外定时刷新API模板也在这里完成 为了保证只被一台ATS服务器调用，因此需要做是否已启动的校验
	 */
	public void cleanSharedData() {
		Date now = new Date();
		log.warn("开始执行共享历史任务数据清理...");

		// 1. 查询DB中可清除的记录总数
		Date endCreated = DateUtils.addDays(now, -taskExpiredDays);
		int taskPageCount = getCleanableTaskPageCount(endCreated);

		// 2. 根据每次读出的记录数，分页读出所有记录，并执行相关的TFS和资源服务器上文件清理　
		PageList<AtsTaskDO> cleanableResult;
		List<AtsTaskDO> cleanableTasks;

		QueryTaskDO query = new QueryTaskDO();
		query.setEndCreated(endCreated);
		query.setPageSize(DEFAULT_PAGE_SIZE);

		for (int i = taskPageCount; i > 0; --i) {
			query.setPageNo(i);
			try {
				cleanableResult = taskDao.queryCleanableTasks(query);
			} catch (DAOException e) {
				log.error("查询可清理任务出错", e);
				continue;
			}
			if (cleanableResult != null) {
				cleanableTasks = cleanableResult.getData();
				for (AtsTaskDO atsTaskDO : cleanableTasks) {
					// (1). 删除top_ats_sub_task中的历史记录
					try {
						taskDao.deleteSubTasks(atsTaskDO.getId());
					} catch (DAOException e) {
						log.error("清理" + atsTaskDO.getId() + "的子任务时出错", e);
					}
					// (2). 根据是否为大任务(BigTask)判断是清理资源服务器还是清理TFS
					if (AttributeUtil.isBigResult(atsTaskDO.getAttributes())) {
						// 大任务需要清理token文件和资源服务器文件(大任务结果不存入TFS)
						cleanTaskResource(atsTaskDO);
					} else {
						// 非大任务只需清理TFS上的结果
						tfsManager.unlinkFile(atsTaskDO.getResponse(), null);
					}
					try {
						taskDao.deleteParentTask(atsTaskDO.getId());
					} catch (DAOException e) {
						log.error("清理数据库记录" + atsTaskDO.getId() + "出错", e);
					}
				}
			}
		}
		// 删除资源服务器上的可能保存了七天而未被删除的文件
		cleanAtsTokenOrResource(tokenPathPre, now);
		cleanAtsTokenOrResource(remotePath, now);

		log.warn("此次执行共享历史任务数据清理结束。");
	}

	/**
	 * 清理资源服务器上的历史任务文件 此实现根据文件的保存时间，而和具体任务无关 是清理token文件或者是资源文件，仅与指定的rootPath路径有关
	 */
	private void cleanAtsTokenOrResource(String rootPath, Date now) {
		File dir = new File(rootPath);
		if (!dir.exists()) {
			return;
		}

		Date oldDate = DateUtils.addDays(now, -taskExpiredDays);
		Collection<?> files = FileUtils.listFiles(dir, null, true);
		for (Object tmp : files) {
			File file = (File) tmp;
			if (FileUtils.isFileOlder(file, oldDate)) {
				FileUtils.deleteQuietly(file);
			}
		}
	}

	/**
	 * 根据指定的AtsTaskDO，清除其token和资源文件
	 */
	private void cleanTaskResource(AtsTaskDO atsTaskDO) {
		// 清理资源文件
		if (StringUtils.isNotBlank(atsTaskDO.getResponse())) {
			File resourceFile = new File(remotePath, atsTaskDO.getResponse());
			if (resourceFile.exists()) {
				resourceFile.delete();
			}
		}

		// 清除token文件
		try {
			cleanAtsToken(atsTaskDO);
		} catch (AtsException e) {
			log.error("清除指定的token出错", e);
		}
	}

	/**
	 * 清除指定的token文件
	 */
	private void cleanAtsToken(AtsTaskDO atsTaskDO) throws AtsException {
		String token = atsTaskDO.getAttributes().get(AtsTaskDO.DOWNLOAD_TOKEN);
		if (StringUtils.isNotBlank(token)) {
			String tokenPath = urlGenerator.getTokenFilePath(token);
			File tokenFile = new File(tokenPath);
			if (tokenFile.exists()) {
				tokenFile.delete();
			}
		}
	}

	/**
	 * 获得可执行清理的历史任务的记录数
	 */
	private int getCleanableTaskPageCount(Date endCreated) {
		QueryTaskDO query = new QueryTaskDO();
		int cleanableTaskCount = 0;
		int taskPageCount = 0;
		try {
			query.setEndCreated(endCreated);
			cleanableTaskCount = taskDao.queryCleanableTaskCount(query);
		} catch (DAOException e) {
			log.error("获得可清理任务数失败", e);
		}
		if (cleanableTaskCount != 0) {
			if (cleanableTaskCount % DEFAULT_PAGE_SIZE == 0) {
				taskPageCount = cleanableTaskCount / DEFAULT_PAGE_SIZE;
			} else {
				taskPageCount = cleanableTaskCount / DEFAULT_PAGE_SIZE + 1;
			}
		}

		return taskPageCount;
	}

	/**
	 * 检查是否指定本机启动定时清理任务。
	 */
	public boolean isCleanerAvailable() {
		String ip = null;
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			ip = inetAddress.getHostAddress().toString();
		} catch (Exception e) {
			return false;
		}
		return ip.equals(ipAddress);
	}

}