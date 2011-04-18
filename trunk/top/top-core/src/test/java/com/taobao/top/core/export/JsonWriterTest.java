/**
 * 
 */
package com.taobao.top.core.export;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.taobao.top.impl.core.export.JsonWriter;

/**
 * @version 2009-2-19
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public class JsonWriterTest {
	private JsonWriter jw = new JsonWriter();

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
	 * {@link com.taobao.top.impl.core.export.JsonWriter#write(java.io.Writer, java.lang.Object)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testWriteMap() throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "alin");
		map.put("null", null);
		map.put("boo", true);
		StringWriter sw = new StringWriter();
		jw.write(sw, map);
		assertEquals("{\"boo\":true,\"name\":\"alin\"}", sw.toString());
	}

	@Test
	public void testWritePrimitive() throws IOException {
		StringWriter sw = new StringWriter();
		jw.write(sw, new Boolean(true));
		assertEquals("true", sw.toString());

		sw = new StringWriter();
		jw.write(sw, 1);
		assertEquals("1", sw.toString());

	}

}
