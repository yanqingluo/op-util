/**
 * 
 */
package com.taobao.top.sm;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

/**
 * @author alin mailto:xalinx@gmail.com
 * @date Jan 6, 2010
 */
public class SessionTest {

	/**
	 * 测试是否溢出
	 * 
	 * 2147483647.0 / 1000 / 3600 / 24 =  24.855134803240741
	 * 
	 * Test method for {@link com.taobao.top.sm.Session#getValidThru()}.
	 */
	@Test
	public void testGetValidThru() {
		Session session = new Session();
		Calendar calStart = Calendar.getInstance();
		calStart.set(1999, 9, 1, 9, 9, 9);
		Calendar calEnd = Calendar.getInstance();
		calEnd.set(1999, 9, 26, 9, 9, 9);
		session.setValidFrom(calStart.getTime());
		session.setValidEnd(calEnd.getTime());
		long validThru = session.getValidThru();
		assertEquals((26 - 1) * 1000 * 24 * 3600L, validThru);
	}

}
