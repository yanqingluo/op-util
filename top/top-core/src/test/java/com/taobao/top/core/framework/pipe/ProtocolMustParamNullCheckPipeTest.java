package com.taobao.top.core.framework.pipe;


import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.framework.AbstractTopPipeManager;
import com.taobao.top.core.framework.TopPipeData;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeManager;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.log.TopLog;
import com.taobao.top.log.asyn.AsynWriterTemplate;
import com.taobao.top.log.asyn.IAsynWriter;

import static com.taobao.top.core.ProtocolConstants.*;
import static org.junit.Assert.*;
/**
 * 
 * @author zhenzi 
 *
 */
public class ProtocolMustParamNullCheckPipeTest {
	private ProtocolMustParamNullCheckPipe nullCheckPipe ;
	private TopPipeManager topPipeManager;
	private IAsynWriter<TopLog> asynLogWriter;
	@Before
	public void setUp() throws Exception {
		nullCheckPipe = new ProtocolMustParamNullCheckPipe();
		topPipeManager = new TopPipeManager();
		topPipeManager.register(nullCheckPipe);
		asynLogWriter = new AsynWriterTemplate<TopLog>();
		topPipeManager.setAsynLogWriter(asynLogWriter);
	}

	@After
	public void tearDown() throws Exception {
		topPipeManager.unregister(nullCheckPipe);
//		asynLogWriter.stop();
	}
	@Test
	public void testDoPipe(){
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		//method is null
		TopPipeInput input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
				topPipeManager).inputInstance(request, response, null);
		TopPipeResult result = topPipeManager.doPipes(input);
		assertEquals(ErrorCode.MISSING_METHOD,result.getErrorCode());
		request.setParameter(P_METHOD, "");
		input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
				topPipeManager).inputInstance(request, response, null);
		result = topPipeManager.doPipes(input);
		assertEquals(ErrorCode.MISSING_METHOD,result.getErrorCode());
		
		//appkey is null
		request.setParameter(P_METHOD, "taobao.time.get");
		input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
				topPipeManager).inputInstance(request, response, null);
		result = topPipeManager.doPipes(input);
		assertEquals(ErrorCode.MISSING_APP_KEY,result.getErrorCode());
		
		//timestamp is null
		request.setParameter(P_APP_KEY, "4272");
		input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
				topPipeManager).inputInstance(request, response, null);
		result = topPipeManager.doPipes(input);
		assertEquals(ErrorCode.MISSING_TIMESTAMP,result.getErrorCode());
		
		//v is null
		request.setParameter(P_TIMESTAMP, "2010-02-02");
		input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
				topPipeManager).inputInstance(request, response, null);
		result = topPipeManager.doPipes(input);
		assertEquals(ErrorCode.MISSING_VERSION,result.getErrorCode());
		
		//sign is null
		request.setParameter(P_VERSION, "2.0");
		input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
				topPipeManager).inputInstance(request, response, null);
		result = topPipeManager.doPipes(input);
		assertEquals(ErrorCode.MISSING_SIGNATURE,result.getErrorCode());
		
	}
	@Test
	public void testT(){
		
	}
}
