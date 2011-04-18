/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

import java.util.HashSet;
import java.util.Set;

/**
 * TOP的系统级参数常量
 * 
 * @version 2008-3-4
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public abstract class ProtocolConstants {
	
	public static final Set<String> systemParams = new HashSet<String>();
	
	public static final String P_METHOD = "method";

	public static final String P_VERSION = "v";
	
	public static final String VERSION_1 = "1.0";
	
	public static final String VERSION_2 = "2.0";
	
	public static final String P_STYLE = "s";
	
	/**
	 * Though c is a system level parameter,
	 * however, TIP would not use it directly,
	 * so, no need to add a cache in DefaultApiInput
	 * for it.
	 */
	public static final String P_COMPRESS = "c";

	public static final String P_TIMESTAMP = "timestamp";

	public static final String P_API_KEY = "api_key";
	
	public static final String P_APP_KEY = "app_key";
	
	public static final String P_SIGN = "sign";
	
	public static final String P_SIGN_METHOD = "sign_method";
	
	public static final String SIGN_METHOD_MD5 = "md5";
	
	public static final String SIGN_METHOD_HMAC = "hmac";

	/**
	 * 合作方id
	 */
	public static final String P_PARTNER_ID = "partner_id";

	public static final String P_SESSION = "session";

	public static final String P_SESSION_NICK = "session_nick";
	
	public static final String P_SESSION_UID = "session_uid";

	public static final String P_FORMAT = "format";

	public static final String FORMAT_XML = "xml";

	public static final String FORMAT_JSON = "json";
    
	public static final String FORMAT_STR="str";
	
	public static final String P_CALLBACK = "callback";
    
	public static final String P_APP_IP="app_ip";
	
	public static final String P_ENDUSER_IP = "top_enduser_ip";
	
	public static final String FORMAT_HTML = "html";
	
	/*
	 * ISP透传的签名top_sign
	 */
	public static final String P_TOP_SIGN = "top_sign";
	
	/**
	 * Tag, value of this key is integer: 1,2,...,n
	 */
	public static final String P_TOP_TAG = "top_tag";
	
	/**
	 * Session Bound, value of this key is the nick.
	 */
	public static final String P_TOP_BIND_NICK = "top_bind_nick";
	
	/**
	 * value of this key is isvId
	 */
	public static final String P_TOP_ISV_ID = "top_isv_id";
	
	static{
		systemParams.add(P_METHOD);
		systemParams.add(P_VERSION);
		systemParams.add(P_TIMESTAMP);
		systemParams.add(P_API_KEY);
		systemParams.add(P_APP_KEY);
		systemParams.add(P_SIGN);
		systemParams.add(P_SIGN_METHOD);
		systemParams.add(P_PARTNER_ID);
		systemParams.add(P_SESSION);
		systemParams.add(P_FORMAT);
		systemParams.add(P_CALLBACK);
	}
}
