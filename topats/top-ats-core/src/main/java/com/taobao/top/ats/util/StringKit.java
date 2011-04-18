package com.taobao.top.ats.util;

import java.text.ParseException;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author moling
 * @since 1.0, 2010-8-23
 */
public class StringKit {

	public static StringBuffer append(StringBuffer sb, Object content, boolean needAppend) {
		if (null == sb) {
			return null;
		}

		if (null != content) {
			sb.append(content);
		}

		if (needAppend) {
			sb.append(",");
		}

		return sb;
	}

	/**
	 * 是否为无符号整数。
	 */
	public static boolean isUnsignedNumber(String text) {
		if (StringUtils.isEmpty(text)) {
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
			DateKit.ymdOrYmdhms2Date(input);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	/**
	 * Positive Integer or Decimal
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
