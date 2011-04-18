package com.taobao.top.core.framework.pipe;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.core.Api;
import com.taobao.top.core.ApiApplicationParameter;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.core.framework.TopPipeUtil;
import com.taobao.util.CollectionUtil;

/**
 * 业务必选参数校验
 * @author zhenzi
 *
 */
public class ServiceMustParamCheckPipe extends TopPipe<TopPipeInput, TopPipeResult> {
	private static final Log logger = LogFactory.getLog(ServiceMustParamCheckPipe.class);
	
	@Override
	public void doPipe(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		
		Api api = pipeInput.getApi();
		ErrorCode errorCode = null;
		if (!CollectionUtil.isEmpty(api.getApplicationMustParams())) {
			for (ApiApplicationParameter param : api.getApplicationMustParams()) {
				errorCode = checkApplicationParam(pipeInput,
						param);
				if (null != errorCode) {
					pipeResult.setErrorCode(errorCode);
					pipeResult.setMsg(param.getName());
					return;
				}
			}
		}
		// 检查业务组合参数
		errorCode = api.checkCombine(pipeInput);
		if (null != errorCode) {
			pipeResult.setErrorCode(errorCode);
			return;
		}
	}

	@Override
	public boolean ignoreIt(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		if(pipeResult.getErrorCode() != null || TopPipeUtil.isIgnore(pipeInput)){
			return true;
		}
		return false;
	}

	/**
	 * Check application parameter.
	 * 
	 * @param api
	 * 
	 * @param apiInput
	 * @param param
	 * @return
	 */
	protected ErrorCode checkApplicationParam(TopPipeInput pipeInput,
			ApiApplicationParameter param) {
		ErrorCode errorCode = param.checkInput(pipeInput);
		return errorCode;
	}
}
