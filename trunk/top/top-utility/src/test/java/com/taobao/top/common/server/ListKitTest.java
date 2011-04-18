package com.taobao.top.common.server;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * 
 * @author zhenzi
 *
 */
public class ListKitTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testContains(){
		assertTrue(ListKit.contains(null, "in") == false);
		assertTrue(ListKit.contains(new String[]{}, "in") == false);
		assertTrue(ListKit.contains(new String[]{"aa"}, null) == false);
		assertTrue(ListKit.contains(new String[]{"aa"}, "bb") == false);
		
		assertTrue(ListKit.contains(new String[]{"aa"}, "aa"));
	}
	@Test
	public void testRemove(){
		assertNull(ListKit.removes(null, new String[]{"aa"}));
		assertEquals(0,ListKit.removes(new ArrayList<String>(), new String[]{"aa"}).size());
		List<String> l = new ArrayList<String>();
		l.add("s");
		assertEquals(1,ListKit.removes(l, null).size());
		assertEquals(1,ListKit.removes(l, new String[]{}).size());
		
		l.add("aa");
		l.add("bb");
		List<String> r = ListKit.removes(l, new String[]{"aa"});
		assertEquals(2,r.size());
		assertTrue(r.contains("bb"));
		assertFalse(r.contains("aa"));
	}
}
