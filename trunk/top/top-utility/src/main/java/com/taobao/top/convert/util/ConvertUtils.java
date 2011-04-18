package com.taobao.top.convert.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import com.taobao.top.convert.Convert;
import com.taobao.top.convert.ConvertArray;
import com.taobao.top.convert.ConvertException;
import com.taobao.top.convert.ConvertNull;
import com.taobao.top.convert.ConvertObject;

/**
 * Provides useful methods on java objects and JSON values.
 * 
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 * @version 6
 */
public final class ConvertUtils {
	/** Constant for char " */
	public static final String DOUBLE_QUOTE = "\"";
	/** Constant for char ' */
	public static final String SINGLE_QUOTE = "'";
	
	public static final char LT = '<';

	public static final char RT = '>';

	public static final String LTS = "</";
	
	public static final String LCDATA="<![CDATA[";
	
	public static final String RCDATA="]]>";
	
	public static final String SPACE=" ";
    /**
     * XML extend 默认格式JSON
     */
	public static boolean expandElements = true;
	private static Map<String, Method> methodMap = new ConcurrentHashMap<String, Method>();

	public static Method getMethod(String key) {
		return methodMap.get(key);
	}

	public static void setMethod(String key, Method method) {
		methodMap.put(key, method);
	}
	/**
	 * Returns the hashcode of value.<br>
	 * If null it will return JSONNull.getInstance().hashCode().<br>
	 * If value is JSON, JSONFunction or String, value.hashCode is returned,
	 * otherwise the value is transformed to a String an its hashcode is
	 * returned.
	 */
	public static int hashCode(Object value) {
		if (value == null) {
			return ConvertNull.getInstance().hashCode();
		} else if (value instanceof Convert || value instanceof String) {
			return value.hashCode();
		} else {
			return String.valueOf(value).hashCode();
		}
	}

	/**
	 * Tests if obj is an array or Collection.
	 */
	public static boolean isArray(Object obj) {
		if ((obj != null && obj.getClass().isArray())
				|| (obj instanceof Collection) || (obj instanceof ConvertArray)) {
			return true;
		}
		return false;
	}

	/**
	 * Tests if obj is a Boolean or primitive boolean
	 */
	public static boolean isBoolean(Object obj) {
		if ((obj instanceof Boolean)
				|| (obj != null && obj.getClass() == Boolean.TYPE)) {
			return true;
		}
		return false;
	}

	/**
	 * Tests if the obj is a javaScript null.
	 */
	public static boolean isNull(Object obj) {
		if (obj instanceof ConvertObject) {
			return ((ConvertObject) obj).isNullObject();
		}
		return ConvertNull.getInstance().equals(obj);
	}

	

	/**
	 * Tests if obj is a primitive number or wrapper.<br>
	 */
	public static boolean isNumber(Object obj) {
		if ((obj != null && obj.getClass() == Byte.TYPE)
				|| (obj != null && obj.getClass() == Short.TYPE)
				|| (obj != null && obj.getClass() == Integer.TYPE)
				|| (obj != null && obj.getClass() == Long.TYPE)
				|| (obj != null && obj.getClass() == Float.TYPE)
				|| (obj != null && obj.getClass() == Double.TYPE)) {
			return true;
		}

		return obj instanceof Number;
	}

	/**
	 * Tests if obj is a String or a char
	 */
	public static boolean isString(Object obj) {
		if ((obj instanceof String)
				|| (obj instanceof Character)
				|| (obj != null && (obj.getClass() == Character.TYPE || String.class
						.isAssignableFrom(obj.getClass())))) {
			return true;
		}
		return false;
	}

	/**
	 * Tests if the String possibly represents a valid JSON String.<br>
	 * Valid JSON strings are:
	 * <ul>
	 * <li>"null"</li>
	 * <li>starts with "[" and ends with "]"</li>
	 * <li>starts with "{" and ends with "}"</li>
	 * </ul>
	 */
	public static boolean mayBeJSON(String string) {
		return string != null
				&& ("null".equals(string)
						|| (string.startsWith("[") && string.endsWith("]")) || (string
						.startsWith("{") && string.endsWith("}")));
	}

	/**
	 * Produce a string from a Number.
	 * 
	 * @param n
	 *            A Number
	 * @return A String.
	 * @throws ConvertException
	 *             If n is a non-finite number.
	 */
	public static String numberToString(Number n) {
		if (n == null) {
			throw new ConvertException("Null pointer");
		}
		testValidity(n);

		// Shave off trailing zeros and decimal point, if possible.

		String s = n.toString();
		if (s.indexOf('.') > 0 && s.indexOf('e') < 0 && s.indexOf('E') < 0) {
			while (s.endsWith("0")) {
				s = s.substring(0, s.length() - 1);
			}
			if (s.endsWith(".")) {
				s = s.substring(0, s.length() - 1);
			}
		}
		return s;
	}

	/**
	 * Produce a string in double quotes with backslash sequences in all the
	 * right places. A backslash will be inserted within </, allowing JSON text
	 * to be delivered in HTML. In JSON text, a string cannot contain a control
	 * character or an unescaped quote or backslash.<br>
	 * <strong>CAUTION:</strong> if <code>string</code> represents a
	 * javascript function, translation of characters will not take place. This
	 * will produce a non-conformant JSON text.
	 * 
	 * @param string
	 *            A String
	 * @return A String correctly formatted for insertion in a JSON text.
	 */
	public static String quote(String string) {

		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		char b;
		char c = 0;
		int i;
		int len = string.length();
		StringBuffer sb = new StringBuffer(len + 4);
		String t;

		sb.append('"');
		for (i = 0; i < len; i += 1) {
			b = c;
			c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				sb.append('\\');
				sb.append(c);
				break;
			case '/':
				if (b == '<') {
					sb.append('\\');
				}
				sb.append(c);
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			default:
				if (c < ' ') {
					t = "000" + Integer.toHexString(c);
					sb.append("\\u").append(t.substring(t.length() - 4));
				} else {
					sb.append(c);
				}
			}
		}
		sb.append('"');
		return sb.toString();
	}

	/**
	 * Strips any single-quotes or double-quotes from boths sides of the string.
	 */
	public static String stripQuotes(String input) {
		if (input.length() < 2) {
			return input;
		} else if (input.startsWith(SINGLE_QUOTE)
				&& input.endsWith(SINGLE_QUOTE)) {
			return input.substring(1, input.length() - 1);
		} else if (input.startsWith(DOUBLE_QUOTE)
				&& input.endsWith(DOUBLE_QUOTE)) {
			return input.substring(1, input.length() - 1);
		} else {
			return input;
		}
	}

	/**
	 * Throw an exception if the object is an NaN or infinite number.
	 * 
	 * @param o
	 *            The object to test.
	 * @throws ConvertException
	 *             If o is a non-finite number.
	 */
	public static void testValidity(Object o) {
		if (o != null) {
			if (o instanceof Double) {
				if (((Double) o).isInfinite() || ((Double) o).isNaN()) {
					throw new ConvertException(
							"JSON does not allow non-finite numbers");
				}
			} else if (o instanceof Float) {
				if (((Float) o).isInfinite() || ((Float) o).isNaN()) {
					throw new ConvertException(
							"JSON does not allow non-finite numbers.");
				}
			} else if (o instanceof BigDecimal || o instanceof BigInteger) {
				// ok
				return;
			}
		}
	}

	/**
	 * Transforms a Number into a valid javascript number.<br>
	 * Float gets promoted to Double.<br>
	 * Byte and Short get promoted to Integer.<br>
	 * Long gets downgraded to Integer if possible.<br>
	 */
	public static Number transformNumber(Number input) {
		if (input instanceof Float) {
			return new Double(input.doubleValue());
		} else if (input instanceof Short) {
			return new Integer(input.intValue());
		} else if (input instanceof Byte) {
			return new Integer(input.intValue());
		} else if (input instanceof Long) {
			Long max = new Long(Integer.MAX_VALUE);
			if (input.longValue() <= max.longValue()
					&& input.longValue() >= Integer.MIN_VALUE) {
				return input.intValue();
			}
		}

		return input;
	}

	/**
	 * Make a JSON text of an Object value. If the object has an
	 * value.toJSONString() method, then that method will be used to produce the
	 * JSON text. The method is required to produce a strictly conforming text.
	 * If the object does not contain a toJSONString method (which is the most
	 * common case), then a text will be produced by the rules.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @param value
	 *            The value to be serialized.
	 * @return a printable, displayable, transmittable representation of the
	 *         object, beginning with <code>{</code>&nbsp;<small>(left
	 *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *         brace)</small>.
	 * @throws ConvertException
	 *             If the value is or contains an invalid number.
	 */
	public static String valueToString(Object value) {
		if (value == null || isNull(value)) {
			return "null";
		}

		if (value instanceof Number) {
			return numberToString((Number) value);
		}
		if (value instanceof Boolean || value instanceof ConvertObject
				|| value instanceof ConvertArray) {
			return value.toString();
		}
		return quote(value.toString());
	}

	/**
	 * Make a prettyprinted JSON text of an object value.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @param value
	 *            The value to be serialized.
	 * @param indentFactor
	 *            The number of spaces to add to each level of indentation.
	 * @param indent
	 *            The indentation of the top level.
	 * @return a printable, displayable, transmittable representation of the
	 *         object, beginning with <code>{</code>&nbsp;<small>(left
	 *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *         brace)</small>.
	 * @throws ConvertException
	 *             If the object contains an invalid number.
	 */
	public static String valueToString(Object value, int indentFactor,
			int indent) {
		if (value == null || isNull(value)) {
			return "null";
		}

		if (value instanceof Number) {
			return numberToString((Number) value);
		}
		if (value instanceof Boolean) {
			return value.toString();
		}
		if (value instanceof ConvertObject) {
			return ((ConvertObject) value).toString(indentFactor, indent);
		}
		if (value instanceof ConvertArray) {
			return ((ConvertArray) value).toString(indentFactor, indent);
		}
		return quote(value.toString());
	}

	public static boolean isExpandElements() {
		return expandElements;
	}

	public static void setExpandElements(boolean expandElements) {
		ConvertUtils.expandElements = expandElements;
	}
	/**
	 * 判断字符串有没有做过 CDATA 格式 是=false 否=true
	 * @author liupo
	 * @param value
	 * @return
	 */
	public static Boolean  isXMLEscape(String value){
		Boolean type=true;
		if(StringUtils.indexOf(value,LCDATA)>0){
			type=false;
		}
		return type;
	}
    public static String getClassName(String className){
    	if(null!=className){
    	  return	className.substring(className.lastIndexOf(".")+1);
    	}
    	return "";
    }
    public static String toLowerCaseWithUnderscores(String key){
    	StringBuilder  sb=new StringBuilder();
    	 int   length = key.length();
    	 char ch;
    	 for (int index = 0; index < length; index++) {
             ch = key.charAt(index);
    		if(Character.isUpperCase(ch)){
    			sb.append("_");
    			sb.append(Character.toLowerCase(ch));
    		}else{
    		    sb.append(ch);
    		}
    	}
    	return sb.toString();
    }
}