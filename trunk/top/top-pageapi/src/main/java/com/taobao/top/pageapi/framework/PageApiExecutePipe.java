package com.taobao.top.pageapi.framework;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.common.TopPipeConfig;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.framework.pipe.TopPipe;
import com.taobao.top.pageapi.common.SignUtils;
import com.taobao.top.pageapi.core.IPageAPIExecutor;
import com.taobao.top.pageapi.core.impl.TopPageAPIResult;
import com.taobao.top.pageapi.services.WhiteListDomainService;

public class PageApiExecutePipe extends
		TopPipe<TopPagePipeInput, TopPageAPIResult> {
	private static final Log log = LogFactory.getLog(PageApiExecutePipe.class);

	private IPageAPIExecutor apiExecutor;

	private String URLSignKey;
	
	private WhiteListDomainService whiteListDomainService;

	//private PaywaySignCheckUtil paywaySignCheckUtil;

	public void setURLSignKey(String signKey) {
		this.URLSignKey = signKey;
	}

	public void setApiExecutor(IPageAPIExecutor apiExecutor) {
		this.apiExecutor = apiExecutor;
	}

	@Override
	public void doPipe(TopPagePipeInput pipeInput, TopPageAPIResult pipeResult) {
		if (pipeResult.isSuccess()) {
			apiExecutor.execute(pipeInput, (TopPageAPIResult) pipeResult);
		} else {
			callbackExtShop(pipeInput.getString("callback_url", true),
					pipeResult.getErrorCode(), pipeInput.getResponse());
		}
	}

	private void callbackExtShop(String callbackUrl, ErrorCode errorCode,
			HttpServletResponse response) {
		if (callbackUrl != null && !StringUtils.isEmpty(callbackUrl)) {
			if (errorCode != null
					&& errorCode.equals(ErrorCode.INVALID_SESSION)) {
				try {
					String cbPage = URLDecoder.decode(callbackUrl, "UTF-8");
					if(TopPipeConfig.getInstance().isCheckDomain()) {
					    //开关开启的情况下，检查域名是否在白名单中
					    if(!whiteListDomainService.isInWhiteList(cbPage)){
					        if(log.isErrorEnabled()) {
					            log.error(cbPage + " is not in white list!");    
					        }
					        return;
					    }
					}
					if (cbPage.indexOf("?") != -1) {
						cbPage = cbPage + "&result_code=" + errorCode.getCode();
					} else {
						cbPage = cbPage + "?result_code=" + errorCode.getCode();
					}
					String signUrl = SignUtils.signURL(cbPage, URLSignKey);
					response.sendRedirect(signUrl);
				} catch (IOException e) {
					log.error("failed to redirect to target url ", e);
				}
			}
		}

	}

    public void setWhiteListDomainService(WhiteListDomainService whiteListDomainService) {
        this.whiteListDomainService = whiteListDomainService;
    }

    public WhiteListDomainService getWhiteListDomainService() {
        return whiteListDomainService;
    }

	/*public void setPaywaySignCheckUtil(PaywaySignCheckUtil paywaySignCheckUtil) {
		this.paywaySignCheckUtil = paywaySignCheckUtil;
	}

	public PaywaySignCheckUtil getPaywaySignCheckUtil() {
		return paywaySignCheckUtil;
	}*/
}
