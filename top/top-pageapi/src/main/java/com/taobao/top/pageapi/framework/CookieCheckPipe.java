package com.taobao.top.pageapi.framework;

import static com.taobao.top.core.ProtocolConstants.P_SESSION;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.core.Api;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeUtil;
import com.taobao.top.core.framework.pipe.TopPipe;
import com.taobao.top.pageapi.common.TopCookieManager;
import com.taobao.top.pageapi.core.impl.CookieInvalideException;
import com.taobao.top.pageapi.core.impl.TopPageAPIResult;
import com.taobao.util.CollectionUtil;

/**
 * 验证cookie的是否是TOPcookie 目前只用到页面流程化api中
 * @author zhenzi
 *
 */
public class CookieCheckPipe extends TopPipe<TopPagePipeInput,TopPageAPIResult> {
	private static final Log log = LogFactory.getLog(CookieCheckPipe.class);
	private static final String MULTIPART = "multipart/";

	private static final String POST = "post";
	@Override
	public void doPipe(TopPagePipeInput pipeInput, TopPageAPIResult pipeResult) {
		
		if(StringUtils.isEmpty(pipeInput.getApiName())){
			pipeResult.setErrorCode(ErrorCode.MISSING_METHOD);
			return;
		}
		//cookie校验
		try {
			TopCookieManager.validateCookies(pipeInput.getRequest());
		} catch (CookieInvalideException e) {
			log.error(e,e);
			pipeResult.setErrorCode(ErrorCode.INVALID_SESSION);
			pipeResult.setMsg("session 不合法");
			return;
		}
		//top auth校验
		if(isInProcessPage(pipeInput)){
			try {
				TopCookieManager.validateTOPAuthCookie(pipeInput.getRequest());
			} catch (CookieInvalideException e) {
				log.error(e,e);
				pipeResult.setErrorCode(ErrorCode.INVALID_SESSION);
				pipeResult.setMsg("TOP auth cookie 不合法");
				return;
			}
		}
	}
	private boolean isInProcessPage(TopPipeInput pipeInput) {
		boolean isInProcess = false;
		String pageUrl = pipeInput.getString("pageUrl", true);
		if (pageUrl != null && "true".equals(pageUrl)) {
			isInProcess = true;
			if (log.isDebugEnabled()){
				log.debug("It is a in-proces page,so only need to check cooki and call limitation");
			}
			return isInProcess;
		}
		HttpServletRequest request = pipeInput.getRequest();
		String method = request.getMethod();
		String contentType = request.getContentType();
		if ((method != null && method.toLowerCase().equals(POST)
				&& contentType != null && contentType.toLowerCase().startsWith(
				MULTIPART))) {
			isInProcess = true;
			return isInProcess;
		}
		return isInProcess;
	}
	@Override
	public boolean ignoreIt(TopPagePipeInput pipeInput,	TopPageAPIResult pipeResult) {
		if(pipeResult.getErrorCode() != null || TopPipeUtil.isIgnore(pipeInput)){
			return true;
		}
		/*
		 * 如果此api必须传递sessionKey，则不能忽略
		 */
		Api api = pipeInput.getApi();
		if(api == null){//如果没有请求的api对应的Api对象，则忽略此管道
			return true;
		}
		List<String> protocolParam = null; 
		protocolParam = api.getProtocolMustParams();
		if(!CollectionUtil.isEmpty(protocolParam)){
			if(protocolParam.contains(P_SESSION)){
				return false;
			}
		}
		
		return true;
	}
}
