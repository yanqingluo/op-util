package com.taobao.top.notify.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户授权。
 * 
 * @author fengsheng
 * @since 1.0, Jan 26, 2010
 */
public class AuthorizeDO implements Serializable {

	private static final long serialVersionUID = -6327184411575842021L;
	public static final int STATUS_NORMAL = 1;
	public static final int STATUS_EXPIRED = 0;

	private Long id;
	private String appKey;
	private Long userId;
	private String userName;
	private Date startDate;
	private Date endDate;
	private Integer status;
	private String email;
	private Date gmtCreate;
	private Date gmtModified;

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

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getGmtCreate() {
		return this.gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return this.gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

}
