package com.taobao.top.core.framework.pipe;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.common.lang.StringUtil;
import com.taobao.top.core.Api;
import com.taobao.top.core.ApiApplicationParameter;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.ApiApplicationParameter.Type;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.core.framework.TopPipeUtil;
import com.taobao.util.CollectionUtil;

/**
 * 业务可选参数校验
 * @author zhenzi
 *
 */
public class ServiceOptParamCheckPipe extends TopPipe<TopPipeInput, TopPipeResult> {
	private static final Log logger = LogFactory.getLog(ServiceOptParamCheckPipe.class);
	
	@Override
	public void doPipe(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		
		Api api = pipeInput.getApi();
		com.taobao.top.core.framework.FileItem fileItem = pipeInput.getFileData();
		ErrorCode errorCode = null;
		
		byte[] fileData = null;
		if(fileItem != null){
			fileData = fileItem.getBout().toByteArray();
		}
		// 对应用可选参数进行defaultValue的保证。
		// Yes, it's ugly to do the casting; however, we need to
		// do the 'write' operation.
		if (!CollectionUtil.isEmpty(api.getApplicationOptionalParams())) {
			for (ApiApplicationParameter param : api
					.getApplicationOptionalParams()) {
				ensureApplicationOptionalParams(pipeInput, param);
				// Check optional parameter if 'validate' property is set as
				// true.
				if (param.isValidate()) {
					String value = pipeInput.getString(param.getName(), true);

					// Very dangerous code. Should be reviewed 
					// by Fengshen, Cilai, and Haishi, if following code would be modified.
					if (StringUtil.isNotEmpty(value)
							|| (param.getType() == Type.BYTE_ARRAY && fileData != null)) {
						errorCode = checkApplicationParam(pipeInput, param);
						if (null != errorCode) {
							pipeResult.setErrorCode(errorCode);
							pipeResult.setMsg(param.getName());
							return;
						}
					} else {
						if (value != null && param.getType() == Type.NUMBER) {
							errorCode = ErrorCode.INVALID_ARGUMENTS;
							pipeResult.setErrorCode(errorCode);
							pipeResult.setMsg(param.getName());
							return;
						}
					}
				}
			}
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
	 * Ensure the optional parameter value if no vlaue find in input AND param
	 * has default value.
	 * 
	 * @param apiInput
	 * @param param
	 */
	private void ensureApplicationOptionalParams(TopPipeInput pipeInput,
			ApiApplicationParameter param) {
		// param is optional parameter.
		String name = param.getName();
		Object defaultValue = param.getDefaultValue();

		if (defaultValue != null) {
			// check the default value first.
			// If the default value is not set, we'll pass
			// the value getting.
			String value = pipeInput.getString(name, true);
			if (StringUtils.isEmpty(value)) {
				pipeInput.put(name, defaultValue);
			}
		}
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
