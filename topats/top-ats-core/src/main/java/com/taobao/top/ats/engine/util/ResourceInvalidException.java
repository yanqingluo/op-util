package com.taobao.top.ats.engine.util;

/**
 * 非常资源异常。
 * 
 * @author carver.gu
 * @since 1.0, Aug 23, 2010
 */
public class ResourceInvalidException extends Exception {

	private static final long serialVersionUID = 769265418763187068L;

	public ResourceInvalidException() {
		super();
	}

	public ResourceInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceInvalidException(String message) {
		super(message);
	}

	public ResourceInvalidException(Throwable cause) {
		super(cause);
	}

}
