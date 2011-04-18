package com.taobao.top.pageapi.framework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.common.TOPConfig;
import com.taobao.top.core.ErrorCodeTransform;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.core.framework.pipe.LogPipe;
import com.taobao.top.log.TopPageRequestLog;
import com.taobao.top.log.TopRequestLog;
import com.taobao.top.pageapi.core.impl.TopPageAPIResult;
import com.taobao.top.tim.domain.TinyAppDO;

/**
 * pageapi打点管道
 * @version 2010-10-11
 * @author zhuyong.pt
 *
 */
public class PageLogPipe extends LogPipe {
	private static final Log NORMAL_LOG = LogFactory.getLog(PageLogPipe.class);
	private ErrorCodeTransform errorCodeTransform;
	
	public void setErrorCodeTransform(ErrorCodeTransform errorCodeTransform) {
		this.errorCodeTransform = errorCodeTransform;
	}
	/**
	 * 记录requestlog
	 * @param topLog
	 */
	protected void requestLog(TopRequestLog topLog,TopPipeInput input,TopPipeResult result){
		//TOPRequestLog日志异步打点
		try
		{ 
			topLog.setApiName(input.getApiName());
			topLog.setAppKey(input.getAppKey());
			((TopPageRequestLog)topLog).setIspErrorCode(((TopPageAPIResult)result).getIspErrorCode());
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
			
//			if (topLog.getTimeStampQueue() != null && topLog.getTimeStampQueue().size() > 0)
//				topLog.setTransactionConsumeTime(topLog.getTimeStampQueue()
//					.get(topLog.getTimeStampQueue().size() -1));
//			else
//				topLog.setTransactionConsumeTime(-1);
			
			/*
			 * 打app tag
			 */
			TinyAppDO appDO = input.getAppDO();
			if(appDO != null && appDO.getAppTag() != null)
				topLog.setAppTag(appDO.getAppTag().intValue());	
			topLog.setId(getId(input));
			if (topLog.getVersion() != null && topLog.getVersion().equals("1.0")
					&& topLog.getSubErrorCode() == null)
			{
				topLog.setSubErrorCode(topLog.getErrorCode());
			}
			
			//临时为转换做处理
			topLog.setErrorCode(String.valueOf(errorCodeTransform.getSpecificCode
					(input.getApiName(), result.getCode())));
			
		}
		catch(Exception ex)
		{
			NORMAL_LOG.error("create TOPLOG error!",ex);
		}
	}
}
