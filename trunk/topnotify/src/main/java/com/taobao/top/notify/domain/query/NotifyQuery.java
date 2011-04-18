package com.taobao.top.notify.domain.query;

import java.util.Calendar;
import java.util.Date;

/**
 * 消息查询条件。
 * 
 * @author fengsheng
 * @since 1.0, Dec 17, 2009
 */
public class NotifyQuery extends PageQuery {

	private String appKey;
	private Integer category;
	private Integer bizType;
	private Integer status;
	private Long userId;
	private Date startModified;
	private Date endModified;

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

	public Date getStartModified() {
		if (this.startModified == null) {
			return getDefaultStartModified();
		} else {
			return this.startModified;
		}
	}

	public void setStartModified(Date startModified) {
		this.startModified = startModified;
	}

	public Date getEndModified() {
		if (this.endModified == null) {
			return getDefaultEndModified();
		} else {
			return this.endModified;
		}
	}

	public void setEndModified(Date endModified) {
		this.endModified = endModified;
	}

	public boolean isSearchModified() {
		return startModified != null && endModified != null;
	}

	private Date getDefaultStartModified() {
		Calendar cal = Calendar.getInstance();
		if (this.endModified != null) {
			cal.setTime(this.endModified);
		}

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	private Date getDefaultEndModified() {
		Calendar cal = Calendar.getInstance();
		if (this.startModified != null) {
			cal.setTime(this.startModified);
		}

		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}

}
