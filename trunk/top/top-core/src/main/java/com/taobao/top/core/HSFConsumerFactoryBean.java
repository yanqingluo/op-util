/**
 * 
 */
package com.taobao.top.core;

import org.springframework.beans.factory.FactoryBean;

/**
 * @version 2009-2-10
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public class HSFConsumerFactoryBean implements FactoryBean {

	private ConsumerFactory consumerFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	public Object getObject() throws Exception {
		if (consumerFactory == null) {
			consumerFactory = new DefaultConsumerFactory();
		}
		return consumerFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	public Class getObjectType() {
		return ConsumerFactory.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return true;
	}

}
