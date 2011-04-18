/**
 * 
 */
package com.taobao.top.sm;

/**
 * @version 2008-11-20
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class SessionExpiredException extends SessionRevertException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8241797541983883247L;

	/**
	 * 
	 */
	public SessionExpiredException() {
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SessionExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public SessionExpiredException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SessionExpiredException(Throwable cause) {
		super(cause);
	}

}
