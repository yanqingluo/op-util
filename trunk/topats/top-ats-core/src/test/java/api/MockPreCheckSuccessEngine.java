package api;

import com.taobao.top.ats.engine.ApiEngine;
import com.taobao.top.ats.engine.ApiRequest;
import com.taobao.top.ats.engine.ApiResponse;

/**
 * 
 * @author moling
 * @since 1.0, 2010-8-25
 */
public class MockPreCheckSuccessEngine implements ApiEngine {

	public ApiResponse invokeApi(ApiRequest request) {
		ApiResponse response = new ApiResponse();
		response.setResponse("{\"trade\":{\"tid\":12345,\"modified\":\"2010-8-30 10:10:10\"}}");
		return response;
	}

}
