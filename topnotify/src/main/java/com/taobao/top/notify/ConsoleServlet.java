package com.taobao.top.notify;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.taobao.top.console.client.handler.ConsoleClientHandler;

/**
 * 控制台管理。
 * 
 * @author moling
 * @since 1.0, 2010-8-26
 */
public class ConsoleServlet extends HttpServlet {

	private static final long serialVersionUID = 2144700696422474161L;

	protected void doGet(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		doPost(req, rsp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		ConsoleClientHandler cch = (ConsoleClientHandler) ac.getBean("consoleClientHandler");
		cch.handleRequest(req, rsp);
	}
	
}
