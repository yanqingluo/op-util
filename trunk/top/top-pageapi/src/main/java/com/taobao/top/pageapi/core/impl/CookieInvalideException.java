/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.pageapi.core.impl;

import com.taobao.top.core.exception.TopException;

/**
 * 验证cookie 失败抛出的异常
 * 
 * @version 2009-08-16
 * @author <a href="mailto:yueqian@taobao.com">yueqian</a>
 * 
 */
public class CookieInvalideException extends TopException {

	private static final long serialVersionUID = 6797279593719308722L;

	private Object invalidValue;

	private String key;
	
	public CookieInvalideException(String msg) {
		super(msg);
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

	public String toString() {
		return "Cookie invalid[" + key + ":" + invalidValue + "]";
	}

}
