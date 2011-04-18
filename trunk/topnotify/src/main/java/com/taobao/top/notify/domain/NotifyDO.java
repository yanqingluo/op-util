package com.taobao.top.notify.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息持久对象。
 * 
 * @author fengsheng
 * @since 1.0, Dec 16, 2009
 */
public class NotifyDO implements Serializable {

	private static final long serialVersionUID = 4558649385369597309L;

	private Long id;
	private String appKey;
	private Integer category;
	private Integer bizType;
	private Integer status;
	private Long userId;
	private String userName;
	private String content;
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
	public String getContent() {
		return this.content;
	}
	public void setContent(String content) {
		this.content = content;
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
