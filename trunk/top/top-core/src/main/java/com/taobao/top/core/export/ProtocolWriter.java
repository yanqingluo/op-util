/**
 * 
 */
package com.taobao.top.core.export;

import java.io.IOException;
import java.io.Writer;

/**
 * @version 2009-2-18
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public interface ProtocolWriter {

	void write(Writer w, Object source) throws IOException;
}
