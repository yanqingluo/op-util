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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.taobao.top.convert.util.ConvertUtils;

/**
 * A JSONArray is an ordered sequence of values. Its external text form is a
 * string wrapped in square brackets with commas separating the values. The
 * internal form is an object having <code>get</code> and <code>opt</code>
 * methods for accessing the values by index, and <code>element</code> methods
 * for adding or replacing values. The values can be any of these types:
 * <code>Boolean</code>, <code>JSONArray</code>, <code>JSONObject</code>,
 * <code>Number</code>, <code>String</code>, or the
 * <code>JSONNull object</code>.
 * <p>
 * The constructor can convert a JSON text into a Java object. The
 * <code>toString</code> method converts to JSON text.
 * <p>
 * A <code>get</code> method returns a value if one can be found, and throws
 * an exception if one cannot be found. An <code>opt</code> method returns a
 * default value instead of throwing an exception, and so is useful for
 * obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an
 * object which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking and
 * type coersion for you.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to
 * JSON syntax rules. The constructors are more forgiving in the texts they will
 * accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just
 * before the closing bracket.</li>
 * <li>The <code>null</code> value will be inserted when there is
 * <code>,</code>&nbsp;<small>(comma)</small> elision.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a
 * quote or single quote, and if they do not contain leading or trailing spaces,
 * and if they do not contain any of these characters:
 * <code>{ } [ ] / \ : , = ; #</code> and if they do not look like numbers and
 * if they are not the reserved words <code>true</code>, <code>false</code>,
 * or <code>null</code>.</li>
 * <li>Values can be separated by <code>;</code> <small>(semicolon)</small>
 * as well as by <code>,</code> <small>(comma)</small>.</li>
 * <li>Numbers may have the <code>0-</code> <small>(octal)</small> or
 * <code>0x-</code> <small>(hex)</small> prefix.</li>
 * <li>Comments written in the slashshlash, slashstar, and hash conventions
 * will be ignored.</li>
 * </ul>
 * 
 * @author JSON.org
 */
@SuppressWarnings("unchecked")
public final class ConvertArray implements Convert, List {

	/**
	 * Creates a JSONArray.<br>
	 * Inspects the object type to call the correct JSONArray factory method.
	 * Accepts JSON formatted strings, arrays, Collections and Enums.
	 * 
	 * @param object
	 * @throws ConvertException
	 *             if the object can not be converted to a proper JSONArray.
	 */
	public static ConvertArray fromObject(Object object) {
		if (object instanceof Collection) {
			return _fromCollection((Collection) object);
		} else if (object != null && object.getClass().isArray()) {
			Class type = object.getClass().getComponentType();
			if (!type.isPrimitive()) {
				return _fromArray((Object[]) object);
			} else {
				if (type == Boolean.TYPE) {
					return _fromArray((boolean[]) object);
				} else if (type == Byte.TYPE) {
					return _fromArray((byte[]) object);
				} else if (type == Short.TYPE) {
					return _fromArray((short[]) object);
				} else if (type == Integer.TYPE) {
					return _fromArray((int[]) object);
				} else if (type == Long.TYPE) {
					return _fromArray((long[]) object);
				} else if (type == Float.TYPE) {
					return _fromArray((float[]) object);
				} else if (type == Double.TYPE) {
					return _fromArray((double[]) object);
				} else if (type == Character.TYPE) {
					return _fromArray((char[]) object);
				} else {
					throw new ConvertException("Unsupported type");
				}
			}
		} else if (ConvertUtils.isBoolean(object) || ConvertUtils.isNumber(object)
				|| ConvertUtils.isNull(object) || ConvertUtils.isString(object)
				|| object instanceof Convert) {

			ConvertArray jsonArray = new ConvertArray().element(object);

			return jsonArray;
		} else {
			ConvertArray jsonArray = new ConvertArray().element(ConvertObject
					.fromObject(object));

			return jsonArray;
		}
	}

	

	/**
	 * Construct a JSONArray from an boolean[].<br>
	 * 
	 * @param array
	 *            An boolean[] array.
	 */
	private static ConvertArray _fromArray(boolean[] array) {

		ConvertArray jsonArray = new ConvertArray();
		for (int i = 0; i < array.length; i++) {
			Boolean b = array[i] ? Boolean.TRUE : Boolean.FALSE;
			jsonArray.elements.add(b);

		}

		return jsonArray;
	}

	/**
	 * Construct a JSONArray from an byte[].<br>
	 * 
	 * @param array
	 *            An byte[] array.
	 */
	private static ConvertArray _fromArray(byte[] array) {

		ConvertArray jsonArray = new ConvertArray();
		for (int i = 0; i < array.length; i++) {
			Number n = ConvertUtils.transformNumber(new Byte(array[i]));
			jsonArray.elements.add(n);
		}

		return jsonArray;
	}

	/**
	 * Construct a JSONArray from an char[].<br>
	 * 
	 * @param array
	 *            An char[] array.
	 */
	private static ConvertArray _fromArray(char[] array) {

		ConvertArray jsonArray = new ConvertArray();
		for (int i = 0; i < array.length; i++) {
			Character c = new Character(array[i]);
			jsonArray.elements.add(c);

		}

		return jsonArray;
	}

	/**
	 * Construct a JSONArray from an double[].<br>
	 * 
	 * @param array
	 *            An double[] array.
	 */
	private static ConvertArray _fromArray(double[] array) {

		ConvertArray jsonArray = new ConvertArray();
		try {
			for (int i = 0; i < array.length; i++) {
				Double d = new Double(array[i]);
				ConvertUtils.testValidity(d);
				jsonArray.elements.add(d);

			}
		} catch (ConvertException jsone) {

			throw jsone;
		}

		return jsonArray;
	}

	/**
	 * Construct a JSONArray from an Enum value.
	 * 
	 * @param e
	 *            A enum value.
	 * @throws ConvertException
	 *             If there is a syntax error.
	 */
	private static ConvertArray _fromArray(Enum e) {

		ConvertArray jsonArray = new ConvertArray();
		if (e != null) {
			jsonArray.elements.add(e.toString());

		} else {
			ConvertException jsone = new ConvertException("enum value is null");

			throw jsone;
		}

		return jsonArray;
	}

	/**
	 * Construct a JSONArray from an float[].<br>
	 * 
	 * @param array
	 *            An float[] array.
	 */
	private static ConvertArray _fromArray(float[] array) {

		ConvertArray jsonArray = new ConvertArray();
		try {
			for (int i = 0; i < array.length; i++) {
				Float f = new Float(array[i]);
				ConvertUtils.testValidity(f);
				jsonArray.elements.add(f);
			}
		} catch (ConvertException jsone) {

			throw jsone;
		}

		return jsonArray;
	}

	/**
	 * Construct a JSONArray from an int[].<br>
	 * 
	 * @param array
	 *            An int[] array.
	 */
	private static ConvertArray _fromArray(int[] array) {

		ConvertArray jsonArray = new ConvertArray();
		for (int i = 0; i < array.length; i++) {
			Number n = new Integer(array[i]);
			jsonArray.elements.add(n);

		}

		return jsonArray;
	}

	/**
	 * Construct a JSONArray from an long[].<br>
	 * 
	 * @param array
	 *            An long[] array.
	 */
	private static ConvertArray _fromArray(long[] array) {

		ConvertArray jsonArray = new ConvertArray();
		for (int i = 0; i < array.length; i++) {
			Number n = ConvertUtils.transformNumber(new Long(array[i]));
			jsonArray.elements.add(n);

		}

		return jsonArray;
	}

	// ------------------------------------------------------

	private static ConvertArray _fromArray(Object[] array) {

		ConvertArray jsonArray = new ConvertArray();
		try {
			for (int i = 0; i < array.length; i++) {
				Object element = array[i];
				jsonArray.addValue(element);

			}
		} catch (ConvertException jsone) {

			throw jsone;
		} catch (RuntimeException e) {

			ConvertException jsone = new ConvertException(e);

			throw jsone;
		}

		return jsonArray;
	}

	/**
	 * Construct a JSONArray from an short[].<br>
	 * 
	 * @param array
	 *            An short[] array.
	 */
	private static ConvertArray _fromArray(short[] array) {

		ConvertArray jsonArray = new ConvertArray();
		for (int i = 0; i < array.length; i++) {
			Number n = ConvertUtils.transformNumber(new Short(array[i]));
			jsonArray.elements.add(n);

		}

		return jsonArray;
	}

	private static ConvertArray _fromCollection(Collection collection) {

		ConvertArray jsonArray = new ConvertArray();
		try {
			int i = 0;
			for (Iterator elements = collection.iterator(); elements.hasNext();) {
				Object element = elements.next();
				jsonArray.addValue(element);

			}
		} catch (ConvertException jsone) {

			throw jsone;
		} catch (RuntimeException e) {

			ConvertException jsone = new ConvertException(e);

			throw jsone;
		}

		return jsonArray;
	}

	/**
	 * The List where the JSONArray's properties are kept.
	 */
	private List elements;

	/**
	 * A flag for XML processing.
	 */
	private boolean expandElements = false;

	/**
	 * Construct an empty JSONArray.
	 */
	public ConvertArray() {
		this.elements = new ArrayList();
	}

	public void add(int index, Object value) {
		this.elements.add(index, processValue(value));
	}

	public boolean add(Object value) {
		element(value);
		return true;
	}

	public boolean addAll(Collection collection) {
		if (collection == null || collection.size() == 0) {
			return false;
		}
		for (Iterator i = collection.iterator(); i.hasNext();) {
			element(i.next());
		}
		return true;
	}

	public boolean addAll(int index, Collection collection) {
		if (collection == null || collection.size() == 0) {
			return false;
		}
		int offset = 0;
		for (Iterator i = collection.iterator(); i.hasNext();) {
			this.elements.add(index + (offset++), processValue(i.next()));
		}
		return true;
	}

	public void clear() {
		elements.clear();
	}

	public boolean contains(Object o) {
		return elements.contains(processValue(o));
	}

	public boolean containsAll(Collection collection) {
		return elements.containsAll(fromObject(collection));
	}

	/**
	 * Remove an element, if present.
	 * 
	 * @param index
	 *            the index of the element.
	 * @return this.
	 */
	public ConvertArray discard(int index) {
		elements.remove(index);
		return this;
	}

	/**
	 * Remove an element, if present.
	 * 
	 * @param index
	 *            the element.
	 * @return this.
	 */
	public ConvertArray discard(Object o) {
		elements.remove(o);
		return this;
	}

	/**
	 * Append a boolean value. This increases the array's length by one.
	 * 
	 * @param value
	 *            A boolean value.
	 * @return this.
	 */
	public ConvertArray element(boolean value) {
		return element(value ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Append a value in the JSONArray, where the value will be a JSONArray
	 * which is produced from a Collection.
	 * 
	 * @param value
	 *            A Collection value.
	 * @return this.
	 */
	public ConvertArray element(Collection value) {
		if (value instanceof ConvertArray) {
			elements.add(value);
			return this;
		} else {
			return element(_fromCollection(value));
		}
	}

	/**
	 * Append a double value. This increases the array's length by one.
	 * 
	 * @param value
	 *            A double value.
	 * @throws ConvertException
	 *             if the value is not finite.
	 * @return this.
	 */
	public ConvertArray element(double value) {
		Double d = new Double(value);
		ConvertUtils.testValidity(d);
		return element(d);
	}

	/**
	 * Append an int value. This increases the array's length by one.
	 * 
	 * @param value
	 *            An int value.
	 * @return this.
	 */
	public ConvertArray element(int value) {
		return element(new Integer(value));
	}

	/**
	 * Put or replace a boolean value in the JSONArray. If the index is greater
	 * than the length of the JSONArray, then null elements will be added as
	 * necessary to pad it out.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            A boolean value.
	 * @return this.
	 * @throws ConvertException
	 *             If the index is negative.
	 */
	public ConvertArray element(int index, boolean value) {
		return element(index, value ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Put a value in the JSONArray, where the value will be a JSONArray which
	 * is produced from a Collection.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            A Collection value.
	 * @return this.
	 * @throws ConvertException
	 *             If the index is negative or if the value is not finite.
	 */
	public ConvertArray element(int index, Collection value) {
		if (value instanceof ConvertArray) {
			if (index < 0) {
				throw new ConvertException("JSONArray[" + index + "] not found.");
			}
			if (index < size()) {
				elements.set(index, value);
			} else {
				while (index != size()) {
					element(ConvertNull.getInstance());
				}
				element(value);
			}
			return this;
		} else {
			return element(index, _fromCollection(value));
		}
	}

	/**
	 * Put or replace a double value. If the index is greater than the length of
	 * the JSONArray, then null elements will be added as necessary to pad it
	 * out.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            A double value.
	 * @return this.
	 * @throws ConvertException
	 *             If the index is negative or if the value is not finite.
	 */
	public ConvertArray element(int index, double value) {
		return element(index, new Double(value));
	}

	/**
	 * Put or replace an int value. If the index is greater than the length of
	 * the JSONArray, then null elements will be added as necessary to pad it
	 * out.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            An int value.
	 * @return this.
	 * @throws ConvertException
	 *             If the index is negative.
	 */
	public ConvertArray element(int index, int value) {
		return element(index, new Integer(value));
	}

	/**
	 * Put or replace a long value. If the index is greater than the length of
	 * the JSONArray, then null elements will be added as necessary to pad it
	 * out.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            A long value.
	 * @return this.
	 * @throws ConvertException
	 *             If the index is negative.
	 */
	public ConvertArray element(int index, long value) {
		return element(index, new Long(value));
	}

	/**
	 * Put a value in the JSONArray, where the value will be a JSONObject which
	 * is produced from a Map.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            The Map value.
	 * @return this.
	 * @throws ConvertException
	 *             If the index is negative or if the the value is an invalid
	 *             number.
	 */
	public ConvertArray element(int index, Map value) {
		if (value instanceof ConvertObject) {
			if (index < 0) {
				throw new ConvertException("JSONArray[" + index + "] not found.");
			}
			if (index < size()) {
				elements.set(index, value);
			} else {
				while (index != size()) {
					element(ConvertNull.getInstance());
				}
				element(value);
			}
			return this;
		} else {
			return element(index, ConvertObject.fromObject(value));
		}
	}

	/**
	 * Put or replace an object value in the JSONArray. If the index is greater
	 * than the length of the JSONArray, then null elements will be added as
	 * necessary to pad it out.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            An object value. The value should be a Boolean, Double,
	 *            Integer, JSONArray, JSONObject, JSONFunction, Long, String,
	 *            JSONString or the JSONNull object.
	 * @return this.
	 * @throws ConvertException
	 *             If the index is negative or if the the value is an invalid
	 *             number.
	 */
	public ConvertArray element(int index, Object value) {
		ConvertUtils.testValidity(value);
		if (index < 0) {
			throw new ConvertException("JSONArray[" + index + "] not found.");
		}
		if (index < size()) {
			this.elements.set(index, processValue(value));
		} else {
			while (index != size()) {
				element(ConvertNull.getInstance());
			}
			element(value);
		}
		return this;
	}

	/**
	 * Put or replace a String value in the JSONArray. If the index is greater
	 * than the length of the JSONArray, then null elements will be added as
	 * necessary to pad it out.<br>
	 * The string may be a valid JSON formatted string, in tha case, it will be
	 * trabsformed to a JSONArray, JSONObjetc or JSONNull.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            A String value.
	 * @return this.
	 * @throws ConvertException
	 *             If the index is negative or if the the value is an invalid
	 *             number.
	 */
	public ConvertArray element(int index, String value) {
		if (index < 0) {
			throw new ConvertException("JSONArray[" + index + "] not found.");
		}
		if (index < size()) {
			if (value == null) {
				this.elements.set(index, "");
			} else {
				this.elements.set(index, ConvertUtils.stripQuotes(value));
			}
		} else {
			while (index != size()) {
				element(ConvertNull.getInstance());
			}
			element(value);
		}
		return this;
	}

	/**
	 * Append an JSON value. This increases the array's length by one.
	 * 
	 * @param value
	 *            An JSON value.
	 * @return this.
	 */
	public ConvertArray element(ConvertNull value) {
		this.elements.add(value);
		return this;
	}

	/**
	 * Append an JSON value. This increases the array's length by one.
	 * 
	 * @param value
	 *            An JSON value.
	 * @return this.
	 */
	public ConvertArray element(ConvertObject value) {
		this.elements.add(value);
		return this;
	}

	/**
	 * Append an long value. This increases the array's length by one.
	 * 
	 * @param value
	 *            A long value.
	 * @return this.
	 */
	public ConvertArray element(long value) {
		return element(ConvertUtils.transformNumber(new Long(value)));
	}

	/**
	 * Put a value in the JSONArray, where the value will be a JSONObject which
	 * is produced from a Map.
	 * 
	 * @param value
	 *            A Map value.
	 * @return this.
	 */
	public ConvertArray element(Map value) {
		if (value instanceof ConvertObject) {
			elements.add(value);
			return this;
		} else {
			return element(ConvertObject.fromObject(value));
		}
	}

	/**
	 * Append an object value. This increases the array's length by one.
	 * 
	 * @param value
	 *            An object value. The value should be a Boolean, Double,
	 *            Integer, JSONArray, JSONObject, JSONFunction, Long, String,
	 *            JSONString or the JSONNull object.
	 * @return this.
	 */
	public ConvertArray element(Object value) {
		return addValue(value);
	}

	/**
	 * Append a String value. This increases the array's length by one.<br>
	 * The string may be a valid JSON formatted string, in tha case, it will be
	 * trabsformed to a JSONArray, JSONObjetc or JSONNull.
	 * 
	 * @param value
	 *            A String value.
	 * @return this.
	 */
	public ConvertArray element(String value) {
		if (value == null) {
			this.elements.add("");
		} else {
			this.elements.add(ConvertUtils.stripQuotes(value));
		}
		return this;
	}

	/**
	 * Get the object value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and size() - 1.
	 * @return An object value.
	 */
	public Object get(int index) {
		/*
		 * Object o = opt( index ); if( o == null ){ throw new JSONException(
		 * "JSONArray[" + index + "] not found." ); } return o;
		 */
		return this.elements.get(index);
	}

	/**
	 * Get the boolean value associated with an index. The string values "true"
	 * and "false" are converted to boolean.
	 * 
	 * @param index
	 *            The index must be between 0 and size() - 1.
	 * @return The truth.
	 * @throws ConvertException
	 *             If there is no value for the index or if the value is not
	 *             convertable to boolean.
	 */
	public boolean getBoolean(int index) {
		Object o = get(index);
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
		throw new ConvertException("JSONArray[" + index + "] is not a Boolean.");
	}

	/**
	 * Get the double value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and size() - 1.
	 * @return The value.
	 * @throws ConvertException
	 *             If the key is not found or if the value cannot be converted
	 *             to a number.
	 */
	public double getDouble(int index) {
		Object o = get(index);
		if (o != null) {
			try {
				return o instanceof Number ? ((Number) o).doubleValue()
						: Double.parseDouble((String) o);
			} catch (Exception e) {
				throw new ConvertException("JSONArray[" + index
						+ "] is not a number.");
			}
		}
		throw new ConvertException("JSONArray[" + index + "] is not a number.");
	}

	/**
	 * Get the int value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and size() - 1.
	 * @return The value.
	 * @throws ConvertException
	 *             If the key is not found or if the value cannot be converted
	 *             to a number. if the value cannot be converted to a number.
	 */
	public int getInt(int index) {
		Object o = get(index);
		if (o != null) {
			return o instanceof Number ? ((Number) o).intValue()
					: (int) getDouble(index);
		}
		throw new ConvertException("JSONArray[" + index + "] is not a number.");
	}

	/**
	 * Get the JSONArray associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and size() - 1.
	 * @return A JSONArray value.
	 * @throws ConvertException
	 *             If there is no value for the index. or if the value is not a
	 *             JSONArray
	 */
	public ConvertArray getJSONArray(int index) {
		Object o = get(index);
		if (o != null && o instanceof ConvertArray) {
			return (ConvertArray) o;
		}
		throw new ConvertException("JSONArray[" + index + "] is not a JSONArray.");
	}

	/**
	 * Get the JSONObject associated with an index.
	 * 
	 * @param index
	 *            subscript
	 * @return A JSONObject value.
	 * @throws ConvertException
	 *             If there is no value for the index or if the value is not a
	 *             JSONObject
	 */
	public ConvertObject getJSONObject(int index) {
		Object o = get(index);
		if (ConvertNull.getInstance().equals(o)) {
			return new ConvertObject(true);
		} else if (o instanceof ConvertObject) {
			return (ConvertObject) o;
		}
		throw new ConvertException("JSONArray[" + index + "] is not a JSONObject.");
	}

	/**
	 * Get the long value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and size() - 1.
	 * @return The value.
	 * @throws ConvertException
	 *             If the key is not found or if the value cannot be converted
	 *             to a number.
	 */
	public long getLong(int index) {
		Object o = get(index);
		if (o != null) {
			return o instanceof Number ? ((Number) o).longValue()
					: (long) getDouble(index);
		}
		throw new ConvertException("JSONArray[" + index + "] is not a number.");
	}

	/**
	 * Get the string associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and size() - 1.
	 * @return A string value.
	 * @throws ConvertException
	 *             If there is no value for the index.
	 */
	public String getString(int index) {
		Object o = get(index);
		if (o != null) {
			return o.toString();
		}
		throw new ConvertException("JSONArray[" + index + "] not found.");
	}

	public int hashCode() {
		int hashcode = 29;

		for (Iterator e = elements.iterator(); e.hasNext();) {
			Object element = e.next();
			hashcode += ConvertUtils.hashCode(element);
		}
		return hashcode;
	}

	public int indexOf(Object o) {
		return elements.indexOf(o);
	}

	public boolean isArray() {
		return true;
	}

	public boolean isEmpty() {
		return this.elements.isEmpty();
	}

	public boolean isExpandElements() {
		return expandElements;
	}

	/**
	 * Returns an Iterator for this JSONArray
	 */
	public Iterator iterator() {
		return this.elements.iterator();
	}

	/**
	 * Make a string from the contents of this JSONArray. The
	 * <code>separator</code> string is inserted between each element.
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @param separator
	 *            A string that will be inserted between the elements.
	 * @return a string.
	 * @throws ConvertException
	 *             If the array contains an invalid number.
	 */
	public String join(String separator) {
		return join(separator, false);
	}

	/**
	 * Make a string from the contents of this JSONArray. The
	 * <code>separator</code> string is inserted between each element.
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @param separator
	 *            A string that will be inserted between the elements.
	 * @return a string.
	 * @throws ConvertException
	 *             If the array contains an invalid number.
	 */
	public String join(String separator, boolean stripQuotes) {
		int len = size();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < len; i += 1) {
			if (i > 0) {
				sb.append(separator);
			}
			String value = this.elements.get(i).toString();
			sb.append(stripQuotes ? ConvertUtils.stripQuotes(value) : value);
		}
		return sb.toString();
	}

	public int lastIndexOf(Object o) {
		return elements.lastIndexOf(o);
	}

	public ListIterator listIterator() {
		return elements.listIterator();
	}

	public ListIterator listIterator(int index) {
		return elements.listIterator(index);
	}

	public Object remove(int index) {
		return elements.remove(index);
	}

	public boolean remove(Object o) {
		return elements.remove(o);
	}

	public boolean removeAll(Collection collection) {
		return elements.removeAll(fromObject(collection));
	}

	public boolean retainAll(Collection collection) {
		return elements.retainAll(fromObject(collection));
	}

	public Object set(int index, Object value) {
		Object previous = get(index);
		element(index, value);
		return previous;
	}

	public void setExpandElements(boolean expandElements) {
		this.expandElements = expandElements;
	}

	/**
	 * Get the number of elements in the JSONArray, included nulls.
	 * 
	 * @return The length (or size).
	 */
	public int size() {
		return this.elements.size();
	}

	public List subList(int fromIndex, int toIndex) {
		return elements.subList(fromIndex, toIndex);
	}

	/**
	 * Produce an Object[] with the contents of this JSONArray.
	 */
	public Object[] toArray() {
		return this.elements.toArray();
	}

	public Object[] toArray(Object[] array) {
		return elements.toArray(array);
	}

	/**
	 * Make a JSON text of this JSONArray. For compactness, no unnecessary
	 * whitespace is added. If it is not possible to produce a syntactically
	 * correct JSON text then null will be returned instead. This could occur if
	 * the array contains an invalid number.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @return a printable, displayable, transmittable representation of the
	 *         array.
	 */
	public String toString() {
		try {
			return '[' + join(",") + ']';
		} catch (Exception e) {
			return null;
		}
	}

	
	/**
	 * Make a prettyprinted JSON text of this JSONArray. Warning: This method
	 * assumes that the data structure is acyclical.
	 * 
	 * @param indentFactor
	 *            The number of spaces to add to each level of indentation.
	 * @param indent
	 *            The indention of the top level.
	 * @return a printable, displayable, transmittable representation of the
	 *         array.
	 * @throws ConvertException
	 */
	public String toString(int indentFactor, int indent) {
		int len = size();
		if (len == 0) {
			return "[]";
		}
		if (indentFactor == 0) {
			return this.toString();
		}
		int i;
		StringBuffer sb = new StringBuffer("[");
		if (len == 1) {
			sb.append(ConvertUtils.valueToString(this.elements.get(0),
					indentFactor, indent));
		} else {
			int newindent = indent + indentFactor;
			sb.append('\n');
			for (i = 0; i < len; i += 1) {
				if (i > 0) {
					sb.append(",\n");
				}
				for (int j = 0; j < newindent; j += 1) {
					sb.append(' ');
				}
				sb.append(ConvertUtils.valueToString(this.elements.get(i),
						indentFactor, newindent));
			}
			sb.append('\n');
			for (i = 0; i < indent; i += 1) {
				sb.append(' ');
			}
			for (i = 0; i < indent; i += 1) {
				sb.insert(0, ' ');
			}
		}
		sb.append(']');
		return sb.toString();
	}

	/**
	 * Write the contents of the JSONArray as JSON text to a writer. For
	 * compactness, no whitespace is added.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @return The writer.
	 * @throws ConvertException
	 */
	public Writer write(Writer writer) {
		try {
			int len = size();
            if(ConvertUtils.isExpandElements()){
            	writerJson(writer,len);
            }else{
            	writerXml(writer,len);
            }
			
			return writer;
		} catch (IOException e) {
			throw new ConvertException(e);
		}
	}
	
	
    /**
     * json 格式
     * @author liupo
     * @param writer
     * @param len
     * @throws IOException
     */
	private void writerJson(Writer writer,int len)throws IOException{
		writer.write('[');
		boolean b = false;
		for (int i = 0; i < len; i += 1) {
			if (b) {
				writer.write(',');
			}
			Object v = this.elements.get(i);
			if (v instanceof ConvertObject) {
				((ConvertObject) v).write(writer);
			} else if (v instanceof ConvertArray) {
				((ConvertArray) v).write(writer);
			} else {
				writer.write(v.toString());
			}
			b = true;
		}
		writer.write(']');
	}
    /**
     * XML 格式
     * @author liupo
     * @param writer
     * @param len
     * @throws IOException
     */
	private void writerXml(Writer writer,int len)throws IOException{
		boolean b = false;
		for (int i = 0; i < len; i += 1) {
			Object v = this.elements.get(i);
			if (b && ConvertUtils.isXMLEscape(v.toString())) {
				writer.write(',');
			}
			
			if (v instanceof ConvertObject) {
				((ConvertObject) v).write(writer);
			} else if (v instanceof ConvertArray) {
				((ConvertArray) v).write(writer);
			} else {

				writer.write(v.toString());

			}		
			b = true;
		}
	}
	
	/**
	 * Adds a String without performing any conversion on it.
	 */
	protected ConvertArray addString(String str) {
		if (str != null) {
			elements.add(str);
		}
		return this;
	}

	/**
	 * Append an object value. This increases the array's length by one.
	 * 
	 * @param value
	 *            An object value. The value should be a Boolean, Double,
	 *            Integer, JSONArray, JSONObject, JSONFunction, Long, String,
	 *            JSONString or the JSONNull object.
	 * @return this.
	 */
	
	private ConvertArray _addValue(Object value) {

		this.elements.add(value);
		return this;
	}

	private Object _processValue(Object value) {
		if ((value != null && Class.class.isAssignableFrom(value.getClass()))
				|| value instanceof Class) {
			return ((Class) value).getName();
		} else if (ConvertUtils.isArray(value)) {
			return ConvertArray.fromObject(value);
		} else if (ConvertUtils.isString(value)) {
			return String.valueOf(value);

		} else if (ConvertUtils.isNumber(value)) {
			ConvertUtils.testValidity(value);
			return ConvertUtils.transformNumber((Number) value);
		} else if (ConvertUtils.isBoolean(value)) {
			return value;
		} else {
			
			ConvertObject jsonObject = ConvertObject.fromObject(value);
			jsonObject.setObjectName(ConvertUtils.getClassName(value.getClass().getName()).toLowerCase());
			if (jsonObject.isNullObject()) {
				return ConvertNull.getInstance();
			} else {
				return jsonObject;
			}
		}
	}

	/**
	 * Append an object value. This increases the array's length by one.
	 * 
	 * @param value
	 *            An object value. The value should be a Boolean, Double,
	 *            Integer, JSONArray, JSONObject, JSONFunction, Long, String,
	 *            JSONString or the JSONNull object.
	 * @return this.
	 */
	private ConvertArray addValue(Object value) {
		return _addValue(processValue(value));
	}

	private Object processValue(Object value) {
		if (value != null) {
			/*
			 * JsonValueProcessor jsonValueProcessor =
			 * jsonConfig.findJsonValueProcessor( value.getClass() ); if(
			 * jsonValueProcessor != null ){ value =
			 * jsonValueProcessor.processArrayValue( value, jsonConfig ); if(
			 * !JsonVerifier.isValidJsonValue( value ) ){ throw new
			 * JSONException( "Value is not a valid JSON value. " + value ); } }
			 */
		}
		return _processValue(value);
	}

	public List getElements() {
		return elements;
	}

}
