package com.taobao.top.core.accesscontrol;

import java.util.HashMap;
import java.util.Map;

import com.taobao.top.core.CallLimitManager;

public class MockCallLimitManager implements CallLimitManager {

	private Map<String, Integer> newCallTimes = new HashMap<String, Integer>();
	
	@Override
	public int incrNewFlowTimes(String key) throws Exception {
		Integer callTime = newCallTimes.get(key);
		callTime = callTime == null ? 0 : callTime;
		callTime++;
		newCallTimes.put(key, callTime);
		return callTime;
	}
	
}
