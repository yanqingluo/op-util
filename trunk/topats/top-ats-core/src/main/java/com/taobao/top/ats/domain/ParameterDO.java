package com.taobao.top.ats.domain;

/**
 * 
 * @author moling
 * @since 1.0, 2010-8-19
 */
public class ParameterDO {
	//参数对外的名字（父任务参数名称），一定要有
	private String name;
	//参数在后台api的名字（子任务参数名称）,没有表示不在子任务中，只做校验
	private String  targetName;
	//此参数是否支持按照固定分隔符分割，默认false（不可分割）
	private Boolean enableSplit;
	//参数的分隔符，如果指定了参数是可以分割的，必需指定分隔符，不指定报错
	private String separator;
	//此参数分割成子任务参数是否可以为null或"",默认为true（可以为null）
	private Boolean targetEnableNull;
	//分割后子参数类型，目前支持string、number、date、boolean、price（只能正），不设置默认不对参数格式进行校验
	private String targetType;
	//是否只是用于前置校验，默认不是，标识为true的是前置校验参数
	private Boolean forCheck;
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTargetName() {
		return this.targetName;
	}
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	public Boolean getEnableSplit() {
		return this.enableSplit;
	}
	public void setEnableSplit(Boolean enableSplit) {
		this.enableSplit = enableSplit;
	}
	public String getSeparator() {
		return this.separator;
	}
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	public Boolean getTargetEnableNull() {
		return this.targetEnableNull;
	}
	public void setTargetEnableNull(Boolean targetEnableNull) {
		this.targetEnableNull = targetEnableNull;
	}
	public String getTargetType() {
		return this.targetType;
	}
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	public Boolean getForCheck() {
		return forCheck;
	}
	public void setForCheck(Boolean forCheck) {
		this.forCheck = forCheck;
	}
	
	
}
