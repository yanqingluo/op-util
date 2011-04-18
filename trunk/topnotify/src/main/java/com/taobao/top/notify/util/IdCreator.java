package com.taobao.top.notify.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdCreator {

	private static AtomicLong seed = new AtomicLong();

	public static Long createId(long ip) {
		long time = System.currentTimeMillis();
		// 一周以内的数据变化只会反馈到后32位，这里做随机数只取后33位
		time = time << (30 + 1);
		time = time >>> 1;// 避免首位为1

		long random = seed.incrementAndGet() & ((1 << 20) - 1);
		return time | ip | random;
	}

}
