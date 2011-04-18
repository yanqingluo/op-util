/**
 * 
 */
package com.taobao.top.core.framework.pipe;


import com.taobao.top.xbox.framework.IPipe;
import com.taobao.top.xbox.framework.IPipeContext;
import com.taobao.top.xbox.framework.IPipeInput;
import com.taobao.top.xbox.framework.IPipeResult;
import com.taobao.top.xbox.framework.PipeContextManager;

/**
 * 抽象管道基类
 * @author fangweng
 *
 */
public abstract class TopPipe<I extends IPipeInput, O extends IPipeResult>
		implements IPipe<I, O> {

	private String pipeName;
	private boolean asynMode = false;
	
	public TopPipe()
	{
		this.setPipeName(this.getClass().getSimpleName());
	}
	
	@Override
	public boolean isAsynMode() {
		return asynMode;
	}


	@Override
	public void setAsynMode(boolean asynMode) {
		this.asynMode = asynMode;
	} 
	
	@Override
	public IPipeContext getContext() {
		return PipeContextManager.getContext();
	}
	
	@Override
	public String getPipeName() {
		return pipeName;
	}
	
	@Override
	public void setPipeName(String pipeName) {
		this.pipeName = pipeName;
	}
	
	@Override
	public boolean ignoreIt(I pipeInput,
			O pipeResult) {
		return false;
	}
	
	@Override
	public boolean ignoreAsynMode(I pipeInput, O pipeResult) {
		// TODO Auto-generated method stub
		return false;
	}
}
