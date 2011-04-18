package url;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.common.dao.persistence.exception.DAOException;
import com.taobao.top.ats.dao.TaskDao;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.url.UrlGenerator;
import com.taobao.top.ats.util.TokenUtil;

public class UrlGeneratorTest {

	protected static UrlGenerator urlGenerator;
	protected static TaskDao taskDao;
	private AtsTaskDO _task;
	
	static {
		List<String> paths = new ArrayList<String>();
		paths.add("spring-config-test.xml");
		paths.add("spring-engine-test.xml");
		paths.add("spring-persist-test.xml");
		ApplicationContext ctx = new ClassPathXmlApplicationContext(paths.toArray(new String[0]));
		urlGenerator = (UrlGenerator) ctx.getBean("urlGenerator");
		taskDao = (TaskDao) ctx.getBean("taskDao");
	}
	
	@Test
	public void testAtsDownloadUrl() throws Exception {
		addParentTask();
		
		String atsDownloadUrl = urlGenerator.getAtsDownloadUrl(_task);
		System.out.println(atsDownloadUrl);
		Assert.assertNotNull(atsDownloadUrl);
	}
	
	private void addParentTask() throws DAOException {
		AtsTaskDO task = new AtsTaskDO();
		task.setApiName("taobao.toptas.delivery.send");
		task.setAppKey("1000120");
		task.setStatus(0);
		task.setRetries(0);
		task.setPriority(0);
		task.setGmtCreated(new Date());
		task.addAttributes("token", "123");
		_task = taskDao.addParentTask(task);
	}
	
	@Test
	public void testSign() throws Exception {
		TreeMap<String, String> tmap = new TreeMap<String, String>();
		tmap.put("token", "123");
		tmap.put("appkey", "123");
		tmap.put("timestamp", "1289892736700L");
		Assert.assertEquals("36B3A7E5A530CC30D54FE32A57A159AF",TokenUtil.sign("110", tmap));
	}
	
}
