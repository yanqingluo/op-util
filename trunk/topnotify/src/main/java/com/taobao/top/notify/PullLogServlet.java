package com.taobao.top.notify;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.taobao.top.console.client.analysis.SimpleLogPullHandler;


/**
 * 
 * @author zongping
 * 
 */
public class PullLogServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -2255773372654240725L;


    protected void doGet(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
        doPost(req, rsp);
    }


    protected void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        SimpleLogPullHandler cch = (SimpleLogPullHandler) ac.getBean("simpleLogPullHandler");
        cch.handleRequest(req, rsp);
    }

}
