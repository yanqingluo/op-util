package com.taobao.top.common.validate;

import java.math.BigInteger;
import java.text.ParseException;

import com.taobao.top.common.server.DateKitNew;

/**
 * @version 2010-04-09
 * @author haishi
 */
public class ValidateUtil {

	/**
	 * The number is defined as serial of digits. Note： 01234 would return true since most programing language
	 * treat it as a valid number.
	 * @param text
	 * @return
	 */
	public static boolean isUnsignedNumber(String text) {
		if (text == null) {
			return false;
		}
		
		if (text.length() == 0) {
			return false;
		}

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}

		return true;
		
	}

	
	/**
	 * Check whether the number is in range [min, max] inclusive.
	 * <p> Any/Both of the min/max could be null, which would be ignored.
	 * <p> Note, it's your responsibility to ensure min <= max if neither of them is null.
	 * @param text
	 * @param min
	 * @param max
	 * @return
	 */
	public static boolean isNumberInRange(String text, BigInteger min, BigInteger max) {
		BigInteger value = null;
		try {
			value = new BigInteger(text);
		} catch (Exception e) {
			// could be NullPointerException and MalformatException.
			return false;
		}
		
		if (min != null) {
			if (value.compareTo(min) < 0) {
				// smaller then min.
				return false;
			}
		} 
		
		if (max != null) {
			if (value.compareTo(max) > 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean isBoolean(String input) {
		if (input == null) {
			return false;
		}
		
		return input.equals("true") || input.equals("false");
	}
	
	public static boolean isDate(String input) {
		if (input == null) {
			return false;
		}
		
		try {
			DateKitNew.ymdOrYmdhms2Date(input);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	/**
	 * Positive Integer or Decimal
	 * @param input
	 * @return
	 */
	public static boolean isPrice(String input) {
		if (input == null) {
			return false;
		}
		int dotPos = input.indexOf('.');
		if (dotPos == -1) {
			return isUnsignedNumber(input);
		}
		
		if (dotPos + 1 == input.length()) {
			return false;
		}
		String partInteger = input.substring(0, dotPos);
		String partDecimal = input.substring(dotPos + 1, input.length());
		
		if (partDecimal.length() > 2) {
			return false;
		}
		
		return isUnsignedNumber(partInteger) && isUnsignedNumber(partDecimal);
	}
}
