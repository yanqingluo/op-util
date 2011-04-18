package com.taobao.top.notify;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.top.notify.domain.AuthorizeDO;
import com.taobao.top.notify.domain.Notify;
import com.taobao.top.notify.domain.NotifyDO;
import com.taobao.top.notify.domain.SubscribeDO;
import com.taobao.top.notify.util.DateUtils;

/**
 * 单元测试基类。
 * 
 * @author fengsheng
 * @since 1.0, Dec 15, 2009
 */
public abstract class TestBase {

	protected static final Random random = new Random();
	protected static final ApplicationContext ctx;

	static {
		List<String> paths = new ArrayList<String>();
		paths.add("spring-config-test.xml");
		paths.add("spring-cache-test.xml");
		paths.add("spring-receive-test.xml");
		paths.add("spring-persist-test.xml");
		paths.add("spring-monitor.xml");
		paths.add("spring-send-test.xml");
		ctx = new ClassPathXmlApplicationContext(paths.toArray(new String[0]));
	}

	protected void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
		}
	}

	protected NotifyDO mockNotifyDO() {
		NotifyDO notify = new NotifyDO();
		notify.setId(33333331L);
		notify.setAppKey(randomAppKey());
		notify.setCategory(random.nextInt(3) + 1);
		notify.setBizType(random.nextInt(900));
		notify.setStatus(random.nextInt(5) + 1);
		notify.setUserId((long) random.nextInt(500));
		notify.setUserName("Jerry_" + notify.getUserId());
		notify.setGmtCreate(new Date());
		notify.setGmtModified(new Date());

		notify.setContent("{\"buyer_nick\":\"" + notify.getUserName() + "\"}");
		return notify;
	}

	protected Notify mockNotify() {
		Notify notify = new Notify();
		notify.setAppKey(randomAppKey());
		notify.setCategory(random.nextInt(3) + 1);
		notify.setBizType(random.nextInt(900));
		notify.setStatus(random.nextInt(5) + 1);
		notify.setUserId(randomUserId());
		notify.setUserName("tbtest" + random.nextInt(1000));
		notify.setCreated(new Date());

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, -random.nextInt(7));
		notify.setModified(cal.getTime());

		notify.addContent("seller_nick", "tbtest1063");
		notify.addContent("price", "17.23");
		return notify;
	}

	protected SubscribeDO mockSubscribe() {
		SubscribeDO subscribe = new SubscribeDO();
		subscribe.setAppKey("427280");
		subscribe.setUserId(88888888L);
		subscribe.setUserName("tbtest" + random.nextInt(1000));
		subscribe.setType(random.nextInt(4) + 1);
		subscribe.setStartDate(new Date());
		subscribe.setEndDate(DateUtils.addDays(new Date(), 7));
		subscribe.setEmail("fengsheng@taobao.com");
		subscribe.setGmtCreate(new Date());
		subscribe.setGmtModified(new Date());
		subscribe.setStatus(1);
		subscribe.setSubscriptions("0:128;1:64;2:1024");
		return subscribe;
	}

	protected AuthorizeDO mockAuthorize() {
		AuthorizeDO authorize = new AuthorizeDO();
		authorize.setAppKey("148");
		authorize.setUserId(1898570469L);
		authorize.setUserName("tbtest" + random.nextInt(1000));
		authorize.setStatus(random.nextInt() % 2 == 0 ? 0 : 1);
		authorize.setStartDate(new Date());
		authorize.setEndDate(DateUtils.addDays(new Date(), 5));
		authorize.setEmail("carver.gu@hotmail.com");
		authorize.setGmtCreate(new Date());
		authorize.setGmtModified(new Date());
		return authorize;
	}

	protected String randomAppKey() {
		return random.nextInt(20) + "";
	}
	
	protected Long randomUserId() {
		return 100000L + Math.abs(random.nextInt());
	}

	protected void printObj(Object obj) {
		if (obj instanceof String) {
			System.out.println(obj);
		} else if (obj instanceof Long) {
			System.out.println((Long) obj);
		} else {
			System.out.println(ToStringBuilder.reflectionToString(obj));
		}
	}

}
