package com.taobao.console;

import com.taobao.console.domain.AppDO;
import com.taobao.top.console.client.handler.ConsoleClientPipeInput;
import com.taobao.top.console.client.handler.ConsoleClientPipeResult;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.tim.service.TIMServiceException;
import com.taobao.top.timwrapper.manager.TadgetManager;

/**
 *  http://192.168.207.127/top/services/inner?direct=CacheCenter&cmd=select&type=other&appKey=xx 查询appkey的数据
 * 其他一些cache的操作
 * @author zhenzi
 * 2010-12-24 下午12:46:51
 */
public class OtherCacheOpt extends AbstractCacheOptImpl {
	private TadgetManager tadgetManager;
	
	public void setTadgetManager(TadgetManager tadgetManager) {
		this.tadgetManager = tadgetManager;
	}

	@Override
	public void deleteCache(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
	}

	@Override
	public void getCache(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		TinyAppDO appDO = null;
		try {
			appDO = tadgetManager.getTadgetByKey(input.getString("appKey"));
		} catch (TIMServiceException e) {
			result.setErr_msg("get appinfo  error");
			return ;
		}
		if(appDO != null){
			AppDO app = new AppDO();
			app.setAppKey(appDO.getAppKey());
			app.setUserId(appDO.getUserId());
			app.setDescription(appDO.getDescription());
			app.setAppStatus(appDO.getAppStatus());
			app.setParentAppkey(appDO.getParentAppkey());
			app.setNotifyUrl(appDO.getNotifyUrl());
			app.setId(appDO.getId());
			app.setAccessStrategy(appDO.getAccessStrategy());
			app.setLicenseType(appDO.getLicenseType());
			app.setAppLevel(appDO.getAppLevel());
			app.setBindNick(appDO.getBindNick());
			app.setCallbackUrl(appDO.getCallbackUrl());
			app.setVersion(appDO.getVersion());
			app.setAppType(appDO.getAppType());
			app.setAppTag(appDO.getAppTag());
			app.setSecret(appDO.getSecret());
			app.setSessionKeyType(appDO.getSessionKeyType());
			app.setSessionKeyValidTime(appDO.getSessionKeyValidTime());
			app.setSessionKeyValidTimeHour(appDO.getSessionKeyValidTimeHour());
			app.setIsvId(appDO.getIsvId());
			app.setIsvName(appDO.getIsvName());
			app.setRoleIdList(appDO.getRoleIdList());
			app.setTitle(appDO.getTitle());
			app.setFlowRuleIds(appDO.getFlowRuleIds());
			app.setScopeRuleIds(appDO.getScopeRuleIds());
			result.setBlack(app);
		}
	}

	@Override
	public void updateCache(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		// do nothing;
	}

}
