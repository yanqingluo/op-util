package com.taobao.top.privilege;



import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

/**
 * 
 * @author zhenzi
 *  TODO： 非常奇怪的类路径问题，说找不到com.taobao.remoting.IoEventListener$ConnectionLifecycleListener
 * 但是，类明显是在的。
 */

@Ignore
public class BlackListManagerImplTest {
	private BlackListManagerImpl blackListM;
	@Before
	public void setUp() throws Exception {
		blackListM = new BlackListManagerImpl();
		blackListM.setCacheClient(new com.taobao.top.common.MockDuplicateCacheClient());

	}

	@After
	public void tearDown() throws Exception {
	}

}
