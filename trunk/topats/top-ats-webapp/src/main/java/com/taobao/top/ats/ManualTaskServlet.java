package com.taobao.top.ats;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.taobao.top.ats.task.TaskManager;

public class ManualTaskServlet extends HttpServlet {

	private static final long serialVersionUID = 3418001648286859884L;

	private static final Log log = LogFactory.getLog(ManualTaskServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		TaskManager taskManager = (TaskManager) context.getBean("taskManager");
		try {
			Object taskId = req.getParameter("taskId");
			if(null == taskId){
				resp.getWriter().println("参数中没有Task ID，此操作无法执行");
				return;
			}
			taskManager.executeManualAtsTask(Long.valueOf(taskId.toString()));
		} catch (AtsException e) {
			log.error("手动执行任务失败", e);
			resp.getWriter().println("手动执行任务失败");
		}
	}

}
