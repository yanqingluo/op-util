/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.traffic.mapping.MethodMapping;
import com.taobao.util.CollectionUtil;

/**
 * 运行期，每个api一个实例
 * 
 * @version 2008-3-3
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class DefaultApi implements Api {
	private long lastModified = -1;
	private long lastCheckedForUpdate = -1;

	/**
	 * api名称
	 */
	private String name;

	private List<String> aliases;

	private String[] supportedVersions;

	private String redirectUrl;

	private String hsfInterfaceName;

	private String hsfInterfaceVersion;

	private String hsfMethodName;

	public List<String> getAliases() {
		return aliases;
	}

	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

	/**
	 * 协议层，必须附上的参数
	 */
	private List<String> protocolMustParams;

	/**
	 * 协议层，请求隐私数据时需要附上的参数
	 */
	private List<String> protocolPrivateParams;

	/**
	 * 应用层，必须附上的参数
	 */
	private List<ApiApplicationParameter> applicationMustParams = new ArrayList<ApiApplicationParameter>();

	private List<ApiApplicationParameter> applicationOptionalParams = new ArrayList<ApiApplicationParameter>();

	private ApiType apiType = ApiType.SELECT;

	// add it for page api support
	/** page api 调用url */
	private String requestURL;

	private List<ApiApplicationParameter> applicationCombinedParams;

	private List<ParameterCombine> applicationCombines;
	/**
	 * 存在MethodMapping,表示可以远程呼叫
	 * 
	 * @return the callable
	 */
	public boolean isCallable(String version) {
		if (version.equals(ProtocolConstants.VERSION_1)) {
			if (this.methodMapping != null) {
				return true;
			}
			// 2.0
		} else {
			if (this.hsfInterfaceName != null) {
				return true;
			}
		}
		return false;
	}

	//--------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.core.Api#getType()
	 */
	public ApiType getApiType() {
		return apiType;
	}

	/**
	 * @param apiType
	 *            the apiType to set
	 */
	public void setApiType(ApiType apiType) {
		this.apiType = apiType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	public List<String> getProtocolMustParams() {
		return protocolMustParams;
	}

	public void addProtocolMustParameter(String protocolParameter) {
		if (null == this.protocolMustParams) {
			this.protocolMustParams = new ArrayList<String>();
		}
		this.protocolMustParams.add(protocolParameter);
	}

	public List<String> getProtocolPrivateParams() {
		return protocolPrivateParams;
	}

	public void addProtocolPrivateParameter(String protocolParameter) {
		if (null == this.protocolPrivateParams) {
			this.protocolPrivateParams = new ArrayList<String>();
		}
		this.protocolPrivateParams.add(protocolParameter);
	}

	public List<ApiApplicationParameter> getApplicationMustParams() {
		return applicationMustParams;
	}

	public List<ApiApplicationParameter> getApplicationOptionalParams() {
		return applicationOptionalParams;
	}

	public void addApplicationCombine(ParameterCombine pc) {
		if (null == this.applicationCombines) {
			this.applicationCombines = new ArrayList<ParameterCombine>();
		}
		this.applicationCombines.add(pc);
	}

	public void addApplicationMustParameter(
			ApiApplicationParameter applicationParameter) {
		if (null == this.applicationMustParams) {
			this.applicationMustParams = new ArrayList<ApiApplicationParameter>();
		}
		this.applicationMustParams.add(applicationParameter);
	}

	public void addApplicationOptionalParameter(
			ApiApplicationParameter applicationParameter) {
		if (null == this.applicationOptionalParams) {
			this.applicationOptionalParams = new ArrayList<ApiApplicationParameter>();
		}
		this.applicationOptionalParams.add(applicationParameter);
	}

	private MethodMapping<Object> methodMapping;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.core.Api#getMethodMapping()
	 */
	public MethodMapping<Object> getMethodMapping() {
		return this.methodMapping;
	}

	/**
	 * @param methodMapping
	 *            the methodMapping to set
	 */
	public void setMethodMapping(MethodMapping<Object> methodMapping) {
		this.methodMapping = methodMapping;
	}

	public long getLastModified() {
		return this.lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public long getLastCheckedForUpdate() {
		return lastCheckedForUpdate;
	}

	public void setLastCheckedForUpdate(long lastCheckedForUpdate) {
		this.lastCheckedForUpdate = lastCheckedForUpdate;
	}

	public String[] getSupportedVersions() {
		return supportedVersions;
	}

	public void setSupportedVersions(String[] supportedVersions) {
		this.supportedVersions = supportedVersions;
	}

	/**
	 * 透传
	 * 
	 * @param parseRedirect
	 */
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public boolean isRedirect() {
		return !StringUtils.isEmpty(redirectUrl);
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public String getHsfInterfaceName() {
		return hsfInterfaceName;
	}

	public void setHsfInterfaceName(String hsfInterfaceName) {
		this.hsfInterfaceName = hsfInterfaceName;
	}

	public String getHsfInterfaceVersion() {
		return hsfInterfaceVersion;
	}

	public void setHsfInterfaceVersion(String hsfInterfaceVersion) {
		this.hsfInterfaceVersion = hsfInterfaceVersion;
	}

	public String getHsfMethodName() {
		return hsfMethodName;
	}

	public void setHsfMethodName(String hsfMethodName) {
		this.hsfMethodName = hsfMethodName;
	}
	
	private String localPath;

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	private Long hsfTimeout;
	
	@Override
	public Long getHsfTimeout() {
		return hsfTimeout;
	}
	
	public void setHsfTimeout(Long hsfTimeout) {
		this.hsfTimeout = hsfTimeout;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.core.Api#getApplicationCombineParams()
	 */
	public List<ApiApplicationParameter> getApplicationCombineParams() {
		return this.applicationCombinedParams;
	}

	/**
	 * @param param
	 */
	public void addApplicationCombineParameter(ApiApplicationParameter param) {
		if (null == this.applicationCombinedParams) {
			this.applicationCombinedParams = new ArrayList<ApiApplicationParameter>();
		}
		this.applicationCombinedParams.add(param);
	}
	public ErrorCode checkCombine(TopPipeInput pipeInput) {
		if (CollectionUtil.isEmpty(this.applicationCombines)) {
			return null;
		}
		int i = 0;
		for (ParameterCombine pc : this.applicationCombines) {
			ErrorCode err = pc.check(pipeInput);
			if (err != null) {
				return err;
			} else {
				if (i == this.applicationCombines.size() - 1) {
					return null;
				}
				// else continue
			}
			i++;
		}
		return ErrorCode.MISSING_REQUIRED_ARGUMENTS;
	}
}
