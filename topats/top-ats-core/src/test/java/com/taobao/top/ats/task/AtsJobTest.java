package com.taobao.top.ats.task;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.taobao.top.ats.AtsTestBase;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.SubAtsTaskDO;
import com.taobao.top.ats.util.ModelKeyConstants;

public class AtsJobTest extends AtsTestBase {

	private AtsJob atsJob = (AtsJob) ctx.getBean("atsJob");

	@Test
	public void runBigJob() {
		AtsTaskDO atsTask = atsJob.getAtsTask();
		List<SubAtsTaskDO> subTasks = new ArrayList<SubAtsTaskDO>();
		SubAtsTaskDO subTask = new SubAtsTaskDO();
		subTask.setStatus(0);
		subTask.setRequest("{\"tid\":\"24007427010707\",\"fields\":\"tid,seller_nick,buyer_nick,orders\"}");
		subTasks.add(subTask);
		atsTask.setSubTasks(subTasks);
		atsTask.addAttributes(ModelKeyConstants.IS_BIG_RESULT, Boolean.TRUE.toString());
		atsJob.run();
	}

	@Test
	public void getApiRootXmlTag() {
		String tag = atsJob.getApiRootTag("taobao.trades.sold.get");
		System.out.println(tag);
	}

}
