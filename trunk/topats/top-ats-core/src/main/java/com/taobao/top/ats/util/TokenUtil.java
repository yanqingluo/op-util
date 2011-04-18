package com.taobao.top.ats.util;

import java.security.MessageDigest;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;

import com.alibaba.common.lang.StringUtil;

public abstract class TokenUtil {

	public static String generateToken(int length) {
		StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		int range = buffer.length();
		for (int i = 0; i < length; i++) {
			sb.append(buffer.charAt(r.nextInt(range)));
		}
		return sb.toString();
	}

	public static String createDownLoadUrl(String downloadUrl, String secret,
			TreeMap<String, String> bizParams) throws Exception {
		long timestamp = new Date().getTime();
		TreeMap<String, String> tmap = new TreeMap<String, String>();
		tmap.putAll(bizParams);
		tmap.put("timestamp", String.valueOf(timestamp));
		tmap.put("sign", sign(secret, tmap));
		StringBuilder sb = new StringBuilder(downloadUrl);
		Iterator<String> itor = tmap.keySet().iterator();
		while (itor.hasNext()) {
			String name = (String) itor.next();
			sb.append(name).append("=").append(tmap.get(name)).append("&");
		}
		String url = sb.toString();
		// 删除末尾多余的&
		return StringUtil.substring(url, 0, url.length() - 1);
	}

	public static String sign(String secret, TreeMap<String, String> tmap) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(secret);
		Iterator<String> itor = tmap.keySet().iterator();
		while (itor.hasNext()) {
			String name = (String) itor.next();
			sb.append(name).append(tmap.get(name));
		}
		sb.append(secret);

		return getMD5Str(sb.toString());
	}

	public static String getMD5Str(String value) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		byte[] bytes = md5.digest(value.getBytes("UTF-8"));

		StringBuilder str = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				str.append("0");
			}
			str.append(hex.toUpperCase());
		}

		return str.toString();
	}

}
