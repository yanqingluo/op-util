/**
 * 
 */
package com.taobao.top.sm;

/**
 * 会话不存在
 * 
 * @version 2008-11-20
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class SessionNotExistException extends SessionRevertException {
	
	// session不存在分为好集中，使用detail参数，存储不同的SessionNotExistException 的子错误码，为空时 上层抓住，不设置子错误码
	private String detail;
	
	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1821331359309751854L;

	/**
	 * 
	 */
	public SessionNotExistException() {
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SessionNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public SessionNotExistException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SessionNotExistException(Throwable cause) {
		super(cause);
	}

}
