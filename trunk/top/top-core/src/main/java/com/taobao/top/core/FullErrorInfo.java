package com.taobao.top.core;

/**
 * Info returned to isv.
 * @author <a href="mailto:huaisu@taobao.com">huaisu</a>
 * @since 2010-8-3
 */
public class FullErrorInfo {
	private ErrorCode errorCode;
	private String subErrCode;
	private String msg;
	private String subMsg;
	
	public ErrorCode getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	public String getSubErrCode() {
		return subErrCode;
	}
	public void setSubErrCode(String subErrCode) {
		this.subErrCode = subErrCode;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getSubMsg() {
		return subMsg;
	}
	public void setSubMsg(String subMsg) {
		this.subMsg = subMsg;
	}
	
}
