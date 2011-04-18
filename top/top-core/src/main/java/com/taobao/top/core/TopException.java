/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

/**
 * @version 2008-2-29
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 *
 */
public class TopException extends Exception {

	private static final long serialVersionUID = -238091758285157331L;

	public TopException() {
		super();
	}

	public TopException(String message, Throwable cause) {
		super(message, cause);
	}

	public TopException(String message) {
		super(message);
	}

	public TopException(Throwable cause) {
		super(cause);
	}

}
