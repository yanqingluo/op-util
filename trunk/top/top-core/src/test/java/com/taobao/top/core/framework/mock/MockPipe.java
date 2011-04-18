package com.taobao.top.core.framework.mock;



import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.xbox.framework.IPipe;
import com.taobao.top.xbox.framework.IPipeContext;
import com.taobao.top.xbox.framework.PipeContextManager;

public class MockPipe implements IPipe<TopPipeInput,TopPipeResult>
{
	String pipeName;
	
	public MockPipe(String pipeName)
	{
		this.pipeName = pipeName;
	}
	
	@Override
	public void doPipe(TopPipeInput pipeInput,
			TopPipeResult pipeResult)
	{
		
		pipeResult.setSubCode(pipeName);
		
		if (pipeInput.getParameter("isbreak") != null)
		{
			pipeResult.setBreakPipeChain(true);
			return;
		}
		
		if (pipeInput.getParameter("errorcode") != null)
			pipeResult.setErrorCode(ErrorCode.PLATFORM_SYSTEM_ERROR);
				
	}

	@Override
	public IPipeContext getContext()
	{
		return PipeContextManager.getContext();
	}

	@Override
	public boolean ignoreIt(TopPipeInput pipeInput,
			TopPipeResult pipeResult)
	{
		return pipeInput.getRequest().getRequestURI().equals("/router/service");
	}

	@Override
	public String getPipeName()
	{
		// TODO Auto-generated method stub
		return pipeName;
	}

	@Override
	public void setPipeName(String pipeName)
	{
		this.pipeName = pipeName;
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
	
	/**
	 * 乱码fix1
	 * @return
	 */
	public boolean isDone(){
		return true;
	}
	
	/**
	 * 乱码fix2
	 * @return
	 */
	public boolean isCancelled()
	{
		return false;
	}
	
	/**
	 * 乱码fix3
	 * @return
	 */
	public TopPipeResult get()
	{
		return null;
	}

	@Override
	public boolean ignoreAsynMode(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		// TODO Auto-generated method stub
		return false;
	}

}
