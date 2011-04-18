package com.taobao.top.notify.cache.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.notify.cache.SubscriptionManager;
import com.taobao.top.notify.domain.NotifyInfo;
import com.taobao.top.tim.domain.SubscribeDO;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.tim.service.SamService;
import com.taobao.top.tim.service.TIMServiceException;

/**
 * 订阅管理默认实现。
 * 
 * @author moling
 * @since 1.0, 2009-12-15
 */
public class SubscriptionManagerImpl implements SubscriptionManager {

	private static final Log log = LogFactory.getLog(SubscriptionManagerImpl.class);
	
	private SamService samService;
	
	public void setSamService(SamService samService){
		this.samService = samService;
	}
	
	public List<NotifyInfo> getSubscription(Long userId, Integer category, Integer type, Integer status, Long userRole) throws TIMServiceException {
		List<SubscribeDO> apps = samService.getAuthorizedSubscribes(userId, category, status, userRole);
		if (apps == null || apps.isEmpty()) {
			return null;
		}

		if (log.isDebugEnabled()) {
			log.debug("Subscribed Apps: " + apps);
		}
		
		List<NotifyInfo> result = new ArrayList<NotifyInfo>();
		NotifyInfo info;
		for(SubscribeDO app : apps){
			info = new NotifyInfo();
			info.setAppKey(app.getAppKey());
			info.setIsNotify(app.getIsNotify());
			//由于notifyUrl需要一次额外的调用，因此先判断是否有订阅发送字段，没有就不取notifyUrl了
			if (StringUtils.isNotBlank(app.getIsNotify())) {
				//notifyUrl在tadget中，要额外取一次，SubscribeDO里面此字段是空的
				TinyAppDO appDO = samService.getAppByKey(app.getAppKey());
				//如果取不到不做额外的异常处理，记日志即可
				if (null != appDO) {
					info.setNotifyUrl(appDO.getNotifyUrl());
				} else {
					if (log.isWarnEnabled()) {
						log.warn("SubscribeDO cannot find TinyAppDO:" + app.getAppKey());
					}
				}
			}
			
			info.setAttributes(app.getListAttributes());
			info.setSubscriptions(app.getSubscriptions());
			result.add(info);
		}
		return result;
	}

}
