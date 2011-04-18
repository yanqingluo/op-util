package com.taobao.top.notify.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.taobao.common.tair.DataEntry;
import com.taobao.common.tair.Result;
import com.taobao.common.tair.ResultCode;
import com.taobao.common.tair.TairManager;
import com.taobao.top.notify.TestBase;
import com.taobao.top.notify.cache.impl.MockTairManager;
import com.taobao.top.notify.util.NotifyHelper;

/**
 * 
 * @author moling
 * @since 1.0, 2009-12-17
 */
@SuppressWarnings("unchecked")
public class TairServiceTest extends TestBase {

	private TairManager tairManager = (TairManager) ctx.getBean("tairManager");
	private int nameSpace = 177;

	@Test
	public void auhorizeMessage() {
		ResultCode code = tairManager.put(nameSpace, Long.valueOf(111111L), MockTairManager.getSellerAuthorize());
		assertTrue(code.isSuccess());

		Result<DataEntry> data = tairManager.get(nameSpace, Long.valueOf(111111L));
		assertTrue(data.isSuccess());

		ArrayList<String> list = (ArrayList<String>) data.getValue().getValue();

		assertEquals(2, list.size());
		assertTrue(list.contains("appKey1"));
		assertTrue(list.contains("appKey2"));

		code = tairManager.delete(nameSpace, Long.valueOf(111111L));
		assertTrue(code.isSuccess());

		data = tairManager.get(nameSpace, Long.valueOf(111111L));
		assertEquals(ResultCode.DATANOTEXSITS.getCode(), data.getRc().getCode());
	}

	@Test
	public void subscribeMessage() {
		ResultCode code = tairManager.put(nameSpace, NotifyHelper.getCacheAppKey("appKey1"), MockTairManager.getAppKey1Subscribe());
		assertTrue(code.isSuccess());

		Result<DataEntry> data = tairManager.get(nameSpace, NotifyHelper.getCacheAppKey("appKey1"));
		assertTrue(data.isSuccess());

		String sub = (String) data.getValue().getValue();

		assertTrue("1:1;2:0;3:0".equals(sub));

		code = tairManager.delete(nameSpace, NotifyHelper.getCacheAppKey("appKey1"));
		assertTrue(code.isSuccess());

		data = tairManager.get(nameSpace, NotifyHelper.getCacheAppKey("appKey1"));
		assertEquals(ResultCode.DATANOTEXSITS.getCode(), data.getRc().getCode());
	}

}
