package com.taobao.top.timwrapper;


import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.top.common.cache.ICache;
import com.taobao.top.tim.domain.PlatformDO;
import com.taobao.top.tim.domain.TinyApiDO;
import com.taobao.top.tim.domain.TinyAppDO;
import com.taobao.top.tim.service.SamService;
/**
 * 
 * @author zhenzi
 *
 */
@Ignore
public class LocalCachedTIMServiceTest {
	private LocalCachedTIMService timService;
	private ICache<String,Object> cache;
	@Before
	public void setUp() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-tim-test.xml");
		SamService samService = (SamService) ctx.getBean("samService");
		timService = new LocalCachedTIMService();
		timService.setTimService(samService);
		List<Long> filterTag = new ArrayList<Long>();
		filterTag.add(new Long(8));
		timService.setFilterTags(filterTag);
		
		Class clazz = timService.getClass();
		Field field = clazz.getDeclaredField("cache");
		field.setAccessible(true);
		cache = (ICache<String, Object>) field.get(timService);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAppByID()throws Exception{
		TinyAppDO appDO = timService.getAppById(34794l);
		assertNotNull(appDO);
		assertNotNull(cache.get("APPID:34794"));
	}
	@Test
	public void testGetAppByID_no_local_cache()throws Exception{
		TinyAppDO appDO = timService.getAppById(75559l);
		assertNotNull(appDO);
		assertNull(cache.get("APPID:75559"));
	}
	@Test
	public void testGetAppByKey()throws Exception{
		TinyAppDO appDO = timService.getAppByKey("56698");
		assertNotNull(appDO);
		assertNotNull(cache.get("APPKEY:56698"));
	}
	@Test
	public void testGetAppByKey_no_local_cache()throws Exception{
		TinyAppDO appDO = timService.getAppByKey("71142");
		assertNotNull(appDO);
		assertNull(cache.get("APPKEY:71142"));
	}
	
	@Test
	public void testGetPlatformById()throws Exception{
		PlatformDO platformDO = timService.getPlatformById(2l);
		assertNotNull(platformDO);
		assertNotNull(cache.get("PLATFORM:2"));
	}
	@Test
	public void testGetValidApiByApiName()throws Exception{
		TinyApiDO apiDO = timService.getValidApiByApiName(null);
		assertNull(apiDO);
		apiDO = timService.getValidApiByApiName("taobao.time.get");
		assertNotNull(apiDO);
		assertNotNull(cache.get( "API:taobao.time.get"));
	}

}
