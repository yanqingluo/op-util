package com.taobao.top.notify.service;

import com.taobao.top.notify.NotifyException;
import com.taobao.top.notify.domain.NotifyDO;
import com.taobao.top.notify.domain.PageList;
import com.taobao.top.notify.domain.query.NotifyQuery;

/**
 * 消息服务接口。
 * 
 * @author fengsheng
 * @since 1.0, Jan 26, 2010
 */
public interface NotifyService {

	public PageList<NotifyDO> queryNotifys(NotifyQuery query) throws NotifyException;

}
