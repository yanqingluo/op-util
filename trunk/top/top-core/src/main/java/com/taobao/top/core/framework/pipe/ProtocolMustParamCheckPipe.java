package com.taobao.top.core.framework.pipe;


import static com.taobao.top.core.ProtocolConstants.FORMAT_HTML;
import static com.taobao.top.core.ProtocolConstants.FORMAT_JSON;
import static com.taobao.top.core.ProtocolConstants.FORMAT_XML;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.common.TOPConstants;
import com.taobao.top.core.Api;
import com.taobao.top.core.ApiFactory;
import com.taobao.top.core.ApiType;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.FullErrorInfo;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.core.framework.TopPipeUtil;
import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.tim.util.ExPropertyName;
import com.taobao.top.timwrapper.manager.TadgetManager;
/**
 * 用于检查协议必填参数是否正确
 * @author zhenzi
 *
 */
public class ProtocolMustParamCheckPipe extends TopPipe<TopPipeInput, TopPipeResult> {
	private static final Log logger = LogFactory.getLog(ProtocolMustParamCheckPipe.class);
	private ApiFactory apiFactory = null;
	
	public void setApiFactory(ApiFactory apiFactory) {
		this.apiFactory = apiFactory;
	}

	public ProtocolMustParamCheckPipe() {
		super();
	}

	/**
	 * 更新类API是否强迫需要POST
	 */
	private boolean isForcePost = true;

	public void setForcePost(boolean isForcePost) {
		this.isForcePost = isForcePost;
	}
	
	private TadgetManager tadgetManager = null;
	
	public void setTadgetManager(TadgetManager tadgetManager) {
		this.tadgetManager = tadgetManager;
	}

	/**
	 * 当前是否运行在沙箱环境
	 */
	public boolean sandbox = false;

	public void setSandbox(boolean sandbox) {
		this.sandbox = sandbox;
	}

		
	@Override
	public void doPipe(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		Api api = null;
		TinyApiDO apiDO = null;
		ErrorCode errCode = null;
		
		//---------------------method校验
		try{
			api = apiFactory.getApi(pipeInput.getApiName());
			apiDO = tadgetManager.getValidApiByApiName(pipeInput.getApiName());
		}catch(Exception e){
			logger.error(pipeInput.getApiName() + ":" + e.getMessage(), e);
			pipeResult.setErrorCode(ErrorCode.SERVICE_CURRENTLY_UNAVAILABLE);
			pipeResult.setMsg(pipeInput.getApiName());
			return;
		}
		if(null == api || apiDO == null){
			logger.warn("api " + pipeInput.getApiName() + " invlid:"  
					+ api + "," + apiDO);
			pipeResult.setErrorCode(ErrorCode.INVALID_METHOD);
			return;
		}
		/*
		 * 检查http动作
		 */
		if(api.getApiType() != ApiType.SELECT){
			if(this.isForcePost){
				if(!"POST".equalsIgnoreCase(pipeInput.getHttpMethod())){
					pipeResult.setErrorCode(ErrorCode.HTTP_ACTION_NOT_ALLOWED);
					return;
				}
			}
		}
		/**
		 * api数据正确，把此api对象存入pipeInput
		 */
		pipeInput.setApi(api);
		//------------------appkey校验
		TinyAppDO appDO = pipeInput.getAppDO();
		FullErrorInfo errInfo = checkAppCorrect(appDO);
		if(errInfo != null){
			pipeResult.setErrorCode(errInfo.getErrorCode());
			pipeResult.setSubCode(errInfo.getSubErrCode());
			return;
		}
		//--------appkey是否设置了ip白名单
		boolean isAllow = checkAppIpWhite(appDO.getPropertyValue(ExPropertyName.IP),pipeInput.getAppIp());
		if(!isAllow){
			pipeResult.setErrorCode(ErrorCode.INSUFFICIENT_ISV_PERMISSIONS);
			StringBuilder sb = new StringBuilder();
			sb.append("The appkey ").append(pipeInput.getAppKey())
					.append(" is only allowed to call from ")
					.append(appDO.getPropertyValue(ExPropertyName.IP))
					.append(", but your ip is ").append(pipeInput.getAppIp());
			pipeResult.setSubCode(ErrorCode.INSUFFICIENT_ISV_PERMISSIONS.getMsg());
			pipeResult.setSubMsg(sb.toString());
			return;
		}
		//------------------appkey+method校验，判断此appkey是否有权限调用api
		errCode = checkIsvPermission(appDO,apiDO);
		if(errCode != null){
			pipeResult.setErrorCode(errCode);
			return;
		}
		//------------------version检查
		errCode = checkVersion(api, pipeInput.getVersion());
		if(errCode != null){
			pipeResult.setErrorCode(errCode);
			return;
		}
		//禁止掉除了pageapi之外的1.0的调用
		if(!pipeInput.getApiName().endsWith(".page")){
			if("1.0".equals(pipeInput.getVersion())){
				pipeResult.setErrorCode(ErrorCode.UNSUPPORTED_VERSION);
				return;
			}
		}
		
		//--------------------format的检查
		errCode = checkFormat(pipeInput.getFormat());
		if(errCode != null){
			pipeResult.setErrorCode(errCode);
			return;
		}
		//----------------timestamp校验
		errCode = checkTimestamp(pipeInput.getTimeStamp());
		if(errCode != null){
			pipeResult.setErrorCode(errCode);
			return;
		}
		
		return;
	}
	
	@Override
	public boolean ignoreIt(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		if(pipeResult.getErrorCode() != null || TopPipeUtil.isIgnore(pipeInput)){//如果前一个管道的校验没有通过，则忽略此管道
			return true;
		}
		return false;
	}
	/**
	 * 判断远端ip是否在允许的ip白名单里
	 * @param ipWhilte
	 * @param remoteIp
	 * @return 如果在ip白名单里或者admincenter中没有设置ip白名单，或者取不到远端ip，则返回true
	 * 否则，根据ip是否在ip白名单里判断是否允许调用
	 */
	private boolean checkAppIpWhite(String ipWhilte,String remoteIp){
		if(StringUtils.isEmpty(ipWhilte) || StringUtils.isEmpty(remoteIp)){
			return true;
		}
		String[] allowIps = StringUtils.split(ipWhilte,",");
		List<String[]> whiteIps = new ArrayList<String[]>();
		if (null != allowIps && allowIps.length > 0) {
			for (int i = 0; i < allowIps.length; i++) {
				//把每个ip按“.”拆开，得到一个四位的数组
				String[] ipParse = StringUtils.split(allowIps[i], ".");
				whiteIps.add(ipParse);
			}
		}
		String[] requestParse = StringUtils.split(remoteIp, ".");
		for (String[] whiteIp : whiteIps) {
			if (ipsEqual(requestParse, whiteIp)) {
				return true;
			}
		}
		return false;
	}
	//判断两个ip是否相等
	private boolean ipsEqual(String[] requestIp, String[] whiteIp) {
		boolean equal = false;
		
		//判断白名单ip是否在列表中必须要两个ip都不为空进行比较
		if (requestIp != null && whiteIp != null && requestIp.length == whiteIp.length) {			
			if (requestIp[0].equals(whiteIp[0])
					&& requestIp[1].equals(whiteIp[1])
					&& ("*".equals(whiteIp[2]) || requestIp[2]
							.equals(whiteIp[2]))
					&& ("*".equals(whiteIp[3]) || requestIp[3]
							.equals(whiteIp[3]))) {
				equal = true;
			}
		}
		
		return equal;
	}
	/**
	 * 检查version是否正确
	 * @param api 此次请求对应的method对应的Api对象
	 * @param version 客户端传递的v变量
	 * @return
	 */
	private ErrorCode checkVersion(Api api,String version){
		// 检查版本号是否格式错误,目前只支持1.0和2.0
		if (!"1.0".equals(version) && !"2.0".equals(version)) {
			return ErrorCode.INVALID_VERSION;
		}
		
		//检查API是否支持该版本
		String[] apiSupportedVersions = api.getSupportedVersions(); // getApiDefinedVersion

		if (apiSupportedVersions == null || apiSupportedVersions.length == 0) {
			// not set, assume as "1.0".
			apiSupportedVersions = TOPConstants.DEFAULT_VERSIONS;
		}

		for (String supportedVersion : apiSupportedVersions) {
			if (supportedVersion.equals(version)) {
				return null;//找到了匹配的v
			}
		}
		return ErrorCode.UNSUPPORTED_VERSION;
	}
	

	/**
	 * 检查format的正确性
	 * @param format 
	 * @return
	 */
	private ErrorCode checkFormat(String format){
		if(!(FORMAT_XML.equals(format) || FORMAT_JSON.equals(format) || FORMAT_HTML.equals(format))){
			return ErrorCode.INVALID_FORMAT;
		}
		return null;
	}
	/**
	 * 检查timestamp是否正确
	 * @param timeStamp
	 * @return
	 */
	private ErrorCode checkTimestamp(String timeStamp){
		if (timeStamp.indexOf("-")!=-1) {
			try {
				com.taobao.top.common.server.DateKit
						.ymdOrYmdhms2Date(timeStamp);
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.debug(e.getMessage(), e);
				}
				return ErrorCode.INVALID_TIMESTAMP;
			}
		}else{
			try{
				Long.parseLong(timeStamp);
			}catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.debug(e.getMessage(), e);
				}
				return ErrorCode.INVALID_TIMESTAMP;
			}
		}
		return null;
	}
	/**
	 * 检查appkey对应的插件状态是否正确
	 * @param appkey
	 * @return
	 */
	private FullErrorInfo checkAppCorrect(TinyAppDO appDO){		
		FullErrorInfo errorInfo = new FullErrorInfo();
		//如果appkey对应的appDO为null，则认为没有此appkey
		if(appDO == null){
			errorInfo.setErrorCode(ErrorCode.INVALID_APP_KEY);
			errorInfo.setSubErrCode(TOPConstants.APPKEY_INVALID_NOT_EXIST);
			return errorInfo;
		}
		//如果不是上线、线上测试状态。则无权限调用
		if (!TinyAppDO.APP_STATUS_RUNNING.equals(appDO.getAppStatus()) 
					&& !TinyAppDO.APP_STATUS_TESTING.equals(appDO.getAppStatus())) {
			errorInfo.setErrorCode(ErrorCode.INVALID_APP_KEY);
			errorInfo.setSubErrCode(TOPConstants.APPKEY_INVALID_INVALID_STATUS);
			return errorInfo;

		}
				
		return null;
	}
	/**
	 * 检查appkey是否有调用api的权限
	 * @param appkey
	 * @param apiName
	 */
	private ErrorCode checkIsvPermission(TinyAppDO app,TinyApiDO api){
		//如果是沙箱环境，则直接通过
		//TODO:该沙箱的特殊处理可以通过沙箱环境的DB体现，但是会涉及到aua团队的修改，暂时先在tip通过代码逻辑实现;
		if(sandbox){
			return null;
		}
		try{
			boolean checked = tadgetManager.appCanAccessApi(app, api);
			if(!checked){//检查未通过
				return ErrorCode.INSUFFICIENT_ISV_PERMISSIONS;
			}
		}catch(Exception e){//如果出现异常，允许调用api
			logger.error(e.getMessage(),e);
		}
		return null;
	}
}
