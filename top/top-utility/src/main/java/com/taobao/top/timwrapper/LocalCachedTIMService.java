/**
 * 
 */
package com.taobao.top.timwrapper;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.alibaba.common.lang.StringUtil;
import com.taobao.top.common.TopPipeConfig;
import com.taobao.top.common.cache.ICache;
import com.taobao.top.common.cache.aop.LocalCacheImpl;
import com.taobao.top.tim.domain.AppAccountDO;
import com.taobao.top.tim.domain.AuthDO;
import com.taobao.top.tim.domain.AuthorizeDO;
import com.taobao.top.tim.domain.AuthorizeResultDO;
import com.taobao.top.tim.domain.ExPropertyDO;
import com.taobao.top.tim.domain.PlatformDO;
import com.taobao.top.tim.domain.RuleDO;
import com.taobao.top.tim.domain.SubscribeDO;
import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.tim.service.SamService;
import com.taobao.top.tim.service.TIMServiceException;

/**
 * 在TIM HSF服务基础上增加本地缓存
 * 
 * @author zhangsan
 * @since 1.0, 2010-1-16 上午10:19:49
 */
public class LocalCachedTIMService implements SamService {

	private static int APP_CACHE_TIME = 10 * 60;
	private static int API_CACHE_TIME = 10 * 60;
	private static int PLATFORM_CACHE_TIME = 10 * 60;
	private static int APP_RULE_CACHE_TIME = 30 * 60;
	private static int AUTH_CACHE_TIME = 5 * 60;

	
	/**
	 * 是否使用authCache的本地缓存
	 */
	private boolean userAuthCache = true;
	public boolean isUserAuthCache() {
		return userAuthCache;
	}
	public void setUserAuthCache(boolean userAuthCache) {
		this.userAuthCache = userAuthCache;
	}


	private transient static Log log = LogFactory
			.getLog(LocalCachedTIMService.class);

	private static ICache<String, Object> cache = new LocalCacheImpl();
	
	private TopPipeConfig pipeConfig = TopPipeConfig.getInstance();
	
	public static ICache<String, Object> getCache() {
		return cache;
	}

	
	private SamService timService;
	private SamService richClientService;
	
	/**
	 * 部分app信息是不需要缓存的，根据tag来判断
	 */
	private List<Long> filterTags;

	public List<Long> getFilterTags() {
		return filterTags;
	}

	public void setFilterTags(List<Long> filterTags) {
		this.filterTags = filterTags;
	}

	public void setTimService(SamService timService) {
		this.timService = timService;
	}

	public SamService getRichClientService() {
		return richClientService;
	}

	public void setRichClientService(SamService richClientService) {
		this.richClientService = richClientService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.tim.service.SamService#getAppById(java.lang.Long)
	 */
	public TinyAppDO getAppById(Long appId) throws TIMServiceException {

		if (null == appId)
			return null;
		if(pipeConfig.isUserRichClientCache()){
			return richClientService.getAppById(appId);
		}
		String cacheKey = "APPID:" + appId;
		TinyAppDO value = (TinyAppDO) cache.get(cacheKey);

		if (null == value) {
			if (log.isDebugEnabled()) {
				log.debug("Local cache not hit: " + cacheKey);
			}
			value = timService.getAppById(appId);

			if (null != value)
			{
				//判断是否需要缓存在本地
				if(TopPipeConfig.getInstance().isUserCache())
				{
					if((filterTags != null && !filterTags.contains(value.getAppTag())) || filterTags == null)
					{
						cache.put(cacheKey, value, APP_CACHE_TIME);
					}
				}
				
			}
				
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.tim.service.SamService#getAppByKey(java.lang.String)
	 */
	public TinyAppDO getAppByKey(String appkey) throws TIMServiceException {

		if (StringUtil.isEmpty(appkey))
			return null;
		if(pipeConfig.isUserRichClientCache()){
			return richClientService.getAppByKey(appkey);
		}
		String cacheKey = "APPKEY:" + appkey;
		TinyAppDO value = (TinyAppDO) cache.get(cacheKey);

		if (null == value) {
			if (log.isDebugEnabled()) {
				log.debug("Local cache not hit: " + cacheKey);
			}
			value = timService.getAppByKey(appkey);

			if (null != value)
			{
				//判断是否需要缓存在本地
				if(TopPipeConfig.getInstance().isUserCache())
				{
					if((filterTags != null && !filterTags.contains(value.getAppTag())) || filterTags == null)
					{
						cache.put(cacheKey, value, APP_CACHE_TIME);
					}
				}
			}
				
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taobao.top.tim.service.SamService#getPlatformById(java.lang.Long)
	 */
	public PlatformDO getPlatformById(Long platformId)
			throws TIMServiceException {

		if (null == platformId)
			return null;
		if(pipeConfig.isUserRichClientCache()){
			return richClientService.getPlatformById(platformId);
		}
		String cacheKey = "PLATFORM:" + platformId;
		PlatformDO value = (PlatformDO) cache.get(cacheKey);

		if (null == value) {
			if (log.isDebugEnabled()) {
				log.debug("Local cache not hit: " + cacheKey);
			}
			value = timService.getPlatformById(platformId);

			if (null != value)
				cache.put(cacheKey, value, PLATFORM_CACHE_TIME);
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taobao.top.tim.service.SamService#getValidApiByApiName(java.lang.
	 * String)
	 */
	public TinyApiDO getValidApiByApiName(String apiname)
			throws TIMServiceException {

		if (StringUtil.isEmpty(apiname))
			return null;
		if(pipeConfig.isUserRichClientCache()){
			return richClientService.getValidApiByApiName(apiname);
		}
		String cacheKey = "API:" + apiname;
		TinyApiDO value = (TinyApiDO) cache.get(cacheKey);

		if (null == value) {
			if (log.isDebugEnabled()) {
				log.debug("Local cache not hit: " + cacheKey);
			}
			value = timService.getValidApiByApiName(apiname);

			if (null != value)
				cache.put(cacheKey, value, API_CACHE_TIME);
		}
		return value;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taobao.top.tim.service.SamService#getAppkeysByPlatformIdUserId(java
	 * .lang.Long, java.lang.Long)
	 */
	public List<String> getAppkeysByPlatformIdUserId(Long userId,
			Long platformId) throws TIMServiceException {

		return timService.getAppkeysByPlatformIdUserId(userId, platformId);
	}

	/* (non-Javadoc)
	 * @see com.taobao.top.tim.service.SamService#checkUserPermitForApp(java.lang.Long, java.lang.Long)
	 */
	public boolean checkUserPermitForApp(Long userId, Long appId)
			throws TIMServiceException {

		return timService.checkUserPermitForApp(userId, appId);
	}

	@Override
	public Long createChildApp(String parentAppkey, String title,
			String callbackUrl, String notifyUrl, String logoUrl)
			throws TIMServiceException {
		
		return timService.createChildApp(parentAppkey, title, callbackUrl, notifyUrl, logoUrl);
	}

	
	@Override
	public RuleDO getRuleByIdAndType(Long ruleId, Long type)
			throws TIMServiceException {
		if(ruleId == null) {
			return null;
		}
		if(pipeConfig.isUserRichClientCache()){
			return richClientService.getRuleByIdAndType(ruleId, type);
		}
		String cacheKey = "rule:" + type + ":" + ruleId;
		RuleDO ruleDO = (RuleDO) cache.get(cacheKey);
		if(ruleDO == null) {
			ruleDO = timService.getRuleByIdAndType(ruleId, type);
			if(ruleDO != null) {
				cache.put(cacheKey, ruleDO, APP_RULE_CACHE_TIME);
			}
		}
		return ruleDO;
	}

	/* (non-Javadoc)
	 * @see com.taobao.top.tim.service.SamService#bindAppAccount(java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public Long bindAppAccount(String appKey, String appUid, Long userId)
			throws TIMServiceException {
		return timService.bindAppAccount(appKey, appUid, userId);
	}

	/* (non-Javadoc)
	 * @see com.taobao.top.tim.service.SamService#getBindedAppAccountByAppKeyAppUidTaobaoUid(java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public AppAccountDO getBindedAppAccountByAppKeyAppUidTaobaoUid(
			String appKey, String appUid, Long userId)
			throws TIMServiceException {
		return timService.getBindedAppAccountByAppKeyAppUidTaobaoUid(appKey, appUid, userId);
	}

	/* (non-Javadoc)
	 * @see com.taobao.top.tim.service.SamService#unbindAppAccount(java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public int unbindAppAccount(String appKey, String appUid, Long userId)
			throws TIMServiceException {
		return timService.unbindAppAccount(appKey, appUid, userId);
	}

	@Override
	public Date getExpiryDate(String appkey, Long userId)
			throws TIMServiceException {
		
		return timService.getExpiryDate(appkey, userId);
	}

	@Override
	public Long addAuthorize(AuthorizeDO arg0) throws TIMServiceException {
		return timService.addAuthorize(arg0);
	}

	@Override
	public Long addSubscribe(SubscribeDO arg0) throws TIMServiceException {
		return timService.addSubscribe(arg0);
	}

	@Override
	public AuthorizeResultDO getAuthorize(String arg0, List<Long> arg1,
			Long arg2, Long arg3) throws TIMServiceException {
		return timService.getAuthorize(arg0, arg1, arg2, arg3);
	}

	@Override
	public List<String> getAuthorizedAppKeys(Long arg0)
			throws TIMServiceException {
		return timService.getAuthorizedAppKeys(arg0);
	}

	@Override
	public List<TinyAppDO> getAuthorizedApps(Long arg0, Integer arg1,
			Integer arg2) throws TIMServiceException {
		return timService.getAuthorizedApps(arg0, arg1, arg2);
	}

	@Override
	public List<SubscribeDO> getAuthorizedSubscribes(Long arg0, Integer arg1,
			Integer arg2, Long arg3) throws TIMServiceException {
		return timService.getAuthorizedSubscribes(arg0, arg1, arg2, arg3);
	}

	@Override
	public List<ExPropertyDO> getExtPropertyByKeyAndType(String arg0, Long arg1)
			throws TIMServiceException {
		
		return timService.getExtPropertyByKeyAndType(arg0, arg1);
	}

	@Override
	public AuthDO getPersistentAuthBySessionKey(String sessionId)
			throws TIMServiceException {
		if(pipeConfig.isUserRichClientCache()){
			return richClientService.getPersistentAuthBySessionKey(sessionId);
		}
		return timService.getPersistentAuthBySessionKey(sessionId);
	}

	@Override
	public SubscribeDO getSubscribe(String arg0) throws TIMServiceException {
		return timService.getSubscribe(arg0);
	}

	@Override
	public void insertOrUpdateAuth(AuthDO arg0) throws TIMServiceException {
		timService.insertOrUpdateAuth(arg0);
	}

	@Override
	public void removePersistentAuth(long arg0, long arg1)
			throws TIMServiceException {
		timService.removePersistentAuth(arg0, arg1);
	}

	@Override
	public int updateAuthorize(AuthorizeDO arg0) throws TIMServiceException {
		return timService.updateAuthorize(arg0);
	}

	@Override
	public int updateSubscribe(SubscribeDO arg0) throws TIMServiceException {
		return timService.updateSubscribe(arg0);
	}

}
