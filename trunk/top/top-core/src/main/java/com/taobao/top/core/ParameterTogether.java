/**
 * 
 */
package com.taobao.top.core;

import java.util.ArrayList;
import java.util.List;

import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.util.CollectionUtil;

/**
 * @author xalinx at gmail dot com
 * @date Dec 8, 2009
 */
public class ParameterTogether implements ParameterCombine{
	private List<ApiApplicationParameter> applicationParams;
	
	public List<ApiApplicationParameter> getApplicationParams() {
		return applicationParams;
	}
	/**
	 * @param param
	 */
	public void addApplicationParameter(ApiApplicationParameter param) {
		if (null == this.applicationParams) {
			this.applicationParams = new ArrayList<ApiApplicationParameter>();
		}
		this.applicationParams.add(param);		
	}
	//-----------------------------以下的功能是重构增加的方法，目的是不影响已有的代码，能让新旧处理逻辑切换
	public ErrorCode check(TopPipeInput pipeInput) {
		if(CollectionUtil.isEmpty(applicationParams)) {
			return null;
		}
		for(ApiApplicationParameter aap: applicationParams) {
			ErrorCode err = aap.checkInput(pipeInput);
			if(err != null) {
				return err;
			}
		}
		return null;
	}	
}
