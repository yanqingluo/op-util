package com.taobao.top.privilege;

/**
 * 白名单操作接口
 * @author moling
 * @since 1.0, 2009-9-2
 */
public interface WhiteListManager {
	/**
	 * appKey是否在app白名单中
	 * @param appKey
	 * @return
	 */
	boolean isAppWhite(String appKey);
	
	/**
	 * 调用者ip是否在ip白名单中
	 * @param ip
	 * @return
	 */
	boolean isIpWhite(String ip);
}
