/**
 * 
 */
package com.taobao.top.core;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.hsf.app.spring.util.SuperHSFSpringConsumerBeanTop;
import com.taobao.top.common.resource.AbstractSharedResources;


/**
 * @version 2009-2-10
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */

public class DefaultConsumerFactory extends
		AbstractSharedResources<SuperHSFSpringConsumerBeanTop> implements
		ConsumerFactory {

	private static final Log log = LogFactory
			.getLog(DefaultConsumerFactory.class);

	private static final String KEY_SPLIT = " ";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taobao.top.core.hsf.ConsumerFactory#getConsumer(java.lang.String,
	 * java.lang.String)
	 */
	public SuperHSFSpringConsumerBeanTop getConsumer(String interfaceName,
			String version) throws Exception {
		if (isBlank(interfaceName, version)) {
			throw new IllegalArgumentException(
					String.format("Remote service %s:%s unavailable!",
							interfaceName, version));
		}
		String key = interfaceName + KEY_SPLIT + version;
		SuperHSFSpringConsumerBeanTop consumer = this.getResource(key);
		return consumer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taobao.remoting.util.SharedResourcesInit#keyedInitResource(java.lang
	 * .String)
	 */
	@Override
	protected SuperHSFSpringConsumerBeanTop initResource(String key)
			throws Exception {
		String[] args = StringUtils.split(key, KEY_SPLIT);
		if (args.length != 2) {
			throw new IllegalArgumentException(key + " is not a valid key!");
		}
		String interfaceName = args[0];
		String version = args[1];
		// 初始化consumer
		SuperHSFSpringConsumerBeanTop consumer = new SuperHSFSpringConsumerBeanTop();
		consumer.setInterfaceName(interfaceName);
		consumer.setVersion(version);
		consumer.init();
		int tryTimes = 20;
		for (int i = 0; i < tryTimes; i++) {
			log.warn("try init consumer: " + key + " " + i + " time");
			boolean ready = ServiceUtil.isServiceAddressReadyForTop(consumer);
			if (ready) {
				break;
			}
			Thread.sleep(100);
		}
		return consumer;
	}
	private boolean isBlank(String... params) {
		boolean result = false;
		int length;
		
		for (String str : params) {
			if (null == str || (length = str.length()) == 0) {
				result = true;
				break;
			} else {
				boolean flag = false;
				
				for (int i = 0; i < length; i++) {
		            if (!Character.isWhitespace(str.charAt(i))) {
		            	flag = true;
		            }
		        }
				
				if (!flag) {
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
}
