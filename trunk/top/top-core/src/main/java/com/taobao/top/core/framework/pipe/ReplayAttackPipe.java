package com.taobao.top.core.framework.pipe;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.common.TOPConstants;
import com.taobao.top.common.cache.aop.ReplayAttackLocalCacheImpl;
import com.taobao.top.core.ApiParameterFormatException;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.ProtocolConstants;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.core.framework.TopPipeUtil;
import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.service.TIMServiceException;
import com.taobao.top.timwrapper.manager.TadgetManager;

/**
 * this pipe protects our system from replay attack with using local cache
 * @author zhudi
 *
 */

public class ReplayAttackPipe extends TopPipe<TopPipeInput, TopPipeResult>{
	
	private static final Log log  = LogFactory.getLog(ReplayAttackPipe.class);
	/**
	 * 时间戳的误差允许范围
	 */
	private long scope = 30L*60L*1000L;
	private ReplayAttackLocalCacheImpl cache;
	private TadgetManager tadgetManager;
	
	private boolean isLogOn = false;
	
	

	public boolean isLogOn() {
		return isLogOn;
	}

	public void setLogOn(boolean isLogOn) {
		this.isLogOn = isLogOn;
	}

	public TadgetManager getTadgetManager() {
		return tadgetManager;
	}

	public void setTadgetManager(TadgetManager tadgetManager) {
		this.tadgetManager = tadgetManager;
	}

	public long getScope() {
		return scope;
	}

	public void setScope(long scope) {
		this.scope = scope;
	}

	public ReplayAttackLocalCacheImpl getCache() {
		return cache;
	}

	public void setCache(ReplayAttackLocalCacheImpl cache) {
		this.cache = cache;
	}

	@Override
	public void doPipe(TopPipeInput pipeInput, TopPipeResult pipeResult) {
		
		Date timeStamp = null;
		try {
			timeStamp = pipeInput.getDate(ProtocolConstants.P_TIMESTAMP);
		} catch (ApiParameterFormatException e) {
			return;
		}
		long nowTime = System.currentTimeMillis();
		/**
		 * check if the timestamp is out of scope limit
		 */
		if(Math.abs(nowTime - timeStamp.getTime())>scope){
			if (isLogOn) {
				StringBuilder str = new StringBuilder();
				str.append("replay attack happend:\n");
				str.append("appKey:").append(pipeInput.getAppKey());
				str.append("appIp:").append(pipeInput.getAppIp()).append("\n");
				str.append("timestamps:").append(pipeInput.getTimeStamp());
				str.append("ErrorCode:").append("request out of time");
				log.error(str.toString());
			}
			pipeResult.setErrorCode(ErrorCode.INVALID_TIMESTAMP); 
			pipeResult.setSubCode(TOPConstants.ISV_TIMESTAMP_LIMIT_ERROR);
		}else{
			/**
			 * if not,check if it be hitted in replayAttack cache
			 */
			String key  = pipeInput.getSign()+pipeInput.getTimeStamp();
			Long value = cache.get(key);
			if(value!=null){
				if (isLogOn) {
					StringBuilder str = new StringBuilder();
					str.append("replay attack happend:\n");
					str.append("appKey:").append(pipeInput.getAppKey());
					str.append("appIp:").append(pipeInput.getAppIp()).append(
							"\n");
					str.append("timestamps:").append(pipeInput.getTimeStamp());
					str.append("ErrorCode:").append("request again");
					log.error(str.toString());
				}
				pipeResult.setErrorCode(ErrorCode.INVALID_TIMESTAMP);
				pipeResult.setSubCode(TOPConstants.ISV_REQUEST_REPLAY_ERROR);
			}else{
				cache.put(key, System.currentTimeMillis());
			}
		}
	}

	@Override
	public boolean ignoreIt(TopPipeInput pipeInput, TopPipeResult pipeResult) {
		
		if (pipeResult.getErrorCode() != null || TopPipeUtil.isIgnore(pipeInput)) {
			return true;
		}
		String apiName = pipeInput.getApiName();
		try {
			TinyApiDO api = tadgetManager.getValidApiByApiName(apiName);
			String tag = api.getPropertyValue(TinyApiDO.IS_SENSITIVE);
			if(tag!=null){
				boolean ingore = !Boolean.parseBoolean(tag);
				return ingore;
			}
			return true;			
		} catch (TIMServiceException e) {
			log.error(new StringBuilder("TIM Exception:").append(e),e);
			return true;
		}
	}

	
}
