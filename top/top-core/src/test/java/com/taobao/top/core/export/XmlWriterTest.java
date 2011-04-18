/**
 * 
 */
package com.taobao.top.core.export;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.taobao.top.impl.core.export.XmlWriter;

/**
 * @version 2009-3-19
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public class XmlWriterTest {
	private XmlWriter jw = new XmlWriter();

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

	@Test
	public void testWriteMap() throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "alin");
		map.put("null", null);
		map.put("boo", true);
		StringWriter sw = new StringWriter();
		jw.write(sw, map);
		assertEquals(
				"<boo><![CDATA[true]]></boo><name><![CDATA[alin]]></name>", sw
						.toString());
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

	@Test
	public void testWriteMapList() throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("album", "001");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.add(map);
		Map<String, Object> all = new HashMap<String, Object>();
		all.put("lists", list);
		all.put("totalResults", 1);
		StringWriter sw = new StringWriter();
		jw.write(sw, all);
		System.out.println(sw.toString());
		assertEquals(
				"<totalResults><![CDATA[1]]></totalResults><album><![CDATA[001]]></album>",
				sw.toString());

	}

}
