package com.taobao.top.ats;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.top.ats.dao.TaskDao;
import com.taobao.top.ats.engine.ApiEngine;

public abstract class AtsTestBase {

	protected static TaskDao taskDao;
	protected static ApiEngine apiEngine;
	protected static ApplicationContext ctx;

	static {
		List<String> paths = new ArrayList<String>();
		paths.add("spring-config-test.xml");
		paths.add("spring-engine-test.xml");
		paths.add("spring-persist-test.xml");
		paths.add("spring-task-test.xml");
		ctx = new ClassPathXmlApplicationContext(paths.toArray(new String[0]));
		taskDao = (TaskDao) ctx.getBean("taskDao");
		apiEngine = (ApiEngine) ctx.getBean("apiEngine");
	}

}
