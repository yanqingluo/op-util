/**
 * 
 */
package com.taobao.top.sm;

/**
 * session 存贮的值无效
 * 
 * 
 * @version 2009-2-28
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public class SessionValueInvalidException extends SessionRevertException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 783018221945905328L;

	/**
	 * 
	 */
	public SessionValueInvalidException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public SessionValueInvalidException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public SessionValueInvalidException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SessionValueInvalidException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
