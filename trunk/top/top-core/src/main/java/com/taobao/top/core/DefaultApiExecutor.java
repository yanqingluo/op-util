/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.hsf.exception.HSFException;
import com.taobao.top.common.TOPConstants;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.traffic.mapping.OperationCodeException;


/**
 * API执行实现
 * 
 * @version 2008-2-29
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class DefaultApiExecutor implements ApiExecutor {

	protected final transient Log log = LogFactory.getLog(this.getClass());
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
	/**
	 * 临时增加对于isp.top-remote-connection-timeout异常是否打印日常堆栈
	 */
	private boolean openLog = true;
	/**
	 * 黑盒引擎
	 */
	private BlackBoxEngine blackBoxEngine;

	
	private RedirectBlackBoxEngine redirectBlackBoxEngine = null;

	/**
	 * @param blackBoxEngine
	 *            the blackBoxEngine to set
	 */
	public void setBlackBoxEngine(BlackBoxEngine blackBoxEngine) {
		this.blackBoxEngine = blackBoxEngine;
	}

	/**
	 * Just for Junit test since the RedirectBlackBoxEngine has been initialized
	 * on construction.
	 * 
	 * @param redirectBlackBoxEngine
	 */
	public void setRedirectBlackBoxEngine(
			RedirectBlackBoxEngine redirectBlackBoxEngine) {
		this.redirectBlackBoxEngine = redirectBlackBoxEngine;
	}

	public boolean isOpenLog() {
		return openLog;
	}

	public void setOpenLog(boolean openLog) {
		this.openLog = openLog;
	}
	@Override
	public void execute(TopPipeInput pipeInput, TopPipeResult pipeResult) {
		// Api is set in DefaultApiChecker.checkBefore()
		Api api = pipeInput.getApi();

		// ====================== 直接透传===========================
		if (api.isRedirect()) {
			long start = System.currentTimeMillis();
			try {
				// the returned 'black' is in fact the HttpConnection.
				this.redirectBlackBoxEngine.execute(pipeResult, pipeInput, api);
			} catch (Exception e) {
				//ISP异常打点，zhudi
				if(debugController!=null){
					debugController.saveIspErrorLog(pipeInput,e);
				}
				pipeResult.setErrorCode(ErrorCode.REMOTE_SERVICE_ERROR);
				pipeResult.setSubCode(TOPConstants.ISP_REDIRECT_ERROR);
				pipeResult.setMsg(e.getMessage());
			} finally {
				pipeResult.setExecuteTime(System.currentTimeMillis() - start);
			}

			return;
		}

		// ====================== 直接远程呼叫 ===========================
		if (api.isCallable(pipeInput.getVersion())) {
			long start = System.currentTimeMillis();
			try {
				this.blackBoxEngine.execute(pipeResult, pipeInput, api);
			} catch (Exception e) {
				//ISP异常打点，zhudi
				if(debugController!=null){
					debugController.saveIspErrorLog(pipeInput,e);
				}
				pipeResult.setErrorCode(ErrorCode.REMOTE_SERVICE_ERROR);
				if (e instanceof OperationCodeException) {
					OperationCodeException oce = (OperationCodeException)e;
					pipeResult.setSubCode(oce.getCode());
					pipeResult.setSubMsg(oce.getMsg());
				} else if (e instanceof HSFException) { 
					//the hsf service exception can only be judged
					//this way at present. --added by huaisu.
					if (isTopMappingException(e)) {
						pipeResult.setSubCode(TOPConstants.ISP_TOP_MAPPING_PARSE_ERROR);
					} else if (isTimeOutException(e)) {
						pipeResult.setSubCode(TOPConstants.ISP_TOP_REMOTE_CONNECTION_TIMEOUT);
						if(openLog){
							log.error(e, e);
						}
					} else if (isConnectionError(e)) {
						pipeResult.setSubCode(TOPConstants.ISP_TOP_REMOTE_CONNECTION_ERROR);
					} else {
						pipeResult.setSubCode(TOPConstants.ISP_REMOTE_SERVICE_UNAVAILABLE);
					}
				} else {
					pipeResult.setSubCode(TOPConstants.ISP_REMOTE_SERVICE_UNAVAILABLE);
					pipeResult.setMsg(e.getMessage());
				}
			}
			
			pipeResult.setExecuteTime(System.currentTimeMillis() - start);
			return;
		}
	}

	/**
	 * FIXME: this is the only way now, but to bad way, ask bixuan for help
	 * @param e
	 * @return
	 */
	private boolean isTimeOutException(Throwable e) {
		String errMsg = e.getCause() != null ?  e.getCause().toString() : null;
		return StringUtils.contains(errMsg, "com.taobao.remoting.TimeoutException") 
			|| StringUtils.contains(errMsg, "com.taobao.hsf.exception.HSFTimeOutException");		
	}
	
	
	/**
	 * FIXME: this is the only way now, but to bad way, ask bixuan for help.
	 * @param e
	 * @return
	 */
	private boolean isTopMappingException(Throwable e) {
		String errMsg = e.toString();
		return StringUtils.contains(errMsg, "com.taobao.top.traffic.mapping");
	}

	private boolean isConnectionError(Exception e) {
		String errMsg = e.toString();
		return StringUtils.contains(errMsg, "未找到需要调用的服务的目标地址");			
	}
}

