package com.taobao.top.notify.loadtest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.hsf.notify.client.MessageListener;
import com.taobao.hsf.notify.client.MessageStatus;
import com.taobao.hsf.notify.client.message.Message;
import com.taobao.tc.domain.dataobject.BizOrderDO;
import com.taobao.tc.domain.dataobject.OrderInfoTO;
import com.taobao.tc.message.convertor.OrderInfoMessageConverter;
import com.taobao.top.notify.domain.TradeMsgType;
import com.taobao.top.notify.receive.NotifyListener;
import com.taobao.top.notify.receive.NotifySubscriber;
import com.taobao.top.notify.util.NotifyFillUtilsTest;

/**
 * 消息系统整体压力测试。
 * 
 * @author fengsheng
 * @since 1.0, Dec 16, 2009
 */
public class NotifyLoadTest  {

	protected static final ApplicationContext ctx;

	static {
		List<String> paths = new ArrayList<String>();
		paths.add("spring-config-test.xml");
		paths.add("spring-persist-test.xml");
		paths.add("spring-cache.xml");
		paths.add("spring-receive.xml");
		paths.add("spring-monitor.xml");
		ctx = new ClassPathXmlApplicationContext(paths.toArray(new String[0]));
	}

	private static MessageListener listener = (NotifyListener) ctx.getBean("notifyListener");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		Thread.sleep(3000); // 等待配置中心推送HSF地址
//		CacheManager cache = (CacheManager) ctx.getBean("cacheManager");
//		CacheRefreshTask task = (CacheRefreshTask) ctx.getBean("cacheRefreshTask");
//		cache.init();
//		Timer timer = new Timer();
//		timer.schedule(task, 6000L, task.getRefreshPeriodInMs());
//		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		System.out.println("test start: " + format.format(new Date()));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("test end: " + format.format(new Date()));
	}

	@Test
	public void testing() {
		try {
			long totalTime = System.currentTimeMillis();

			int threads = 30;
			int count = 10000;
			List<Long> result = Collections.synchronizedList(new ArrayList<Long>());

			CountDownLatch startSignal = new CountDownLatch(1);
			CountDownLatch doneSignal = new CountDownLatch(threads);

			for (int i = 0; i < threads; i++) {
				new Thread(new Task(result, count, startSignal, doneSignal)).start();
			}

			startSignal.countDown();
			doneSignal.await();

			if (result.size() == threads) {
				long total = 0;
				for (long l : result) {
					total += l;
				}

				System.out.println(new StringBuffer().append("cache test consume: ").append(total)
						.append(", average boundle consume: ").append(total / (long) result.size())
						.append(", average per request :").append(
								total / (long) result.size() / (long) count));
			}

			totalTime = System.currentTimeMillis() - totalTime;
			System.out.println("total consume: " + totalTime);
			System.out.println("TPS: " + (double) (threads * count) / ((double) totalTime / 1000));

			Thread.sleep(3600000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private class Task implements Runnable {
		private List<Long> result;
		private int count;
		private CountDownLatch start;
		private CountDownLatch done;

		public Task(List<Long> r, int c, CountDownLatch start, CountDownLatch done) {
			count = c;
			result = r;
			this.start = start;
			this.done = done;
		}

		public void run() {
			try {
				start.await();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			long time = System.currentTimeMillis();

			for (int i = 0; i < count; i++) {
				listener.receiveMessage(createTradeMessage(), new MessageStatus());
			}

			time = System.currentTimeMillis() - time;
			result.add(time);
			done.countDown();
		}
	}

	private Message createTradeMessage() {
		OrderInfoMessageConverter converter = new OrderInfoMessageConverter(new TradeMsgType(200));
		OrderInfoTO orderInfo = new OrderInfoTO();
		BizOrderDO bizOrder = NotifyFillUtilsTest.mockBizOrderDOMain();
		orderInfo.setBizOrderDO(bizOrder);

		Message message = converter.toMessage(orderInfo);
		message.setTopic(NotifySubscriber.TRADE_TOPIC);
		message.setMessageType("200-trade-modify-fee-done");
		return message;
	}

}
