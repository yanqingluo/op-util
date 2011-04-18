/**
 * 
 */
package com.taobao.top.config.spring;

import static org.junit.Assert.*;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.top.core.Api;
import com.taobao.top.core.ApiConfigException;
import com.taobao.top.core.ApiFactory;

/**
 * @version 2009-2-19
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 */
public class ApiFactoryBeanTest {
	private static ApiFactory af;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String[] paths = { "spring-ApiFactoryBeanTest.xml" };
		ApplicationContext springContext = new ClassPathXmlApplicationContext(paths);
		af = (ApiFactory) springContext.getBean("apiFactory");
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetApi() throws ApiConfigException {
		Api api = af.getApi("taobao.sns.message.sendSysMsg");
		assertNotNull(api);
		assertEquals("1.0.0.daily", api.getHsfInterfaceVersion());
		assertEquals("com.taobao.core.biz.top.BBJTopService", api.getHsfInterfaceName());
	}

}
