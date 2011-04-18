package mock;

import java.util.Date;
import java.util.Map;

import com.taobao.top.ats.AtsException;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.ResultSet;
import com.taobao.top.ats.task.api.ApiManager;

public class ApiManagerMock implements ApiManager {

	public String getErrorResponse(String errorCode, String errorMsg,
			Map<String, String> subTaskRequest) {
		// TODO Auto-generated method stub
		return "Response_Unknown_Error";
	}

	public String getIngoreErrorResponse(String taskName,
			Map<String, String> subTaskRequest) throws AtsException {
		// TODO Auto-generated method stub
		return "Response_Ingore_Error";
	}

	public String getRetrunRequest(String taskName,
			Map<String, String> subTaskRequest) throws AtsException {
		return "Request_" + taskName;
	}

	public AtsTaskDO getTask(Map<String, String> request) throws AtsException {
		AtsTaskDO atsTask = new AtsTaskDO();
		atsTask.setApiName("Jerry");
		return atsTask;
	}

	public void init() throws Exception {
		// TODO Auto-generated method stub

	}

	public boolean isIgnoreError(String taskName, String errorCode)
			throws AtsException {
		return TaskDaoMock.RESPONSE_IGNORE_ERROR.equalsIgnoreCase(errorCode);
	}

	public ResultSet isPreCheckOK(Map<String, String> request) {
		return new ResultSet();
	}

	public ResultSet isRequestRight(Map<String, String> request) {
		return new ResultSet();
	}

	public boolean isRetryError(String taskName, String errorCode)
			throws AtsException {
		return TaskDaoMock.RESPONSE_REPEAT_ERROR.equalsIgnoreCase(errorCode);
	}

	public Map<String, String> getPageDownRequest(String taskName,
			Map<String, String> subTaskRequest, Long totalResult)
			throws AtsException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isApiIntime(String taskName, Date date) {
		return true;
	}

	public boolean isBigResult(String taskName) throws AtsException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isBigResult(Map<String, String> attributes) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPageNeeded(String taskName) throws AtsException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean refreshApiModels() {
		// TODO Auto-generated method stub
		return false;
	}


}
