package com.taobao.top.core.framework;


import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.xbox.framework.IPipe;
import com.taobao.top.xbox.framework.PipeContextManager;
import com.taobao.top.xbox.threadpool.Job;

/**
 * 异步执行管道的任务封装
 * @author fangweng
 *
 */
public class TopPipeTask<I extends TopPipeInput, R extends TopPipeResult,D extends TopPipeData<I,R>> 
			implements Runnable,Job{

	private static final Log logger = LogFactory.getLog(TopPipeTask.class);
			
	D data;
	IPipe<? super I, ? super R> pipe = null;
	AbstractTopPipeManager<I,R,D> topPipeManager = null;
	boolean isAsyn = false;//是否是纯异步方式，还是同步等待服务返回
	
	public TopPipeTask(D topData,IPipe<? super I, ? super R> pipe
			,AbstractTopPipeManager<I,R,D> topPipeManager,boolean isAsyn)
	{
		data = topData;
		this.pipe = pipe;
		this.topPipeManager = topPipeManager;
		this.isAsyn = isAsyn;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run()
	{
		try
		{
			if (data.getPipeContext().getAttachment(LazyParser.LAZY_CONTEXT) != null)
			{
				LazyParser.addParams2LazyContext(
						(Map<String,Object>)data.getPipeContext().getAttachment(LazyParser.LAZY_CONTEXT));
				
				data.getPipeContext().removeAttachment(LazyParser.LAZY_CONTEXT);
			}
			
			PipeContextManager.setContext(data.getPipeContext());
			
			//可能是同步也可能是异步，如果是异步需要在执行完毕后设置
			//data.getPipeResult().setStatus(IPipeResult.STATUS_DONE);
			if (pipe != null)
				pipe.doPipe(data.getPipeInput(), data.getPipeResult());
					
			if (isAsyn)//要注意，只有进入dopipes才会释放threadlocal的内容，因此这里可能有危险
				topPipeManager.addAsynPipeResult(data);
			else
				topPipeManager.doPipes(data);
		}
		catch(Exception ex)
		{
			logger.error("TopPipeTask error!",ex);
		}
		
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return data.getPipeInput().getApiName();
	}

}
