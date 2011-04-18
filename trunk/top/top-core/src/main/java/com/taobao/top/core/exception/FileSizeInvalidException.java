package com.taobao.top.core.exception;


public class FileSizeInvalidException extends TopException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1093139935663157928L;
	public FileSizeInvalidException(){
		
	}
	public FileSizeInvalidException(String message,Throwable cause){
		super(message,cause);
	}
	public FileSizeInvalidException(String message){
		super(message);
	}
	public FileSizeInvalidException(Throwable cause){
		super(cause);
	}
}
