package com.taobao.top.notify.domain;

import java.util.List;

import com.taobao.top.tim.domain.AttributeDO;

public class NotifyInfo {
	
	private String appKey;
	
	private String isNotify;
	
	private String notifyUrl;
	
	private String subscriptions;
	
	//attributes标记列表
	private List<AttributeDO> attributes;

	public List<AttributeDO> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(List<AttributeDO> attributes) {
		this.attributes = attributes;
	}

	public String getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(String subscriptions) {
		this.subscriptions = subscriptions;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getIsNotify() {
		return isNotify;
	}

	public void setIsNotify(String isNotify) {
		this.isNotify = isNotify;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	

}
