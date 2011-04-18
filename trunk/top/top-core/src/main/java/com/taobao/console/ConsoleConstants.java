package com.taobao.console;

/**
 * 控制台用到的常量
 * @author zhenzi
 * 2010-12-24 下午12:21:23
 */
public final class ConsoleConstants {
	public static final String CACHE_TYPE = "type";
	
	public static final String CACHE_TYPE_BLACK_LIST = "blacklist";
	public static final String CACHE_TYPE_SESSION = "session";
	public static final String CACHE_TYPE_API = "api";
	public static final String CACHE_TYPE_OTHER = "other";
	
	
	public static final String CMD = "cmd";
	public static final String cmd_delete = "delete";
	public static final String cmd_select = "select";
	public static final String cmd_update = "update";
	
	public static final String OPT_TRUE = "true";
	public static final String OPT_FALSE = "false";
	
	/**
	 * TIP的会话默认保持时间为半个小时（用于在TOP-admin中没有设置的情况，用于延长的session生成机制）
	 */
	public static final long SESSION_VALIDATE_TIME = 30 * 60 * 1000;
	/**
	 * 对于固定时间失效的sessionKey的默认失效时间（默认为12小时）
	 */
	public static final long FIX_TYPE_SESSION_VALIDATE_TIME = 12 * 60 * 60 *1000;
}
