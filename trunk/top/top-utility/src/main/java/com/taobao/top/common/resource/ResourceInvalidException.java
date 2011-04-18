package com.taobao.top.common.resource;

public class ResourceInvalidException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public ResourceInvalidException(String msg) {
        super(msg);
    }
    
    public ResourceInvalidException(Throwable cause) {
        super(cause);
    }
    
    public ResourceInvalidException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    
}