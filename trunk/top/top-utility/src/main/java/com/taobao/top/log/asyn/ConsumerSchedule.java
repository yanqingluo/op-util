package com.taobao.top.log.asyn;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.common.TOPConfig;


/**
 * 
 * 读取访问日志，累计到输出页面大小
 * 或者到达输出间隔时间创建输出线程输出日志
 * @author wenchu
 *
 * @param <T>
 */
public class ConsumerSchedule <T> implements ISchedule<T>{

private static final Log Logger = LogFactory.getLog(ConsumerSchedule.class);
	
	/**
	 * 日志队列
	 */
	private LinkedBlockingQueue<T> logQueue;
	
	private RecordBundle<T> bundle;
	
	private String name;

	private TOPConfig config = TOPConfig.getInstance();
	
	/**
	 * 输出操作线程工作池
	 */
	private ExecutorService writeBundleService;
	
	private boolean activeFlag;
	
	public ConsumerSchedule() {
		
	}
	
	
	/**
	 * 内部检查
	 * @return
	 */
	public boolean checkInnerElement()
	{
		if (logQueue == null  
				|| name == null || config == null)
			return false;
		else
			return true;
	}
	
	public void run()
	{
		if (!checkInnerElement())
		{
			throw new java.lang.RuntimeException("ConsumerSchedule start Error!,please check innerElement");
		}
		
		bundle = new RecordBundle<T>(config.getFlushInterval());
		writeBundleService = Executors.newFixedThreadPool(config.getWriterMaxCount());
		activeFlag = true;
		
		try
		{
			while(activeFlag)
			{
				//到了输出间隔时间
				if (bundle.getCount() > 0 && bundle.getFlushTime().before(new Date()))
				{
					flush();
				}
				
				//缓存中的记录数到达
				if (bundle.getCount() >= config.getBundleMaxCount())
				{
					flush();
				}
				
				
				T node = logQueue.poll(100,TimeUnit.MICROSECONDS);
				

				if (node != null)
				{
					bundle.add(node);
				}
			}
		}
		catch (Exception e)
		{
			if (e instanceof java.lang.InterruptedException)
			{
				Logger.error("ConsumerSchedule is stop!");
			}
			else
				Logger.error("ConsumerSchedule error!",e);
		}
		finally
		{
			if (writeBundleService != null)
				writeBundleService.shutdown();
			
			writeBundleService = null;
		}
	}
	
	public void flush() throws CloneNotSupportedException, 
					InterruptedException, ExecutionException, TimeoutException
	{
		WriteSchedule<T> writeSchedule = new WriteSchedule<T>();
		
		writeSchedule.setBundle(bundle.clone());
		writeBundleService.submit(writeSchedule);
		
		bundle.reset(config.getFlushInterval());
	}
	
	public void stop()
	{
		try
		{
			flush();
		}
		catch(Exception ex)
		{
			Logger.error("ConsumerSchedule stop error!",ex);
		}
		finally
		{
			activeFlag = false;
			
			if (writeBundleService != null)
				writeBundleService.shutdown();
			
			writeBundleService = null;
		}
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		
		result.append("consumer name :").append(name);
		
		if (bundle != null)
			result.append(",bundle data count: ").append(bundle.getCount())
				.append(",flushTime:").append(bundle.getFlushTime());
		
		return result.toString();
	}

	public LinkedBlockingQueue<T> getLogQueue()
	{
		return logQueue;
	}

	public void setLogQueue(LinkedBlockingQueue<T> logQueue)
	{
		this.logQueue = logQueue;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ExecutorService getWriteBundleService()
	{
		return writeBundleService;
	}

}
