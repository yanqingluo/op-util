package com.taobao.top.ats.util;

/**
 * 
 * @author moling
 * @since 1.0, 2010-8-20
 */
public enum StatusConstants {
	TASK_STATUS_NEW(0, "new"),
	TASK_STATUS_DOING(1, "doing"),
	TASK_STATUS_DONE(2, "done"),
	TASK_STATUS_FAIL(-1, "fail"),
	TASK_STATUS_SENT(3, "sent"),
	SUBTASK_STATUS_FAIL(-1, "fail"),
	SUBTASK_STATUS_NEW(0, "new"),
	SUBTASK_STATUS_DONE(2, "done");
	
	private int status;
	private String statusName;
	
	private StatusConstants(int status, String statusName) {
		this.status = status;
		this.statusName = statusName;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusName() {
		return this.statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	
}
