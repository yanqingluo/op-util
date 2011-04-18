package com.taobao.top.core;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.core.framework.TopPipeInput;

public class DebugController {


	private final transient Log ispErrLog = LogFactory
			.getLog("com.taobao.top.ispError");

	// auto reset counter，this counter will reset everyday.
	private final long TIME_OF_ONEDAY = 24L * 60L * 60L * 1000L;
	private AutoResetCounter counter;

	// the configuration that will be pushed by top console.
	private int logLevel = 0;
	private int maxLogCounter = 30000;

	public DebugController() {
		super();
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		counter = new AutoResetCounter(TIME_OF_ONEDAY, calendar
				.getTimeInMillis());
	}

	public int getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	public int getMaxLogCounter() {
		return maxLogCounter;
	}

	public void setMaxLogCounter(int maxLogCounter) {
		this.maxLogCounter = maxLogCounter;
	}

	public void saveIspErrorLog(TopPipeInput pipeInput, Exception e) {
		// 如果日志等级非1，或者2,不打日志
		if (logLevel != 1 && logLevel != 2) {
			return;
		}
		// 如果今天的日志量已经超过最大日志数，不打日志
		if (counter.incrementAndGet() > maxLogCounter) {
			return;
		}
		ispErrLog.error("the app key:" + pipeInput.getAppKey());
		ispErrLog.error("the error is:" + e.getMessage(), e);

		ispErrLog.error("the api name is:" + pipeInput.getApiName());

		if (logLevel == 2) {
			ispErrLog.error("the parameters is:" + pipeInput.getParamStr());
		}
		ispErrLog.error("error message end");
	}

}
