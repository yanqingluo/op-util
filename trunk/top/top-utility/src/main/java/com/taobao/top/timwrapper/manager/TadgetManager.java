package com.taobao.top.timwrapper.manager;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.tim.domain.AuthDO;
import com.taobao.top.tim.domain.FlowRuleV2DO;
import com.taobao.top.tim.domain.RuleAssDO;
import com.taobao.top.tim.domain.ScopeRuleDO;
import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.tim.service.SamService;
import com.taobao.top.tim.service.TIMServiceException;

/**
 * 
 * @author zhangsan
 * @since 1.0, 2010-1-17 上午10:54:41
 */
public class TadgetManager {

	private static final transient Log log = LogFactory
			.getLog(TadgetManager.class);
	
	private SamService timService;
	private static final Long SCOPE_EXCLUDE_TYPE = 4L;

	public void setTimService(SamService timService) {
		this.timService = timService;
	}

	public TinyAppDO getTadgetByKey(String appKey) throws TIMServiceException {
		
		return timService.getAppByKey(appKey);
		
	}

	public TinyAppDO getTadgetById(Long appId) throws TIMServiceException {
		
		return timService.getAppById(appId);
	}

	public SamService getTimService() {
		return timService;
	}
	
	
	/**
	 * 
	 * Judge whether the api is in app access scope.
	 * The principle is: Union(include) - Union(exclude).
	 * 
	 * when entered, the app and api should not be null here.
	 * TODO: Note that it can be optimized to caculate the api set 
	 * once and use many times. but the ones which can't be cached
	 * in local can't use the optimize.
	 * @param app
	 * @param api
	 * @return
	 * @throws TIMServiceException 
	 */
	public boolean appCanAccessApi(TinyAppDO app, TinyApiDO api) throws TIMServiceException {
		//如果不是上线状态、线上测试状态，不许访问
		if (!TinyAppDO.APP_STATUS_RUNNING.equals(app.getAppStatus()) && 
						!TinyAppDO.APP_STATUS_TESTING.equals(app.getAppStatus()) ) 
			return false;
		
		List<Long> scopeRuleIds = app.getScopeRuleIds();
		if(scopeRuleIds == null) {
			return false;
		}
		
		Long requestApiId = api.getId();

		boolean include = false;
		for(Long scopeRuleId : scopeRuleIds) {
			ScopeRuleDO scopeRule = (ScopeRuleDO) timService.getRuleByIdAndType(scopeRuleId, RuleAssDO.RULE_TYPE_SCOPE);
			if(scopeRule == null) {
				log.warn("ScopeRule " + scopeRuleId + " null from tim");
				continue;
			}
			if(scopeRule.getApiIdList().contains(requestApiId)) {
				//exclude is superior than include.
				if(SCOPE_EXCLUDE_TYPE.equals(scopeRule.getType())) {
					return false;
				} else {
					include = true;
				}
			}
		}
		
		return include;
		
	}

	/**
	 * 查用户是否有访问当前插件的权限
	 * @param appId
	 * @param userId
	 * @return
	 * @throws TIMServiceException
	 */
	public boolean checkTadgetPermit(Long appId, Long userId)
			throws TIMServiceException {
		
		return this.timService.checkUserPermitForApp(userId, appId);
	}

	
	public TinyApiDO getValidApiByApiName(String apiname)throws TIMServiceException {
		return timService.getValidApiByApiName(apiname);
	}

	public FlowRuleV2DO getFlowRule(Long flowRuleId) throws TIMServiceException {
		FlowRuleV2DO flowRule = (FlowRuleV2DO) timService.getRuleByIdAndType(
				flowRuleId, RuleAssDO.RULE_TYPE_FLOW);
		return flowRule;
	}
	
	public AuthDO getPersistentAuthBySessionKey(String sessionkey) throws TIMServiceException{
		return timService.getPersistentAuthBySessionKey(sessionkey);
	}

	
}
