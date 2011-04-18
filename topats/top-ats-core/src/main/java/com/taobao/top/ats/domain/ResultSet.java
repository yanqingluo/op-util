package com.taobao.top.ats.domain;

/**
 * 
 * @author moling
 * @since 1.0, 2010-8-19
 */
public class ResultSet {
	private String errorCode;
	private String errorMsg;
	private Exception exception;
	private AtsTaskDO result; // 结果

	public boolean isError() {
		return this.errorCode != null || this.errorMsg != null;
	}
	public String getErrorCode() {
		return this.errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return this.errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public Exception getException() {
		return this.exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public AtsTaskDO getResult() {
		return this.result;
	}
	public void setResult(AtsTaskDO result) {
		this.result = result;
	}
	
}
