package com.taobao.top.common.validate;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

/**
 * @version 2009-8-6
 * @author <a href="mailto:haishi@taobao.com">haishi</a>
 *
 */
public class ValidateUtilTest {

	@Test
	public void testIsUnsignedNumber() {
		/////////////////
		// Positive Cases		
		
		// Zero
		assertTrue(ValidateUtil.isUnsignedNumber("0"));

		// Positive
		assertTrue(ValidateUtil.isUnsignedNumber("1"));
		assertTrue(ValidateUtil.isUnsignedNumber("9"));
		
		// Very long positive.
		assertTrue(ValidateUtil.isUnsignedNumber("1234567890000000000000000000000000"));

		/////////////////
		// Negative Cases
		
		// Negative
		assertFalse(ValidateUtil.isUnsignedNumber("-123456"));

		// Special Character
		assertFalse(ValidateUtil.isUnsignedNumber("123456%"));
		
		// Positive Sign
		assertFalse(ValidateUtil.isUnsignedNumber("+123456"));

		// Letters
		assertFalse(ValidateUtil.isUnsignedNumber("0x123456"));

		// Space, Empty, and Null.
		assertFalse(ValidateUtil.isUnsignedNumber("123 456"));
		assertFalse(ValidateUtil.isUnsignedNumber(" 123456 "));
		assertFalse(ValidateUtil.isUnsignedNumber(" "));
		assertFalse(ValidateUtil.isUnsignedNumber(""));
		assertFalse(ValidateUtil.isUnsignedNumber(null));
	
	}

	@Test
	public void testIsNumberInRange() {
		/////////////////
		// Range not set - Positive Cases
		
		assertTrue(ValidateUtil.isNumberInRange("0", null, null));
		assertTrue(ValidateUtil.isNumberInRange("1", null, null));
		assertTrue(ValidateUtil.isNumberInRange("9", null, null));
		assertTrue(ValidateUtil.isNumberInRange("12345678900000000000000000000000000", null, null));
		
		assertTrue(ValidateUtil.isNumberInRange("-0", null, null));
		assertTrue(ValidateUtil.isNumberInRange("-1", null, null));
		assertTrue(ValidateUtil.isNumberInRange("-9", null, null));
		assertTrue(ValidateUtil.isNumberInRange("-12345678900000000000000000000000000", null, null));
		
		/////////////////
		// Range not set - Negative Cases

		// Leading zero is allowed?
		assertTrue(ValidateUtil.isNumberInRange("00", null, null));
		assertTrue(ValidateUtil.isNumberInRange("01", null, null));

		// leading + is considered as malformat.
		assertFalse(ValidateUtil.isNumberInRange("+0", null, null));
		assertFalse(ValidateUtil.isNumberInRange("+1", null, null));
		
		// leading and/or tailing space is considered as malformat.
		assertFalse(ValidateUtil.isNumberInRange(" 0", null, null));
		assertFalse(ValidateUtil.isNumberInRange("1 ", null, null));
		assertFalse(ValidateUtil.isNumberInRange(" 1 ", null, null));
		assertFalse(ValidateUtil.isNumberInRange("- 1", null, null));
		
		// Special characters and letters.
		assertFalse(ValidateUtil.isNumberInRange("0!", null, null));
		assertFalse(ValidateUtil.isNumberInRange("0x1", null, null));
		assertFalse(ValidateUtil.isNumberInRange("EFA1", null, null));
		
		///////////
		// Min Range is set - Positive Cases
		assertTrue(ValidateUtil.isNumberInRange("0", new BigInteger("-1"), null));
		assertTrue(ValidateUtil.isNumberInRange("1", new BigInteger("0"), null));
		assertTrue(ValidateUtil.isNumberInRange("-1", new BigInteger("-2"), null));
		assertTrue(ValidateUtil.isNumberInRange("012345678900000000000000000000000000", new BigInteger("-12345678900000000000000000000000000"), null));

		// Min == Value cases
		assertTrue(ValidateUtil.isNumberInRange("0", new BigInteger("0"), null));
		assertTrue(ValidateUtil.isNumberInRange("01", new BigInteger("1"), null));
		assertTrue(ValidateUtil.isNumberInRange("-01", new BigInteger("-1"), null));
		assertTrue(ValidateUtil.isNumberInRange("12345678900000000000000000000000000", new BigInteger("12345678900000000000000000000000000"), null));
		
		// Min Range is set - Negative Cases
		assertFalse(ValidateUtil.isNumberInRange("0", new BigInteger("1"), null));
		assertFalse(ValidateUtil.isNumberInRange("1", new BigInteger("2"), null));
		assertFalse(ValidateUtil.isNumberInRange("-1", new BigInteger("0"), null));
		assertFalse(ValidateUtil.isNumberInRange("12345678900000000000000000000000000", new BigInteger("12345678900000000000000000000000001"), null));
		
		///////////
		// Max Range is set - Positive Cases
		assertTrue(ValidateUtil.isNumberInRange("0", null, new BigInteger("1")));
		assertTrue(ValidateUtil.isNumberInRange("1", null, new BigInteger("2")));
		assertTrue(ValidateUtil.isNumberInRange("-1", null, new BigInteger("0")));
		assertTrue(ValidateUtil.isNumberInRange("12345678900000000000000000000000000", null, new BigInteger("12345678900000000000000000000000001")));

		// Max == Value cases
		assertTrue(ValidateUtil.isNumberInRange("0", null, new BigInteger("0")));
		assertTrue(ValidateUtil.isNumberInRange("1", null, new BigInteger("1")));
		assertTrue(ValidateUtil.isNumberInRange("-1", null, new BigInteger("-1")));
		assertTrue(ValidateUtil.isNumberInRange("12345678900000000000000000000000000", null, new BigInteger("12345678900000000000000000000000000")));
		
		// Max Range is set - Negative Cases
		assertFalse(ValidateUtil.isNumberInRange("0", null, new BigInteger("-1")));
		assertFalse(ValidateUtil.isNumberInRange("1", null, new BigInteger("0")));
		assertFalse(ValidateUtil.isNumberInRange("-1", null, new BigInteger("-2")));
		assertFalse(ValidateUtil.isNumberInRange("12345678900000000000000000000000000", null, new BigInteger("-12345678900000000000000000000000000")));
		
		/////////////////
		// Full range set - Positive Cases
		assertTrue(ValidateUtil.isNumberInRange("0", new BigInteger("-1"), new BigInteger("1")));
		assertTrue(ValidateUtil.isNumberInRange("1", new BigInteger("0"), new BigInteger("2")));
		assertTrue(ValidateUtil.isNumberInRange("-1", new BigInteger("-2"), new BigInteger("0")));
		assertTrue(ValidateUtil.isNumberInRange("12345678900000000000000000000000000", new BigInteger("-12345678900000000000000000000000000"), new BigInteger("12345678900000000000000000000000001")));

		// Min == Value cases
		assertTrue(ValidateUtil.isNumberInRange("0", new BigInteger("0"), new BigInteger("1")));
		assertTrue(ValidateUtil.isNumberInRange("1", new BigInteger("1"), new BigInteger("2")));
		assertTrue(ValidateUtil.isNumberInRange("-1", new BigInteger("-1"), new BigInteger("0")));
		assertTrue(ValidateUtil.isNumberInRange("12345678900000000000000000000000000", new BigInteger("12345678900000000000000000000000000"), new BigInteger("12345678900000000000000000000000001")));
		
		// Max == Value cases
		assertTrue(ValidateUtil.isNumberInRange("0",  new BigInteger("-1"), new BigInteger("0")));
		assertTrue(ValidateUtil.isNumberInRange("1", new BigInteger("0"), new BigInteger("1")));
		assertTrue(ValidateUtil.isNumberInRange("-1", new BigInteger("-2"), new BigInteger("-1")));
		assertTrue(ValidateUtil.isNumberInRange("12345678900000000000000000000000000", new BigInteger("-12345678900000000000000000000000000"), new BigInteger("12345678900000000000000000000000000")));
	
		// Min == Max == Value cases
		assertTrue(ValidateUtil.isNumberInRange("0",  new BigInteger("0"), new BigInteger("0")));
		assertTrue(ValidateUtil.isNumberInRange("1", new BigInteger("1"), new BigInteger("1")));
		assertTrue(ValidateUtil.isNumberInRange("-1", new BigInteger("-1"), new BigInteger("-1")));
		assertTrue(ValidateUtil.isNumberInRange("12345678900000000000000000000000000", new BigInteger("12345678900000000000000000000000000"), new BigInteger("12345678900000000000000000000000000")));
	
		
		// Min Range is not satisfied - Negative Cases
		assertFalse(ValidateUtil.isNumberInRange("0", new BigInteger("1"), new BigInteger("1")));
		assertFalse(ValidateUtil.isNumberInRange("1", new BigInteger("2"), new BigInteger("2")));
		assertFalse(ValidateUtil.isNumberInRange("-1", new BigInteger("0"), new BigInteger("0")));
		assertFalse(ValidateUtil.isNumberInRange("12345678900000000000000000000000000", new BigInteger("12345678900000000000000000000000001"), new BigInteger("12345678900000000000000000000000001")));
		
		// Max Range is not satisfied - Negative Cases
		assertFalse(ValidateUtil.isNumberInRange("0", new BigInteger("-1"), new BigInteger("-1")));
		assertFalse(ValidateUtil.isNumberInRange("1", new BigInteger("0"), new BigInteger("0")));
		assertFalse(ValidateUtil.isNumberInRange("-1", new BigInteger("-2"), new BigInteger("-2")));
		assertFalse(ValidateUtil.isNumberInRange("12345678900000000000000000000000000", new BigInteger("-12345678900000000000000000000000000"), new BigInteger("-12345678900000000000000000000000000")));

		// Both Min and Max are not satisfied - Negative Cases
		assertFalse(ValidateUtil.isNumberInRange("0", new BigInteger("1"), new BigInteger("-1")));
		assertFalse(ValidateUtil.isNumberInRange("1", new BigInteger("2"), new BigInteger("0")));
		assertFalse(ValidateUtil.isNumberInRange("-1", new BigInteger("0"), new BigInteger("-2")));
		assertFalse(ValidateUtil.isNumberInRange("12345678900000000000000000000000000",  new BigInteger("12345678900000000000000000000000000"), new BigInteger("-12345678900000000000000000000000000")));

	}

	
	@Test
	public void testIsBoolean() {
		assertTrue(ValidateUtil.isBoolean("true"));
		assertTrue(ValidateUtil.isBoolean("false"));

		assertFalse(ValidateUtil.isBoolean(null));
		assertFalse(ValidateUtil.isBoolean("true1"));
		assertFalse(ValidateUtil.isBoolean("false1"));
		assertFalse(ValidateUtil.isBoolean(""));
	}
	
	@Test
	public void testIsDate() {
		assertTrue(ValidateUtil.isDate("2010-10-12 23:23:33"));
		assertTrue(ValidateUtil.isDate("2010-10-12 23:23:33.334"));
		
		assertTrue(ValidateUtil.isDate("2010-10-12"));
		assertTrue(ValidateUtil.isDate("2010-01-12"));

		assertFalse(ValidateUtil.isDate("2010-1-12"));
		assertFalse(ValidateUtil.isDate("2010-10-2X 23:23:33.334"));
		
	}
	
	@Test
	public void testIsPrice() {
		assertTrue(ValidateUtil.isPrice("12.12"));
		assertTrue(ValidateUtil.isPrice("12.1"));
		assertTrue(ValidateUtil.isPrice("12"));
		assertTrue(ValidateUtil.isPrice("0"));
		assertTrue(ValidateUtil.isPrice("0.0"));
		
		assertFalse(ValidateUtil.isPrice("12.123"));
		assertFalse(ValidateUtil.isPrice("12."));
		assertFalse(ValidateUtil.isPrice("."));
		assertFalse(ValidateUtil.isPrice(".0"));
		assertFalse(ValidateUtil.isPrice("1."));
		
		
	}
}
