package com.taobao.top.notify.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class MapUtil {

	/**
	 * 清除内容中的空字段。
	 */
	public static Map<String, Object> normalizeMap(Map<String, Object> content) {
		Map<String, Object> result = new HashMap<String, Object>(content.size());
		Set<Entry<String, Object>> fields = content.entrySet();

		for (Entry<String, Object> field : fields) {
			if (field.getValue() != null) {
				result.put(field.getKey(), field.getValue());
			}
		}

		return result;
	}
}
