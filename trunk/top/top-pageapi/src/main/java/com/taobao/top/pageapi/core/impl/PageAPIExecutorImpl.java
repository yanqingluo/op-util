package com.taobao.top.pageapi.core.impl;

import static com.taobao.top.core.ProtocolConstants.P_SESSION;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.taobao.loadbalance.http.domain.HttpResultDO;
import com.taobao.top.core.Api;
import com.taobao.top.core.ApiConfigException;
import com.taobao.top.core.ApiFactory;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.pageapi.common.SignUtils;
import com.taobao.top.pageapi.common.EncryptException;
import com.taobao.top.pageapi.common.TopCookieManager;
import com.taobao.top.pageapi.core.IPageAPIExecutor;
import com.taobao.top.pageapi.services.ITBService;
import com.taobao.top.pageapi.services.TBServiceFactory;

/**
 * 处理流程页面API 访问请求，转发给Trade Management
 * 
 * @version 2009-08-16
* @author <a href="mailto:yueqian@taobao.com">yueqian</a>
 * 
 */
public class PageAPIExecutorImpl implements IPageAPIExecutor {
	
	private static final String REQUEST_PARA_TOP_SIGN = "top_sign";

	protected final transient Log log = LogFactory.getLog(this.getClass());
    
	TBServiceFactory serviceFactory;
	
	public TBServiceFactory getServiceFactory() {
		return serviceFactory;
	}

	public void setServiceFactory(TBServiceFactory serviceFactory) {
		this.serviceFactory = serviceFactory;
	}
	private ApiFactory apiFactory;
	
	private  String top_sign_key ;

	public String getTop_sign_key() {
		return top_sign_key;
	}

	public void setTop_sign_key(String top_sign_key) {
		this.top_sign_key = top_sign_key;
	}

	public void setApiFactory(ApiFactory apiFactory) {
		this.apiFactory = apiFactory;
	}
	
	 public ITBService getService(String requestUrl) {
		 
		 if (log.isTraceEnabled()){
			 log.trace("requestUrl is "+requestUrl);
		 }
		 String serviceKey = null;
		 if (requestUrl == null || "".equals(requestUrl)){
			 return null;
		 }
		 int i= requestUrl.indexOf("/");
		 if (i == 0 ){
			 int j = requestUrl.indexOf("/",i+1);
			 serviceKey = requestUrl.substring(i+1, j);
			 requestUrl = requestUrl.substring(j);
		 }else if (i>0) {
			 serviceKey = requestUrl.substring(0, i);
			 requestUrl = requestUrl.substring(i+1);			 
		 }
		 if (log.isTraceEnabled()){
			 log.trace("ServiceKey is "+serviceKey);
		 }
		 ITBService service = serviceFactory.getService(serviceKey);
		return service;
	}
	 
	 public String getString(Map parameterMap,String key) {
			String value = null ;
			if (parameterMap == null || key == null ){
				return value ;
			}
			Object valueObj = parameterMap.get(key);
			if (valueObj != null && valueObj instanceof String [] ){
				value = ((String[])valueObj)[0];
			}
			// just for parameter map 
			return value;
		}
	public void execute(TopPipeInput pipeInput, TopPageAPIResult result) {
		String requestUrl = null ;
		String apiName = pipeInput.getApiName();
		Api api = null;
		try {
			 api = apiFactory.getApi(apiName);
			requestUrl = api.getRequestURL();
		} catch (ApiConfigException e) {
			result.setErrorCode(ErrorCode.INVALID_METHOD);
			return;
		}
		HttpServletRequest req = pipeInput.getRequest();
		if (req == null ){
			result.setErrorCode(ErrorCode.SERVICE_CURRENTLY_UNAVAILABLE);
			return;
		}
		Map parameterMap = new HashMap();
		
		parameterMap.putAll(req.getParameterMap());
		String clientIP=getIpAddr(pipeInput.getRequest());
		Iterator keySetIt = parameterMap.keySet().iterator();
		TreeMap<String, String> apiparamsMap = new TreeMap<String, String>();
		while (keySetIt.hasNext()) {
			String name = (String) keySetIt.next();
			String value = getString(parameterMap,name);
			if (value != null) {
				apiparamsMap.put(name, value);
			}
		}
		try {
			String nick = TopCookieManager.getSessionNick(req);
			if(nick!=null)
				apiparamsMap.put("nick", nick);
			else{
				if(api.getProtocolMustParams().contains(P_SESSION))
					throw new CookieInvalideException("No TOP Cookie exist!");
			}
			apiparamsMap.put("client_ip", clientIP); 
			String topsign = SignUtils.sign(apiparamsMap, top_sign_key);
			String top_sign = URLEncoder.encode(topsign, "GBK");
			
			Map <String,String> topExtraMap = new HashMap<String,String>(); 
			topExtraMap.put(REQUEST_PARA_TOP_SIGN,top_sign);
			topExtraMap.put("client_ip",clientIP);
			if(nick!=null){
			nick = URLEncoder.encode(nick, "GBK");
			topExtraMap.put("nick", nick);
			}
			ITBService service = getService(requestUrl);
			HttpResultDO response = service.getResponse(req, topExtraMap);
			Map header = response.getHeaderMap();
			//获取错误码，用作打点记录@zhuyong.pt
			if(header.get("errorCode")!=null){
				//服务出错
				List<String> errorCodes = (List<String>) header.get("errorCode");
				if(errorCodes.size()>0){
					result.setIspErrorCode(errorCodes.get(0).toString());
					log.error("ISP error : "+header.get("errorMsg"));
				}else{
					result.setIspErrorCode("0");
				}
			}
			else{
				//服务正常
				result.setIspErrorCode("0");
			}
			String responsePage = response.getContent();
			result.setResponsePage(responsePage);
			TopCookieManager.refreshCookies(req,pipeInput.getResponse());
		} catch (EncryptException e) {
			if (log.isDebugEnabled()) {
				log.debug("Failed to get top_sign", e);
			}
		} catch (UnsupportedEncodingException e) {
			log.debug("Failed to encode top_sign", e);
		} catch (CookieInvalideException e) {
			log.debug("Failed to get session nick", e);
		}
	}
	/**
	 * 获取客户端IP地址
	 * @author zhuyong.pt
	 * 2010-10-11
	 * @param request
	 * @return
	 */
	 private String getIpAddr(HttpServletRequest request) {
	       String ip = request.getHeader("x-forwarded-for");
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	           ip = request.getHeader("Proxy-Client-IP");
	       }
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	           ip = request.getHeader("WL-Proxy-Client-IP");
	       }
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	           ip = request.getRemoteAddr();
	       }
	       return ip;
	   }
}

