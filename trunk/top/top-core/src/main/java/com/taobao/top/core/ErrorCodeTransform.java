/**
 * 
 */
package com.taobao.top.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.service.SamService;
import com.taobao.top.tim.service.TIMServiceException;
import com.taobao.top.tim.util.ExPropertyName;

/**
 * @author lin.wangl
 * 
 */
public class ErrorCodeTransform {
	private static final transient Log log = LogFactory
			.getLog(ErrorCodeTransform.class);

	private SamService samService;

	public void setSamService(SamService samService) {
		this.samService = samService;
	}

	public int getSpecificCode(String api, int defaultCode) throws TopException {
		// not operation error code
		if (defaultCode != 15 && (defaultCode <= 200 || defaultCode >= 10000)) {
			return defaultCode;
		}
		TinyApiDO apiDO = null;
		try {
			apiDO = samService.getValidApiByApiName(api);
		} catch (TIMServiceException e) {
			throw new TopException(e);
		}
		if (apiDO == null) {
			return defaultCode;
		}
		String dbErrorCode = apiDO.getPropertyValue(ExPropertyName.ERROR_CODE);
		if (dbErrorCode == null) {
			return defaultCode;
		}
		try {
			return Integer.valueOf(dbErrorCode);
		} catch (Exception e) {// NFE
			log.error("" + dbErrorCode, e);
			return defaultCode;
		}
	}

}
