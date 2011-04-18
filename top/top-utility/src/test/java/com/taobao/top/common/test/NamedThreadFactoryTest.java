package com.taobao.top.common.test;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.taobao.top.common.NamedThreadFactory;

/**
 * 
 * @author zhenzi
 *
 */
public class NamedThreadFactoryTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void test_默认构造函数(){
		NamedThreadFactory threadFactory = new NamedThreadFactory();
		Thread thread = threadFactory.newThread(new Runnable(){
			@Override
			public void run() {
				try{
					Thread.sleep(10 * 60 * 1000 );
				}catch(Exception e){
					
				}
			}
			
		});
		assertTrue(thread.isDaemon() == false);
		assertTrue(thread.getName().indexOf("pool") != -1);
		thread.interrupt();
	}
}
