package com.taobao.top.ats.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

/**
 * 
 * @author moling
 * @since 1.0, 2010-11-19
 */
public class RangeParseUtil {
	public static String getNextDate(String start, String end, Integer parsePeriod, String parseMeasure) {
		try {
			Date startDate = DateKit.ymdOrYmdhms2Date(start);
			Date endDate = DateKit.ymdOrYmdhms2Date(end);
			Date nextDate = null;
			
			//如果无法得到合法的时间片就不执行
			if (!startDate.before(endDate)) {
				return null;
			}
			
			if (ModelKeyConstants.MEASURE_HOUR.equals(parseMeasure)) {
				nextDate = DateUtils.addHours(startDate, parsePeriod);
			} else if (ModelKeyConstants.MEASURE_DAY.equals(parseMeasure)) {
				nextDate = DateUtils.addDays(startDate, parsePeriod);
			} else if (ModelKeyConstants.MEASURE_MONTH.equals(parseMeasure)) {
				nextDate = DateUtils.addMonths(startDate, parsePeriod);
			} else {
				//没有合法的时间单位就不进行分割
				return null;
			}
			
			if (endDate.before(nextDate)) {
				//如果下一个时间点大于最大时间点，取最大时间点
				return end;
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(nextDate);
		} catch (Exception e) {
			// 如果解析异常就返回为空
			return null;
		}
	}
	
	public static String getNextNumber(String start, String end, Integer parsePeriod) {
		try {
			Long startNum = Long.valueOf(start);
			Long endNum = Long.valueOf(end);
			
			//等于的时候需要查一次的
			if (startNum.longValue() > endNum.longValue()) {
				return null;
			}
			
			Long nextNum = startNum + parsePeriod;
			
			if (nextNum.longValue() > endNum.longValue()) {
				//最后一个parse的时候返回
				return String.valueOf(endNum + 1);
			}
			
			return nextNum.toString();
		} catch (Exception e) {
			// 如果解析异常就返回为空
			return null;
		}
	}
}
