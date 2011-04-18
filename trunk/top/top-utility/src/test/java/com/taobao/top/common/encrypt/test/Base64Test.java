package com.taobao.top.common.encrypt.test;

import org.junit.Assert;
import org.junit.Test;

import com.taobao.top.common.encrypt.Base64;

public class Base64Test {

	@Test
	public void testEncode() {
		String data = "value=1+&value=2+&type=loginprofile&uid=%B2%BB%B4%E6%D4%DA%B5%C4%BB%A8%C3%FB";
		String result = Base64.encode(data.getBytes());
		System.out.println(result);
		Assert.assertEquals(data, new String(Base64.decode(result)));
	}
}
