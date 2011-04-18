package util;

import junit.framework.Assert;

import org.junit.Test;

import com.taobao.top.ats.util.StringKit;

/**
 * 
 * @author moling
 * @since 1.0, 2010-8-24
 */
public class ValidateUtilTest {
	@Test
	public void isUnsignedNumberTest() {
		Assert.assertTrue(StringKit.isUnsignedNumber("12345"));
	}
	
	@Test
	public void isNotUnsignedNumberTest() {
		Assert.assertFalse(StringKit.isUnsignedNumber(null));
		Assert.assertFalse(StringKit.isUnsignedNumber(""));
		Assert.assertFalse(StringKit.isUnsignedNumber("a12345"));
		Assert.assertFalse(StringKit.isUnsignedNumber("-123"));
		Assert.assertFalse(StringKit.isUnsignedNumber("123.2"));
	}
	
	@Test
	public void isBooleanTest() {
		Assert.assertTrue(StringKit.isBoolean("true"));
		Assert.assertTrue(StringKit.isBoolean("false"));
	}
	
	@Test
	public void isNotBooleanTest() {
		Assert.assertFalse(StringKit.isBoolean(null));
		Assert.assertFalse(StringKit.isBoolean(""));
		Assert.assertFalse(StringKit.isBoolean("aa"));
	}
	
	@Test
	public void isDate() {
		Assert.assertTrue(StringKit.isDate("2010-03-08"));
		Assert.assertTrue(StringKit.isDate("2010-03-08 22:33:44"));
		Assert.assertTrue(StringKit.isDate("2010-03-08 22:33:44.555"));
	}
	
	@Test
	public void isNotDate() {
		Assert.assertFalse(StringKit.isDate(null));
		Assert.assertFalse(StringKit.isDate(""));
		Assert.assertFalse(StringKit.isDate("2010-3-8"));
	}
	

	@Test
	public void isPrice() {
		Assert.assertTrue(StringKit.isPrice("0"));
		Assert.assertTrue(StringKit.isPrice("12"));
		Assert.assertTrue(StringKit.isPrice("12.1"));
		Assert.assertTrue(StringKit.isPrice("12.35"));
		Assert.assertTrue(StringKit.isPrice("0.23"));
	}
	
	@Test
	public void isNotPrice() {
		Assert.assertFalse(StringKit.isPrice(null));
		Assert.assertFalse(StringKit.isPrice(""));
		Assert.assertFalse(StringKit.isPrice("-5"));
		Assert.assertFalse(StringKit.isPrice("-5.6"));
		Assert.assertFalse(StringKit.isPrice("6."));
		Assert.assertFalse(StringKit.isPrice(".64"));
	}
}
