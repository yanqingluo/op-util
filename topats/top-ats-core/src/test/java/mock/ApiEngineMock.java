package mock;

import com.taobao.top.ats.engine.ApiEngine;
import com.taobao.top.ats.engine.ApiRequest;
import com.taobao.top.ats.engine.ApiResponse;

public class ApiEngineMock implements ApiEngine {

	public ApiResponse invokeApi(ApiRequest request) {
		ApiResponse rsp = new ApiResponse();
		if(TaskDaoMock.RESPONSE_IGNORE_ERROR.equalsIgnoreCase(request.getInterfaceVersion())){
			rsp.setErrCode(TaskDaoMock.RESPONSE_IGNORE_ERROR);
		}else if(TaskDaoMock.RESPONSE_REPEAT_ERROR.equalsIgnoreCase(request.getInterfaceVersion())){
			rsp.setErrCode(TaskDaoMock.RESPONSE_REPEAT_ERROR);
		}else if(TaskDaoMock.RESPONSE_UNKNOWN_ERROR.equalsIgnoreCase(request.getInterfaceVersion())){
			rsp.setErrCode(TaskDaoMock.RESPONSE_UNKNOWN_ERROR);
		}
		rsp.setResponse("Response_Jerry");
		return rsp;
	}

}
