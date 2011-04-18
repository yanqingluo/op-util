package com.taobao.top.log.asyn;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 输出日志结果集任务定义
 * @author wenchu.cenwc
 *
 */
public class WriteSchedule <T> implements ISchedule<T>
{
	private static final Log Logger = LogFactory.getLog(WriteSchedule.class);
	
	RecordBundle<T> bundle;
	
	
	public void run()
	{
		Logger.info("WriteSchedule is called!");
		write(bundle);
	}
	
	public void write(RecordBundle<T> bundle)
	{
		if (bundle != null)
		{
			if (bundle.getCount() > 0 || 
					(bundle.getRecords() != null && bundle.getRecords().size() > 0))
			{
				StringBuilder content = new StringBuilder();
				
				List<T> records = bundle.getRecords();
				
				int count = records.size();
				
				for(int i =0 ; i < count ; i++)
				{
					T node = records.get(i);
					
					if (i == count -1)
						content.append(node);
					else
						content.append(node).append("\r\n");
				}
				
				Logger.fatal(content.toString());
				
			}
		}
	}

	public RecordBundle<T> getBundle()
	{
		return bundle;
	}

	public void setBundle(RecordBundle<T> bundle)
	{
		this.bundle = bundle;
	}


	
}
