package com.taobao.top.log;
/**
 * pageapi打点模型
 * @version 2010-10-11
 * @author zhuyong.pt
 *
 */
public class TopPageRequestLog extends TopRequestLog {
	private String ispErrorCode;
	
	public String getIspErrorCode() {
		return ispErrorCode;
	}
	public void setIspErrorCode(String ispErrorCode) {
		this.ispErrorCode = ispErrorCode;
	}
	public TopPageRequestLog(){
		super();
	}
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		
		result.append("pageapi").append(splitStr)
			.append(getAppStatus()).append(splitStr)
		    .append(getRemoteIp()).append(splitStr)
			.append(getPartnerId()).append(splitStr)
			.append(getFormat()).append(splitStr)
			.append(getAppKey()).append(splitStr)
			.append(getApiName()).append(splitStr)
			.append(getReadBytes()).append(splitStr)
			.append(ispErrorCode).append(splitStr)
			.append(getErrorCode()).append(splitStr)
			.append(getSubErrorCode()).append(splitStr)
			.append(getLocalIp()).append(splitStr)
			.append(getNick()).append(splitStr)
			.append(getVersion()).append(splitStr)
			.append(getSignMethod()).append(splitStr)
			.append(getAppTag()).append(splitStr)
			.append(getId()).append(splitStr)
			.append(getResponseMappingTime()).append(splitStr)
			.append(getServiceConsumeTime()).append(splitStr)
			.append(getTransactionConsumeTime());
		
		int size = timeStampQueue.size();
		
	
		for(int i = 0 ; i < size; i++)
			result.append(splitStr)
				.append(timeStampQueue.get(i));
		
		return result.toString();
	}
}
