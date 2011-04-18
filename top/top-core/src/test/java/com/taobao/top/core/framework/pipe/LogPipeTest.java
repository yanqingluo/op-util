/**
 * 
 */
package com.taobao.top.core.framework.pipe;


import static com.taobao.top.core.ProtocolConstants.P_APP_KEY;
import static com.taobao.top.core.ProtocolConstants.P_FORMAT;
import static com.taobao.top.core.ProtocolConstants.P_METHOD;
import static com.taobao.top.core.ProtocolConstants.P_SESSION;
import static com.taobao.top.core.ProtocolConstants.P_TIMESTAMP;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.taobao.top.core.framework.AbstractTopPipeManager;
import com.taobao.top.core.framework.TopPipeData;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeManager;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.log.TopLog;
import com.taobao.top.log.asyn.AsynWriterTemplate;
import com.taobao.top.log.asyn.IAsynWriter;
import com.taobao.top.xbox.framework.IPipeInput;
import com.taobao.top.xbox.framework.IPipeResult;

/**
 * @author fangweng
 *
 */
public class LogPipeTest
{
	private static LogPipe logPipe;
	private static IAsynWriter<TopLog> asynLogWriter;
	static TopPipeManager topPipeManager;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		logPipe = new LogPipe();
		asynLogWriter = new AsynWriterTemplate();
		asynLogWriter.start();
		topPipeManager = new TopPipeManager();
		topPipeManager.setAsynLogWriter(asynLogWriter);
		
		topPipeManager.register(logPipe);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		topPipeManager.unregister(logPipe);
		asynLogWriter.stop();
	}

	/**
	 * Test method for {@link com.taobao.top.core.framework.pipe.LogPipe#doPipe(IPipeInput, IPipeResult)}.
	 */
	@Test
	public void testDoPipe()
	{
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/router/rest");
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.setParameter(P_METHOD, "taobao.item.get");
		request.setParameter(P_APP_KEY, "12369");
		request.setParameter(P_FORMAT, "xml");
		request.setParameter(P_SESSION, "session");
		request.setParameter(P_TIMESTAMP, "200902312");
		
		TopPipeInput input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
				topPipeManager).inputInstance(request, response, null);
		
		topPipeManager.doPipes(input);
	}
	
	@Test
	public void testLogTradeApi() {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/router/rest");
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.setParameter(P_METHOD, "taobao.trade.open.add");
		request.setParameter(P_APP_KEY, "12369");
		request.setParameter(P_FORMAT, "xml");
		request.setParameter(P_SESSION, "session");
		request.setParameter(P_TIMESTAMP, "200902312");
		
		TopPipeInput input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
				topPipeManager).inputInstance(request, response, null);
		
		topPipeManager.doPipes(input);

	}

}
