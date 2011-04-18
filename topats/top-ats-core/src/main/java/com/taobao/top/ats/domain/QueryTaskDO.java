package com.taobao.top.ats.domain;

import java.util.Date;

/**
 * 任务查询条件。
 * 
 * @author moling
 * @since 1.0, 2010-8-20
 */
public class QueryTaskDO {

	// 需要查询的任务的状态
	private Integer taskStatus;
	// 查询的分页页码
	private Integer pageNo = 1;
	// 查询的分页个数
	private Integer pageSize = 40;
	// 任务创建的起始时间点
	private Date startCreated;
	// 任务创建的最大时间点
	private Date endCreated;

	public Integer getTaskStatus() {
		return this.taskStatus;
	}
	public void setTaskStatus(Integer taskStatus) {
		this.taskStatus = taskStatus;
	}
	public Integer getPageNo() {
		return this.pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Integer getPageSize() {
		return this.pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Date getStartCreated() {
		return this.startCreated;
	}
	public void setStartCreated(Date startCreated) {
		this.startCreated = startCreated;
	}
	public Date getEndCreated() {
		return this.endCreated;
	}
	public void setEndCreated(Date endCreated) {
		this.endCreated = endCreated;
	}
	public int getStartRow() {
		if (this.pageNo > 0) {
			return (this.pageNo - 1) * this.pageSize + 1;
		} else {
			return 1;
		}
	}
	public int getEndRow() {
		return getStartRow() + this.pageSize;
	}

}
