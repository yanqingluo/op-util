package com.taobao.top.pageapi.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.session.TaobaoSession;
import com.taobao.top.pageapi.core.impl.CookieInvalideException;
/**
 * 验证cookie 是否有效
 * 
 * @version 2009-08-16
 * @author <a href="mailto:yueqian@taobao.com">yueqian</a>
 * 
 */
public class TopCookieManager {
	private static final Log log = LogFactory
			.getLog(TopCookieManager.class);
	
	private static final String SESSION_ATTRIBUTE_TOPLOGIN = "topLogin";
	/**
	 * 用于在session中保存
	 */
	public static final String SESSION_ATTRIBUTE_LAST_VISIT_COOKIE = "lastVisitCookie";	
	public static final String SESSION_ATTRIBUTE_NICK = "_nk_";
	
	public static final String SESSION_ATTRIBUTE_TOP_APP_AUTH = "topAPPAuth";
	
	public static void refreshCookies(HttpServletRequest request,
			HttpServletResponse response) {
		TaobaoSession session = (TaobaoSession)(request.getSession());
		response.setHeader("P3P","CP=CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR");
		session.setAttribute(SESSION_ATTRIBUTE_LAST_VISIT_COOKIE, Long.toString(System.currentTimeMillis() / 1000));
	}
		
	public static String validateCookies(HttpServletRequest request) throws CookieInvalideException {
		String nick = null;
		TaobaoSession session = (TaobaoSession)(request.getSession());
		String login = (String) session
				.getAttribute(SESSION_ATTRIBUTE_TOPLOGIN);
		if (log.isTraceEnabled()) {
			log.trace("TOPLogin is " + login);
		}
		if (login != null && "true".equals(login)) {
			String lastTime = (String) session
					.getAttribute(SESSION_ATTRIBUTE_LAST_VISIT_COOKIE);
			int lastVisitTime = 0;
			if (log.isTraceEnabled()) {
				log.trace("TOPLogin lastTime is " + lastTime);
			}
			if (lastTime != null) {
				try {
					lastVisitTime = Integer.parseInt(lastTime);
				} catch (NumberFormatException e1) {
					lastVisitTime = 0;
				}
			}
			if (Math.abs((System.currentTimeMillis() / 1000) - lastVisitTime) >= 3600) {
				session.invalidate();
				throw new CookieInvalideException ("Cookie Time Out");
			} else {
				if (log.isTraceEnabled()) {
					log.trace("TOP cookie is right");
				}
			}
			nick = (String) session
						.getAttribute(SESSION_ATTRIBUTE_NICK);
		} else  throw new CookieInvalideException ("No TOP Cookie exist!");
		 return nick;
	}
	
	public static String getSessionNick(HttpServletRequest request) throws CookieInvalideException {
		String nick = null;
		TaobaoSession session = (TaobaoSession)(request.getSession());
		String login = (String) session
				.getAttribute(SESSION_ATTRIBUTE_TOPLOGIN);
		if (log.isTraceEnabled()) {
			log.trace("TOPLogin is " + login);
		}
		if (login != null && "true".equals(login)) {
			String lastTime = (String) session
					.getAttribute(SESSION_ATTRIBUTE_LAST_VISIT_COOKIE);
			int lastVisitTime = 0;
			if (log.isTraceEnabled()) {
				log.trace("TOPLogin lastTime is " + lastTime);
			}
			if (lastTime != null) {
				try {
					lastVisitTime = Integer.parseInt(lastTime);
				} catch (NumberFormatException e1) {
					lastVisitTime = 0;
				}
			}
			if (Math.abs((System.currentTimeMillis() / 1000) - lastVisitTime) >= 3600) {
				session.invalidate();
				throw new CookieInvalideException ("Cookie Time Out");
			} else {
				if (log.isTraceEnabled()) {
					log.trace("TOP cookie is right");
				}
			}
			nick = (String) session
						.getAttribute(SESSION_ATTRIBUTE_NICK);
		} else  return null;
			//throw new CookieInvalideException ("No TOP Cookie exist!");
		 return nick;
	}

	
	public static void validateTOPAuthCookie(HttpServletRequest request)throws CookieInvalideException {
		TaobaoSession session = (TaobaoSession)(request.getSession());
		String nick = (String) session
				.getAttribute(SESSION_ATTRIBUTE_NICK);
		String topAPPAuth = (String) session
		.getAttribute(SESSION_ATTRIBUTE_TOP_APP_AUTH);
		if ( nick != null &&topAPPAuth!= null && topAPPAuth.endsWith(nick) ){
			if (log.isTraceEnabled()) {
				log.trace("TOP cookie is right");
			}
		}else {
			throw new CookieInvalideException("TOPAuth Cookie validation failed");
		}
	}

	public static void setTOPAuthCookie(String appkey,HttpServletRequest request,HttpServletResponse response) {
		TaobaoSession session = (TaobaoSession)(request.getSession());        
        String nick = (String)session.getAttribute(SESSION_ATTRIBUTE_NICK);
        StringBuilder builder = new StringBuilder();
        response.setHeader("P3P","CP=CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR");        
        session.setAttribute(SESSION_ATTRIBUTE_TOP_APP_AUTH, builder.append(appkey).append(nick));
	}
}
