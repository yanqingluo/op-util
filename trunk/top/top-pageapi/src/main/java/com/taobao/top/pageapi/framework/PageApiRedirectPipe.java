package com.taobao.top.pageapi.framework;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.common.TopPipeConfig;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.framework.pipe.TopPipe;
import com.taobao.top.pageapi.common.EncryptException;
import com.taobao.top.pageapi.common.SignUtils;
import com.taobao.top.pageapi.common.TopCookieManager;
import com.taobao.top.pageapi.core.impl.CookieInvalideException;
import com.taobao.top.pageapi.core.impl.TopPageAPIResult;
import com.taobao.top.pageapi.services.ITBService;
import com.taobao.top.pageapi.services.TBServiceFactory;
import com.taobao.top.pageapi.services.WhiteListDomainService;
/**
 * 页面流程化，直接跳转模块
 * @author zhenzi
 *
 */
public class PageApiRedirectPipe extends TopPipe<TopPagePipeInput, TopPageAPIResult> {
	private static final transient Log log = LogFactory.getLog(PageApiRedirectPipe.class); 
	private static final String STR_CONTENT_TYPE = "text/html;charset=UTF-8";
	private String top_sign_key ;
	
    private String URLSignKey;
    
    private WhiteListDomainService whiteListDomainService;
    
    //private PaywaySignCheckUtil paywaySignCheckUtil;
    
	public String getURLSignKey() {
		return URLSignKey;
	}
	
	public void setURLSignKey(String signKey) {
		this.URLSignKey = signKey;
	}
	
	public String getTop_sign_key() {
		return top_sign_key;
	}
	
	public void setTop_sign_key(String top_sigh_key) {
		this.top_sign_key = top_sigh_key;
	}

	private TBServiceFactory tbServiceFactory = null;

	public void setTbServiceFactory(TBServiceFactory tbServiceFactory) {
		this.tbServiceFactory = tbServiceFactory;
	}

	@Override
	public void doPipe(TopPagePipeInput pipeInput, 
			TopPageAPIResult pipeResult) {
		
		if(pipeResult.isSuccess()){
			redirectRequest(pipeInput.getRequest(), pipeInput.getResponse());
		}else{
			//校验签名
			/*if(!getPaywaySignCheckUtil().checkSign(pipeInput)) {
				return;
			}*/
			callbackExtShop(pipeInput.getString("callback_url", true),pipeResult.getErrorCode(),pipeInput.getResponse());
		}
	}

	private void redirectRequest (HttpServletRequest request, HttpServletResponse response) {
    	// 获取top_sign
		Map<String, String> apiparamsMap = new HashMap<String, String>();
		String top_sign = null;
		try {
			String queryString  = request.getQueryString();
			String input_charset = request.getParameter("_input_charset");
			if (input_charset == null ){
				input_charset = "GBK";
			}
			TreeMap<String,String> queryMap = getQueryMap(queryString,input_charset);
			String nick = TopCookieManager.getSessionNick(request);
			queryMap.put("nick", nick);
			String topsign = SignUtils.sign(queryMap,top_sign_key);
			top_sign = URLEncoder.encode(topsign,input_charset);
			nick = URLEncoder.encode(nick,input_charset);
		    apiparamsMap.put("top_sign",top_sign);
		    apiparamsMap.put("nick", nick);
		    response.setContentType(STR_CONTENT_TYPE);
		    // 获取响应页面
		    // 如果是错误页面，则直接显示
		    String requestUrl = request.getPathInfo();
		    if (requestUrl != null && requestUrl.endsWith("topErrMsg.htm")) {
				String msg = request.getParameter("msg");
				msg = msg.replace(">", "");
				msg = msg.replace("<", "");
				response.getWriter().write(msg);
				return;
		    }
		// 根据请求URL 找到服务页面
		ITBService service = getService(requestUrl);
		if (service == null) {
			log.error("failed to find ITBService to get a response");
			return;
		}
		String responsePage = service.getResponsePage(request, apiparamsMap);
		TopCookieManager.refreshCookies(request, response);
		response.getWriter().write(responsePage);	
		} catch (EncryptException e) {
			if (log.isErrorEnabled()){
				log.error("Failed to get top_sign",e);
			}
		} catch (UnsupportedEncodingException e) {
			log.error("Failed to encode sign",e);
		} catch (IOException e) {
			log.error("Add response failed",e);
		} catch (CookieInvalideException e) {
			log.error("Add response failed",e);
		}
    }
	private void callbackExtShop(String callbackUrl,ErrorCode errorCode,HttpServletResponse response) {
		if (callbackUrl != null && !StringUtils.isEmpty(callbackUrl)){
			if (errorCode != null && errorCode.equals(ErrorCode.INVALID_SESSION)){
				try {
					String cbPage = URLDecoder.decode(callbackUrl, "UTF-8");
					if(TopPipeConfig.getInstance().isCheckDomain()) {
                        //开关开启的情况下，检查域名是否在白名单中
                        if(!getWhiteListDomainService().isInWhiteList(cbPage)){
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
					String signUrl = SignUtils.signURL(cbPage,URLSignKey);
					response.sendRedirect(signUrl);
				} catch (IOException e) {
					log.error("failed to redirect to target url ", e);
				}
			}
		}
		
	}
	private ITBService getService(String requestUrl) {
		 if (log.isTraceEnabled()){
			 log.trace("requestUrl is "+requestUrl);
		 }
		 // 通过PathInfo 截取该请求的内部业务服务，约定 tm 访问连接为tm/xxx.htm,
		 String serviceKey = null;
		 if (requestUrl == null || "".equals(requestUrl)){
			 return null;
		 }
		 int i= requestUrl.indexOf("/");
		 if (i == 0 ){
			 int j = requestUrl.indexOf("/",i+1);
			 serviceKey = requestUrl.substring(i+1, j);
		 }else if (i>0) {
			 serviceKey = requestUrl.substring(0, i);
		 }
		 if (log.isTraceEnabled()){
			 log.trace("ServiceKey is "+serviceKey);
		 }
		 ITBService service = tbServiceFactory.getService(serviceKey);
		return service;
	}
	
	/**
	 * 解析queryString 参数为Map
	 * @param queryString
	 * @return
	 */
	private TreeMap<String, String> getQueryMap(String queryString,String input_charset) {
		TreeMap<String, String> map = new TreeMap<String, String>();
		String[] params = queryString.split("\\&");
		try {
			for (String p : params) {				
				int equalPos = p.indexOf("=");
				if (equalPos == -1)
					continue; // 不正确的参数会被丢弃
				String pname = p.substring(0, equalPos);
				String pvalue = p.substring(equalPos + 1);
				map.put(pname,URLDecoder.decode(pvalue, input_charset));// TM 使用GBK 编码
			}
		} catch (UnsupportedEncodingException e) {
			log.error("URL参数编码[" + queryString + "]转换出错:" + e);
		}
		return map;
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
