/**
 * 
 */
package com.taobao.top.common.lang;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @version 2009-2-27
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public class MapKitTest {

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
	 * {@link com.taobao.top.common.lang.MapKit#dumpMap(java.util.Map)}.
	 */
	@Test
	public void testDumpMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("arr", new String[] { "a1", "a2" });
		map.put("str", "str");
		System.out.println(MapKit.dumpMap(map));
	}

}
