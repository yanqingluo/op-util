package com.taobao.top.pageapi.services;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.taobao.loadbalance.http.domain.HttpResultDO;
/**
 * 封装提供流程页面API的业务服务
 * 
 * @version 2009-08-16
 * @author <a href="mailto:yueqian@taobao.com">yueqian</a>
 * 
 */
public interface ITBService {
    
	String getResponsePage(HttpServletRequest req,Map <String,String> paraMap);
	HttpResultDO getResponse( HttpServletRequest req,Map <String,String> paramMap); 
}
