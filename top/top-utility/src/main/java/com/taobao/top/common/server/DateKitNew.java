/**
 * 
 */
package com.taobao.top.common.server;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author xalinx at gmail dot com
 * @date Jun 7, 2010
 */
public class DateKitNew {
	/**
	 * @param timestamp
	 * @param expectDuration
	 * @return true if invalid, else fasle
	 */
	public static boolean isTimestampInvalid(String timestamp, Date expect, long expectDuration) {
		if (timestamp == null) {
			return false;
		}
		Date actual = null;
		try {
			actual = ymdOrYmdhms2Date(timestamp);
		} catch (ParseException e) {
			return false;
		}
		if (actual == null) {
			return false;
		}
		long duration = actual.getTime() - expect.getTime();
		if (duration > expectDuration
				|| duration < -expectDuration) {
			return false;
		}
		return true;
	}

	/**
	 * Ymd or ymdhms2 date.
	 * 
	 * @param str
	 *            the str
	 * 
	 * @return the date
	 * 
	 * @throws ParseException
	 *             the parse exception
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
	 * 
	 * @param strs
	 * @param beginindex
	 * @param endindex
	 * @return
	 * @throws Exception
	 */
	private static int parseInt(char[] strs, int beginindex, int endindex)
			throws ParseException {
		int result = 0;
		int b = 1;
		for (int i = endindex - 1; i >= beginindex; i--) {
			if (strs[i] < 48 || strs[i] > 57) {
				throw new ParseException(
						"Parse error,can't parse char to int . ", 0);
			}
			result = result + (strs[i] - 48) * b;
			b *= 10;
		}
		return result;
	}

	public static String date2ymdhms(Date date) {
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		int y = c.get(GregorianCalendar.YEAR);
		int m = c.get(GregorianCalendar.MONTH) + 1;
		int d = c.get(GregorianCalendar.DAY_OF_MONTH);
		int aop = c.get(GregorianCalendar.AM_PM);
		int h = c.get(GregorianCalendar.HOUR);
		h = aop == GregorianCalendar.PM ? h + 12 : h;
		int minute = c.get(GregorianCalendar.MINUTE);
		int s = c.get(GregorianCalendar.SECOND);

		char[] str = new char[19];
		str[4] = str[7] = '-';
		str[0] = (char) (y / 1000 + '0');
		str[1] = (char) (y / 100 % 10 + '0');
		str[2] = (char) (y / 10 % 10 + '0');
		str[3] = (char) (y % 10 + '0');

		str[5] = (char) (m / 10 + '0');
		str[6] = (char) (m % 10 + '0');
		str[8] = (char) (d / 10 + '0');
		str[9] = (char) (d % 10 + '0');

		str[10] = ' ';

		str[13] = str[16] = ':';
		str[11] = (char) (h / 10 + '0');
		str[12] = (char) (h % 10 + '0');
		str[14] = (char) (minute / 10 + '0');
		str[15] = (char) (minute % 10 + '0');
		str[17] = (char) (s / 10 + '0');
		str[18] = (char) (s % 10 + '0');

		return new String(str);
	}

}
