/**
 * 
 */
package com.taobao.top.sm;

/**
 * @version 2008-12-30
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class SessionOwnerUnmatchException extends SessionRevertException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7108391053543898614L;

	/**
	 * 
	 */
	public SessionOwnerUnmatchException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SessionOwnerUnmatchException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public SessionOwnerUnmatchException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SessionOwnerUnmatchException(Throwable cause) {
		super(cause);
	}

}
