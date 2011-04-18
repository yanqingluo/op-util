/**
 * 
 */
package com.taobao.top.sm;

/**
 * 申请会话指定的API方法不存在
 * 
 * @version 2008-11-20
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 *
 */
public class SessionMethodNotExistException extends SessionGenerateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6948724815798256982L;

	/**
	 * 
	 */
	public SessionMethodNotExistException() {
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SessionMethodNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public SessionMethodNotExistException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SessionMethodNotExistException(Throwable cause) {
		super(cause);
	}

}
