/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

import java.util.List;

import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.traffic.mapping.MethodMapping;

/**
 * Api对象
 * 
 * @version 2008-3-3
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public interface Api {
	
	long getLastModified();
	void setLastModified(long lastModified);
    long getLastCheckedForUpdate();
	void setLastCheckedForUpdate(long lastCheckedForUpdate);
	/**
	 * 支持直接远程呼叫
	 * @param apiInput 
	 * 
	 * @return
	 */
	boolean isCallable(String version);
	//---------------------
	/**
	 * 得到Api类型
	 * 
	 * @return
	 */
	ApiType getApiType();

	/**
	 * 得到对外的api名
	 * 
	 * @return
	 */
	String getName();
	
	/**
	 * 得到别名列表
	 * 
	 * @return
	 */
	List<String> getAliases();

	/**
	 * 得到必须的协议参数
	 * 
	 * @return
	 */
	public List<String> getProtocolMustParams();

	/**
	 * 得到隐私数据请求必须的协议参数
	 * 
	 * @return
	 */
	public List<String> getProtocolPrivateParams();

	/**
	 * 得到应用必传参数列表
	 * 
	 * @return
	 */
	public List<ApiApplicationParameter> getApplicationMustParams();
	/**
	 * 得到应用可选参数列表
	 * @return
	 */
	public List<ApiApplicationParameter> getApplicationOptionalParams();

	MethodMapping<Object> getMethodMapping();

	void setMethodMapping(MethodMapping<Object> mm);

	
	String[] getSupportedVersions();
	
	/**
	 * 透传 URL
	 * @return
	 */
	String getRedirectUrl();
	
	
	/**
	 * 是否透传
	 * @return
	 */
	boolean isRedirect();
	
	String getRequestURL();
	void  setRequestURL(String url);

	String getHsfInterfaceName();
	
	String getHsfInterfaceVersion();
	
	String getHsfMethodName();
	
	String getLocalPath();
	/**
	 * @return
	 */
	List<ApiApplicationParameter> getApplicationCombineParams();
	
	ErrorCode checkCombine(TopPipeInput pipeInput);
	/**
	 * @return
	 */
	Long getHsfTimeout();
}
