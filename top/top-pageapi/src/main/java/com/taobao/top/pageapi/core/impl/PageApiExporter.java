package com.taobao.top.pageapi.core.impl;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import com.taobao.top.core.ApiExporter;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.ErrorCodeTransform;
import com.taobao.top.core.ProtocolConstants;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
/**
 * 封装流程页面API 响应页面
 * 
 * @version 2009-08-16
 * @author <a href="mailto:yueqian@taobao.com">yueqian</a>
 * 
 */
public class PageApiExporter implements ApiExporter {

	String htmlHeader = "<html><body><center>";
	String htmlEnd = "</center></body></html>";
	/**
	 *  重构中加入的页面流程化的输出
	 */
	@Override
	public void export(HttpServletResponse response, TopPipeInput pipeInput,
			TopPipeResult apiResult, ErrorCodeTransform errorCodeTransform) throws IOException {
	    Writer writer=response.getWriter();		
		
		if (ProtocolConstants.FORMAT_HTML.equals(pipeInput.getFormat())) {
			if (apiResult.isSuccess()) {
				TopPageAPIResult result = (TopPageAPIResult) apiResult;
				writer.write(result.getResponsePage());
			} else {
				// 返回错误信息
				writer.write(htmlHeader);
				ErrorCode errCode = apiResult.getErrorCode();
				if (errCode != null) {
					if (errCode == ErrorCode.INVALID_SESSION) {
						writer.write("用户登录超时，请重新登录！");
					}else {
						String msg = pipeInput.getString("msg", true);// 用于显示TM 调用支付宝时，支付宝报错信息
						if (msg != null) {
							msg = msg.replace("<", "");
							msg = msg.replace(">", "");
							writer.write(msg);
						} else {
							writer.write(errCode.getMsg());
						}
					   writer.write(errCode.getMsg());
					}
					writer.write(htmlEnd);
				}
			}
		} else {

			// 返回错误信息
			writer.write(htmlHeader);
			ErrorCode errCode = apiResult.getErrorCode();
			if (errCode == ErrorCode.INVALID_FORMAT) {
				writer.write("页面API 传入 format 值错误，应该为 html");
			}
			writer.write(htmlEnd);
		}

	}



}
