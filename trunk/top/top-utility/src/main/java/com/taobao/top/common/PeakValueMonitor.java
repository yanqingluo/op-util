package com.taobao.top.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 用于峰值监控,TIP,container,api
 * 
 * @author zhenzi
 * 
 */
public class PeakValueMonitor {
	private Log log = LogFactory.getLog(PeakValueMonitor.class);

	private int queueSize = 300 * 5;// 保存api的数量加 tip container的5分钟的数据量
	private LinkedBlockingQueue<Data> data = new LinkedBlockingQueue<Data>(
			queueSize);
	private ConcurrentHashMap<String, AtomicLong> peakValue;

	private Object o = new Object();
	private BackThread backThread = null;

	public PeakValueMonitor() {
		// new BackThread().start();
	}

	// -------------test
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		final PeakValueMonitor test = new PeakValueMonitor();
		for (int i = 0; i < 1000; i++) {
			Thread t = new Thread(new Test("thread-" + i % 10, test));
			t.start();
		}
	}

	private static class Test implements Runnable {
		private PeakValueMonitor test = null;
		private String threadName = null;

		public Test(String threadName, PeakValueMonitor test) {
			this.threadName = threadName;
			this.test = test;
		}

		public void run() {
			for (int i = 0; i < 1000; i++) {
				test.add(threadName);
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
			}
		}
	}

	// ----------test

	public void add(String type) {
		if (backThread == null || !backThread.isAlive()) {
			return;
		}
		try {
			peakValue.get(type).incrementAndGet();
		} catch (Exception e) {
			synchronized (o) {
				if (peakValue.get(type) == null) {
					peakValue.put(type, new AtomicLong(1));
				} else {
					peakValue.get(type).incrementAndGet();
				}
			}
		}
	}

	public List<String> get() {
		
		if (backThread == null || !backThread.isAlive()) {
			throw new IllegalThreadStateException("balck thread dead");
		}
		List<String> dataList = new ArrayList<String>();
		for (int i = 0, n = data.size(); i < n; i++) {
			dataList.add(data.poll().toString());
		}
		return dataList;
	}

	/**
	 * 设置状态
	 * 
	 * @param status
	 * @return
	 */
	public boolean changeStatus(boolean status) {
		if (status) {
			peakValue = new ConcurrentHashMap<String, AtomicLong>();
			if(backThread!=null&&backThread.isAlive()){
				backThread.setRunning(false);
				backThread.interrupt();
			}
			backThread = new BackThread();
			backThread.setPeakValue(peakValue);
			backThread.start();
		}else{
			backThread.setRunning(false);
			backThread.interrupt();
		}
		return true;
	}

	private class BackThread extends Thread {
		private long interval = 1 * 60 * 1000;// 一分钟
		private long start = 0;
		private long end = 0;
		private boolean isRunning = true;
		private Map<String, AtomicLong> peakValueMap;

		public void setRunning(boolean isRunning) {
			this.isRunning = isRunning;
		}

		public void setPeakValue(Map<String, AtomicLong> peakValue) {
			this.peakValueMap = peakValue;
		}

		public void run() {
			while (isRunning) {

				try {
					long sleepTime = interval - (end - start);
					if(sleepTime>0){
						Thread.sleep(sleepTime);
					}
				} catch (InterruptedException e1) {
					break;
				}
				try {
					start = System.currentTimeMillis();
					Iterator<Entry<String, AtomicLong>> iterator = peakValueMap
							.entrySet().iterator();
					while (iterator.hasNext()) {
						Entry<String, AtomicLong> e = iterator.next();
						long times = e.getValue().get();
						e.getValue().addAndGet(-times);
						Data d = new Data(e.getKey(), start, times);
						if (data.size() >= queueSize) {
							data.poll();
						}
						data.offer(d);
					}
					end = System.currentTimeMillis();

				} catch (Exception e) {
					log.error(e, e);
				}
			}
		}
	}

	private class Data implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6568267199877486806L;

		public Data(String key, long date, long times) {
			this.key = key;
			this.date = date;
			this.times = times;
		}

		/*
		 * 那个的峰值
		 */

		private String key;
		/*
		 * 取这个峰值的时间
		 */
		private long date;
		/*
		 * 峰值大小
		 */
		private long times;

		public String toString() {
			return new StringBuilder(key).append("=").append(times).append("=")
					.append(date).toString();
		}
	}
}
