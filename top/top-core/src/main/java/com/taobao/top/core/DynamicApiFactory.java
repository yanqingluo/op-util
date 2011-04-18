/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.alibaba.common.logging.LoggerFactory;
import com.taobao.common.tfs.TfsManager;
import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.service.SamService;
import com.taobao.top.tim.service.TIMServiceException;
import com.taobao.top.traffic.mapping.config.MappingConfigReader;
import com.taobao.top.util.ConcurrentInitializer;
import com.taobao.top.util.ResourceHolder;

/**
 * @version 2009-3-31
 * @author <a href="mailto:linxuan@taobao.com">linxuan</a>
 * 
 */
public class DynamicApiFactory implements ApiFactory {
	private static final Log logger = LoggerFactory
			.getLog(DynamicApiFactory.class);
	private static final int CHECK_FORUPDATE_INTERVAL = 30 * 60 * 1000; // 单位毫秒, 默认30分钟
																	// seconds
	private final Lock checkForUpdateLock = new ReentrantLock();	
	/**
	 * 动态配置api
	 */
	private final Map<String, Api> apiStore = new ConcurrentHashMap<String, Api>();

	/**
	 * 实现ResourceHolder接口以使用ConcurrentInitializer模板，采用匿名内嵌类对象，以避免暴露接口方法。
	 */
	private final ResourceHolder<String, Api, ApiConfigException> resourceHolder = new ResourceHolder<String, Api, ApiConfigException>() {
		public Api currentData(String key) {
			return apiStore.get(key);
		}

		public Api initializeDate(String key) throws ApiConfigException {
			TinyApiDO apiDo = loadApi(key);
			return parseAndcacheApi(key, apiDo);
		}
	};

	private final ConcurrentInitializer<String, Api, ApiConfigException> initializer = new ConcurrentInitializer<String, Api, ApiConfigException>(
			resourceHolder);

	ApiConfigReader apiConfigReader;
	MappingConfigReader mappingConfigReader;
	private SamService timService;

	public void setTimService(SamService timService) {
		this.timService = timService;
	}
	/*
	 * 为了能测试动态api接入，用于判断TFS是线上还是预发环境。
	 * true 表示线上环境，和daily环境
	 * false 表示预发环境
	 */
	private boolean tfsEnvOnline = true;
	
	public void setTfsEnvOnline(boolean tfsEnvOnline) {
		this.tfsEnvOnline = tfsEnvOnline;
	}
	
	private TfsManager tfsManager = null;
	
	public void setTfsManager(TfsManager tfsManager) {
		this.tfsManager = tfsManager;
	}

	public DynamicApiFactory() {
		this.apiConfigReader = new ApiConfigReader();
		this.mappingConfigReader = new MappingConfigReader();
	}
	public Api getApi(String method) throws ApiConfigException {
		return getRemoteApi(method);
	}	
	/**
	 * 仅读远程
	 * 
	 * @param method
	 * @return
	 * @throws ApiConfigException
	 */
	private Api getRemoteApi(final String method) throws ApiConfigException {
		if (method == null) {
			return null;
		}
		Api api = this.apiStore.get(method);
		if (api != null) {
			if (isNeedCheckForUpdate(api)) {
				if (checkForUpdateLock.tryLock()) {
					try {
						if (isNeedCheckForUpdate(api)) {
							logger.debug("[getApi] 检查api是否有更新，api：" + method);
							TinyApiDO apiDo = loadApi(method);
							if (apiDo == null
									|| apiDo.getGmtModified().getTime() > api
											.getLastModified()) {
								api = parseAndcacheApi(method, apiDo);
							} else {
								api.setLastCheckedForUpdate(System
										.currentTimeMillis());
							}
						}
					} finally {
						checkForUpdateLock.unlock();
					}
				}
			}
			return api;
		} else {
			logger.debug("[getApi] 开始初始化api：" + method);
			api = initializer.getData(method);
			logger.debug("[getApi] 完成初始化api：" + method + ",api=" + api);
			return api;
		}
	}

	private Api parseAndcacheApi(String apiName, TinyApiDO apiDO)
			throws ApiConfigException {
		logger.debug("[parseAndcacheApi] begin. apiName=" + apiName);
		if (apiDO == null) { // api被动态删除了
			logger.info("[parseAndcacheApi] apiDO==null.");
			// this.apiStore.remove(apiDO.getName());
			this.apiStore.remove(apiName);
			return null;
		}
		String apiDefine = null;
		String apiMapping = null;
		if(tfsEnvOnline){//正式环境或者daily环境
			apiDefine = apiDO.getApiDefinePath();
			apiMapping = apiDO.getApiMappingPath();
		}else{//预发环境
			apiDefine = apiDO.getApiDefinePath2();
			apiMapping = apiDO.getApiMappingPath2();
			if(logger.isDebugEnabled()){
				logger.debug(new StringBuilder(
						"TFS in prepub evn apiDefine is:").append(apiDefine)
						.append("\n apiMapping is:").append(apiMapping));
			}
		}
		/*
		 * 转化为文件内容
		 */
		try {
			apiDefine = getTfsFile(apiDefine);
			apiMapping = getTfsFile(apiMapping);
		} catch (Exception e) {
			throw new ApiConfigException(e);
		}
		
		logger.debug("[parseAndcacheApi] api：" + apiDO.getName());
		if (StringUtils.isEmpty(apiDefine)) {
			logger
					.info("[parseAndcacheApi] apiDO.getApiDefineAsFile() is empty");
			return null;
		}
		
		// Integer apiKind = apiDO.getApiKind();
		Api api = apiConfigReader.xmlString2Api(apiDefine);
		api.setLastModified(apiDO.getGmtModified().getTime());

		if (!StringUtils.isEmpty(apiMapping)) {
			logger.debug("[parseAndcacheApi] 解析api Mappingap 定义，api："
					+ apiDO.getName());
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(apiMapping.getBytes("UTF-8"));
				api.setMethodMapping(mappingConfigReader.read(in));
			} catch (Exception e) {
				throw new ApiConfigException("Parse api mapping xml failed", e);
			}
		}

		api.setLastCheckedForUpdate(System.currentTimeMillis());
		this.apiStore.put(apiDO.getName(), api);
		logApiFile(api);
		return api;
	}
	/**
	 * 从tfs里拿到fileName对应的文件内容
	 * @param fileName 文件名
	 * @return 文件内容
	 * @throws Exception 
	 */
	private String getTfsFile(String fileName) throws Exception{
		if(StringUtils.isEmpty(fileName)){			
			return null;
		}
		ByteArrayOutputStream outStream  = new ByteArrayOutputStream();
		try{
			tfsManager.fetchFile(fileName, null, outStream);
			return outStream.toString();
		}finally{
			try{
				outStream.close();
			}catch(Exception e){
				logger.error("close outStream error.");
			}
		}
	}
	
	public TinyApiDO loadApi(String apiName) throws ApiConfigException {
		try {
			TinyApiDO apiDo = timService.getValidApiByApiName(apiName);
			if (apiDo == null) {
				logger.warn("[loadApi] API not exist in database, api name:"
						+ apiName);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug(new StringBuilder("[loadApi] ")
							.append(apiName).append(", ApiDefineAsFile:")
							.append(apiDo.getApiDefinePath()).append(
									",\nApiMappingAsFile:").append(
									apiDo.getApiMappingPath()));
				}
			}
			return apiDo;
		} catch (TIMServiceException e) {
			logger.error("[loadApi] Load api failed", e);
			throw new ApiConfigException("Load api failed", e);
		}
	}

	private boolean isNeedCheckForUpdate(Api api) {
		String intervalProp = System
				.getProperty("com.taobao.top.api.checkforupdate.interval");
		int interval = intervalProp == null ? CHECK_FORUPDATE_INTERVAL
				: Integer.parseInt(intervalProp);
		return System.currentTimeMillis() - api.getLastCheckedForUpdate() > interval;
	}

	private void logApiFile(Api api) {
		if (logger.isDebugEnabled()) {
			logger.debug(new StringBuilder("apiName=").append(api.getName())
					.append(", MethodMappingInterface=").append(
							api.getMethodMapping() == null ? "null" : api
									.getMethodMapping().getInterfaceName()
									+ ":"
									+ api.getMethodMapping()
											.getInterfaceVersion()));
		}
	}	
	public void clearAllApi() {
		this.apiStore.clear();
	}

	public Map<String, Api> getApiStore() {
		return apiStore;
	}

	public ApiConfigReader getApiConfigReader() {
		return apiConfigReader;
	}
	
}
