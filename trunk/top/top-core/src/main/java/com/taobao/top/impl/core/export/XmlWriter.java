/**
 * 
 */
package com.taobao.top.impl.core.export;

import java.io.IOException;
import java.io.Writer;

import com.taobao.top.convert.ConvertObject;
import com.taobao.top.convert.util.ConvertUtils;
import com.taobao.top.core.export.ProtocolWriter;
import com.taobao.top.json.util.JSONUtils;

/**
 * Xml 输出
 * 
 * @version 2009-2-18
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public class XmlWriter implements ProtocolWriter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.access.xml.XmlWriter#write(java.io.Writer,
	 * java.lang.Object)
	 */
	public void write(Writer w, Object source) throws IOException {
		if (JSONUtils.isNumber(source) || JSONUtils.isBoolean(source)
				|| JSONUtils.isString(source)) {
			w.write(String.valueOf(source));
		} else {
			ConvertUtils.setExpandElements(false);
			ConvertObject xmlWriter = ConvertObject.fromObject(source);
			xmlWriter.write(w);
		}
	}

}
