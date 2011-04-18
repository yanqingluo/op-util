package com.taobao.top.pageapi.framework;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.taobao.top.core.framework.AbstractTopPipeManager;
import com.taobao.top.core.framework.TopPipeData;
import com.taobao.top.core.framework.TopPipeTask;
import com.taobao.top.log.TopLog;
import com.taobao.top.log.TopPageRequestLog;
import com.taobao.top.log.TopRequestLog;
import com.taobao.top.pageapi.core.impl.TopPageAPIResult;
import com.taobao.top.timwrapper.manager.TadgetManager;
import com.taobao.top.xbox.framework.IPipe;
import com.taobao.top.xbox.framework.IPipeContext;

/**
 * 页面流程化的 pipe管理类（page api）
 * @author zhenzi 
 *
 */
public class TopPagePipeManager extends AbstractTopPipeManager
		<TopPagePipeInput,TopPageAPIResult,TopPipeData<TopPagePipeInput,TopPageAPIResult>> {
	
	@Override
	public TopLog getTopLog() {
		return new TopPageRequestLog();
	}


	@Override
	public TopPagePipeInput inputInstance(HttpServletRequest request,
			HttpServletResponse response, TadgetManager tadgetManager) {
		return new TopPagePipeInput(request,response,tadgetManager);
	}

	@Override
	public TopPageAPIResult resultInstance() {
		return new TopPageAPIResult();
	}


	@Override
	public TopPipeData<TopPagePipeInput, TopPageAPIResult> getPipeDataInstance(
			TopPagePipeInput pipeInput, TopPageAPIResult result,
			IPipeContext context) {
		// TODO Auto-generated method stub
		return new TopPipeData<TopPagePipeInput, TopPageAPIResult>(context,pipeInput,result);
	}


	@Override
	public boolean submitAsynTask(
			TopPipeData<TopPagePipeInput, TopPageAPIResult> data,
			IPipe<? super TopPagePipeInput, ? super TopPageAPIResult> pipe) {
		_threadPool.submit
			(new TopPipeTask<TopPagePipeInput, TopPageAPIResult
					,TopPipeData<TopPagePipeInput,TopPageAPIResult>>(data,pipe,this,false));
		return true;
	}

}
