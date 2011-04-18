/**
 * 
 */
package com.taobao.top.core.framework.pipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.common.TOPConfig;
import com.taobao.top.common.TOPConstants;
import com.taobao.top.core.Api;
import com.taobao.top.core.ApiApplicationParameter;
import com.taobao.top.core.ErrorCodeTransform;
import com.taobao.top.core.ProtocolConstants;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.log.TopLog;
import com.taobao.top.log.TopRequestLog;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.xbox.commonlog.LogManager;
import com.taobao.top.xbox.framework.Pipe;
import com.taobao.util.CollectionUtil;

/**
 * 负责输出日志的Pipe，放在最后一个Pipe
 * 
 * @author fangweng
 * 
 */
@Pipe(name = "logPipe", version = "1.0")
public class LogPipe extends TopPipe<TopPipeInput, TopPipeResult> {
	private static final Log NORMAL_LOG = LogFactory.getLog(LogPipe.class);

	private boolean needLogTradeApi = false;
	public boolean isNeedLogTradeApi() {
		return needLogTradeApi;
	}

	public void setNeedLogTradeApi(boolean needLogTradeApi) {
		this.needLogTradeApi = needLogTradeApi;
	}

	private String ruleName = "request";

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	private ErrorCodeTransform errorCodeTransform;

	public void setErrorCodeTransform(ErrorCodeTransform errorCodeTransform) {
		this.errorCodeTransform = errorCodeTransform;
	}

	public ErrorCodeTransform getErrorCodeTransform() {
		return errorCodeTransform;
	}

	@Override
	public void doPipe(TopPipeInput pipeInput,
			TopPipeResult pipeResult)
	{
		
		TopLog topLog = (TopLog)getContext().getAttachment(TOPConstants.TOP_LOG);
		if(topLog instanceof TopRequestLog){
			requestLog((TopRequestLog)topLog,pipeInput,pipeResult);
		}
		if(needLogTradeApi){ 
			LogManager.getInstance().logData(getInputData(pipeInput,pipeResult), getOutputData(pipeInput,pipeResult), ruleName);
		}		
	}

	private Map<String, String> getInputData(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		String[] paramNames = pipeInput.getParameterNames().toArray(
				ArrayUtils.EMPTY_STRING_ARRAY);
		Map<String, String> param = null;
		if (paramNames != null && paramNames.length > 0) {
			param = new HashMap<String, String>();
			for (String string : paramNames) {
				param.put(string, pipeInput.getString(string, true));
			}
			param.put("formmethod", pipeInput.getRequest().getMethod());
		}
		return param;
	}

	private Map<String, String> getOutputData(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("result", String.valueOf(pipeResult.isSuccess()));
		result.put("errorCode", String.valueOf(pipeResult.getCode()));
		result.put("subErrorCode", String.valueOf(pipeResult.getSubCode()));
		result.put("msg", String.valueOf(pipeResult.getMsg()));
		result.put("subMsg", String.valueOf(pipeResult.getSubMsg()));
		return result;
	}

	/**
	 * 记录requestlog
	 * 
	 * @param topLog
	 */
	protected void requestLog(TopRequestLog topLog, TopPipeInput input,
			TopPipeResult result) {
		// TOPRequestLog日志异步打点
		try {
			topLog.setApiName(input.getApiName());
			topLog.setAppKey(input.getAppKey());
			topLog.setErrorCode(String.valueOf(result.getCode()));
			topLog.setFormat(input.getFormat());
			topLog.setLocalIp(TOPConfig.getInstance().getLocalAddress());
			topLog.setNick(input.getSessionNick());
			topLog.setPartnerId(input.getPartnerId());
			topLog.setReadBytes(input.getRequest().getContentLength());
			topLog.setRemoteIp(input.getAppIp());
			topLog.setSubErrorCode(result.getSubCode());
			topLog.setVersion(input.getVersion());
			topLog.setSignMethod(input.getSignMethod());
			topLog.setServiceConsumeTime(result.getExecuteTime());
			topLog.setResponseMappingTime(result.getResponseMappingTime());

			// if (topLog.getTimeStampQueue() != null &&
			// topLog.getTimeStampQueue().size() > 0)
			// topLog.setTransactionConsumeTime(topLog.getTimeStampQueue()
			// .get(topLog.getTimeStampQueue().size() -1));
			// else
			// topLog.setTransactionConsumeTime(-1);

			/*
			 * 打app tag
			 */
			TinyAppDO appDO = input.getAppDO();
			if (appDO != null && appDO.getAppTag() != null)
				topLog.setAppTag(appDO.getAppTag().intValue());
			topLog.setId(getId(input));
			if (topLog.getVersion() != null
					&& topLog.getVersion().equals("1.0")
					&& topLog.getSubErrorCode() == null) {
				topLog.setSubErrorCode(topLog.getErrorCode());
			}

			// 临时为转换做处理
			topLog.setErrorCode(String.valueOf(errorCodeTransform
					.getSpecificCode(input.getApiName(), result.getCode())));

		} catch (Exception ex) {
			NORMAL_LOG.error("create TOPLOG error!", ex);
			try{
				Map<String,String> keyValues = queryStringToMap(input.getRequest().getQueryString());
				if(keyValues != null){
					String appkey = keyValues.get(ProtocolConstants.P_APP_KEY);
					if(appkey == null){
						appkey = keyValues.get(ProtocolConstants.P_API_KEY);
					}
					topLog.setAppKey(appkey);
					topLog.setVersion(keyValues.get(ProtocolConstants.P_VERSION));
					topLog.setApiName(keyValues.get(ProtocolConstants.P_METHOD));
					topLog.setFormat(keyValues.get(ProtocolConstants.P_FORMAT));
				}
				topLog.setErrorCode(String.valueOf(result.getCode()));
				topLog.setSubErrorCode(result.getSubCode());
				topLog.setServiceConsumeTime(result.getExecuteTime());
				topLog.setResponseMappingTime(result.getResponseMappingTime());
			}catch(Exception e){
				NORMAL_LOG.error("create TOPLOG error!", e);
			}
		}
	}
	/**
	 * 把URL中的参数转换成key=value的形式
	 * @param queryString
	 * @return
	 */
	private Map<String,String> queryStringToMap(String queryString){
		if(StringUtils.isEmpty(queryString)){
			return null;
		}
		String[] keyValues = queryString.split("&");
		Map<String,String> keyValueMap = new HashMap<String,String>(keyValues.length);
		for (String keyValue : keyValues) {
			String[] temp = keyValue.split("=",2);
			if(temp.length == 2){
				keyValueMap.put(temp[0], temp[1]);
			}		
		}
		return keyValueMap;
	}
	/**
	 * 取得危险字段,iid,sid,tid
	 * 
	 * @param input
	 * @return
	 */
	protected String getId(TopPipeInput input) {
		Api api = input.getApi();
		if (api != null) {
			List<ApiApplicationParameter> mustParam = api
					.getApplicationMustParams();
			if (!CollectionUtil.isEmpty(mustParam)) {
				for (ApiApplicationParameter apiApplicationParameter : mustParam) {
					String name = apiApplicationParameter.getName();
					if ("num_iid".equals(name) || "num_iids".equals(name) || "tid".equals(name)
							|| "sid".equals(name)) {
						return input.getString(name, true);
					}
				}
			}
			List<ApiApplicationParameter> optParam = api
					.getApplicationOptionalParams();
			if (!CollectionUtil.isEmpty(optParam)) {
				for (ApiApplicationParameter apiApplicationParameter : optParam) {
					String name = apiApplicationParameter.getName();
					if ("num_iid".equals(name) || "num_iids".equals(name) || "tid".equals(name)
							|| "sid".equals(name)) {
						return input.getString(name, true);
					}
				}
			}
		}
		return null;
	}
}
