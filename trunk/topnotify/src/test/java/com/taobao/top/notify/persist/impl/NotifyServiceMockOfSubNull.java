package com.taobao.top.notify.persist.impl;

import java.util.ArrayList;
import java.util.List;

import com.taobao.top.notify.NotifyException;
import com.taobao.top.notify.domain.AuthorizeDO;
import com.taobao.top.notify.domain.NotifyDO;
import com.taobao.top.notify.domain.PageList;
import com.taobao.top.notify.domain.SubscribeDO;
import com.taobao.top.notify.domain.query.AuthorizeQuery;
import com.taobao.top.notify.domain.query.NotifyQuery;
import com.taobao.top.notify.domain.query.SubscribeQuery;
import com.taobao.top.notify.service.NotifyService;

/**
 * 消息服务模拟实现。
 * 
 * @author moling
 * @since 1.0, 2010-1-7
 */
public class NotifyServiceMockOfSubNull implements NotifyService {

	public void addAuthorize(AuthorizeDO authorizeDO) throws NotifyException {
	}

	public void addSubscribe(SubscribeDO subscribeDO) throws NotifyException {
	}

	public AuthorizeDO getAuthorize(String appKey, Long userId) throws NotifyException {
		return null;
	}

	public SubscribeDO getSubscribe(String appKey) throws NotifyException {
		return null;
	}

	public PageList<AuthorizeDO> queryAuthorizes(AuthorizeQuery query) throws NotifyException {
		PageList<AuthorizeDO> result = new PageList<AuthorizeDO>();
		Long userId1 = null;
		Long userId2 = null;
		if ("appKey1".equals(query.getAppKey())) {
			userId1 = 111111L;
			userId2 = 333333L;
		} else if ("appKey2".equals(query.getAppKey())) {
			userId1 = 111111L;
			userId2 = 333333L;
		} else if ("appKey3".equals(query.getAppKey())) {
			userId1 = 333333L;
			userId2 = 222222L;
		}
		
		AuthorizeDO autho1 = getAuthorize(query.getAppKey(), userId1);
		AuthorizeDO autho2 = getAuthorize(query.getAppKey(), userId2);
		result.addRecord(autho1);
		result.addRecord(autho2);
		result.setPageSize(100);
		result.setTotal(2);
	
		return result;
	}

	public PageList<SubscribeDO> querySubscribes(SubscribeQuery query) throws NotifyException {
		PageList<SubscribeDO> result = new PageList<SubscribeDO>();
		result.setPageSize(100);
		result.setTotal(0);
		return result;
	}

	public void updateAuthorize(AuthorizeDO authorizeDO) throws NotifyException {
	}

	public void updateSubscribe(SubscribeDO subscribeDO) throws NotifyException {
	}

	public List<AuthorizeDO> getUserAuthorizes(Long userId) throws NotifyException {
		List<AuthorizeDO> result = new ArrayList<AuthorizeDO>();
		return result;
	}

	public PageList<NotifyDO> queryNotifys(NotifyQuery query)
			throws NotifyException {
		return null;
	}

}
