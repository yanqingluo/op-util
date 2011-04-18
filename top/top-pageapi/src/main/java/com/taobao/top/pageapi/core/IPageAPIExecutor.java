package com.taobao.top.pageapi.core;

import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.pageapi.core.impl.TopPageAPIResult;
/**
 * 处理流程页面API 访问请求，转发给Trade Management
 * 
 * @version 2009-08-16
 * @author <a href="mailto:yueqian@taobao.com">zixue</a>
 * 
 */
public interface IPageAPIExecutor {
	public void execute(TopPipeInput pipeInput,TopPageAPIResult result);
}
