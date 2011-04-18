package com.taobao.console;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.taobao.top.console.client.handler.ConsoleClientPipeInput;
import com.taobao.top.console.client.handler.ConsoleClientPipeResult;
import com.taobao.top.console.client.util.ConsoleClientErrorCode;
import com.taobao.top.sm.Session;
import com.taobao.top.sm.SessionGenerator;
import com.taobao.top.sm.SessionNotExistException;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.timwrapper.manager.TadgetManager;
import com.taobao.uic.common.domain.BaseUserDO;
import com.taobao.uic.common.domain.ResultDO;
import com.taobao.uic.common.service.userinfo.client.UicReadServiceClient;

/**
 * http://localhost:8080/top/services/inner?direct=CacheCenter&cmd=select&type=session&sessionKey=xx 查询某个sessionkey对应的Session对象
 *                                                                                     appkey=xx&nick=xx 生成session
 *                                                             cmd=delete              sessionKey=xx 删除sessionKey                        
 * session类型缓存操作实现
 * @author zhenzi
 * 2010-12-24 下午12:16:06
 */
public class SessionCacheOpt extends AbstractCacheOptImpl {
	private SessionGenerator sessionGenerator;
	private TadgetManager tadgetManager;
	private UicReadServiceClient uicReadServiceClient;
	
	public void setSessionGenerator(SessionGenerator sessionGenerator) {
		this.sessionGenerator = sessionGenerator;
	}

	public void setTadgetManager(TadgetManager tadgetManager) {
		this.tadgetManager = tadgetManager;
	}

	public void setUicReadServiceClient(UicReadServiceClient uicReadServiceClient) {
		this.uicReadServiceClient = uicReadServiceClient;
	}

	@Override
	public void deleteCache(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		String sessionKey = input.getString("sessionKey");
		try{
			sessionGenerator.deleteSessionId(sessionKey);
			Session session = sessionGenerator.revertSession(sessionKey);
			if(session != null){
				result.setErr_msg("delete session error");
			}else{
				result.setBlack(ConsoleConstants.OPT_TRUE);
			}
		}catch(Exception e){
			if(e instanceof SessionNotExistException){
				result.setBlack(ConsoleConstants.OPT_TRUE);
			}else{
				result.setErr_msg("delete session error");
			}
		}
	}

	@Override
	public void getCache(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		String sessionId = input.getString("sessionKey");
		if(!StringUtils.isBlank(sessionId)){//反解sessionKey对应的session数据
			Map<String,String> sessionMap = new HashMap<String,String>();
			Session session = null;
			try{
				session = sessionGenerator.revertSession(sessionId);
			}catch(Exception e){
				session = null;
			}
			if(session != null){
				int sessionType = session.getType();
				if(sessionType == 1){
					sessionMap.put("sessionType", "固定有效时间");
				}else if(sessionType == 2){
					sessionMap.put("sessionType", "延长有效时间");
				}else if(sessionType == 3){
					sessionMap.put("sessionType", "一次使用有效");
				}else if(sessionType == 4){
					sessionMap.put("sessionType", "持久有效时间");
				}
				sessionMap.put("sessionTime", String.valueOf(session.getValidThru() / 1000 / 60));
				sessionMap.put("sessionKey", session.getSessionId());
				sessionMap.put("appkeys", session.getAppKeys().toString());
				sessionMap.put("nick", session.getNick());
				sessionMap.put("userId", session.getUserId());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
				sessionMap.put("validFrom", sdf.format(session.getValidFrom()));
				sessionMap.put("validEnd", sdf.format(session.getValidEnd()));
				sessionMap.putAll(session.getProperties());
				result.setBlack(sessionMap);
			}else {
				result.setErr_msg("sessionkey has no session value");
				result.setErrorCode(ConsoleClientErrorCode.INVALID_ARGUMENTS);
			}
		}else{//生成session
			String appkey = input.getString("appkey");
			String nick = input.getString("nick");
			if(StringUtils.isEmpty(appkey) || StringUtils.isEmpty(nick)){
				result.setErr_msg("appkey or nick is null");
				result.setErrorCode(ConsoleClientErrorCode.MISS_ARGUMENTS);
				return;
			}
			ResultDO<BaseUserDO> resultDO = uicReadServiceClient.getBaseUserByNick(nick);
			BaseUserDO visitor = null;
			if(resultDO.isSuccess()){
				visitor = resultDO.getModule();
			}else{
				result.setErr_msg("get visitor error");
				result.setErrorCode(ConsoleClientErrorCode.INVALID_ARGUMENTS);
				return;
			}
			String session = null;
			try {
				session = generateSession(tadgetManager.getTadgetByKey(appkey),visitor);
				result.setBlack(session);
			}catch (Exception e) {
				logger.error(e, e);
				result.setErr_msg("generate session error: " + e.getMessage());
				result.setErrorCode(ConsoleClientErrorCode.PLATFORM_SYSTEM_ERROR);
			}
		}
	}

	@Override
	public void updateCache(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		//do nothing
	}
	private String generateSession(TinyAppDO tadget, BaseUserDO visitor) throws Exception{
		String session = "";
		long sessionValidTime = ConsoleConstants.FIX_TYPE_SESSION_VALIDATE_TIME;//固定失效时间sessionKey
		if(tadget.getSessionKeyType() != null && tadget.getSessionKeyValidTimeHour() != null){//如果top-admin设置了session的生成机制
			if(tadget.getSessionKeyType().intValue() == 1){//1为自定义访问延迟有效期SessionKey 
				sessionValidTime = (long)(tadget.getSessionKeyValidTimeHour().floatValue() * 60 * 60 * 1000);
				session = sessionGenerator.generateKeepAliveSession(null, visitor
						.getNick(), String.valueOf(visitor.getUserId()),
						sessionValidTime,false, tadget.getSecret(),new String[]{tadget.getAppKey()});
			}else if(tadget.getSessionKeyType().intValue() == 2){//2为固定有效期SessionKey
				sessionValidTime = (long)(tadget.getSessionKeyValidTimeHour().floatValue() * 60 * 60 * 1000);
				session = sessionGenerator.generateKeepAliveSession(null, visitor.
						getNick(), String.valueOf(visitor.getUserId()),sessionValidTime,true,tadget.getSecret(), new String[]{tadget.getAppKey()});
			}
		}else{//top-admin没有设置任何session的生成机制，则用延时生成机制
			sessionValidTime = ConsoleConstants.SESSION_VALIDATE_TIME;
			session = sessionGenerator.generateKeepAliveSession(null, visitor
					.getNick(), String.valueOf(visitor.getUserId()),
					sessionValidTime, false,tadget.getSecret(),new String[]{tadget.getAppKey()});
		}
		return session;
	}
}
