package com.taobao.top.core;

import java.util.Date;
import java.util.List;

/**
 * taobao.com 2008 copyright
 */

/**
 * Tbql查询结果集
 * 
 * @version 2008-2-27
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public interface ResultSet {
	boolean isAllowRecordSuccessTimes();

	void setAllowRecordSuccessTimes(boolean allowSuccessTimes);

	String getTableName();



	boolean next();

	Object getObject();





	void addRow(Object row);

	void setMsg(String retMsg);

	String getMsg();
	
	/**是否单条记录	 */
	boolean isSingleResult();

	/**设置单条记录  */
	void setSingleResult(boolean isList);

	List<Object> getResult();

	Integer getCount();

	void setCount(Integer count);

	Date getModified();

	void setModified(Date modified);

	Date getTime();

	void setTime(Date time);

	boolean isUserSuccessTimesLimited();

	void setUserSuccessTimesLimited(boolean userSuccessTimesLimited);


	public Exception getException();

	public void setException(Exception exception);
}
