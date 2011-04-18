package com.taobao.top.common.lang;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.Map.Entry;

public class MapKit {

	/**
	 * 仅在测试时使用
	 * 
	 * @param map
	 * @return
	 */
	public static String dumpMap(Map<String, ?> map) {
		if (map == null || map.size() == 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (Entry<String, ?> entry : map.entrySet()) {
			sb.append(entry.getKey());
			sb.append(":");
			Object value = entry.getValue();
			if (value != null) {
				if (value.getClass().isArray()) {
					int l = Array.getLength(value);
					for (int i = 0; i < l; i++) {
						sb.append(Array.get(value, i));
						if (i != l - 1) {
							sb.append(',');
						}
					}
				} else {
					sb.append(value);
				}
				sb.append(" ");
			}
		}
		return sb.toString();
	}

}
