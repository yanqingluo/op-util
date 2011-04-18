package com.taobao.top.pageapi.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.taobao.loadbalance.http.RelayTunnel;
import com.taobao.loadbalance.http.domain.HttpResultDO;
import com.taobao.loadbalance.http.domain.RewriteConfigPerRequest;
/**
 * 封装提供流程页面API的业务服务
 * 
 * @version 2009-08-16
 * @author <a href="mailto:yueqian@taobao.com">yueqian</a>
 * 
 */
public class DefaultTBService implements ITBService {
 
	private RelayTunnel  relayTunnel ;
	
	public RelayTunnel getRelayTunnel() {
		return relayTunnel;
	}

	public void setRelayTunnel(RelayTunnel relayTunnel) {
		this.relayTunnel = relayTunnel;
	}

	public DefaultTBService (RelayTunnel relayTunnel ){
		this.relayTunnel = relayTunnel;
	}
	
	public DefaultTBService (){
	}
	
	public String getResponsePage(HttpServletRequest req,Map <String,String> paramMap){
		String responsePage=relayTunnel.getResponsePage(req, paramMap);
		return responsePage;
	}
	public HttpResultDO getResponse( HttpServletRequest req,Map <String,String> paramMap){
//		RewriteConfigPerRequest rewriteConfig = new RewriteConfigPerRequest();
//		Map<String, Set<String>> addParamList = new HashMap<String, Set<String>>();
//		for(Map.Entry<String, String> entry : paramMap.entrySet()){
//			Set<String> valueList = addParamList.get(entry.getKey());
//			if(valueList == null){
//				valueList = new HashSet<String>();
//				addParamList.put(entry.getKey(), valueList);
//			}
//			valueList.add(entry.getValue());		
//		}
//		rewriteConfig.setRequestRewriteParamList(addParamList);
//		HttpResultDO result = relayTunnel.getResponsePageWithHeader(req, rewriteConfig);
//		
		//使用httpbalance提供的方法获得response的header和content @zhuyong.pt 2010-10-11
		HttpResultDO result = relayTunnel.getResponsePageWithHeader(req, paramMap);
		return result;
	}
}
