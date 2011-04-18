package com.taobao.top.ats.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author moling
 * @since 1.0, 2010-8-19
 */
public class ParamsDO {
	//批量操作的最小子任务个数，默认为1
	private Integer paramsMinSize;
	//批量操作的最大子任务个数，默认为40，最多不超过200
	private Integer paramsMaxSize;
	
	//分片参数列表
	private RangeParameterDO paramRange;
	//需要分割的参数列表
	private List<ParameterDO> paramsParse;
	//不需要分割的参数列表
	private List<ParameterDO> paramsKeep;
	//只做校验的参数列表
	private List<ParameterDO> paramsCheck;
	
	public Integer getParamsMinSize() {
		return this.paramsMinSize;
	}
	public void setParamsMinSize(Integer paramsMinLength) {
		this.paramsMinSize = paramsMinLength;
	}
	public Integer getParamsMaxSize() {
		return this.paramsMaxSize;
	}
	public void setParamsMaxSize(Integer paramsMaxLength) {
		this.paramsMaxSize = paramsMaxLength;
	}
	public List<ParameterDO> getParamsParse() {
		return this.paramsParse;
	}
	public void setParamsParse(List<ParameterDO> paramsParse) {
		this.paramsParse = paramsParse;
	}
	public List<ParameterDO> getParamsKeep() {
		return this.paramsKeep;
	}
	public void setParamsKeep(List<ParameterDO> paramsKeep) {
		this.paramsKeep = paramsKeep;
	}
	public List<ParameterDO> getParamsCheck() {
		return this.paramsCheck;
	}
	public void setParamsCheck(List<ParameterDO> paramsCheck) {
		this.paramsCheck = paramsCheck;
	}
	public void addCheckParam(ParameterDO param) {
		if (null == paramsCheck) {
			paramsCheck = new ArrayList<ParameterDO>();
		}
		paramsCheck.add(param);
	}
	public void addKeepParam(ParameterDO param) {
		if (null == paramsKeep) {
			paramsKeep = new ArrayList<ParameterDO>();
		}
		paramsKeep.add(param);
	}
	public void addParseParam(ParameterDO param) {
		if (null == paramsParse) {
			paramsParse = new ArrayList<ParameterDO>();
		}
		paramsParse.add(param);
	}
	public RangeParameterDO getParamRange() {
		return this.paramRange;
	}
	public void setParamRange(RangeParameterDO paramRange) {
		this.paramRange = paramRange;
	}
	
	
	
}
