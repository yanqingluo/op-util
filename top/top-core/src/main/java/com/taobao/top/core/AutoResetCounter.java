package com.taobao.top.core;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 一个会周期性自动清0的计数器
 * 
 * @author zhudi
 * 
 */

public class AutoResetCounter {
	/**
	 * 周期
	 */
	private long periods;
	/**
	 * 基准值
	 */
	private long benchmark;
	/**
	 * 计数器
	 */
	private AtomicInteger counter = new AtomicInteger(0);
	

	/**
	 * 初始化对象
	 * @param periods
	 * @param benchmark
	 */
	public AutoResetCounter(long periods, long benchmark) {
		this.periods = periods;
		this.benchmark = benchmark;
	}
	
	/**
	 * 初始化对象,默认基准值为当前时间加一个周期
	 * 
	 * @param periods
	 */
	public AutoResetCounter(long periods) {
		
		this.periods = periods;
		this.benchmark = System.currentTimeMillis()+periods;
		
	}

	


	public long getPeriods() {
		return periods;
	}

	public void setPeriods(long periods) {
		this.periods = periods;
	}

	/**
	 * 首先检查是否需要reset，然后获取计数器的自增之后的值
	 * 
	 * @return
	 */
	public int incrementAndGet() {
		checkReset();
		return counter.incrementAndGet();
	}

	/**
	 * 检查是否需要reset 如果当前时间大于基准值了，则说明需要重置
	 */
	private void checkReset() {
		long time = System.currentTimeMillis();
		if (time > benchmark) {
			synchronized (this) {
				while (time > benchmark) {
					benchmark = benchmark + periods;
				}
				counter = new AtomicInteger(0);
			}
		}
	}

}
