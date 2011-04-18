/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

/**
 * @version 2008-3-6
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 *
 */
public class ApiConfigException extends Exception {

	private static final long serialVersionUID = -5848370702203255197L;

	/**
	 * 
	 */
	public ApiConfigException() {
	}

	/**
	 * @param message
	 */
	public ApiConfigException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ApiConfigException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ApiConfigException(String message, Throwable cause) {
		super(message, cause);
	}

}
