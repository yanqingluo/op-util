package com.taobao.top.log;

import java.util.LinkedList;
import java.util.List;


/**
 * 日志记录结构定义 
 * @author wenchu
 *
 */
public class TopLog implements java.io.Serializable
{
	private static final long serialVersionUID = -1553696467252437619L;
	
	String className;
	Long logTime;
	
	/**
	 * 整个请求事务占用的时间，包括平台消耗时间和服务消耗时间
	 */
	private long transactionConsumeTime;
	
	/**
	 * 时间打点队列
	 */
	protected List<Long> timeStampQueue = new LinkedList<Long>();

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public Long getLogTime() {
		return logTime;
	}

	public void setLogTime(Long logTime) {
		this.logTime = logTime;
	}
	
	public List<Long> getTimeStampQueue() {
		return timeStampQueue;
	}
	
	public long getTransactionConsumeTime() {
		return transactionConsumeTime;
	}


	public void setTransactionConsumeTime(long transactionConsumeTime) {
		this.transactionConsumeTime = transactionConsumeTime;
	}

	public void track(Long timestamp)
	{
		timeStampQueue.add(timestamp);
	}
	
	public void track()
	{		
		timeStampQueue.add(System.currentTimeMillis());
	}

}
