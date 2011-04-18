package com.taobao.top.notify.loadtest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.junit.Test;

import com.taobao.top.notify.NotifyException;
import com.taobao.top.notify.TestBase;

public class SubscribeDaoLoadTest extends TestBase {

	private static ExecutorService executor;

	@Test
	public void test() throws NotifyException {
		execute(1, 1, null);
	}

	private TestResult execute(int thread, final int times, final Runnable task) {
		List<Future<?>> futures = new ArrayList<Future<?>>();
		long begin = System.currentTimeMillis();

		for (int i = 0; i < times; i++) {
			Future<?> future = executor.submit(task);
			futures.add(future);
		}

		while (true) {
			boolean allDone = true;
			x: for (Future<?> future : futures) {
				if (!future.isDone()) {
					allDone = false;
					break x;
				}
			}
			if (allDone) {
				break;
			} else {
				sleep(500);
			}
		}

		long end = System.currentTimeMillis();
		long elapsed = end - begin;
		int speed = (int) ((float) times / ((float) elapsed / 1000F));

		return new TestResult(thread, times, elapsed, speed);
	}
	protected String layout(int size, String value) {
		StringBuilder layout = new StringBuilder();
		int remain = size - value.length();
		for (int i = 0; i < remain; i++) {
			layout.append(" ");
		}
		layout.append(value).append("|");
		return layout.toString();
	}

}
