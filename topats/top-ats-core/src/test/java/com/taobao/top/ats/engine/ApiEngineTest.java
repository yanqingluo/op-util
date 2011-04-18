package com.taobao.top.ats.engine;

import org.junit.Test;

import com.taobao.top.ats.AtsTestBase;

public class ApiEngineTest extends AtsTestBase {

	@Test
	public void invokeApi() {
		ApiRequest req = new ApiRequest();
		req.setInterfaceName("com.taobao.api.user.UserService");
		req.setInterfaceMethod("doUserQuery");
		req.setInterfaceVersion("1.0.0.daily");
		apiEngine.invokeApi(req);
	}

}
