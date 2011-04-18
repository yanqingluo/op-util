package com.taobao.top.pageapi.framework;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.framework.pipe.TopPipe;
import com.taobao.top.pageapi.common.TopCookieManager;
import com.taobao.top.pageapi.core.impl.CookieInvalideException;
import com.taobao.top.pageapi.core.impl.TopPageAPIResult;

public class PageApiUrlCheckPipe extends TopPipe<TopPagePipeInput, TopPageAPIResult> {
	private static final transient Log log = LogFactory.getLog(PageApiUrlCheckPipe.class);
	@Override
	public void doPipe(TopPagePipeInput pipeInput, 
			TopPageAPIResult pipeResult) {
		
		try {
			TopCookieManager.validateCookies(pipeInput.getRequest());
		} catch (CookieInvalideException e) {
			log.error(e,e);
			pipeResult.setErrorCode(ErrorCode.INVALID_SESSION);
			pipeResult.setMsg("session 不合法");
			return;
		}	
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
