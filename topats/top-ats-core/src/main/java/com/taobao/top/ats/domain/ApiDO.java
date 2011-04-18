package com.taobao.top.ats.domain;

import java.util.List;

/**
 * 
 * @author moling
 * @since 1.0, 2010-8-19
 */
public class ApiDO {
	//api的名字
	private String taskName;
	//需要调用api进行前置校验（如交易改价进行支付宝签名校验）的api调用方法，如：hsf
	private String preCheckType;
	//前置校验调用的接口名称
	private String preCheckInterface;
	//前置校验调用的方法名称
	private String preCheckMethod;
	//前置校验hsf服务版本号
	private String preCheckVersion;
	//子任务调用方法，如：hsf
	private String subTaskType;
	//子任务调用的接口名称
	private String subTaskInterface;
	//子任务调用的方法名称
	private String subTaskMethod;
	//子任务调用的服务版本号
	private String subTaskVersion;
	
	//api优先级 moling 2010-11-18
	private Integer level;
	//可执行时间段列表 moling 2010-11-18
	private List<TimePeriod> timePeriods;
	//是否大任务类型api moling 2010-11-18
	private Boolean isBigResult;
	
	//参数规则
	private ParamsDO paramsRule;
	//子任务执行时如果报这个错误，默认执行结果为成功，根据ResponseParameter和入参拼装结果
	private List<String> subTaskIgnoreFails;
	//子任务执行时如果报这些错误，需要重试一次，如果仍旧失败，将错误信息拼装进结果中
	private List<String> subTaskRetryFails;
	//子任务入参需要填充的参数
	private List<String> subTaskSendOutRequests;
	//子任务发生IgnoreFails时，需要返回的参数，遇到created或modified时new一个date放进去。
	//根据#将配置文件分割放进来,偶数位的标记取入参中的值填充
	//json格式的返回
	private String[] subTaskSendOutResultsJson;
	//xml格式的返回
	private String[] subTaskSendOutResultsXml;
	
	public String getTaskName() {
		return this.taskName;
	}
	public void setTaskName(String apiName) {
		this.taskName = apiName;
	}
	public String getPreCheckType() {
		return this.preCheckType;
	}
	public void setPreCheckType(String preCheckType) {
		this.preCheckType = preCheckType;
	}
	public String getPreCheckInterface() {
		return this.preCheckInterface;
	}
	public void setPreCheckInterface(String preCheckInterface) {
		this.preCheckInterface = preCheckInterface;
	}
	public String getPreCheckMethod() {
		return this.preCheckMethod;
	}
	public void setPreCheckMethod(String preCheckMethod) {
		this.preCheckMethod = preCheckMethod;
	}
	public String getSubTaskType() {
		return this.subTaskType;
	}
	public void setSubTaskType(String subTaskType) {
		this.subTaskType = subTaskType;
	}
	public String getSubTaskInterface() {
		return this.subTaskInterface;
	}
	public void setSubTaskInterface(String subTaskInterface) {
		this.subTaskInterface = subTaskInterface;
	}
	public String getSubTaskMethod() {
		return this.subTaskMethod;
	}
	public void setSubTaskMethod(String subTaskMethod) {
		this.subTaskMethod = subTaskMethod;
	}
	public List<String> getSubTaskIgnoreFails() {
		return this.subTaskIgnoreFails;
	}
	public void setSubTaskIgnoreFails(List<String> subTaskIgnoreFails) {
		this.subTaskIgnoreFails = subTaskIgnoreFails;
	}
	public List<String> getSubTaskRetryFails() {
		return this.subTaskRetryFails;
	}
	public void setSubTaskRetryFails(List<String> subTaskRetryFails) {
		this.subTaskRetryFails = subTaskRetryFails;
	}
	public List<String> getSubTaskSendOutRequests() {
		return this.subTaskSendOutRequests;
	}
	public void setSubTaskSendOutRequests(List<String> subTaskSendOutRequests) {
		this.subTaskSendOutRequests = subTaskSendOutRequests;
	}
	public String[] getSubTaskSendOutResultsJson() {
		return this.subTaskSendOutResultsJson;
	}
	public void setSubTaskSendOutResultsJson(String[] subTaskSendOutResultsJson) {
		this.subTaskSendOutResultsJson = subTaskSendOutResultsJson;
	}
	public ParamsDO getParamsRule() {
		return this.paramsRule;
	}
	public void setParamsRule(ParamsDO paramsRule) {
		this.paramsRule = paramsRule;
	}
	public String getPreCheckVersion() {
		return this.preCheckVersion;
	}
	public void setPreCheckVersion(String preCheckVersion) {
		this.preCheckVersion = preCheckVersion;
	}
	public String getSubTaskVersion() {
		return this.subTaskVersion;
	}
	public void setSubTaskVersion(String subTaskVersion) {
		this.subTaskVersion = subTaskVersion;
	}
	public Integer getLevel() {
		return this.level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public List<TimePeriod> getTimePeriods() {
		return this.timePeriods;
	}
	public void setTimePeriods(List<TimePeriod> timePeriods) {
		this.timePeriods = timePeriods;
	}
	public Boolean getIsBigResult() {
		return this.isBigResult;
	}
	public void setIsBigResult(Boolean isBigResult) {
		this.isBigResult = isBigResult;
	}
	public String[] getSubTaskSendOutResultsXml() {
		return this.subTaskSendOutResultsXml;
	}
	public void setSubTaskSendOutResultsXml(String[] subTaskSendOutResultsXml) {
		this.subTaskSendOutResultsXml = subTaskSendOutResultsXml;
	}
	
}
