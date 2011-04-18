package com.taobao.top.pageapi.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import com.taobao.loadbalance.http.PoolingRelayTunnel;
import com.taobao.loadbalance.http.RelayConverter;
import com.taobao.loadbalance.http.RelayTunnel;
import com.taobao.loadbalance.http.domain.HttpResultDO;

public class TMDirectTBService implements ITBService {
	private static final Logger logger = Logger.getLogger(PoolingRelayTunnel.class);
 
	private RelayTunnel  relayTunnel ;
	
	private RelayConverter relayConverter;
	
	
	public RelayConverter getRelayConverter() {
		return relayConverter;
	}

	public void setRelayConverter(RelayConverter relayConverter) {
		this.relayConverter = relayConverter;
	}

	public RelayTunnel getRelayTunnel() {
		return relayTunnel;
	}

	public void setRelayTunnel(RelayTunnel relayTunnel) {
		this.relayTunnel = relayTunnel;
	}

	public TMDirectTBService (RelayTunnel relayTunnel ){
		this.relayTunnel = relayTunnel;
	}
	
	
	public TMDirectTBService (){
	}
	
	private HttpClient  client = new HttpClient();
	
	 /* RelayTunnel实现方法。
	 * 失败重新选址重试
	 */
	public String getResponsePage(HttpServletRequest req,
			Map<String, String> keyvaluePairs) {
		org.apache.commons.httpclient.Cookie[] cookies = null;
		if (req.getCookies() != null) {
			cookies = HttpClientUtil.convert(req.getCookies());
		}
		List<Header> headers = HttpClientUtil.getHeaders(req);
		StringBuilder page = new StringBuilder();
		String address = "";
		String targetAddress = relayConverter.convert(req, address);

		HttpMethodBase httpMethod = getHttpMethod(req, targetAddress,
				keyvaluePairs);
       /* for (Header h : headers) {
			httpMethod.setRequestHeader(h);
		}*/
       
		try {
			client.executeMethod(httpMethod);
			page = HttpClientUtil.dump(httpMethod);
		} catch (Exception e) {
			logger.error("Failed to address: ", e);
			page = new StringBuilder("Failed." + e);
		} finally {
			httpMethod.releaseConnection();
		}
        //检查是否重定向
        int statuscode = httpMethod.getStatusCode();
        if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY) ||
            (statuscode == HttpStatus.SC_MOVED_PERMANENTLY) ||
            (statuscode == HttpStatus.SC_SEE_OTHER) ||(statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
           //读取新的URL地址
            Header header = httpMethod.getResponseHeader("location");
            if (header != null) {
                String newuri = header.getValue();
                if ((newuri == null) || (newuri.equals("")))
                    newuri = "/";
                GetMethod redirect = new GetMethod(newuri);
                try {
					client.executeMethod(redirect);
					page = HttpClientUtil.dump(redirect);
				} catch (HttpException e) {
					logger.error("Failed to address: ", e);
				} catch (IOException e) {
					logger.error("Failed to address: ", e);
				} finally {
					 redirect.releaseConnection();
				}
            } else
            	page.append("Invalid redirect");
        }
		page.append("\n<!-- 该页面来自服务器：").append(targetAddress).append("-->");
		return page.toString();
	}

/**
 * TODO: 
 * 1. 当method是Get时，要不要将keyvaluePairs以query string的形式缀在url之后？
 * 2. 当method是Post时，需要将targetAddress的query string中的参数加入RequestBody吗?
 *    httpClient会不会自动做这个事情？
 * 
 * @param req
 * @param targetAddress
 * @param keyvaluePairs
 * @return
 */
    private HttpMethodBase getHttpMethod(HttpServletRequest req, String targetAddress, Map<String, String> keyvaluePairs) {
	if ("POST".equals(req.getMethod())) {
		PostMethod post = new PostMethod(targetAddress);
		//post = new UTF8PostMethod(targetAddress); 
		if (keyvaluePairs != null) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> e : keyvaluePairs.entrySet()) {
				pairs.add(new NameValuePair(e.getKey(), e.getValue()));
			}
			post.setRequestBody(pairs.toArray(new NameValuePair[pairs.size()]));
		}
		return post;
	} else {//if("GET".equals(req.getMethod())){
		GetMethod get = new GetMethod(targetAddress);
		return get;
	}
}

@Override
public HttpResultDO getResponse(HttpServletRequest req,
		Map<String, String> paramMap) {
	// TODO Auto-generated method stub
	return null;
}

}
