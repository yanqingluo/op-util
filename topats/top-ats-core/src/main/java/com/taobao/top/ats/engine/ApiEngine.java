package com.taobao.top.ats.engine;

/**
 * API调用引擎。
 * 
 * @author carver.gu
 * @since 1.0, Aug 23, 2010
 */
public interface ApiEngine {

	/**
	 * 通过动态接入方式调用API。
	 * 
	 * @param request API请求参数
	 * @return API响应结果
	 */
	public ApiResponse invokeApi(ApiRequest request);

}
