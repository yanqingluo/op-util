package com.taobao.top.core.exception;


public class FileTypeInvalidException extends TopException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8135274904695801043L;
	
	public FileTypeInvalidException(){
		
	}
	public FileTypeInvalidException(String message,Throwable cause){
		super(message,cause);
	}
	public FileTypeInvalidException(String message){
		super(message);
	}
	public FileTypeInvalidException(Throwable cause){
		super(cause);
	}
}
