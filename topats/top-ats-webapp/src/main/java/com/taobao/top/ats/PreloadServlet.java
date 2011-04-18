package com.taobao.top.ats;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PreloadServlet extends HttpServlet {

	private static final long serialVersionUID = -1711555252167496194L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.getOutputStream().println("true");
	}

}
