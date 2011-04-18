package task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.common.dao.persistence.exception.DAOException;
import com.taobao.top.ats.AtsException;
import com.taobao.top.ats.dao.TaskDao;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.PageList;
import com.taobao.top.ats.domain.QueryTaskDO;
import com.taobao.top.ats.task.TaskManagerImpl;
import com.taobao.top.ats.util.StatusConstants;

public class TaskManagerTest{
	
	private static TaskManagerImpl taskManager;
	protected static TaskDao taskDao;
	static{
		List<String> paths = new ArrayList<String>();
		paths.add("spring-config-test.xml");
		paths.add("spring-engine-test.xml");
		paths.add("spring-persist-test.xml");
		paths.add("spring-task-test.xml");
		ApplicationContext ctx = new ClassPathXmlApplicationContext(paths.toArray(new String[0]));
		taskManager = (TaskManagerImpl) ctx.getBean("taskManager");
		taskDao = (TaskDao) ctx.getBean("taskDao");
	}
	
	@Test
	public void testInit(){
		taskManager.init();
		System.out.println("2_task.txt".matches("\\d+_task\\.txt"));
		System.out.println("266_task.txt".equals("266_task.txt"));
		System.out.println("266_task.txt".indexOf("_task.txt"));
		System.out.printf("%s, %s\n", 0, 0);
	}
	
	@Test
	public void testCreateTask() throws AtsException{
		System.out.println(taskManager.createTask(new HashMap<String, String>()).getResult().getApiName());
	}
	
	@Test 
	public void testNotifyTask() throws AtsException{
		for(int i = 0; i < 561; i++){
			taskManager.notifyTask(null);
		}
	}
	
	@Test
	public void testStartProtectedThread() throws DAOException {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				try {
				Date now = new Date();
				QueryTaskDO query = new QueryTaskDO();
				query.setTaskStatus(StatusConstants.TASK_STATUS_NEW.getStatus());
				query.setEndCreated(DateUtils.addHours(now, -1));
				query.setStartCreated(DateUtils.addDays(now, -1));
				PageList<AtsTaskDO> newTaskResult = taskDao.queryTasks(query);
				
				query.setTaskStatus(StatusConstants.TASK_STATUS_DOING.getStatus());
				PageList<AtsTaskDO> doingTaskResult = taskDao.queryTasks(query);
				
				query.setTaskStatus(StatusConstants.TASK_STATUS_DONE.getStatus());
				PageList<AtsTaskDO> doneTaskResult = taskDao.queryTasks(query);
				
				List<AtsTaskDO> atsTasks = newTaskResult.getData();
				atsTasks.addAll(doingTaskResult.getData());
				atsTasks.addAll(doneTaskResult.getData());
				System.out.println(DateUtils.addHours(now, -1));
				System.out.println(DateUtils.addDays(now, -1));
				System.out.println(newTaskResult.getTotal());
				System.out.println(doingTaskResult.getTotal());
				System.out.println("done = " + doneTaskResult.getTotal());
				AtsTaskDO atsTaskWithSub;
				int i = 0 ;
				for(AtsTaskDO atsTask : atsTasks){
					atsTaskWithSub = taskDao.getTask(atsTask.getId());
					System.out.println("" + i++ + ", " + atsTaskWithSub.getId() + "_task.txt");
				}
				} catch (DAOException e) {
				}
			}
		}, 0, 2000);
	}
	
	@Test
	public void testExecuteManualTask() throws AtsException{
//		AtsTaskDO atsTask = taskDao.getTask(285L);
//		List<SubAtsTaskDO> subs = atsTask.getSubTasks();
//		for(SubAtsTaskDO sub : subs){
//			System.out.println(sub.getRequest());
//		}
//		Date now = new Date();
//		QueryTaskDO query = new QueryTaskDO();
//		query.setTaskStatus(StatusConstants.TASK_STATUS_NEW.getStatus());
//		query.setEndCreated(DateUtils.addHours(now, -1));
//		query.setStartCreated(DateUtils.addMonths(now, -1));
//		PageList<AtsTaskDO> newTaskResult = taskDao.queryTasks(query);
//		List<String> localFileNames = Arrays.asList(getLocalFileNames());
//		List<AtsTaskDO> atsTasks = newTaskResult.getData();
//		long taskId;
//		System.out.println(newTaskResult.getTotal());
//		for(AtsTaskDO atsTask : atsTasks){
//			taskId = atsTask.getId();
//			System.out.println(taskId + "_task.txt");
//			if(localFileNames.contains(taskId + "_task.txt")){
//				continue;
//			}
////			executeAtsTask(taskDao.getTask(taskId));
//			System.out.println(taskId);
//		}
		taskManager.executeManualAtsTask(400L);
	}
	
	public static void main(String[] args) throws DAOException {
		TaskManagerTest test = new TaskManagerTest();
		test.testStartProtectedThread();
	}

}
