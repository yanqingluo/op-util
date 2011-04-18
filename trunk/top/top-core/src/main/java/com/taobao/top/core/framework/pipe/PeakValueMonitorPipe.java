package com.taobao.top.core.framework.pipe;


import com.taobao.top.common.PeakValueMonitor;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
/**
 * 用于峰值监控
 * @author zhenzi
 *
 */
public class PeakValueMonitorPipe extends TopPipe<TopPipeInput, TopPipeResult> {
	private PeakValueMonitor peakValueMonitor;
	
	public PeakValueMonitor getPeakValueMonitor() {
		return peakValueMonitor;
	}

	public void setPeakValueMonitor(PeakValueMonitor peakValueMonitor) {
		this.peakValueMonitor = peakValueMonitor;
	}

	@Override
	public void doPipe(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		String servletPath = pipeInput.getRequest().getServletPath();
		if(servletPath != null && servletPath.startsWith("/container")){
			peakValueMonitor.add("container");
		}else{
			peakValueMonitor.add("TIP");
		}
		String apiName = pipeInput.getApiName();
		if(apiName != null){
			peakValueMonitor.add(apiName);
		}
	}	
}
