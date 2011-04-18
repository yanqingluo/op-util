package com.taobao.top.common.encrypt.test;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.taobao.top.common.encrypt.Base64;

public class EncryptTest {

	@Before
	public void setUp() throws Exception {
	}
    @Test
    public void testDES(){
    	String str="haha";
    	str=Base64.encode(str.getBytes());
    	str=new String(Base64.decode(str));
    	Assert.assertEquals(str, "haha");
    }
}
