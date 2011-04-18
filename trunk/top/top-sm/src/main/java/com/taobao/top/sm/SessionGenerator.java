/**
 * 
 */
package com.taobao.top.sm;

/**
 * 会话码生成和更新
 * 
 * @version 2008-11-21
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public interface SessionGenerator {

	/**
	 * 生成持续有效会话码，绑定到多个api_key上
	 * 
	 * @param apiKeys
	 *            api_key列表
	 * @param method
	 *            api方法 FIXME 有bug,不能用""
	 * @param sessionNick
	 *            会话帐号昵称
	 * @param validThru
	 *            有效期，毫秒
	 * @param isFixedTime
	 *            有效期是否固定
	 * @return
	 * @throws SessionGenerateException
	 */
	String generateKeepAliveSession(String method, String sessionNick,
			String userId, long validThru, boolean isFixedTime,String appSecret,
			String... apiKeys) throws SessionGenerateException;

	/**
	 * 生成仅适用一次会话码
	 * 
	 * @param apiKey
	 *            程序key
	 * @param method
	 *            api方法
	 * @param sessionNick
	 *            会话帐号昵称
	 * @return
	 * @throws SessionGenerateException
	 */
	String generateOneTimeSession(String method, String sessionNick,
			String userId, String appSecret, String... apiKeys) throws SessionGenerateException;

	/**
	 * 还原会话
	 * 
	 * @param sessionId
	 * @param apiKey
	 * @return
	 * @throws SessionNotExistException
	 * @throws SessionOwnerUnmatchException
	 *             该sessionId不是该apiKey所拥有
	 * @throws SessionValueInvalidException
	 */
	Session revertSession(String sessionId) throws SessionRevertException;

	/**
	 * 更新会话
	 * 
	 * @param session
	 * @throws SessionRevertException
	 * @throws SessionGenerateException
	 */
	void updateSession(Session session) throws SessionRevertException,
			SessionGenerateException;

	/**
	 * 删除会话
	 * 
	 * @param sessionId
	 * @throws SessionRevertException
	 */
	void deleteSessionId(String sessionId) throws SessionRevertException;
	/**
	 * 绑定sessionKey
	 */
	void bindAppkey(String sessionId,String... appkey)throws SessionGenerateException;
	

}
