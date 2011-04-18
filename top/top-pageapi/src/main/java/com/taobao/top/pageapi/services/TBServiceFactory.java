package com.taobao.top.pageapi.services;

import java.util.HashMap;

/**
 * 业务方提供service factory
 * 
 * @version 2009-08-16
 * @author <a href="mailto:yueqian@taobao.com">yueqian</a>
 * 
 */
public class TBServiceFactory {

	private  HashMap<String, ITBService> servicesMap  = null;

	public  HashMap<String, ITBService> getServicesMap() {
		return servicesMap;
	}
	public TBServiceFactory() {
	}
    public void setServicesMap(HashMap<String, ITBService> map){
    	servicesMap = map ;
    }
	
	public ITBService getService(String key) {
		ITBService service =  servicesMap.get(key);
		return service;
	}
}
