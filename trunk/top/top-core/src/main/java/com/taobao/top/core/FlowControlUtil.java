package com.taobao.top.core;

import java.util.Calendar;

public final class FlowControlUtil {
	private FlowControlUtil(){
		
	}
	/**
	 * 生成key
	 * @param key
	 * @return
	 */
	public static String createKey(String key){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		long time = calendar.getTimeInMillis();
		return new StringBuilder(key).append(":").append(time).toString();
	}
}
