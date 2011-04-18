package com.taobao.top.ats;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.taobao.top.ats.task.api.ApiManager;

/**
 * 
 * @author moling
 * @since 1.0, 2010-11-22
 */
public class RefreshApiServlet extends HttpServlet {

	private static final long serialVersionUID = -6317277982637858458L;
	private static final Log log = LogFactory.getLog(RefreshApiServlet.class);

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		ApiManager apiManager = (ApiManager) context.getBean("apiManager");
		try {
			String operator = req.getParameter("operator");
			if (StringUtils.isNotBlank(operator)) {
				boolean isOk = apiManager.refreshApiModels();
				if (isOk) {
					if (log.isWarnEnabled()) {
						log.warn(operator + "刷新API缓存成功");
					}
				} else {
					if (log.isWarnEnabled()) {
						log.warn(operator + "刷新API缓存失败");
					}
				}
				resp.getWriter().println(operator + "手动刷新缓存结果：" + isOk);
			} else {
				if (log.isWarnEnabled()) {
					log.warn("刷新API缓存失败，没有输入花名");
				}
				resp.getWriter().println("手动刷新缓存必需输入操作者花名！");
			}
		} catch (Exception e) {
			log.error("手动执行任务失败", e);
			resp.getWriter().println("手动刷新缓存异常");
		}
	}
}
