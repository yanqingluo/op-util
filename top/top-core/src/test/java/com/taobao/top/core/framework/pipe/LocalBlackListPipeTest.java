package com.taobao.top.core.framework.pipe;


import static com.taobao.top.core.ProtocolConstants.P_APP_KEY;
import static com.taobao.top.core.ProtocolConstants.P_METHOD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.After;
import org.junit.Before;
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
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.tim.service.TIMServiceException;
import com.taobao.top.timwrapper.manager.TadgetManager;
/**
 * 
 * @author zhenzi
 *
 */
public class LocalBlackListPipeTest {
	private LocalBlackListPipe localBlackListPipe;
	private IAsynWriter<TopLog> asynLogWriter;
	private TopPipeManager topPipeManager;
	@Before
	public void setUp() throws Exception {
		localBlackListPipe = new LocalBlackListPipe();
		asynLogWriter = new AsynWriterTemplate();
		topPipeManager = new TopPipeManager();
		topPipeManager.setAsynLogWriter(asynLogWriter);
		
		topPipeManager.register(localBlackListPipe);
	}

	@After
	public void tearDown() throws Exception {
		topPipeManager.unregister(localBlackListPipe);
	}
	/*
	 * 测试doPipe
	 */
	@Test
	public void testDoPipe()throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		//ip在黑名单内
		{
			ConcurrentMap<String, String> blackList = new ConcurrentHashMap<String,String>();
			blackList.put("127.0.0.1", "exits");
			localBlackListPipe.setLocalBlackList(blackList);
			TopPipeInput input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
					topPipeManager).inputInstance(request, response, null);
			TopPipeResult result = topPipeManager.doPipes(input);
			assertEquals(ErrorCode.PLATFORM_SYSTEM_BLACKLIST,result.getErrorCode());
			assertTrue(result.getMsg().indexOf("ip") != -1);
		}
		//apiName在黑名单中
		{
			request.setParameter(P_METHOD, "taobao.time.get");
			ConcurrentMap<String, String> blackList = new ConcurrentHashMap<String,String>();
			blackList.put("taobao.time.get", "exits");
			localBlackListPipe.setLocalBlackList(blackList);
			TopPipeInput input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
					topPipeManager).inputInstance(request, response, null);
			TopPipeResult result = topPipeManager.doPipes(input);
			assertEquals(ErrorCode.PLATFORM_SYSTEM_BLACKLIST,result.getErrorCode());
			assertTrue(result.getMsg().indexOf("taobao.time.get") != -1);
		}
		//appkey在黑名单中
		{
			request.setParameter(P_APP_KEY,"4272");
			ConcurrentMap<String, String> blackList = new ConcurrentHashMap<String,String>();
			blackList.put("4272", "exits");
			localBlackListPipe.setLocalBlackList(blackList);
			TopPipeInput input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
					topPipeManager).inputInstance(request, response, null);
			TopPipeResult result = topPipeManager.doPipes(input);
			assertEquals(ErrorCode.PLATFORM_SYSTEM_BLACKLIST,result.getErrorCode());
			assertTrue(result.getMsg().indexOf("4272") != -1);
		}
		//isvName在黑名单中
		{
			TadgetManager targetManager = new TadgetManager(){
				@Override
				public TinyAppDO getTadgetByKey(String appKey) throws TIMServiceException {
					TinyAppDO appDO = new TinyAppDO();
					appDO.setAppStatus(TinyAppDO.APP_STATUS_RUNNING);
					appDO.setIsvName("zhenzi");
					return appDO;
				}
			};
				
			ConcurrentMap<String, String> blackList = new ConcurrentHashMap<String,String>();
			blackList.put("zhenzi", "exits");
			localBlackListPipe.setLocalBlackList(blackList);
			TopPipeInput input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
					topPipeManager).inputInstance(request, response, targetManager);
			TopPipeResult result = topPipeManager.doPipes(input);
			assertEquals(ErrorCode.PLATFORM_SYSTEM_BLACKLIST,result.getErrorCode());
			assertTrue(result.getMsg().indexOf("zhenzi") != -1);
		}
	}
}	
