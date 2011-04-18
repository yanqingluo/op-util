package com.taobao.top.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * taobao.com 2008 copyright
 */

/**
 * @version 2008-2-27
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class DefaultResultSet implements ResultSet, Serializable {
	private static final long serialVersionUID = -4681199751410108006L;

	private String tableName;

	private String msg;

	// private String[] columnNames;

	private List<Object> result;

	private int rowCursor = -1;

	
	private Exception exception;
	
	/** 单记录返回api	 */
	private boolean singleResult = false;
	
	/** 单记录返回api	 */
	public boolean isSingleResult() {
		return singleResult;
	}

	/** 单记录返回api	 */
	public void setSingleResult(boolean singleResult) {
		this.singleResult = singleResult;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}



	private Integer count;
	
	private Date modified;
	
	private Date time;
	
	/**
	 * 是否对调用成功的次数做限制,默认限制
	 */
	private boolean allowRecordSuccessTimes = true;




	/**
	 * 用户成功次数已经超出配额
	 */
	private boolean userSuccessTimesLimited = false;
	
	public boolean isAllowRecordSuccessTimes() {
		return allowRecordSuccessTimes;
	}

	public void setAllowRecordSuccessTimes(boolean allowSuccessTimes) {
		this.allowRecordSuccessTimes = allowSuccessTimes;
	}


	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public void setMsg(String retMsg) {
		this.msg = retMsg;
	}

	public String getMsg() {
		return msg;
	}

	public void addRow(Object row) {
		if (row == null) {
			return;
		}
		if (this.result == null) {
			this.result = new ArrayList<Object>();
		}
		this.result.add(row);
	}

	public void prepared() {
		rowCursor = -1;
	}


	public boolean next() {
		if (null == result) {
			return false;
		}
		if (rowCursor >= this.result.size() - 1) {
			return false;
		}
		rowCursor++;
		return true;
	}

	public Object getObject() {
		return this.result.get(rowCursor);
	}

	public List<Object> getResult() {
		return result;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public boolean isUserSuccessTimesLimited() {
		return this.userSuccessTimesLimited;
	}

	public void setUserSuccessTimesLimited(boolean userSuccessTimesLimited) {
		this.userSuccessTimesLimited = userSuccessTimesLimited;
	}
	
	

}
