package com.taobao.console;

import java.util.List;

import com.taobao.top.common.PeakValueMonitor;
import com.taobao.top.console.client.domain.Command;
import com.taobao.top.console.client.handler.ConsoleClientPipeInput;
import com.taobao.top.console.client.handler.ConsoleClientPipeResult;
import com.taobao.top.console.client.service.BaseConsoleClientPipe;
import com.taobao.top.console.client.util.ConsoleClientErrorCode;

public class PeakPipe extends BaseConsoleClientPipe {
	
	private PeakValueMonitor peakValueMonitor;
	
	public PeakValueMonitor getPeakValueMonitor() {
		return peakValueMonitor;
	}

	public void setPeakValueMonitor(PeakValueMonitor peakValueMonitor) {
		this.peakValueMonitor = peakValueMonitor;
	}

	@Override
	public void doPipe(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		String command = input.getString("command");
		if(command.equals(Command.START)){
			peakValueMonitor.changeStatus(true);
		}else if(command.equals(Command.STOP)){
			peakValueMonitor.changeStatus(false);
		}else{
			try{
				List<String> datas = peakValueMonitor.get();
				result.setBlack(datas);
			}catch (Exception e) {
				result.setErrorCode(ConsoleClientErrorCode.ILLEGAL_THREAD_STATE);
			}
		}
	}


	@Override
	public boolean ignoreIt(ConsoleClientPipeInput input,
			ConsoleClientPipeResult result) {
		String direct = input.getRequest().getParameter("direct");
		if ("PeakCenter".equalsIgnoreCase(direct)) {
			if (result.isSuccess())
				return false;
		}
		return true;
	}

	@Override
	public boolean isAsynMode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAsynMode(boolean mode) {
		// TODO Auto-generated method stub
		
	}
	
}
