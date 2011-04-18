package com.taobao.top.sm;

import java.util.Date;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.taobao.top.tim.domain.AuthDO;
import com.taobao.top.tim.service.SamService;

public class TimSessionManagerImplTest {
	
	private TimSessionManagerImpl timSessionManagerImpl;
	private String sessionId = "4test";
	private String sessionId_valid = "4test_valid";
	private String appKey = "test";
	
	@Before
	public void setUp() throws Exception{
		timSessionManagerImpl = new TimSessionManagerImpl();
		SamService timService = EasyMock.createMock(SamService.class);
		timService.getPersistentAuthBySessionKey(sessionId);
		
		EasyMock.expectLastCall().andStubReturn(getAuthDO(System.currentTimeMillis()+10000L));
		timService.getPersistentAuthBySessionKey(sessionId_valid);
		
		EasyMock.expectLastCall().andStubReturn(getAuthDO(System.currentTimeMillis()-100L));
		timSessionManagerImpl.setTimService(timService);
		EasyMock.replay(timService);
	}
	
	private AuthDO getAuthDO(long vaildData){
		AuthDO authDO = new AuthDO();
		authDO.setAppkey(appKey);
		Date validData = new Date();
		validData.setTime(vaildData);
		authDO.setValidDate(validData);
		return authDO;
	}
	
	@Test
	public void test_正常情况(){
		try {
			AuthDO authDO = timSessionManagerImpl.revertValidSession(sessionId, appKey);
			Assert.assertNotNull(authDO);
		} catch (SessionRevertException e) {
			Assert.fail();
		} catch (SessionGenerateException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void test_APP不匹配(){
		try {
			AuthDO authDO = timSessionManagerImpl.revertValidSession(sessionId, "unmatch");
			Assert.fail();
		} catch (SessionRevertException e) {
			if(!(e instanceof SessionOwnerUnmatchException)){
				Assert.fail();
			}
		} catch (SessionGenerateException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void test_过期(){
		try {
			AuthDO authDO = timSessionManagerImpl.revertValidSession(sessionId, "unmatch");
			Assert.fail();
		} catch (SessionRevertException e) {
			if(!(e instanceof SessionOwnerUnmatchException)){
				Assert.fail();
			}
		} catch (SessionGenerateException e) {
			Assert.fail();
		}
	}
	@Test
	public void test_checkSessionCreateByTop(){
		String userId = "123";
		String appKey = "123";
		boolean result = timSessionManagerImpl.checkSessionCreateByTop("41232840bd74c798f2ed64ecd8a10ab3dcf6", appKey);
		Assert.assertTrue(result);
	}
	

}
