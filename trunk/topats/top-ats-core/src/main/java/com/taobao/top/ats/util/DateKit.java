package com.taobao.top.ats.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 
 * @author moling
 * @since 1.0, 2010-8-23
 */
public class DateKit {
	/**
	 * Ymd or ymdhms2 date.
	 */
	public static Date ymdOrYmdhms2Date(String str) throws ParseException {
		if (str == null)
			return null;
		if (str.length() != 10 && str.length() != 19 && str.length() != 23) {
			throw new ParseException("error date:" + str, 0);
		}

		char[] strs = str.toCharArray();
		Calendar cal = null;
		try {
			int year = parseInt(strs, 0, 4);
			int month = parseInt(strs, 5, 7) - 1;
			int date = parseInt(strs, 8, 10);
			if (strs.length >= 19) {
				int hrs = parseInt(strs, 11, 13);
				int min = parseInt(strs, 14, 16);
				int sec = parseInt(strs, 17, 19);
				cal = new GregorianCalendar(year, month, date, hrs, min, sec);
				if (strs.length == 23) {
					int sss = parseInt(strs, 20, 23);
					cal.set(Calendar.MILLISECOND, sss);
				}
			} else {
				cal = new GregorianCalendar(year, month, date);
			}
			return cal.getTime();
		} catch (ParseException e) {
			throw e;
		}
	}

	/**
	 * 将字符数组的指定位转换为int
	 */
	private static int parseInt(char[] strs, int beginindex, int endindex) throws ParseException {
		int result = 0;
		int b = 1;
		for (int i = endindex - 1; i >= beginindex; i--) {
			if (strs[i] < 48 || strs[i] > 57) {
				throw new ParseException("Parse error,can't parse char to int . ", 0);
			}
			result = result + (strs[i] - 48) * b;
			b *= 10;
		}
		return result;
	}

}
