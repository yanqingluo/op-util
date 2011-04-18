package com.taobao.top.notify;

import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.taobao.top.notify.receive.NotifySubscriber;
import com.taobao.top.notify.util.DateUtils;
import com.taobao.top.tim.client.service.WhiteListService;

/**
 * 系统启动入口。
 * 
 * @author fengsheng
 * @since 1.0, Dec 23, 2009
 */
public class NotifyStartupListener extends ContextLoaderListener {

	private static final Log log = LogFactory.getLog(NotifyStartupListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		log.warn("Top Notify is starting up...");
		super.contextInitialized(event);

		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		NotifySubscriber ns = (NotifySubscriber) ctx.getBean("notifySubscriber");
		WhiteListService<?> wls = (WhiteListService<?>) ctx.getBean("authUserWhiteListService");

		try {
			Thread.sleep(30000); // 等待配置中心推送HSF地址、初始化缓存
			wls.syncAllWhiteList(false);
			ns.init();
		} catch (Exception e) {
			log.error("error", e);
			throw new RuntimeException(e);
		}

		log.warn("Top Notify has started up at: " + DateUtils.getCurrentDateTime());
	}

}
