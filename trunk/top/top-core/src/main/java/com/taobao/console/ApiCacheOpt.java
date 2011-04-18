package com.taobao.console;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.taobao.top.console.client.handler.ConsoleClientPipeInput;
import com.taobao.top.console.client.handler.ConsoleClientPipeResult;
import com.taobao.top.console.client.util.ConsoleClientErrorCode;
import com.taobao.top.core.Api;
import com.taobao.top.core.ApiApplicationParameter;
import com.taobao.top.core.ApiConfigException;
import com.taobao.top.core.DefaultApi;
import com.taobao.top.core.DynamicApiFactory;

/**
 * http://192.168.207.127/top/services/inner?direct=CacheCenter&cmd=select&type=api&apiname=xxx  得到缓存中的apiname中对应的定义文件以及版本信息内容
 * 
 *                                                              cmd=update&type=api&apiname=xxx&config=xxx 必须POST请求，更新定义文件
 *                                                              cmd=update&type=api&apiname=xxx&hsfversion=xxx修改api对应的hsf版本
 *                                                              cmd=update&type=api&apiname=xxx&redirectUrl=xx修改透传的地址
 *                                                              
 *                                                              cmd=delete&type=api&apiname=xxx 删除本地的api对象缓存，如果apiname为空则
 *                                                                                              删除所有api本地缓存
 * api缓存操作实现
 * @author zhenzi
 * 2010-12-24 下午12:18:08
 */
public class ApiCacheOpt extends AbstractCacheOptImpl {
	private static final String API_NAME = "apiname";
	private DynamicApiFactory dynamicApiFactory;
	/*
	 * 对于本地定义文件的更新是在线上是不允许打开的
	 */
	private boolean openUpdateDefineConfig = false;
	
	public boolean isOpenUpdateDefineConfig() {
		return openUpdateDefineConfig;
	}

	public void setOpenUpdateDefineConfig(boolean openUpdateDefineConfig) {
		this.openUpdateDefineConfig = openUpdateDefineConfig;
	}

	public void setDynamicApiFactory(DynamicApiFactory dynamicApiFactory) {
		this.dynamicApiFactory = dynamicApiFactory;
	}

	@Override
	public void deleteCache(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		String apiname = input.getString(API_NAME);
		if(StringUtils.isBlank(apiname)){//删除所有api的缓存
			dynamicApiFactory.clearAllApi();
		}else{//删除指定api的缓存
			dynamicApiFactory.getApiStore().remove(apiname);
		}
		result.setBlack(ConsoleConstants.OPT_TRUE);
	}

	@Override
	public void getCache(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		String apiname = input.getString(API_NAME);
		if(!StringUtils.isBlank(apiname)){
			DefaultApi api = null;
			try {
				api = (DefaultApi)dynamicApiFactory.getApi(apiname);
			} catch (ApiConfigException e) {
				result.setErr_msg("get api error");
			}
			result.setBlack(getApiDefine(api));
		}else{
			result.setErr_msg("apiname is null");
		}
	}

	@Override
	public void updateCache(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		if (openUpdateDefineConfig) {
			String defineConfig = input.getString("config");
			String hsfversion = input.getString("hsfversion");
			String redirectUrl = input.getString("redirectUrl");

			String apiname = input.getString(API_NAME);
			if (StringUtils.isEmpty(apiname)) {
				result.setErrorCode(ConsoleClientErrorCode.MISS_ARGUMENTS);
				result.setErr_msg("apiname is null");
			}
			if (defineConfig != null) {//修改api的定义文件
				try {
					Api newApi = dynamicApiFactory.getApiConfigReader()
							.xmlString2Api(defineConfig.trim());
					if (newApi != null) {
						dynamicApiFactory.getApiStore().put(apiname, newApi);
						result.setBlack("success");
						return;
					} else {
						result.setErrorCode(ConsoleClientErrorCode.PLATFORM_SYSTEM_ERROR);
						result.setErr_msg("gerated new api is null");
						return;
					}
				} catch (Exception e) {
					logger.error(e, e);
					result.setErrorCode(ConsoleClientErrorCode.INVALID_ARGUMENTS);
					result.setErr_msg("invalid api config");
					return;
				}
			}else{//修改api的hsf版本，或者透传的地址
				Api api = null;
				try {
					api = dynamicApiFactory.getApi(apiname);
				} catch (ApiConfigException e) {
					result.setErr_msg("get api error");
					return;
				}
				if (api != null && api instanceof DefaultApi) {
					DefaultApi defaultApi = (DefaultApi)api;
					if(hsfversion != null){
						defaultApi.setHsfInterfaceVersion(hsfversion);
					}else if(redirectUrl != null){
						defaultApi.setRedirectUrl(redirectUrl);
					}else{
						result.setErrorCode(ConsoleClientErrorCode.INVALID_ARGUMENTS);
						result.setErr_msg("only support update hsfversion and redirectUrl");
						return;
					}
					result.setBlack("success");
					return;
				}else{
					result.setErrorCode(ConsoleClientErrorCode.INVALID_ARGUMENTS);
					result.setErr_msg("api name has no api");
					return;
				}
			}
		}		
	}
	private String getApiDefine(DefaultApi api){
		StringBuilder apiDefine = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		apiDefine.append("<api name=\"").append(api.getName());
		if(api.isRedirect()){
			apiDefine.append(" redirectUrl=\"").append(api.getRedirectUrl());
			
		}
		apiDefine.append("\">");
		//组装api的hsf信息
		if(!api.isRedirect()){
			apiDefine.append("<hsf interface_name=\"").append(api.getHsfInterfaceName()).append("\" ") 
			.append("interface_version=\"").append(api.getHsfInterfaceVersion()).append("\"  method_name=\"").append(api.getHsfMethodName()).append("\"/>");
		}
		apiDefine.append("<params>");
		List<String> protocolMust = api.getProtocolMustParams();
		List<String> protocolOpt = api.getProtocolPrivateParams();
		if(protocolMust != null || protocolOpt != null){
			apiDefine.append("<protocol>");
			if(protocolMust != null){
				apiDefine.append("<must>");
				for (String pMust : protocolMust) {
					apiDefine.append("<param name=\"").append(pMust).append("\" />");
				}
				apiDefine.append("</must>");
			}
			if(protocolOpt != null){
				apiDefine.append("<private>");
				for (String pOpt : protocolOpt) {
					apiDefine.append("<param name=\"").append(pOpt).append("\" />");
				}
				apiDefine.append("</private>");
			}
			apiDefine.append("</protocol>");
		}
		List<ApiApplicationParameter> aMust = api.getApplicationMustParams();
		List<ApiApplicationParameter> aOpt = api.getApplicationOptionalParams();
		if(aMust != null || aOpt != null){
			apiDefine.append("<application>");
			if(aMust != null){
				apiDefine.append("<must>");
				for (ApiApplicationParameter aMustParam : aMust) {
					apiDefine.append(aMustParam.toString());
				}
				apiDefine.append("</must>");
			}
			if(aOpt != null){
				apiDefine.append("<optional>");
				for (ApiApplicationParameter aOptParam : aOpt) {
					apiDefine.append(aOptParam.toString());
				}
				apiDefine.append("</optional>");
			}
			apiDefine.append("</application>");
		}
		apiDefine.append("</params>");
		if(api.getRequestURL() != null){
			apiDefine.append("<requesturl url=\"").append(api.getRequestURL()).append("\" />");
		}
		apiDefine.append("</api>");
		return apiDefine.toString();
	}
}
