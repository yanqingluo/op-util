package com.taobao.top.core.accesscontrol;

import java.util.Collection;

import org.apache.commons.logging.Log;

import com.alibaba.common.logging.LoggerFactory;

public class MockCallRecorder implements CallRecorder {
	private static final Log logger = LoggerFactory.getLog(MockCallRecorder.class);

	private int sent = 0;
	
	@Override
	public FlowControlResult recordAndCanCall(Collection<FlowRule> flowRules) {
		sent++;
		logger.info("record");
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			logger.error(e, e);
		}
		return FlowControlResult.NOT_FORBIDDEN;
	}
	
	public int getSent() {
		return sent;
	}

}
