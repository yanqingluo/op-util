package com.taobao.top.log.asyn;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.common.NamedThreadFactory;
import com.taobao.top.common.TOPConfig;





/**
 * 异步输出访问日志接口默认实现类
 * @author wenchu
 *
 */
public class AsynWriterTemplate <T> implements IAsynWriter<T>
{
	private static final Log Logger = LogFactory.getLog(AsynWriterTemplate.class);
	
	/**
	 * 日志队列
	 */
	private LinkedBlockingQueue<T> logQueue;
	
	/**
	 * 根据logQueue的数据来创建RecordBundle，一个线程对应到一个flushPool中的RecordBundle,
	 * 当数据满页或者在配置间隔时间已到的情况下,flushBundle中的数据到数据库中
	 */
	private ExecutorService createBundleService;
	
	private ConsumerSchedule<T>[] consumers;
	
	private TOPConfig config = TOPConfig.getInstance();
	
	

	public AsynWriterTemplate()
	{
		logQueue = new LinkedBlockingQueue<T>();
		
		if (Logger.isInfoEnabled())
			Logger.info(new StringBuffer("AsynWriterConfig :").append(config));
		
		
		// It's too early to start the, since, for now, the
		// topLogDao hasn't gotten chance to be initialized.
		// let Spring to call the start().
		//start();
	}
	
	
	@SuppressWarnings("unchecked")
	public void start()
	{
		createBundleService = 
			new ThreadPoolExecutor(config.getCreateBundleServiceThreadCount()
					, config.getCreateBundleServiceThreadCount(), 
					0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
					new NamedThreadFactory("AsynWriter"),
					new ThreadPoolExecutor.AbortPolicy());
		
		consumers = 
			new ConsumerSchedule[config.getCreateBundleServiceThreadCount()];
		

		for(int i = 0 ; i < config.getCreateBundleServiceThreadCount(); i++)
		{
			consumers[i] = new ConsumerSchedule<T>();
			consumers[i].setLogQueue(logQueue);
			consumers[i].setName(new StringBuffer().
					append("consumerSchedule").append(i).toString());
			
			createBundleService.submit(consumers[i]);
		}
	}
	
	public void stop()
	{
		try
		{
			for(ConsumerSchedule<T> consumer: consumers)
			{
				consumer.stop();
			}	
			
			Thread.sleep(3000);
		} catch (InterruptedException e){}
		
		if (createBundleService != null)
			createBundleService.shutdown();
		
		createBundleService = null;
	}
	
	public void restart()
	{
		stop();
		start();
	}
	
	
	public void write(T content)
	{
		if (logQueue == null)
			logQueue = new LinkedBlockingQueue<T>();
		
		logQueue.add(content);
	}


	public LinkedBlockingQueue<T> getLogQueue()
	{
		return logQueue;
	}


	public ConsumerSchedule<T>[] getConsumers()
	{
		return consumers;
	}

	

}
