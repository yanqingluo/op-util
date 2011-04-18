/**
 * 
 */
package com.taobao.top.sm;

/**
 * Session生成失败错误
 * 
 * @version 2008-11-20
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class SessionGenerateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3610538623508330767L;

	/**
	 * 
	 */
	public SessionGenerateException() {
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SessionGenerateException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public SessionGenerateException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SessionGenerateException(Throwable cause) {
		super(cause);
	}

}
