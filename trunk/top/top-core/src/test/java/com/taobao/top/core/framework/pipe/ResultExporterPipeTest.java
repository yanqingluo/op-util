package com.taobao.top.core.framework.pipe;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.taobao.top.core.DefaultApiExporter;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeManager;
import com.taobao.top.log.TopLog;
import com.taobao.top.log.asyn.AsynWriterTemplate;
import com.taobao.top.log.asyn.IAsynWriter;
/**
 * 
 * @author zhenzi
 *
 */
public class ResultExporterPipeTest {
	private ResultExporterPipe exporterPipe;
	private TopPipeManager topPipeManager;
	private IAsynWriter<TopLog> asynLogWriter;
	@Before
	public void setUp() throws Exception {
		exporterPipe = new ResultExporterPipe();
		exporterPipe.setApiExporter(new DefaultApiExporter());
		topPipeManager = new TopPipeManager();
		topPipeManager.register(exporterPipe);
		asynLogWriter = new AsynWriterTemplate<TopLog>();
		topPipeManager.setAsynLogWriter(asynLogWriter);
		
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testDoPipe(){
		//contentType = xml
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			TopPipeInput input = new TopPipeInput(request,response,null);
			request.addParameter("format", "xml");
			topPipeManager.doPipes(input);
			assertEquals("text/xml;charset=UTF-8",response.getContentType());
		}
		//contentType = json
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			request.addParameter("format", "json");
			TopPipeInput input = new TopPipeInput(request,response,null);
			topPipeManager.doPipes(input);
			assertEquals("text/javascript;charset=UTF-8",response.getContentType());
		}
		//contentType=str
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			request.addParameter("format", "str");
			TopPipeInput input = new TopPipeInput(request,response,null);
			topPipeManager.doPipes(input);
			assertEquals("text/html;charset=UTF-8",response.getContentType());
		}
	}
}
