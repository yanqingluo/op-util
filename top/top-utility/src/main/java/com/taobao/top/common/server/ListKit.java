package com.taobao.top.common.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

public class ListKit {
	public static List<String> removes(List<String> src, String[] removes) {
		if (null == src || src.size() == 0 || null == removes
				|| removes.length == 0) {
			return src;
		}
		String[] fieldsArray = src.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
		List<String> result = new ArrayList<String>(fieldsArray.length);
		for (int i = 0; i < fieldsArray.length; i++) {
			String field = fieldsArray[i];
			if (!contains(removes, field)) {
				result.add(field);
			}
		}
		return result;
	}

	public static boolean contains(String[] src, String in) {
		if (null == src || src.length == 0 || null == in) {
			return false;
		}
		for (int i = 0; i < src.length; i++) {
			if (in.equals(src[i])) {
				return true;
			}
		}
		return false;
	}
}