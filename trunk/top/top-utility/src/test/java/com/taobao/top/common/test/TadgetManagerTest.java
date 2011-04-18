package com.taobao.top.common.test;


import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.top.tim.domain.FlowRuleV2DO;
import com.taobao.top.tim.service.SamService;
import com.taobao.top.tim.service.TIMServiceException;
import com.taobao.top.timwrapper.manager.TadgetManager;

public class TadgetManagerTest {
	private TadgetManager tadgetManager;
	@Before
	public void setUp() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-tim-test.xml");
		SamService samService = (SamService) ctx.getBean("samService");
		tadgetManager = new TadgetManager();
		tadgetManager.setTimService(samService);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/*
	 * 测试app是否有访问api的权限
	 */
	@Ignore
	public void testValidate()throws Exception{
		//如果传递的appkey或者method为null，则抛出异常
		{
			boolean result = false;
			try{
				result = tadgetManager.appCanAccessApi(null, tadgetManager.getValidApiByApiName("taobao.time.get"));
				fail();
			}catch(Exception e){
				assertTrue(e instanceof NullPointerException);
			}
			assertFalse(result);
		}
		//状态不对，不是线上状态、线上测试状态
		{
			boolean result = tadgetManager.appCanAccessApi(tadgetManager
					.getTadgetByKey("40786"), tadgetManager
					.getValidApiByApiName("taobao.time.get"));
			assertFalse(result);
		}
		//有权限调用api
		{
			boolean result = tadgetManager.appCanAccessApi(tadgetManager
					.getTadgetByKey("4272"), tadgetManager
					.getValidApiByApiName("taobao.time.get"));
			assertTrue(result);
		}
		//状态正确，没有权限调用api
		{
			boolean result = tadgetManager.appCanAccessApi(tadgetManager
					.getTadgetByKey("56698"), tadgetManager
					.getValidApiByApiName("taobao.time.get"));
			assertFalse(result);
		}
	}
}	
