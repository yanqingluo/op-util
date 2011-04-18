package com.taobao.top.notify.domain.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 授权查询条件。
 * 
 * @author fengsheng
 * @since 1.0, Jan 26, 2010
 */
public class AuthorizeQuery extends PageQuery {

	private String appKey;
	private List<Long> userIds;
	private Date startDate;
	private Date endDate;
	private int status;

	public String getAppKey() {
		return this.appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public List<Long> getUserIds() {
		return this.userIds;
	}
	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}
	public void addUserId(Long userId) {
		if (this.userIds == null) {
			this.userIds = new ArrayList<Long>();
		}
		if (!this.userIds.contains(userId)) {
			this.userIds.add(userId);
		}
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
	public int getStatus() {
		return this.status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
