package monitor;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Test;

import com.taobao.top.ats.domain.LogDO;
import com.taobao.top.ats.monitor.TaskLog;
import com.taobao.top.ats.util.StringKit;

/**
 * 
 * @author moling
 * @since 1.0, 2010-11-17
 */
public class TaskLogTest {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Test
	public void toStringWithAllMsg() throws JSONException {
		TaskLog log = new TaskLog();
		LogDO logDO = getLogDOWithAllMsg();
		log.setLogDO(logDO);
		log.setLocalIp("11.22.33.44");
		Assert.assertEquals("taobao.test.method,appKey,222,212121,121212,7,6,5,11.22.33.44", log.toString());
		Assert.assertEquals("task", log.getClassName());
		Assert.assertEquals(sdf.format(new Date()), sdf.format(log.getLogTime()));
	}
	
	@Test
	public void toStringWithPartMsg() throws JSONException {
		TaskLog log = new TaskLog();
		LogDO logDO = getLogDOWithAllMsg();
		logDO.setSubTaskTime(null);
		log.setLogDO(logDO);
		log.setLocalIp("11.22.33.44");
		Assert.assertEquals("taobao.test.method,appKey,222,212121,,7,6,5,11.22.33.44", log.toString());
		Assert.assertEquals("task", log.getClassName());
		Assert.assertEquals(sdf.format(new Date()), sdf.format(log.getLogTime()));
	}
	
	@Test
	public void append() throws JSONException {
		Assert.assertEquals(null, StringKit.append(null, "ab", true));
		Assert.assertEquals("ab", StringKit.append(new StringBuffer(), "ab", false).toString());
		Assert.assertEquals("ab,", StringKit.append(new StringBuffer(), "ab", true).toString());
		Assert.assertEquals("", StringKit.append(new StringBuffer(), null, false).toString());
		Assert.assertEquals(",", StringKit.append(new StringBuffer(), null, true).toString());
	}
	
	public static LogDO getLogDOWithAllMsg() {
		LogDO logDO = new LogDO();
		logDO.setAppkey("appKey");
		logDO.setCostTime(12345L);
		logDO.setErrorCode("errorCode");
		logDO.setErrorCount(5);
		logDO.setMethod("taobao.test.method");
		logDO.setOneErrorCount(6);
		logDO.setSubTaskId(111L);
		logDO.setSubTaskTime(121212L);
		logDO.setSuccessCount(7);
		logDO.setTaskId(222L);
		logDO.setTaskTime(212121L);
		
		return logDO;
	}
}
