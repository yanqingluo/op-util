package com.taobao.top.ats.engine.hsf;

import org.apache.commons.lang.StringUtils;

import com.taobao.hsf.app.spring.util.SuperHSFSpringConsumerBeanTop;
import com.taobao.top.ats.engine.util.AbstractSharedResources;

/**
 * 消费者工厂默认实现。
 * 
 * @author carver.gu
 * @since 1.0, Aug 23, 2010
 */
public class DefaultConsumerFactory extends AbstractSharedResources<SuperHSFSpringConsumerBeanTop> implements ConsumerFactory {

	private static final String KEY_SPLIT = " ";

	public SuperHSFSpringConsumerBeanTop getConsumer(String interfaceName, String version)
			throws Exception {
		if (StringUtils.isBlank(interfaceName) || StringUtils.isBlank(version)) {
			throw new IllegalArgumentException(String.format("remote service %s:%s unavailable!", interfaceName, version));
		}
		String key = interfaceName + KEY_SPLIT + version;
		return this.getResource(key);
	}

	protected SuperHSFSpringConsumerBeanTop initResource(String key) throws Exception {
		String[] args = StringUtils.split(key, KEY_SPLIT);
		if (args.length != 2) {
			throw new IllegalArgumentException(key + " is invalid!");
		}

		String interfaceName = args[0];
		String version = args[1];
		SuperHSFSpringConsumerBeanTop consumer = new SuperHSFSpringConsumerBeanTop();
		consumer.setInterfaceName(interfaceName);
		consumer.setVersion(version);
		consumer.init();
		return consumer;
	}

}
