package com.taobao.top.notify.service;

import org.junit.Assert;
import org.junit.Test;

import com.taobao.top.notify.NotifyException;
import com.taobao.top.notify.TestBase;
import com.taobao.top.notify.domain.NotifyDO;
import com.taobao.top.notify.domain.PageList;
import com.taobao.top.notify.domain.query.NotifyQuery;
import com.taobao.top.notify.util.DateUtils;

/**
 * 
 * @author fengsheng
 * @since 1.0, Jan 30, 2010
 */
public class NotifyServiceTest extends TestBase {

	private NotifyService notifyService = (NotifyService) ctx.getBean("notifyService");

	@Test
	public void queryNotifys() throws Exception {
		NotifyQuery query = new NotifyQuery();
		query.setAppKey("69546");
		query.setUserId(175947548L);
		query.setStartModified(DateUtils.parseDate("2010-6-29 14:45:16"));
		query.setEndModified(DateUtils.parseDate("2010-6-30 14:45:16"));
		PageList<NotifyDO> rsp = notifyService.queryNotifys(query);
		System.out.println(rsp.getTotal());
		for(NotifyDO no : rsp.getData()){
			System.out.println(no.getCategory());
		}
	}

	@Test
	public void queryOldNotifys() throws Exception {
		NotifyQuery query = new NotifyQuery();
		query.setAppKey("100120");
		query.setUserId(456L);
		query.setStartModified(DateUtils.parseDate("2010-06-03 12:00:00"));
		PageList<NotifyDO> rsp = notifyService.queryNotifys(query);
		System.out.println(rsp.getTotal());
	}

	@Test
	public void queryNotifysWithErrParam() throws NotifyException {
		NotifyQuery query = new NotifyQuery();
		query.setAppKey(null);
		try {
			notifyService.queryNotifys(query);
			Assert.fail("Should not come here!");
		} catch (RuntimeException e) {
			Assert.assertEquals("AppKey should not be blank", e.getMessage());
		}
	}

//	@Test
//	public void addSubscribe() throws NotifyException {
//		SubscribeDO sub = mockSubscribe();
//		sub.setAppKey("42726");
//		System.out.println(sub.getAppKey());
//		SubscribeDO oldSub = notifyService.getSubscribe(sub.getAppKey());
//		System.out.println("oldSub = " + oldSub);
//		if (oldSub == null) {
//			notifyService.addSubscribe(sub);
//		}
//	}
//	
//	public static void main(String[] args) throws NotifyException {
//		NotifyServiceTest test = new NotifyServiceTest();
//		SubscribeDO sub = test.mockSubscribe();
//		sub.setAppKey("42727");
//		System.out.println(sub.getAppKey());
//		SubscribeDO oldSub = test.notifyService.getSubscribe(sub.getAppKey());
//		System.out.println("oldSub = " + oldSub);
//		if (oldSub == null) {
//			test.notifyService.addSubscribe(sub);
//		}
//	}
//
//	@Test
//	public void getSubscribeWithErrParam() throws NotifyException {
//		SubscribeDO result = notifyService.getSubscribe("4272");
//		System.out.println("result = " + result);
//		Assert.assertNull(result);
//	}
//
//	@Test
//	public void updateSubscribeWithErrParam() {
//		SubscribeDO sub = mockSubscribe();
//		sub.setId(2L);
//		sub.setAppKey("fengsheng");
//		try {
//			notifyService.updateSubscribe(sub);
//			Assert.fail("Should not come here!");
//		} catch (NotifyException e) {
//		}
//	}
//
//	@Test
//	public void addAuthorize() throws NotifyException {
//		AuthorizeDO auth = mockAuthorize();
//		auth.setAppKey("188");
//		AuthorizeDO oldAuth = notifyService.getAuthorize(auth.getAppKey(), auth.getUserId());
//		System.out.println("oldAuth = " + oldAuth);
//		if (oldAuth == null) {
//			notifyService.addAuthorize(auth);
//		}
//	}
//
//	@Test
//	public void getAuthorizeWithErrParam() throws NotifyException {
//		try {
//			notifyService.getAuthorize(null, null);
//			Assert.fail("Should not come here!");
//		} catch (RuntimeException e) {
//			Assert.assertEquals("AppKey should not be blank", e.getMessage());
//		}
//	}
//
//	@Test
//	public void updateAuthorize() {
//		AuthorizeDO auth = mockAuthorize();
//		auth.setAppKey("148");
//		System.out.println(auth.getUserName());
//		try {
//			notifyService.updateAuthorize(auth);
//			Assert.fail("Should not come here!");
//		} catch (NotifyException e) {
//		}
//	}

}
