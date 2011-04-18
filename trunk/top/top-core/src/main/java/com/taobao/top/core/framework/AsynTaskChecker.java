/**
 * 
 */
package com.taobao.top.core.framework;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 用于检查异步任务执行状况的工作者
 * @author fangweng
 *
 */
public class AsynTaskChecker <I extends TopPipeInput, R extends TopPipeResult,
											D extends TopPipeData<I,R>> extends Thread
{
	private static final Log logger = LogFactory.getLog(AsynTaskChecker.class);
	private AtomicInteger _counter;
	private int _maxResultQueueLength;
	boolean flag = true;
	AbstractTopPipeManager<I,R,D> topPipeManager;
	ThreadPoolExecutor _threadPool;
	private LinkedBlockingQueue<D> _asynResultPool;//存储异步中间对象
	
	public AsynTaskChecker(AbstractTopPipeManager<I,R,D> topPipeManager
				,ThreadPoolExecutor threadPool,AtomicInteger counter,int maxResultQueueLength)
	{
		super("AsynTaskChecer_Thread");
		this.topPipeManager = topPipeManager;
		this._threadPool = threadPool;
		_asynResultPool = new LinkedBlockingQueue<D>();
		_counter = counter;
		_maxResultQueueLength = maxResultQueueLength;
	}
	
	
	@Override
	public void run()
	{
		while(flag)
		{
			D data = getAsynPipeResult();
			
			if (data != null)
			{
				if (data.getPipeResult().isDone())
					_threadPool.execute(new TopPipeTask<I,R,D>(data,null,topPipeManager,false));
				else
					addAsynPipeResult(data);
			}
		}
	}
	
	public void stopThread()
	{
		if (_asynResultPool != null)
			_asynResultPool.clear();
		
		flag = false;
		interrupt();
	}
	
	/**
	 * 存储异步请求的中间结果
	 * @param data
	 */
	protected void addAsynPipeResult(D data)
	{
		
		//超过可以支持的中间结果的数量，抛出异常，只是log
		if (_counter.getAndIncrement() > _maxResultQueueLength)
		{
			_counter.decrementAndGet();
			logger.error("PipeResultQueue full ...");
			throw new java.lang.RuntimeException("PipeResultQueue full ...");
		}
		else
		{
			_asynResultPool.add(data);
		}
	}
	
	protected D getAsynPipeResult()
	{
		D result = null;
		
		try 
		{
			result = _asynResultPool.poll(3, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) 
		{
			logger.error(e,e);
		}
		
		if (result != null)
			_counter.decrementAndGet();
		
		return result;
	}
	
	public void removeAsynPipeResult(D data)
	{
		boolean result = _asynResultPool.remove(data);
		
		if (result)
			_counter.decrementAndGet();
	}
}
