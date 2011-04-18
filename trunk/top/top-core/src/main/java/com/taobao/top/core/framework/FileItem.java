/**
 * 
 */
package com.taobao.top.core.framework;

import java.io.ByteArrayOutputStream;

/**
 * Multipart 结构对象
 * @author fangweng
 * @email fangweng@taobao.com
 *
 */
public class FileItem implements java.io.Serializable
{

	private static final long serialVersionUID = 8590819519688791155L;
	
	private String name;
	private String fileName;
	private String contentType;
	private boolean isFile;
	private String value;
	private ByteArrayOutputStream bout;
	
	public FileItem()
	{
		isFile = false;
	}
	
	public ByteArrayOutputStream getBout() {
		if (bout == null)
		{
			bout = new ByteArrayOutputStream();	
		}
		return bout;
	}
	public void setBout(ByteArrayOutputStream bout) {
		this.bout = bout;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public boolean isFile() {
		return isFile;
	}
	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}

}
