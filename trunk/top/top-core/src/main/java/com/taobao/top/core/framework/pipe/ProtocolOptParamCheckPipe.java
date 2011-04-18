package com.taobao.top.core.framework.pipe;

import static com.taobao.top.core.ProtocolConstants.P_SESSION;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.core.Api;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.ProtocolConstants;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.core.framework.TopPipeUtil;
import com.taobao.top.privilege.WhiteListManager;
import com.taobao.top.sm.Session;
import com.taobao.top.sm.SessionExpiredException;
import com.taobao.top.sm.SessionManager;
import com.taobao.top.sm.SessionNotExistException;
import com.taobao.top.sm.SessionOwnerUnmatchException;
import com.taobao.top.tim.domain.AuthDO;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.util.CollectionUtil;

/**
 * 检查协议可选参数,目前只有session
 * @author zhenzi
 *
 */
public class ProtocolOptParamCheckPipe extends TopPipe<TopPipeInput, TopPipeResult> {
	private static final Log logger = LogFactory.getLog(ProtocolOptParamCheckPipe.class);
	
	/**
	 * 白名单放行
	 */
	private WhiteListManager whiteListManager;

	public void setWhiteListManager(WhiteListManager whiteListManager) {
		this.whiteListManager = whiteListManager;
	}
	
	private SessionManager sessionManager;

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	@Override
	public void doPipe(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		checkSession(pipeInput,pipeResult);
	}

	@Override
	public boolean ignoreIt(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		if(pipeResult.getErrorCode() != null || TopPipeUtil.isIgnore(pipeInput)){
			return true;
		}
		/*
		 * 如果此api必须传递sessionKey，则不能忽略
		 */
		Api api = pipeInput.getApi();
		if(api == null){//如果没有请求的api对应的Api对象，则忽略此管道
			return true;
		}
		List<String> protocolParam = null; 
		protocolParam = api.getProtocolMustParams();
		if(!CollectionUtil.isEmpty(protocolParam)){
			if(protocolParam.contains(P_SESSION)){
				return false;
			}
		}
		/*
		 * 如果此api可选传递sessionKey，在分两种，
		 * 1，如果appkey是自用型的。则不能忽略。
		 * 2，如果appkey不是自用型的，则根据用户是否传递了session参数，判断是否忽略，传了则不能忽略，不传则可以忽略
		 */
		protocolParam = null;
		protocolParam = api.getProtocolPrivateParams();
		if(!CollectionUtil.isEmpty(protocolParam)){
			if(protocolParam.contains(P_SESSION)){//可选参数中包括session参数
				String[] bindNick_userId = getSelfUseAppBindNick_UserId(pipeInput.getAppDO());
				if(bindNick_userId != null){//自用型的
					return false;
				}
				if (pipeInput.getSession() != null
						|| StringUtils.isNotBlank(pipeInput.getString(
								ProtocolConstants.P_SESSION_NICK, true))
						|| StringUtils.isNotBlank(pipeInput.getString(
								ProtocolConstants.P_SESSION_UID, true))) {// 用户传了session参数，或者此appkey传递了session_nick或者session_uid
					return false;
				}
			}
		}
		return true;
	}

	//-----------------自用型不需要sessionKey的checkSession方法
	/**
	 * 检查客户端传递的sessionKey正确性
	 *   //FIXME 如何改造，使得只传递此方法需要的参数
	 * @param pipeInput
	 * @return
	 */
	
	private void checkSession(TopPipeInput pipeInput,TopPipeResult result){
		// 如果调用方appkey或ip不在白名单，不是免登用户，不是自用型应用，要进行session校验。
		String appkey = pipeInput.getAppKey();
		String[] bindNick_userId = getSelfUseAppBindNick_UserId(pipeInput.getAppDO());
		if ((!whiteListManager.isAppWhite(appkey) || !whiteListManager
				.isIpWhite(pipeInput.getAppIp()))
				&& bindNick_userId == null) {
			String sessionId = pipeInput.getSession();
			if(sessionId == null){//如果是null
				result.setErrorCode(ErrorCode.MISSING_SESSION);
				return ;
			}
			if(StringUtils.isBlank(sessionId)){//如果为空字符串
				result.setErrorCode(ErrorCode.INVALID_SESSION);
				result.setMsg("Session not exist");
				return ;
			}
			Object blackboxSession = null;
			try {
				blackboxSession = sessionManager.revertValidSession(sessionId, appkey);
			} catch (SessionNotExistException e) {
				if (logger.isDebugEnabled()) {
					logger.debug(new StringBuilder("session not exist:").append(sessionId), e);
				}
				result.setErrorCode(ErrorCode.INVALID_SESSION);
				result.setMsg("Session not exist");
				if(e!=null&&e.getDetail()!=null){
					result.setSubCode(e.getDetail());
				}
				return ;
			} catch (SessionExpiredException e) {
				if (logger.isDebugEnabled()) {					
					logger.debug(new StringBuilder("session expired:").append(sessionId), e);
				}
				result.setErrorCode(ErrorCode.INVALID_SESSION);
				result.setMsg("Session expired");
				return ;
			} catch (SessionOwnerUnmatchException e) {
				if (logger.isDebugEnabled()) {
					logger.debug(new StringBuilder("session owner unmatch:").append(sessionId), e);
				}
				result.setErrorCode(ErrorCode.INVALID_SESSION);
				result.setMsg("Session owner unmatch");
				return ;
			}catch(Exception e){
				if(logger.isWarnEnabled()){
					logger.warn(e,e);
				}
				result.setErrorCode(ErrorCode.INVALID_SESSION);
				result.setMsg("Service error");
				return ;
			}
			/**
			 * 还原session，拿到NICK和UID
			 * 目前有两种session 一种是tair里的Session（临时的session）
			 * 一种是Tim数据库中的authDO（持久化session）
			 */
			if(blackboxSession instanceof Session){//临时session
				Session session = (Session) blackboxSession;
				pipeInput.setSessionNick(session.getNick());
				pipeInput.setSessionUid(session.getUserId());
			}else if(blackboxSession instanceof AuthDO){//持久化session
				AuthDO session = (AuthDO) blackboxSession;
				pipeInput.setSessionNick(session.getNick());
				pipeInput.setSessionUid(String.valueOf(session.getUserid()));
			}
			return ;
		}else {
			if(bindNick_userId != null){//自用型
				pipeInput.setSessionNick(bindNick_userId[0]);
				pipeInput.setSessionUid(bindNick_userId[1]);
			}else{//参照DefaultApiChecker中的bindSession // is white
				pipeInput.setSessionUid(pipeInput.getString(ProtocolConstants.P_SESSION_UID, true));
				pipeInput.setSessionNick(pipeInput.getString(ProtocolConstants.P_SESSION_NICK, true));
				if(null == pipeInput.getSessionNick()){
					result.setErrorCode(ErrorCode.INVALID_SESSION);
					result.setMsg("Missing session_nick");
					return ;
				}				
			}
			return ;
		}
	}
	/**
	 * 判断是否为自用型应用，
	 * @param apiInput
	 * @return
	 */
	private String[] getSelfUseAppBindNick_UserId(TinyAppDO appDO){
		if(appDO == null){
			return null;
		}
		
		String bindNick = null;
		String userId = null;
		
		bindNick = appDO.getBindNick();
		userId = appDO.getUserId() != null?appDO.getUserId().toString():null;
		
		if(bindNick == null || userId == null){
			return null;
		}		
		return new String[]{bindNick,userId};
	}
}
