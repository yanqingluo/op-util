package com.taobao.top.notify;

/**
 * 系统通用异常。
 * 
 * @author fengsheng
 * @since 1.0, Dec 14, 2009
 */
public class NotifyException extends Exception {

	private static final long serialVersionUID = -5034893028326839281L;

	public NotifyException() {
		super();
	}

	public NotifyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotifyException(String message) {
		super(message);
	}

	public NotifyException(Throwable cause) {
		super(cause);
	}

}
