/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.common.lang;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.taobao.top.common.lang.StringKit;

/**
 * @version 2008-3-10
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class StringKitTest {

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
	 * {@link com.taobao.top.common.lang.StringKit#removeEach(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testRemoveEach() {
		assertEquals("bdf", StringKit.removeEach("abccdef", "ace"));
	}

	@Test
	public void testReplaceEach() {
		assertEquals("abccd_f", StringKit.replaceEach("abccdef", 'e',
				new Object[] { "_" }));
	}

	@Test
	public void testIsAlphaDigitUnderSpotAndAlphaFirst() {
		assertTrue(StringKit.isAlphaDigitUnderSpotAndAlphaFirst("abcd_3abc"));
		assertTrue(StringKit.isAlphaDigitUnderSpotAndAlphaFirst("abCD_abc."));
		assertFalse(StringKit.isAlphaDigitUnderSpotAndAlphaFirst(".abCD_abc."));
		assertFalse(StringKit.isAlphaDigitUnderSpotAndAlphaFirst("_abcd_abc."));
		assertFalse(StringKit.isAlphaDigitUnderSpotAndAlphaFirst("3abcd_abc."));
		assertFalse(StringKit.isAlphaDigitUnderSpotAndAlphaFirst(" abcd_abc"));
	}

	
	@Test
	public void testOnlyContainsAlphaDigitUnderlineDot() {
		assertTrue(StringKit.onlyContainsAlphaDigitUnderlineDot("A"));
		assertTrue(StringKit.onlyContainsAlphaDigitUnderlineDot("Z"));
		assertTrue(StringKit.onlyContainsAlphaDigitUnderlineDot("a"));
		assertTrue(StringKit.onlyContainsAlphaDigitUnderlineDot("z"));
		assertTrue(StringKit.onlyContainsAlphaDigitUnderlineDot("0"));
		assertTrue(StringKit.onlyContainsAlphaDigitUnderlineDot("9"));
		assertTrue(StringKit.onlyContainsAlphaDigitUnderlineDot("_"));
		assertTrue(StringKit.onlyContainsAlphaDigitUnderlineDot("Bb5_"));
		assertTrue(StringKit.onlyContainsAlphaDigitUnderlineDot("."));
		

		assertFalse(StringKit.onlyContainsAlphaDigitUnderlineDot(null));
		assertFalse(StringKit.onlyContainsAlphaDigitUnderlineDot(""));
		assertFalse(StringKit.onlyContainsAlphaDigitUnderlineDot("-"));
		assertFalse(StringKit.onlyContainsAlphaDigitUnderlineDot(" "));
		assertFalse(StringKit.onlyContainsAlphaDigitUnderlineDot("AZ /-"));
		
	}
	
	
	@Test
	public void testUrlEncode() {
		assertEquals("%E4%B8%AD%E6%96%87", StringKit.urlEncode("中文"));
		assertEquals("", StringKit.urlEncode(""));
	}

	@Test
	public void testReadUnicode() {
		assertEquals("类目ID", StringKit.readUnicode("\\u7c7b\\u76eeID"));
	}

	@Test
	public void testGenOneChinese() {
		System.out.println(StringKit.genRandomOneChinese());
	}

	@Test
	public void testGenChinese() {
		System.out.println(StringKit.genRandomChinese(100));
	}

	@Test
	public void testSplitByComma() {
		String[] result = StringKit.splitByComma("A,B");
		assertEquals(2, result.length);
		assertEquals("A", result[0]);
		assertEquals("B", result[1]);
		
		// Spaces are trimmed.
		result = StringKit.splitByComma(" A, B ,C ");
		assertEquals(3, result.length);
		assertEquals("A", result[0]);
		assertEquals("B", result[1]);
		assertEquals("C", result[2]);
		
		// Empty String, the last empty string is igonred.
		result = StringKit.splitByComma("A,B,");
		assertEquals(2, result.length);
		assertEquals("A", result[0]);
		assertEquals("B", result[1]);
		
		// Empty String only, ignored
		result = StringKit.splitByComma("");
		assertEquals(0, result.length);
		
		// Comma only
		result = StringKit.splitByComma(",");
		assertEquals(0, result.length);
		
		// Chinese test
		result = StringKit.splitByComma("中文,B");
		assertEquals("中文", result[0]);
		assertEquals("B", result[1]);
		
	}
	
	@Test
	public void testConcatenate() {
		assertEquals("ABC", StringKit.concatenate("A", "B", "C"));
		assertEquals("AC", StringKit.concatenate("A", "", "C"));
		assertEquals("A", StringKit.concatenate("A"));
		assertEquals("nullA", StringKit.concatenate(null, "A"));
	}
}