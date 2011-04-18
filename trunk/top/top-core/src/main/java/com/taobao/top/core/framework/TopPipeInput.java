/**
 * 
 */
package com.taobao.top.core.framework;



import static com.taobao.top.core.ProtocolConstants.FORMAT_XML;
import static com.taobao.top.core.ProtocolConstants.P_API_KEY;
import static com.taobao.top.core.ProtocolConstants.P_APP_IP;
import static com.taobao.top.core.ProtocolConstants.P_APP_KEY;
import static com.taobao.top.core.ProtocolConstants.P_FORMAT;
import static com.taobao.top.core.ProtocolConstants.P_METHOD;
import static com.taobao.top.core.ProtocolConstants.P_PARTNER_ID;
import static com.taobao.top.core.ProtocolConstants.P_SESSION;
import static com.taobao.top.core.ProtocolConstants.P_SIGN;
import static com.taobao.top.core.ProtocolConstants.P_SIGN_METHOD;
import static com.taobao.top.core.ProtocolConstants.P_TIMESTAMP;
import static com.taobao.top.core.ProtocolConstants.P_VERSION;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.common.lang.StringUtil;
import com.taobao.top.common.TOPConstants;
import com.taobao.top.common.TopPipeConfig;
import com.taobao.top.common.server.DateKit;
import com.taobao.top.core.Api;
import com.taobao.top.core.ApiParameterFormatException;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.ProtocolConstants;
import com.taobao.top.core.exception.FileNumInvalidException;
import com.taobao.top.core.exception.FileSizeInvalidException;
import com.taobao.top.core.exception.FileTypeInvalidException;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.timwrapper.manager.TadgetManager;
import com.taobao.top.xbox.framework.IPipeContext;
import com.taobao.top.xbox.framework.IPipeInput;
import com.taobao.top.xbox.framework.PipeContextManager;

/**
 * 这个是管道输入的结构体，
 * 对Mutlipart采用lazyparse的方式,普通的post暂时不做处理
 * @author fangweng
 *
 */
public class TopPipeInput implements IPipeInput
{
	private static final Log logger = LogFactory.getLog(TopPipeInput.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3751285820488494069L;
	
	private final static String MULTIPART = "multipart/form-data";
	
	private String apiName;
	private String appKey;
	private String format;
	private String session;
	private String timeStamp;
	private String version;
	private String sign;
	private String signMethod;
	private String sessionNick;
	private String sessionUid;
	private String partnerId;
	private String httpMethod;
	private String httpContentType;
	private String appIp;
	private String tag;
	private String bindNick;
	private String isvId;
	
	private TinyAppDO appDO;
	private Api api;
	private String fileContentType;
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private final TadgetManager tadgetManager;
	private boolean isCacheAppDO = false;//判断是否已经缓存了AppDo，避免重复获取
	
	/*
	 * 流量控制的key（由于有app级别的流量控制和app+api级别的流量控制） 
	 */
	private String contorlKey = null;
	/**
	 * 用于存放业务参数的默认值,或者从form中拿到的数据
	 */
	private Map<String,Object> formData = null;

	
	private TopPipeConfig pipeConfig = TopPipeConfig.getInstance();
	
	public TopPipeInput(HttpServletRequest req,HttpServletResponse resp, TadgetManager tadgetManager)
	{
		request = req;
		response = resp;
		this.tadgetManager = tadgetManager;
		
		this.httpMethod = request.getMethod();
		this.httpContentType = request.getContentType();
	}
	
	public HttpServletRequest getRequest(){
		return request;
	}
	public HttpServletResponse getResponse(){
		return response;
	}
	public void put(String key, Object value) {
		if (null == formData) {
			formData = new HashMap<String, Object>();
		}
		formData.put(key, value);
	}
	
	@Override
	public Object getParameter(String key)
	{
		boolean isMultipart = isMultipartContent();
		Object value = null;
		
		if (!pipeConfig.isUseLazyParser() && !isMultipart)
			value = request.getParameter(key);
		else
		{						
			try
			{
				value = LazyParser.getParameter(request, key);
			}
			catch(Exception ex)
			{
				logger.error("getParameter error!",ex);
				dealFileUploadFail(ex);
				if (ex instanceof FileTypeInvalidException
						|| ex instanceof FileSizeInvalidException
						|| ex instanceof FileNumInvalidException)
				{
					throw new java.lang.RuntimeException(ex);
				}
			}
		}
		if (value == null && null != formData) {
			value = formData.get(key);
		}
		return value;
	}
	private void dealFileUploadFail(Exception ex){
		IPipeContext context = PipeContextManager.getContext();
		if(context != null && context.getAttachment(TOPConstants.TOP_ERROR_CODE) == null){
			context.setAttachment(TOPConstants.TOP_ERROR_CODE, ErrorCode.UPLOAD_FAIL);
		}
	}
	/**
	 * 取到上传文件内容的二进制数据
	 * @return
	 */
	public FileItem getFileData() {
		if(!isMultipartContent()){
			return null;
		}
		List<FileItem> fileList = null;
		try {
			fileList = LazyParser.getFileItemsFromRequest(request);
		} catch (Exception e) {
			logger.error("deal with file upload error:", e);
			dealFileUploadFail(e);
			return null;
		}
		//现在只允许上传一个文件，所以只取第一个文件
		if(fileList != null && fileList.size() > 0){
			return fileList.get(0);
		}
		return null;
	}
	/**
	 * 取到所有请求的参数key集合（只取客户端请求的参数集合）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getParameterNames(){
		boolean isMultipart = isMultipartContent();
		
		if (!pipeConfig.isUseLazyParser() && !isMultipart){
			return request.getParameterMap().keySet();
		}else{
			try{
				Map<String,Object> paramters = new HashMap<String,Object>(); 
				Set<String> keys = LazyParser.getParameterNames(request);
				for (String string : keys) {
					Object o = LazyParser.getParameter(request, string);
					
					if(o != null)
						if (!(o instanceof FileItem && ((FileItem)o).isFile()))
					{
						paramters.put(string, o);
					}
				}
				return paramters.keySet();
			}
			catch(Exception ex){
				logger.error("getParameterNames error!",ex);
				dealFileUploadFail(ex);
			}
		}
		return new HashSet<String>();
	}
	/**
	 * 取到所有请求参数和业务参数中有默认值的参数
	 * @return
	 * @deprecated 没有再用
	 */
	public Set<String> getAllParameterNames(){
		Set<String> names = new HashSet<String>();
		if(null != formData){
			names.addAll(formData.keySet());
		}
		names.addAll(getParameterNames());
		return names;
	}
	
	/**
	 * 取到key对应的值，不同的应用场景下对该值是否需要trim要区别对待.
	 * 大多数情况都需要trim，以下几种典型情况需要区别对待：
	 * 1. 在做签名校验的时候，签名之外的各参数都不能trim
	 * 2. 后传参数到各isp的时候，需要根据参数类型判断是否需要trim
	 * 3. 1.0的情况不需要trim.(因为之前都不trim)
	 * @param key
	 * @param needTrim 是否需要对该值进行trim
	 * @return
	 */
	public String getString(String key, boolean needTrim){
		if(key == null){
			return null;
		}
		Object v = getParameter(key);
		if(v == null){
			return null;
		}
		if(v instanceof FileItem){
			String value = ((FileItem)v).getValue();
			if(value == null){
				value = "";
			}
			return needTrim ? StringUtils.trim(value) : value;//((FileItem)v).getValue();
		}
		String str = null;
		if (v.getClass().equals(String.class)) {
			str = (String) v;
		} else if (v.getClass().isArray()) {
			String[] array = (String[]) v;
			str = (String) array[0];
		}
		return needTrim ? StringUtils.trim(str) : str;
	}
	
	public String getAppIp()
	{
		if (appIp == null){
			appIp = getString(P_APP_IP, true);
			if(appIp == null){
				// 为了防止因代理或其他原因导致取到不正确的IP
				String ip = request.getHeader("x-forwarded-for");
				if (StringUtil.isEmpty(ip)) {
					ip = request.getRemoteAddr();
				} else {// add jeck 	http://jira.taobao.ali.com/browse/TIP-9
					ip = StringUtils.split(ip, ',')[0];
				}
				appIp = ip;
			}
		}			
		return appIp;
	}

	public void setAppIp(String appIp)
	{
		this.appIp = appIp;
	}

	public String getApiName()
	{
		if (apiName == null)
			apiName = getString(P_METHOD, true);
			
		return apiName;
	}
	public void setApiName(String apiName)
	{
		this.apiName = apiName;
	}
	public String getAppKey()
	{
		if (appKey == null)
		{
			appKey = getString(P_APP_KEY, true);
			
			if (appKey == null)
				appKey = getString(P_API_KEY, true);
		}
			
		return appKey;
	}
	public void setAppKey(String appKey)
	{
		this.appKey = appKey;
	}
	public String getFormat()
	{
		if(format == null){
			format = getString(P_FORMAT, true);
			if(format == null){
				format = FORMAT_XML;
			}
		}		
		return format;
	}
	public void setFormat(String format)
	{
		this.format = format;
	}
	public String getSession()
	{
		
		if (session == null)
			session = getString(P_SESSION, true);
		
		return session;
	}
	public void setSession(String session)
	{
		this.session = session;
	}
	public String getTimeStamp()
	{
		if (timeStamp == null)
			timeStamp = getString(P_TIMESTAMP, true);
			
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp)
	{
		this.timeStamp = timeStamp;
	}
	public String getVersion()
	{
		if (version == null)
			version = getString(P_VERSION, true);
			
		return version;
	}
	public void setVersion(String version)
	{
		this.version = version;
	}
	public String getSign()
	{
		if (sign == null)
			sign = getString(P_SIGN, true);
			
		return sign;
	}
	public void setSign(String sign)
	{
		this.sign = sign;
	}
	public String getSignMethod()
	{
		if (signMethod == null)
			signMethod = getString(P_SIGN_METHOD, true);
			
		return signMethod;
	}
	public void setSignMethod(String signMethod)
	{
		this.signMethod = signMethod;
	}
	public String getSessionNick()
	{
		return sessionNick;
	}
	public void setSessionNick(String sessionNick)
	{
		this.sessionNick = sessionNick;
	}
	public String getPartnerId()
	{
		if (partnerId == null)
			partnerId = getString(P_PARTNER_ID, true);
			
		return partnerId;
	}
	public void setPartnerId(String partnerId)
	{
		this.partnerId = partnerId;
	}
	public String getHttpMethod()
	{
		if (httpMethod == null)
			httpMethod = request.getMethod();
		
		return httpMethod;
	}
	public void setHttpMethod(String httpMethod)
	{
		this.httpMethod = httpMethod;
	}

	public String getTag() {
		if (tag == null && appDO != null) {
			Long appTag = appDO.getAppTag();
			tag = (appTag == null ? null: "" + appTag);
		}
		
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getBindNick() {
		if (bindNick == null && appDO != null) {
			bindNick = appDO.getBindNick();
		}
		return bindNick;
	}

	public void setBindNick(String bindNick) {
		this.bindNick = bindNick;
	}
	
	public String getIsvId() {
		if(isvId == null && appDO != null) {
			Long isvIdLong = appDO.getIsvId();
			isvId = (isvIdLong == null) ? null : "" + isvIdLong;
		}
		return isvId;
	}

	public void setIsvId(String isvId) {
		this.isvId = isvId;
	}

	public TinyAppDO getAppDO() {
		if(isCacheAppDO){
			return appDO;
		}
		try{
			isCacheAppDO = true;
			appDO = tadgetManager.getTadgetByKey(appKey);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			appDO = null;
		}
		return appDO;
	}

	public Api getApi() {
		return api;
	}

	public void setApi(Api api) {
		this.api = api;
	}
	public String getSessionUid() {
		return sessionUid;
	}

	public void setSessionUid(String sessionUid) {
		this.sessionUid = sessionUid;
	}
	
	public String getEndUserIp() {
		String ip = request.getHeader(ProtocolConstants.P_ENDUSER_IP);
		return ip;
	}
	//-----------------
	public String getStringTrimToNull(String key) {
		return StringUtils.trimToNull(getString(key, true));
	}

	/**
	 * 按逗号分割
	 */
	public String[] getStringArray(String key)
			throws ApiParameterFormatException {
		String list = getString(key, true);
		if (StringUtils.isEmpty(list))
			return null;
		String[] array = StringUtils.split(list, ',');
		return array;
	}

	public Integer getInteger(String key) throws ApiParameterFormatException {
		String value = getString(key, true);
		Integer rs = null;
		if (StringUtils.isNotEmpty(value)) {
			try {
				rs = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new ApiParameterFormatException(e, key, value);
			}
		}
		return rs;
	}

	public Boolean getBoolean(String key) throws ApiParameterFormatException {
		String value = getString(key, true);
		Boolean rs = null;
		if (StringUtils.isNotEmpty(value)) {
			try {
				rs = Boolean.parseBoolean(value);
			} catch (NumberFormatException e) {
				throw new ApiParameterFormatException(e, key, value);
			}
		}
		return rs;
	}

	public Date getDate(String key) throws ApiParameterFormatException {
		String value = getString(key, true);
		Date rs = null;
		if (StringUtils.isNotEmpty(value)) {
			try {
				if(value.indexOf("-")!=-1){
					rs = DateKit.ymdOrYmdhms2Date(value);
				}else{
					rs = new Date(Long.parseLong(value));
				}
			} catch (Exception e) {
				throw new ApiParameterFormatException(e, key, value);
			}
		}
		return rs;
	}
	public String getFileContentType() {
		if(fileContentType != null){
			return fileContentType;
		}
		FileItem fileItem = getFileData();
		if(fileItem != null){
			String ctype = fileItem.getContentType();
			if (!StringUtils.isEmpty(ctype)) {
				int token = ctype.indexOf(';') == -1 ? ctype.length() : ctype.indexOf(';');
				ctype = StringUtil.trimToEmpty(ctype.substring(0, token));
			}
			fileContentType = ctype;
		}
		return fileContentType;
	}
	/**
	 * is login
	 * @param username
	 * @return
	 */
	public boolean isLogin(String username) {
		if (getSessionNick() == null || username == null) {
			// 未登录
			return false;
		}
		return username.equals(getSessionNick());
	}
	
	//FIXME 页面流程化中用到，有可能在合并包后去掉
	public Map<String,Object> getFormData(){
		return formData;
	}
	public String getContorlKey() {
		return contorlKey;
	}
	public void setContorlKey(String contorlKey) {
		this.contorlKey = contorlKey;
	}

	/**
	 * 得到所有参数key和value的列表字符串，便于debug，分析问题使用
	 * NOTE: 不太应该作为业务逻辑代码使用 
	 * 大多数时候不会被调用，只有在debug时候使用，现阶段不太需要优先
	 * @return
	 */
	public String getParamStr() {
		StringBuilder tradeInfo = new StringBuilder();
		
		String[] paramNames = getParameterNames().toArray(
				ArrayUtils.EMPTY_STRING_ARRAY);
		Arrays.sort(paramNames);
		tradeInfo.append("params:{");
		for(String paramName : paramNames) {
			tradeInfo.append(paramName).append("=").append(
					getString(paramName, true)).append(";");
		}
		tradeInfo.append("}");
		return tradeInfo.toString();
	}
	
	public boolean isMultipartContent()
	{
		if (!"post".equalsIgnoreCase(this.httpMethod)) {
			return false;
		}
	
		if (httpContentType != null && httpContentType.toLowerCase().startsWith(MULTIPART)) {
			return true;
		}
		
		return false;
	}
}
