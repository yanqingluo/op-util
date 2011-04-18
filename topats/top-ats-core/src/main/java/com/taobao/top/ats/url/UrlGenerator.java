package com.taobao.top.ats.url;

import com.taobao.top.ats.AtsException;
import com.taobao.top.ats.domain.AtsTaskDO;

/**
 * ats download url generator.
 * 
 * @author jeck.xie 2010-11-16
 */ 
public interface UrlGenerator {
	
	public String getAtsDownloadUrl(AtsTaskDO taskDO) throws AtsException;
	
	public String getTokenFilePath(String token) throws AtsException;

}
