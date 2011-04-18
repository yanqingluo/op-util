package api;

import com.taobao.top.ats.engine.ApiEngine;
import com.taobao.top.ats.engine.ApiRequest;
import com.taobao.top.ats.engine.ApiResponse;
import com.taobao.top.ats.task.api.ApiManagerImpl;

/**
 * 
 * @author moling
 * @since 1.0, 2010-8-25
 */
public class MockSuccessEngine implements ApiEngine {

	public ApiResponse invokeApi(ApiRequest request) {
		ApiResponse response = new ApiResponse();
		response.setResponse(ApiManagerImpl.PRECHECK_SUCCESS_JSON);
		return response;
	}

}
