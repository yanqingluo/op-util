package com.taobao.top.ats.task.api;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;

import com.taobao.top.ats.AtsException;
import com.taobao.top.ats.XmlException;
import com.taobao.top.ats.domain.ApiDO;
import com.taobao.top.ats.domain.ParameterDO;
import com.taobao.top.ats.domain.ParamsDO;
import com.taobao.top.ats.domain.RangeParameterDO;
import com.taobao.top.ats.domain.TimePeriod;
import com.taobao.top.ats.util.DateKit;
import com.taobao.top.ats.util.ModelKeyConstants;
import com.taobao.top.ats.util.XmlUtils;

/**
 * 任务模板解决器。
 * 
 * @author moling
 * @since 1.0, 2010-8-20
 */
public class TaskParser {
	public static final char LT = '<';

	public static final char RT = '>';

	public static final String LTS = "</";

	public static ApiDO parseTaskTemplate(InputStream is) throws AtsException, ParseException {
		Element root = XmlUtils.getRootElementFromStream(is);
		ApiDO apiDO = new ApiDO();

		// 取得API对外的名字
		String taskName = root.getAttribute(ModelKeyConstants.NAME);
		if (StringUtils.isBlank(taskName)) {
			throw new XmlException("任务模板没有设置任务名字");
		} else {
			apiDO.setTaskName(taskName);
		}

		// 解释pre_check节点
		Element preCheckE = XmlUtils.getElement(root, ModelKeyConstants.PRE_CHECK);
		if (preCheckE != null) {
			apiDO.setPreCheckType(preCheckE.getAttribute(ModelKeyConstants.SERVICE_TYPE));
			apiDO.setPreCheckInterface(preCheckE.getAttribute(ModelKeyConstants.SERVICE_INTERFACE));
			apiDO.setPreCheckMethod(preCheckE.getAttribute(ModelKeyConstants.SERVICE_METHOD));
			apiDO.setPreCheckVersion(preCheckE.getAttribute(ModelKeyConstants.SERVICE_VERSION));
		}
		
		//解释priority节点
		Element priorityE = XmlUtils.getElement(root, ModelKeyConstants.PRIORITY);
		if (priorityE != null) {
			String level = priorityE.getAttribute(ModelKeyConstants.LEVEL);
			if ("0".equals(level)) {
				apiDO.setLevel(ModelKeyConstants.LEVEL_HIGH);
			} else if ("2".equals(level)) {
				apiDO.setLevel(ModelKeyConstants.LEVEL_LOW);
				
				//只有优先级为2的api需要解析执行区间
				String period = priorityE.getAttribute(ModelKeyConstants.TIME_PERIOD);
				if (StringUtils.isBlank(period)) {
					throw new XmlException("空闲时任务没有指定空闲时间，无法处理");
				}
				
				String[] periods = period.split(";");
				if (null == periods || periods.length == 0) {
					throw new XmlException("空闲时间为空，无法处理");
				}
				
				List<TimePeriod> timePeriods = new ArrayList<TimePeriod>();
				for (int i = 0; i < periods.length; ++i) {
					timePeriods.add(parseString2TimePeriod(periods[i]));
				}
				
				apiDO.setTimePeriods(timePeriods);
			} else {
				//默认情况都是普通level的
				apiDO.setLevel(ModelKeyConstants.LEVEL_NORMAL);
			}
			
			String isBigResult = priorityE.getAttribute(ModelKeyConstants.IS_BIG_RESULT);
			if ("true".equals(isBigResult)) {
				apiDO.setIsBigResult(true);
			} else {
				apiDO.setIsBigResult(false);
			}
		} else {
			//取不到优先级节点默认为普通level
			apiDO.setLevel(ModelKeyConstants.LEVEL_NORMAL);
			apiDO.setIsBigResult(false);
		}

		// 解释sub_task节点
		Element subTaskE = XmlUtils.getElement(root, ModelKeyConstants.SUB_TASK);
		if (subTaskE != null) {
			apiDO.setSubTaskType(subTaskE.getAttribute(ModelKeyConstants.SERVICE_TYPE));
			apiDO.setSubTaskInterface(subTaskE.getAttribute(ModelKeyConstants.SERVICE_INTERFACE));
			apiDO.setSubTaskMethod(subTaskE.getAttribute(ModelKeyConstants.SERVICE_METHOD));
			apiDO.setSubTaskVersion(subTaskE.getAttribute(ModelKeyConstants.SERVICE_VERSION));
		}

		// 解释params节点
		Element paramsE = XmlUtils.getElement(root, ModelKeyConstants.PARAMS);
		if (paramsE != null) {
			ParamsDO paramsDO = new ParamsDO();

			String minSize = paramsE.getAttribute(ModelKeyConstants.PARAMS_MIN_SIZE);
			if (NumberUtils.isDigits(minSize)) {
				paramsDO.setParamsMinSize(Integer.valueOf(minSize));
			}

			String maxSize = paramsE.getAttribute(ModelKeyConstants.PARAMS_MAX_SIZE);
			if (NumberUtils.isDigits(maxSize)) {
				paramsDO.setParamsMaxSize(Integer.valueOf(maxSize));
			}

			// 解释param节点
			List<Element> paramEs = XmlUtils.getElements(paramsE, ModelKeyConstants.PARAM);
			for (Element paramE : paramEs) {
				//range parameter格式特殊，要单独拆分 moling 2010-11-20
				if ("true".equals(paramE.getAttribute(ModelKeyConstants.IS_RANGE))) {
					RangeParameterDO paramRange = parseRangeParameters(paramE);
					paramsDO.setParamRange(paramRange);
				} else {
					ParameterDO subParam = getParameter(paramE);
					if (Boolean.TRUE.equals(subParam.getForCheck())) {
						paramsDO.addCheckParam(subParam);
					} else if (Boolean.TRUE.equals(subParam.getEnableSplit())) {
						paramsDO.addParseParam(subParam);
					} else {
						paramsDO.addKeepParam(subParam);
					}
				}
			}

			apiDO.setParamsRule(paramsDO);
		}

		String ignoreFails = XmlUtils.getElementValue(root, ModelKeyConstants.IGNORE_FAILS);
		if (StringUtils.isNotBlank(ignoreFails)) {
			String[] fails = ignoreFails.split(",");
			apiDO.setSubTaskIgnoreFails(Arrays.asList(fails));
		}

		String retryFails = XmlUtils.getElementValue(root, ModelKeyConstants.RETRY_FAILS);
		if (StringUtils.isNotBlank(retryFails)) {
			String[] fails = retryFails.split(",");
			apiDO.setSubTaskRetryFails(Arrays.asList(fails));
		}

		String sendRequests = XmlUtils.getElementValue(root, ModelKeyConstants.SEND_OUT_REQUESTS);
		if (StringUtils.isNotBlank(sendRequests)) {
			String[] requests = sendRequests.split(",");
			apiDO.setSubTaskSendOutRequests(Arrays.asList(requests));
		}

		String sendResultsJson = XmlUtils.getElementValue(root, ModelKeyConstants.SEND_OUT_RESULTS_JSON);
		if (StringUtils.isNotBlank(sendResultsJson)) {
			String[] results = sendResultsJson.split("#");
			apiDO.setSubTaskSendOutResultsJson(results);
		}
		
		String sendResultsXml = XmlUtils.getElementValue(root, ModelKeyConstants.SEND_OUT_RESULTS_XML);
		if (StringUtils.isNotBlank(sendResultsXml)) {
			String[] results = sendResultsXml.split("#");
			apiDO.setSubTaskSendOutResultsXml(results);
		}

		if (StringUtils.isBlank(apiDO.getSubTaskInterface())
				|| StringUtils.isBlank(apiDO.getSubTaskType())
				|| StringUtils.isBlank(apiDO.getSubTaskMethod())
				|| StringUtils.isBlank(apiDO.getSubTaskVersion())) {
			throw new XmlException("子任务HSF相关描述不能为空");
		}

		return apiDO;
	}

	private static RangeParameterDO parseRangeParameters(Element paramE)
			throws XmlException, ParseException {
		RangeParameterDO paramRange = new RangeParameterDO();
		String rangeType = paramE.getAttribute(ModelKeyConstants.RANGE_TYPE);
		
		String parsePeriod = paramE.getAttribute(ModelKeyConstants.PARSE_PERIOD);
		String maxPeriod = paramE.getAttribute(ModelKeyConstants.MAX_PERIOD);
		String[] pps = parsePeriod(parsePeriod, rangeType); 
		String[] mps = parsePeriod(maxPeriod, rangeType);
		paramRange.setParsePeriod(Integer.valueOf(pps[0]));
		paramRange.setMaxPeriod(Integer.valueOf(mps[0]));
		
		if (ModelKeyConstants.RANGE_TYPE_DATE.equals(rangeType)) {
			paramRange.setRangeType(ModelKeyConstants.RANGE_TYPE_DATE);
			paramRange.setParseMeasure(pps[1]);
			paramRange.setMaxMeasure(mps[1]);
		} else if (ModelKeyConstants.RANGE_TYPE_NUMBER.equals(rangeType)) {
			paramRange.setRangeType(ModelKeyConstants.RANGE_TYPE_NUMBER);
		} else {
			throw new XmlException("分片任务模板找不到合法range_type配置");
		}
		
		String minName = paramE.getAttribute(ModelKeyConstants.MIN_NAME);
		String maxName = paramE.getAttribute(ModelKeyConstants.MAX_NAME);
		String targetMinName = paramE.getAttribute(ModelKeyConstants.TARGET_MIN_NAME);
		String targetMaxName = paramE.getAttribute(ModelKeyConstants.TARGET_MAX_NAME);
		if (StringUtils.isBlank(minName) || StringUtils.isBlank(maxName) || 
				StringUtils.isBlank(targetMinName) || StringUtils.isBlank(targetMaxName)) {
			throw new XmlException("分片任务的key找不到合法的配置");
		} else {
			paramRange.setMinName(minName);
			paramRange.setMaxName(maxName);
			paramRange.setTargetMinName(targetMinName);
			paramRange.setTargetMaxName(targetMaxName);
		}
		
		String minValue = paramE.getAttribute(ModelKeyConstants.MIN_VALUE);
		if (StringUtils.isNotBlank(minValue) && ModelKeyConstants.RANGE_TYPE_DATE.equals(rangeType)) {
			paramRange.setMinValue(DateKit.ymdOrYmdhms2Date(minValue).getTime());
		} else if (StringUtils.isNotBlank(minValue) && ModelKeyConstants.RANGE_TYPE_NUMBER.equals(rangeType)) {
			paramRange.setMinValue(Long.valueOf(minValue));
		} //其他格式的到不了这里了
		
		String maxValue = paramE.getAttribute(ModelKeyConstants.MAX_VALUE);
		if (StringUtils.isNotBlank(maxValue) && ModelKeyConstants.RANGE_TYPE_DATE.equals(rangeType)) {
			paramRange.setMaxValue(DateKit.ymdOrYmdhms2Date(maxValue).getTime());
		} else if (StringUtils.isNotBlank(maxValue) && ModelKeyConstants.RANGE_TYPE_NUMBER.equals(rangeType)) {
			paramRange.setMaxValue(Long.valueOf(maxValue));
		} //其他格式的到不了这里了
		
		//自动分页相关参数解析
		if ("true".equals(paramE.getAttribute(ModelKeyConstants.ENABLE_PAGE))) {
			paramRange.setEnablePage(true);
			String pageNoName = paramE.getAttribute(ModelKeyConstants.PAGE_NO_NAME);
			String pageSizeName = paramE.getAttribute(ModelKeyConstants.PAGE_SIZE_NAME);
			String totalResultName = paramE.getAttribute(ModelKeyConstants.TOTAL_RESULT_NAME);
			String pageSize = paramE.getAttribute(ModelKeyConstants.PAGE_SIZE);
			if (StringUtils.isBlank(pageNoName) || StringUtils.isBlank(pageSizeName) || 
					StringUtils.isBlank(totalResultName) || !NumberUtils.isDigits(pageSize)) {
				throw new XmlException("自动翻页任务配置无法解析");
			}
			paramRange.setPageNoName(pageNoName);
			paramRange.setPageSizeName(pageSizeName);
			paramRange.setPageSize(Integer.valueOf(pageSize));
			paramRange.setTotalResultName(totalResultName);
		} else {
			//默认不分页调用
			paramRange.setEnablePage(false);
		}
		return paramRange;
	}
	
	public static TimePeriod parseString2TimePeriod(String input) throws XmlException, ParseException {
		//如果时间片格式非法就抛错
		if (StringUtils.isBlank(input) || input.indexOf('-') <= 0 || input.indexOf('-') == (input.length() - 1)) {
			throw new XmlException("模板配置的空闲时间片无法解析");
		}
		String start = input.substring(0, input.indexOf('-'));
		String end = input.substring(input.indexOf('-') + 1, input.length());
		if (start.length() != 8 || end.length() != 8) {
			throw new XmlException("模板配置的空闲时间片不满足hh:mm:ss-hh:mm:ss的结构");
		}
		TimePeriod output = new TimePeriod();
		output.setStartDate(start);
		output.setEndDate(end);
		return output;
	}
	
	public static String[] parsePeriod(String period, String rangeType) throws XmlException {
		if (StringUtils.isBlank(period) || period.indexOf(':') <= 0) {
			throw new XmlException("模板配置的分割参数无法解析");
		}
		
		String[] periods = period.split(":");
		
		if (ModelKeyConstants.RANGE_TYPE_DATE.equals(rangeType)) {
			if (periods.length != 2 || !NumberUtils.isDigits(periods[0]) || (!ModelKeyConstants.MEASURE_HOUR.equals(periods[1]) 
					&& !ModelKeyConstants.MEASURE_DAY.equals(periods[1]) && !ModelKeyConstants.MEASURE_MONTH.equals(periods[1]))) {
				throw new XmlException("模板配置的分割参数无法解析");
			}
		} else if (ModelKeyConstants.RANGE_TYPE_NUMBER.equals(rangeType)) {
			if (periods.length != 1 || !NumberUtils.isDigits(periods[0])) {
				throw new XmlException("模板配置的分割参数无法解析");
			}
		} else {
			throw new XmlException("模板配置的分割参类型无法解析");
		}
		return periods;
	}

	public static String fromMapToJson(Map<String, String> params) {
		JSONObject content = new JSONObject(normalizeMap(params));
		return content.toString();
	}
	
	public static String fromMapToXml(Map<String, String> params) {
		Map<String, String> parameters =  normalizeMap(params);
		StringBuffer sb = new StringBuffer();
		if (null != parameters && !parameters.isEmpty()) {
			for (Entry<String, String> param : parameters.entrySet()) {
				sb.append(writeXmlTag(param.getKey(), param.getValue(), false));
			}
		}
		return sb.toString();
	}
	
	public static String writeXmlTag(String key, String value, boolean isList) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(LT);
		sb.append(key);
		//如果指定了是list类型的结果，要加上list标记
		if (isList) {
			sb.append(" list=\"true\"");
		}
		sb.append(RT);
		if (StringUtils.isNotEmpty(value)) {
			sb.append(value);
		}
		sb.append(LTS);
		sb.append(key);
		sb.append(RT);
		
		return sb.toString();
	}
	
	public static Map<String, String> fromJsonToMap(String json) throws JSONException {
		Map<String, String> output = new HashMap<String, String>();
		JSONObject content = new JSONObject(json);
		Iterator<?> it = content.keys();
		while (it != null && it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof String) {
				String key = (String) obj;
				output.put(key, String.valueOf(content.get(key)));
			}
		}
		return output;
	}

	private static ParameterDO getParameter(Element paramE) throws AtsException {
		ParameterDO parameter = new ParameterDO();

		String name = paramE.getAttribute(ModelKeyConstants.NAME);
		String targetName = paramE.getAttribute(ModelKeyConstants.TARGET_NAME);
		String enableSplit = paramE.getAttribute(ModelKeyConstants.ENABLE_SPLIT);
		String separator = paramE.getAttribute(ModelKeyConstants.SEPARATOR);
		String enableNull = paramE.getAttribute(ModelKeyConstants.TARGET_ENABLE_NULL);
		String targetType = paramE.getAttribute(ModelKeyConstants.TARGET_TYPE);
		String forCheck = paramE.getAttribute(ModelKeyConstants.FOR_CHECK);

		// 参数名称必须指定
		if (StringUtils.isBlank(name)) {
			throw new XmlException("name不能为空");
		} else {
			parameter.setName(name);
		}

		// 后传参数名称必须指定
		if (StringUtils.isBlank(targetName)) {
			throw new XmlException("target_name不能为空");
		} else {
			parameter.setTargetName(targetName);
		}

		// 指定了需要分割的参数必需指定分隔符,默认不需要分割
		if (Boolean.TRUE.toString().equals(enableSplit) && StringUtils.isEmpty(separator)) {
			throw new XmlException("标记为可分割的属性必须指定分隔符");
		} else if (Boolean.TRUE.toString().equals(enableSplit) && StringUtils.isNotEmpty(separator)) {
			parameter.setEnableSplit(true);
			parameter.setSeparator(separator);
		} else {
			parameter.setEnableSplit(false);
		}

		// 默认内容不能为空
		if (Boolean.TRUE.toString().equals(enableNull)) {
			parameter.setTargetEnableNull(true);
		} else {
			parameter.setTargetEnableNull(false);
		}

		// 默认参数类型为string
		if ("number".equals(targetType) || "date".equals(targetType)
				|| "boolean".equals(targetType) || "price".equals(targetType)) {
			parameter.setTargetType(targetType);
		} else {
			parameter.setTargetType("string");
		}

		// 默认不是用于前置校验的参数
		if (Boolean.TRUE.toString().equals(forCheck)) {
			parameter.setForCheck(true);
		} else {
			parameter.setForCheck(false);
		}

		return parameter;
	}

	/**
	 * 清除内容中的空字段。
	 */
	private static Map<String, String> normalizeMap(Map<String, String> content) {
		Map<String, String> result = new HashMap<String, String>(content.size());
		Set<Entry<String, String>> fields = content.entrySet();

		for (Entry<String, String> field : fields) {
			if (StringUtils.isNotEmpty(field.getValue())) {
				result.put(field.getKey(), field.getValue());
			}
		}

		return result;
	}

}
