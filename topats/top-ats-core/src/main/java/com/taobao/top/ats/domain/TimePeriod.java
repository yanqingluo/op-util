package com.taobao.top.ats.domain;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.taobao.top.ats.util.DateKit;

/**
 * 
 * @author moling
 * @since 1.0, 2010-11-18
 */
public class TimePeriod {
	//默认所有日期的起始时间为2010-11-18,纪念此段代码的诞生
	public static final String ymd = "2010-11-18 ";
	
	//起始区间
	private Date startDate;
	//结束区间
	private Date endDate;
	
	public Date getStartDate() {
		return this.startDate;
	}
	
	//入参为"12:10:10"小时、分、秒格式的字符串，其他格式解析会有问题
	public void setStartDate(String hms) throws ParseException {
		this.startDate = DateKit.ymdOrYmdhms2Date(ymd + hms);
	}
	
	public Date getEndDate() {
		return this.endDate;
	}
	
	//入参为"12:10:10"小时、分、秒格式的字符串，其他格式解析会有问题
	public void setEndDate(String hms) throws ParseException {
		this.endDate = DateKit.ymdOrYmdhms2Date(ymd + hms);
	}
	
	public boolean isInTimePeriod(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.YEAR, 2010);
		c.set(Calendar.MONTH, 10);
		c.set(Calendar.DAY_OF_MONTH, 18);
		Date compareTime = c.getTime();
		
		if (startDate.before(compareTime) && endDate.after(compareTime)) {
			return true;
		}
		
		return false;
	}
}
