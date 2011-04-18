package com.taobao.top.notify.persist;

import com.taobao.top.notify.domain.NotifyDO;
import com.taobao.top.notify.domain.PageList;
import com.taobao.top.notify.domain.query.NotifyQuery;

/**
 * 通知数据访问对象。
 * 
 * @author fengsheng
 * @since 1.0, Dec 15, 2009
 */
public interface NotifyDao {

	/**
	 * 添加通知。
	 */
	public void addNotify(NotifyDO notify);

	/**
	 * 获取通知。
	 */
	public NotifyDO getNotify(Long id);

	/**
	 * 根据指定的条件查询消息。
	 */
	public PageList<NotifyDO> queryNotifys(NotifyQuery query);

}
