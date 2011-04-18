package com.taobao.top.common.lang;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * {@link org.apache.commons.lang.StringUtils} enhence.
 * 
 * @version 2008-3-10
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class StringKit {
	
	public static boolean hasEmpty(String... strs) {
		for (String str : strs) {
			if (str == null || str.length() == 0) {
				return true;
			}
		}
		return false;
	}

	public static String urlEncode(String str) {
		String out = "";
		if (str == null)
			return out;
		try {
			out = URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		return out;
	}

	public static String urlDecode(String str) {
		String out = "";
		if (str == null)
			return out;
		try {
			out = URLDecoder.decode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		return out;
	}

	/**
	 * Remove each chars from input str.
	 * 
	 * @param str
	 * @param remove
	 * @return
	 */
	public static String removeEach(String str, String remove) {
		if (str == null || str.length() == 0 || remove == null
				|| remove.length() == 0) {
			return str;
		}
		char[] chars = str.toCharArray();
		char[] removeChars = remove.toCharArray();
		int pos = 0;
		for (int i = 0; i < chars.length; i++) {
			boolean match = false;
			for (int j = 0; j < removeChars.length; j++) {
				if (chars[i] == removeChars[j]) {
					match = true;
					break;
				}
			}
			if (!match) {
				chars[pos++] = chars[i];
			}
		}
		return new String(chars, 0, pos);
	}

	public static String replaceEach(String str, char repl, Object[] with) {
		if (str == null || str.length() == 0 || str.indexOf(repl) == -1) {
			return str;
		}
		char[] chars = str.toCharArray();
		StringBuilder builder = new StringBuilder(chars.length + 32);
		int pos = 0;
		for (int i = 0; i < chars.length; i++) {
			char ch = chars[i];
			if (ch != repl) {
				builder.append(ch);
			} else {
				builder.append(with[pos++]);
			}
		}
		return builder.toString();
	}

	/**
	 * Split a comma separated string into a string array.
	 * <p>
	 * A split method that doesn't use pattern.
	 * <p> And the null or empty value is appropriately handled, as
	 * well as heading and trailing space. 
	 * 
	 * then the JDK's 'split'.
	 * @param input
	 * @return
	 */
	public static String[] splitByComma(String input) {
		if (StringUtils.isEmpty(input)) {
			return new String[]{};
		}
		int start = 0;
		int commaIndex = 0;
		Vector<String> v = new Vector<String>();
		while(start < input.length()) {
			if ((commaIndex = input.indexOf(',', start)) == -1) {
				commaIndex = input.length();
			}
			
			String part = input.substring(start, commaIndex).trim();
			
			if (part.length() != 0) { // kick out empty string.
				v.add(part); 
			}
			
			start = commaIndex + 1;
		}
		
		String[] result = new String[v.size()];
		int i = 0;
		for(String value: v) {
			result[i] = value;
			i ++;
		}
		return result;
	}
	
	public static String toListString(List<String> list) {
		if (list == null || list.size() == 0) {
			return "";
		}
		String[] a = list.toArray(new String[list.size()]);
		StringBuilder buf = new StringBuilder();
		buf.append(a[0]);
		for (int i = 1; i < a.length; i++) {
			buf.append(",");
			buf.append(a[i]);
		}
		return buf.toString();
	}

	public static String toListStringTrimFirstLastChar(List<String> list) {
		if (list == null || list.size() == 0) {
			return "";
		}
		String[] a = list.toArray(new String[list.size()]);
		StringBuilder buf = new StringBuilder();
		buf.append(trimFirstLastChar(a[0]));
		for (int i = 1; i < a.length; i++) {
			buf.append(",");
			buf.append(trimFirstLastChar(a[i]));
		}
		return buf.toString();
	}

	/**
	 * An empty immutable <code>long</code> array.
	 */
	public static final long[] EMPTY_LONG_ARRAY = new long[0];

	public static long[] toLongArrayTrimFirstLastChar(List<String> list) {
		if (list == null || list.size() == 0) {
			return EMPTY_LONG_ARRAY;
		}
		String[] a = list.toArray(new String[list.size()]);
		long[] result = new long[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = trimFirstLastCharToLong(a[i]);
		}
		return result;
	}

	public static String[] toStringArrayTrimFirstListChar(List<String> list) {
		if (list == null || list.size() == 0) {
			return new String[0];
		}
		return list.toArray(new String[list.size()]);
	}

	public static String trimFirstLastChar(String str) {
		return str.substring(1, str.length() - 1);
	}

	public static long trimFirstLastCharToLong(String str) {
		String trimed = trimFirstLastChar(str);
		Long rs = Long.parseLong(trimed);
		return rs;
	}

	/**
	 * 只允许英文字符,数字,_,.,且首字必须是英文字符
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isAlphaDigitUnderSpotAndAlphaFirst(String str) {
		if (str == null) {
			return false;
		}
		int len = str.length();
		char cha = str.charAt(0);
		// ensure first char is letter
		if (Character.isLetter(cha) == false) {
			return false;
		}
		for (int i = 1; i < len; i++) {
			cha = str.charAt(i);
			if ((Character.isLetterOrDigit(cha) == false) && (cha != '_')
					&& (cha != '.')) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Check the string is not null and only contains [a-zA-Z0-9_.]
	 * @param str
	 * @return
	 */
	public static boolean onlyContainsAlphaDigitUnderlineDot(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		
		for (int i = 0; i < str.length(); i++) {
			char cha = str.charAt(i);
			if ((Character.isLetterOrDigit(cha) == false) && (cha != '_') && (cha != '.')) {
				return false;
			}
		}
		
		return true;
	}


	public static String readUnicode(String text) {
		if (StringUtils.isEmpty(text)) {
			return text;
		}
		StringBuilder sb = new StringBuilder(text.length() / 3);
		char before = text.charAt(0);
		int endIndex = 0;
		for (int i = 0; i < text.length(); i++) {
			char cha = text.charAt(i);
			if (cha == 'u' && before == '\\') {
				String codeStr = text.substring(i + 1, i + 5);
				int code = Integer.parseInt(codeStr, 16);
				sb.append((char) code);
				endIndex = i + 5;
			} else if (i >= endIndex && cha != '\\') {
				sb.append(cha);
			}
			before = cha;
		}
		return sb.toString();
	}

	/**
	 * 碰到半角大写的字母，转成小写
	 * 
	 * @author liupo
	 * @param str
	 * @return
	 */
	public static String toLowerCase(String str) {
		int length = str.length();
		if (length == 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char c = str.charAt(i);
			if (c > 255) {
				sb.append(c);
			} else {
				sb.append(String.valueOf(c).toLowerCase());
			}

		}
		return sb.toString();
	}

	/**
	 * 根据种子字符串生成固定长度的字符串
	 * 
	 * @param seed
	 * @param length
	 * @return
	 */
	public static String genWithSeed(String seed, int length) {
		StringBuffer sb = new StringBuffer(seed.length());
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(seed.charAt(random.nextInt(seed.length())));
		}
		return sb.toString();
	}

	/**
	 * 生成随机的中文字
	 * 
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	public static String genRandomOneChinese() {
		String str = null;
		int highPos, lowPos;
		Random random = new Random();
		highPos = (176 + Math.abs(random.nextInt(39)));
		lowPos = 161 + Math.abs(random.nextInt(93));

		byte[] b = new byte[2];
		b[0] = (byte)highPos;
		b[1] = (byte)lowPos;
		try {
			str = new String(b, "GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 生成随机的中文字
	 * 
	 * @param len
	 * @return
	 */
	public static String genRandomChinese(int len) {
		Random random = new Random();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			int highPos = (176 + Math.abs(random.nextInt(39)));
			int lowPos = 161 + Math.abs(random.nextInt(93));

			byte[] b = new byte[2];
			b[0] = (byte)highPos;
			b[1] = (byte)lowPos;
			try {
				sb.append(new String(b, "GB2312"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 过滤不可见字符
	 * 
	 * @author liupo
	 * @param input
	 * @return
	 */
	public static String stripNonValidXMLCharacters(String input) {
		if (input == null || ("".equals(input)))
			return "";
		StringBuilder out = new StringBuilder();
		char current;
		for (int i = 0; i < input.length(); i++) {
			current = input.charAt(i);
			if ((current == 0x9) || (current == 0xA) || (current == 0xD)
					|| ((current >= 0x20) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD))
					|| ((current >= 0x10000) && (current <= 0x10FFFF)))
				out.append(current);
		}
		return out.toString();
	}
	
	/**
	 * 请在debug时候使用
	 * 
	 * @param obj
	 * @return
	 */
	public static String dump(Object obj) {
		String result = null;
		if(obj instanceof Map) {
			result = MapKit.dumpMap((Map)obj);
		}else {
			result = ReflectionToStringBuilder.toString(obj);
		}
		return result; 
	}
	
	public static String concatenate(String... strings) {
		StringBuilder sb = new StringBuilder();
		
		for (String s : strings) {
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * 验证输入的参数是否走出指定的GBK字节长度。
	 * 
	 * @param value 待验证的字符串
	 * @param length 最大长度
	 * @return true/false（没有超出/超出长度）
	 */
	public static boolean validateByteLength(String value, int length) {
		return validateByteLength(value, length, "GBK");
	}

	/**
	 * 验证输入的参数是否走出指定的字节长度。
	 * 
	 * @param value 待验证的字符串
	 * @param length 最大长度
	 * @param charset 字符集
	 * @return true/false（没有超出/超出长度）
	 */
	public static boolean validateByteLength(String value, int length, String charset) {
		byte[] bytes = null;
		if (value == null || value.length() == 0) {
			return true;
		}

		try {
			bytes = value.getBytes(charset);
		} catch (Exception e) {
		}

		return bytes.length <= length;
	}

}