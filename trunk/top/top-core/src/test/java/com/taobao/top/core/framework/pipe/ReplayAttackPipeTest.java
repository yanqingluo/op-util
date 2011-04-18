package com.taobao.top.core.framework.pipe;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.taobao.top.common.cache.aop.ReplayAttackLocalCacheImpl;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.ProtocolConstants;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;

public class ReplayAttackPipeTest {
	
	private ReplayAttackLocalCacheImpl cache;
	private ReplayAttackPipe pipe;
	private long scope = 30*60*1000L;
	private TopPipeInput pipeInput = null;
	private TopPipeResult pipeResult = null;
	MockHttpServletRequest request = new MockHttpServletRequest();
	MockHttpServletResponse response = new MockHttpServletResponse();
	
	@Before
	public void setUp(){
		pipe = new ReplayAttackPipe();
		cache = new ReplayAttackLocalCacheImpl();
		cache.setExpiry(10*1000L);
		pipe.setCache(cache);
		pipe.setScope(scope);
		pipeInput = new TopPipeInput(request, response, null);
		pipeResult = new TopPipeResult();	
	}
	@After
	public void doAfter(){
		cache.destroy();
	}
	
	/**
	 * 测试时间戳超过预设的差值范围（正向超过），将被访问禁止，并返回errorCode
	 */
	@Test
	public void testDoPipeTimeStamp1(){
		
		long time = System.currentTimeMillis()+scope+1000L;
		
		request.setParameter(ProtocolConstants.P_TIMESTAMP,String.valueOf(time));
		pipe.doPipe(pipeInput, pipeResult);
		Assert.assertEquals(ErrorCode.INVALID_TIMESTAMP,pipeResult.getErrorCode());
	
	}
	/**
	 * 测试时间戳超过预设的差值范围（负向超过），将被访问禁止，并返回errorCode
	 */
	@Test
	public void testDoPipeTimeStamp2(){
		
		long time = System.currentTimeMillis()-scope-1000L;
		request.setParameter(ProtocolConstants.P_TIMESTAMP, String.valueOf(time));
		pipe.doPipe(pipeInput, pipeResult);
		Assert.assertEquals(pipeResult.getErrorCode(), ErrorCode.INVALID_TIMESTAMP);
	}
	/**
	 * 测试时间戳在预设的差值范围内，访问将通过，无errorCode
	 */
	@Test
	public void testDoPipeTimeStamp3(){
		long time = System.currentTimeMillis()-scope+1000L;
		request.setParameter(ProtocolConstants.P_TIMESTAMP, String.valueOf(time));
		pipe.doPipe(pipeInput, pipeResult);
		Assert.assertNull(pipeResult.getErrorCode());
	}
	/**
	 * 测试同一个请求（参数和sign都一样），第一次通过，第二次被认为是重放攻击，访问被拒绝
	 */
	@Test
	public void testDoPipeCache1(){
		long time = System.currentTimeMillis()-scope+1000L;
		request.setParameter(ProtocolConstants.P_TIMESTAMP, String.valueOf(time));
		request.setParameter(ProtocolConstants.P_SIGN, "sign");
		request.setParameter(ProtocolConstants.P_APP_KEY, "1234");
		pipe.doPipe(pipeInput, pipeResult);
		Assert.assertNull(pipeResult.getErrorCode());
		pipeResult = new TopPipeResult();	
		pipe.doPipe(pipeInput, pipeResult);
		Assert.assertEquals(pipeResult.getErrorCode(), ErrorCode.INVALID_TIMESTAMP);
		
	}
	/**
	 * 测试三个周期之后，第一个周期放入的将被清理
	 */
	@Test
	public void testDoPipeCache2(){
		long expiry = 1*1000L;
		cache = new ReplayAttackLocalCacheImpl(500,expiry);
		pipe.setCache(cache);
		Assert.assertEquals(0, cache.size());
		long time = System.currentTimeMillis();
		request.setParameter(ProtocolConstants.P_TIMESTAMP, String.valueOf(time));
		request.setParameter(ProtocolConstants.P_SIGN, "sign");
		request.setParameter(ProtocolConstants.P_APP_KEY, "1234");
		pipe.doPipe(pipeInput, pipeResult);
		Assert.assertNull(pipeResult.getErrorCode());
		Assert.assertEquals(1, cache.size());
		try {
			Thread.sleep(expiry*3);
		} catch (InterruptedException e) {
			
		}
		Assert.assertEquals(0, cache.size());
		
	}
	/**
	 * 测试如果第一个周期放的数目超过了限制会立马开始触发清理事件，保护内存
	 */
	@Test
	public void testDoPipeCache3(){
		long expiry = 1*1000L;
		cache = new ReplayAttackLocalCacheImpl(30,expiry);
		pipe.setCache(cache);
		long time = System.currentTimeMillis();
		request.setParameter(ProtocolConstants.P_TIMESTAMP, String.valueOf(time));
		
		request.setParameter(ProtocolConstants.P_APP_KEY, "1234");
		for (int i = 0; i < 31; i++) {
			pipeInput.setSign("sign"+i);
			pipeResult = new TopPipeResult();	
			pipe.doPipe(pipeInput, pipeResult);
			Assert.assertNull(pipeResult.getErrorCode());
		}
		try {
			Thread.sleep(expiry);
		} catch (InterruptedException e) {
			
		}
		
		Assert.assertEquals(0, cache.size());
		
	}
	/**
	 * 测试一直放一直放，内存不会溢出
	 */
	@Test
	public void testDoPipeCache4(){
		long expiry = 1*1000L;
		cache = new ReplayAttackLocalCacheImpl(50,expiry);
		pipe.setCache(cache);
		long time = System.currentTimeMillis();
		request.setParameter(ProtocolConstants.P_TIMESTAMP, String.valueOf(time));
		
		request.setParameter(ProtocolConstants.P_APP_KEY, "1234");
		for (int i = 0; i < 1000; i++) {
			pipeInput.setSign("sign"+i);
			pipeResult = new TopPipeResult();	
			pipe.doPipe(pipeInput, pipeResult);
			Assert.assertNull(pipeResult.getErrorCode());
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				
			}
		}
		try {
			Thread.sleep(expiry);
		} catch (InterruptedException e) {
			
		}
		System.out.println(cache.getIngoreTimes().get());
		Assert.assertEquals(true, cache.size()<50);
	}
}
