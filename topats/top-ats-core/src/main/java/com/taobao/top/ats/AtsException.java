package com.taobao.top.ats;

/**
 * ATS通用异常。
 * 
 * @author carver.gu
 * @since 1.0, Aug 18, 2010
 */
public class AtsException extends Exception {

	private static final long serialVersionUID = -1134081985686097804L;

	public AtsException() {
		super();
	}

	public AtsException(String message, Throwable cause) {
		super(message, cause);
	}

	public AtsException(String message) {
		super(message);
	}

	public AtsException(Throwable cause) {
		super(cause);
	}

}
