package com.taobao.top.log.asyn;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 一次输出的结果集对象
 * @author wenchu
 *
 */
public class RecordBundle <T>
{
	/**
	 * 输出结果集内的记录数
	 */
	private int count = 0;
	/**
	 * 最后需要输出的时间
	 */
	private Date flushTime;
	/**
	 * 结果集
	 */
	private List<T> records;
	
	public RecordBundle(int count,Date flushTime,List<T> records)
	{
		this.count = count;
		this.flushTime = flushTime;
		this.records =  new LinkedList<T>();
		
		this.records.addAll(records);
	}
	
	public RecordBundle(int flushInterval)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.SECOND, flushInterval);
		
		flushTime = calendar.getTime();
		records = new LinkedList<T>();
	}
	
	public int getCount()
	{
		return count;
	}
	public void setCount(int count)
	{
		this.count = count;
	}
	public Date getFlushTime()
	{
		return flushTime;
	}
	public void setFlushTime(Date flushTime)
	{
		this.flushTime = flushTime;
	}
	public List<T> getRecords()
	{
		return records;
	}
	public void setRecords(List<T> records)
	{
		this.records = records;
	}
	
	public void reset(int flushInterval)
	{
		count = 0;
		records.clear();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.SECOND, flushInterval);
		
		flushTime = calendar.getTime();
	}
	
	public void add(T node)
	{
		count = count + 1;
		records.add(node);
	}


	protected RecordBundle<T> clone() throws CloneNotSupportedException
	{
		RecordBundle<T> clone = new RecordBundle<T>(count,flushTime,records);

		return clone;
	}

	
}
