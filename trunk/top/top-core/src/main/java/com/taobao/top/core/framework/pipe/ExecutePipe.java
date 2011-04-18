package com.taobao.top.core.framework.pipe;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.core.ApiExecutor;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;

public class ExecutePipe extends TopPipe<TopPipeInput, TopPipeResult> {
	private static final Log logger = LogFactory.getLog(ExecutePipe.class);
	
	private ApiExecutor apiExecutor = null;
	
	private String asynExcludeAPIName; 
	
	public String getAsynExcludeAPIName() {
		return asynExcludeAPIName;
	}

	public void setAsynExcludeAPIName(String asynExcludeAPIName) {
		this.asynExcludeAPIName = asynExcludeAPIName;
	}

	public void setApiExecutor(ApiExecutor apiExecutor) {
		this.apiExecutor = apiExecutor;
	}
	
	@Override
	public void doPipe(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		
		apiExecutor.execute(pipeInput, pipeResult);
	}

	@Override
	public boolean ignoreIt(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		if(pipeResult.getErrorCode() != null){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean ignoreAsynMode(TopPipeInput pipeInput, TopPipeResult pipeResult) {

		if (asynExcludeAPIName != null && !"".equals(asynExcludeAPIName))
		{
			if (asynExcludeAPIName.indexOf(pipeInput.getApiName()) >= 0)
				return true;
		}
		
		return false;
	}
}
