package com.taobao.top.pageapi.core.impl;

import com.taobao.top.core.framework.TopPipeResult;
/**
 * 封装流程页面API 响应结果
 * 
 * @version 2009-08-16
 * @author <a href="mailto:yueqian@taobao.com">yueqian</a>
 */
public class TopPageAPIResult extends TopPipeResult {
	
	private static final long serialVersionUID = 1L;
	private String responsePage;
	private Object httpResponse;
	/**
	 * isp返回的错误码
	 */
	private String ispErrorCode;
	
	public String getIspErrorCode() {
		return ispErrorCode;
	}
	public void setIspErrorCode(String ispErrorCode) {
		this.ispErrorCode = ispErrorCode;
	}
	public Object getHTTPResponse() {
		return httpResponse;
	}
	public void setHTTPResponse(Object response) {
		this.httpResponse = response;
	}
	
	public String getResponsePage() {
		return responsePage;
	}

	public void setResponsePage(String responsePage) {
		this.responsePage = responsePage;
	}

	/**
	 * 是否纪录用户该api成功次数
	 * 
	 * @return
	 */
	public boolean isAllowRecordSuccessTimes() {
		return true;
	}

	/**
	 * 成功次数是否已经超过用户配额
	 * 
	 * @return
	 */
	public boolean isUserSuccessTimesLimited() {
		return false;
	}

}
