package com.taobao.top.core.framework;


import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
/**
 * 
 * @author zhenzi
 *
 */
public class TopPipeUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	/*
	 * 测试是否忽略
	 */
	@Test
	public void testisIgnore(){
		//servletPath 没有设置
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		TopPipeInput input = new TopPipeInput(request,response,null);
		
		boolean result = TopPipeUtil.isIgnore(input);
		assertEquals(false,result);
		//servletPath = /router/rest
		request.setServletPath("/router/rest");
		result = TopPipeUtil.isIgnore(input);
		assertEquals(false,result);
		
		//servletPath = /router/page
		request.setServletPath("/router/page");
		request.setParameter("pageUrl", "true");
		request.setContentType("multipart/form-data");
		result = TopPipeUtil.isIgnore(input);
		assertTrue(result);
	}
	//测试isInProcessPage
	@Test
	public void testIsInProcessPage()throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		Class clazz = TopPipeUtil.class;
		Method method = clazz.getDeclaredMethod("isInProcessPage", String.class,HttpServletRequest.class);
		method.setAccessible(true);
		//pageUrl = true
		boolean result = (Boolean)method.invoke(TopPipeUtil.class, "true",request);
		assertTrue(result);
		
		//pageurl = false && method and contentType 没有设置
		result = (Boolean)method.invoke(TopPipeUtil.class, "false",request);
		assertTrue(!result);
		
		//pageurl = false && method = get or contentType设置不对
		request.setMethod("get");
		result = (Boolean)method.invoke(TopPipeUtil.class, "false",request);
		assertTrue(!result);
		
		//pageUrl = false && method = POST and contentType=multipart/form-data
		request.setMethod("post");
		request.setContentType("multipart/form-data");
		result = (Boolean)method.invoke(TopPipeUtil.class, "false",request);
		assertTrue(result);
	}
}
