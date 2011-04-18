/**
 * 
 */
package com.taobao.top.sm;

import static com.taobao.top.common.cache.CacheNamespace.SESSION;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.common.cache.CacheClient;
import com.taobao.top.common.cache.OperationTimeoutException;
import com.taobao.top.timwrapper.manager.TadgetManager;

/**
 * @version 2009-2-4
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public class DefaultSessionGenerator implements SessionGenerator {
	private static final transient Log log = LogFactory
			.getLog(DefaultSessionGenerator.class);
	
	
	/************设置deadline的开关*********************************/
	/**
	 * 因为每次创建sessionKey都会去Tim查询一次订购关系，而Tim反应表比较大，不好做缓存。
	 * 加入开关，万一Tim压力大，可以及时停止查询。
	 */
	private boolean isDeadlineOnWork = true;
	private int appKeyMax = 5;
	
	public int getAppKeyMax() {
		return appKeyMax;
	}

	public void setAppKeyMax(int appKeyMax) {
		this.appKeyMax = appKeyMax;
	}

	public boolean isDeadlineOnWork() {
		return isDeadlineOnWork;
	}

	public void setDeadlineOnWork(boolean isDeadlineOnWork) {
		this.isDeadlineOnWork = isDeadlineOnWork;
	}

	/***********end 开关******************************************/
	/**
	 * session 类型：固定有效时间
	 */
	public static final char fixedTime = '1';

	/**
	 * session 类型：后延有效时间
	 */
	public static final char activeTime = '2';

	/**
	 * session 类型：一次性使用
	 */
	public static final char oneTime = '3';

	
	/**
	 * 缓存客户端
	 */
	private CacheClient cacheClient;
	/**
	 * TIP平台的 secret
	 */
	private String tipSecret = null;
	
	public void setTipSecret(String tipSecret) {
		this.tipSecret = tipSecret;
	}

	/**
	 * @param cacheClient
	 *            the cacheClient to set
	 */
	public void setCacheClient(CacheClient cacheClient) {
		this.cacheClient = cacheClient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taobao.top.core.SessionGenerator#generateOneTimneSession(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	public String generateOneTimeSession(String method, String sessionNick,
			String userId, String appSecret, String... apiKeys) throws SessionGenerateException {
		String sessionId = generateSessionId(oneTime,userId, appSecret, apiKeys);
		Session session = new Session();
		session.setV(Session.version);
		session.setSessionId(sessionId);
		session.setMethod(method);
		session.setNick(sessionNick);
		session.setUserId(userId);
		Date date = new Date();
		session.setValidFrom(date);
		session.setValidEnd(date);
		session.setAppKeys(apiKeys);
		store(session);
		return sessionId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taobao.top.sm.SessionGenerator#generateKeepAliveSession(java.lang
	 * .String[], java.lang.String, java.lang.String, java.lang.String, long,
	 * boolean)
	 */
	public String generateKeepAliveSession(String method, String sessionNick,
			String userId, long validThru, boolean isFixedTime,String appSecret,
			String... apiKeys) throws SessionGenerateException {
		//默认的最后失效时间是负数，表示没有最后失效的时间
		long deadLine = -1L;
		
		// 默认是active
		char first = activeTime;
		// 是fixed time
		if (isFixedTime) {
			first = fixedTime;
		}else{
			//那么说明是可持续延长的session类型，从Tim那边调用查询订购关系
			//Tip不需要这段逻辑@zhudi
//			if(isDeadlineOnWork&&apiKeys!=null&&NumberUtils.isNumber(userId)){
//				try {
//					for (int i = 0; i < apiKeys.length && i < appKeyMax; i++) {
//						String appKey = apiKeys[i];
//						Date deadDate = tadgetManager.getExpiryDateBaseOnSubscribe(appKey, Long.parseLong(userId));
//						if(deadDate!=null){
//							if(deadDate.getTime()>deadLine){
//								deadLine = deadDate.getTime();
//							}
//						}else{
//							deadLine = -1L;
//							break;
//						}
//					}
//					
//				} catch (Exception e) {
//					log.error(e.getMessage(),e);
//					deadLine = -1L;
//				}
//			}
		}
		String sessionId = generateSessionId(first,userId,appSecret,apiKeys);
		Session session = new Session();
		session.setSessionId(sessionId);
		session.setV(Session.version);
		session.setMethod(method);
		session.setNick(sessionNick);
		session.setUserId(userId);
		session.setAppKeys(apiKeys);
		Date date = new Date();
		session.setValidFrom(date);
		Date endDate = new Date();
		endDate.setTime(date.getTime()+validThru);
		session.setValidEnd(endDate);
		//如果有最后实效时间
		if(deadLine>0){
			session.setProperty(Session.SESSION_DEAD_LINE,String.valueOf(deadLine));
		}
		
		store(session);
		return sessionId;
	}

	/**
	 * 存贮sessionId和附带信息
	 * 
	 * 
	 * @param sessionId
	 * @param apiKey
	 * @param method
	 * @param sessionNick
	 * @param userId
	 * @param validThru
	 * @throws SessionGenerateException
	 */
	private void store(Session session)
			throws SessionGenerateException {
		// 缓存key
		String key = session.getSessionId();
		// 缓存value
		
		String value = session.convertToStr();
		try {
			add(key, value, session.getValidThru());
		} catch (Exception e) {
			log.error(String.format("key:%s value:%s", key, value), e);
			throw new SessionGenerateException(e);
		}
		if (log.isDebugEnabled()) {
			log.debug(String.format("gen session key:%s value:%s", key, value));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.core.SessionGenerator#revertSession(java.lang.String)
	 */
	public Session revertSession(String sessionId)
			throws SessionRevertException {
		// 37 是普通的sessionId的长度
		
		if (37 != sessionId.length()) {
			throw new SessionNotExistException(sessionId);
		}
		
		String value = null;
		try {
			// 获取缓存到的value
			value = (String) this.cacheClient
					.get(SESSION.getValue(), sessionId);
		} catch (Exception e) {
			throw new SessionRevertException(sessionId, e);
		}
		if (value == null) {
			throw new SessionNotExistException(sessionId);
		}
		Session session = new Session();
		session.init(sessionId, value);
		return session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taobao.top.sm.SessionGenerator#updateSession(com.taobao.top.sm.Session
	 * )
	 */
	public void updateSession(Session session) throws SessionRevertException,
			SessionGenerateException {
		if (log.isDebugEnabled()) {
			log.debug("old session:\n" + session);
		}
		
		// update session
		long validThru = session.getValidThru();
		session.getValidFrom().setTime(System.currentTimeMillis());
		session.getValidEnd().setTime(
				session.getValidFrom().getTime() + validThru);
		
		String updateValue = session.convertToStr();
		// 更新cache
		try {
			update(session.getSessionId(), updateValue, session.getValidThru());
		} catch (Exception e) {
			throw new SessionRevertException(e);
		}
		if (log.isDebugEnabled()) {
			log.debug("updated session:\n" + session);
		}
	}

	/**
	 * 对更新缓存的校验不严格
	 * 
	 * @param key
	 * @param value
	 * @param validThru
	 * @throws OperationTimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	private void update(String key, String value, long validThru) throws InterruptedException, ExecutionException, OperationTimeoutException {
		// 注意，cache的expiredTime是以秒为单位
		// XXX maybe overflow if validThru bigger than Int.MAX_VALUE * 1000
		this.cacheClient.putWithoutCheck(SESSION.getValue(),
				key, value, (int) (validThru / 1000));
	}

	/**
	 * add对更新缓存的校验要比update严格
	 * 
	 * @param key
	 * @param value
	 * @param validThru
	 * @throws OperationTimeoutException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void add(String key, String value, long validThru)
			throws OperationTimeoutException, InterruptedException,
			ExecutionException {
		// 注意，cache的expiredTime是以秒为单位
		this.cacheClient.put(SESSION.getValue(), key, value,
				(int) (validThru / 1000));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.sm.SessionGenerator#deleteSessionId(java.lang.String)
	 */
	public void deleteSessionId(String sessionId) throws SessionRevertException {
		try {
			this.cacheClient.delete(SESSION.getValue(),
					sessionId);
		} catch (Exception e) {
			throw new SessionRevertException(e);
		}
	}

	/**
	 * 共37位
	 * 
	 * 第1位：
	 * 
	 * 1表示Fixed Time SessionId
	 * 
	 * 2表示Active SessionId
	 * 
	 * 3表示一次性SessionId
	 * 
	 * 新的sessionKey生成机制
	 * @param first 标志
	 * @param userId 用户id
	 * @param appSecret 
	 * @param appkeys
	 * @return
	 */
	private String generateSessionId(char first,String userId,String appSecret,String...appkeys){
		StringBuilder sessionKey = new StringBuilder();
		sessionKey.append(first);
		if(userId.length() >= 4){
			sessionKey.append(userId.substring(userId.length() - 4));
		}else{
			char[] temp = new char[4 - userId.length()];
			for(int i = 0 ; i < temp.length ; i++){
				temp[i] = '0';
			}
			sessionKey.append(temp).append(userId);
		}
		//md5
		StringBuilder md5Str = new StringBuilder(appSecret);
		md5Str.append(System.currentTimeMillis() / (24 * 60 * 60 * 1000));//表示一天内不管用户调用多少次容器，容器只生成一个sessionKey
		for (String string : appkeys) {
			md5Str.append(string);
		}
		md5Str.append(userId).append(tipSecret);
		try {
			sessionKey.append(DigestUtils.md5Hex(md5Str.toString().getBytes("utf-8")));
		} catch (Exception e) {
			if(log.isWarnEnabled()){
				log.error(e);
			}
		}		
		return sessionKey.toString();
	}

	

	public void bindAppkey(String sessionId, String... appkey)
			throws SessionGenerateException {
		try {
			Session session = this.revertSession(sessionId);
			Set<String> appkeySet = session.getAppKeys();
			for(String app_key :appkey){
				if(!appkeySet.contains(app_key)){
					appkeySet.add(app_key);
				}
			}
			this.updateSession(session);
		} catch (Exception e) {
			throw new SessionGenerateException(e.getMessage());
		}
	}
	
}
