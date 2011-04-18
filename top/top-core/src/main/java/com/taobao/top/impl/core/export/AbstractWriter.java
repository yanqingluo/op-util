/**
 * 
 */
package com.taobao.top.impl.core.export;

import java.io.IOException;
import java.io.Writer;

import com.taobao.top.core.export.ProtocolWriter;

/**
 * @version 2009-2-18
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public class AbstractWriter implements ProtocolWriter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.top.core.export.ProtocolWriter#write(java.io.Writer,
	 * java.lang.Object)
	 */
	public void write(Writer w, Object source) throws IOException {
		w.write("unsupport!");
	}

}
