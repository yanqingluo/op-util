package com.taobao.top.notify.persist;

import java.util.Date;

import org.junit.Test;

import com.taobao.top.notify.NotifyException;
import com.taobao.top.notify.TestBase;
import com.taobao.top.notify.domain.NotifyDO;
import com.taobao.top.notify.domain.PageList;
import com.taobao.top.notify.domain.query.NotifyQuery;
import com.taobao.top.notify.util.DateUtils;

/**
 * 消息数据访问对象测试。
 * 
 * @author fengsheng
 * @since 1.0, Dec 15, 2009
 */
public class NotifyDaoTest extends TestBase {

	private NotifyDao notifyDao = (NotifyDao) ctx.getBean("notifyDao");

	@Test
	public void addNotify() throws NotifyException {
		notifyDao.addNotify(mockNotifyDO());
	}

	@Test
	public void queryNotifys() throws NotifyException {
		NotifyQuery query = new NotifyQuery();
		query.setAppKey(randomAppKey());
		query.setStartModified(DateUtils.getDateStart(new Date()));
		query.setEndModified(DateUtils.getDateEnd(new Date()));
		query.setCategory(1);
		PageList<NotifyDO> notifys = notifyDao.queryNotifys(query);
		System.out.println(notifys.getTotal());
		if (!notifys.isEmpty()) {
			System.out.println(notifys.getData().size());
		}
	}

	@Test
	public void getNotify() throws NotifyException {
		NotifyDO nd = notifyDao.getNotify(1345134062014668614L);
		System.out.println(nd.getUserName());
	}


}
