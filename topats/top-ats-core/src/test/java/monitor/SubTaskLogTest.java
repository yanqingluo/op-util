package monitor;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Test;

import com.taobao.top.ats.domain.LogDO;
import com.taobao.top.ats.monitor.SubTaskLog;

/**
 * 
 * @author moling
 * @since 1.0, 2010-11-17
 */
public class SubTaskLogTest {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Test
	public void toStringWithAllMsg() throws JSONException {
		SubTaskLog log = new SubTaskLog();
		LogDO logDO = TaskLogTest.getLogDOWithAllMsg();
		log.setLogDO(logDO);
		log.setSuccess(false);
		Assert.assertEquals("taobao.test.method,appKey,12345,111,errorCode,false", log.toString());
		Assert.assertEquals("subTask", log.getClassName());
		Assert.assertEquals(sdf.format(new Date()), sdf.format(log.getLogTime()));
	}
	
	@Test
	public void toStringWithPartMsg() throws JSONException {
		SubTaskLog log = new SubTaskLog();
		LogDO logDO = TaskLogTest.getLogDOWithAllMsg();
		logDO.setErrorCode(null);
		log.setLogDO(logDO);
		log.setSuccess(true);
		Assert.assertEquals("taobao.test.method,appKey,12345,111,,true", log.toString());
		Assert.assertEquals("subTask", log.getClassName());
		Assert.assertEquals(sdf.format(new Date()), sdf.format(log.getLogTime()));
	}
}
