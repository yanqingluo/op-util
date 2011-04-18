package com.taobao.top.common.test;


import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.taobao.top.common.cache.aop.LocalCacheImpl;
/**
 * 
 * @author zhenzi
 *
 */
public class LocalCacheImplTest {
	private LocalCacheImpl localCache = new LocalCacheImpl();
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	/*
	 * 测试有过期时间的 ,ttl
	 */
	@Test
	public void test_has_expired_time_ttl(){
		localCache.put("key1", "value1", 3);//10秒
		assertEquals("value1",localCache.get("key1"));
		try{
			Thread.sleep(3100);
		}catch(Exception e){
			
		}
		assertNull(localCache.get("key1"));
	}
	/*
	 * 测试有过期时间的，date
	 */
	@Test
	public void test_has_expired_time_date(){
		localCache.put("key2", "value2", new Date(System.currentTimeMillis() + 1 * 1000));
		assertEquals("value2",localCache.get("key2"));
		try{
			Thread.sleep(2000);
		}catch(Exception e){
			
		}
		assertNull(localCache.get("key2"));
	}
	@Test
	public void test_has_no_expired_time(){
		localCache.put("key3", "value3");
		assertEquals("value3",localCache.get("key3"));
	}
	@Test
	public void test_clear(){
		localCache.put("key3", "value3");
		assertEquals("value3",localCache.get("key3"));
		assertTrue(localCache.clear());
		assertNull(localCache.get("key3"));
	}
	@Test
	public void test_containerKey(){
		localCache.put("key3", "value3");
		assertTrue(localCache.containsKey("key3"));
	}
	@Test
	public void test_size(){
		localCache.clear();
		localCache.put("key3", "value3");
		assertEquals(1,localCache.size());
	}
	@Test
	public void test_remove(){
		localCache.clear();
		localCache.put("key4", "value4");
		localCache.put("key5", "value5");
		localCache.remove("key4");
		assertNull(localCache.get("key4"));
		assertEquals("value5",localCache.get("key5"));
	}
	@Test
	public void test_ket_set(){
		localCache.clear();
		localCache.put("key4", "value4");
		localCache.put("key5", "value5");
		Set<String> set = localCache.keySet();
		assertTrue(set.contains("key4"));
		assertEquals(2,set.size());
	}
	@Test
	public void test_collect(){
		localCache.clear();
		localCache.put("key4", "value4");
		localCache.put("key5", "value5");
		Collection<Object> value = localCache.values();
		assertEquals(2,value.size());
		assertTrue(value.contains("value4"));
	}
	@Test
	public void test_destroy(){
		localCache.clear();
		localCache.put("key4", "value4");
		localCache.put("key5", "value5");
		localCache.destroy();
		assertNull(localCache.get("key4"));
	}
}
