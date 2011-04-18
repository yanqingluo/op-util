package com.taobao.top.privilege;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author moling
 * @since 1.0, 2009-10-21
 */
public class WhiteListManagerImpTest {
	private WhiteListManagerImpl white;
	
	@Before
    public void setUp() throws Exception {
        white = new WhiteListManagerImpl();
        List<String> ips = new ArrayList<String>();
        List<String> appKeys = new ArrayList<String>();
        
        ips.add("192.168.*.*,172.19.*.*,172.23.*.*,172.24.*.*");
        ips.add("192.171.212.175");
        white.setWhiteIpList(ips);
        
        appKeys.add("12345");
        appKeys.add("54321");
        white.setWhiteAppList(appKeys);
    }
	
	@After
    public void tearDown() throws Exception {
    }
	
    @Test
    public void testAppKeyWhite(){
    	assertTrue(white.isAppWhite("12345"));
    	assertFalse(white.isAppWhite("123456"));
    	assertFalse(white.isAppWhite(""));
    }
    
    @Test
    public void testIpWhite(){
    	assertTrue(white.isIpWhite("192.168.2.2"));
    	assertFalse(white.isAppWhite("192.171.3.2"));
    	assertFalse(white.isAppWhite("172.11.*.*"));
    	assertFalse(white.isAppWhite("172.11"));
    	assertFalse(white.isAppWhite(""));
    }

}
