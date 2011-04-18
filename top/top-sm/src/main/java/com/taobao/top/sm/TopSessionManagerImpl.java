package com.taobao.top.sm;


/**
 * top 的session管理者
 * @author zhudi
 *
 */
public class TopSessionManagerImpl implements SessionManager {
	
	private SessionManager tairSessionManager;
	private SessionManager timSessionManager;
	
	public void setTairSessionManager(SessionManager tairSessionManager) {
		this.tairSessionManager = tairSessionManager;
	}



	public void setTimSessionManager(SessionManager timSessionManager) {
		this.timSessionManager = timSessionManager;
	}



	@Override
	public Object revertValidSession(String sessionId, String appKey)
			throws SessionRevertException, SessionGenerateException {
		if(sessionId.charAt(0)=='4'){
			return timSessionManager.revertValidSession(sessionId, appKey);
		}else{
			return tairSessionManager.revertValidSession(sessionId, appKey);
		}
	}

}
