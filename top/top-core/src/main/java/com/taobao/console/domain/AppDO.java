package com.taobao.console.domain;

import java.io.Serializable;
import java.util.List;

public class AppDO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3631876657653129705L;
	/**
	 * ID
	 */
	private Long id;

	/**
	 * 插件状态:1：已上线运行中；3：待受理；4：测试中；5：待修改；6：开发中
	 */
	private Long appStatus;

	/**
	 * 插件描述
	 */
	private String description;

	/**
	 * 插件访问策略： 只允许哪些人访问
	 */
	private Long accessStrategy;

	/**
	 * 插件License类型： 100：基础插件，不需要安装 101：基础插件（默认安装） 201：免费插件 301：收费插件
	 */
	private Long licenseType;

	/**
	 * APP的安全级别<br>
	 * 1:外部开发者非自用型应用; 2:外部开发者自用型应用; 3:淘宝合作伙伴应用; 4:淘宝自有应用
	 */
	private Long appLevel;

	/**
	 * 绑定昵称
	 */
	private String bindNick;

	/**
	 * 应用回调URL
	 * 
	 */
	private String callbackUrl;

	/**
	 * 插件使用的协议版本
	 */
	private Long version = 2L;

	/**
	 * 插件类型，目前支持： 1：iframe插件 2：TBML插件 3：客户端插件
	 */
	private Long appType = 1L;

	/**
	 * sessionkey的有效时间,分钟，对应数据库中的SESSION_KEY_VALID_TIME_MIN,为保持当前测试代码完整
	 * 这个变量暂时不变。sessionKeyValidTimeHour=sessionKeyValidTime/60
	 */
	private Long sessionKeyValidTime;

	/**
	 * sessionkey的有效时间,对应老数据表的SESSION_KEY_VALID_TIME，浮点型,小时
	 * sessionKeyValidTimeHour=sessionKeyValidTime/60
	 */
	private Float sessionKeyValidTimeHour;

	/**
	 * sessionkey授权类型： 1. 自定义访问延迟有效期SessionKey ; 2. 固定有效期SessionKey
	 */
	private Long sessionKeyType = 1L;

	/**
	 * 插件子AppKey
	 */
	private String appKey;

	/**
	 * 插件的Secret
	 */
	private String secret;

	/**
	 * isv开发者id
	 */
	private Long isvId;

	/***
	 * 开发者名称
	 */
	private String isvName;

	/**
	 * 插件所对应的API角色的ID集合
	 */
	private List<Long> roleIdList;

	/**
	 * App的标题
	 */
	private String title;

	/**
	 * App的Logo地址
	 */
	private String logo;

	/**
	 * APP流量规则
	 */
	private Long appFlowRuleId;

	/**
	 * 父APPKEY
	 */
	private String parentAppkey;

	/**
	 * 通知地址
	 */
	private String notifyUrl;

	/**
	 * APP标签：淘宝商城=1、淘拍档=2、淘宝客=3、淘宝箱=4 、淘江湖=5、社区=6 、无线终端 =7、开源网店=8
	 */
	private Long appTag;

	/**
	 * 
	 * 
	 * 冗余的UIC的USER_ID,写冗余,可减少TIP对UIC的读调用
	 */
	private Long userId;
	
  	/**
     * 流量规则ID列表
     */
    private List<Long> flowRuleIds ;
    
    /**
     * 访问控制规则ID列表
     */
    private List<Long> scopeRuleIds;


	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(Long appStatus) {
		this.appStatus = appStatus;
	}

	public String getParentAppkey() {
		return parentAppkey;
	}

	public void setParentAppkey(String parentAppkey) {
		this.parentAppkey = parentAppkey;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAccessStrategy() {
		return accessStrategy;
	}

	public void setAccessStrategy(Long accessStrategy) {
		this.accessStrategy = accessStrategy;
	}

	public Long getLicenseType() {
		return licenseType;
	}

	public void setLicenseType(Long licenseType) {
		this.licenseType = licenseType;
	}

	public Long getAppLevel() {
		return appLevel;
	}

	public void setAppLevel(Long appLevel) {
		this.appLevel = appLevel;
	}

	public String getBindNick() {
		return bindNick;
	}

	public void setBindNick(String bindNick) {
		this.bindNick = bindNick;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getAppType() {
		return appType;
	}

	public void setAppType(Long appType) {
		this.appType = appType;
	}

	public Long getSessionKeyValidTime() {
		return sessionKeyValidTime;
	}

	public void setSessionKeyValidTime(Long sessionKeyValidTime) {
		this.sessionKeyValidTime = sessionKeyValidTime;
	}

	public Long getSessionKeyType() {
		return sessionKeyType;
	}

	public void setSessionKeyType(Long sessionKeyType) {
		this.sessionKeyType = sessionKeyType;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Long getIsvId() {
		return isvId;
	}

	public void setIsvId(Long isvId) {
		this.isvId = isvId;
	}

	public String getIsvName() {
		return isvName;
	}

	public void setIsvName(String isvName) {
		this.isvName = isvName;
	}

	public List<Long> getRoleIdList() {
		return roleIdList;
	}

	public void setRoleIdList(List<Long> roleIdList) {
		this.roleIdList = roleIdList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public Long getAppFlowRuleId() {
		return appFlowRuleId;
	}

	public void setAppFlowRuleId(Long appFlowRuleId) {
		this.appFlowRuleId = appFlowRuleId;
	}

	public Float getSessionKeyValidTimeHour() {
		return sessionKeyValidTimeHour;
	}

	public void setSessionKeyValidTimeHour(Float sessionKeyValidTimeHour) {
		this.sessionKeyValidTimeHour = sessionKeyValidTimeHour;
	}

	public Long getAppTag() {
		return appTag;
	}

	public void setAppTag(Long appTag) {
		this.appTag = appTag;
	}

	public List<Long> getFlowRuleIds() {
		return flowRuleIds;
	}

	public void setFlowRuleIds(List<Long> flowRuleIds) {
		this.flowRuleIds = flowRuleIds;
	}

	public List<Long> getScopeRuleIds() {
		return scopeRuleIds;
	}

	public void setScopeRuleIds(List<Long> scopeRuleIds) {
		this.scopeRuleIds = scopeRuleIds;
	}
	
}
