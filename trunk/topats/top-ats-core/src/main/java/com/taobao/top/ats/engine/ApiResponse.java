package com.taobao.top.ats.engine;

import org.apache.commons.lang.StringUtils;

public class ApiResponse {

	private String errCode;
	private String errMsg;
	private String response;

	public String getErrCode() {
		return this.errCode;
	}
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
	public String getErrMsg() {
		return this.errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public String getResponse() {
		return this.response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public boolean isError() {
		return StringUtils.isNotBlank(errCode) || StringUtils.isNotBlank(errMsg);
	}

}
