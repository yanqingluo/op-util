/**
 * 
 */
package com.taobao.top.sm;

/**
 * Sessin还原错误
 * 
 * @version 2009-2-28
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public class SessionRevertException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6927651772961115478L;

	/**
	 * 
	 */
	public SessionRevertException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public SessionRevertException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public SessionRevertException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SessionRevertException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
