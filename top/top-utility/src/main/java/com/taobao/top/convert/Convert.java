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

import java.io.Writer;

/**
 * Marker interface, identifies a valid JSON value.<br>
 * A JSON value may be a {@link ConvertObject}, a {@link ConvertArray} or a
 * {@link ConvertNull}.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public interface Convert {
   /**
    * Returns true if this object is a JSONArray, false otherwise.
    */
   boolean isArray();

   /**
    * Returns true if this object has no elements or keys.
    *
    * @throws ConvertException if called on a 'null' object
    */
   boolean isEmpty();

   /**
    * Returns the number of properties in an object or the size of the array.
    *
    * @return the size of an json object or array
    * @throws ConvertException if called on a 'null' object
    */
   int size();

  

   /**
    * Make a prettyprinted JSON text.
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    *
    * @param indentFactor The number of spaces to add to each level of
    *        indentation.
    * @param indent The indentation of the top level.
    * @return a printable, displayable, transmittable representation of the
    *         object, beginning with <code>{</code>&nbsp;<small>(left brace)</small>
    *         and ending with <code>}</code>&nbsp;<small>(right brace)</small>.
    * @throws ConvertException If the object contains an invalid number.
    */
   String toString( int indentFactor, int indent );

   /**
    * Write the contents as JSON text to a writer. For compactness, no
    * whitespace is added.
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    *
    * @return The writer.
    * @throws ConvertException
    */
   Writer write( Writer writer );
}