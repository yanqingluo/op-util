package com.taobao.top.pageapi.common;

/**
 * 签名错误异常
 * @version 2009-08-16
 * @author <a href="mailto:yueqian@taobao.com">yueqian</a>
 *
 */
public class EncryptException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7918721863029334392L;
	
	public EncryptException(Exception e){
		super(e);
	}
	public EncryptException(String  e){
		super(e);
	}

}
