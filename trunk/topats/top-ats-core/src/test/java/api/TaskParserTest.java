package api;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Test;

import com.taobao.top.ats.XmlException;
import com.taobao.top.ats.domain.ApiDO;
import com.taobao.top.ats.domain.ParameterDO;
import com.taobao.top.ats.domain.ParamsDO;
import com.taobao.top.ats.domain.TimePeriod;
import com.taobao.top.ats.task.api.TaskParser;
import com.taobao.top.ats.util.DateKit;
import com.taobao.top.ats.util.ModelKeyConstants;


/**
 * 
 * @author moling
 * @since 1.0, 2010-8-20
 */
public class TaskParserTest {
	@Test
	public void mapToJsonTest() throws JSONException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("akey", "avalue");
		params.put("ckey", "cvalue");
		params.put("bkey", "bvalue");
		params.put("dkey", null);
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"ckey\":\"cvalue\",\"akey\":\"avalue\",\"bkey\":\"bvalue\"}"), 
				TaskParser.fromJsonToMap(TaskParser.fromMapToJson(params)));
	}
	
	@Test
	public void jsonToMapTest() throws JSONException {
		Map<String, String> params = TaskParser.fromJsonToMap("{\"ckey\":\"cvalue\",\"akey\":\"avalue\",\"bkey\":\"bvalue\"}");
		Assert.assertEquals(3, params.size());
		Assert.assertEquals("avalue", params.get("akey"));
		Assert.assertEquals("bvalue", params.get("bkey"));
		Assert.assertEquals("cvalue", params.get("ckey"));
	}
	
	@Test
	public void mapToXmlTest() throws JSONException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("akey", "avalue");
		params.put("ckey", "cvalue");
		params.put("bkey", "bvalue");
		params.put("dkey", null);
		Assert.assertEquals("<bkey>bvalue</bkey><akey>avalue</akey><ckey>cvalue</ckey>", 
				TaskParser.fromMapToXml(params));
	}
	
	@Test
	public void parseTaskTemplateTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskDemo.xml");
		ApiDO api = TaskParser.parseTaskTemplate(is);
		is.close();
		//测试任务名称填充
		Assert.assertEquals("taobao.topats.trades.price.update", api.getTaskName());
		//测试前置校验参数填充
		Assert.assertEquals("com.taobao.api.trade.TradeService", api.getPreCheckInterface());
		Assert.assertEquals("doCheckCertInfo", api.getPreCheckMethod());
		Assert.assertEquals("hsf", api.getPreCheckType());
		Assert.assertEquals("1.0.0", api.getPreCheckVersion());
		//测试子任务hsf相关
		Assert.assertEquals("com.taobao.api.trade.TradeService", api.getSubTaskInterface());
		Assert.assertEquals("doTradePriceUpdate", api.getSubTaskMethod());
		Assert.assertEquals("hsf", api.getSubTaskType());
		Assert.assertEquals("1.0.0", api.getSubTaskVersion());
		//测试忽略错误填充
		Assert.assertEquals(2, api.getSubTaskIgnoreFails().size());
		Assert.assertTrue(api.getSubTaskIgnoreFails().contains("123456"));
		Assert.assertTrue(api.getSubTaskIgnoreFails().contains("223456"));
		//测试重试错误填充
		Assert.assertEquals(4, api.getSubTaskRetryFails().size());
		Assert.assertTrue(api.getSubTaskRetryFails().contains("isp.trade-service-unavailable"));
		Assert.assertTrue(api.getSubTaskRetryFails().contains("isp.remote-service-error"));
		Assert.assertTrue(api.getSubTaskRetryFails().contains("isp.remote-service-timeout"));
		Assert.assertTrue(api.getSubTaskRetryFails().contains("isp.remote-connection-error"));
		//测试子任务请求参数填充
		Assert.assertEquals(5, api.getSubTaskSendOutRequests().size());
		Assert.assertTrue(api.getSubTaskSendOutRequests().contains("tid"));
		Assert.assertTrue(api.getSubTaskSendOutRequests().contains("oids"));
		Assert.assertTrue(api.getSubTaskSendOutRequests().contains("adjust_fees"));
		Assert.assertTrue(api.getSubTaskSendOutRequests().contains("logistics_fee"));
		Assert.assertTrue(api.getSubTaskSendOutRequests().contains("origin_fees"));
		//测试子任务结果填充
		Assert.assertEquals(5, api.getSubTaskSendOutResultsJson().length);
		Assert.assertEquals("{\"trade\":{\"tid\":", api.getSubTaskSendOutResultsJson()[0]);
		Assert.assertEquals("tid", api.getSubTaskSendOutResultsJson()[1]);
		Assert.assertEquals(",\"modified\":\"", api.getSubTaskSendOutResultsJson()[2]);
		Assert.assertEquals("modified", api.getSubTaskSendOutResultsJson()[3]);
		Assert.assertEquals("\"}}", api.getSubTaskSendOutResultsJson()[4]);
		Assert.assertEquals("<trade><tid>", api.getSubTaskSendOutResultsXml()[0]);
		Assert.assertEquals("tid", api.getSubTaskSendOutResultsXml()[1]);
		Assert.assertEquals("</tid><modified>", api.getSubTaskSendOutResultsXml()[2]);
		Assert.assertEquals("modified", api.getSubTaskSendOutResultsXml()[3]);
		Assert.assertEquals("</modified></trade>", api.getSubTaskSendOutResultsXml()[4]);
		
		//判断不设置优先级的情况下优先级为普通
		Assert.assertEquals(Integer.valueOf(1), api.getLevel());
		Assert.assertNull(api.getTimePeriods());
		Assert.assertFalse(api.getIsBigResult());
		//测试子任务基本模板
		ParamsDO params = api.getParamsRule();
		//判断无模板情况下默人range参数为空
		Assert.assertNull(params.getParamRange());
		Assert.assertEquals(Integer.valueOf(1), params.getParamsMinSize());
		Assert.assertEquals(Integer.valueOf(40), params.getParamsMaxSize());
		Assert.assertEquals(1, params.getParamsCheck().size());
		Assert.assertEquals(1, params.getParamsKeep().size());
		Assert.assertEquals(5, params.getParamsParse().size());
		//测试只作为校验的参数
		Assert.assertEquals("cert_sign", params.getParamsCheck().get(0).getName());
		Assert.assertEquals("cert_sign", params.getParamsCheck().get(0).getTargetName());
		Assert.assertEquals(null, params.getParamsCheck().get(0).getSeparator());
		Assert.assertEquals("string", params.getParamsCheck().get(0).getTargetType());
		Assert.assertEquals(Boolean.FALSE, params.getParamsCheck().get(0).getEnableSplit());
		Assert.assertEquals(Boolean.TRUE, params.getParamsCheck().get(0).getForCheck());
		Assert.assertEquals(Boolean.FALSE, params.getParamsCheck().get(0).getTargetEnableNull());
		//测试不分割的参数
		Assert.assertEquals("nick", params.getParamsKeep().get(0).getName());
		Assert.assertEquals("nick", params.getParamsKeep().get(0).getTargetName());
		Assert.assertEquals(null, params.getParamsKeep().get(0).getSeparator());
		Assert.assertEquals("string", params.getParamsKeep().get(0).getTargetType());
		Assert.assertEquals(Boolean.FALSE, params.getParamsKeep().get(0).getEnableSplit());
		Assert.assertEquals(Boolean.FALSE, params.getParamsKeep().get(0).getForCheck());
		Assert.assertEquals(Boolean.FALSE, params.getParamsKeep().get(0).getTargetEnableNull());
		//测试需分割的参数
		for (ParameterDO parameter : params.getParamsParse()) {
			//测试name和target——name不一致，子格式为数字
			if (parameter.getName().equals("tids")) {
				Assert.assertEquals("tids", parameter.getName());
				Assert.assertEquals("tid", parameter.getTargetName());
				Assert.assertEquals(";", parameter.getSeparator());
				Assert.assertEquals("number", parameter.getTargetType());
				Assert.assertEquals(Boolean.TRUE, parameter.getEnableSplit());
				Assert.assertEquals(Boolean.FALSE, parameter.getForCheck());
				Assert.assertEquals(Boolean.FALSE, parameter.getTargetEnableNull());
			} else if (parameter.getName().equals("oids")) {
				//测试name和target——name一致，子格式为不存在的
				Assert.assertEquals("oids", parameter.getName());
				Assert.assertEquals("oids", parameter.getTargetName());
				Assert.assertEquals(";", parameter.getSeparator());
				Assert.assertEquals("string", parameter.getTargetType());
				Assert.assertEquals(Boolean.TRUE, parameter.getEnableSplit());
				Assert.assertEquals(Boolean.FALSE, parameter.getForCheck());
				Assert.assertEquals(Boolean.FALSE, parameter.getTargetEnableNull());
			} else if (parameter.getName().equals("logistics_fees")) {
				//测试子数字可以为空的
				Assert.assertEquals("logistics_fees", parameter.getName());
				Assert.assertEquals("logistics_fee", parameter.getTargetName());
				Assert.assertEquals(";", parameter.getSeparator());
				Assert.assertEquals("price", parameter.getTargetType());
				Assert.assertEquals(Boolean.TRUE, parameter.getEnableSplit());
				Assert.assertEquals(Boolean.FALSE, parameter.getForCheck());
				Assert.assertEquals(Boolean.TRUE, parameter.getTargetEnableNull());
			}
		}
	}
	
	@Test
	public void parseTaskTemplateWithPriorityAndRangeTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskDemoWithPriorityAndRange.xml");
		ApiDO api = TaskParser.parseTaskTemplate(is);
		is.close();
		//测试任务名称填充
		Assert.assertEquals("taobao.topats.trades.price.update", api.getTaskName());
		//测试前置校验参数填充
		Assert.assertEquals("com.taobao.api.trade.TradeService", api.getPreCheckInterface());
		Assert.assertEquals("doCheckCertInfo", api.getPreCheckMethod());
		Assert.assertEquals("hsf", api.getPreCheckType());
		Assert.assertEquals("1.0.0", api.getPreCheckVersion());
		//测试子任务hsf相关
		Assert.assertEquals("com.taobao.api.trade.TradeService", api.getSubTaskInterface());
		Assert.assertEquals("doTradePriceUpdate", api.getSubTaskMethod());
		Assert.assertEquals("hsf", api.getSubTaskType());
		Assert.assertEquals("1.0.0", api.getSubTaskVersion());
		//测试忽略错误填充
		Assert.assertEquals(2, api.getSubTaskIgnoreFails().size());
		Assert.assertTrue(api.getSubTaskIgnoreFails().contains("123456"));
		Assert.assertTrue(api.getSubTaskIgnoreFails().contains("223456"));
		//测试重试错误填充
		Assert.assertEquals(4, api.getSubTaskRetryFails().size());
		Assert.assertTrue(api.getSubTaskRetryFails().contains("isp.trade-service-unavailable"));
		Assert.assertTrue(api.getSubTaskRetryFails().contains("isp.remote-service-error"));
		Assert.assertTrue(api.getSubTaskRetryFails().contains("isp.remote-service-timeout"));
		Assert.assertTrue(api.getSubTaskRetryFails().contains("isp.remote-connection-error"));
		//测试子任务请求参数填充
		Assert.assertEquals(5, api.getSubTaskSendOutRequests().size());
		Assert.assertTrue(api.getSubTaskSendOutRequests().contains("tid"));
		Assert.assertTrue(api.getSubTaskSendOutRequests().contains("oids"));
		Assert.assertTrue(api.getSubTaskSendOutRequests().contains("adjust_fees"));
		Assert.assertTrue(api.getSubTaskSendOutRequests().contains("logistics_fee"));
		Assert.assertTrue(api.getSubTaskSendOutRequests().contains("origin_fees"));
		//测试子任务结果填充
		Assert.assertEquals(5, api.getSubTaskSendOutResultsJson().length);
		Assert.assertEquals("{\"trade\":{\"tid\":", api.getSubTaskSendOutResultsJson()[0]);
		Assert.assertEquals("tid", api.getSubTaskSendOutResultsJson()[1]);
		Assert.assertEquals(",\"modified\":\"", api.getSubTaskSendOutResultsJson()[2]);
		Assert.assertEquals("modified", api.getSubTaskSendOutResultsJson()[3]);
		Assert.assertEquals("\"}}", api.getSubTaskSendOutResultsJson()[4]);
		Assert.assertEquals("<trade><tid>", api.getSubTaskSendOutResultsXml()[0]);
		Assert.assertEquals("tid", api.getSubTaskSendOutResultsXml()[1]);
		Assert.assertEquals("</tid><modified>", api.getSubTaskSendOutResultsXml()[2]);
		Assert.assertEquals("modified", api.getSubTaskSendOutResultsXml()[3]);
		Assert.assertEquals("</modified></trade>", api.getSubTaskSendOutResultsXml()[4]);
		//测试优先级判断
		Assert.assertEquals(Integer.valueOf(2), api.getLevel());
		Assert.assertEquals(2,api.getTimePeriods().size());
		//设置时间片的区间确认
		for (TimePeriod timePeriod: api.getTimePeriods()) {
			if (DateKit.ymdOrYmdhms2Date("2010-11-18 00:00:00").equals(timePeriod.getStartDate())) {
				Assert.assertEquals(DateKit.ymdOrYmdhms2Date("2010-11-18 08:59:59"),timePeriod.getEndDate());
			} else if (DateKit.ymdOrYmdhms2Date("2010-11-18 12:00:00").equals(timePeriod.getStartDate())) {
				Assert.assertEquals(DateKit.ymdOrYmdhms2Date("2010-11-18 17:00:00"),timePeriod.getEndDate());
			} else {
				Assert.assertTrue(false);
			}
		}
		Assert.assertTrue(api.getIsBigResult());
		//测试子任务基本模板
		ParamsDO params = api.getParamsRule();
		Assert.assertEquals(Integer.valueOf(1), params.getParamsMinSize());
		Assert.assertEquals(Integer.valueOf(40), params.getParamsMaxSize());
		Assert.assertNotNull(params.getParamRange());
		Assert.assertEquals(1, params.getParamsCheck().size());
		Assert.assertEquals(1, params.getParamsKeep().size());
		Assert.assertEquals(2, params.getParamsParse().size());
		//测试range类型参数
		Assert.assertEquals(ModelKeyConstants.RANGE_TYPE_DATE, params.getParamRange().getRangeType());
		Assert.assertEquals(Integer.valueOf(4), params.getParamRange().getParsePeriod());
		Assert.assertEquals(ModelKeyConstants.MEASURE_DAY, params.getParamRange().getParseMeasure());
		Assert.assertEquals(Integer.valueOf(3), params.getParamRange().getMaxPeriod());
		Assert.assertEquals(ModelKeyConstants.MEASURE_MONTH, params.getParamRange().getMaxMeasure());
		Assert.assertEquals(Long.valueOf(DateKit.ymdOrYmdhms2Date("2010-06-10 00:00:00").getTime()),
				params.getParamRange().getMinValue());
		Assert.assertEquals(Long.valueOf(DateKit.ymdOrYmdhms2Date("2012-06-10 00:00:00").getTime()),
				params.getParamRange().getMaxValue());
		Assert.assertEquals("start_created", params.getParamRange().getMinName());
		Assert.assertEquals("end_created", params.getParamRange().getMaxName());
		Assert.assertEquals("start_created_back", params.getParamRange().getTargetMinName());
		Assert.assertEquals("end_created_back", params.getParamRange().getTargetMaxName());
		Assert.assertTrue(params.getParamRange().getEnablePage());
		Assert.assertEquals("page_no", params.getParamRange().getPageNoName());
		Assert.assertEquals("page_size", params.getParamRange().getPageSizeName());
		Assert.assertEquals("total_result", params.getParamRange().getTotalResultName());
		Assert.assertEquals(Integer.valueOf(100), params.getParamRange().getPageSize());
		//测试只作为校验的参数
		Assert.assertEquals("cert_sign", params.getParamsCheck().get(0).getName());
		Assert.assertEquals("cert_sign", params.getParamsCheck().get(0).getTargetName());
		Assert.assertEquals(null, params.getParamsCheck().get(0).getSeparator());
		Assert.assertEquals("string", params.getParamsCheck().get(0).getTargetType());
		Assert.assertEquals(Boolean.FALSE, params.getParamsCheck().get(0).getEnableSplit());
		Assert.assertEquals(Boolean.TRUE, params.getParamsCheck().get(0).getForCheck());
		Assert.assertEquals(Boolean.FALSE, params.getParamsCheck().get(0).getTargetEnableNull());
		//测试不分割的参数
		Assert.assertEquals("nick", params.getParamsKeep().get(0).getName());
		Assert.assertEquals("nick", params.getParamsKeep().get(0).getTargetName());
		Assert.assertEquals(null, params.getParamsKeep().get(0).getSeparator());
		Assert.assertEquals("string", params.getParamsKeep().get(0).getTargetType());
		Assert.assertEquals(Boolean.FALSE, params.getParamsKeep().get(0).getEnableSplit());
		Assert.assertEquals(Boolean.FALSE, params.getParamsKeep().get(0).getForCheck());
		Assert.assertEquals(Boolean.FALSE, params.getParamsKeep().get(0).getTargetEnableNull());
		//测试需分割的参数
		for (ParameterDO parameter : params.getParamsParse()) {
			//测试name和target——name不一致，子格式为数字
			if (parameter.getName().equals("tids")) {
				Assert.assertEquals("tids", parameter.getName());
				Assert.assertEquals("tid", parameter.getTargetName());
				Assert.assertEquals(";", parameter.getSeparator());
				Assert.assertEquals("number", parameter.getTargetType());
				Assert.assertEquals(Boolean.TRUE, parameter.getEnableSplit());
				Assert.assertEquals(Boolean.FALSE, parameter.getForCheck());
				Assert.assertEquals(Boolean.FALSE, parameter.getTargetEnableNull());
			}else if (parameter.getName().equals("logistics_fees")) {
				//测试子数字可以为空的
				Assert.assertEquals("logistics_fees", parameter.getName());
				Assert.assertEquals("logistics_fee", parameter.getTargetName());
				Assert.assertEquals(";", parameter.getSeparator());
				Assert.assertEquals("price", parameter.getTargetType());
				Assert.assertEquals(Boolean.TRUE, parameter.getEnableSplit());
				Assert.assertEquals(Boolean.FALSE, parameter.getForCheck());
				Assert.assertEquals(Boolean.TRUE, parameter.getTargetEnableNull());
			}
		}
	}
	
	@Test (expected=XmlException.class)
	public void parseTaskTemplateWithoutTasknameTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskWithoutTaskname.xml");
		TaskParser.parseTaskTemplate(is);
		is.close();
	}
	
	@Test
	public void parseTaskTemplateWithoutPrecheckTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskWithOutPrecheck.xml");
		ApiDO api = TaskParser.parseTaskTemplate(is);
		is.close();
		//测试任务名称填充
		Assert.assertEquals("taobao.topats.trades.price.update", api.getTaskName());
		//测试前置校验参数填充
		Assert.assertEquals(null, api.getPreCheckInterface());
		Assert.assertEquals(null, api.getPreCheckMethod());
		Assert.assertEquals(null, api.getPreCheckType());
		Assert.assertEquals(null, api.getPreCheckVersion());
		//测试子任务hsf相关不受影响
		Assert.assertEquals("taobao.trade.price.update", api.getSubTaskInterface());
		Assert.assertEquals("updateTradePrice", api.getSubTaskMethod());
		Assert.assertEquals("hsf", api.getSubTaskType());
		Assert.assertEquals("1.0.0", api.getSubTaskVersion());
	}
	
	@Test (expected=XmlException.class)
	public void parseTaskTemplateWithoutSubTaskHsfTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskWithoutSubTaskHsf.xml");
		TaskParser.parseTaskTemplate(is);
		is.close();
	}
	
	@Test (expected=XmlException.class)
	public void parseTaskTemplateWithoutSubTaskTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskWithoutSubTask.xml");
		TaskParser.parseTaskTemplate(is);
		is.close();
	}
	
	@Test (expected=XmlException.class)
	public void parseTaskTemplateWithoutParameterNameTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskWithoutParametername.xml");
		TaskParser.parseTaskTemplate(is);
		is.close();
	}
	
	@Test (expected=XmlException.class)
	public void parseTaskTemplateWithoutParameterTargetNameTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskWithoutParameterTargetname.xml");
		TaskParser.parseTaskTemplate(is);
		is.close();
	}
	
	@Test (expected=XmlException.class)
	public void parseTaskEnbaleParseWithoutSeparatorTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskParameterEnableParseWithoutSeparator.xml");
		TaskParser.parseTaskTemplate(is);
		is.close();
	}
	
	@Test
	public void parseTaskDemoWithUnknownPriorityTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskDemoWithUnknownPriority.xml");
		ApiDO api = TaskParser.parseTaskTemplate(is);
		is.close();
		
		Assert.assertEquals(Integer.valueOf(1), api.getLevel());
		Assert.assertNull(api.getTimePeriods());
		Assert.assertFalse(api.getIsBigResult());
	}
	
	@Test (expected=XmlException.class)
	public void parseTaskDemoWithEmptyTimePeriodsTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskDemoWithEmptyTimePeriods.xml");
		TaskParser.parseTaskTemplate(is);
		is.close();
	}
	
	@Test (expected=XmlException.class)
	public void parsePeriodBlankTest() throws Exception {
		TaskParser.parsePeriod(" ", "date");
	}
	
	@Test (expected=XmlException.class)
	public void parsePeriodWithoutColonTest() throws Exception {
		TaskParser.parsePeriod(":123", "date");
	}
	
	@Test (expected=XmlException.class)
	public void parsePeriodWithoutWrongRageTypeTest() throws Exception {
		TaskParser.parsePeriod("123:day", "unknown");
	}
	
	@Test (expected=XmlException.class)
	public void parseDatePeriodWithNumberFormatTest() throws Exception {
		TaskParser.parsePeriod("123:", "date");
	}
	
	@Test (expected=XmlException.class)
	public void parseDatePeriodWithWrongPeriodTest() throws Exception {
		TaskParser.parsePeriod("abc:day", "date");
	}
	
	@Test (expected=XmlException.class)
	public void parseDatePeriodWithWrongMeasureTest() throws Exception {
		TaskParser.parsePeriod("123:unknown", "date");
	}
	
	@Test (expected=XmlException.class)
	public void parseNumberPeriodWithDateFormatTest() throws Exception {
		TaskParser.parsePeriod("123:day", "number");
	}
	
	@Test (expected=XmlException.class)
	public void parseNumberPeriodWithWrongPeriodTest() throws Exception {
		TaskParser.parsePeriod("abc:", "date");
	}
	
	@Test (expected=XmlException.class)
	public void parseTaskDemoWithWrongRangeTypeTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskDemoWithWrongRangeType.xml");
		TaskParser.parseTaskTemplate(is);
		is.close();
	}
	
	@Test (expected=XmlException.class)
	public void parseTaskDemoWithoutMinOrMaxNameTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskDemoWithoutMinOrMaxName.xml");
		TaskParser.parseTaskTemplate(is);
		is.close();
	}
	
	@Test
	public void parseTaskTemplateWithNumberRangeTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskDemoWithNumberRange.xml");
		ApiDO api = TaskParser.parseTaskTemplate(is);
		is.close();
		//测试任务名称填充
		Assert.assertEquals("taobao.topats.trades.price.update", api.getTaskName());
		//测试子任务基本模板
		ParamsDO params = api.getParamsRule();
		Assert.assertEquals(Integer.valueOf(1), params.getParamsMinSize());
		Assert.assertEquals(Integer.valueOf(40), params.getParamsMaxSize());
		Assert.assertNotNull(params.getParamRange());
		Assert.assertEquals(1, params.getParamsCheck().size());
		Assert.assertEquals(1, params.getParamsKeep().size());
		Assert.assertEquals(2, params.getParamsParse().size());
		//测试range类型参数
		Assert.assertEquals(ModelKeyConstants.RANGE_TYPE_NUMBER, params.getParamRange().getRangeType());
		Assert.assertEquals(Integer.valueOf(400), params.getParamRange().getParsePeriod());
		Assert.assertNull(params.getParamRange().getParseMeasure());
		Assert.assertEquals(Integer.valueOf(3000), params.getParamRange().getMaxPeriod());
		Assert.assertNull(params.getParamRange().getMaxMeasure());
		Assert.assertEquals(Long.valueOf(1),params.getParamRange().getMinValue());
		Assert.assertEquals(Long.valueOf(10000),params.getParamRange().getMaxValue());
		Assert.assertEquals("start_created", params.getParamRange().getMinName());
		Assert.assertEquals("end_created", params.getParamRange().getMaxName());
		Assert.assertEquals("start_created_back", params.getParamRange().getTargetMinName());
		Assert.assertEquals("end_created_back", params.getParamRange().getTargetMaxName());
		Assert.assertTrue(params.getParamRange().getEnablePage());
		Assert.assertEquals("page_no", params.getParamRange().getPageNoName());
		Assert.assertEquals("page_size", params.getParamRange().getPageSizeName());
		Assert.assertEquals("total_result", params.getParamRange().getTotalResultName());
		Assert.assertEquals(Integer.valueOf(100), params.getParamRange().getPageSize());
		//测试只作为校验的参数
		Assert.assertEquals("cert_sign", params.getParamsCheck().get(0).getName());
		Assert.assertEquals("cert_sign", params.getParamsCheck().get(0).getTargetName());
		Assert.assertEquals(null, params.getParamsCheck().get(0).getSeparator());
		Assert.assertEquals("string", params.getParamsCheck().get(0).getTargetType());
		Assert.assertEquals(Boolean.FALSE, params.getParamsCheck().get(0).getEnableSplit());
		Assert.assertEquals(Boolean.TRUE, params.getParamsCheck().get(0).getForCheck());
		Assert.assertEquals(Boolean.FALSE, params.getParamsCheck().get(0).getTargetEnableNull());
		//测试不分割的参数
		Assert.assertEquals("nick", params.getParamsKeep().get(0).getName());
		Assert.assertEquals("nick", params.getParamsKeep().get(0).getTargetName());
		Assert.assertEquals(null, params.getParamsKeep().get(0).getSeparator());
		Assert.assertEquals("string", params.getParamsKeep().get(0).getTargetType());
		Assert.assertEquals(Boolean.FALSE, params.getParamsKeep().get(0).getEnableSplit());
		Assert.assertEquals(Boolean.FALSE, params.getParamsKeep().get(0).getForCheck());
		Assert.assertEquals(Boolean.FALSE, params.getParamsKeep().get(0).getTargetEnableNull());
		//测试需分割的参数
		for (ParameterDO parameter : params.getParamsParse()) {
			//测试name和target——name不一致，子格式为数字
			if (parameter.getName().equals("tids")) {
				Assert.assertEquals("tids", parameter.getName());
				Assert.assertEquals("tid", parameter.getTargetName());
				Assert.assertEquals(";", parameter.getSeparator());
				Assert.assertEquals("number", parameter.getTargetType());
				Assert.assertEquals(Boolean.TRUE, parameter.getEnableSplit());
				Assert.assertEquals(Boolean.FALSE, parameter.getForCheck());
				Assert.assertEquals(Boolean.FALSE, parameter.getTargetEnableNull());
			}else if (parameter.getName().equals("logistics_fees")) {
				//测试子数字可以为空的
				Assert.assertEquals("logistics_fees", parameter.getName());
				Assert.assertEquals("logistics_fee", parameter.getTargetName());
				Assert.assertEquals(";", parameter.getSeparator());
				Assert.assertEquals("price", parameter.getTargetType());
				Assert.assertEquals(Boolean.TRUE, parameter.getEnableSplit());
				Assert.assertEquals(Boolean.FALSE, parameter.getForCheck());
				Assert.assertEquals(Boolean.TRUE, parameter.getTargetEnableNull());
			}
		}
	}
	
	@Test
	public void parseTaskDemoWithPriorityAndRangeNotPageableTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskDemoWithPriorityAndRangeNotPageable.xml");
		ApiDO api = TaskParser.parseTaskTemplate(is);
		is.close();
		//测试子任务基本模板
		ParamsDO params = api.getParamsRule();
		Assert.assertFalse(params.getParamRange().getEnablePage());
		Assert.assertNull(params.getParamRange().getPageNoName());
		Assert.assertNull(params.getParamRange().getPageSizeName());
		Assert.assertNull(params.getParamRange().getTotalResultName());
		Assert.assertNull(params.getParamRange().getPageSize());
	}
	
	@Test (expected=XmlException.class)
	public void parseTaskDemoWithPriorityAndRangeWithoutAllPageParamsTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskDemoWithPriorityAndRangeWithoutAllPageParams.xml");
		TaskParser.parseTaskTemplate(is);
		is.close();
	}
	
	@Test
	public void parseString2TimePeriodTest() throws Exception {
		TimePeriod timePeriod = TaskParser.parseString2TimePeriod("11:10:00-12:00:00");
		Assert.assertEquals(DateKit.ymdOrYmdhms2Date("2010-11-18 11:10:00"),timePeriod.getStartDate());
		Assert.assertEquals(DateKit.ymdOrYmdhms2Date("2010-11-18 12:00:00"),timePeriod.getEndDate());
	}
	
	@Test (expected=XmlException.class)
	public void parseString2TimePeriodWithBlankTest() throws Exception {
		TaskParser.parseString2TimePeriod(" ");
	}
	
	@Test (expected=XmlException.class)
	public void parseString2TimePeriodWithWrongSeparatorTest() throws Exception {
		TaskParser.parseString2TimePeriod(" -");
	}
	
	@Test (expected=XmlException.class)
	public void parseString2TimePeriodWithWrongLengthTest() throws Exception {
		TaskParser.parseString2TimePeriod("11:8:00-12:00:00");
	}
	
	@Test
	public void writeXmlTagTest() throws Exception {
		Assert.assertNull(TaskParser.writeXmlTag(" ", "abc", true));
		Assert.assertEquals("<key list=\"true\">abc</key>", TaskParser.writeXmlTag("key", "abc", true));
		Assert.assertEquals("<key list=\"true\"></key>", TaskParser.writeXmlTag("key", "", true));
		Assert.assertEquals("<key>abc</key>", TaskParser.writeXmlTag("key", "abc", false));
	}
	
	@Test
	public void taskWithoutSendoutResultsTest() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/TaskWithoutSendoutResults.xml");
		ApiDO api = TaskParser.parseTaskTemplate(is);
		is.close();
		//测试子任务结果填充
		Assert.assertNull(api.getSubTaskSendOutResultsJson());
		Assert.assertNull(api.getSubTaskSendOutResultsXml());
	}
}
