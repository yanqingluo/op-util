/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;


/**
 * @version 2008-2-29
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public interface ApiExecutor {
	void execute(TopPipeInput pipeInput, TopPipeResult pipeResult);
}
