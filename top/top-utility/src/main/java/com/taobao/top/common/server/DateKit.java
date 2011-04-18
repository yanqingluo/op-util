/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.common.server;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateKit {

	public static Date ymdOrYmdhms2Date(String str) throws ParseException {
		if (str == null)
			return null;

		Date date = null;
		if (str.length() == 10) {
			DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
			date = f.parse(str);
		} else if (str.length() == 19) {
			DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = f.parse(str);
		} else if (str.length() == 23) {
			DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			date = f.parse(str);
		} else {
			throw new ParseException("error date foramt: " + str, 0);
		}
		return date;
	}

	public static String date2ymdhms(Date date) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return f.format(date);
	}

}
