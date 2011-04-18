package com.taobao.top.notify.loadtest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

import com.taobao.top.notify.NotifyException;
import com.taobao.top.notify.TestBase;
import com.taobao.top.notify.domain.NotifyDO;
import com.taobao.top.notify.domain.NotifyEnum;
import com.taobao.top.notify.domain.query.NotifyQuery;
import com.taobao.top.notify.persist.NotifyDao;

/**
 * 消息数据访问对象压力测试。
 * 
 * @author fengsheng
 * @since 1.0, Dec 16, 2009
 */
public class NotifyDaoLoadTest extends TestBase {

	private NotifyDao notifyDao = (NotifyDao) ctx.getBean("notifyDao");
	private static ExecutorService executor;

	@Test
	public void test() throws NotifyException {
		addNotify();
		addIsvNotify();
	}

	@Test
	public void addNotify() throws NotifyException {
		int[] threads = { 10, 20, 30, 40, 50 };
		int[] notifys = { 10000, 40000, 50000 };
		List<TestResult> results = new ArrayList<TestResult>();

		for (int i = 0; i < threads.length; i++) {
			executor = Executors.newFixedThreadPool(threads[i]);
			for (int j = 0; j < notifys.length; j++) {
				results.add(execute(threads[i], notifys[j], new Runnable() {
					public void run() {
						notifyDao.addNotify(mockNotifyDO());
					}
				}));
			}
			executor.shutdown();
		}

		printObj("|ThreadNumber|TotalNotifys|TotalTime(ms)|Speed(m/s)|");
		for (TestResult result : results) {
			StringBuilder line = new StringBuilder("|");
			line.append(layout(12, result.getThread() + ""));
			line.append(layout(12, result.getTimes() + ""));
			line.append(layout(13, result.getElapsed() + ""));
			line.append(layout(10, result.getSpeed() + ""));
			printObj(line.toString());
		}
	}

	@Test
	public void addIsvNotify() throws NotifyException {
		int[] threads = { 10, 20, 30, 40, 50 };
		int[] notifys = { 100000, 140000, 160000 };
		List<TestResult> results = new ArrayList<TestResult>();

		for (int i = 0; i < threads.length; i++) {
			executor = Executors.newFixedThreadPool(threads[i]);
			for (int j = 0; j < notifys.length; j++) {
				results.add(execute(threads[i], notifys[j], new Runnable() {
					public void run() {
						NotifyDO notifyDO = mockNotifyDO();
						notifyDO.setAppKey("100120");
						notifyDao.addNotify(notifyDO);
					}
				}));
			}
			executor.shutdown();
		}

		printObj("|ThreadNumber|TotalNotifys|TotalTime(ms)|Speed(m/s)|");
		for (TestResult result : results) {
			StringBuilder line = new StringBuilder("|");
			line.append(layout(12, result.getThread() + ""));
			line.append(layout(12, result.getTimes() + ""));
			line.append(layout(13, result.getElapsed() + ""));
			line.append(layout(10, result.getSpeed() + ""));
			printObj(line.toString());
		}
	}

	@Test
	public void queryNotifys() {
		final int[] threads = { 10, 20, 30, 40, 50 };
		final int[] timeses = { 2000, 4000, 10000 };
		List<TestResult> results = new ArrayList<TestResult>();

		for (int i = 0; i < threads.length; i++) {
			executor = Executors.newFixedThreadPool(threads[i]);
			for (int j = 0; j < timeses.length; j++) {
				results.add(execute(threads[i], timeses[j], new Runnable() {
					public void run() {
						NotifyQuery query = new NotifyQuery();
						query.setAppKey(randomAppKey());
						query.setUserId(randomUserId());
						query.setCategory(NotifyEnum.TRADE.getCategory());
						notifyDao.queryNotifys(query);
					}
				}));
			}
			executor.shutdown();
		}

		printObj("|ThreadNumber|TotalInvokes|TotalTime(ms)|Speed(i/s)|");
		for (TestResult result : results) {
			StringBuilder line = new StringBuilder("|");
			line.append(layout(12, result.getThread() + ""));
			line.append(layout(12, result.getTimes() + ""));
			line.append(layout(13, result.getElapsed() + ""));
			line.append(layout(10, result.getSpeed() + ""));
			printObj(line.toString());
		}
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
	private String layout(int size, String value) {
		StringBuilder layout = new StringBuilder();
		int remain = size - value.length();
		for (int i = 0; i < remain; i++) {
			layout.append(" ");
		}
		layout.append(value).append("|");
		return layout.toString();
	}

}
