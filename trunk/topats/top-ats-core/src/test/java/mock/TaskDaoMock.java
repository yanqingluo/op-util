package mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.taobao.common.dao.persistence.exception.DAOException;
import com.taobao.top.ats.dao.TaskDao;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.PageList;
import com.taobao.top.ats.domain.QueryTaskDO;
import com.taobao.top.ats.domain.SubAtsTaskDO;
import com.taobao.top.ats.util.StatusConstants;

public class TaskDaoMock implements TaskDao {
	
	public static final long MISMATCH_TASK_APP = 81;
	
	public static final long TASK_NOT_EXIST = 82;
	
	public static final long TASK_STATUS_NEW = 83;
	
	public static final long TASK_STATUS_DOING = 84;
	
	public static final long TASK_STATUS_FAIL = 85;
	
	public static final long TASK_STATUS_DONE = 86;
	
	public static final long TASK_STATUS_SENT = 87;

	public static final long SUBTASK_STATUS_FAIL = 88;
	
	public static final long SUBTASK_STATUS_DONE = 89;
	
	public static final String RESPONSE_IGNORE_ERROR = "810";
	
	public static final String RESPONSE_REPEAT_ERROR = "811";
	
	public static final String RESPONSE_UNKNOWN_ERROR = "812";
	
	public static final String DEFAULT_VERSION = "88";
	
	private String version = "88";
	
	public void setVersion(String version) {
		this.version = version;
	}

	private long count = 0;
	public AtsTaskDO addParentTask(AtsTaskDO task) {
		return getParentTask(count++);
	}

	public SubAtsTaskDO addSubTask(SubAtsTaskDO subTask) {
		// TODO Auto-generated method stub
		return null;
	}

	public AtsTaskDO getParentTask(long taskId) {
		if(TASK_NOT_EXIST == taskId){
			return null;
		}
		List<SubAtsTaskDO> subTasks = new ArrayList<SubAtsTaskDO>();
		SubAtsTaskDO subTask;
		for(int i = 0; i < 2; i++){
			subTask = new SubAtsTaskDO();
			if(SUBTASK_STATUS_FAIL == taskId){
				subTask.setStatus(StatusConstants.SUBTASK_STATUS_FAIL.getStatus());
			}else if(SUBTASK_STATUS_DONE == taskId){
				subTask.setStatus(StatusConstants.SUBTASK_STATUS_DONE.getStatus());
			}else{
				subTask.setStatus(StatusConstants.SUBTASK_STATUS_NEW.getStatus());
			}
			subTask.setRequest("{\"param\":\"param\",\"name\":\"Jerry\",\"method\":\"method\",\"interface\":\"inteface\",\"type\":\"type\",\"version\":\"" + version + "\"}");
			subTasks.add(subTask);
		}
		
		AtsTaskDO atsTask = new AtsTaskDO();
		if(MISMATCH_TASK_APP == taskId){
			atsTask.setAppKey("4272");
		}else{
			atsTask.setAppKey("Jerry");
		}
		atsTask.setSubTasks(subTasks);
		atsTask.setId(taskId);
		if(TASK_STATUS_NEW == taskId){
			atsTask.setStatus(StatusConstants.TASK_STATUS_NEW.getStatus()); 
		}else if(TASK_STATUS_DOING == taskId){
			atsTask.setStatus(StatusConstants.TASK_STATUS_DOING.getStatus());
		}else if(TASK_STATUS_FAIL == taskId){
			atsTask.setStatus(StatusConstants.TASK_STATUS_FAIL.getStatus());
		}else if(TASK_STATUS_DONE == taskId){
			atsTask.setStatus(StatusConstants.TASK_STATUS_DONE.getStatus());
		}else if(TASK_STATUS_SENT == taskId){
			atsTask.setStatus(StatusConstants.TASK_STATUS_SENT.getStatus());
		}else{
			atsTask.setStatus(StatusConstants.TASK_STATUS_NEW.getStatus()); 
		}
		atsTask.setGmtCreated(new Date());
		atsTask.setApiName("taobao.topats.trades.fullinfo.get");
		
		return atsTask;
	}

	public SubAtsTaskDO getSubTask(long subTaskId) {
		// TODO Auto-generated method stub
		return null;
	}

	public AtsTaskDO getTask(long taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	public PageList<AtsTaskDO> queryTasks(QueryTaskDO query) {
		// TODO Auto-generated method stub
		return null;
	}

	public AtsTaskDO updateParentTask(AtsTaskDO task) {
		// TODO Auto-generated method stub
		return null;
	}

	public SubAtsTaskDO updateSubTask(SubAtsTaskDO subTask) {
		// TODO Auto-generated method stub
		return null;
	}

	public int cleanParentTask(Date endCreated) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int deleteParentTask(long taskId) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int deleteSubTasks(long taskId) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Integer queryTaskCount(QueryTaskDO query) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	public PageList<AtsTaskDO> queryCleanableTasks(QueryTaskDO query)
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer queryCleanableTaskCount(QueryTaskDO query)
			throws DAOException {
		return 10;
	}
}
