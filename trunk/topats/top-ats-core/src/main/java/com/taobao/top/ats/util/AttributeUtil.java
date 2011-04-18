package com.taobao.top.ats.util;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.common.lang.StringUtil;

/**
 * for AtsTaskDO's Attributes
 * 
 * @author jeck.xie 2010-11-16
 */ 
public class AttributeUtil {

	static final String SP = ";";
	static final String SSP = ":";

	public static final Map<String, String> toMap(String str) {
		Map<String, String> attrs = new HashMap<String, String>();
		if (StringUtil.isNotBlank(str)) {
			String[] arr = str.split(SP);
			if (null != arr) {
				for (String kv : arr) {
					if (StringUtil.isNotBlank(kv)) {
						String[] ar = kv.split(SSP);
						if (null != ar && ar.length == 2) {
							String key = ar[0];
							String val = ar[1];
							if (StringUtil.isNotEmpty(val)) {
								attrs.put(key, val);
							}
						}
					}
				}
			}
		}
		return attrs;
	}

	public static final String toString(Map<String, String> attrs) {
		StringBuilder sb = new StringBuilder();
		if (null != attrs && !attrs.isEmpty()) {
			sb.append(SP);
			for (String key : attrs.keySet()) {
				String val = attrs.get(key);
				if (StringUtil.isNotEmpty(val)) {
					sb.append(key).append(SSP).append(val).append(SP);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 判断是否为大任务结果。
	 */
	public static boolean isBigResult(Map<String, String> attrs) {
		return null != attrs && ModelKeyConstants.BOOLEAN_ATTRIBUTE_MARK.equals(attrs.get(ModelKeyConstants.IS_BIG_RESULT));
	}

}
