package api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.taobao.common.tfs.TfsManager;
import com.taobao.top.ats.AtsException;
import com.taobao.top.ats.XmlException;
import com.taobao.top.ats.domain.ApiDO;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.ParameterDO;
import com.taobao.top.ats.domain.ResultSet;
import com.taobao.top.ats.domain.TimePeriod;
import com.taobao.top.ats.task.api.ApiManagerImpl;
import com.taobao.top.ats.task.api.TaskParser;
import com.taobao.top.ats.util.DateKit;
import com.taobao.top.ats.util.KeyConstants;
import com.taobao.top.ats.util.ModelKeyConstants;
import com.taobao.top.ats.util.StatusConstants;
import com.taobao.top.tim.service.SamService;

/**
 * 
 * @author moling
 * @since 1.0, 2010-8-24
 */
public class ApiManagerTest {
	ApiManagerImpl apiManager = new ApiManagerImpl();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Test
	public void getErrorResponseTest() throws JSONException {
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"sub_msg\":\"msg\",\"sub_code\":\"code\"}"), 
				TaskParser.fromJsonToMap(apiManager.getErrorResponse("code", "msg", getSubtaskRequestMapWithFormatJson())));
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"sub_msg\":\"msg\",\"sub_code\":\"isp.unknown-error\"}"),
				TaskParser.fromJsonToMap(apiManager.getErrorResponse(null, "msg", getSubtaskRequestMapWithFormatJson())));
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"sub_msg\":\"msg\",\"sub_code\":\"isp.unknown-error\"}"),
				TaskParser.fromJsonToMap(apiManager.getErrorResponse("", "msg", getSubtaskRequestMapWithFormatJson())));
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"sub_msg\":\"msg\",\"sub_code\":\"isp.unknown-error\"}"), 
				TaskParser.fromJsonToMap(apiManager.getErrorResponse(" ", "msg", getSubtaskRequestMapWithFormatJson())));
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"sub_msg\":\"此错误未被详细说明\",\"sub_code\":\"code\"}"),
				TaskParser.fromJsonToMap(apiManager.getErrorResponse("code", null, getSubtaskRequestMapWithFormatJson())));
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"sub_msg\":\"此错误未被详细说明\",\"sub_code\":\"code\"}"), 
				TaskParser.fromJsonToMap(apiManager.getErrorResponse("code", "", getSubtaskRequestMapWithFormatJson())));
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"sub_msg\":\"此错误未被详细说明\",\"sub_code\":\"code\"}"),
				TaskParser.fromJsonToMap(apiManager.getErrorResponse("code", " ", getSubtaskRequestMapWithFormatJson())));
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"sub_msg\":\"此错误未被详细说明\",\"sub_code\":\"isp.unknown-error\"}"), 
				TaskParser.fromJsonToMap(apiManager.getErrorResponse(null, null, getSubtaskRequestMapWithFormatJson())));
		
		Assert.assertEquals("<sub_code>code</sub_code><sub_msg>msg</sub_msg>", apiManager.getErrorResponse("code", "msg", getSubtaskRequestMapWithFormatXml()));
		Assert.assertEquals("<sub_code>isp.unknown-error</sub_code><sub_msg>msg</sub_msg>", apiManager.getErrorResponse(null, "msg", getSubtaskRequestMapWithFormatXml()));
		Assert.assertEquals("<sub_code>code</sub_code><sub_msg>此错误未被详细说明</sub_msg>", apiManager.getErrorResponse("code", " ", getSubtaskRequestMapWithFormatXml()));
		Assert.assertEquals("<sub_code>isp.unknown-error</sub_code><sub_msg>此错误未被详细说明</sub_msg>", apiManager.getErrorResponse("", " ", getSubtaskRequestMapWithFormatXml()));
	}
	
	@Test
	public void getIngoreErrorResponseTest() throws Exception {
		initApi("/TaskDemo.xml");
		Date date = new Date();
		Map<String, String> request = getRightRequestSubTaskMap();
		Assert.assertEquals("{\"trade\":{\"tid\":1111,\"modified\":\"" + sdf.format(date) + "\"}}",
				apiManager.getIngoreErrorResponse("taobao.topats.trades.price.update", request));
		request.put(KeyConstants.FORMAT, KeyConstants.FORMAT_XML);
		Assert.assertEquals("<trade><tid>1111</tid><modified>" + sdf.format(date) + "</modified></trade>",
				apiManager.getIngoreErrorResponse("taobao.topats.trades.price.update", request));
	}
	
	@Test (expected=AtsException.class)
	public void getIngoreErrorResponseWithWrongApiTest() throws Exception {
		initApi("/TaskDemo.xml");
		apiManager.getIngoreErrorResponse("taobao.topats.api", getRightRequestSubTaskMap());
	}
	
	@Test (expected=AtsException.class)
	public void getIngoreErrorResponseWithSendoutResultsTest() throws Exception {
		initApi("/TaskWithoutSendoutResults.xml");
		apiManager.getIngoreErrorResponse("taobao.topats.trades.price.update", getRightRequestSubTaskMap());
	}
	
	@Test
	public void getRetrunRequestTest() throws Exception {
		initApi("/TaskDemo.xml");
		Map<String, String> request = getRightRequestSubTaskMap();
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"oids\":\"123,124\",\"adjust_fees\":\"10,12\",\"origin_fees\":\"20,22\",\"logistics_fee\":\"10\",\"tid\":\"1111\"}"),
				TaskParser.fromJsonToMap(apiManager.getRetrunRequest("taobao.topats.trades.price.update", request)));
		request.put(KeyConstants.FORMAT, KeyConstants.FORMAT_XML);
		Assert.assertEquals("<oids>123,124</oids><adjust_fees>10,12</adjust_fees><tid>1111</tid><origin_fees>20,22</origin_fees><logistics_fee>10</logistics_fee>",
				apiManager.getRetrunRequest("taobao.topats.trades.price.update", request));
	}
	
	@Test (expected=AtsException.class)
	public void getRetrunRequestWithWrongApiTest() throws Exception {
		initApi("/TaskDemo.xml");
		apiManager.getRetrunRequest("taobao.topats.api", getRightRequestSubTaskMap());
	}
	
	@Test
	public void getRetrunRequestWithoutSendoutRequestsTest() throws Exception {
		initApi("/TaskWithoutSendoutRequests.xml");
		Map<String, String> request = getRightRequestSubTaskMap();
		Assert.assertEquals("{}",
				apiManager.getRetrunRequest("taobao.topats.trades.price.update", request));
		request.put(KeyConstants.FORMAT, KeyConstants.FORMAT_XML);
		Assert.assertEquals("",
				apiManager.getRetrunRequest("taobao.topats.trades.price.update", request));
	}
	
	@Test
	public void getTaskTestParse() throws Exception {
		initApi("/TaskDemo.xml");
		AtsTaskDO task = apiManager.getTask(getRightRequestTaskMap());
		
		//父任务值判断
		Assert.assertEquals("123456",task.getAppKey());
		Assert.assertEquals("taobao.topats.trades.price.update", task.getApiName());
		Assert.assertNotNull(task.getGmtCreated());
		Assert.assertEquals(Integer.valueOf(1), task.getPriority());
		Assert.assertEquals(Integer.valueOf(0), task.getRetries());
		Assert.assertEquals(Integer.valueOf(StatusConstants.TASK_STATUS_NEW.getStatus()), task.getRetries());
		Assert.assertEquals(3, task.getSubTasks().size());
		Assert.assertEquals(KeyConstants.FORMAT_JSON, task.getAttributes().get(KeyConstants.FORMAT));
		Assert.assertNull(task.getAttributes().get(ModelKeyConstants.IS_BIG_RESULT));
		
		//子任务1结果判断
		Assert.assertEquals(Integer.valueOf(StatusConstants.SUBTASK_STATUS_NEW.getStatus()), task.getSubTasks().get(0).getStatus());
		Assert.assertNotNull(task.getSubTasks().get(0).getGmtCreated());
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"adjust_fees\":\"10,12\",\"app_ip\":\"10.10.10.10\",\"app_key\":\"123456\"," +
				"\"logistics_fee\":\"10\",\"type\":\"hsf\",\"interface\":\"com.taobao.api.trade.TradeService\"," +
				"\"version\":\"1.0.0\",\"timestamp\":\"2012-12-12 23:59:59\",\"session_uid\":\"999999\"," +
				"\"oids\":\"123,124\",\"top_enduser_ip\":\"20.20.20.20\",\"origin_fees\":\"20,22\"," +
				"\"top_bind_nick\":\"bind_nick\",\"top_tag\":\"8\",\"nick\":\"testnick\",\"session_nick\":\"nick\"," +
				"\"top_isv_id\":\"888888\",\"method\":\"doTradePriceUpdate\",\"tid\":\"1111\",\"is_ats\":\"true\",\"format\":\"json\"}"), 
				TaskParser.fromJsonToMap(task.getSubTasks().get(0).getRequest()));
		//子任务2结果判断
		Assert.assertEquals(Integer.valueOf(StatusConstants.SUBTASK_STATUS_NEW.getStatus()), task.getSubTasks().get(1).getStatus());
		Assert.assertNotNull(task.getSubTasks().get(1).getGmtCreated());
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"adjust_fees\":\"1.2,0.33,-0.56\",\"app_ip\":\"10.10.10.10\",\"app_key\":\"123456\"," +
				"\"type\":\"hsf\",\"interface\":\"com.taobao.api.trade.TradeService\"," +
				"\"version\":\"1.0.0\",\"timestamp\":\"2012-12-12 23:59:59\",\"session_uid\":\"999999\"," +
				"\"oids\":\"223,224,225\",\"top_enduser_ip\":\"20.20.20.20\",\"origin_fees\":\"10,12,60\"," +
				"\"top_bind_nick\":\"bind_nick\",\"top_tag\":\"8\",\"nick\":\"testnick\",\"session_nick\":\"nick\"," +
				"\"top_isv_id\":\"888888\",\"method\":\"doTradePriceUpdate\",\"tid\":\"2222\",\"is_ats\":\"true\",\"format\":\"json\"}"), 
				TaskParser.fromJsonToMap(task.getSubTasks().get(1).getRequest()));
		//子任务3结果判断
		Assert.assertEquals(Integer.valueOf(StatusConstants.SUBTASK_STATUS_NEW.getStatus()), task.getSubTasks().get(2).getStatus());
		Assert.assertNotNull(task.getSubTasks().get(2).getGmtCreated());
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"adjust_fees\":\"-3\",\"app_ip\":\"10.10.10.10\",\"app_key\":\"123456\"," +
				"\"type\":\"hsf\",\"interface\":\"com.taobao.api.trade.TradeService\"," +
				"\"version\":\"1.0.0\",\"timestamp\":\"2012-12-12 23:59:59\",\"session_uid\":\"999999\"," +
				"\"oids\":\"323\",\"top_enduser_ip\":\"20.20.20.20\",\"origin_fees\":\"80\"," +
				"\"top_bind_nick\":\"bind_nick\",\"top_tag\":\"8\",\"nick\":\"testnick\",\"session_nick\":\"nick\"," +
				"\"top_isv_id\":\"888888\",\"method\":\"doTradePriceUpdate\",\"tid\":\"3333\",\"is_ats\":\"true\",\"format\":\"json\"}"), 
				TaskParser.fromJsonToMap(task.getSubTasks().get(2).getRequest()));

	}
	
	@Test
	public void getTaskTestDateRange() throws Exception {
		initApi("/TaskDemoWithPriorityAndRange.xml");
		AtsTaskDO task = apiManager.getTask(getRightRequestTaskMapWithDateRangeParams());
		
		//父任务值判断
		Assert.assertEquals("123456",task.getAppKey());
		Assert.assertEquals("taobao.topats.trades.price.update", task.getApiName());
		Assert.assertNotNull(task.getGmtCreated());
		Assert.assertEquals(Integer.valueOf(2), task.getPriority());
		Assert.assertEquals(Integer.valueOf(0), task.getRetries());
		Assert.assertEquals(Integer.valueOf(StatusConstants.TASK_STATUS_NEW.getStatus()), task.getRetries());
		Assert.assertEquals(5, task.getSubTasks().size());
		Assert.assertEquals(KeyConstants.FORMAT_JSON, task.getAttributes().get(KeyConstants.FORMAT));
		Assert.assertEquals(ModelKeyConstants.BOOLEAN_ATTRIBUTE_MARK, task.getAttributes().get(ModelKeyConstants.IS_BIG_RESULT));
		
		//子任务1结果判断
		Assert.assertEquals(Integer.valueOf(StatusConstants.SUBTASK_STATUS_NEW.getStatus()), task.getSubTasks().get(0).getStatus());
		Assert.assertNotNull(task.getSubTasks().get(0).getGmtCreated());
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"app_ip\":\"10.10.10.10\",\"app_key\":\"123456\"," +
				"\"type\":\"hsf\",\"interface\":\"com.taobao.api.trade.TradeService\",\"end_created_back\":\"2010-11-14 00:00:00\"," +
				"\"version\":\"1.0.0\",\"timestamp\":\"2012-12-12 23:59:59\",\"session_uid\":\"999999\"," +
				"\"top_enduser_ip\":\"20.20.20.20\",\"start_created_back\":\"2010-11-10 00:00:00\"," +
				"\"top_bind_nick\":\"bind_nick\",\"top_tag\":\"8\",\"nick\":\"testnick\",\"session_nick\":\"nick\"," +
				"\"top_isv_id\":\"888888\",\"method\":\"doTradePriceUpdate\",\"is_ats\":\"true\",\"format\":\"json\"}"), 
				TaskParser.fromJsonToMap(task.getSubTasks().get(0).getRequest()));
		//子任务2结果判断
		Assert.assertEquals(Integer.valueOf(StatusConstants.SUBTASK_STATUS_NEW.getStatus()), task.getSubTasks().get(1).getStatus());
		Assert.assertNotNull(task.getSubTasks().get(1).getGmtCreated());
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"app_ip\":\"10.10.10.10\",\"app_key\":\"123456\"," +
				"\"type\":\"hsf\",\"interface\":\"com.taobao.api.trade.TradeService\",\"end_created_back\":\"2010-11-18 00:00:00\"," +
				"\"version\":\"1.0.0\",\"timestamp\":\"2012-12-12 23:59:59\",\"session_uid\":\"999999\"," +
				"\"top_enduser_ip\":\"20.20.20.20\",\"start_created_back\":\"2010-11-14 00:00:00\"," +
				"\"top_bind_nick\":\"bind_nick\",\"top_tag\":\"8\",\"nick\":\"testnick\",\"session_nick\":\"nick\"," +
				"\"top_isv_id\":\"888888\",\"method\":\"doTradePriceUpdate\",\"is_ats\":\"true\",\"format\":\"json\"}"), 
				TaskParser.fromJsonToMap(task.getSubTasks().get(1).getRequest()));
		//子任务3结果判断
		Assert.assertEquals(Integer.valueOf(StatusConstants.SUBTASK_STATUS_NEW.getStatus()), task.getSubTasks().get(2).getStatus());
		Assert.assertNotNull(task.getSubTasks().get(2).getGmtCreated());
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"app_ip\":\"10.10.10.10\",\"app_key\":\"123456\"," +
				"\"type\":\"hsf\",\"interface\":\"com.taobao.api.trade.TradeService\",\"end_created_back\":\"2010-11-22 00:00:00\"," +
				"\"version\":\"1.0.0\",\"timestamp\":\"2012-12-12 23:59:59\",\"session_uid\":\"999999\"," +
				"\"top_enduser_ip\":\"20.20.20.20\",\"start_created_back\":\"2010-11-18 00:00:00\"," +
				"\"top_bind_nick\":\"bind_nick\",\"top_tag\":\"8\",\"nick\":\"testnick\",\"session_nick\":\"nick\"," +
				"\"top_isv_id\":\"888888\",\"method\":\"doTradePriceUpdate\",\"is_ats\":\"true\",\"format\":\"json\"}"), 
				TaskParser.fromJsonToMap(task.getSubTasks().get(2).getRequest()));
		//子任务4结果判断
		Assert.assertEquals(Integer.valueOf(StatusConstants.SUBTASK_STATUS_NEW.getStatus()), task.getSubTasks().get(3).getStatus());
		Assert.assertNotNull(task.getSubTasks().get(3).getGmtCreated());
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"app_ip\":\"10.10.10.10\",\"app_key\":\"123456\"," +
				"\"type\":\"hsf\",\"interface\":\"com.taobao.api.trade.TradeService\",\"end_created_back\":\"2010-11-26 00:00:00\"," +
				"\"version\":\"1.0.0\",\"timestamp\":\"2012-12-12 23:59:59\",\"session_uid\":\"999999\"," +
				"\"top_enduser_ip\":\"20.20.20.20\",\"start_created_back\":\"2010-11-22 00:00:00\"," +
				"\"top_bind_nick\":\"bind_nick\",\"top_tag\":\"8\",\"nick\":\"testnick\",\"session_nick\":\"nick\"," +
				"\"top_isv_id\":\"888888\",\"method\":\"doTradePriceUpdate\",\"is_ats\":\"true\",\"format\":\"json\"}"), 
				TaskParser.fromJsonToMap(task.getSubTasks().get(3).getRequest()));
		//子任务5结果判断
		Assert.assertEquals(Integer.valueOf(StatusConstants.SUBTASK_STATUS_NEW.getStatus()), task.getSubTasks().get(4).getStatus());
		Assert.assertNotNull(task.getSubTasks().get(4).getGmtCreated());
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"app_ip\":\"10.10.10.10\",\"app_key\":\"123456\"," +
				"\"type\":\"hsf\",\"interface\":\"com.taobao.api.trade.TradeService\",\"end_created_back\":\"2010-11-28 20:00:00\"," +
				"\"version\":\"1.0.0\",\"timestamp\":\"2012-12-12 23:59:59\",\"session_uid\":\"999999\"," +
				"\"top_enduser_ip\":\"20.20.20.20\",\"start_created_back\":\"2010-11-26 00:00:00\"," +
				"\"top_bind_nick\":\"bind_nick\",\"top_tag\":\"8\",\"nick\":\"testnick\",\"session_nick\":\"nick\"," +
				"\"top_isv_id\":\"888888\",\"method\":\"doTradePriceUpdate\",\"is_ats\":\"true\",\"format\":\"json\"}"), 
				TaskParser.fromJsonToMap(task.getSubTasks().get(4).getRequest()));

	}
	
	@Test
	public void getTaskTestNumberRange() throws Exception {
		initApi("/TaskDemoWithNumberRange.xml");
		AtsTaskDO task = apiManager.getTask(getRightRequestTaskMapWithNumberRangeParams());
		
		//父任务值判断
		Assert.assertEquals("123456",task.getAppKey());
		Assert.assertEquals("taobao.topats.trades.price.update", task.getApiName());
		Assert.assertNotNull(task.getGmtCreated());
		Assert.assertEquals(Integer.valueOf(2), task.getPriority());
		Assert.assertEquals(Integer.valueOf(0), task.getRetries());
		Assert.assertEquals(Integer.valueOf(StatusConstants.TASK_STATUS_NEW.getStatus()), task.getRetries());
		Assert.assertEquals(5, task.getSubTasks().size());
		Assert.assertEquals(KeyConstants.FORMAT_JSON, task.getAttributes().get(KeyConstants.FORMAT));
		Assert.assertEquals(ModelKeyConstants.BOOLEAN_ATTRIBUTE_MARK, task.getAttributes().get(ModelKeyConstants.IS_BIG_RESULT));
		
		//子任务1结果判断
		Assert.assertEquals(Integer.valueOf(StatusConstants.SUBTASK_STATUS_NEW.getStatus()), task.getSubTasks().get(0).getStatus());
		Assert.assertNotNull(task.getSubTasks().get(0).getGmtCreated());
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"app_ip\":\"10.10.10.10\",\"app_key\":\"123456\"," +
				"\"type\":\"hsf\",\"interface\":\"com.taobao.api.trade.TradeService\",\"end_created_back\":\"404\"," +
				"\"version\":\"1.0.0\",\"timestamp\":\"2012-12-12 23:59:59\",\"session_uid\":\"999999\"," +
				"\"top_enduser_ip\":\"20.20.20.20\",\"start_created_back\":\"5\"," +
				"\"top_bind_nick\":\"bind_nick\",\"top_tag\":\"8\",\"nick\":\"testnick\",\"session_nick\":\"nick\"," +
				"\"top_isv_id\":\"888888\",\"method\":\"doTradePriceUpdate\",\"is_ats\":\"true\",\"format\":\"json\"}"), 
				TaskParser.fromJsonToMap(task.getSubTasks().get(0).getRequest()));
		//子任务2结果判断
		Assert.assertEquals(Integer.valueOf(StatusConstants.SUBTASK_STATUS_NEW.getStatus()), task.getSubTasks().get(1).getStatus());
		Assert.assertNotNull(task.getSubTasks().get(1).getGmtCreated());
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"app_ip\":\"10.10.10.10\",\"app_key\":\"123456\"," +
				"\"type\":\"hsf\",\"interface\":\"com.taobao.api.trade.TradeService\",\"end_created_back\":\"804\"," +
				"\"version\":\"1.0.0\",\"timestamp\":\"2012-12-12 23:59:59\",\"session_uid\":\"999999\"," +
				"\"top_enduser_ip\":\"20.20.20.20\",\"start_created_back\":\"405\"," +
				"\"top_bind_nick\":\"bind_nick\",\"top_tag\":\"8\",\"nick\":\"testnick\",\"session_nick\":\"nick\"," +
				"\"top_isv_id\":\"888888\",\"method\":\"doTradePriceUpdate\",\"is_ats\":\"true\",\"format\":\"json\"}"), 
				TaskParser.fromJsonToMap(task.getSubTasks().get(1).getRequest()));
		//子任务3结果判断
		Assert.assertEquals(Integer.valueOf(StatusConstants.SUBTASK_STATUS_NEW.getStatus()), task.getSubTasks().get(2).getStatus());
		Assert.assertNotNull(task.getSubTasks().get(2).getGmtCreated());
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"app_ip\":\"10.10.10.10\",\"app_key\":\"123456\"," +
				"\"type\":\"hsf\",\"interface\":\"com.taobao.api.trade.TradeService\",\"end_created_back\":\"1204\"," +
				"\"version\":\"1.0.0\",\"timestamp\":\"2012-12-12 23:59:59\",\"session_uid\":\"999999\"," +
				"\"top_enduser_ip\":\"20.20.20.20\",\"start_created_back\":\"805\"," +
				"\"top_bind_nick\":\"bind_nick\",\"top_tag\":\"8\",\"nick\":\"testnick\",\"session_nick\":\"nick\"," +
				"\"top_isv_id\":\"888888\",\"method\":\"doTradePriceUpdate\",\"is_ats\":\"true\",\"format\":\"json\"}"), 
				TaskParser.fromJsonToMap(task.getSubTasks().get(2).getRequest()));
		//子任务4结果判断
		Assert.assertEquals(Integer.valueOf(StatusConstants.SUBTASK_STATUS_NEW.getStatus()), task.getSubTasks().get(3).getStatus());
		Assert.assertNotNull(task.getSubTasks().get(3).getGmtCreated());
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"app_ip\":\"10.10.10.10\",\"app_key\":\"123456\"," +
				"\"type\":\"hsf\",\"interface\":\"com.taobao.api.trade.TradeService\",\"end_created_back\":\"1604\"," +
				"\"version\":\"1.0.0\",\"timestamp\":\"2012-12-12 23:59:59\",\"session_uid\":\"999999\"," +
				"\"top_enduser_ip\":\"20.20.20.20\",\"start_created_back\":\"1205\"," +
				"\"top_bind_nick\":\"bind_nick\",\"top_tag\":\"8\",\"nick\":\"testnick\",\"session_nick\":\"nick\"," +
				"\"top_isv_id\":\"888888\",\"method\":\"doTradePriceUpdate\",\"is_ats\":\"true\",\"format\":\"json\"}"), 
				TaskParser.fromJsonToMap(task.getSubTasks().get(3).getRequest()));
		//子任务5结果判断
		Assert.assertEquals(Integer.valueOf(StatusConstants.SUBTASK_STATUS_NEW.getStatus()), task.getSubTasks().get(4).getStatus());
		Assert.assertNotNull(task.getSubTasks().get(4).getGmtCreated());
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"app_ip\":\"10.10.10.10\",\"app_key\":\"123456\"," +
				"\"type\":\"hsf\",\"interface\":\"com.taobao.api.trade.TradeService\",\"end_created_back\":\"1605\"," +
				"\"version\":\"1.0.0\",\"timestamp\":\"2012-12-12 23:59:59\",\"session_uid\":\"999999\"," +
				"\"top_enduser_ip\":\"20.20.20.20\",\"start_created_back\":\"1605\"," +
				"\"top_bind_nick\":\"bind_nick\",\"top_tag\":\"8\",\"nick\":\"testnick\",\"session_nick\":\"nick\"," +
				"\"top_isv_id\":\"888888\",\"method\":\"doTradePriceUpdate\",\"is_ats\":\"true\",\"format\":\"json\"}"), 
				TaskParser.fromJsonToMap(task.getSubTasks().get(4).getRequest()));

	}
	
	@Test (expected=AtsException.class)
	public void getTaskWithWrongApiTest() throws Exception {
		initApi("/TaskDemo.xml");
		apiManager.getTask(getWrongMethodRequestSubTaskMap());
	}
	
	@Test
	public void getTaskWithOneSubtaskTest() throws Exception {
		initApi("/TaskDemo.xml");
		AtsTaskDO task = apiManager.getTask(getRightRequestTaskWithOneSubtaskMap());
		
		//父任务值判断
		Assert.assertEquals("123456",task.getAppKey());
		Assert.assertEquals("taobao.topats.trades.price.update", task.getApiName());
		Assert.assertNotNull(task.getGmtCreated());
		Assert.assertEquals(Integer.valueOf(1), task.getPriority());
		Assert.assertEquals(Integer.valueOf(0), task.getRetries());
		Assert.assertEquals(Integer.valueOf(StatusConstants.TASK_STATUS_NEW.getStatus()), task.getRetries());
		Assert.assertEquals(1, task.getSubTasks().size());
		
		//子任务结果判断
		Assert.assertEquals(Integer.valueOf(StatusConstants.SUBTASK_STATUS_NEW.getStatus()), task.getSubTasks().get(0).getStatus());
		Assert.assertNotNull(task.getSubTasks().get(0).getGmtCreated());
		Assert.assertEquals(TaskParser.fromJsonToMap("{\"adjust_fees\":\"10,12\",\"app_ip\":\"10.10.10.10\",\"app_key\":\"123456\"," +
				"\"logistics_fee\":\"10\",\"type\":\"hsf\",\"interface\":\"com.taobao.api.trade.TradeService\"," +
				"\"version\":\"1.0.0\",\"timestamp\":\"2012-12-12 23:59:59\",\"session_uid\":\"999999\"," +
				"\"oids\":\"123,124\",\"top_enduser_ip\":\"20.20.20.20\",\"origin_fees\":\"20,22\"," +
				"\"top_bind_nick\":\"bind_nick\",\"top_tag\":\"8\",\"nick\":\"testnick\",\"session_nick\":\"nick\"," +
				"\"top_isv_id\":\"888888\",\"method\":\"doTradePriceUpdate\",\"tid\":\"1111\",\"is_ats\":\"true\",\"format\":\"json\"}"), 
				TaskParser.fromJsonToMap(task.getSubTasks().get(0).getRequest()));
	}
	
	@Test
	public void isIgnoreErrorTest() throws Exception {
		initApi("/TaskDemo.xml");
		Assert.assertTrue(apiManager.isIgnoreError("taobao.topats.trades.price.update", "123456"));
	}
	
	@Test
	public void isIgnoreErrorWithoutIgnoreTest() throws Exception {
		initApi("/TaskWithoutIngoreAndRetryErrors.xml");
		Assert.assertFalse(apiManager.isIgnoreError("taobao.topats.trades.price.update", "123456"));
	}
	
	@Test
	public void isIgnoreErrorNotInlistTest() throws Exception {
		initApi("/TaskDemo.xml");
		Assert.assertFalse(apiManager.isIgnoreError("taobao.topats.trades.price.update", "333333"));
	}
	
	@Test (expected=AtsException.class)
	public void isIgnoreErrorApiNotExistTest() throws Exception {
		initApi("/TaskDemo.xml");
		apiManager.isIgnoreError("taobao.topats.api", "123456");
	}
	
	@Test
	public void isPreCheckOKTest() throws Exception {
		initApi("/TaskDemo.xml");
		apiManager.setApiEngine(new MockSuccessEngine());
		ResultSet result = apiManager.isPreCheckOK(getRightRequestTaskMap());
		Assert.assertFalse(result.isError());
		Assert.assertNull(result.getResult());
	}
	
	@Test
	public void isPreCheckOKApiNotExistTest() throws Exception {
		initApi("/TaskDemo.xml");
		Map<String, String> request = getRightRequestTaskMap();
		request.remove("method");
		ResultSet result = apiManager.isPreCheckOK(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.missing-parameter", result.getErrorCode());
		Assert.assertEquals("入参缺少method", result.getErrorMsg());
		
		request.put("method", "taobao.topats.api");
		result = apiManager.isPreCheckOK(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.api-not-exist", result.getErrorCode());
		Assert.assertEquals("指定的api不存在", result.getErrorMsg());
	}
	
	@Test
	public void isPreCheckOKErrorTest() throws Exception {
		initApi("/TaskDemo.xml");
		apiManager.setApiEngine(new MockOtherErrorEngine());
		ResultSet result = apiManager.isPreCheckOK(getRightRequestTaskMap());
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isp.trade-service-unavailable", result.getErrorCode());
		Assert.assertEquals("error,error", result.getErrorMsg());
	}
	
	@Test
	public void isRequestRightTest() throws Exception {
		initApi("/TaskDemo.xml");
		ResultSet result = apiManager.isRequestRight(getRightRequestTaskMap());
		Assert.assertFalse(result.isError());
	}
	
	@Test
	public void isRequestRightWithDateRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithPriorityAndRange.xml");
		ResultSet result = apiManager.isRequestRight(getRightRequestTaskMapWithDateRangeParams());
		Assert.assertFalse(result.isError());
	}
	
	@Test
	public void isRequestRightWithNumberRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithNumberRange.xml");
		ResultSet result = apiManager.isRequestRight(getRightRequestTaskMapWithNumberRangeParams());
		Assert.assertFalse(result.isError());
	}
	
	@Test
	public void isRequestMinValueEmptyWithRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithPriorityAndRange.xml");
		Map<String, String> request = getRightRequestTaskMapWithDateRangeParams();
		request.remove("start_created");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.missing-parameter:start_created", result.getErrorCode());
		Assert.assertEquals("参数start_created不能有为空的值", result.getErrorMsg());
	}
	
	@Test
	public void isRequestMaxValueEmptyWithRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithPriorityAndRange.xml");
		Map<String, String> request = getRightRequestTaskMapWithDateRangeParams();
		request.remove("end_created");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.missing-parameter:end_created", result.getErrorCode());
		Assert.assertEquals("参数end_created不能有为空的值", result.getErrorMsg());
	}
	
	@Test
	public void isRequestMinValueNotDateWithDateRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithPriorityAndRange.xml");
		Map<String, String> request = getRightRequestTaskMapWithDateRangeParams();
		request.put("start_created", "102-6-6 11:2:0");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:start_created", result.getErrorCode());
		Assert.assertEquals("参数start_created不符合格式，不是时间类型", result.getErrorMsg());
	}
	
	@Test
	public void isRequestMaxValueNotDateWithDateRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithPriorityAndRange.xml");
		Map<String, String> request = getRightRequestTaskMapWithDateRangeParams();
		request.put("end_created", "102-6-6 11:2:0");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:end_created", result.getErrorCode());
		Assert.assertEquals("参数end_created不符合格式，不是时间类型", result.getErrorMsg());
	}
	
	@Test
	public void isRequestMinValueNotNumberWithNumberRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithNumberRange.xml");
		Map<String, String> request = getRightRequestTaskMapWithNumberRangeParams();
		request.put("start_created", "102-6-6 11:2:0");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:start_created", result.getErrorCode());
		Assert.assertEquals("参数start_created不符合格式，不是数字类型", result.getErrorMsg());
	}
	
	@Test
	public void isRequestMaxValueNotNumberWithNumberRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithNumberRange.xml");
		Map<String, String> request = getRightRequestTaskMapWithNumberRangeParams();
		request.put("end_created", "102-6-6 11:2:0");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:end_created", result.getErrorCode());
		Assert.assertEquals("参数end_created不符合格式，不是数字类型", result.getErrorMsg());
	}
	
	@Test
	public void isRequestMimLargerThanMaxWithDateRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithPriorityAndRange.xml");
		Map<String, String> request = getRightRequestTaskMapWithDateRangeParams();
		request.put("end_created", "2010-11-09 00:00:00");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:start_created,end_created", result.getErrorCode());
		Assert.assertEquals("参数start_created必需小于end_created", result.getErrorMsg());
	}
	
	@Test
	public void isRequestMinTooSmallWithDateRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithPriorityAndRange.xml");
		Map<String, String> request = getRightRequestTaskMapWithDateRangeParams();
		request.put("start_created", "2010-05-10 00:00:00");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:start_created", result.getErrorCode());
		Assert.assertEquals("参数start_created不能小于2010-06-10 00:00:00", result.getErrorMsg());
	}
	
	@Test
	public void isRequestMaxTooLargeWithDateRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithPriorityAndRange.xml");
		Map<String, String> request = getRightRequestTaskMapWithDateRangeParams();
		request.put("end_created", "2013-05-10 00:00:00");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:end_created", result.getErrorCode());
		Assert.assertEquals("参数end_created不能大于2012-06-10 00:00:00", result.getErrorMsg());
	}
	
	@Test
	public void isRequestRangeTooLargeWithDateRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithPriorityAndRange.xml");
		Map<String, String> request = getRightRequestTaskMapWithDateRangeParams();
		request.put("end_created", "2011-03-10 00:00:00");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:start_created,end_created", result.getErrorCode());
		Assert.assertEquals("参数start_created和end_created间隔区间超过大小限制", result.getErrorMsg());
	}
	
	@Test
	public void isRequestMinLargerThanMaxWithNumberRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithNumberRange.xml");
		Map<String, String> request = getRightRequestTaskMapWithNumberRangeParams();
		request.put("end_created", "3");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:start_created,end_created", result.getErrorCode());
		Assert.assertEquals("参数start_created必需小于end_created", result.getErrorMsg());
	}
	
	@Test
	public void isRequestMinTooSmallWithNumberRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithNumberRange.xml");
		Map<String, String> request = getRightRequestTaskMapWithNumberRangeParams();
		request.put("start_created", "0");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:start_created", result.getErrorCode());
		Assert.assertEquals("参数start_created不能小于1", result.getErrorMsg());
	}
	
	@Test
	public void isRequestMaxTooLargeWithNumberRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithNumberRange.xml");
		Map<String, String> request = getRightRequestTaskMapWithNumberRangeParams();
		request.put("end_created", "10001");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:end_created", result.getErrorCode());
		Assert.assertEquals("参数end_created不能大于10000", result.getErrorMsg());
	}
	
	@Test
	public void isRequestRangeTooLargeWithNumberRangeParamsTest() throws Exception {
		initApi("/TaskDemoWithNumberRange.xml");
		Map<String, String> request = getRightRequestTaskMapWithNumberRangeParams();
		request.put("end_created", "4000");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:start_created,end_created", result.getErrorCode());
		Assert.assertEquals("参数start_created和end_created间隔区间超过大小限制", result.getErrorMsg());
	}
	
	@Test
	public void isRequestRightApiNotExistTest() throws Exception {
		initApi("/TaskDemo.xml");
		Map<String, String> request = getRightRequestTaskMap();
		request.remove("method");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.missing-parameter", result.getErrorCode());
		Assert.assertEquals("入参缺少method", result.getErrorMsg());
		
		request.put("method", "taobao.topats.api");
		result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.api-not-exist", result.getErrorCode());
		Assert.assertEquals("指定的api不存在", result.getErrorMsg());
	}
	
	@Test
	public void isRequestRightWithoutRulesTest() throws Exception {
		initApi("/TaskWithoutParamsRules.xml");
		ResultSet result = apiManager.isRequestRight(getRightRequestTaskMap());
		Assert.assertFalse(result.isError());
	}
	
	@Test
	public void isRequestRightPrecheckErrorTest() throws Exception {
		initApi("/TaskDemo.xml");
		Map<String, String> request = getRightRequestTaskMap();
		request.remove("cert_sign");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:cert_sign", result.getErrorCode());
		Assert.assertEquals("参数cert_sign不能有为空的值", result.getErrorMsg());
	}
	
	@Test
	public void isRequestRightNoParseParamsErrorTest() throws Exception {
		initApi("/TaskWithoutPrecheck.xml");
		Map<String, String> request = getRightRequestTaskMap();
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:nick", result.getErrorCode());
		Assert.assertEquals("参数nick不符合格式，不是时间类型", result.getErrorMsg());
	}
	
	@Test
	public void isRequestRightParamsLengthNotEqualTest() throws Exception {
		initApi("/TaskDemo.xml");
		Map<String, String> request = getRightRequestTaskMap();
		request.put("tids", "11111;22222;33333;44444");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.mismatch-parameter", result.getErrorCode());
		Assert.assertEquals("tids,oids,adjust_fees,logistics_fees,origin_fees,长度不一致", result.getErrorMsg());
	}
	
	@Test
	public void isRequestRightParamsTypeErrorTest() throws Exception {
		initApi("/TaskDemo.xml");
		Map<String, String> request = getRightRequestTaskMap();
		request.put("tids", "aaa;22222;33333");
		ResultSet result = apiManager.isRequestRight(request);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:tids", result.getErrorCode());
		Assert.assertEquals("参数tids不符合格式，不是数字类型", result.getErrorMsg());
	}
	
	@Test
	public void isRequestRightParamsLengthNotRightTest() throws Exception {
		initApi("/TaskDemo.xml");
		apiManager.getApis().get("taobao.topats.trades.price.update").getParamsRule().setParamsMaxSize(2);
		
		ResultSet result = apiManager.isRequestRight(getRightRequestTaskMap());
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter", result.getErrorCode());
		Assert.assertEquals("tids,oids,adjust_fees,logistics_fees,origin_fees,长度超过限制区域：1~2", result.getErrorMsg());
	}
	
	public void isRetryErrorTest() throws Exception {
		initApi("/TaskDemo.xml");
		Assert.assertTrue(apiManager.isRetryError("taobao.topats.trades.price.update", "isp.trade-service-unavailable"));
	}
	
	@Test
	public void isRetryErrorWithoutRetryTest() throws Exception {
		initApi("/TaskWithoutIngoreAndRetryErrors.xml");
		Assert.assertFalse(apiManager.isIgnoreError("taobao.topats.trades.price.update", "isp.trade-service-unavailable"));
	}
	
	@Test (expected=AtsException.class)
	public void isRetryErrorApiNotExistTest() throws Exception {
		initApi("/TaskDemo.xml");
		apiManager.isRetryError("taobao.topats.api", "123456");
	}
	
	@Test
	public void isRetryErrorNotInlistTest() throws Exception {
		initApi("/TaskDemo.xml");
		Assert.assertFalse(apiManager.isRetryError("taobao.topats.trades.price.update", "not exist api"));
	}
	
	@Test
	public void checkTypeTest() {
		ParameterDO parameter = new ParameterDO();
		//初始化一个基本的parameterDO
		parameter.setEnableSplit(true);
		parameter.setForCheck(false);
		parameter.setName("name");
		parameter.setSeparator(";");
		parameter.setTargetEnableNull(false);
		parameter.setTargetName("targetName");
		parameter.setTargetType("string");
		
		ResultSet result = null;
		
		//测试标记不能为空的参数子值为空的时候
		result = new ResultSet();
		apiManager.checkType("", parameter, result);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:name", result.getErrorCode());
		Assert.assertEquals("参数name不能有为空的值", result.getErrorMsg());
		
		//测试标记能为空的参数子值为空的时候
		result = new ResultSet();
		parameter.setTargetEnableNull(true);
		apiManager.checkType("", parameter, result);
		Assert.assertFalse(result.isError());
		
		//测试非数字类型报错
		parameter.setTargetType("number");
		apiManager.checkType("-bc.23", parameter, result);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:name", result.getErrorCode());
		Assert.assertEquals("参数name不符合格式，不是数字类型", result.getErrorMsg());
		//测试非价格类型报错
		parameter.setTargetType("price");
		apiManager.checkType("-123.666", parameter, result);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:name", result.getErrorCode());
		Assert.assertEquals("参数name不符合格式，不是价格类型", result.getErrorMsg());
		//测试非时间类型报错
		parameter.setTargetType("date");
		apiManager.checkType("2010-8-9 1/2/3", parameter, result);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:name", result.getErrorCode());
		Assert.assertEquals("参数name不符合格式，不是时间类型", result.getErrorMsg());
		//测试非boolean类型报错
		parameter.setTargetType("boolean");
		apiManager.checkType("abc", parameter, result);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:name", result.getErrorCode());
		Assert.assertEquals("参数name不符合格式，不是布尔类型", result.getErrorMsg());
	}
	
	@Test
	public void checkParameterTest() {
		ParameterDO parameter = new ParameterDO();
		//初始化一个基本的parameterDO
		parameter.setEnableSplit(true);
		parameter.setForCheck(false);
		parameter.setName("name");
		parameter.setSeparator(";");
		parameter.setTargetEnableNull(false);
		parameter.setTargetName("targetName");
		parameter.setTargetType("string");
		
		ResultSet result = null;
		
		//测试可分割参数为空的情况
		result = new ResultSet();
		apiManager.checkParameter("", parameter, result);
		Assert.assertTrue(result.isError());
		Assert.assertEquals("isv.invalid-parameter:name", result.getErrorCode());
		Assert.assertEquals("参数name不能有为空的值", result.getErrorMsg());
		
		//测试可分割参数不为空的情况
		result = new ResultSet();
		apiManager.checkParameter("aaa,bbb,ccc", parameter, result);
		Assert.assertFalse(result.isError());
		
		//测试不可分割参数
		parameter.setEnableSplit(false);
		result = new ResultSet();
		apiManager.checkParameter("aa", parameter, result);
		Assert.assertFalse(result.isError());
	}
	
	@Test
	public void getActiveApisTest() throws ParseException {
		 Map<String, ApiDO> apis = new HashMap<String, ApiDO>();
		 TimePeriod timePeriod1 = new TimePeriod();
		 timePeriod1.setStartDate("00:00:00");
		 timePeriod1.setEndDate("06:00:00");
		 TimePeriod timePeriod2 = new TimePeriod();
		 timePeriod2.setStartDate("10:00:00");
		 timePeriod2.setEndDate("12:00:00");
		 TimePeriod timePeriod3 = new TimePeriod();
		 timePeriod3.setStartDate("17:00:00");
		 timePeriod3.setEndDate("20:00:00");
		 
		 ApiDO api1 = new ApiDO();
		 api1.setTaskName("taobao.api.one");
		 api1.setLevel(0);
		 List<TimePeriod> period1 = new ArrayList<TimePeriod>();
		 period1.add(timePeriod1);
		 period1.add(timePeriod2);
		 api1.setTimePeriods(period1);
		 apis.put("taobao.api.one", api1);
		 
		 ApiDO api2 = new ApiDO();
		 api2.setTaskName("taobao.api.two");
		 api2.setLevel(2);
		 List<TimePeriod> period2 = new ArrayList<TimePeriod>();
		 period2.add(timePeriod3);
		 period2.add(timePeriod2);
		 api2.setTimePeriods(period2);
		 apis.put("taobao.api.two", api2);
		 
		 ApiDO api3 = new ApiDO();
		 api3.setTaskName("taobao.api.three");
		 api3.setLevel(1);
		 apis.put("taobao.api.three", api3);
		 
		 apiManager.setApis(apis);
		 
		 Assert.assertFalse(apiManager.isApiIntime(null, null));
		 Assert.assertFalse(apiManager.isApiIntime("taobao.api.unknown", new Date()));
		 Assert.assertTrue(apiManager.isApiIntime("taobao.api.three", new Date()));
		 api3.setLevel(5);
		 Assert.assertFalse(apiManager.isApiIntime("taobao.api.three", new Date()));
		 Assert.assertTrue(apiManager.isApiIntime("taobao.api.one", DateKit.ymdOrYmdhms2Date("2010-11-18 09:00:00")));
		 Assert.assertFalse(apiManager.isApiIntime("taobao.api.two", DateKit.ymdOrYmdhms2Date("2010-11-18 09:00:00")));
		 Assert.assertTrue(apiManager.isApiIntime("taobao.api.two", DateKit.ymdOrYmdhms2Date("2010-11-18 11:00:00")));
	}
	
	@Test
	public void getPageDownRequest() throws Exception {
		initApi("/TaskDemoWithPriorityAndRange.xml");
		Map<String, String> wannaMap = getRightRequestSubTaskMap();
		wannaMap.put("page_no", "1");
		wannaMap.put("page_size", "100");
		Assert.assertEquals(wannaMap, apiManager.getPageDownRequest("taobao.topats.trades.price.update",
				getRightRequestSubTaskMap(), 10L));
		Assert.assertEquals(wannaMap, apiManager.getPageDownRequest("taobao.topats.trades.price.update",
				getRightRequestSubTaskMap(), null));
		
		wannaMap.put("page_no", "2");
		wannaMap.put("page_size", "100");
		Assert.assertEquals(wannaMap, apiManager.getPageDownRequest("taobao.topats.trades.price.update", 
				apiManager.getPageDownRequest("taobao.topats.trades.price.update", getRightRequestSubTaskMap(), null), 300L));
		Assert.assertEquals(wannaMap, apiManager.getPageDownRequest("taobao.topats.trades.price.update", 
				apiManager.getPageDownRequest("taobao.topats.trades.price.update", getRightRequestSubTaskMap(), null), 150L));
		Assert.assertNull(apiManager.getPageDownRequest("taobao.topats.trades.price.update", 
				apiManager.getPageDownRequest("taobao.topats.trades.price.update", getRightRequestSubTaskMap(), null), 90L));
	}
	
	@Test
	public void getPageDownRequestNotNeedPagedown() throws Exception {
		initApi("/TaskDemo.xml");
		Assert.assertNull(apiManager.getPageDownRequest("taobao.api.unknown", new HashMap<String, String>(), 2L));
		Assert.assertNull(apiManager.getPageDownRequest("taobao.topats.trades.price.update", new HashMap<String, String>(), 2L));
	}
	
	@Test
	public void apiIsBigResultTest() throws Exception {
		initApi("/TaskDemoWithPriorityAndRange.xml");
		Assert.assertTrue(apiManager.isBigResult("taobao.topats.trades.price.update"));
	}
	
	@Test (expected=AtsException.class)
	public void apiIsBigResultApiNotExistTest() throws Exception {
		initApi("/TaskDemoWithPriorityAndRange.xml");
		apiManager.isBigResult("taobao.api.unknown");
	}
	
	@Test
	public void apiIsBigResultFalseTest() throws Exception {
		initApi("/TaskDemo.xml");
		Assert.assertFalse(apiManager.isBigResult("taobao.topats.trades.price.update"));
	}
	
	@Test
	public void apiIsBigAttributesTest() throws Exception {
		Map<String, String> attributes = new HashMap<String, String>();
		Assert.assertFalse(apiManager.isBigResult(attributes));
		attributes.put(ModelKeyConstants.IS_BIG_RESULT, "abc");
		Assert.assertFalse(apiManager.isBigResult(attributes));
		attributes.put(ModelKeyConstants.IS_BIG_RESULT, ModelKeyConstants.BOOLEAN_ATTRIBUTE_MARK);
		Assert.assertTrue(apiManager.isBigResult(attributes));
	}
	
	@Test
	public void isPageNeededTest() throws Exception {
		initApi("/TaskDemoWithPriorityAndRange.xml");
		Assert.assertTrue(apiManager.isPageNeeded("taobao.topats.trades.price.update"));
		
		apiManager.getApis().get("taobao.topats.trades.price.update").getParamsRule().getParamRange().setEnablePage(false);
		Assert.assertFalse(apiManager.isPageNeeded("taobao.topats.trades.price.update"));
	}
	
	@Test (expected=AtsException.class)
	public void isPageNeededApiNotExistTest() throws Exception {
		initApi("/TaskDemoWithPriorityAndRange.xml");
		apiManager.isPageNeeded("taobao.api.unknown");
	}
	
	@Test
	public void isPageNeededApiWithoutRangeTest() throws Exception {
		initApi("/TaskDemo.xml");
		Assert.assertFalse(apiManager.isPageNeeded("taobao.topats.trades.price.update"));
	}
	
	@Test
	public void refreshApiModelsTest() throws Exception {
		List<String> paths = new ArrayList<String>();
		paths.add("spring-api-test.xml");
		ApplicationContext ctx = new ClassPathXmlApplicationContext(paths.toArray(new String[0]));
		TfsManager tfsManager = (TfsManager) ctx.getBean("tfsManager");
		SamService samService = (SamService) ctx.getBean("samService");
		apiManager.setTfsManager(tfsManager);
		apiManager.setSamService(samService);
		apiManager.setModelFileKey("ats");
		apiManager.setTimApiType(1L);
		
		System.out.println(apiManager.refreshApiModels());
		
	}
	
	private Map<String, String> getRightRequestTaskMap() {
		Map<String, String> requestMap = new HashMap<String, String>();
		
		//mockTip传过来的系统参数
		requestMap.put(KeyConstants.METHOD, "taobao.topats.trades.price.update");
		requestMap.put(KeyConstants.API_KEY, "123456");
		requestMap.put(KeyConstants.APP_KEY, " ");
		requestMap.put(KeyConstants.APP_IP, "10.10.10.10");
		requestMap.put(KeyConstants.ENDUSER_IP, "20.20.20.20");
		requestMap.put(KeyConstants.SESSION_NICK, "nick");
		requestMap.put(KeyConstants.SESSION_UID, "999999");
		requestMap.put(KeyConstants.TIMESTAMP, "2012-12-12 23:59:59");
		requestMap.put(KeyConstants.TOP_BIND_NICK, "bind_nick");
		requestMap.put(KeyConstants.TOP_ISV_ID, "888888");
		requestMap.put(KeyConstants.TOP_TAG, "8");
		requestMap.put(KeyConstants.FORMAT, KeyConstants.FORMAT_JSON);
		
		//mock业务参数
		requestMap.put("tids", "1111;2222;3333");
		requestMap.put("oids", "123,124;223,224,225;323");
		requestMap.put("adjust_fees", "10,12;1.2,0.33,-0.56;-3");
		requestMap.put("logistics_fees", "10;;");
		requestMap.put("origin_fees", "20,22;10,12,60;80");
		requestMap.put("nick", "testnick");
		requestMap.put("cert_sign", "abcdefghijklmn");
		
		return requestMap;
	}
	
	private Map<String, String> getRightRequestTaskMapWithDateRangeParams() {
		Map<String, String> requestMap = getRightRequestTaskMap();
		requestMap.put("start_created", "2010-11-10 00:00:00");
		requestMap.put("end_created", "2010-11-28 20:00:00");
		
		return requestMap;
	}
	
	private Map<String, String> getRightRequestTaskMapWithNumberRangeParams() {
		Map<String, String> requestMap = getRightRequestTaskMap();
		requestMap.put("start_created", "5");
		requestMap.put("end_created", "1605");
		
		return requestMap;
	}
	
	private Map<String, String> getRightRequestSubTaskMap() {
		Map<String, String> requestMap = new HashMap<String, String>();
		
		//mockTip传过来的系统参数
		requestMap.put(ModelKeyConstants.SERVICE_METHOD, "doTradePriceUpdate");
		requestMap.put(ModelKeyConstants.SERVICE_INTERFACE, "com.taobao.api.trade.TradeService");
		requestMap.put(ModelKeyConstants.SERVICE_TYPE, "hsf");
		requestMap.put(ModelKeyConstants.SERVICE_VERSION, "1.0.0");
		requestMap.put(KeyConstants.API_KEY, "123456");
		requestMap.put(KeyConstants.APP_KEY, "654321");
		requestMap.put(KeyConstants.APP_IP, "10.10.10.10");
		requestMap.put(KeyConstants.ENDUSER_IP, "20.20.20.20");
		requestMap.put(KeyConstants.SESSION_NICK, "nick");
		requestMap.put(KeyConstants.SESSION_UID, "999999");
		requestMap.put(KeyConstants.TIMESTAMP, "2012-12-12 23:59:59");
		requestMap.put(KeyConstants.TOP_BIND_NICK, "bind_nick");
		requestMap.put(KeyConstants.TOP_ISV_ID, "888888");
		requestMap.put(KeyConstants.TOP_TAG, "8");
		requestMap.put(KeyConstants.FORMAT, KeyConstants.FORMAT_JSON);
		
		//mock业务参数
		requestMap.put("tid", "1111");
		requestMap.put("oids", "123,124");
		requestMap.put("adjust_fees", "10,12");
		requestMap.put("logistics_fee", "10");
		requestMap.put("origin_fees", "20,22");
		requestMap.put("nick", "testnick");
		
		return requestMap;
	}
	
	private Map<String, String> getWrongMethodRequestSubTaskMap() {
		Map<String, String> output = getRightRequestTaskMap();
		output.put(KeyConstants.METHOD, "taobao.topats.api");
		return output;
	}
	
	private Map<String, String> getRightRequestTaskWithOneSubtaskMap() {
		Map<String, String> output = getRightRequestTaskMap();
		//mock业务参数
		output.put("tid", "1111");
		output.put("oids", "123,124");
		output.put("adjust_fees", "10,12");
		output.put("logistics_fee", "10");
		output.put("origin_fees", "20,22");
		output.put("nick", "testnick");
		output.put("cert_sign", "abcdefghijklmn");
		return output;
	}
	
	private Map<String, String> getSubtaskRequestMapWithFormatJson() {
		Map<String, String> output = getRightRequestTaskMap();
		//mock业务参数
		output.put("format", "json");
		return output;
	}
	
	private Map<String, String> getSubtaskRequestMapWithFormatXml() {
		Map<String, String> output = getRightRequestTaskMap();
		//mock业务参数
		output.put("format", "xml");
		return output;
	}
	
	private void initApi(String path) throws Exception {
		List<Resource> paths = new ArrayList<Resource>();
		paths.add(new UrlResource(this.getClass().getResource(path)));

		// 初始化APIs
		Map<String, ApiDO> apis = new HashMap<String, ApiDO>();

		for (Resource resource : paths) {
			ApiDO api = TaskParser.parseTaskTemplate(resource.getInputStream());

			// 这个情况不可能发生，放在这里保险
			if (null == api) {
				throw new XmlException("配置的任务模板的路径解析不到正确的模板");
			}

			// 将非空的模板API加入MAP中
			apis.put(api.getTaskName(), api);
		}
		
		apiManager.setApis(apis);
	}
}
