package com.taobao.top.notify.service;

import java.util.ArrayList;
import java.util.List;

import com.taobao.top.notify.AbstractSamService;
import com.taobao.top.tim.domain.AuthorizeDO;
import com.taobao.top.tim.domain.SubscribeDO;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.tim.service.TIMServiceException;

public class SamServiceMock extends AbstractSamService {

	@Override
	public Long addAuthorize(AuthorizeDO authorize) throws TIMServiceException {
		return 8L;
	}

	@Override
	public List<SubscribeDO> getAuthorizedSubscribes(Long userId, Integer category,
			Integer bizType, Long userRole) throws TIMServiceException {
		List<SubscribeDO> result = new ArrayList<SubscribeDO>();
		SubscribeDO app1 = new SubscribeDO();
		app1.setAppKey("Jerry");
		app1.setSubscriptions("1:0;2:1;3:0");
		app1.setIsNotify("1:0;2:1;3:0");
		app1.setUserRole(0L);
		app1.setAttributes("key1:value1:key1name:value1name;key2:value2:key2name:value2name");
		SubscribeDO app2 = new SubscribeDO();
		app2.setAppKey("Jerry");
		app2.setSubscriptions("1:0;2:1;3:0");
		app2.setIsNotify("1:0;2:1;3:0");
		app2.setUserRole(0L);
		app1.setAttributes("key1:value1:key1name:value1name;key2:value2:key2name:value2name");
		result.add(app1);
		result.add(app2);
		return result;
	}
	
	@Override
	public TinyAppDO getAppByKey(String appkey) throws TIMServiceException {
		if ("Jerry".equals(appkey)) {
			TinyAppDO app = new TinyAppDO();
			app.setAppKey(appkey);
			app.setNotifyUrl("http://jerry.com");
		}
		return null;
	}

	@Override
	public Long addSubscribe(SubscribeDO subscribe) throws TIMServiceException {
		// TODO Auto-generated method stub
		return 8L;
	}

//	@Override
//	public AuthorizeResultModel getAuthorize(String appKey, List<Long> userIds,
//			Long pageSize, Long currentPage) throws TIMServiceException {
//		AuthorizeResultModel result = new AuthorizeResultModel();
//		return result;
//	}
	
	@Override
	public SubscribeDO getSubscribe(String appKey) throws TIMServiceException {
		SubscribeDO result = new SubscribeDO();
		return result;
	}

	@Override
	public int updateAuthorize(AuthorizeDO authorize)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return 8;
	}

	@Override
	public int updateSubscribe(SubscribeDO subscribe)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return 8;
	}

}
