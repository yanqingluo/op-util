package com.taobao.top.notify.util;

import org.junit.Test;

public class IdCreatorTest {

	@Test
	public void testCreateId() throws InterruptedException {
		for (int i = 0; i < 1000; i++) {
			long id = IdCreator.createId(1<<20);
			 System.out.println(id);
//			Thread.sleep(2);
//			Thread.sleep(15);
			// System.out.println(Long.toBinaryString(time));
			// System.out.println(Long.toBinaryString(id));
		}
	}

}
