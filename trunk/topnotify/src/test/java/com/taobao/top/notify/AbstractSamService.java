package com.taobao.top.notify;

import java.util.Date;
import java.util.List;

import com.taobao.top.tim.domain.ApiGroupDO;
import com.taobao.top.tim.domain.AppAccountDO;
import com.taobao.top.tim.domain.AuthDO;
import com.taobao.top.tim.domain.AuthorizeDO;
import com.taobao.top.tim.domain.AuthorizeResultDO;
import com.taobao.top.tim.domain.ExPropertyDO;
import com.taobao.top.tim.domain.FlowRuleV2DO;
import com.taobao.top.tim.domain.PlatformDO;
import com.taobao.top.tim.domain.RuleDO;
import com.taobao.top.tim.domain.SubscribeDO;
import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.tim.service.SamService;
import com.taobao.top.tim.service.TIMServiceException;

public class AbstractSamService implements SamService {

	public Long addAuthorize(AuthorizeDO authorize) throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public Long addSubscribe(SubscribeDO subscribe) throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public Long bindAppAccount(String appKey, String appUid, Long userId)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean checkUserPermitForApp(Long userId, Long appId)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return false;
	}

	public Long createChildApp(String parentAppkey, String title,
			String callbackUrl, String notifyUrl, String logoUrl)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public ApiGroupDO getApiGroupById(Long apiGroupId)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Long> getApiIdListByRoleId(Long roleId)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public Long getAppApiLimit(Long appId, Long apiId)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public TinyAppDO getAppById(Long appId) throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public TinyAppDO getAppByKey(String appkey) throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

//	public AppFlowRuleDO getAppFlowRuleById(Long ruleId)
//			throws TIMServiceException {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public List<String> getAppkeysByPlatformIdUserId(Long userId,
			Long platformId) throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

//	public AuthorizeResultModel getAuthorize(String appKey, List<Long> userIds,
//			Long pageSize, Long currentPage) throws TIMServiceException {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public List<TinyAppDO> getAuthorizedApps(Long userId, Integer category,
			Integer bizType) throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public AppAccountDO getBindedAppAccountByAppKeyAppUidTaobaoUid(
			String appKey, String appUid, Long userId)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public FlowRuleV2DO getFlowRuleById(Long flowRuleId)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public PlatformDO getPlatformById(Long platformId)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public RuleDO getRuleByIdAndType(Long ruleId, Long type)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public SubscribeDO getSubscribe(String appKey) throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public TinyApiDO getValidApiByApiName(String apiname)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public int unbindAppAccount(String appKey, String appUid, Long userId)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int updateAuthorize(AuthorizeDO authorize)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int updateSubscribe(SubscribeDO subscribe)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<String> getAuthorizedAppKeys(Long userId)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public AuthorizeResultDO getAuthorize(String appKey, List<Long> userIds, Long pageSize,
			Long currentPage) throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getExpiryDate(String appKey, Long userId) throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SubscribeDO> getAuthorizedSubscribes(Long userId,
			Integer category, Integer status, Long userRole)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ExPropertyDO> getExtPropertyByKeyAndType(String key, Long type)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public AuthDO getPersistentAuthBySessionKey(String arg0)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void insertOrUpdateAuth(AuthDO arg0) throws TIMServiceException {
		// TODO Auto-generated method stub
		
	}

	public void removePersistentAuth(long arg0, long arg1)
			throws TIMServiceException {
		// TODO Auto-generated method stub
		
	}

}
