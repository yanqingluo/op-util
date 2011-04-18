package com.taobao.top.core.exception;

public class FileNumInvalidException extends TopException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6257394005778092605L;

	/**
	 * 
	 */
	public FileNumInvalidException() {
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FileNumInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public FileNumInvalidException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public FileNumInvalidException(Throwable cause) {
		super(cause);
	}

}
