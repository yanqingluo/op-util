package monitor;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.taobao.top.ats.domain.LogDO;
import com.taobao.top.ats.monitor.TaskMonitorImpl;

/**
 * 
 * @author moling
 * @since 1.0, 2010-11-17
 */
public class TaskMonitorTest {
	private static TaskMonitorImpl monitor;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		monitor = new TaskMonitorImpl();
		monitor.init();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		monitor.stop();
	}
	
	@Test
	public void logSuccessTest() throws InterruptedException {
		LogDO logDO = TaskLogTest.getLogDOWithAllMsg();
		monitor.logSuccess(logDO);
		Thread.sleep(1000);
	}
	
	@Test
	public void logErrorTest() throws InterruptedException {
		LogDO logDO = TaskLogTest.getLogDOWithAllMsg();
		monitor.logError(logDO);
		Thread.sleep(1000);
	}
	
	@Test
	public void logTaskTest() throws InterruptedException {
		LogDO logDO = TaskLogTest.getLogDOWithAllMsg();
		monitor.logTask(logDO);
		Thread.sleep(1000);
	}
}
