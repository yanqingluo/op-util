/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

import com.taobao.top.core.exception.TopException;

/**
 * @version 2008-3-4
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class ApiParameterFormatException extends TopException {

	private static final long serialVersionUID = 6797279593719308722L;

	private Object invalidValue;

	private String key;

	/**
	 * @param cause
	 */
	public ApiParameterFormatException(String key, Object invalidValue) {
		this.invalidValue = invalidValue;
		this.key = key;
	}

	/**
	 * @param cause
	 */
	public ApiParameterFormatException(Throwable cause, String key,
			Object invalidValue) {
		super(cause);
		this.invalidValue = invalidValue;
		this.key = key;
	}

	/**
	 * @return the invalidValue
	 */
	public Object getInvalidValue() {
		return invalidValue;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return "parameter format invalid:[" + key + ":" + invalidValue + "]";
	}

}
