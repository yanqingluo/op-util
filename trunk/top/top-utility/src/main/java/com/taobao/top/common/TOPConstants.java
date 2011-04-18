package com.taobao.top.common;

public class TOPConstants 
{
	public final static String  TOP_CONFIG = "top-config.properties";
	public final static String TOP_INNERIP = "top-innerip";
	public final static String ISP_ERRORCODE = "isp-errorcode";
	
	
	public final static String TOP_TRACK_TAG_GETMETHOD = "gm";
	public final static String TOP_TRACK_TAG_GETAPI= "ga";
	public final static String TOP_TRACK_TAG_CHECKSYSPARAMS= "csys";
	public final static String TOP_TRACK_TAG_CALLLIMIT= "cm";
	public final static String TOP_TRACK_TAG_CHECKOPTPARAM = "copt";
	public final static String TOP_TRACK_TAG_CHECKSERVICEPARAM = "cser";
	public final static String TOP_TRACK_TAG_CHECKAPPLICATIONPARAM = "capp";
	
	public final static String TOP_LOG = "toplog";
	public final static String TOP_ERROR_CODE = "errorcode";
	public final static String TOP_TRANSACTION_BEG = "_transaction_beg_";
	
	//default version
	public final static String[] DEFAULT_VERSIONS = new String[] { "1.0" };
	
	//sub error code. added by huaisu. TODO: refactor it to a enum
	public final static String ISP_TOP_MAPPING_PARSE_ERROR = "isp.top-mapping-parse-error";
	public final static String ISP_TOP_REMOTE_CONNECTION_ERROR = "isp.top-remote-connection-error";
	public final static String ISP_TOP_REMOTE_CONNECTION_TIMEOUT = "isp.top-remote-connection-timeout";
	public final static String ISP_UNKNOWN_ERROR = "isp.unknown-error";
	public final static String ISP_REMOTE_SERVICE_UNAVAILABLE = "isp.top-remote-service-unavailable";
	
	public final static String ISP_REDIRECT_ERROR = "isp.redirect-error";
	
	public final static String FLOW_LIMITED_BY_APP_RULE = "isv.access-limited-by-app-access-count";
	public final static String FLOW_LIMITED_BY_API_RULE = "isv.access-limited-by-api-access-count";
	public final static String FLOW_LIMITED_BY_APP_API_RULE = "isv.access-limited-by-app-api-access-count";

	public final static String APPKEY_INVALID_NOT_EXIST = "isv.appkey not exists";
	public final static String APPKEY_INVALID_INVALID_STATUS = "isv.appkey invalid status";
	//sub error code. create by zhudi
	
	public final static String ISV_TIMESTAMP_LIMIT_ERROR = "isv.timestamp-limit-error";
	public final static String ISV_REPLAYATTACK_TIMESTAMP_INVALID_ERROR = "isv.replayattack-timestamp-invalid-error";
	public final static String ISV_REQUEST_REPLAY_ERROR = "isv.request-replay-error";
	
	public final static String ISV_INVALID_PERSISTENT_SESSION_PATTERN = "isv.invalid-persistent-session-pattern";
	public final static String ISV_PERSISTENT_SESSION_NOT_EXIST = "isv.persistent-session-not-exist";
	public final static int SESSION_KEY_MIN_LENGTH = 1+1+32;
	//end zhudi
	
	
}
