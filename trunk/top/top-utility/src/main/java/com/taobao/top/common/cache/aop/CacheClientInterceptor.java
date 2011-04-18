package com.taobao.top.common.cache.aop;

import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.monitor.alert.client.AlertAgent;
import com.taobao.top.common.cache.CacheNamespace;
import com.taobao.top.common.cache.ICache;
import com.taobao.top.common.cache.OperationTimeoutException;

/**
 * Only cache CacheClient.get() and cache namespace equals Role,App,Api.
 * 
 * @author xalinx at gmail dot com
 * @date Jul 16, 2009
 */
public class CacheClientInterceptor implements MethodInterceptor {

	private static final String TAIR_OPERATION_TIMEOUT_ERROR = "TAIR_OPERATION_TIMEOUT_ERROR";
	private static final String TAIR_EXECUTE_ERROR = "TAIR_EXECUTE_ERROR";

	private transient static Log log = LogFactory
			.getLog(CacheClientInterceptor.class);

	private String cacheMethod = "get";

	private static ICache<String, Object> cache = new LocalCacheImpl();

	private AlertAgent alertAgent;

	/**
	 * @TODO will refactor Default cache Role,App,Api 可以对每一个列表缓存定义失效时间
	 */
	private static Map<Integer, Integer> cachedNamespaces = new HashMap<Integer, Integer>();

	static {
		if (cachedNamespaces == null)
			cachedNamespaces = new HashMap<Integer, Integer>();

		cachedNamespaces.put(CacheNamespace.APP_BLACK_LIST.getValue(), 10 * 60);//总量控制黑名单
		
		/*
		新的流量控制黑名单，在本地缓存失效时间由tair中的value指定
		@see
		com.taobao.top.privilege.BlackListManagerImpl#addNewFlowBlack(String key, int banDuration)
		added by huaisu		
		*/
		cachedNamespaces.put(CacheNamespace.NEW_FLOW_BLACK_LIST.getValue(), 0);
	}

	public String generateCacheKey(int namespace, Object key) {
		StringBuilder cachekey = new StringBuilder();
		cachekey.append("key:").append(namespace).append("::").append(key);

		return cachekey.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept
	 * .MethodInvocation)
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String method = null;
		int ns = 0;
		try {
			method = invocation.getMethod().getName();
			Object[] args = invocation.getArguments();
			ns = (Integer) args[0];
			Object key = args[1];
			if (log.isDebugEnabled()) {
				log.debug(String.format("method:%s,ns:%s,key:%s", method, ns,
						key));
			}
			Object rs = null;
			Integer cacheTime = cachedNamespaces.get(ns);

			if (method.equals(cacheMethod) && cacheTime != null) {
				// maybe need reset
				String cacheKey = generateCacheKey(ns, key);
				Object value = cache.get(cacheKey);
				// not hit cache
				if (null == value) {
					if (log.isDebugEnabled()) {
						log.debug("not hit:" + cacheKey);
					}
					value = invocation.proceed();

					if (value != null) {
						if(ns == CacheNamespace.NEW_FLOW_BLACK_LIST.getValue()) {
							//use the value in tair to get the cacheTime. added by huaisu
							cacheTime = (Integer)value - (int)(System.currentTimeMillis() / 1000);
						}
						cache.put(cacheKey, value, cacheTime);
					}
				}

				rs = value;

			} else {
				rs = invocation.proceed();
			}
			if (log.isDebugEnabled()) {
				log.debug(method + " result:" + rs);
			}
			return rs;
		} catch (Throwable e) {
			if (alertAgent != null) { // just in case the setting failed.
				if (e instanceof OperationTimeoutException) {
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("Operation_");
					stringBuilder.append(method);
					stringBuilder.append("__");
					stringBuilder.append("Namespace_");
					stringBuilder.append(ns);
					alertAgent.alertWithAutoDismiss(
							TAIR_OPERATION_TIMEOUT_ERROR, stringBuilder
									.toString());
				} else { // Other exception goes to TAIR_EXECUTE_ERROR
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("Operation_");
					stringBuilder.append(method);
					stringBuilder.append("__");
					stringBuilder.append("Namespace_");
					stringBuilder.append(ns);
					stringBuilder.append("__Error_");
					stringBuilder.append(e.getClass().getName());
					alertAgent.alertWithAutoDismiss(TAIR_EXECUTE_ERROR,
							stringBuilder.toString());
				}
			}
			throw e;
		}

	}

	/**
	 * For spring IOC
	 * 
	 * @param alertAgent
	 */
	public void setAlertAgent(AlertAgent alertAgent) {
		this.alertAgent = alertAgent;
	}

	public static ICache<String, Object> getCache() {
		return cache;
	}
}