package com.taobao.top.ats.service;

import java.util.Map;

import com.taobao.top.ats.domain.ResultSet;

/**
 * ATS服务接口。
 * 
 * @author carver.gu
 * @since 1.0, Aug 18, 2010
 */
public interface TaskService {

	public ResultSet addTask(Map<String, String> params);

	public ResultSet getResult(Long taskId, String appKey);

}
