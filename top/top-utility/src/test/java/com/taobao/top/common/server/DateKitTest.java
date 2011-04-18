/**
 * 
 */
package com.taobao.top.common.server;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @version 2008-12-24
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class DateKitTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.taobao.top.common.server.DateKit#ymdOrYmdhms2Date(java.lang.String)}
	 * .
	 */
	@Test
	public void testYmdOrYmdhms2Date() {
		Date expected = new GregorianCalendar(2008, 0, 11, 12, 23, 12)
				.getTime();
		Date expected2 = new GregorianCalendar(2008, 0, 11)
		.getTime();
		try {
			assertEquals(expected, DateKit
					.ymdOrYmdhms2Date("2008-01-11 12:23:12.000"));
		} catch (ParseException e) {
			fail();
		}
		try {
			assertEquals(expected, DateKit
					.ymdOrYmdhms2Date("2008-01-11 12:23:12"));
		} catch (ParseException e) {
			fail();
		}
		try {
			assertEquals(expected2, DateKit
					.ymdOrYmdhms2Date("2008-01-11"));
		} catch (ParseException e) {
			fail();
		}
		try {
			assertFalse(expected.equals(DateKit
					.ymdOrYmdhms2Date("2008-01-11 12:23:12.010")));
		} catch (ParseException e) {
			fail();
		}
		try {
			assertFalse(expected.equals(DateKit
					.ymdOrYmdhms2Date("2008-01-11 12:23:12 010")));
			fail();
		} catch (ParseException e) {
		}
	}

}
