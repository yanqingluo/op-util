package com.taobao.top.notify.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.junit.Test;

/**
 * 日期工具测试类。
 * 
 * @author fengsheng
 * @since 1.0, Dec 16, 2009
 */
public class DateUtilsTest {

	@Test
	public void getNextDayStart() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		System.out.println(format.format(DateUtils.getNextDayStart()));
	}

	@Test
	public void getPrev7DaysEnd() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		System.out.println(format.format(DateUtils.getPrev7DaysEnd()));
	}

}
