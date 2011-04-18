package com.taobao.top.sm;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.common.TOPConstants;
import com.taobao.top.tim.domain.AuthDO;
import com.taobao.top.tim.service.SamService;
import com.taobao.top.tim.service.TIMServiceException;

/**
 * 持久化的授权关系管理者
 * @author zhudi
 *
 */
public class TimSessionManagerImpl implements SessionManager {
	
	private static final Log log = LogFactory.getLog(TimSessionManagerImpl.class);
	
	/**
	 * 去Tim中查询长久的授权关系
	 */
	private SamService timService = null;

	public SamService getTimService() {
		return timService;
	}

	public void setTimService(SamService timService) {
		this.timService = timService;
	}

	@Override
	public AuthDO revertValidSession(String sessionId, String appKey)
			throws SessionRevertException, SessionGenerateException {
		
		if(!checkSessionCreateByTop(sessionId,appKey)){
			SessionNotExistException e = new SessionNotExistException();
			e.setDetail(TOPConstants.ISV_INVALID_PERSISTENT_SESSION_PATTERN);
			throw e;
		}
		AuthDO authDO = null;
		try {
			authDO = timService.getPersistentAuthBySessionKey(sessionId);
		} catch (TIMServiceException e) {
			log.error("error to get session from tim ,e :"+e.getMessage());
			throw new SessionRevertException(sessionId, e);
		}
		//如果没有这个sessionkey
		if(authDO==null){
			SessionNotExistException e = new SessionNotExistException();
			e.setDetail(TOPConstants.ISV_PERSISTENT_SESSION_NOT_EXIST);
			throw e;
		}
		//session过期了
		if(authDO.getValidDate()!=null&&authDO.getValidDate().getTime() < System.currentTimeMillis()){
			throw new SessionExpiredException("Session expired!");
		}
		return authDO;
	}
	
	protected boolean checkSessionCreateByTop(String sessionId,String appKey){
		if(sessionId.length() < TOPConstants.SESSION_KEY_MIN_LENGTH){//如果sessionId连34位都没有，肯定不是top建的
			return false;
		}else{
			String userId = sessionId.substring(1,sessionId.length()-32);
			String checkCode = sessionId.substring(sessionId.length()-32,sessionId.length());
			StringBuilder md5Str = new StringBuilder();
			md5Str.append(appKey);
			md5Str.append(':');
			md5Str.append(userId);
			String compareCode = null;
			try {
				compareCode = DigestUtils.md5Hex(md5Str.toString().getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				log.error(e.getMessage());
			}
			
			if(checkCode.equals(compareCode)){
				return true;
			}else{
				return false;
			}
		}
	}

}
