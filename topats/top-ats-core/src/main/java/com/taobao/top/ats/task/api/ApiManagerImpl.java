package com.taobao.top.ats.task.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.common.lang.io.ByteArrayOutputStream;
import com.taobao.common.tfs.TfsManager;
import com.taobao.top.ats.AtsException;
import com.taobao.top.ats.XmlException;
import com.taobao.top.ats.domain.ApiDO;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.ParameterDO;
import com.taobao.top.ats.domain.ParamsDO;
import com.taobao.top.ats.domain.RangeParameterDO;
import com.taobao.top.ats.domain.ResultSet;
import com.taobao.top.ats.domain.SubAtsTaskDO;
import com.taobao.top.ats.domain.TimePeriod;
import com.taobao.top.ats.engine.ApiEngine;
import com.taobao.top.ats.engine.ApiRequest;
import com.taobao.top.ats.engine.ApiResponse;
import com.taobao.top.ats.util.DateKit;
import com.taobao.top.ats.util.ErrorCode;
import com.taobao.top.ats.util.KeyConstants;
import com.taobao.top.ats.util.ModelKeyConstants;
import com.taobao.top.ats.util.RangeParseUtil;
import com.taobao.top.ats.util.StatusConstants;
import com.taobao.top.ats.util.StringKit;
import com.taobao.top.tim.domain.ExPropertyDO;
import com.taobao.top.tim.service.SamService;
import com.taobao.top.tim.service.TIMServiceException;

/**
 * API管理器默认实现。
 * 
 * @author moling
 * @since 1.0, 2010-8-20
 */
public class ApiManagerImpl implements ApiManager {
	//前置校验返回结果，强制要求返回此结果
	public static final String PRECHECK_SUCCESS_JSON = "{\"pre_check_result\":{\"is_ok\":true}}";
	public static final String PRECHECK_SUCCESS_XML = "<pre_check_result><is_ok>true</is_ok></pre_check_result>";
	private static final Log log = LogFactory.getLog(ApiManagerImpl.class);

	private Map<String, ApiDO> apis;
	private ApiEngine apiEngine;
	private SamService samService;
	private String modelFileKey;	//数据库中存模板的key
	private Long timApiType;	//数据库中表示api类型的值
	private TfsManager tfsManager;
	
	public void setSamService(SamService samService){
		this.samService = samService;
	}

	public Map<String, ApiDO> getApis() {
		return this.apis;
	}

	public void setApis(Map<String, ApiDO> apis) {
		this.apis = apis;
	}

	public void setApiEngine(ApiEngine apiEngine) {
		this.apiEngine = apiEngine;
	}
	
	public String getModelFileKey() {
		return this.modelFileKey;
	}

	public void setModelFileKey(String modelFileKey) {
		this.modelFileKey = modelFileKey;
	}

	public Long getTimApiType() {
		return this.timApiType;
	}

	public void setTimApiType(Long timApiType) {
		this.timApiType = timApiType;
	}

	public TfsManager getTfsManager() {
		return this.tfsManager;
	}

	public void setTfsManager(TfsManager tfsManager) {
		this.tfsManager = tfsManager;
	}

	public String getErrorResponse(String errorCode, String errorMsg, Map<String, String> subTaskRequest) {
		Map<String, String> sourceMap = new HashMap<String, String>();
		String code = ErrorCode.UNKNOWN_ERROR;
		String msg = "此错误未被详细说明";

		if (StringUtils.isNotBlank(errorCode)) {
			code = errorCode;
		}
		if (StringUtils.isNotBlank(errorMsg)) {
			msg = errorMsg;
		}

		sourceMap.put(ErrorCode.SUB_CODE, code);
		sourceMap.put(ErrorCode.SUB_MSG, msg);

		//如果是xml格式的请求就转成xml格式的，否则就转成json格式的
		if(KeyConstants.FORMAT_XML.equals(subTaskRequest.get(KeyConstants.FORMAT))) {
			return TaskParser.fromMapToXml(sourceMap);
		} else {
			return TaskParser.fromMapToJson(sourceMap);
		}
	}

	public String getIngoreErrorResponse(String taskName, Map<String, String> subTaskRequest)
			throws AtsException {
		ApiDO api = apis.get(taskName);

		if (null == api) {
			throw new AtsException("指定的API不存在");
		}
		
		//取出format参数进行判断 moling 2010-11-19
		String format = subTaskRequest.get(KeyConstants.FORMAT);
		//要组装的结果串
		String[] sendOuts = null;
		
		//如果不是xml的格式的认为，默认为json
		if (KeyConstants.FORMAT_XML.equals(format)) {
			sendOuts = api.getSubTaskSendOutResultsXml();
		} else {
			format = KeyConstants.FORMAT_JSON;
			sendOuts = api.getSubTaskSendOutResultsJson();
		}

		//没有format指定的结果就判错
		if (null == sendOuts || sendOuts.length < 1) {
			throw new AtsException("此API没有可以忽略的错误配置结果信息：" + taskName);
		}

		StringBuffer sb = new StringBuffer();
		
		//增加json和xml的判断，找不到标记位默认为json
		

		for (int i = 0; i < sendOuts.length; ++i) {
			// 如果是排在偶数位上，就将值直接append到后面
			if (i % 2 == 0) {
				sb.append(sendOuts[i]);
			} else {
				// 如果是奇数位，就取业务参数放进来
				// 如果需要mock的格式是时间，new一个时间返回，否则从入参中拿这个参数
				if (KeyConstants.CREATED.equals(sendOuts[i])
						|| KeyConstants.MODIFIED.equals(sendOuts[i])) {
					// 设置modified时间过滤
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					sb.append(sdf.format(new Date()));
				} else {
					sb.append(subTaskRequest.get(sendOuts[i]));
				}
			}
		}
		return sb.toString();
	}

	public String getRetrunRequest(String taskName, Map<String, String> subTaskRequest)
			throws AtsException {
		ApiDO api = apis.get(taskName);

		if (null == api) {
			throw new AtsException("指定的api不存在");
		}
		
		String format = subTaskRequest.get(KeyConstants.FORMAT);

		// 如果没有指定返回参数记录空串"{}"
		if (null == api.getSubTaskSendOutRequests() || api.getSubTaskSendOutRequests().isEmpty()) {
			//如果是xml格式的请求就转成xml格式的，否则就转成json格式的
			if(KeyConstants.FORMAT_XML.equals(format)) {
				return  KeyConstants.NULL_XML_STRING;
			} else {
				return  KeyConstants.NULL_JSON_STRING;
			}
		}

		Map<String, String> requestMap = new HashMap<String, String>();
		for (String request : api.getSubTaskSendOutRequests()) {
			requestMap.put(request, subTaskRequest.get(request));
		}

		//如果是xml格式的请求就转成xml格式的，否则就转成json格式的
		if(KeyConstants.FORMAT_XML.equals(format)) {
			return TaskParser.fromMapToXml(requestMap);
		} else {
			return TaskParser.fromMapToJson(requestMap);
		}
	}

	public AtsTaskDO getTask(Map<String, String> request) throws AtsException {
		// TIP默认会将API的名称放在method里面，根据这个字段可以获得任务模板
		String taskName = request.get(KeyConstants.METHOD);
		ApiDO api = apis.get(taskName);
		if (null == api) {
			throw new AtsException("指定的api不存在");
		}

		AtsTaskDO task = new AtsTaskDO();
		// 设置任务API的名称
		task.setApiName(taskName);
		// APPKEY可能在两个字段上，都取一下，确保不漏掉
		String appKey = request.get(KeyConstants.APP_KEY);
		if (StringUtils.isBlank(appKey)) {
			appKey = request.get(KeyConstants.API_KEY);
		}
		task.setAppKey(appKey);
		// 默认创建时间为当前时间
		task.setGmtCreated(new Date());
		// 根据模板文件配置的优先级赋值 moling	2010-11-18
		task.setPriority(api.getLevel());
		// 初始化重试次数为0次
		task.setRetries(0);
		// 初始化任务状态为new
		task.setStatus(StatusConstants.TASK_STATUS_NEW.getStatus());
		// 设置任务是否大任务,是大任务在api中增加模板，否则不加（节约字段） moling 2010-11-18
		if (api.getIsBigResult()) {
			task.addAttributes(ModelKeyConstants.IS_BIG_RESULT, ModelKeyConstants.BOOLEAN_ATTRIBUTE_MARK);
		}
		//将format加入attribute中，兼容旧结果和任务结果记录需要 moling 2010-11-18
		task.addAttributes(KeyConstants.FORMAT, request.get(KeyConstants.FORMAT));
		

		// 填充子任务模板，将公有参数填充到一个demo中
		SubAtsTaskDO subTaskDemo = new SubAtsTaskDO();
		// 子任务状态初始化为new
		subTaskDemo.setStatus(StatusConstants.SUBTASK_STATUS_NEW.getStatus());
		// 初始时间为当前时间
		subTaskDemo.setGmtCreated(new Date());

		// 设置共有调用后方服务的系统参数
		subTaskDemo.addRequestParameter(ModelKeyConstants.SERVICE_INTERFACE, api.getSubTaskInterface());
		subTaskDemo.addRequestParameter(ModelKeyConstants.SERVICE_METHOD, api.getSubTaskMethod());
		subTaskDemo.addRequestParameter(ModelKeyConstants.SERVICE_TYPE, api.getSubTaskType());
		subTaskDemo.addRequestParameter(ModelKeyConstants.SERVICE_VERSION, api.getSubTaskVersion());
		subTaskDemo.addRequestParameter(KeyConstants.APP_KEY, appKey);
		subTaskDemo.addRequestParameter(KeyConstants.SESSION_NICK, request.get(KeyConstants.SESSION_NICK));
		subTaskDemo.addRequestParameter(KeyConstants.SESSION_UID, request.get(KeyConstants.SESSION_UID));
		subTaskDemo.addRequestParameter(KeyConstants.APP_IP, request.get(KeyConstants.APP_IP));
		subTaskDemo.addRequestParameter(KeyConstants.ENDUSER_IP, request.get(KeyConstants.ENDUSER_IP));
		subTaskDemo.addRequestParameter(KeyConstants.TOP_TAG, request.get(KeyConstants.TOP_TAG));
		subTaskDemo.addRequestParameter(KeyConstants.TOP_BIND_NICK, request.get(KeyConstants.TOP_BIND_NICK));
		subTaskDemo.addRequestParameter(KeyConstants.TOP_ISV_ID, request.get(KeyConstants.TOP_ISV_ID));
		subTaskDemo.addRequestParameter(KeyConstants.TIMESTAMP, request.get(KeyConstants.TIMESTAMP));
		//将创建任务传入的format设置到每个子任务中
		subTaskDemo.addRequestParameter(KeyConstants.FORMAT, request.get(KeyConstants.FORMAT));
		//默认设置ats调用的标识
		subTaskDemo.addRequestParameter(KeyConstants.IS_ATS, Boolean.TRUE.toString());

		// 设置共有调用后方服务的业务参数
		List<ParameterDO> keepParams = api.getParamsRule().getParamsKeep();
		if (null != keepParams && !keepParams.isEmpty()) {
			for (ParameterDO param : keepParams) {
				subTaskDemo.addRequestParameter(param.getTargetName(), request.get(param.getName()));
			}
		}

		// 设置需要拆分的后方业务参数
		// 先准备好一个子任务的list
		List<SubAtsTaskDO> subTasks = new ArrayList<SubAtsTaskDO>();
		
		//FIXME range类型的分割和parse类型的参数分割不支持同时出现
		RangeParameterDO rangeParams = api.getParamsRule().getParamRange();
		List<ParameterDO> parseParams = api.getParamsRule().getParamsParse();
		if (null != rangeParams) {
			//拆分任务时只拆分range的参数，分页相关的参数需要等到调用的时候再加入（没有totalResult无法知道多少页）
			String minValue = request.get(rangeParams.getMinName());
			String maxValue = request.get(rangeParams.getMaxName());
			//此处不在进行范围值判断。郊游参数校验的方法挡掉非法请求。目前只支持date和number两种类型
			if (ModelKeyConstants.RANGE_TYPE_DATE.equals(rangeParams.getRangeType())) {
				String nextValue = RangeParseUtil.getNextDate(minValue, maxValue, rangeParams.getParsePeriod(), rangeParams.getParseMeasure()); 
				while (StringUtils.isNotBlank(nextValue)) {
					SubAtsTaskDO subTask =  subTaskDemo.clone();
					subTask.addRequestParameter(rangeParams.getTargetMinName(), minValue);
					subTask.addRequestParameter(rangeParams.getTargetMaxName(), nextValue);
					// 得到json格式的入参
					subTask.setRequest(TaskParser.fromMapToJson(subTask.getRequestMap()));
					subTasks.add(subTask);
					
					//将剩余的minValue同步到nextValue上
					minValue = nextValue;
					nextValue = RangeParseUtil.getNextDate(minValue, maxValue, rangeParams.getParsePeriod(), rangeParams.getParseMeasure()); 
				}
			} else if (ModelKeyConstants.RANGE_TYPE_NUMBER.equals(rangeParams.getRangeType())) {
				String nextValue = RangeParseUtil.getNextNumber(minValue, maxValue, rangeParams.getParsePeriod()); 
				while (StringUtils.isNotBlank(nextValue)) {
					SubAtsTaskDO subTask =  subTaskDemo.clone();
					subTask.addRequestParameter(rangeParams.getTargetMinName(), minValue);
					subTask.addRequestParameter(rangeParams.getTargetMaxName(), String.valueOf(Long.valueOf(nextValue) - 1));
					// 得到json格式的入参
					subTask.setRequest(TaskParser.fromMapToJson(subTask.getRequestMap()));
					subTasks.add(subTask);
					
					//将剩余的minValue同步到nextValue+1上
					minValue = nextValue;
					nextValue = RangeParseUtil.getNextNumber(minValue, maxValue, rangeParams.getParsePeriod()); 
				}
			}
		} else if (null != parseParams && !parseParams.isEmpty()) {
			// map的key为target_name,value为拆分后的target的value
			Map<String, String[]> params = new HashMap<String, String[]>();
			int length = 0;
			for (ParameterDO param : parseParams) {
				String requestParam = request.get(param.getName());
				if (StringUtils.isNotEmpty(requestParam)) {
					String[] paramList = requestParam.split(param.getSeparator(), -1);
					params.put(param.getTargetName(), paramList);
					// 所有可分割的长度在check的时候已经判断长度一致，这里随便哪个的值都是ok的。
					length = paramList.length;
				}
			}

			// 如果没有分割的任务最后在外面统一处理 moling 2010-11-18
			if (length > 0) {
				for (int i = 0; i < length; ++i) {
					SubAtsTaskDO subTask = subTaskDemo.clone();
					for (Entry<String, String[]> iterator : params.entrySet()) {
						subTask.addRequestParameter(iterator.getKey(), iterator.getValue()[i]);
					}
					// 得到json格式的入参
					subTask.setRequest(TaskParser.fromMapToJson(subTask.getRequestMap()));
					// 将clone并填入新入参的子任务加入子任务列表中
					subTasks.add(subTask);
				}
			}
		}
		
		//如果最后拆完没有子任务列表，将模板子任务加入其中
		if(subTasks.isEmpty()) {
			subTaskDemo.setRequest(TaskParser.fromMapToJson(subTaskDemo.getRequestMap()));
			subTasks.add(subTaskDemo);
		}

		task.setSubTasks(subTasks);
		return task;
	}

	public void init() throws Exception {
//		// 没有任务模版路径启动报错
//		if (null == taskTemplatePath || taskTemplatePath.isEmpty()) {
//			throw new XmlException("没有配置任务模板的路径");
//		}
//
//		// 初始化APIs
//		apis = new HashMap<String, ApiDO>();
//
//		for (Resource resource : taskTemplatePath) {
//			ApiDO api = TaskParser.parseTaskTemplate(resource.getInputStream());
//
//			// 这个情况不可能发生，放在这里保险
//			if (null == api) {
//				throw new XmlException("配置的任务模板的路径解析不到正确的模板");
//			}
//
//			// 将非空的模板API加入MAP中
//			apis.put(api.getTaskName(), api);
//		}
		
		//新的刷模板缓存方法
		//为了保证所有的hsf配置都注入成功并生效，刷缓存前先sleep 5s
		Thread.sleep(5000);
		if (!refreshApiModels()) {
			throw new XmlException("初始化任务模板错误");
		}

		// API HSF服务预热
		Collection<ApiDO> apiDOs = apis.values();
		for (ApiDO apiDO : apiDOs) {
			ApiRequest request = new ApiRequest();
			request.setInterfaceName(apiDO.getSubTaskInterface());
			request.setInterfaceVersion(apiDO.getSubTaskVersion());
			request.setInterfaceMethod(apiDO.getSubTaskMethod());
			try {
				apiEngine.invokeApi(request);
			} catch (Exception e) {
			}
		}
	}

	public boolean isIgnoreError(String taskName, String errorCode) throws AtsException {
		ApiDO api = apis.get(taskName);

		if (null == api) {
			throw new AtsException("指定的api不存在");
		}

		if (StringUtils.isNotBlank(errorCode) && null != api.getSubTaskIgnoreFails()
				&& !api.getSubTaskIgnoreFails().isEmpty()) {
			if (api.getSubTaskIgnoreFails().contains(errorCode)) {
				return true;
			}
		}

		return false;
	}

	public ResultSet isPreCheckOK(Map<String, String> request) {
		ResultSet result = new ResultSet();

		// tip默认会将api的名称放在method里面，根据这个字段可以获得任务模板
		String taskName = request.get(KeyConstants.METHOD);
		if (StringUtils.isBlank(taskName)) {
			result.setErrorCode(ErrorCode.MISSING_PARAMETER);
			result.setErrorMsg("入参缺少method");
			return result;
		}
		ApiDO api = apis.get(taskName);
		if (null == api) {
			result.setErrorCode(ErrorCode.API_NOT_EXIST);
			result.setErrorMsg("指定的api不存在");
			return result;
		}

		// 四个前置校验参数一定会同时存在，判断其中一个即可
		if (StringUtils.isBlank(api.getPreCheckInterface())) {
			return result;
		}

		ApiRequest apiRequest = new ApiRequest();
		// 填入前置校验的hsf相关服务
		apiRequest.setInterfaceMethod(api.getPreCheckMethod());
		apiRequest.setInterfaceName(api.getPreCheckInterface());
		apiRequest.setInterfaceVersion(api.getPreCheckVersion());

		// 将参数直接传到后面去，靠后台的mapping来获取需要的参数
		apiRequest.setParameters(request);
		
		ApiResponse response = apiEngine.invokeApi(apiRequest);
		if (response.isError()) {
			result.setErrorCode(response.getErrCode());
			result.setErrorMsg(response.getErrMsg());
			return result;
		} else if (PRECHECK_SUCCESS_JSON.equals(response.getResponse()) || PRECHECK_SUCCESS_XML.equals(response.getResponse())) {
			//增加子任务结果校验,必须是规定的结果才通过
			return result;
		} else {
			result.setErrorCode(ErrorCode.PRECHECK_RESULT_UNKNOWN);
			result.setErrorMsg("后台入参校验出现无法判断结果");
			return result;
		}
	}

	public ResultSet isRequestRight(Map<String, String> request) {
		ResultSet result = new ResultSet();

		// tip默认会将api的名称放在method里面，根据这个字段可以获得任务模板
		String taskName = request.get(KeyConstants.METHOD);
		if (StringUtils.isBlank(taskName)) {
			result.setErrorCode(ErrorCode.MISSING_PARAMETER);
			result.setErrorMsg("入参缺少method");
			return result;
		}

		ApiDO api = apis.get(taskName);
		if (null == api) {
			result.setErrorCode(ErrorCode.API_NOT_EXIST);
			result.setErrorMsg("指定的api不存在");
			return result;
		}

		ParamsDO rules = api.getParamsRule();
		// 没有入参校验规则，默认校验正确
		if (null == rules) {
			return result;
		}

		// 校验前置校验参数
		if (null != rules.getParamsCheck() && !rules.getParamsCheck().isEmpty()) {
			for (ParameterDO param : rules.getParamsCheck()) {
				checkParameter(request.get(param.getName()), param, result);
				if (result.isError()) {
					return result;
				}
			}
		}

		// 校验不拆分参数
		if (null != rules.getParamsKeep() && !rules.getParamsKeep().isEmpty()) {
			for (ParameterDO param : rules.getParamsKeep()) {
				checkParameter(request.get(param.getName()), param, result);
				if (result.isError()) {
					return result;
				}
			}
		}
		
		//校验period类型需要拆分的参数 moling 2010-11-20
		if (null != rules.getParamRange()) {
			checkRangeParameter(request, result, taskName, rules);
			if (result.isError()) {
				return result;
			}
		}

		// 校验前置需要拆分的参数
		if (null != rules.getParamsParse() && !rules.getParamsParse().isEmpty()) {
			// 准备好一个长度要一致的参数列表，报错信息时用
			StringBuffer sb = new StringBuffer();
			// 先进行参数解析
			List<String[]> parameters = new ArrayList<String[]>();
			for (ParameterDO param : rules.getParamsParse()) {
				String value = request.get(param.getName());

				// 校验这个参数的格式的空
				checkParameter(value, param, result);
				if (result.isError()) {
					return result;
				}

				// 不为空的都要校验，为空在每个参数校验的时候保证
				if (StringUtils.isNotEmpty(value)) {
					String[] values = value.split(param.getSeparator(), -1);
					parameters.add(values);
				}

				sb.append(param.getName());
				sb.append(",");
			}

			// 有大于等于一个parse的参数才需要进行长度校验
			if (parameters.size() > 0) {
				// 取第一个参数的长度做为标准
				int length = parameters.get(0).length;
				for (String[] parameter : parameters) {
					// 如果有长度不一致，报错
					if (parameter.length != length) {
						sb.append("长度不一致");
						result.setErrorCode(ErrorCode.MISMATCH_PARAMETER);
						result.setErrorMsg(sb.toString());
						return result;
					}
				}

				if ((null != rules.getParamsMinSize() && length < rules.getParamsMinSize())
						|| (null != rules.getParamsMaxSize() && length > rules.getParamsMaxSize())) {
					sb.append("长度超过限制区域：");
					sb.append(rules.getParamsMinSize());
					sb.append("~");
					sb.append(rules.getParamsMaxSize());
					result.setErrorCode(ErrorCode.INVALID_PARAMETER);
					result.setErrorMsg(sb.toString());
					return result;
				}
			}

		}

		return result;
	}

	public void checkRangeParameter(Map<String, String> request,
			ResultSet result, String taskName, ParamsDO rules) {
		//最小值和最大值不能为空校验
		String minValueString = request.get(rules.getParamRange().getMinName());
		if (StringUtils.isBlank(minValueString)) {
			result.setErrorCode(ErrorCode.MISSING_PARAMETER + ":" + rules.getParamRange().getMinName());
			result.setErrorMsg("参数" + rules.getParamRange().getMinName() + "不能有为空的值");
			return;
		}
		String maxValueString = request.get(rules.getParamRange().getMaxName());
		if (StringUtils.isBlank(maxValueString)) {
			result.setErrorCode(ErrorCode.MISSING_PARAMETER + ":" + rules.getParamRange().getMaxName());
			result.setErrorMsg("参数" + rules.getParamRange().getMaxName() + "不能有为空的值");
			return;
		}
		
		//两种类型的分片参数统一转成Long型的处理,最大间隔计算成值Long统一处理
		Long minValue = null;
		Long maxValue = null;
		Long maxRange = null;
		
		//参数类型和区间限制校验
		if (ModelKeyConstants.RANGE_TYPE_DATE.equals(rules.getParamRange().getRangeType())) {
			if (!StringKit.isDate(minValueString)) {
				result.setErrorCode(ErrorCode.INVALID_PARAMETER + ":" + rules.getParamRange().getMinName());
				result.setErrorMsg("参数" + rules.getParamRange().getMinName() + "不符合格式，不是时间类型");
				return;
			}
			try {
				minValue = DateKit.ymdOrYmdhms2Date(minValueString).getTime();
			} catch (Exception e) {
				result.setErrorCode(ErrorCode.INVALID_PARAMETER + ":" + rules.getParamRange().getMinName());
				result.setErrorMsg("参数" + rules.getParamRange().getMinName() + "格式错误，无法解析");
				return;
			}
			
			if (!StringKit.isDate(maxValueString)) {
				result.setErrorCode(ErrorCode.INVALID_PARAMETER + ":" + rules.getParamRange().getMaxName());
				result.setErrorMsg("参数" + rules.getParamRange().getMaxName() + "不符合格式，不是时间类型");
				return;
			}
			try {
				maxValue = DateKit.ymdOrYmdhms2Date(maxValueString).getTime();
			} catch (Exception e) {
				result.setErrorCode(ErrorCode.INVALID_PARAMETER + ":" + rules.getParamRange().getMaxName());
				result.setErrorMsg("参数" + rules.getParamRange().getMaxName() + "格式错误，无法解析");
				return;
			}
			
			if (ModelKeyConstants.MEASURE_HOUR.equals( rules.getParamRange().getMaxMeasure())) {
				maxRange = DateUtils.addHours(new Date(minValue), rules.getParamRange().getMaxPeriod()).getTime();
			} else if (ModelKeyConstants.MEASURE_DAY.equals( rules.getParamRange().getMaxMeasure())) {
				maxRange = DateUtils.addDays(new Date(minValue), rules.getParamRange().getMaxPeriod()).getTime();
			} else if (ModelKeyConstants.MEASURE_MONTH.equals( rules.getParamRange().getMaxMeasure())) {
				maxRange = DateUtils.addMonths(new Date(minValue), rules.getParamRange().getMaxPeriod()).getTime();
			}else {
				//模板目前只支持这三种格式，其他格式模板解析会出错的，此处不予保证,模板目前不允许走到此处
				if (log.isErrorEnabled()) {
					log.error("模板" + taskName + "出现无法解析的拆分单位");
				}
				return;
			}
		} else if (ModelKeyConstants.RANGE_TYPE_NUMBER.equals(rules.getParamRange().getRangeType())) {
			if (!StringKit.isUnsignedNumber(minValueString)) {
				result.setErrorCode(ErrorCode.INVALID_PARAMETER + ":" + rules.getParamRange().getMinName());
				result.setErrorMsg("参数" + rules.getParamRange().getMinName() + "不符合格式，不是数字类型");
				return;
			}
			minValue = Long.valueOf(minValueString);
			
			if (!StringKit.isUnsignedNumber(maxValueString)) {
				result.setErrorCode(ErrorCode.INVALID_PARAMETER + ":" + rules.getParamRange().getMaxName());
				result.setErrorMsg("参数" + rules.getParamRange().getMaxName() + "不符合格式，不是数字类型");
				return;
			}
			maxValue = Long.valueOf(maxValueString);
			
			maxRange = minValue + rules.getParamRange().getMaxPeriod();
		} else {
			//其他类型暂不支持解析,模板目前不允许走到此处
			if (log.isErrorEnabled()) {
				log.error("模板" + taskName + "出现无法解析的拆分类型");
			}
			return;
		}
		
		//最大值最小值边界判断
		if (minValue >= maxValue) {
			result.setErrorCode(ErrorCode.INVALID_PARAMETER + ":" + rules.getParamRange().getMinName() + 
					"," + rules.getParamRange().getMaxName());
			result.setErrorMsg("参数" + rules.getParamRange().getMinName() + "必需小于" + 
					rules.getParamRange().getMaxName());
			return;
		}
		
		//最小值小于边界报错
		if (null != rules.getParamRange().getMinValue() && minValue < rules.getParamRange().getMinValue()) {
			result.setErrorCode(ErrorCode.INVALID_PARAMETER + ":" + rules.getParamRange().getMinName());
			//时间类型的最小值需要转换后再报错
			if (ModelKeyConstants.RANGE_TYPE_DATE.equals(rules.getParamRange().getRangeType())) {
				Date minDate = new Date(rules.getParamRange().getMinValue());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				result.setErrorMsg("参数" + rules.getParamRange().getMinName() + "不能小于" + 
						sdf.format(minDate));
			} else {
				//否则直接用数字类型报错
				result.setErrorMsg("参数" + rules.getParamRange().getMinName() + "不能小于" + 
						rules.getParamRange().getMinValue());
			}
			return;
		}
		
		//最大值大于边界报错
		if (null != rules.getParamRange().getMaxValue() && maxValue > rules.getParamRange().getMaxValue()) {
			result.setErrorCode(ErrorCode.INVALID_PARAMETER + ":" + rules.getParamRange().getMaxName());
			if (ModelKeyConstants.RANGE_TYPE_DATE.equals(rules.getParamRange().getRangeType())) {
				//时间类型的最大值需要转换后再报错
				Date maxDate = new Date(rules.getParamRange().getMaxValue());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				result.setErrorMsg("参数" + rules.getParamRange().getMaxName() + "不能大于" + 
						sdf.format(maxDate));
			} else {
				//否则直接用数字类型报错
				result.setErrorMsg("参数" + rules.getParamRange().getMaxName() + "不能大于" + 
						rules.getParamRange().getMaxValue());
			}
			
			return;
		}
		
		//间隔区间校验
		if (maxValue > maxRange) {
			result.setErrorCode(ErrorCode.INVALID_PARAMETER + ":" + rules.getParamRange().getMinName() + 
					"," + rules.getParamRange().getMaxName());
			result.setErrorMsg("参数" + rules.getParamRange().getMinName() + "和" + 
					rules.getParamRange().getMaxName() + "间隔区间超过大小限制");
			return;
		}
	}

	public boolean isRetryError(String taskName, String errorCode) throws AtsException {
		ApiDO api = apis.get(taskName);

		if (null == api) {
			throw new AtsException("指定的api不存在");
		}

		if (StringUtils.isNotBlank(errorCode) && null != api.getSubTaskRetryFails()
				&& !api.getSubTaskRetryFails().isEmpty()) {
			if (api.getSubTaskRetryFails().contains(errorCode)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * 校验参数是否可以为空和参数的类型
	 * 
	 * @param value
	 * @param param
	 * @param result
	 */
	public void checkParameter(String value, ParameterDO param, ResultSet result) {
		if (param.getEnableSplit()) {
			if (StringUtils.isEmpty(value)) {
				// 标记为可以分割的参数，但是没有被传入值，直接整个校验
				checkType(value, param, result);
				return;
			}
			// 分割参数一个一个校验
			String[] values = value.split(param.getSeparator(), -1);
			for (int i = 0; i < values.length; ++i) {
				checkType(values[i], param, result);
				// 只要中间有参数错误，直接返回
				if (result.isError()) {
					return;
				}
			}
		} else {
			checkType(value, param, result);
		}

	}

	public void checkType(String value, ParameterDO param, ResultSet result) {
		// 如果参数标记了不能为null，但是却为null或""了，报错
		if (Boolean.FALSE.equals(param.getTargetEnableNull()) && StringUtils.isEmpty(value)) {
			result.setErrorCode(ErrorCode.INVALID_PARAMETER + ":" + param.getName());
			result.setErrorMsg("参数" + param.getName() + "不能有为空的值");
			return;
		}

		if (StringUtils.isNotEmpty(value)) {
			if ("number".equals(param.getTargetType())) {
				if (!StringKit.isUnsignedNumber(value)) {
					result.setErrorCode(ErrorCode.INVALID_PARAMETER + ":" + param.getName());
					result.setErrorMsg("参数" + param.getName() + "不符合格式，不是数字类型");
					return;
				}
			} else if ("date".equals(param.getTargetType())) {
				if (!StringKit.isDate(value)) {
					result.setErrorCode(ErrorCode.INVALID_PARAMETER + ":" + param.getName());
					result.setErrorMsg("参数" + param.getName() + "不符合格式，不是时间类型");
					return;
				}
			} else if ("price".equals(param.getTargetType())) {
				if (!StringKit.isPrice(value)) {
					result.setErrorCode(ErrorCode.INVALID_PARAMETER + ":" + param.getName());
					result.setErrorMsg("参数" + param.getName() + "不符合格式，不是价格类型");
					return;
				}
			} else if ("boolean".equals(param.getTargetType())) {
				if (!StringKit.isBoolean(value)) {
					result.setErrorCode(ErrorCode.INVALID_PARAMETER + ":" + param.getName());
					result.setErrorMsg("参数" + param.getName() + "不符合格式，不是布尔类型");
					return;
				}
			}
		}
	}

	public boolean isApiIntime(String taskName, Date date) {
		if (StringUtils.isBlank(taskName) || null == date) {
			return false;
		}
		
		ApiDO api = apis.get(taskName);
		if (null == api) {
			return false;
		}
		
		if (0 == api.getLevel() || 1 == api.getLevel()) {
			return true;
		} else if (2 == api.getLevel()) {
			if (null != api.getTimePeriods() && !api.getTimePeriods().isEmpty()) {
				for (TimePeriod timePeriod : api.getTimePeriods()) {
					if (timePeriod.isInTimePeriod(date)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public Map<String, String> getPageDownRequest(String taskName,
			Map<String, String> subTaskRequest, Long totalResult) {
		ApiDO api = apis.get(taskName);

		if (null == api) {
			return null;
		}
		
		if (null == api.getParamsRule() || null == api.getParamsRule().getParamRange() || 
				!api.getParamsRule().getParamRange().getEnablePage()) {
			return null;
		}
		
		String page = subTaskRequest.get(api.getParamsRule().getParamRange().getPageNoName());
		Long pageNo = null;
		//如果已经调用过翻页了，在现有基础上向后走
		if (StringUtils.isNotBlank(page)) {
			if (null == totalResult) {
				//如果翻过页但是没有传总结果过来的，不支持再翻页
				return null;
			}
			pageNo = Long.valueOf(page) + 1;
			//计算翻页是否已经结束了,已结束的不用再翻页
			if (((pageNo - 1) * api.getParamsRule().getParamRange().getPageSize()) >= totalResult) {
				return null;
			}
		} else {
			//如果没有翻过页，默认从第一页开始翻
			pageNo = 1L;
		}
		
		subTaskRequest.put(api.getParamsRule().getParamRange().getPageNoName(), pageNo.toString());
		subTaskRequest.put(api.getParamsRule().getParamRange().getPageSizeName(), 
				api.getParamsRule().getParamRange().getPageSize().toString());
		
		return subTaskRequest;
	}

	public boolean isBigResult(String taskName) throws AtsException {
		ApiDO api = apis.get(taskName);

		if (null == api) {
			throw new AtsException("指定的API不存在");
		}
		
		if (api.getIsBigResult()){ 
			return true;
		} else {
			return false;
		}
	}

	public boolean isBigResult(Map<String, String> attributes) {
		if (null == attributes || attributes.isEmpty()) {
			return false;
		}
		
		String isBig = attributes.get(ModelKeyConstants.IS_BIG_RESULT);
		if (ModelKeyConstants.BOOLEAN_ATTRIBUTE_MARK.equals(isBig)) {
			return true;
		} else {
			return false;
		}
		
	}

	public boolean isPageNeeded(String taskName) throws AtsException {
		ApiDO api = apis.get(taskName);

		if (null == api) {
			throw new AtsException("指定的API不存在");
		}
		
		if (null == api.getParamsRule() || null == api.getParamsRule().getParamRange()) {
			//防止空指针
			return false;
		} else if (api.getParamsRule().getParamRange().getEnablePage()) {
			return true;
		} else {
			return false;
		}
		
	}

	public boolean refreshApiModels() {
		try {
			List<ExPropertyDO> propsList = samService.getExtPropertyByKeyAndType(modelFileKey, timApiType);
			if (null == propsList || propsList.isEmpty()) {
				//tim读取不到模板列表
				if (log.isErrorEnabled()) {
					log.error("tim读取模板列表为空，无法刷新缓存列表！");
				}
				return false;
			}
			
			HashMap<String, ApiDO> newApis = new HashMap<String, ApiDO>();
			
			for (ExPropertyDO prop: propsList) {
				String path = prop.getValue();
				if (StringUtils.isBlank(path)) {
					//tim读取到的定义文件路径为空,只要有数据有问题，先启动
					if (log.isErrorEnabled()) {
						log.error("tim读取到的路径为空" + prop.getId() + "，无法刷新缓存列表！");
					}
					return false;
				} else {
					ByteArrayOutputStream bos = null;
					try {
						bos = new ByteArrayOutputStream();
						// 从TFS上获得任务执行结果
						boolean fetchResult = tfsManager.fetchFile(path, null, bos);
						if (!fetchResult) {
							if (log.isErrorEnabled()) {
								log.error("tfs读取模板失败" + path + "，无法刷新缓存列表！");
							}
							return false;
						}
						try {
							ApiDO api = TaskParser.parseTaskTemplate(bos.toInputStream());
							newApis.put(api.getTaskName(), api);
						} catch (Exception e) {
							if (log.isErrorEnabled()) {
								log.error("tfs读取到的模板解析失败" + path + "，无法刷新缓存列表！" + e.getMessage());
							}
							return false;
						}
					} finally {
						IOUtils.closeQuietly(bos);
					}
				}
			}
			
			if (!newApis.isEmpty()) {
				//全部刷新成功更新
				apis = newApis;
				if (log.isWarnEnabled()) {
					log.warn("刷新缓存成功:" + newApis.size());
				}
				return true;
			} else {
				if (log.isWarnEnabled()) {
					log.warn("刷新缓存失败:无法到合法状态的api模板");
				}
				return false;
			}
			
		} catch (TIMServiceException e) {
			//tim异常刷新失败
			if (log.isErrorEnabled()) {
				log.error("tim服务异常，无法刷新缓存列表！" + e.getMessage());
			}
			return false;
		}
		
	}

}
