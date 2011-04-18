/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;

/**
 * api响应输出
 * 
 * @version 2008-2-29
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public interface ApiExporter {
	void export(HttpServletResponse response, TopPipeInput pipeInput, TopPipeResult apiResult, ErrorCodeTransform errorCodeTransform) throws IOException, TopException;
}
