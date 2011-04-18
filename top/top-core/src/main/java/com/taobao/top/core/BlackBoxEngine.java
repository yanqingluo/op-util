/**
 * 
 */
package com.taobao.top.core;

import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;


/**
 * 黑箱操作api，返回最终文本结果
 * 
 * @version 2009-2-9
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public interface BlackBoxEngine {
	void execute(TopPipeResult pipeResult, TopPipeInput pipeInput, Api api) throws Exception;
}
