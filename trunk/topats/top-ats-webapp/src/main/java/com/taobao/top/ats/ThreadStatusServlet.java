package com.taobao.top.ats;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.taobao.top.xbox.threadpool.JobDispatcher;

/**
 * 线程状态监控线程。
 * 
 * @author carver.gu
 * @since 1.0, Dec 15, 2010
 */
public class ThreadStatusServlet extends HttpServlet {

	private static final long serialVersionUID = 248670402811238951L;

	public void doGet(HttpServletRequest req, HttpServletResponse rsp) {
		ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		JobDispatcher jd = (JobDispatcher) ac.getBean("jobDispatcher");
		try {
			rsp.getWriter().write(jd.getCurrentThreadStatus().toString() + "\r\n");
		} catch (IOException e) {
		}
	}

}
