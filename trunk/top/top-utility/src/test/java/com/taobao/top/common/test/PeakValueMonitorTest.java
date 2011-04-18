package com.taobao.top.common.test;


import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.taobao.top.common.PeakValueMonitor;

/**
 * 
 * @author zhenzi
 *
 */
public class PeakValueMonitorTest {
	private PeakValueMonitor peakValue = null;
	@Before
	public void setUp() throws Exception {
		peakValue = new PeakValueMonitor();
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testChangeStatus()throws Exception{
		
		Class clazz = peakValue.getClass();
		Field field = clazz.getDeclaredField("backThread");
		field.setAccessible(true);
		{
			Thread backThread  = (Thread)field.get(peakValue);
			//changeStatus之前后台线程应该是死掉的。
			assertNull(backThread);
		}
		//第一次changeStatus-true后台线程起来
		{
			peakValue.changeStatus(true);
			Thread backThread  = (Thread)field.get(peakValue);
			assertNotNull(backThread);
			assertTrue(backThread.isAlive());
		}
		//changeStatus-false后台线程停止运行
		{
			peakValue.changeStatus(false);
			Thread backThread  = (Thread)field.get(peakValue);
			Thread.sleep(1000);
			assertNotNull(backThread);
			assertFalse(backThread.isAlive());
		}
	}
	
}	
