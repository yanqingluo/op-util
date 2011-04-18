/**
 * 
 */
package com.taobao.top.sm;

/**
 * 会话管理器接口
 * 
 * 负责生成会话码和更新会话。
 * 
 * @version 2008-11-20
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public interface SessionManager {

	/**
	 * 根据sessionID，还原session对象
	 * 返回为object，兼容目前多种session对象
	 * 临时session和持久session
	 * @param sessionId
	 * @param appKey
	 * @return
	 * @throws SessionRevertException
	 * @throws SessionGenerateException
	 */
	Object revertValidSession(String sessionId, String appKey)
			throws SessionRevertException, SessionGenerateException;
	

}
