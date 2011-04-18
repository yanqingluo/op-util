package com.taobao.top.notify.loadtest;

public class TestResult {

	private int thread;
	private int times;
	private long elapsed;
	private int speed;

	public TestResult(int thread, int times, long elapsed, int speed) {
		this.thread = thread;
		this.times = times;
		this.elapsed = elapsed;
		this.speed = speed;
	}

	public int getThread() {
		return this.thread;
	}

	public int getTimes() {
		return this.times;
	}

	public long getElapsed() {
		return this.elapsed;
	}

	public int getSpeed() {
		return this.speed;
	}

}
