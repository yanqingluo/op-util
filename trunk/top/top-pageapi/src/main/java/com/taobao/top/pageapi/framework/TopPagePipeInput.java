package com.taobao.top.pageapi.framework;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.taobao.top.core.framework.FileItem;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.timwrapper.manager.TadgetManager;

/**
 * 由于页面流程化使用了common-fileupload处理请求，与TopPipeInput中使用Lazy解析有冲突，所以单独处理
 * @author zhenzi
 *
 */
public class TopPagePipeInput extends TopPipeInput {
	private static final long serialVersionUID = -6132850141667624266L;
	
	public TopPagePipeInput(HttpServletRequest req, HttpServletResponse resp,
			TadgetManager tadgetManager) {
		super(req, resp, tadgetManager);
	}
	
	@Override
	public Object getParameter(String key){
		Object value = null;
		value = getRequest().getParameter(key);
		if (value == null && null != getFormData()) {
			value = getFormData().get(key);
		}
		return value;
	}
	@Override
	public FileItem getFileData(){
		//do nothing
		return null;
	}
	@Override
	public Set<String> getParameterNames(){
		Set<String> names = new HashSet<String>();
		if (null != getFormData()) {
			names.addAll(getFormData().keySet());
		}
		// 用url里的参数覆盖，合并
		names.addAll(getRequest().getParameterMap().keySet());
		return names;
	}
}
