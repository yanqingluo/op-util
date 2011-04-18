/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.taobao.top.common.TOPConstants;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.impl.core.export.ProtocolJsonV2;
import com.taobao.top.impl.core.export.ProtocolXmlV2;

/**
 * @version 2008-2-29
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * @author jiangyongyuan.tw 2009-9-22
 */
public class DefaultApiExporter implements ApiExporter {

	/**
	 * 不同的协议输出器,注意输出器为共享变量,各自exporter方法均传递参数,防止线程安全
	 */
	ApiExporter v2xml = new ProtocolXmlV2();
	ApiExporter v2json = new ProtocolJsonV2();
	
	public static final String VERSION_2 = "2.0";
	
	/**
	 * ISP的debug打点控制器，由他负责打点<zhudi@taobao.com>
	 */
	private DebugController debugController;
	

	public DebugController getDebugController() {
		return debugController;
	}

	public void setDebugController(DebugController debugController) {
		this.debugController = debugController;
	}
	
	private ErrorCodeTransform errorCodeTransform;
	
	public void setErrorCodeTransform(ErrorCodeTransform errorCodeTransform) {
		this.errorCodeTransform = errorCodeTransform;
	}

	@Override
	public void export(HttpServletResponse response, TopPipeInput pipeInput,
			TopPipeResult apiResult, ErrorCodeTransform errorCodeTransform) throws IOException, TopException {


		// check whether it's redirect(pass thru).
		Api api = pipeInput.getApi();

		if (api != null && api.isRedirect()) {

			// use outputstream directly. we got the response from third
			// party. If apiResult is not successful, we need
			// normal writer-based export() to export the error response.
			try {
				if (apiResult.isSuccess()) {
					exportForRedirect(response, pipeInput, apiResult);
				} else {
					exportStrategy(response, pipeInput, apiResult);
				}
			} finally { // disconnect the connection.

				HttpURLConnection conn = (HttpURLConnection) apiResult
						.getBlack();
				if (conn != null) {
					// Must disconnect connection here. since
					// either apiResult is success or not, we
					// need to disconnect the connection.
					conn.disconnect();
				}
			}
		} else {
			// Use normal writer-based export() to export
			// result as well as error code response.
			exportStrategy(response, pipeInput, apiResult);
		}
	
	}
	/**
	 * Passthru/redirect write out.
	 * 
	 * @param response
	 * @param apiInput
	 * @param apiResult
	 * @throws IOException
	 * @throws TopException 
	 */
	void exportForRedirect(HttpServletResponse response, TopPipeInput pipeInput,
			TopPipeResult apiResult) throws IOException, TopException {

		long start = System.currentTimeMillis();
		
		HttpURLConnection conn = (HttpURLConnection) apiResult.getBlack();

		// Since only when result.isSucess would we invoke this method.
		// we assume every resource is available and in fine state.
		BufferedInputStream bufin = null;
		try {
			bufin = new BufferedInputStream(conn.getInputStream());
		} catch (IOException e) {
			// Can't attain any information from third party
			// use normal version of export to write out
			// the error response.
			apiResult.setErrorCode(ErrorCode.REMOTE_SERVICE_ERROR);

			// Don't set e.getMessage() since that main contains
			// sensitive information.
			apiResult.setMsg("Can't attain remote response");
			apiResult.setSubCode(TOPConstants.ISP_REDIRECT_ERROR);
			//ISP异常打点   朱棣
			if(debugController!=null){
				debugController.saveIspErrorLog(pipeInput,e);
			}
			exportStrategy(response, pipeInput, apiResult);
			return;
		}

		try {
			ServletOutputStream outputStream = response.getOutputStream();
			byte[] tmpbuf = new byte[4000];
			int readbytes = 0;

			while ((readbytes = bufin.read(tmpbuf)) != -1) {
				outputStream.write(tmpbuf, 0, readbytes);
			}
			
			//拿到错误码，设置错误码
			String ispErrorCode = conn.getHeaderField(TOPConstants.ISP_ERRORCODE);
			
			if (ispErrorCode != null)
			{
				apiResult.setErrorCode(ErrorCode.REMOTE_SERVICE_ERROR);
				apiResult.setSubCode(ispErrorCode);
			}
			
		} finally {
			if (bufin != null) {
				bufin.close();
			}

			//设置消耗时间,为透传做的
			apiResult.setExecuteTime(System.currentTimeMillis() - start);
			
			// Can't disconnect connection here.
			// since this is not the only branch needed to get the connection
			// disconnected.
		}
	}

	/**
	 * 使用不同策略输出,默认为1.0 json输出
	 * @param response
	 * @param input
	 * @param apiResult
	 * @throws IOException
	 * @throws TopException 
	 */
	void exportStrategy(HttpServletResponse response, TopPipeInput input, TopPipeResult apiResult)
			throws IOException, TopException {
		//		modify by zixue
		//		ApiExporter export = v1json;
		//
		//		if(isVersion2(input.getVersion()) && isXML_FORMAT(input.getFormat()))
		//			export = v2xml;
		//		else if(isVersion2(input.getVersion()) && !isXML_FORMAT(input.getFormat()))
		//			export = v2json;
		//		else if(!isVersion2(input.getVersion()) && isXML_FORMAT(input.getFormat()))
		//			export = v1xml;
		
		ApiExporter export = null;
		
//		if(isVersion2(input.getVersion())) {
			if (isJSON_FORMAT(input.getFormat())) {
				export = v2json;
			} else {
				export = v2xml;
			}
//		} else {
//			if (isJSON_FORMAT(input.getFormat())) {
//				export = v1json;
//			} else {
//				export = v1xml;
//			}
//		}

		
		//export the result
		export.export(response, input, apiResult, errorCodeTransform);
	}	
	private boolean isJSON_FORMAT(String format){
		return ProtocolConstants.FORMAT_JSON.equals(format);
	}
}
