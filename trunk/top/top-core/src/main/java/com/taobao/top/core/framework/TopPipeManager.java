/**
 * 
 */
package com.taobao.top.core.framework;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.log.TopRequestLog;
import com.taobao.top.timwrapper.manager.TadgetManager;
import com.taobao.top.xbox.framework.IPipe;
import com.taobao.top.xbox.framework.IPipeContext;

/**
 * Top管道管理类（rest api）
 * @author fangweng
 *
 */
public class TopPipeManager extends AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>
{
	
	private static final Log logger = LogFactory.getLog(TopPipeManager.class);
	
	@Override
	public TopPipeInput inputInstance(HttpServletRequest request, HttpServletResponse response, TadgetManager tadgetManager) {
		return new TopPipeInput(request,response, tadgetManager);
	}
	
	@Override
	public TopPipeResult resultInstance() {
		return new TopPipeResult();
	}

	@Override
	public TopRequestLog getTopLog() {
		return new TopRequestLog();
	}

	@Override
	public TopPipeData<TopPipeInput, TopPipeResult> getPipeDataInstance(
			TopPipeInput pipeInput, TopPipeResult result, IPipeContext context) {
		return new TopPipeData<TopPipeInput,TopPipeResult>(context,pipeInput,result);	}

	@Override
	public boolean submitAsynTask(
			TopPipeData<TopPipeInput, TopPipeResult> data,
			IPipe<? super TopPipeInput, ? super TopPipeResult> pipe) {
		
		if (pipeConfig.isUseWeightThreadPool() && jobDispatcher != null)
		{
			logger.info("use weight threadpool to submit job.");
			
			jobDispatcher.submitJob
				(new TopPipeTask<TopPipeInput,TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>(data,pipe,this,false));
		}
		else
			_threadPool.submit
				(new TopPipeTask<TopPipeInput,TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>(data,pipe,this,false));
		
		return true;
	}
	
}
