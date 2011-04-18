package com.taobao.top.ats.domain;

/**
 * 
 * @author moling
 * @since 1.0, 2010-11-16
 */
public class LogDO {
	/**
	 * 以下是公有的必传记录参数
	 */
	//对外服务的api名称
	private String method;
	//创建任务的appKey
	private String appkey;
	
	/**
	 * 以下是调用TaskLog必传的参数
	 */
	//任务id号
	private Long taskId;
	//任务执行总时间
	private Long taskTime;
	//子任务执行总时间
	private Long subTaskTime;
	//一次执行就成功的子任务数
	private Integer successCount = 0;
	//重试一次也成功的子任务数
	private Integer oneErrorCount = 0;
	//执行失败的子任务数
	private Integer errorCount = 0;

	/**
	 * 以下是SubTaskLog必传的参数
	 */
	//执行子任务耗费的时间
	private Long costTime;
	//子任务id号
	private Long subTaskId;
	//子任务返回的错误码
	private String errorCode;
	
	public String getMethod() {
		return this.method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getAppkey() {
		return this.appkey;
	}
	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}
	public Long getTaskId() {
		return this.taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public Long getTaskTime() {
		return this.taskTime;
	}
	public void setTaskTime(Long taskTime) {
		this.taskTime = taskTime;
	}
	public Long getSubTaskTime() {
		return this.subTaskTime;
	}
	public void setSubTaskTime(Long subTaskTime) {
		this.subTaskTime = subTaskTime;
	}
	public Integer getSuccessCount() {
		return this.successCount;
	}
	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}
	public Integer getOneErrorCount() {
		return this.oneErrorCount;
	}
	public void setOneErrorCount(Integer oneErrorCount) {
		this.oneErrorCount = oneErrorCount;
	}
	public Integer getErrorCount() {
		return this.errorCount;
	}
	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}
	public Long getCostTime() {
		return this.costTime;
	}
	public void setCostTime(Long costTime) {
		this.costTime = costTime;
	}
	public Long getSubTaskId() {
		return this.subTaskId;
	}
	public void setSubTaskId(Long subTaskId) {
		this.subTaskId = subTaskId;
	}
	public String getErrorCode() {
		return this.errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
}
