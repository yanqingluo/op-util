package com.taobao.top.ats.util;

/**
 * 
 * @author moling
 * @since 1.0, 2010-11-18
 */
public class ModelKeyConstants {
	public static final String PRE_CHECK = "pre_check";
	public static final String SUB_TASK = "sub_task";
	public static final String PARAMS = "params";
	public static final String PARAM = "param";
	public static final String NAME = "name";
	public static final String IGNORE_FAILS = "ignore_fails";
	public static final String RETRY_FAILS = "retry_fails";
	public static final String SEND_OUT_REQUESTS = "send_out_requests";
	public static final String SEND_OUT_RESULTS_XML = "send_out_results_xml";
	public static final String SEND_OUT_RESULTS_JSON = "send_out_results_json";
	public static final String PARAMS_MIN_SIZE = "min_size";
	public static final String PARAMS_MAX_SIZE = "max_size";
	public static final String SERVICE_TYPE = "type";
	public static final String SERVICE_INTERFACE = "interface";
	public static final String SERVICE_METHOD = "method";
	public static final String SERVICE_VERSION = "version";
	public static final String TARGET_NAME = "target_name";
	public static final String ENABLE_SPLIT = "enable_split";
	public static final String SEPARATOR = "separator";
	public static final String TARGET_ENABLE_NULL = "target_enable_null";
	public static final String TARGET_TYPE = "target_type";
	public static final String FOR_CHECK = "for_check";
	
	//二期项目增加key
	//任务优先级
	public static final String PRIORITY = "priority";
	public static final String LEVEL = "level";
	public static final int LEVEL_HIGH = 0;
	public static final int LEVEL_NORMAL = 1;
	public static final int LEVEL_LOW = 2;
	//执行时间片
	public static final String TIME_PERIOD = "time_period";
	//是否大任务api
	public static final String IS_BIG_RESULT = "is_big_result";
	//bool型标记在attribute中的值
	public static final String BOOLEAN_ATTRIBUTE_MARK = "1";
	//是否分片参数
	public static final String IS_RANGE = "is_range";
	//分片类型，目前只支持date和number两种类型
	public static final String RANGE_TYPE = "range_type";
	public static final String RANGE_TYPE_DATE = "date";
	public static final String RANGE_TYPE_NUMBER = "number";
	//分割粒度，格式为1:hour
	public static final String PARSE_PERIOD = "parse_period";
	//最大服务粒度，格式同分割粒度
	public static final String MAX_PERIOD = "max_period";
	//分割粒度分隔符，前面的表示数字，后面的表示单位。如果是number类型的分割，后面的单位可以为空
	public static final String PERIOD_SPLITER = ":";
	//分割粒度的单位，支持hour(小时)、day(天)、month(月)，number类型的可以没有类型
	public static final String MEASURE_HOUR = "hour";
	public static final String MEASURE_DAY = "day";
	public static final String MEASURE_MONTH = "month";
	//分割区间的最小值限制，格式与RANGE_TYPE一致
	public static final String MIN_VALUE = "min_value";
	//分割区间的最大值限制，格式与RANGE_TYPE一致
	public static final String MAX_VALUE = "max_value";
	//分割最小值的key
	public static final String MIN_NAME = "min_name";
	//分割最大值的key
	public static final String MAX_NAME = "max_name";
	//分割后传到后方的最小值key
	public static final String TARGET_MIN_NAME = "target_min_name";
	//分割后传到后方的最大值key
	public static final String TARGET_MAX_NAME = "target_max_name";
	
	//分页相关的key
	//分割片后是否还需要翻页获取
	public static final String ENABLE_PAGE = "enable_page";
	//传到后方的页码的key
	public static final String PAGE_NO_NAME = "page_no_name";
	//传到后方的每页大小的key
	public static final String PAGE_SIZE_NAME = "page_size_name";
	//返回结果中总结果数的key
	public static final String TOTAL_RESULT_NAME = "total_result_name";
	//传到后方的默认分页大小
	public static final String PAGE_SIZE = "page_size";
}
