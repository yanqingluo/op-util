/**
 * 
 */
package com.taobao.top.core;

public enum ApiType {
	SELECT, UPDATE, INSERT, DELETE;

	public static ApiType strptype(String type) {
		ApiType rs = SELECT;
		if (type.equals("update")) {
			rs = UPDATE;
		} else if (type.equals("insert")) {
			rs = INSERT;
		} else if (type.equals("delete")) {
			rs = DELETE;
		}
		return rs;
	}
}