package com.taobao.top.ats.util;

/**
 * 错误码。
 * 
 * @author moling
 * @since 1.0, 2010-8-20
 */
public final class ErrorCode {
	public static final String SUB_CODE = "sub_code";
	public static final String SUB_MSG = "sub_msg";

	// 参数校验错误
	public static final String INVALID_PARAMETER = "isv.invalid-parameter";
	// 参数长度不匹配
	public static final String MISMATCH_PARAMETER = "isv.mismatch-parameter";
	// 缺少参数
	public static final String MISSING_PARAMETER = "isv.missing-parameter";
	// 远程服务错误
	public static final String REMOTE_SERVICE_ERROR = "isp.remote-service-error";
	// 远程服务超时
	public static final String REMOTE_SERVICE_TIMEOUT = "isp.remote-service-timeout";
	// 远程连接错误
	public static final String REMOTE_CONNECTION_ERROR = "isp.remote-connection-error";
	// API解析错误（出现了未被明确控制的异常信息，需要改进代码）
	public static final String API_PARSE_ERROR = "isp.api-parse-error";
	// 未知错误错误（出现了未被明确控制的错误信息，需要修改代码）
	public static final String UNKNOWN_ERROR = "isp.unknown-error";
	// API空指针错误
	public static final String NULL_POINTER_EXCEPTION = "isp.null-pointer-exception";
	// API空指针错误
	public static final String API_NOT_EXIST = "isv.api-not-exist";

	// 任务不存在
	public static final String TASK_NOT_EXIST = "isv.task-not-exist";
	// 任务不属于当前APP
	public static final String MISMATCH_TASK_APP = "isv.mismatch-task-app";

	// ATS服务不可用
	public static final String ATS_SERVICE_UNAVAILABLE = "isp.ats-service-unavailable";
	// TFS读文件错误
	public static final String READ_RESULT_ERROR = "isp.read-result-error";
	
	// precheck时后台返回无法分辨的结果
	public static final String PRECHECK_RESULT_UNKNOWN = "isp.precheck-result-unknown";

	// 生成下载地址异常
	public static final String GENERATE_DOWNLOAD_URL_ERROR = "isp.generate-download-url-error";
}
