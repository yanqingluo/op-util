package com.taobao.top.ats.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 子任务数据对象。
 * 
 * @author carver.gu
 * @since 1.0, Aug 19, 2010
 */
public class SubAtsTaskDO implements Serializable, Cloneable {

	private static final long serialVersionUID = -5054504423912419646L;

	private Long id; // 子任务编号
	private Long pid; // 父任务编号
	private String request; // 请求参数
	private String response; // 响应结果（冗余字段）
	private Integer status; // 执行状态
	private Date gmtCreated;
	private Map<String, String> requestMap;
	private Boolean isSuccess; //子任务执行是否成功，冗余字段，返回结果用 moling 2010-10-11

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPid() {
		return this.pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getRequest() {
		return this.request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return this.response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getGmtCreated() {
		return this.gmtCreated;
	}

	public void setGmtCreated(Date gmtCreated) {
		this.gmtCreated = gmtCreated;
	}

	public Map<String, String> getRequestMap() {
		return this.requestMap;
	}

	public void setRequestMap(Map<String, String> requestMap) {
		this.requestMap = requestMap;
	}
	
	public Boolean getIsSuccess() {
		return this.isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public void addRequestParameter(String key, String value) {
		if (null == requestMap) {
			requestMap = new HashMap<String, String>();
		}
		requestMap.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public SubAtsTaskDO clone() {
		SubAtsTaskDO task = null;
		try {
			task = (SubAtsTaskDO) super.clone();
			HashMap<String, String> map = (HashMap<String, String>) this.requestMap;
			task.setRequestMap((Map<String, String>) map.clone());
		} catch (Exception e) {
		}
		return task;
	}

}
