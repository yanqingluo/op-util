package com.taobao.top.notify;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.taobao.top.notify.domain.query.NotifyQuery;
import com.taobao.top.notify.service.NotifyService;

public class PreloadServlet extends HttpServlet {

	private static final long serialVersionUID = -2257578810294426067L;

	protected void doGet(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		NotifyService service = (NotifyService) context.getBean("notifyService");
		NotifyQuery query = new NotifyQuery();
		query.setAppKey("4272");
		query.setUserId(22670458L);
		try {
			service.queryNotifys(query);
			rsp.getWriter().write(Boolean.TRUE.toString());
		} catch (Exception e) {
			rsp.getWriter().write(Boolean.FALSE.toString());
		}
	}

}
