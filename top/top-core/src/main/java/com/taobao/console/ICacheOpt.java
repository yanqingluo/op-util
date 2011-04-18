package com.taobao.console;

import com.taobao.top.console.client.handler.ConsoleClientPipeInput;
import com.taobao.top.console.client.handler.ConsoleClientPipeResult;

/**
 * 缓存操作的接口
 * @author zhenzi
 * 2010-12-24 下午12:13:40
 */
public interface ICacheOpt {
	public void cacheOpt(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result);
}
