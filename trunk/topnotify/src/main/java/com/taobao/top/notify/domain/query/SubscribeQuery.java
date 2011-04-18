package com.taobao.top.notify.domain.query;

import com.taobao.top.notify.domain.SubscribeDO;

/**
 * 订阅查询条件。
 * 
 * @author fengsheng
 * @since 1.0, Jan 27, 2010
 */
public class SubscribeQuery extends PageQuery {

	private int status = SubscribeDO.STATUS_NORMAL;

	public int getStatus() {
		return this.status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
