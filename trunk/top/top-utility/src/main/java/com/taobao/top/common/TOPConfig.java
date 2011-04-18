package com.taobao.top.common;

import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author wenchu
 *
 */
public class TOPConfig {
	private static final Log Logger = LogFactory.getLog(TOPConfig.class);
	
	private static TOPConfig config = new TOPConfig(); 
	
	/**
	 * flush的间隔时间，单位秒
	 */
	private int flushInterval = 5;
	
	/**
	 * 创建分页的线程数
	 */
	private int createBundleServiceThreadCount = 5;
	
	/**
	 * 当记录数达到多少的时候写入外部存储
	 */
	private int bundleMaxCount = 100;
	
	/**
	 * 输出记录Bundle的工作线程池大小
	 */
	private int writerMaxCount = 50;
	
	/**
	 * 本地地址
	 */
	private String localAddress;
	
	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	public int getFlushInterval() {
		return flushInterval;
	}

	public void setFlushInterval(int flushInterval) {
		this.flushInterval = flushInterval;
	}

	public int getCreateBundleServiceThreadCount() {
		return createBundleServiceThreadCount;
	}

	public void setCreateBundleServiceThreadCount(int createBundleServiceThreadCount) {
		this.createBundleServiceThreadCount = createBundleServiceThreadCount;
	}

	public int getBundleMaxCount() {
		return bundleMaxCount;
	}

	public void setBundleMaxCount(int bundleMaxCount) {
		this.bundleMaxCount = bundleMaxCount;
	}

	public int getWriterMaxCount() {
		return writerMaxCount;
	}

	public void setWriterMaxCount(int writerMaxCount) {
		this.writerMaxCount = writerMaxCount;
	}

	private TOPConfig()
	{
		Properties prop;
		
		try
		{		
			prop = new Properties();
			
			URL url = Thread.currentThread().getContextClassLoader()
						.getResource(TOPConstants.TOP_CONFIG);
			
			if (url != null)
				prop.load(url.openStream());
			
			if (prop.get("flushInterval") != null)
				flushInterval = Integer.parseInt((String)prop.get("flushInterval"));
			
			if (prop.get("createBundleServiceThreadCount") != null)
				createBundleServiceThreadCount = 
					Integer.parseInt((String)prop.get("createBundleServiceThreadCount"));
			
			if (prop.get("bundleMaxCount") != null)
				bundleMaxCount = 
					Integer.parseInt((String)prop.get("bundleMaxCount"));
			
			if (prop.get("writerMaxCount") != null)
				writerMaxCount = 
					Integer.parseInt((String)prop.get("writerMaxCount"));
			
			localAddress = InetAddress.getLocalHost().getHostAddress().toString();
			
			if (localAddress == null || "".equals(localAddress))
				localAddress = InetAddress.getLocalHost().getHostName().toString();
		}
		catch(Exception ex)
		{
			Logger.error("Load AsynWriter error!",ex);
			throw new java.lang.RuntimeException(ex);
		}		
		
	};
	
	public static TOPConfig getInstance()
	{
		if (config == null)
			config = new TOPConfig();
			
		return config;
	}
	
}
