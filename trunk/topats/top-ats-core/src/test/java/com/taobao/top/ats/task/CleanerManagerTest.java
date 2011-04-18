package com.taobao.top.ats.task;

import org.junit.Test;

import com.taobao.top.ats.AtsTestBase;
import com.taobao.top.ats.task.cleaner.CleanerManager;

public class CleanerManagerTest extends AtsTestBase {

	private static CleanerManager cleanerManager = (CleanerManager) ctx.getBean("cleanerManager");

	@Test
	public void testCleanLocalFile() {
		cleanerManager.cleanLocalFile();
	}
	@Test
	public void testCleanSharedData() {
		cleanerManager.cleanSharedData();
	}
	@Test
	public void isCleanerAvailable() {
		System.out.println(cleanerManager.isCleanerAvailable());
		System.out.println(cleanerManager.isCleanerAvailable());
	}
}
