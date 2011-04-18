package com.taobao.top.core.framework;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.taobao.top.core.exception.FileNumInvalidException;
import com.taobao.top.core.exception.FileSizeInvalidException;
import com.taobao.top.core.exception.FileTypeInvalidException;

import static org.junit.Assert.*;

/**
 * 测试LazyParser 每个测试用例跑完后都要释放掉当天线程的数据
 * 
 * @author zhenzi 目前主要测试上传文件
 */
@Ignore
public class LazyParserTest {
	private static final String CONTENT_TYPE = "multipart/form-data; boundary=---1234";
	private String content = "-----1234\r\n"
		 + "-----1234\r\n";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 测试getParameter的正常情况
	 */
	@Test
	public void testGetParameter_正常情况_上传文件() throws Exception {
		byte[] bytes = content.getBytes("US-ASCII");
		HttpServletRequest request = new MockHttpServletRequest(bytes,
				CONTENT_TYPE);
		// 文件域
		Object o = LazyParser.getParameter(request, "file");
		assertTrue(o instanceof FileItem);
		FileItem f = (FileItem) o;
		assertEquals("This is the content of the file\n", new String(f
				.getBout().toByteArray()));
		assertEquals("text/whatever", f.getContentType());
		assertEquals("foo.tab", f.getFileName());
		// 普通域
		o = LazyParser.getParameter(request, "field");
		assertTrue(o instanceof FileItem);
		f = (FileItem) o;
		assertEquals("fieldValue", f.getValue());

		LazyParser.release();
	}

	/*
	 * 测试getParameterMap
	 */
	@Test
	public void testGetParameterMap_正常情况() throws Exception {
		byte[] bytes = content.getBytes("US-ASCII");
		HttpServletRequest request = new MockHttpServletRequest(bytes,
				CONTENT_TYPE);
		Map map = LazyParser.getParameterMap(request);
		// assertEquals("4",map.size()); //FIXME
		// 在form表单中的field重复的情况下，也应该能返回重复的表单数据
		FileItem f = (FileItem) map.get("file");
		assertEquals("This is the content of the file\n", new String(f
				.getBout().toByteArray()));
		assertEquals("text/whatever", f.getContentType());
		assertEquals("foo.tab", f.getFileName());

		f = (FileItem) map.get("field");
		assertEquals("fieldValue", f.getValue());

		f = (FileItem) map.get("multi");
		assertEquals("value1", f.getValue());
		LazyParser.release();
	}

	/*
	 * 测试getParameterNames
	 */
	@Test
	public void testGetParameterNames_正常情况() throws Exception {
		byte[] bytes = content.getBytes("US-ASCII");
		HttpServletRequest request = new MockHttpServletRequest(bytes,
				CONTENT_TYPE);
		Set<String> paramNames = LazyParser.getParameterNames(request);
		assertEquals(3, paramNames.size());
		assertTrue(paramNames.contains("file") && paramNames.contains("field")
				&& paramNames.contains("multi"));
		LazyParser.release();
	}

	/*
	 * 测试getFileItemsFromRequest
	 */
	@Test
	public void testGetFileItemFromRequest_正常情况() throws Exception {
		byte[] bytes = content.getBytes("US-ASCII");
		HttpServletRequest request = new MockHttpServletRequest(bytes,
				CONTENT_TYPE);
		List<FileItem> fileItem = LazyParser.getFileItemsFromRequest(request);
		assertEquals(1, fileItem.size());
		FileItem f = fileItem.get(0);
		assertEquals("This is the content of the file\n", new String(f
				.getBout().toByteArray()));
		assertEquals("text/whatever", f.getContentType());
		assertEquals("foo.tab", f.getFileName());
		LazyParser.release();
	}

	/**
	 * 测试上传的文件类型不对
	 */
	@Test
	public void test上传的文件类型不对() throws Exception {
		LazyParser.setFileExts(new String[] { ".jpg", ".tab" });
		String temp = "-----1234\r\n"
				+ "Content-Disposition: form-data; name=\"file\"; filename=\"foo.png\"\r\n"
				+ "Content-Type: text/whatever\r\n" + "\r\n"
				+ "This is the content of the file\n" + "\r\n"
				+ "-----1234\r\n"
				+ "Content-Disposition: form-data; name=\"field\"\r\n" + "\r\n"
				+ "fieldValue\r\n" + "-----1234\r\n"
				+ "Content-Disposition: form-data; name=\"multi\"\r\n" + "\r\n"
				+ "value1\r\n" + "-----1234\r\n";
		byte[] bytes = temp.getBytes("US-ASCII");
		HttpServletRequest request = new MockHttpServletRequest(bytes,
				CONTENT_TYPE);
		Object o = null;
		try {
			o = LazyParser.getParameter(request, "file");
		} catch (Exception e) {
			assertTrue(e instanceof FileTypeInvalidException);
		}
		assertNull(o);
		LazyParser.release();
	}

	@Test
	public void test上传的文件大小超过限制大小() throws Exception {
		LazyParser.setMaxBufferSize(10);// 允许上传的文件大小为10byte
		byte[] bytes = content.getBytes("US-ASCII");
		HttpServletRequest request = new MockHttpServletRequest(bytes,
				CONTENT_TYPE);
		Object o = null;
		try {
			o = LazyParser.getParameter(request, "file");
		} catch (Exception e) {
			assertTrue(e instanceof FileSizeInvalidException);
		}
		assertNull(o);
		LazyParser.release();
	}

	@Test
	public void test上传的文件多余1个() throws Exception {
		String temp = "-----1234\r\n"
				+ "Content-Disposition: form-data; name=\"file\"; filename=\"foo.jpg\"\r\n"
				+ "Content-Type: text/whatever\r\n"
				+ "\r\n"
				+ "This is the content of the file\n"
				+ "\r\n"
				+ "-----1234\r\n"
				+ "Content-Disposition: form-data; name=\"file2\"; filename=\"foo2.jpg\"\r\n"
				+ "Content-Type: text/whatever\r\n" + "\r\n"
				+ "This is the file2 content\n" + "\r\n" + "-----1234\r\n"
				+ "Content-Disposition: form-data; name=\"field\"\r\n" + "\r\n"
				+ "fieldValue\r\n" + "-----1234\r\n"
				+ "Content-Disposition: form-data; name=\"multi\"\r\n" + "\r\n"
				+ "value1\r\n" + "-----1234\r\n";
		byte[] bytes = temp.getBytes("US-ASCII");
		HttpServletRequest request = new MockHttpServletRequest(bytes,
				CONTENT_TYPE);
		Object o = null;
		try {
			LazyParser.setMaxBufferSize(1000);
			o = LazyParser.getFileItemsFromRequest(request);
		} catch (Exception e) {
			assertTrue(e instanceof FileNumInvalidException);
		}
		assertNull(o);
		LazyParser.release();
	}

	// ----以下测试普通的post数据
	@Test
	public void test普通的post数据() throws Exception {
		String temp = "product=mail163&" +
				"savelogin=&" +
				"url=http%3A%2F%2Fmail.163.com%2Ferrorpage%2Ferr_163.htm&" +
				"username=2005081097&" +
				"password=013579";
		String normal_con_type = "application/x-www-form-urlencoded";
		byte[] bytes = temp.getBytes("US-ASCII");
		HttpServletRequest request = new MockHttpServletRequest(bytes,normal_con_type);
		Object o = null;
		o = LazyParser.getParameter(request, "product");
		assertEquals("mail163",(String)o);
		o = LazyParser.getParameter(request, "savelogin");
		assertNull(o);
		o = LazyParser.getParameter(request, "url");
		assertEquals("http://mail.163.com/errorpage/err_163.htm",(String)o);
		
		List<FileItem> fileItemList = LazyParser.getFileItemsFromRequest(request);
		assertTrue(fileItemList.size() == 0);
		
		Map map = LazyParser.getParameterMap(request);
		assertEquals(5,map.size());
		assertEquals("mail163",map.get("product"));
		assertEquals("http://mail.163.com/errorpage/err_163.htm",map.get("url"));
		
		Set<String> paramNames = LazyParser.getParameterNames(request);
		assertEquals(5,paramNames.size());
		assertTrue(paramNames.contains("product"));
		LazyParser.release();
	}
}
