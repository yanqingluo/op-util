package com.taobao.top.notify.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.taobao.top.notify.domain.NotifyEnum;

/**
 * 消息帮助类。
 * 
 * @author fengsheng
 * @since 1.0, Jan 27, 2010
 */
public final class NotifyHelper {

	private static final Pattern PT_SUB = Pattern.compile("[1-4]:\\d+(;[1-4]:\\d+)*");

	/**
	 * 把平面订阅信息转换为字符串形式的映射。
	 * 
	 * @param subscriptions 如：1:337;3:196
	 */
	public static Map<String, List<String>> convertSubscribeOutput(String subscriptions) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		Map<Integer, Long> cache = convertSubscribeCache(subscriptions);
		Set<Entry<Integer, Long>> cats = cache.entrySet();
		for (Entry<Integer, Long> cat : cats) {
			String catMsg = NotifyEnum.getInstance(cat.getKey(), 0).getMessage();
			List<String> subMsgs = NotifyEnum.getMessages(cat.getKey(), cat.getValue());
			result.put(catMsg, subMsgs);
		}
		return result;
	}

	/**
	 * 把字符串形式的映射转换为平面订阅信息。
	 * 
	 * @return 如：1:337;3:196
	 */
	public static String convertSubscribeInput(Map<String, List<String>> subscriptions) {
		if (subscriptions == null || subscriptions.isEmpty()) {
			throw new IllegalArgumentException("subscriptions should not be null or empty");
		}

		StringBuffer result = new StringBuffer();
		Set<Entry<String, List<String>>> subSet = subscriptions.entrySet();
		boolean hasSub = false;

		for (Entry<String, List<String>> sub : subSet) {
			if (hasSub) {
				result.append(";");
			} else {
				hasSub = true;
			}

			int cat = NotifyEnum.getCategoryInstance(sub.getKey()).getCategory();
			result.append(cat).append(":");

			List<String> msgs = sub.getValue();
			if (msgs != null && !msgs.isEmpty()) {
				long status = 0L;

				if (msgs.size() == 1 && "all".equals(msgs.get(0))) { // 订阅类别下所有消息类型
					status = 0L;
				} else {
					for (String msg : msgs) {
						int bit = NotifyEnum.getInstance(cat, msg).getStatus();
						status += (1 << (bit - 1));
					}
				}
				result.append(status);
			} else {
				throw new IllegalArgumentException("Invalid subscription format!");
			}
		}

		return result.toString();
	}

	/**
	 * 把平面订阅信息转换为64位整形的映射。
	 * 
	 * @param subscriptions 如：1:337;3:196
	 */
	public static Map<Integer, Long> convertSubscribeCache(String subscriptions) {
		if (StringUtils.isBlank(subscriptions)) {
			throw new IllegalArgumentException("subscriptions should not be blank");
		}

		Matcher matcher = PT_SUB.matcher(subscriptions);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("subscriptions format is invalid");
		}

		Map<Integer, Long> map = new HashMap<Integer, Long>();
		String[] cats = subscriptions.split(";");
		for (String cat : cats) {
			String[] params = cat.split(":");
			map.put(Integer.valueOf(params[0]), Long.valueOf(params[1]));
		}

		return map;
	}

	public static String getCacheAppKey(String appKey) {
		return "APPKEY-" + appKey;
	}

}
