//===================================================================================
// Copyright (c) 2008-2008 by www.TaoBao.com, All rights reserved.
//  391# wen'er road, HangZhou, China
// 
// This software is the confidential and proprietary information of 
// TaoBao.com, Inc. ("Confidential Information"). You shall not disclose 
// such Confidential Information and shall use it only in accordance 
// with the terms of the license agreement you entered into with TaoBao.com, Inc.
//===================================================================================
// File name: ImageUrlKit.java
// Author: liupo
// Date: 2008-7-25 ����04:47:31 
// Description: 	 
// 		��
// Function List: 	 
// 		1. ��
// History: 
// 		1. ��
//===================================================================================

package com.taobao.top.common.server;

import org.apache.commons.lang.StringUtils;

/**
 * 退款留言凭证转换图片相对地址，变成绝对地址
 * @author tianchong
 *
 */
public class RefundMessageUrlKit {
	// 匹配图片地址默认的正则表达式
	//prefixUrl="http://img.daily.taobaocdn.net/refund/T1KXdXXi5yt0L1upjX.jpg";
	//dailyUrl="img.daily.taobao.net";
	//onlineUrl="img.taobao.com";
	private final static String DEFAULT_ONLINE_PICTURL="img.taobao.com";
	private final static String preFix_http="http://";
	private final static String SEP="/";
	private final static String REFUND="refund";
	public static String perform(String prefixUrl, String dbUrl) {
		if (StringUtils.isNotEmpty(dbUrl) && dbUrl.length() > 2) {
			if(StringUtils.isBlank(prefixUrl)){
				prefixUrl = DEFAULT_ONLINE_PICTURL;
			}
			if(!prefixUrl.startsWith(preFix_http)){
				prefixUrl = preFix_http + prefixUrl;
			}
			int index = prefixUrl.indexOf(".");
			String target = prefixUrl.substring(0, index) + "0" + getRandomNumber() + prefixUrl.substring(index);
			target = StringUtils.replace(target, ".taobao.", ".taobaocdn.");
			if(!target.endsWith(SEP)){
				target = target + SEP;
			}
			return  target + REFUND + SEP + dbUrl;
		}
		return null;
	}

	private static int getRandomNumber() {
		// 返回1到8之间的随机数
		int r = (int) Math.round(Math.random() * 7 + 1);
		return r;
	}
}

