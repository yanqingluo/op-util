package com.taobao.top.common;
/**
 * top的有些配置是写在topPipeConfig中的
 * 但是动态修改配置的时候只能修改bean里的属性
 * 而topPipeConfig 是通过static的方式，所有控制台拿不到
 * 于是这边引入一个manager，manager注入spring
 * 这样就可以在spring里拿到这个对象，然后拿到topPipeConfig
 * @author zhudi
 *
 */
public class TopPipeConfigManager {
	
	private TopPipeConfig config = TopPipeConfig.getInstance();

	public TopPipeConfig getConfig() {
		return config;
	}

	public void setConfig(TopPipeConfig config) {
		this.config = config;
	}
	
	

}
