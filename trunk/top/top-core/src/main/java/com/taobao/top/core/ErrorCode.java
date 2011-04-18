/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

/**
 * @version 2008-2-29
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 *         <table>
 *         <thead>
 *         <tr>
 *         <td>错误码</td>
 *         <td>错误信息</td>
 *         <td>说明</td>
 *         </tr>
 *         </thead> <tbody>
 *         <tr>
 *         <td>3</td>
 *         <td>Upload fail</td>
 *         <td>上传出错</td>
 *         </tr>
 *         <tr>
 *         <td>9</td>
 *         <td>[Http action] not allowed</td>
 *         <td>该方法不允许使用此Http动作</td>
 *         </tr>
 *         <tr>
 *         <td>10</td>
 *         <td>Service currently unavailable</td>
 *         <td>服务不可用</td>
 *         </tr>
 *         <tr>
 *         <td>11</td>
 *         <td>Insufficient ISV permissions</td>
 *         <td>第三方程序权限不够</td>
 *         </tr>
 *         <tr>
 *         <td>12</td>
 *         <td>Insufficient user permissions</td>
 *         <td>用户权限不够</td>
 *         </tr>
 *         <tr>
 *         <td>21</td>
 *         <td>Missing Method</td>
 *         <td>方法丢失</td>
 *         </tr>
 *         <tr>
 *         <td>22</td>
 *         <td>Invalid Method</td>
 *         <td>方法无效</td>
 *         </tr>
 *         <tr>
 *         <td>23</td>
 *         <td>Invalid Format</td>
 *         <td>响应格式无效</td>
 *         </tr>
 *         <tr>
 *         <td>24</td>
 *         <td>Missing signature</td>
 *         <td>签名丢失</td>
 *         </tr>
 *         <tr>
 *         <td>25</td>
 *         <td>Invalid signature</td>
 *         <td>签名无效</td>
 *         </tr>
 *         <tr>
 *         <td>26</td>
 *         <td>Missing session</td>
 *         <td>会话期识别码丢失</td>
 *         </tr>
 *         <tr>
 *         <td>27</td>
 *         <td>Invalid session</td>
 *         <td>会话期识别码无效</td>
 *         </tr>
 *         <tr>
 *         <td>28</td>
 *         <td>Missing API Key</td>
 *         <td>api_key丢失</td>
 *         </tr>
 *         <tr>
 *         <td>29</td>
 *         <td>Invalid API Key</td>
 *         <td>api_key无效</td>
 *         </tr>
 *         <tr>
 *         <td>30</td>
 *         <td>Missing timestamp</td>
 *         <td>时间戳丢失</td>
 *         </tr>
 *         <tr>
 *         <td>31</td>
 *         <td>Invalid timestamp</td>
 *         <td>时间戳无效</td>
 *         </tr>
 *         <tr>
 *         <td>32</td>
 *         <td>Missing version</td>
 *         <td>版本丢失</td>
 *         </tr>
 *         <tr>
 *         <td>33</td>
 *         <td>Invalid version</td>
 *         <td>版本错误</td>
 *         </tr>
 *         <tr>
 *         <td>40</td>
 *         <td>Missing required arguments</td>
 *         <td>参数丢失，指除method, api_key, sign,timestamp，session,format,ver外的其他参数丢失
 *         </td>
 *         </tr>
 *         <tr>
 *         <td>45</td>
 *         <td>FORBIDDEN_VERTION</td>
 *         <td>该版本已经被禁止调用
 *         </td>
 *         </tr>
 *         <tr>
 *         <td colspan="3">TBQL错误</td>
 *         </tr>
 *         <tr>
 *         <td>100</td>
 *         <td>Error while parsing TBQL statement</td>
 *         <td>TBQL执行错误</td>
 *         </tr>
 *         <tr>
 *         <td>101</td>
 *         <td>The field you requested does not exist</td>
 *         <td>请求的字段不存在</td>
 *         </tr>
 *         <tr>
 *         <td>102</td>
 *         <td>The table you requested does not exist</td>
 *         <td>请求的表不存在</td>
 *         </tr>
 *         <tr>
 *         <td>103</td>
 *         <td>Your statement is not indexable</td>
 *         <td>语句不能被索引</td>
 *         </tr>
 *         <tr>
 *         <td>104</td>
 *         <td>The function you called down not exist</td>
 *         <td>调用的函数不存在</td>
 *         </tr>
 *         <tr>
 *         <td>105</td>
 *         <td>Wrong number of arguments passed into the function</td>
 *         <td>函数参数个数不正确</td>
 *         </tr>
 *         <tr>
 *         <td>106</td>
 *         <td>Wrong type of arguments passed into the function</td>
 *         <td>函数参数类型不正确</td>
 *         </tr>
 *         <tr>
 *         <td colspan="3">用户错误</td>
 *         </tr>
 *         <tr>
 *         <td>200</td>
 *         <td>People not found</td>
 *         <td>用户不存在</td>
 *         </tr>
 *         <tr>
 *         <td colspan="3">商品错误</td>
 *         </tr>
 *         <tr>
 *         <td>300</td>
 *         <td>Item not found</td>
 *         <td>商品不存在</td>
 *         </tr>
 *         <tr>
 *         <td colspan="3">交易错误</td>
 *         </tr>
 *         <tr>
 *         <td>400</td>
 *         <td>trade not found</td>
 *         <td>交易不存在</td>
 *         </tr>
 *         <tr>
 *         <td colspan="3">店铺错误</td>
 *         </tr>
 *         <tr>
 *         <td>500</td>
 *         <td>shop not found</td>
 *         <td>店铺不存在</td>
 *         </tr>
 *         <tr>
 *         <td colspan="3">评价错误</td>
 *         </tr>
 *         <tr>
 *         <td>600</td>
 *         <td>rate not found</td>
 *         <td>评价不存在</td>
 *         </tr>
 *         </tbody>
 *         </table>
 * 
 */
public enum ErrorCode
{
	PLATFORM_SYSTEM_ERROR(1,"Platform System error"),PLATFORM_SYSTEM_BLACKLIST(2,"Platform System blacklist"),
	UPLOAD_FAIL(3, "Upload fail"), CALL_LIMITED_USER_API(4, "User Call limited"), CALL_LIMITED_SESSION(
			5, "Session Call limited"), CALL_LIMITED_PARTNER(6,
			"Partner Call limited"), CALL_LIMITED_APP_API(7, "App Call Limited"), CALL_LIMITED_FREQ(
			8, "App call exceeds limited frequency"), HTTP_ACTION_NOT_ALLOWED(
			9, "Http action not allowed"), SERVICE_CURRENTLY_UNAVAILABLE(10,
			"Service currently unavailable"), INSUFFICIENT_ISV_PERMISSIONS(11,
			"Insufficient isv permissions"), INSUFFICIENT_USER_PERMISSIONS(12,
			"Insufficient user permissions"),
	/**
	 * TODO: 业务未理清，暂不对外。 对合作伙伴的权限不足
	 */
	INSUFFICIENT_PARTNER_PERMISSIONS(13, "Insufficient partner permissions"), REMOTE_SERVICE_ERROR(
			15, "Remote service error"), MISSING_METHOD(21, "Missing method"), INVALID_METHOD(
			22, "Invalid method"), INVALID_FORMAT(23, "Invalid format"), MISSING_SIGNATURE(
			24, "Missing signature"), INVALID_SIGNATURE(25, "Invalid signature"), MISSING_SESSION(
			26, "Missing session"), INVALID_SESSION(27, "Invalid session"), MISSING_APP_KEY(
			28, "Missing app key"), INVALID_APP_KEY(29, "Invalid app Key"), MISSING_TIMESTAMP(
			30, "Missing timestamp"), INVALID_TIMESTAMP(31, "Invalid timestamp"), MISSING_VERSION(
			32, "Missing version"), INVALID_VERSION(33, "Invalid version"), UNSUPPORTED_VERSION(
			34, "Unsupported version"), MISSING_REQUIRED_ARGUMENTS(40,
			"Missing required arguments"), INVALID_ARGUMENTS(41,
			"Invalid arguments"), @Deprecated
	FORBIDDEN_REQUEST(42, "Forbidden request"), PARAMETER_ERROR(43,
			"parameter error"),ISP_ERROR(44,"isp error"), FORBIDDEN_VERTION(45, "Forbidden version"),
			ABANDON_IID(46,"iid abandoned,use num_iid"),INVALID_ENCODING(47,"Invalid encoding"),
	
	/**
	 * 为了禁止1.0访问，设置的错误码
	 */
	//FORBIDDEN_1_0_CALL(20,"API1.0不可用，请升级到API2.0，升级方法请参考：http://open.taobao.com/bbs/read.php?tid=15113");
	FORBIDDEN_1_0_CALL(20,"");
	private int code;

	private String msg;

	public int getCode()
	{
		return code;
	}

	/**
	 * 
	 * @param code
	 * @return
	 */
	public static boolean isInTopErrorRange(int code)
	{
		// FIXME zixue 不严格判断 最大不超过200
		return code < 200 && code > 0;
	}

	public String getMsg()
	{
		return msg;
	}

	@Override
	public String toString()
	{
		return code + ":" + msg;
	}

	private ErrorCode(int code, String msg)
	{
		this.code = code;
		this.msg = msg;
	}

}
