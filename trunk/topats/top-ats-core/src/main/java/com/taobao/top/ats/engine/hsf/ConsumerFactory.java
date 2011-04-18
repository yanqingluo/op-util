package com.taobao.top.ats.engine.hsf;

import com.taobao.hsf.app.spring.util.SuperHSFSpringConsumerBeanTop;

/**
 * 消费者工厂。
 * 
 * @author carver.gu
 * @since 1.0, Aug 23, 2010
 */
public interface ConsumerFactory {

	public SuperHSFSpringConsumerBeanTop getConsumer(String interfaceName, String version) throws Exception;

}
