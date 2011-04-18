package com.taobao.top.notify.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息结构。
 * 
 * @author fengsheng
 * @since 1.0, Dec 14, 2009
 */
public class Notify implements Serializable, Cloneable {

	private static final long serialVersionUID = -9090696011652376700L;

	private Long id;
	private String appKey;
	private Integer category;
	private Integer bizType;
	private Integer status;
	private Long userId;
	private String userName;
	private Date created;
	private Date modified;
	private String isNotify;
	private String notifyUrl;
	private String subscriptions;
	//消息所属的用户角色
	private Long userRole;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(String subscriptions) {
		this.subscriptions = subscriptions;
	}

	private Map<String, Object> content;
	
	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getAppKey() {
		return this.appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getIsNotify() {
		return isNotify;
	}

	public void setIsNotify(String inNotify) {
		this.isNotify = inNotify;
	}

	public Integer getCategory() {
		return this.category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public Integer getBizType() {
		return this.bizType;
	}

	public void setBizType(Integer bizType) {
		this.bizType = bizType;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getModified() {
		return this.modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Map<String, Object> getContent() {
		return this.content;
	}

	public void setContent(Map<String, Object> content) {
		this.content = content;
	}
	
	public Long getUserRole() {
		//如果消息没有被指定过特殊的角色，默认是所有角色
		if (null == userRole) {
			userRole = 0L;
		}
		return this.userRole;
	}

	public void setUserRole(Long userRole) {
		this.userRole = userRole;
	}

	public void addContent(String key, Object value) {
		if (this.content == null) {
			this.content = new HashMap<String, Object>();
		}
		this.content.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public Notify clone() {
		Notify notify = null;
		try {
			notify = (Notify) super.clone();
			HashMap<String, Object> map = (HashMap<String, Object>) this.content;
			notify.setContent((HashMap<String, Object>) map.clone());
		} catch (Exception e) {
		}
		return notify;
	}

}
