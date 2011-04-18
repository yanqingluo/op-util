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
public class ParameterChoiser implements ParameterCombine {
	private List<ParameterTogether> children;
	/**
	 * @param together
	 */
	public void addParameterTogether(ParameterTogether together) {
		if (this.children == null) {
			this.children = new ArrayList<ParameterTogether>();
		}
		this.children.add(together);
	}
	public ErrorCode check(TopPipeInput pipeInput) {
		if (CollectionUtil.isEmpty(children)) {
			return null;
		}
		int i = 0;
		for (ParameterTogether pt : children) {
			ApiApplicationParameter firstCheck = pt.getApplicationParams().get(0);
			String name = firstCheck.getName();
			String expected = firstCheck.getExpected();
			if (pipeInput.getString(name, true) != null) {
				if(expected != null && !pipeInput.getString(name, true).equals(expected)) {
					continue;
				}
				//mach first parameter
				ErrorCode err = pt.check(pipeInput);
				if (err != null) {
					return err;
				} else {
					// muched, return success
					return null;
				}
			}
			i++;
		}
		return ErrorCode.MISSING_REQUIRED_ARGUMENTS;
	}
}
