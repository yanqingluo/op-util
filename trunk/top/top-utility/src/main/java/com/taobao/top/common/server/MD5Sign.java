//===================================================================================
// Copyright (c) 2008-2008 by www.TaoBao.com, All rights reserved.
//  391# wen'er road, HangZhou, China
// 
// This software is the confidential and proprietary information of 
// TaoBao.com, Inc. ("Confidential Information"). You shall not disclose 
// such Confidential Information and shall use it only in accordance 
// with the terms of the license agreement you entered into with TaoBao.com, Inc.
//===================================================================================
// File name: MD5Sign.java
// Author: liupo
// Date: 2008-9-4 上午10:37:31 
// Description: 	 
// 		无
// Function List: 	 
// 		1. 无
// History: 
// 		1. 无
//===================================================================================

package com.taobao.top.common.server;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.alibaba.common.lang.StringUtil;

/**
 * MD5签名
 * 
 * @author liupo <liupo@taobao.com>
 * @version 1.0
 */

public class MD5Sign {
	/**
	 * 生成签名
	 * 
	 * @author liupo
	 * @param params
	 *            数组
	 * @param signName
	 *            需要剔除SIGN
	 * @param secret
	 *            签名前缀
	 * @return
	 */
	public static String signature(Map<String, String> params, String signName,
			String secret) {
		String result = null;
		if (params == null)
			return result;
		if (signName != null) {
			params.remove(signName);
		}
		Map<String, Object> treeMap = new TreeMap<String, Object>();
		treeMap.putAll(params);
		Iterator<String> iter = treeMap.keySet().iterator();
		StringBuffer orgin = new StringBuffer(secret);
		while (iter.hasNext()) {
			String name = (String) iter.next();
			orgin.append(name).append(params.get(name));
		}
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			result = byte2hex(md.digest(orgin.toString().getBytes("utf-8")));
		} catch (Exception ex) {
			throw new java.lang.RuntimeException("sign error !");
		}

		return result;
	}

	/**
	 * 
	 * 二行制转字符串
	 * 
	 * @param b
	 * 
	 * @return
	 * 
	 */
	public static String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs.append("0").append(stmp);
			else
				hs.append(stmp);
		}
		return hs.toString().toUpperCase();

	}

	/**
	 * 签名检验
	 * 
	 * @author liupo
	 * @param sign
	 * @param checkSign
	 * @return
	 */
	public static Boolean checkSign(String sign, String checkSign) {
		return StringUtil.equals(checkSign, sign.toLowerCase());
	}
}
