package com.taobao.top.notify.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.notify.NotifyException;
import com.taobao.top.notify.domain.NotifyDO;
import com.taobao.top.notify.domain.PageList;
import com.taobao.top.notify.domain.query.NotifyQuery;
import com.taobao.top.notify.persist.NotifyDao;
import com.taobao.top.notify.service.NotifyService;

/**
 * 消息服务接口默认实现。
 * 
 * @author fengsheng
 * @since 1.0, Jan 26, 2010
 */
public class NotifyServiceImpl implements NotifyService {
	
	private static final Log log = LogFactory.getLog(NotifyServiceImpl.class);

	private NotifyDao notifyDao;

	public void setNotifyDao(NotifyDao notifyDao) {
		this.notifyDao = notifyDao;
	}

	public PageList<NotifyDO> queryNotifys(NotifyQuery query) throws NotifyException {
		if (log.isDebugEnabled()) {
			log.debug("Query Notifys：" + ToStringBuilder.reflectionToString(query));
		}
		if (StringUtils.isEmpty(query.getAppKey())) {
			throw new IllegalArgumentException("AppKey should not be blank");
		}
		if (query.getUserId() == null) {
			throw new IllegalArgumentException("UserId should not be null");
		}
		
		return notifyDao.queryNotifys(query);
	}

}
