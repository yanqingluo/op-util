/**
 * 
 */
package com.taobao.top.core.framework;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.framework.mock.MockPipe;
import com.taobao.top.xbox.framework.IPipe;

/**
 * @author fangweng
 *
 */
public class TopPipeManagerTest
{
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private TopPipeManager topPipeManager;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		topPipeManager = new TopPipeManager();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}

	/**
	 * Test method for {@link com.taobao.top.core.framework.TopPipeManager#doPipes(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	@Test
	public void testDoPipes()
	{
		IPipe pipe1 = new MockPipe("MockPipe1");
		IPipe pipe2 = new MockPipe("MockPipe2");
		IPipe pipe3 = new MockPipe("MockPipe3");
		topPipeManager.register(pipe1);
		topPipeManager.register(pipe2);
		topPipeManager.register(pipe3);
		topPipeManager.setAsynLogWriter(new com.taobao.top.log.asyn.AsynWriterTemplate());
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest("GET", "/router/rest");
			MockHttpServletResponse response = new MockHttpServletResponse();
			request.setParameter("errorcode", "PLATFORM_SYSTEM_ERROR");
			TopPipeInput input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
					topPipeManager).inputInstance(request, response, null);
			
			TopPipeResult result = topPipeManager.doPipes(input);
			Assert.assertEquals(result.getErrorCode(), ErrorCode.PLATFORM_SYSTEM_ERROR);
			Assert.assertEquals(result.getSubCode(), "MockPipe3");
			
			request = new MockHttpServletRequest("GET", "/router/rest");
			request.setParameter("isbreak", "true");
			input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
					topPipeManager).inputInstance(request, response, null);
			result = topPipeManager.doPipes(input);
			Assert.assertEquals(result.getSubCode(), "MockPipe1");
			
			request = new MockHttpServletRequest("GET", "/router/service");
			input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
					topPipeManager).inputInstance(request, response, null);
			result = topPipeManager.doPipes(input);
			Assert.assertNull(result.getSubCode());
		}
		finally
		{
			topPipeManager.unregister(pipe1);
			topPipeManager.unregister(pipe2);
			topPipeManager.unregister(pipe3);
		}

	}

	@Test
	public void testManagement()
	{
		try
		{
			IPipe pipe1 = new MockPipe("MockPipe1");
			IPipe pipe2 = new MockPipe("MockPipe2");
			topPipeManager.register(pipe1);
			topPipeManager.register(pipe2);
			
			
			org.junit.Assert.assertArrayEquals(new IPipe[]{pipe1,pipe2}, topPipeManager.getPipes());
			
			
			topPipeManager.unregister(pipe1);
			topPipeManager.unregister(pipe2);
			
			org.junit.Assert.assertArrayEquals(new IPipe[]{},  topPipeManager.getPipes());
			
		}
		catch(Exception ex)
		{
			Assert.assertTrue(false);
		}
		
	}

}
