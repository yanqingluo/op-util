/**
 * 
 */
package com.taobao.top.sm;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.common.lang.StringUtil;
import com.taobao.top.tim.service.TIMServiceException;
import com.taobao.top.timwrapper.manager.TadgetManager;

/**
 * 会话管理的实现
 * 
 * @version 2008-11-20
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class TairSessionManagerImpl implements SessionManager {
	private static final Log log = LogFactory.getLog(TairSessionManagerImpl.class);
	private final int fixted_type = (Integer.valueOf(String.valueOf(DefaultSessionGenerator.fixedTime)).intValue());
	/**
	 * 
	 * 可能以后会废弃
	 */
	private SessionGenerator sessionGenerator;

	public void setSessionGenerator(SessionGenerator sessionGenerator) {
		this.sessionGenerator = sessionGenerator;
	}

	/**
	 * 需要去tim查询数据，暂时采用把TIM客户端注入的方式，以后再整理   @朱棣 
	 */
	private TadgetManager tadgetManager = null;
	
	public void setTadgetManager(TadgetManager tadgetManager) {
		this.tadgetManager = tadgetManager;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taobao.top.router.session.SessionManager#revertSession(java.lang.
	 * String, java.lang.String)
	 */
	public Session revertValidSession(String sessionId, String appKey)
			throws SessionRevertException, SessionGenerateException {
		if (StringUtils.isEmpty(sessionId) || StringUtils.isEmpty(appKey)) {
			throw new SessionNotExistException();
		}
		Session session = null;
		try {
			session = sessionGenerator.revertSession(sessionId);
		} catch (SessionNotExistException e) {
			throw e;
		}
		if (session == null) {
			throw new SessionNotExistException();
		}
		
		// 不是该appKey拥有的session
		if (!session.getAppKeys().contains(appKey)) {
			throw new SessionOwnerUnmatchException(appKey + " no privilege!");
		}
		// 判断是否已过期
		if (session.getValidEnd().before(new Date())) {
			// 过期则删除
			this.sessionGenerator.deleteSessionId(sessionId);
			if (log.isInfoEnabled()) {
				log.info(session);
			}
			throw new SessionExpiredException("Session expired!");
		}
		/**
		 * 如果到了session的最后失效时间，让session失效掉
		 */
	   String deadLine = session.getProperty(Session.SESSION_DEAD_LINE);
	   if(deadLine!=null&&NumberUtils.isNumber(deadLine)){
		   if(System.currentTimeMillis()>Long.parseLong(deadLine)){
			   /**
			    * 先去查询一下，看deadline是否没变（保护续定的用户可以继续使用)
			    */
			  
			   long userId = Long.parseLong(session.getUserId());
			   
			   try {
				   Date originalDeadline = tadgetManager.getTimService().getExpiryDate(appKey,userId);
				   //如果用户的查询到的deadline比session的value里的deadline时间要后，说明用户发生了续定，更新
				   if(originalDeadline!=null&&originalDeadline.getTime()>Long.parseLong(deadLine)){
					   session.setProperty(Session.SESSION_DEAD_LINE, String.valueOf(originalDeadline.getTime()));
					   this.sessionGenerator.updateSession(session);
				   }else{//如果为空(订购关系过期查询出来的就是空），那么删除sessionkey，然后报错
					   this.sessionGenerator.deleteSessionId(sessionId);
					   throw new SessionExpiredException("Session invalidation be fired by session deadline");
				   }
				} catch (TIMServiceException e) {
					log.error(e.getMessage(), e);
				}
		   }
	   }
		//判断sessionKey对应的session的userId后四位和sessionKey中的是否匹配。（前提是此sessionkey是通过调用容器生产的）
		if(sessionId.length() == 37 && !sessionId.substring(1, 5).equals(getSubUID(session.getUserId()))){
			throw new SessionOwnerUnmatchException(sessionId + " no privilege!");
		}else {
			// 如果创建的session类型不是固定有效时间的，则更新过期时间。否则不更新过期时间。
			if(session.getType() != fixted_type){
				this.sessionGenerator.updateSession(session);
			}			
		}
		return session;
	}
	
	/**
	 * 取到userid的后四位，如果userId不足四位，则在前面补零
	 * @param userId
	 * @return
	 */
	private String getSubUID(String userId){
		if(StringUtil.isEmpty(userId)){
			return "";
		}
		if(userId.length() >= 4){
			return userId.substring(userId.length() - 4);
		}else{
			StringBuilder subUID = new StringBuilder();
			char[] temp = new char[4 - userId.length()];
			for(int i = 0 ; i < temp.length ; i++){
				temp[i] = '0';
			}
			subUID.append(temp).append(userId);
			return subUID.toString();
		}	
	}
	
}
