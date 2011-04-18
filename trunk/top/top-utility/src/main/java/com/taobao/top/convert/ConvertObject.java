/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taobao.top.convert;

/*
 * Copyright (c) 2002 JSON.org
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * The Software shall be used for Good, not Evil.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.common.lang.StringUtil;
import com.taobao.top.common.server.DateKit;
import com.taobao.top.convert.util.ConvertUtils;
import com.taobao.util.CalendarUtil;

/**
 * A JSONObject is an unordered collection of name/value pairs. Its external
 * form is a string wrapped in curly braces with colons between the names and
 * values, and commas between the values and names. The internal form is an
 * object having <code>get</code> and <code>opt</code> methods for accessing the
 * values by name, and <code>put</code> methods for adding or replacing values
 * by name. The values can be any of these types: <code>Boolean</code>,
 * <code>JSONArray</code>, <code>JSONObject</code>, <code>Number</code>,
 * <code>String</code>, or the <code>JSONNull</code> object. A JSONObject
 * constructor can be used to convert an external form JSON text into an
 * internal form whose values can be retrieved with the <code>get</code> and
 * <code>opt</code> methods, or to convert values into a JSON text using the
 * <code>element</code> and <code>toString</code> methods. A <code>get</code>
 * method returns a value if one can be found, and throws an exception if one
 * cannot be found. An <code>opt</code> method returns a default value instead
 * of throwing an exception, and so is useful for obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an
 * object, which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking and type
 * coersion for you.
 * <p>
 * The <code>put</code> methods adds values to an object. For example,
 * 
 * <pre>
 * myString = new JSONObject().put(&quot;JSON&quot;, &quot;Hello, World!&quot;).toString();
 * </pre>
 * 
 * produces the string <code>{"JSON": "Hello, World"}</code>.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to
 * the JSON sysntax rules. The constructors are more forgiving in the texts they
 * will accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just
 * before the closing brace.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a quote
 * or single quote, and if they do not contain leading or trailing spaces, and
 * if they do not contain any of these characters:
 * <code>{ } [ ] / \ : , = ; #</code> and if they do not look like numbers and
 * if they are not the reserved words <code>true</code>, <code>false</code>, or
 * <code>null</code>.</li>
 * <li>Keys can be followed by <code>=</code> or <code>=></code> as well as by
 * <code>:</code>.</li>
 * <li>Values can be followed by <code>;</code> <small>(semicolon)</small> as
 * well as by <code>,</code> <small>(comma)</small>.</li>
 * <li>Numbers may have the <code>0-</code> <small>(octal)</small> or <code>0x-</code>
 * <small>(hex)</small> prefix.</li>
 * <li>Comments written in the slashshlash, slashstar, and hash conventions will
 * be ignored.</li>
 * </ul>
 * 
 * @author JSON.org
 */
@SuppressWarnings("unchecked")
public final class ConvertObject implements Convert, Map {

	/** identifies this object as null */
	private boolean nullObject;

	/**
	 * The Map where the JSONObject's properties are kept.
	 */
	private Map properties;

	private String objectName;

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	/**
	 * Construct an empty JSONObject.
	 */
	public ConvertObject() {
		this.properties = new HashMap();
	}

	/**
	 * Creates a JSONObject that is null.
	 */
	public ConvertObject(boolean isNull) {
		this();
		this.nullObject = isNull;
	}

	/**
	 * Creates a JSONObject.<br>
	 * Inspects the object type to call the correct JSONObject factory method.
	 * Accepts JSON formatted strings, Maps, DynaBeans and JavaBeans.
	 * 
	 * @param object
	 * @throws ConvertException
	 *             if the object can not be converted to a proper JSONObject.
	 */
	public static ConvertObject fromObject(Object object) {
		if (object == null || ConvertUtils.isNull(object)) {
			return new ConvertObject(true);
		} else if (object instanceof Map) {
			return _fromMap((Map) object);
		} else if (ConvertUtils.isNumber(object)
				|| ConvertUtils.isBoolean(object)
				|| ConvertUtils.isString(object)) {
			return new ConvertObject();
		} else {
			return _fromBean(object);
		}
	}

	public static void fromObjectToXML(Object object, Writer writer,
			String objectName) throws IOException {
		writer.write(ConvertUtils.LT);
		writer.write(objectName);
		writer.write(ConvertUtils.RT);
		ConvertObject json = ConvertObject.fromObject(object);
		json.write(writer);
		writer.write(ConvertUtils.LTS);
		writer.write(objectName);
		writer.write(ConvertUtils.RT);
	}

	public static void fromListToXML(List object, Writer writer,
			String objectName) throws IOException {
		for (int i = 0; i < object.size(); i++) {
			ConvertObject.fromObjectToXML(object.get(i), writer, objectName);
		}
	}

	/**
	 * Creates a JSONObject from a POJO.<br>
	 * Supports nested maps, POJOs, and arrays/collections.
	 * 
	 * @param bean
	 *            An object with POJO conventions
	 * @throws ConvertException
	 *             if the bean can not be converted to a proper JSONObject.
	 */
	private static ConvertObject _fromBean(Object bean) {

		ConvertObject jsonObject = new ConvertObject();
		try {
			// long stat=System.currentTimeMillis();
			BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			String key = null;
			for (int i = 0; i < pds.length; i++) {
				key = pds[i].getName();
				if (pds[i].getWriteMethod() == null
						|| pds[i].getReadMethod() == null) {
					continue;
				}
				Class type = pds[i].getPropertyType();
				Method method = ConvertUtils.getMethod(key);
				if (method == null) {
					method = pds[i].getReadMethod();
					ConvertUtils.setMethod(key, method);
				}
				// Method method = pds[i].getReadMethod();
				method.setAccessible(true);
				Object value = method.invoke(bean, null);
				if (value == null) {
					continue;
				}
				if (value.getClass() == Date.class) {
					// value=DateTest.dateToStr((Date) value);
					value = DateKit.date2ymdhms((Date) value);
				}
				key = ConvertUtils.toLowerCaseWithUnderscores(key);
				setValue(jsonObject, key, value, type);

			}
		} catch (ConvertException jsone) {

		} catch (Exception e) {

		}

		return jsonObject;
	}

	private static ConvertObject _fromMap(Map map) {

		if (map == null) {

			return new ConvertObject(true);
		}

		ConvertObject jsonObject = new ConvertObject();
		try {
			for (Iterator entries = map.entrySet().iterator(); entries
					.hasNext();) {
				Map.Entry entry = (Map.Entry) entries.next();
				Object k = entry.getKey();
				String key = (k instanceof String) ? (String) k : String
						.valueOf(k);

				Object value = entry.getValue();

				if (value != null) {
					/*
					 * JsonValueProcessor jsonValueProcessor =
					 * jsonConfig.findJsonValueProcessor( value.getClass(), key
					 * ); if( jsonValueProcessor != null ){ value =
					 * jsonValueProcessor.processObjectValue( key, value,
					 * jsonConfig ); if( !JsonVerifier.isValidJsonValue( value )
					 * ){ throw new JSONException( "Value is not a valid JSON
					 * value. " + value ); } }
					 */
					setValue(jsonObject, key, value, value.getClass());
				}
			}
		} catch (ConvertException jsone) {

			throw jsone;
		} catch (RuntimeException e) {

			ConvertException jsone = new ConvertException(e);

			throw jsone;
		}

		return jsonObject;
	}

	private static void setValue(ConvertObject jsonObject, String key,
			Object value, Class type) {
		if (String.class.isAssignableFrom(type)
				|| Boolean.class.isAssignableFrom(type)) {
			jsonObject.properties.put(key, value);
		} else {
			jsonObject._setInternal(key, value);
		}

	}

	public void clear() {
		properties.clear();
	}

	public boolean containsKey(Object key) {
		return properties.containsKey(key);
	}

	public boolean containsValue(Object value) {
		try {
			value = processValue(value);
		} catch (ConvertException e) {
			return false;
		}
		return properties.containsValue(value);
	}

	/**
	 * Remove a name and its value, if present.
	 * 
	 * @param key
	 *            A key string.
	 * @return this.
	 */
	public ConvertObject discard(String key) {
		verifyIsNull();
		this.properties.remove(key);
		return this;
	}

	/**
	 * Put a key/boolean pair in the JSONObject.
	 * 
	 * @param key
	 *            A key string.
	 * @param value
	 *            A boolean which is the value.
	 * @return this.
	 * @throws ConvertException
	 *             If the key is null.
	 */
	public ConvertObject element(String key, boolean value) {
		verifyIsNull();
		return element(key, value ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Put a key/value pair in the JSONObject, where the value will be a
	 * JSONArray which is produced from a Collection.
	 * 
	 * @param key
	 *            A key string.
	 * @param value
	 *            A Collection value.
	 * @return this.
	 * @throws ConvertException
	 */
	public ConvertObject element(String key, Collection value) {
		verifyIsNull();
		if (value instanceof ConvertArray) {
			return setInternal(key, value);
		} else {
			return element(key, ConvertArray.fromObject(value));
		}
	}

	/**
	 * Put a key/double pair in the JSONObject.
	 * 
	 * @param key
	 *            A key string.
	 * @param value
	 *            A double which is the value.
	 * @return this.
	 * @throws ConvertException
	 *             If the key is null or if the number is invalid.
	 */
	public ConvertObject element(String key, double value) {
		verifyIsNull();
		Double d = new Double(value);
		ConvertUtils.testValidity(d);
		return element(key, d);
	}

	/**
	 * Put a key/int pair in the JSONObject.
	 * 
	 * @param key
	 *            A key string.
	 * @param value
	 *            An int which is the value.
	 * @return this.
	 * @throws ConvertException
	 *             If the key is null.
	 */
	public ConvertObject element(String key, int value) {
		verifyIsNull();
		return element(key, new Integer(value));
	}

	/**
	 * Put a key/long pair in the JSONObject.
	 * 
	 * @param key
	 *            A key string.
	 * @param value
	 *            A long which is the value.
	 * @return this.
	 * @throws ConvertException
	 *             If the key is null.
	 */
	public ConvertObject element(String key, long value) {
		verifyIsNull();
		return element(key, new Long(value));
	}

	/**
	 * Put a key/value pair in the JSONObject, where the value will be a
	 * JSONObject which is produced from a Map.
	 * 
	 * @param key
	 *            A key string.
	 * @param value
	 *            A Map value.
	 * @return this.
	 * @throws ConvertException
	 */
	public ConvertObject element(String key, Map value) {
		verifyIsNull();
		if (value instanceof ConvertObject) {
			return setInternal(key, value);
		} else {
			return element(key, ConvertObject.fromObject(value));
		}
	}

	/**
	 * Put a key/value pair in the JSONObject. If the value is null, then the
	 * key will be removed from the JSONObject if it is present.<br>
	 * If there is a previous value assigned to the key, it will call
	 * accumulate.
	 * 
	 * @param key
	 *            A key string.
	 * @param value
	 *            An object which is the value. It should be of one of these
	 *            types: Boolean, Double, Integer, JSONArray, JSONObject, Long,
	 *            String, or the JSONNull object.
	 * @return this.
	 * @throws ConvertException
	 *             If the value is non-finite number or if the key is null.
	 */
	public ConvertObject element(String key, Object value) {
		verifyIsNull();
		if (key == null) {
			throw new ConvertException("Null key.");
		}
		if (value != null) {
			value = processValue(key, value);
			_setInternal(key, value);
		} else {
			remove(key);
		}
		return this;
	}

	public Set entrySet() {
		return properties.entrySet();
	}

	public Object get(Object key) {
		if (key instanceof String) {
			return get((String) key);
		}
		return null;
	}

	/**
	 * Get the value object associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return The object associated with the key.
	 * @throws ConvertException
	 *             if this.isNull() returns true.
	 */
	public Object get(String key) {
		verifyIsNull();
		return this.properties.get(key);
	}

	/**
	 * Get the boolean value associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return The truth.
	 * @throws ConvertException
	 *             if the value is not a Boolean or the String "true" or
	 *             "false".
	 */
	public boolean getBoolean(String key) {
		verifyIsNull();
		Object o = get(key);
		if (o != null) {
			if (o.equals(Boolean.FALSE)
					|| (o instanceof String && ((String) o)
							.equalsIgnoreCase("false"))) {
				return false;
			} else if (o.equals(Boolean.TRUE)
					|| (o instanceof String && ((String) o)
							.equalsIgnoreCase("true"))) {
				return true;
			}
		}
		throw new ConvertException("JSONObject[" + ConvertUtils.quote(key)
				+ "] is not a Boolean.");
	}

	/**
	 * Get the double value associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return The numeric value.
	 * @throws ConvertException
	 *             if the key is not found or if the value is not a Number
	 *             object and cannot be converted to a number.
	 */
	public double getDouble(String key) {
		verifyIsNull();
		Object o = get(key);
		if (o != null) {
			try {
				return o instanceof Number ? ((Number) o).doubleValue()
						: Double.parseDouble((String) o);
			} catch (Exception e) {
				throw new ConvertException("JSONObject["
						+ ConvertUtils.quote(key) + "] is not a number.");
			}
		}
		throw new ConvertException("JSONObject[" + ConvertUtils.quote(key)
				+ "] is not a number.");
	}

	/**
	 * Get the int value associated with a key. If the number value is too large
	 * for an int, it will be clipped.
	 * 
	 * @param key
	 *            A key string.
	 * @return The integer value.
	 * @throws ConvertException
	 *             if the key is not found or if the value cannot be converted
	 *             to an integer.
	 */
	public int getInt(String key) {
		verifyIsNull();
		Object o = get(key);
		if (o != null) {
			return o instanceof Number ? ((Number) o).intValue()
					: (int) getDouble(key);
		}
		throw new ConvertException("JSONObject[" + ConvertUtils.quote(key)
				+ "] is not a number.");
	}

	/**
	 * Get the JSONArray value associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return A JSONArray which is the value.
	 * @throws ConvertException
	 *             if the key is not found or if the value is not a JSONArray.
	 */
	public ConvertArray getJSONArray(String key) {
		verifyIsNull();
		Object o = get(key);
		if (o != null && o instanceof ConvertArray) {
			return (ConvertArray) o;
		}
		throw new ConvertException("JSONObject[" + ConvertUtils.quote(key)
				+ "] is not a JSONArray.");
	}

	/**
	 * Get the JSONObject value associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return A JSONObject which is the value.
	 * @throws ConvertException
	 *             if the key is not found or if the value is not a JSONObject.
	 */
	public ConvertObject getJSONObject(String key) {
		verifyIsNull();
		Object o = get(key);
		if (ConvertNull.getInstance().equals(o)) {
			return new ConvertObject(true);
		} else if (o instanceof ConvertObject) {
			return (ConvertObject) o;
		}
		throw new ConvertException("JSONObject[" + ConvertUtils.quote(key)
				+ "] is not a JSONObject.");
	}

	/**
	 * Get the long value associated with a key. If the number value is too long
	 * for a long, it will be clipped.
	 * 
	 * @param key
	 *            A key string.
	 * @return The long value.
	 * @throws ConvertException
	 *             if the key is not found or if the value cannot be converted
	 *             to a long.
	 */
	public long getLong(String key) {
		verifyIsNull();
		Object o = get(key);
		if (o != null) {
			return o instanceof Number ? ((Number) o).longValue()
					: (long) getDouble(key);
		}
		throw new ConvertException("JSONObject[" + ConvertUtils.quote(key)
				+ "] is not a number.");
	}

	/**
	 * Get the string associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return A string which is the value.
	 * @throws ConvertException
	 *             if the key is not found.
	 */
	public String getString(String key) {
		verifyIsNull();
		Object o = get(key);
		if (o != null) {
			return o.toString();
		}
		throw new ConvertException("JSONObject[" + ConvertUtils.quote(key)
				+ "] not found.");
	}

	/**
	 * Determine if the JSONObject contains a specific key.
	 * 
	 * @param key
	 *            A key string.
	 * @return true if the key exists in the JSONObject.
	 */
	public boolean has(String key) {
		verifyIsNull();
		return this.properties.containsKey(key);
	}

	public int hashCode() {
		int hashcode = 19;
		if (isNullObject()) {
			return hashcode + ConvertNull.getInstance().hashCode();
		}
		for (Iterator entries = properties.entrySet().iterator(); entries
				.hasNext();) {
			Map.Entry entry = (Map.Entry) entries.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			hashcode += key.hashCode() + ConvertUtils.hashCode(value);
		}
		return hashcode;
	}

	public boolean isArray() {
		return false;
	}

	public boolean isEmpty() {
		verifyIsNull();
		return this.properties.isEmpty();
	}

	/**
	 * Returs if this object is a null JSONObject.
	 */
	public boolean isNullObject() {
		return nullObject;
	}

	/**
	 * Get an enumeration of the keys of the JSONObject.
	 * 
	 * @return An iterator of the keys.
	 */
	public Iterator keys() {
		verifyIsNull();
		return this.properties.keySet().iterator();
	}

	public Set keySet() {
		return properties.keySet();
	}

	/**
	 * Produce a JSONArray containing the names of the elements of this
	 * JSONObject.
	 * 
	 * @return A JSONArray containing the key strings, or null if the JSONObject
	 *         is empty.
	 */
	public ConvertArray names() {
		verifyIsNull();
		ConvertArray ja = new ConvertArray();
		Iterator keys = keys();
		while (keys.hasNext()) {
			ja.element(keys.next());
		}
		return ja;
	}

	public Object put(Object key, Object value) {
		if (key == null) {
			throw new IllegalArgumentException("key is null.");
		}
		Object previous = properties.get(key);
		element(String.valueOf(key), value);
		return previous;
	}

	public void putAll(Map map) {
		if (map instanceof ConvertObject) {
			for (Iterator entries = map.entrySet().iterator(); entries
					.hasNext();) {
				Map.Entry entry = (Map.Entry) entries.next();
				String key = (String) entry.getKey();
				Object value = entry.getValue();
				this.properties.put(key, value);
			}
		} else {
			for (Iterator entries = map.entrySet().iterator(); entries
					.hasNext();) {
				Map.Entry entry = (Map.Entry) entries.next();
				String key = String.valueOf(entry.getKey());
				Object value = entry.getValue();
				element(key, value);
			}
		}
	}

	public Object remove(Object key) {
		return properties.remove(key);
	}

	/**
	 * Remove a name and its value, if present.
	 * 
	 * @param key
	 *            The name to be removed.
	 * @return The value that was associated with the name, or null if there was
	 *         no value.
	 */
	public Object remove(String key) {
		verifyIsNull();
		return this.properties.remove(key);
	}

	/**
	 * Get the number of keys stored in the JSONObject.
	 * 
	 * @return The number of keys in the JSONObject.
	 */
	public int size() {
		verifyIsNull();
		return this.properties.size();
	}

	/**
	 * Make a JSON text of this JSONObject. For compactness, no whitespace is
	 * added. If this would not result in a syntactically correct JSON text,
	 * then null will be returned instead.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @return a printable, displayable, portable, transmittable representation
	 *         of the object, beginning with <code>{</code>&nbsp;<small>(left
	 *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *         brace)</small>.
	 */
	public String toString() {
		if (isNullObject()) {
			return ConvertNull.getInstance().toString();
		}
		Iterator keys = keys();
		try {
			StringBuffer sb = new StringBuffer();
			if (ConvertUtils.isExpandElements()) {
				sb.append("{");
				while (keys.hasNext()) {
					if (sb.length() > 1) {
						sb.append(',');
					}
					Object o = keys.next();
					sb.append(ConvertUtils.quote(o.toString()));
					sb.append(':');
					sb.append(ConvertUtils
							.valueToString(this.properties.get(o)));
				}
				sb.append('}');
			} else {
				if (StringUtil.isNotEmpty(this.objectName)) {
					sb.append(ConvertUtils.LT);
					sb.append(objectName);
					sb.append(ConvertUtils.RT);
				}
				while (keys.hasNext()) {
					sb.append(ConvertUtils.LT);
					Object o = keys.next();
					sb.append(o.toString());
					sb.append(ConvertUtils.RT);
					sb.append(ConvertUtils.LCDATA);
					sb.append(this.properties.get(o));
					sb.append(ConvertUtils.RCDATA);
					sb.append(ConvertUtils.LTS);
					sb.append(o.toString());
					sb.append(ConvertUtils.RT);
				}
				if (StringUtil.isNotEmpty(this.objectName)) {
					sb.append(ConvertUtils.LTS);
					sb.append(objectName);
					sb.append(ConvertUtils.RT);
				}
			}
			return sb.toString();
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Make a prettyprinted JSON text of this JSONObject.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
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
	public String toString(int indentFactor, int indent) {
		if (isNullObject()) {
			return ConvertNull.getInstance().toString();
		}
		int i;
		int n = size();
		if (n == 0) {
			return "{}";
		}
		if (indentFactor == 0) {
			return this.toString();
		}
		Iterator keys = keys();
		StringBuffer sb = new StringBuffer("{");
		int newindent = indent + indentFactor;
		Object o;
		if (n == 1) {
			o = keys.next();
			sb.append(ConvertUtils.quote(o.toString()));
			sb.append(": ");
			sb.append(ConvertUtils.valueToString(this.properties.get(o),
					indentFactor, indent));
		} else {
			while (keys.hasNext()) {
				o = keys.next();
				if (sb.length() > 1) {
					sb.append(",\n");
				} else {
					sb.append('\n');
				}
				for (i = 0; i < newindent; i += 1) {
					sb.append(' ');
				}
				sb.append(ConvertUtils.quote(o.toString()));
				sb.append(": ");
				sb.append(ConvertUtils.valueToString(this.properties.get(o),
						indentFactor, newindent));
			}
			if (sb.length() > 1) {
				sb.append('\n');
				for (i = 0; i < indent; i += 1) {
					sb.append(' ');
				}
			}
			for (i = 0; i < indent; i += 1) {
				sb.insert(0, ' ');
			}
		}
		sb.append('}');
		return sb.toString();
	}

	public Collection values() {
		return Collections.unmodifiableCollection(properties.values());
	}

	/**
	 * Write the contents of the JSONObject as JSON text to a writer. For
	 * compactness, no whitespace is added.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @return The writer.
	 * @throws ConvertException
	 */
	public Writer write(Writer writer) {
		try {
			if (isNullObject()) {
				writer.write(ConvertNull.getInstance().toString());
				return writer;
			}
			Iterator keys = keys();
			if (ConvertUtils.isExpandElements()) {
				writerJson(writer, keys);
			} else {
				writerXml(writer, keys);
			}

			return writer;
		} catch (IOException e) {
			throw new ConvertException(e);
		}
	}

	private void writerJson(Writer writer, Iterator keys) throws IOException {
		writer.write('{');
		boolean b = false;
		while (keys.hasNext()) {
			if (b) {
				writer.write(',');
			}
			Object k = keys.next();
			writer.write(k.toString());
			writer.write(':');
			Object v = this.properties.get(k);
			if (v instanceof ConvertObject) {
				((ConvertObject) v).write(writer);
			} else if (v instanceof ConvertArray) {
				((ConvertArray) v).write(writer);
			} else {
				writer.write(ConvertUtils.quote(v.toString()));
			}
			b = true;
		}
		writer.write('}');
	}

	private void writerXml(Writer writer, Iterator keys) throws IOException {
		boolean type = false;
		while (keys.hasNext()) {
			Object k = keys.next();
			Object v = this.properties.get(k);
			type = ConvertUtils.isXMLEscape(v.toString());
			/*
			 * if(!type){ writer.write(JSONUtils.SPACE); writer.write(""); }
			 */
			if(!(v instanceof ConvertArray)){//自雪需求，把第一层摸掉
				writer.write(ConvertUtils.LT);
				writer.write(k.toString());

				writer.write(ConvertUtils.RT);
			}
			
			if (v instanceof ConvertObject) {
				((ConvertObject) v).write(writer);
			} else if (v instanceof ConvertArray) {
				if (type) {
					writer.write(ConvertUtils.LCDATA);
				}
				((ConvertArray) v).write(writer);
				if (type) {
					writer.write(ConvertUtils.RCDATA);
				}
			} else {
				if (type) {
					writer.write(ConvertUtils.LCDATA);
				}
				writer.write(v.toString());
				if (type) {
					writer.write(ConvertUtils.RCDATA);
				}
			}
			if(!(v instanceof ConvertArray)){ //自雪需求，把第一层摸掉
			writer.write(ConvertUtils.LTS);
			writer.write(k.toString());
			writer.write(ConvertUtils.RT);
			}
		}
	}

	private Object _processValue(Object value) {
		if ((value != null && Class.class.isAssignableFrom(value.getClass()))
				|| value instanceof Class) {
			return ((Class) value).getName();
		} else if (ConvertUtils.isArray(value)) {
			return ConvertArray.fromObject(value);
		} else if (ConvertUtils.isString(value)) {
			String str = String.valueOf(value);
			if (value == null) {
				return "";
			} else {
				return str;
			}

		} else if (ConvertUtils.isNumber(value)) {
			ConvertUtils.testValidity(value);
			return ConvertUtils.transformNumber((Number) value);
		} else if (ConvertUtils.isBoolean(value)) {
			return value;
		} else if (value != null
				&& Enum.class.isAssignableFrom(value.getClass())) {
			return ((Enum) value).name();
		} else {
			return fromObject(value);
		}
	}

	/**
	 * Put a key/value pair in the JSONObject.
	 * 
	 * @param key
	 *            A key string.
	 * @param value
	 *            An object which is the value. It should be of one of these
	 *            types: Boolean, Double, Integer, JSONArray, JSONObject, Long,
	 *            String, or the JSONNull object.
	 * @return this.
	 * @throws ConvertException
	 *             If the value is non-finite number or if the key is null.
	 */
	private ConvertObject _setInternal(String key, Object value) {
		verifyIsNull();
		if (key == null) {
			throw new ConvertException("Null key.");
		}

		if (ConvertUtils.isString(value)) {
			this.properties.put(key, value);
		} else {
			Object jo = _processValue(value);
			this.properties.put(key, jo);

		}

		return this;
	}

	private Object processValue(Object value) {
		if (value != null) {
			/*
			 * JsonValueProcessor processor = jsonConfig.findJsonValueProcessor(
			 * value.getClass() ); if( processor != null ){ value =
			 * processor.processObjectValue( null, value, jsonConfig ); if(
			 * !JsonVerifier.isValidJsonValue( value ) ){ throw new
			 * JSONException( "Value is not a valid JSON value. " + value ); } }
			 */
		}
		return _processValue(value);
	}

	private Object processValue(String key, Object value) {
		if (value != null) {
			/*
			 * JsonValueProcessor processor = jsonConfig.findJsonValueProcessor(
			 * value.getClass(), key ); if( processor != null ){ value =
			 * processor.processObjectValue( null, value, jsonConfig ); if(
			 * !JsonVerifier.isValidJsonValue( value ) ){ throw new
			 * JSONException( "Value is not a valid JSON value. " + value ); } }
			 */
		}
		return _processValue(value);
	}

	/**
	 * Put a key/value pair in the JSONObject.
	 * 
	 * @param key
	 *            A key string.
	 * @param value
	 *            An object which is the value. It should be of one of these
	 *            types: Boolean, Double, Integer, JSONArray, JSONObject, Long,
	 *            String, or the JSONNull object.
	 * @return this.
	 * @throws ConvertException
	 *             If the value is non-finite number or if the key is null.
	 */
	private ConvertObject setInternal(String key, Object value) {
		return _setInternal(key, processValue(key, value));
	}

	/**
	 * Checks if this object is a "null" object.
	 */
	private void verifyIsNull() {
		if (isNullObject()) {
			throw new ConvertException("null object");
		}
	}

	/**
	 * Get an optional value associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return An object which is the value, or null if there is no value.
	 */
	public Object opt(String key) {
		verifyIsNull();
		return key == null ? null : this.properties.get(key);
	}

	public Map getProperties() {
		return properties;
	}

}
