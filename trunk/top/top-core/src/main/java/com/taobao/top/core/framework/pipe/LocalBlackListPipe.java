/**
 * 
 */
package com.taobao.top.core.framework.pipe;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.tim.domain.TinyAppDO;

/**
 * 黑名单pipe，主要是本地多维度的黑名单机制
 * 
 * @author fangweng
 * 
 */
public class LocalBlackListPipe extends TopPipe<TopPipeInput, TopPipeResult> {
	private static final Log logger = LogFactory
			.getLog(LocalBlackListPipe.class);

	private ConcurrentMap<String, String> localBlackList;

	public ConcurrentMap<String, String> getLocalBlackList() {
		return localBlackList;
	}

	public void setLocalBlackList(ConcurrentMap<String, String> localBlackList) {
		this.localBlackList = localBlackList;
	}

	public LocalBlackListPipe() {
		localBlackList = new ConcurrentHashMap<String, String>();
	}

	@Override
	public void doPipe(TopPipeInput pipeInput, TopPipeResult pipeResult) {

		if (localBlackList.size() > 0) {
			// 可以封ip,appkey,apiname,isv,tag,nick
			String ip = pipeInput.getAppIp();
			String apiName = pipeInput.getApiName();
			String appkey = pipeInput.getAppKey();
			TinyAppDO appDO = pipeInput.getAppDO();
			String isvName = null;
			Long tag = null;
			String nick = null;
			if(appDO != null){
				isvName = appDO.getIsvName();
				tag = appDO.getAppTag();
				nick = pipeInput.getSessionNick();
			}

			if (ip != null && localBlackList.containsKey(ip)) {
				pipeResult.setErrorCode(ErrorCode.PLATFORM_SYSTEM_BLACKLIST);
				pipeResult.setMsg(new StringBuilder().append("ip : ")
						.append(ip).append(" in blacklist.").toString());
				return;
			}

			if (apiName != null && localBlackList.containsKey(apiName)) {
				pipeResult.setErrorCode(ErrorCode.PLATFORM_SYSTEM_BLACKLIST);
				pipeResult.setMsg(new StringBuilder().append("apiName : ")
						.append(apiName).append(" in blacklist.").toString());

				return;
			}

			if (appkey != null && localBlackList.containsKey(appkey)) {
				pipeResult.setErrorCode(ErrorCode.PLATFORM_SYSTEM_BLACKLIST);
				pipeResult.setMsg(new StringBuilder().append("appkey : ")
						.append(appkey).append(" in blacklist.").toString());

				return;
			}
			if (isvName != null && localBlackList.containsKey(isvName)) {
				pipeResult.setErrorCode(ErrorCode.PLATFORM_SYSTEM_BLACKLIST);
				pipeResult.setMsg(new StringBuilder().append("isvName : ")
						.append(isvName).append(" in blacklist.").toString());

				return;
			}
			if(tag != null && localBlackList.containsKey(tag.toString())){
				pipeResult.setErrorCode(ErrorCode.PLATFORM_SYSTEM_BLACKLIST);
				pipeResult.setMsg(new StringBuilder().append("tag : ")
						.append(tag).append(" in blacklist.").toString());

				return;
			}
			if(nick != null && localBlackList.containsKey(nick)){
				pipeResult.setErrorCode(ErrorCode.PLATFORM_SYSTEM_BLACKLIST);
				pipeResult.setMsg(new StringBuilder().append("nick : ")
						.append(nick).append(" in blacklist.").toString());

				return;
			}
		}

	}

	@Override
	public boolean ignoreIt(TopPipeInput pipeInput, TopPipeResult pipeResult) {
		if (pipeResult.getErrorCode() != null) {
			return true;
		}
		return false;
	}
}
