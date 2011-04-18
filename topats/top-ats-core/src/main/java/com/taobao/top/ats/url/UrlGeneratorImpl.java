package com.taobao.top.ats.url;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import com.alibaba.common.lang.StringUtil;
import com.taobao.common.dao.persistence.exception.DAOException;
import com.taobao.top.ats.AtsException;
import com.taobao.top.ats.dao.TaskDao;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.util.TokenUtil;

public class UrlGeneratorImpl implements UrlGenerator {

	private String secret;
	private String downloadUrlPre;
	private String tokenPathPre;
	
	public void setTokenPathPre(String tokenPathPre) {
		this.tokenPathPre = tokenPathPre;
	}

	public void setDownloadUrlPre(String downloadUrlPre) {
		this.downloadUrlPre = downloadUrlPre;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	private TaskDao taskDao;

	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}

	public String getAtsDownloadUrl(AtsTaskDO taskDO) throws AtsException {
		if (null == taskDO)
			throw new AtsException("异步任务不能为空");

		// 1. token
		String token = taskDO.getAttributes().get(AtsTaskDO.DOWNLOAD_TOKEN);
		if (token == null) {
			// token generator
			token = TokenUtil.generateToken(32);
			taskDO.addAttributes(AtsTaskDO.DOWNLOAD_TOKEN, token);
			try {
				taskDao.updateParentTask(taskDO);
			} catch (DAOException e) {
				throw new AtsException("更新TOKEN出错", e);
			}
		}

		// 2. 生成token文件路径
		String tokenFile = getTokenFilePath(token);
		try {
			FileUtils.writeStringToFile(new File(tokenFile), taskDO.getResponse(), "utf-8");
		} catch (IOException e) {
			throw new AtsException("生成异步文件出错", e);
		}

		// 3. 生成下载链接
		TreeMap<String, String> tmap = new TreeMap<String, String>();
		tmap.put("token", token);
		tmap.put("appkey", taskDO.getAppKey());
		try {
			return TokenUtil.createDownLoadUrl(downloadUrlPre, secret, tmap);
		} catch (Exception e) {
			throw new AtsException("加密url出错", e);
		}
	}
	
	public String getTokenFilePath(String token) throws AtsException {
		if (null == token) {
			throw new AtsException("未传入token串");
		}
		StringBuffer sb = new StringBuffer();
		//容错：如果配置文件中定义的token prefix结尾包含斜线（无论Windows平台的\还是linux平台的/），自动删除之
		sb.append(StringUtil.trimEnd(tokenPathPre, "/\\"));
		sb.append(File.separator);
		sb.append(StringUtil.substring(token, 0, 2));
		sb.append(File.separator);
		sb.append(StringUtil.substring(token, 2, 4));
		sb.append(File.separator);
		sb.append(token);
		return sb.toString();
	}
}
