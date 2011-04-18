package com.taobao.top.notify.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThreadPoolProperty {

	public static final String EXECUTOR_POOL_CORE_SIZE = "core.size";
	
	public static final String EXECUTOR_POOL_MAX_SIZE = "max.size";
	
	public static final String EXECUTOR_POOL_ALIVE_TIME = "alive.time";
	
	public static final String EXECUTOR_POOL_QUEUE_SIZE = "queue.size";
	
	private static final Log log = LogFactory.getLog(ThreadPoolProperty.class);
	
	private static final String PROPERTIES_FILE_PATH = "thread_pool.properties";
	
	private static Properties properties;
	
	static{
		properties = new Properties();
		try {
			properties.load((new FileInputStream(new File(ThreadPoolProperty.class.getClassLoader().getResource(PROPERTIES_FILE_PATH).getPath()))));
		} catch (Exception e) {
			log.error("配置文件加载失败", e);
		}
	}
	
	public static String getProperty(String key){
		return properties.getProperty(key);
	}
	
	public static int getIntProperty(String key){
		return Integer.valueOf(getProperty(key));
	}

}
