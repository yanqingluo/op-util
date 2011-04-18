/**
 * 
 */
package com.taobao.top.core.framework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationListener;

/**
 * @author fangweng
 *
 */
public class TopAsynPipeEventListener<I extends TopPipeInput, R extends TopPipeResult,D extends TopPipeData<I,R>>  implements ContinuationListener{

	private static final Log logger = LogFactory.getLog(TopAsynPipeEventListener.class);
	AbstractTopPipeManager<I,R,D> topPipeManager;
	D data;
	
	
	public TopAsynPipeEventListener(AbstractTopPipeManager<I,R,D> topPipeManager,D data)
	{
		this.topPipeManager = topPipeManager;
		this.data = data;
	}
	
	@Override
	public void onComplete(Continuation continuation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTimeout(Continuation continuation) {
		// TODO Auto-generated method stub
		logger.error("Asyn Web Request time out.");
		topPipeManager.removeAsynPipeResult(data);
		continuation.complete();
	}

}
