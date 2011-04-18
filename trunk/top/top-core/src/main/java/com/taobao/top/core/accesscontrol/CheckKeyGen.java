package com.taobao.top.core.accesscontrol;

import org.apache.commons.lang.StringUtils;

public class CheckKeyGen {
	private static final String EMPTY_APP_STUB = "0";
	private static final Long EMPTY_API_STUB = 0L;

	protected static String genAppKey(String appKey) {
		return genAppApiKey(appKey, EMPTY_API_STUB);
	}
	
	protected static String genApiKey(Long apiId) {
		return genAppApiKey(EMPTY_APP_STUB, apiId);
	}
	
	
	protected static String genAppApiKey(String appKey, Long apiId) {
		if(StringUtils.isBlank(appKey) || apiId == null)
			throw new IllegalArgumentException("Wrong appKey or apiId");
		return appKey + ":" + apiId;
	}
}
