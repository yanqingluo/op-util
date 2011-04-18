/**
 * 
 */
package com.taobao.top.common.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author xalinx at gmail dot com
 * @date Jun 8, 2010
 */
public class DateKitNewTest {
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
	 * {@link com.taobao.top.common.server.DateKitNew#ymdOrYmdhms2Date(java.lang.String)}
	 * .
	 */
	@Test
	public void testYmdOrYmdhms2Date() {
		Date expected = new GregorianCalendar(2008, 0, 11, 23, 23, 12)
				.getTime();
		
		try {
			assertEquals(expected, DateKitNew
					.ymdOrYmdhms2Date("2008-01-11 23:23:12.000"));
		} catch (ParseException e) {
			fail();
		}
		try {
			assertEquals(expected, DateKitNew
					.ymdOrYmdhms2Date("2008-01-11 23:23:12"));
		} catch (ParseException e) {
			fail();
		}
		try {
			assertFalse(expected.equals(DateKitNew
					.ymdOrYmdhms2Date("2008-01-11 23:23:12.010")));
		} catch (ParseException e) {
			fail();
		}
		try {
			assertFalse(expected.equals(DateKitNew
					.ymdOrYmdhms2Date("2008-01-11 23:23:12010")));
			fail();
		} catch (ParseException e) {
		}
	}
	
	@Test
	public void testYmd2Date() {
		Date expected2 = new GregorianCalendar(2008, 0, 11).getTime();
		try {
			assertEquals(expected2.getTime(), DateKitNew.ymdOrYmdhms2Date("2008-01-11").getTime());
		} catch (ParseException e) {
			fail();
		}
	}

	@Test
	public void testDate2String() throws ParseException {
		String str = "2008-01-11 23:23:12";
		Date date = DateKitNew.ymdOrYmdhms2Date(str);
		assertEquals(str, DateKitNew.date2ymdhms(date));
	}
	
	@Test
	public void testIsInvalidTimestamp() {
		Date time = new GregorianCalendar(2010, 5, 9, 10, 10, 10).getTime();
		assertTrue(DateKitNew.isTimestampInvalid("2010-06-09 09-10-10", time, 60* 60* 1000L));
		assertFalse(DateKitNew.isTimestampInvalid("2010-06-09 09-10-09", time, 60* 60* 1000L));
		assertTrue(DateKitNew.isTimestampInvalid("2010-06-09 11-10-10", time, 60* 60* 1000L));
		assertFalse(DateKitNew.isTimestampInvalid("2010-06-09 11-10-11", time, 60* 60* 1000L));
	}

}
