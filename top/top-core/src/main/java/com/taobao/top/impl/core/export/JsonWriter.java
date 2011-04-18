/**
 * 
 */
package com.taobao.top.impl.core.export;

import java.io.IOException;
import java.io.Writer;

import com.taobao.top.core.export.ProtocolWriter;
import com.taobao.top.json.JSONObject;
import com.taobao.top.json.JsonConfig;
import com.taobao.top.json.util.JSONUtils;

/**
 * TFS
 * 
 * @version 2009-2-18
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public class JsonWriter implements ProtocolWriter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.core.ProtocolWriter#write(java.io.Writer,
	 * java.lang.Object)
	 */
	public void write(Writer w, Object source) throws IOException {
		if (JSONUtils.isNumber(source) || JSONUtils.isBoolean(source)
				|| JSONUtils.isString(source)) {
			w.write(String.valueOf(source));
		} else {
			JSONObject json = JSONObject.fromObject(source, new JsonConfig());
			json.write(w);
		}
	}

}
