package com.taobao.top.log;

import org.apache.commons.lang.StringUtils;

import com.taobao.top.common.TopPipeConfig;


/**
 * @author fangweng
 *
 */
public class TopRequestLog extends TopLog
{
	private static final String REQUEST = "request";
	/**
	 * 
	 */
	private static final long serialVersionUID = 8627431125092181816L;
	/**
	 * 服务请求者的ip
	 */
	private String remoteIp;
	private String partnerId;
	private String format;
	private String appKey;
	private String apiName;
	/**
	 * 服务请求字节数
	 */
	private long readBytes;
	private String errorCode;
	private String subErrorCode;
	/**
	 * 当前TOP服务器的ip
	 */
	private String localIp;
	
	private String signMethod;
	private String version;
	
	/**
	 * 服务调用中绑定的用户昵称
	 */
	private String nick;
	/**
	 * app的状态(0未使用)
	 */
	private int appStatus;
	/**
	 * app的tag 0未使用
	 */
	private int appTag = 0;
	
	/**
	 * 一些危险的字段记录到日志里
	 * 暂时有iid,tid,sid
	 */
	private String id;
	
	/**
	 * top-mapping response time, add by zixue
	 */
	private long responseMappingTime;
	
	
	/**
	 * 服务执行时间（业务执行+中转消耗）
	 */
	private long serviceConsumeTime;
	
	private TopPipeConfig pipeConfig = TopPipeConfig.getInstance();
	
	protected String splitStr = "%!";
	
	public TopRequestLog() {
		this.className = REQUEST;
		logTime = System.currentTimeMillis();
		if(!StringUtils.isBlank(pipeConfig.getLogSplitStr())){
			splitStr = pipeConfig.getLogSplitStr();
		}
	}

	public long getServiceConsumeTime() {
		return serviceConsumeTime;
	}


	public void setServiceConsumeTime(long serviceConsumeTime) {
		this.serviceConsumeTime = serviceConsumeTime;
	}


	public int getAppTag() {
		return appTag;
	}

	public void setAppTag(int appTag) {
		this.appTag = appTag;
	}

	public int getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(int appStatus) {
		this.appStatus = appStatus;
	}

	public String getSignMethod()
	{
		return signMethod;
	}

	public void setSignMethod(String signMethod)
	{
		this.signMethod = signMethod;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}
	
	public String getLocalIp() {
		return localIp;
	}
	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getRemoteIp() {
		return remoteIp;
	}
	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}
	public String getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getApiName() {
		return apiName;
	}
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	public long getReadBytes() {
		return readBytes;
	}
	public void setReadBytes(long readBytes) {
		this.readBytes = readBytes;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getSubErrorCode() {
		return subErrorCode;
	}
	public void setSubErrorCode(String subErrorCode) {
		this.subErrorCode = subErrorCode;
	}
		
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String toString()
	{
		StringBuilder result = new StringBuilder();
		
		result.append(appStatus).append(splitStr)
		    .append(remoteIp).append(splitStr)
			.append(partnerId).append(splitStr)
			.append(format).append(splitStr)
			.append(appKey).append(splitStr)
			.append(apiName).append(splitStr)
			.append(readBytes).append(splitStr)
			.append(errorCode).append(splitStr)
			.append(subErrorCode).append(splitStr)
			.append(localIp).append(splitStr)
			.append(nick).append(splitStr)
			.append(version).append(splitStr)
			.append(signMethod).append(splitStr)
			.append(appTag).append(splitStr)
			.append(id).append(splitStr)
			.append(responseMappingTime).append(splitStr)
			.append(serviceConsumeTime).append(splitStr)
			.append(getTransactionConsumeTime());
		
		int size = timeStampQueue.size();
		
	
		for(int i = 0 ; i < size; i++)
			result.append(splitStr)
				.append(timeStampQueue.get(i));
		
		return result.toString();
	}

	/**
	 * @param responseMappingTime
	 */
	public void setResponseMappingTime(long responseMappingTime) {
		this.responseMappingTime = responseMappingTime;
		
	}
	public long getResponseMappingTime(){
		return responseMappingTime;
	}
}

