package com.taobao.top.core;

import com.taobao.hsf.app.spring.util.SuperHSFSpringConsumerBeanTop;

/**
 * consumer工厂
 * 
 * @version 2009-2-12
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public interface ConsumerFactory {

	SuperHSFSpringConsumerBeanTop getConsumer(String interfaceName,
			String version) throws Exception;

}
