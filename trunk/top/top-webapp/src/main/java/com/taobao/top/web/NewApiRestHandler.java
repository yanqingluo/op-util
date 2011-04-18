package com.taobao.top.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.HttpRequestHandler;
import com.taobao.top.core.framework.AbstractTopPipeManager;
import com.taobao.top.core.framework.TopPipeData;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.timwrapper.manager.TadgetManager;
import com.taobao.top.xbox.framework.IPipe;
import com.taobao.top.xbox.framework.IPipeManager;

/**
 * 新的处理逻辑类，通过抽象AbstractTopPipeManager topPipeManager，达到复用的目的
 * @author zhenzi
 *
 */
public class NewApiRestHandler  implements HttpRequestHandler {
	private static final Log logger = LogFactory.getLog(NewApiRestHandler.class);
	
	private IPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>> topPipeManager = null;	
	private TadgetManager tadgetManager;

	public void setTadgetManager(TadgetManager tadgetManager) {
		this.tadgetManager = tadgetManager;
	}

	public void setTopPipeManager(IPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>> topPipeManager) {
		this.topPipeManager = topPipeManager;
	}
	private List<IPipe<? super TopPipeInput, ? super TopPipeResult>> pipeList = null;
	
	public void setPipeList(List<IPipe<? super TopPipeInput, ? super TopPipeResult>> pipeList) {
		this.pipeList = pipeList;
	}
	
	public void init(){
		topPipeManager.addAll(pipeList);
	}
	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		TopPipeInput input = ((AbstractTopPipeManager<TopPipeInput, TopPipeResult,TopPipeData<TopPipeInput,TopPipeResult>>)
				topPipeManager).inputInstance(request, response, tadgetManager);
		
		topPipeManager.doPipes(input);
	}

}
