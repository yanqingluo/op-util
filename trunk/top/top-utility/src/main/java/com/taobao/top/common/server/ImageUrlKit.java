package com.taobao.top.common.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * 图片工具类。
 * 
 * @author liupo
 * @version 1.0
 **/
public class ImageUrlKit {

	// 匹配图片地址默认的正则表达式
	private static final String DEFAULT_REGEX = "(i[1-8]/\\d{8}/\\p{ASCII}*.(jpg|gif|png))|(i[1-8]/\\p{ASCII}*.(jpg|gif|png))";
	private static final Pattern DOMAIN_URL_REG = Pattern.compile("http://\\*\\.(.+?)/");
	private static final Pattern T1_URL_REG = Pattern.compile("i[1-8]/T1.*");
	private static final String IMG = "img0";

	public static String perform(String shortUrl, String lackUrl) {
		if (StringUtils.isNotEmpty(shortUrl) && shortUrl.length() > 2) {
			if (StringUtils.isEmpty(shortUrl)) {
				return shortUrl;
			}
			String nodeNum = null;
			if (shortUrl.matches(DEFAULT_REGEX)) {
				nodeNum = IMG + shortUrl.charAt(1);
			} else {
				nodeNum = "img";
			}

			return StringUtils.replace(lackUrl, "*", nodeNum) + shortUrl;
		}
		return null;
	}

	public static boolean isNormalTfsPicture(String picUrl) {
		return picUrl != null && T1_URL_REG.matcher(picUrl).find();
	}

	public static String getDomainFromUrl(String url) {
		Matcher matcher = DOMAIN_URL_REG.matcher(url);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}

	/**
	 * 获取商品detail url
	 * （线上lackUrl为http://item.TAOBAO.COM/auction/item_detail.jhtml?item_id=）
	 */
	public static String getDetailUrl(String itemId, String xid, String lackUrl) {
		StringBuilder sb = new StringBuilder(lackUrl);
		sb.append(itemId);
		sb.append("&x_id=0");
		sb.append(xid);
		return sb.toString();
	}

}
