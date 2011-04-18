package com.taobao.top.timwrapper;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.monitor.alert.client.AlertAgent;

public class SamServiceInterceptor implements MethodInterceptor {

	public static final String TIM_HSF_ERROR = "TIM_HSF_ERROR";
	private AlertAgent alertAgent;
	private transient static Log log = LogFactory
	.getLog(SamServiceInterceptor.class);
	
	public SamServiceInterceptor() {
		if (log.isDebugEnabled()) {
			log.debug("SamServiceInterceptor constructed");
		}
	}
	
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		if (log.isDebugEnabled()) {
			log.debug("Method invoking: " + methodInvocation.getMethod().getName());
		}
		try {
			return methodInvocation.proceed();
		} catch (com.taobao.hsf.exception.HSFException e) {
			alert(methodInvocation, e);
			throw e;
		}
	}

	void alert(MethodInvocation methodInvocation,
			com.taobao.hsf.exception.HSFException e) {
		try {
			if (alertAgent != null) { // just in case the setting failed.
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("Operation_");
				stringBuilder.append(methodInvocation.getMethod().getName());
				stringBuilder.append("__");
				stringBuilder.append("__Error_");
				stringBuilder.append(e.getMessage());
				alertAgent.alertWithAutoDismiss(TIM_HSF_ERROR,
						stringBuilder.toString());
			}
		} catch (Throwable t) {
			// Do nothing.
		}
	}

	public AlertAgent getAlertAgent() {
		return alertAgent;
	}

	public void setAlertAgent(AlertAgent alertAgent) {
		this.alertAgent = alertAgent;
	}

}
