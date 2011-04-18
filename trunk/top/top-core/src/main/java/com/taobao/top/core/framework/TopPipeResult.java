/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core.framework;

import com.taobao.top.common.TOPConstants;
import com.taobao.top.common.TopPipeConfig;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.ResultSet;
import com.taobao.top.xbox.framework.IPipeContext;
import com.taobao.top.xbox.framework.IPipeResult;
import com.taobao.top.xbox.framework.PipeContextManager;

/**
 * Api调用结果
 * 
 * @version 2008-2-29
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class TopPipeResult implements IPipeResult {

	private static final long serialVersionUID = 410179980412071183L;

	private ErrorCode errorCode;

	private Object black;

	private ResultSet resultSet;
    
	private Exception exception;
	
	private boolean breakPipeChain = false;
	
	private Object result;
	
	private int status = STATUS_UNDO;//
	
	private String msg;
	
	private String subCode;
	
	private String subMsg;
	
	private long executeTime;

	/**
	 * top-mapping response time, add by zixue
	 */
	private long responseMappingTime;

	public void setException(Exception exception)
	{
		this.exception = exception;
	}

	@Override
	public boolean isBreakPipeChain()
	{
		return breakPipeChain;
	}

	@Override
	public void setBreakPipeChain(boolean breakPipeChain)
	{
		this.breakPipeChain = breakPipeChain;
	}
	
	public Exception getException() {
		if (resultSet == null)
			return exception;
		
		return resultSet.getException();
	}
	
	/**
	 * @return the subCode
	 */
	public String getSubCode() {
		return subCode;
	}

	/**
	 * @param subCode the subCode to set
	 */
	public void setSubCode(String subCode) {
		this.subCode = subCode;
	}

	/**
	 * @return the subMsg
	 */
	public String getSubMsg() {
		return subMsg;
	}

	/**
	 * @param subMsg the subMsg to set
	 */
	public void setSubMsg(String subMsg) {
		this.subMsg = subMsg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public ErrorCode getErrorCode() {
		if(errorCode != null){
			return errorCode;
		}
		IPipeContext context = PipeContextManager.getContext();
		if(context != null){
			Object o = context.getAttachment(TOPConstants.TOP_ERROR_CODE);
			if(o != null && o instanceof ErrorCode){
				this.errorCode = (ErrorCode)o;
			}
		}
		return errorCode;
	}

	/**
	 * BlackBoxEngine返回结果
	 * @return the black
	 */
	public Object getBlack() {
		return black;
	}

	/**
	 * @param black
	 *            the black to set
	 */
	public void setBlack(Object black) {
		this.black = black;
	}

	/**
	 * 得到错误码，0表示成功
	 * 
	 * @return
	 */
	public int getCode() {
		int code = 0;
		if (errorCode != null) {
			code = errorCode.getCode();
		}
		return code;
	}
	public Integer getDetailCode(){
		Integer code=null;
		if (errorCode != null) {
			code = errorCode.getCode();
		}
		return code;
	}

	/**
	 * 是否成功
	 * 
	 * @return
	 */
	public boolean isSuccess() {
		return 0 == getCode();
	}

	public String getCodeStr() {
		return String.valueOf(getCode());
	}
	public String getDetailStr(){
		if(getDetailCode()==null){
			return "";
		}
		return String.valueOf(getDetailCode());
	}

	public String getMsg() {
		String out = null;
		if (errorCode != null) {
			out = errorCode.getMsg();
		}
		if (this.msg != null) {
			out += ":" + this.msg;
		}
		return out;
	}
	/**
	 * 保存最原始的ErrorCode
	 * @param errorCode
	 */
	public void setErrorCode(ErrorCode errorCode) {
		if(this.errorCode != null){
			return;
		}
		IPipeContext context = PipeContextManager.getContext();
		if(context != null && context.getAttachment(TOPConstants.TOP_ERROR_CODE) != null && context.getAttachment(TOPConstants.TOP_ERROR_CODE) instanceof ErrorCode){
			this.errorCode = (ErrorCode)context.getAttachment(TOPConstants.TOP_ERROR_CODE);
		}else{
			this.errorCode = errorCode;
		}
	}

	public ResultSet getResultSet() {
		return resultSet;
	}


	public void setExecuteTime(long time) {
		this.executeTime = time;
	}

	public long getExecuteTime() {
		return this.executeTime;
	}

	public void setResultSet(ResultSet result) {
		this.resultSet = result;
	}

	/**
	 * @param responseMappingTime
	 */
	public void setResponseMappingTime(long responseMappingTime) {
		this.responseMappingTime = responseMappingTime;
	}

	public long getResponseMappingTime() {
		return this.responseMappingTime;
	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return result;
	}
	
	public void setResult(Object o)
	{
		result = o;
	}

	@Override
	public boolean isCancelled() {
		if (status == STATUS_CANCEL)
			return true;
		else
			return false;
	}

	@Override
	public boolean isDone() {
		if (status == STATUS_DONE)
			return true;
		else
			return false;
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
	}
	//把结果用字符串的形式输出出来，用于debug和日志记录
	public String getResultStr(){
		StringBuffer result = new StringBuffer();
		result.append("response{isSuccess=");
		result.append( String.valueOf(this.isSuccess()));
		result.append(";");
		result.append("errorCode=");
		result.append( String.valueOf(this.getCode()));
		result.append(";");
		result.append("subErrorCode=");
		result.append( String.valueOf(this.getSubCode()));
		result.append(";");
		result.append("msg=");
		result.append( String.valueOf(this.getMsg()));
		result.append(";");
		result.append("subMsg=");
		result.append( String.valueOf(this.getSubMsg()));
		result.append("}");
		return result.toString();
	}
	
	
}
 
