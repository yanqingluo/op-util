package com.taobao.top.core.accesscontrol;

import java.util.HashSet;
import java.util.Set;

import com.taobao.top.privilege.BlackListManager;

public class MockBlackListManager implements BlackListManager {
	Set<String> newFlowkeys = new HashSet<String>();
	
	@Override
	public Integer getBanEndTimeOfNewFlow(String keyId) {
		return newFlowkeys.contains(keyId) ? (int) (System.currentTimeMillis() / 1000) + 200
				: null;
	}
	
	@Override
	public void addNewFlowBlack(String key, int banDuration) {
		newFlowkeys.add(key);
	}

}
