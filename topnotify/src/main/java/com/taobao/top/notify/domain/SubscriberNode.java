package com.taobao.top.notify.domain;

import java.io.Serializable;

/**
 * 订阅节点。
 * 
 * @author moling
 * @since 1.0, 2009-12-14
 */
public class SubscriberNode implements Serializable {

	private static final long serialVersionUID = 2136640884753915130L;

	private String statusName;
	private boolean bizTypeAble;
	
	public String getStatusName() {
		return this.statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public boolean isBizTypeAble() {
		return this.bizTypeAble;
	}
	public void setBizTypeAble(boolean bizTypeAble) {
		this.bizTypeAble = bizTypeAble;
	}
	
	public int getStatus() {
		return NotifyEnum.valueOf(statusName).getStatus();
	}

}
