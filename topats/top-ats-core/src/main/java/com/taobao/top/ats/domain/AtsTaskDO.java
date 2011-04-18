package com.taobao.top.ats.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.taobao.top.ats.util.AttributeUtil;

/**
 * 主任务数据对象。
 * 
 * @author carver.gu
 * @since 1.0, Aug 18, 2010
 */
public class AtsTaskDO implements Serializable {

	private static final long serialVersionUID = -3857247198946349698L;

	public static final String DOWNLOAD_TOKEN = "token";

	private Long id; // 任务编号
	private String appKey; // 应用编号
	private String apiName; // 接口名称
	private Integer priority; // 优先级
	private Integer retries; // 重试次数
	private Integer status; // 任务状态
	private String response; // 任务执行结果路径
	private Date gmtCreated;
	private List<SubAtsTaskDO> subTasks;
	private Map<String, String> attributes; // 属性
	private String downloadUrl; //下载地址

	public void setAttributesString(String attributesString) {
		attributes = AttributeUtil.toMap(attributesString);
	}

	public String getAttributesString() {
		return AttributeUtil.toString(this.attributes);
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAppKey() {
		return this.appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getApiName() {
		return this.apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public Integer getPriority() {
		return this.priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getRetries() {
		return this.retries;
	}

	public void setRetries(Integer retries) {
		this.retries = retries;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getResponse() {
		return this.response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Date getGmtCreated() {
		return this.gmtCreated;
	}

	public void setGmtCreated(Date gmtCreate) {
		this.gmtCreated = gmtCreate;
	}

	public List<SubAtsTaskDO> getSubTasks() {
		return this.subTasks;
	}

	public void setSubTasks(List<SubAtsTaskDO> subTasks) {
		this.subTasks = subTasks;
	}
	
	public void addAttributes(String key, String value) {
		if (null == attributes) {
			attributes = new HashMap<String, String>();
		}
		if (StringUtils.isNotBlank(key)) {
			attributes.put(key, value);
		}
	}
	
	public void removeAttributes(String key) {
		if (null == attributes) {
			return;
		}
		
		if (StringUtils.isNotBlank(key)) {
			attributes.remove(key);
		}
	}

	public String getDownloadUrl() {
		return this.downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

}
