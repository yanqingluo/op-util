package com.taobao.top.core.accesscontrol;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;

import com.alibaba.common.logging.LoggerFactory;
import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.domain.TinyAppDO;

/**
 * Record call into tair asynchronously.
 * This implementation uses a producer-consumer pattern to 
 * record these calls to avoid the record block the main process 
 * of handling input.
 * 
 * Note that in some suituations, the call will still be recored in 
 * sync way. see {@link #recordAndCanCall(TinyAppDO, TinyApiDO)}
 * @author huaisu
 *
 */
public class AsyncCallRecorder implements CallRecorder {
	private static final Log logger = LoggerFactory.getLog(AsyncCallRecorder.class);

	private CallRecorder callRecorder;
	private BlockingQueue<Collection<FlowRule>> callUnitQueue;
	
	public AsyncCallRecorder(CallRecorder callRecorder, int queueSize, int recordThreadCount) {
		this.callRecorder = callRecorder;
		this.callUnitQueue = new LinkedBlockingQueue<Collection<FlowRule>>(queueSize);
		ExecutorService service = Executors.newFixedThreadPool(recordThreadCount);
		for(int i = 0; i < recordThreadCount; i++) {
			service.submit(new Notify(callUnitQueue, callRecorder));
		}
	}

	/* 
	 * add the callUnit into queue, if queue is full, 
	 * record it to tair in sync way, it is a protection to the queue.
	 * but it should not happen too much in production.
	 * 
	 * Note that in the asynchronous way, the return is always true.
	 */
	@Override
	public FlowControlResult recordAndCanCall(Collection<FlowRule> flowRules) {
		if(callUnitQueue.offer(flowRules)) {
			logger.debug("add flowRules into notify queue succ");
			return FlowControlResult.NOT_FORBIDDEN;
		} else {
			logger.warn("queue full, too many call unit in queue not recorded, record sync");
			return callRecorder.recordAndCanCall(flowRules);
		}
	}

	private static class Notify implements Runnable {
		
		private final BlockingQueue<Collection<FlowRule>> callQueue;
		private final CallRecorder callRecorder;
		
		public Notify(BlockingQueue<Collection<FlowRule>> queue, CallRecorder callRecorder) {
			this.callQueue = queue;
			this.callRecorder = callRecorder;
		}
			
		@Override
		public void run() {
			while(true) {
				recordUnits();				
			}
			
		}

		private void recordUnits() {
			try {
				Collection<FlowRule> flowRules = callQueue.take();
				callRecorder.recordAndCanCall(flowRules);
				logger.debug("call recorded");
			} catch (InterruptedException e) {
				logger.error(e, e);
			}
		}
		
	}	
	
}
