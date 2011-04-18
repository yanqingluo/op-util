package com.taobao.top.core.framework.pipe;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.core.framework.TopPipeUtil;
/**
 * 用于检查客户端是否传递了协议必填参数
 * @author zhenzi
 *
 */
public class ProtocolMustParamNullCheckPipe extends TopPipe<TopPipeInput, TopPipeResult>{
	private static final Log logger = LogFactory.getLog(ProtocolMustParamNullCheckPipe.class);
	
	@Override
	public void doPipe(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		//----------------------method is null
		if(StringUtils.isEmpty(pipeInput.getApiName())){
			pipeResult.setErrorCode(ErrorCode.MISSING_METHOD);
			return;
		}
		//----------------------appkey is null
		if(StringUtils.isEmpty(pipeInput.getAppKey())){
			pipeResult.setErrorCode(ErrorCode.MISSING_APP_KEY);
			return;
		}
		//---------------------timestamp is null
		if(StringUtils.isEmpty(pipeInput.getTimeStamp())){
			pipeResult.setErrorCode(ErrorCode.MISSING_TIMESTAMP);
			return;
		}
		//----------------------v is null
		if(StringUtils.isEmpty(pipeInput.getVersion())){
			pipeResult.setErrorCode(ErrorCode.MISSING_VERSION);
			return;
		}
		//----------------------sign is null
		if(StringUtils.isEmpty(pipeInput.getSign())){
			pipeResult.setErrorCode(ErrorCode.MISSING_SIGNATURE);
			return;
		}
	}
	@Override
	public boolean ignoreIt(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		boolean ignore = TopPipeUtil.isIgnore(pipeInput);
		return ignore;
	}
}
