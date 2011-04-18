package com.taobao.console;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.console.client.handler.ConsoleClientPipeInput;
import com.taobao.top.console.client.handler.ConsoleClientPipeResult;
import com.taobao.top.console.client.util.ConsoleClientErrorCode;

/**
 * cacheOpt的抽象实现类
 * @author zhenzi
 * 2010-12-24 下午12:32:04
 */
public abstract class AbstractCacheOptImpl implements ICacheOpt{
	protected static final Log logger = LogFactory.getLog(AbstractCacheOptImpl.class);
	@Override
	public void cacheOpt(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		String cmd = input.getString(ConsoleConstants.CMD);
		if(ConsoleConstants.cmd_delete.equals(cmd)){
			deleteCache(input, result);
		}else if(ConsoleConstants.cmd_select.equals(cmd)){
			getCache(input, result);
		}else if(ConsoleConstants.cmd_update.equals(cmd)){
			updateCache(input,result);
		}else{
			result.setErrorCode(ConsoleClientErrorCode.INVALID_ARGUMENTS);
			result.setErr_msg("command is must select,delete,update");
			return;
		}
	}
	public abstract void deleteCache(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result);
	public abstract void getCache(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result);
	public abstract void updateCache(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result);
}
