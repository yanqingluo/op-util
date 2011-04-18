package com.taobao.top.core.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.taobao.top.core.exception.FileNumInvalidException;
import com.taobao.top.core.exception.FileSizeInvalidException;
import com.taobao.top.core.exception.FileTypeInvalidException;


/**
 * LazyServletContext支持延时读取数据， 提高对于错误请求报的处理能力
 * 
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 * 
 */
public class LazyParser {
	private static ThreadLocal<Map<String, Object>> lazyServletContext = new ThreadLocal<Map<String, Object>>();
	private static final Log logger = LogFactory.getLog(LazyParser.class);
	/**
	 * 允许上传的后缀名
	 */
	private static String[] fileExts;

	/**
	 * 最大的缓存文件的字节大小，单位字节
	 */
	private static int maxBufferSize = 2 * 1024 * 1024;
	/**
	 * 缓存大小，可以自己调节，大小单位字节
	 */
	private static int bufSize = 8192;
	/**
	 * Multipart类型判断符
	 */
	private final static String MULTIPART = "multipart/form-data";

	/**
	 * 允许最多上传的文件个数
	 */
	private static int maxFileUploadCount = 1;
	/**
	 * 上次解析遗留的数据
	 */
	private final static String LEAVE_BUF = "_le_buf_";
	/**
	 * 消息中参数的名称概述，放在http head中，解析参数可以先以这个概述为准，防止对不存在参数做无谓解析
	 */
	private final static String PARAMETERS_SUMMARY = "_p_summary_";

	/**
	 * 作为URL内参数是否被解析的表示
	 */
	private final static String QUERY_RESOLVED = "_query_resolved_";

	private final static String UPLOAD_FILE_COUNT = "_upload_file_count";
	
	/**
	 * 缓存表示是否是mutilpart的请求
	 */
	private final static String MULTIPART_FLAG = "_multipart_";

	/**
	 * 作为Body内参数是否被解析的表示
	 */
	private final static String BODY_RESOLVED = "_body_resolved_";

	private final static String BOUNDARY = "boundary";

	private final static byte[] RETURN = "\r\n".getBytes();

	private final static byte[] BYTE_AND = "&".getBytes();

	private final static byte[] BYTE_EQUAL = "=".getBytes();

	private final static byte[] BYTE_EE = "--".getBytes();
	
	private final static String BOUNDARY_RETURN = "boundary_return";
	
	public final static String LAZY_CONTEXT = "_lazy_context";
	
	/**
	 * 延时获取数据
	 * 
	 * @param req
	 * @param key
	 * @return
	 * @throws IOException
	 * @throws FileTypeInvalidException
	 * @throws FileSizeInvalidException
	 * @throws FileNumInvalidException
	 */
	public static Object getParameter(HttpServletRequest req,String key) 
		throws IOException, FileTypeInvalidException, FileSizeInvalidException, FileNumInvalidException
	{
		Object result = null;

		if (key == null || ("").equals(key))
			return result;
		
		// get请求不优化
		if ("get".equalsIgnoreCase(req.getMethod()))
			return req.getParameter(key);
		
		Map<String, Object> params = lazyServletContext.get();

		if (params == null)
		{
			params = new HashMap<String, Object>();
			lazyServletContext.set(params);
		}
			
		
		if ((result = params.get(key)) != null)
			return result;


		// 表示已经全部解析完毕
		if (params.get(BODY_RESOLVED) != null)
			return result;

		// 如果采用载要的方式，可以提高解析效率，防止不存在的内容解析耗时
		if (params.get(PARAMETERS_SUMMARY) != null) {
			String p = (String) params.get(PARAMETERS_SUMMARY);

			if (p.indexOf(new StringBuilder().append(",").append(key).append(
					",").toString()) < 0)
				return result;
		} else {
			if (req.getHeader(PARAMETERS_SUMMARY) != null)
				params.put(PARAMETERS_SUMMARY, new StringBuilder().append(",")
						.append(req.getHeader(PARAMETERS_SUMMARY)).append(",")
						.toString());
		}

		boolean isMultipart = isMultipartContent(req);

		// 对URL中的参数做一次解析
		if (resolveFromQueryString(req, params, isMultipart)) {
			if ((result = params.get(key)) != null)
				return result;
		}
		
		//try {
			while (resolve(req, params, isMultipart)) {
				if ((result = params.get(key)) != null)
					return result;
			}
			/*
		} catch (FileTypeInvalidException e) {
			params.put(BODY_RESOLVED, BODY_RESOLVED);
			lazyServletContext.set(params);
			throw e;
		} catch (FileSizeInvalidException e) {
			params.put(BODY_RESOLVED, BODY_RESOLVED);
			lazyServletContext.set(params);
			throw e;
		} catch (FileNumInvalidException e) {
			params.put(BODY_RESOLVED, BODY_RESOLVED);
			lazyServletContext.set(params);
			throw e;
		} catch (IOException e) {
			params.put(BODY_RESOLVED, BODY_RESOLVED);
			lazyServletContext.set(params);
			throw e;
		}
	    */
		params.put(BODY_RESOLVED, BODY_RESOLVED);
		lazyServletContext.set(params);

		return result;
	}
	
	public static Map<String, Object> getLazyContext()
	{
		
		Map<String, Object> params = lazyServletContext.get();

		if (params == null)
		{
			params = new HashMap<String, Object>();
			lazyServletContext.set(params);
		}
		
		return params;
	}
	
	public static Map<String,Object> addParams2LazyContext(Map<String, Object> params)
	{
		Map<String, Object> p = lazyServletContext.get();
		
		if (p == null)
		{
			p = params;
		}
		else
		{
			p.putAll(params);
		}
		
		lazyServletContext.set(p);
		return p;
	}

	@SuppressWarnings("unchecked")
	public static Map getParameterMap(HttpServletRequest req) {
		if ("get".equalsIgnoreCase(req.getMethod()))
			return req.getParameterMap();

		try {
			Map<String, Object> params = lazyServletContext.get();

			if (params == null)
			{
				params = new HashMap<String, Object>();
				lazyServletContext.set(params);
			}

			// 表示已经全部解析完毕
			if (params.get(BODY_RESOLVED) != null)
				return filterSystemParameter(params);

			boolean isMultipart = isMultipartContent(req);

			// 对URL中的参数做一次解析
			resolveFromQueryString(req, params, isMultipart);

			while (resolve(req, params, isMultipart)) {
			}

			params.put(BODY_RESOLVED, BODY_RESOLVED);
			lazyServletContext.set(params);

			return filterSystemParameter(params);
		} catch (Exception ex) {
			logger.error("", ex);
			release();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static Set<String> getParameterNames(HttpServletRequest req)
			throws FileTypeInvalidException, FileSizeInvalidException,
			IOException, FileNumInvalidException {
		if ("get".equalsIgnoreCase(req.getMethod())) {
			HashSet<String> result = new HashSet<String>();

			Enumeration names = req.getParameterNames();

			while (names.hasMoreElements()) {
				result.add((String) names.nextElement());
			}

			return result;
		}
		Map<String, Object> params = lazyServletContext.get();

		if (params == null)
		{
			params = new HashMap<String, Object>();
			lazyServletContext.set(params);
		}

		// 表示已经全部解析完毕
		if (params.get(BODY_RESOLVED) != null)
			return filterSystemParameter(params).keySet();

		boolean isMultipart = isMultipartContent(req);

		// 对URL中的参数做一次解析
		resolveFromQueryString(req, params, isMultipart);

		while (resolve(req, params, isMultipart)) {
		}

		params.put(BODY_RESOLVED, BODY_RESOLVED);
		lazyServletContext.set(params);

		return filterSystemParameter(params).keySet();
	}

	/**
	 * 获取所有File类型的内容
	 * 
	 * @param req
	 * @return
	 * @throws IOException
	 * @throws FileSizeInvalidException
	 * @throws FileTypeInvalidException
	 * @throws FileNumInvalidException
	 */
	public static List<FileItem> getFileItemsFromRequest(HttpServletRequest req)
			throws FileTypeInvalidException, FileSizeInvalidException,
			IOException, FileNumInvalidException {
		ArrayList<FileItem> items = new ArrayList<FileItem>();

		if (!isMultipartContent(req))
			return items;

		Set<String> names = getParameterNames(req);

		Iterator<String> nameIter = names.iterator();
		Map<String, Object> params = lazyServletContext.get();

		while (nameIter.hasNext()) {
			Object node = params.get(nameIter.next());

			if (node == null)
				continue;

			if (node instanceof FileItem && ((FileItem) node).isFile())
				items.add((FileItem) node);
		}

		return items;

	}

	/**
	 * 对URL中的参数做一次解析
	 * 
	 * @param req
	 * @param params
	 * @return
	 */
	protected static boolean resolveFromQueryString(HttpServletRequest req,
			Map<String, Object> params, boolean isMultipart) {
		if (req.getQueryString() == null
				|| (req.getQueryString() != null && "".equals(req
						.getQueryString())))
			return false;

		if (params.get(QUERY_RESOLVED) != null)
			return false;

		String[] nodes = req.getQueryString().split("&");

		for (String node : nodes) {
			int index = node.indexOf("=");
			if (index < 0)
				continue;

			if (params.get(node.substring(0, index)) != null)
				continue;

			if (isMultipart) {
				FileItem param = new FileItem();
				param.setName(node.substring(0, index));
				param.setValue(urlDecode(node.substring(index + 1)));

				params.put(param.getName(), param);
			} else
				params.put(node.substring(0, index), urlDecode(node
						.substring(index + 1)));
		}

		params.put(QUERY_RESOLVED, QUERY_RESOLVED);

		return true;
	}

	/**
	 * 解析数据流
	 * 
	 * @param req
	 * @param params
	 * @return
	 * @throws IOException
	 * @throws FileTypeInvalidException
	 * @throws FileSizeInvalidException
	 * @throws FileNumInvalidException
	 */
	protected static boolean resolve(HttpServletRequest req,
			Map<String, Object> params, boolean isMultipart)
			throws IOException, FileTypeInvalidException,
			FileSizeInvalidException, FileNumInvalidException {
		if (isMultipart) {
			return parseMultiRequest(req, params);
		} else {
			return parseRequest(req, params);
		}
	}

	/**
	 * 解析Multipart消息体
	 *  //FIXME 1， 这个方法的前提是假设客户端在组装参数的时候以如下顺序：
	 *  content-disposition
	 *  content-type
	 *  content-transfer-encoding
	 *  value
	 *  但是事实上有可能不是以这样的顺序或者不知传递了这些头部，所以需要对这些情况做兼容处理
	 *  2， 在极端情况下如果在读完最后一个字段后，刚好没有把结束标记读进来，比如：
	 *  value\r\n,程序刚好读数据到此
	 *  --boundart-- //结束符还没读进来，
	 *  在这种情况下会在一次走到readBoundary虽然程序上没问题，但是分子流应该不能走到这里来。需要对这种极端情况作处理。
	 * @param req
	 * @param params
	 * @return
	 * @throws IOException
	 * @throws FileTypeInvalidException
	 * @throws FileSizeInvalidException
	 * @throws FileNumInvalidException
	 */
	private static boolean parseMultiRequest(HttpServletRequest req,
			Map<String, Object> params) throws IOException,
			FileTypeInvalidException, FileSizeInvalidException,
			FileNumInvalidException {
		byte[] boundry;
		byte[] boundry_return;
		InputStream in = req.getInputStream();
		FileItem node = new FileItem();

		if (params.get(BOUNDARY) != null && params.get(BOUNDARY_RETURN) != null){
			boundry = (byte[]) params.get(BOUNDARY);
			boundry_return = (byte[])params.get(BOUNDARY_RETURN);
		}else {
			boundry = new StringBuilder().append("--").append(
					req.getContentType().substring(
							req.getContentType().indexOf(BOUNDARY)
									+ BOUNDARY.length() + 1)).toString()
					.getBytes("UTF-8");
			boundry_return = new byte[boundry.length + RETURN.length];
			System.arraycopy(boundry, 0, boundry_return, 0, boundry.length);
			System.arraycopy(RETURN, 0, boundry_return, boundry.length, RETURN.length);
			params.put(BOUNDARY, boundry);
			params.put(BOUNDARY_RETURN, boundry_return);
		}

		BufferList byteBuffer = null;

		if (params.get(LEAVE_BUF) != null) {
			byteBuffer = (BufferList) params.get(LEAVE_BUF);
		} else {
			byteBuffer = new BufferList();
			params.put(LEAVE_BUF, byteBuffer);
		}

		if (readBoundary(byteBuffer, boundry_return,in) < 0)
			return false;

		if (readContentDesc(byteBuffer, in, node) < 0)
			return false;

		if (readContentType(byteBuffer, in, node) < 0)
			return false;
		
		if (readContentTransferEncoding(byteBuffer, in, node) < 0)
			return false;

		if (readContentValue(byteBuffer, boundry, in, node, params) < 0)
			return false;

		// 判断是否已经到了结尾
		if (byteBuffer.indexOf(BYTE_EE, boundry.length) == boundry.length)
			byteBuffer.clean();

		if (params.get(node.getName()) == null)
			params.put(node.getName(), node);

		lazyServletContext.set(params);

		return true;
	}

	/**
	 * 解析Multipart的边界
	 * 
	 * @param byteBuffer
	 * @param boundry
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static int readBoundary(BufferList byteBuffer,byte[] boundry_return,
			InputStream in) throws IOException {
		// read boundary
		int beg = 0;
		int count = 0;
		while ((beg = byteBuffer.indexOf(boundry_return)) < 0) {
			count = readBytes(in, byteBuffer);

			if (count <= 0)
				return count;
		}

		beg += boundry_return.length;// \r\n
		byteBuffer.remove(beg);

		return count;
	}

	/**
	 * 解析Multipart的内容描述
	 * 
	 * @param byteBuffer
	 * @param in
	 * @param node
	 * @return
	 * @throws IOException
	 * @throws FileTypeInvalidException
	 */
	private static int readContentDesc(BufferList byteBuffer, InputStream in,
			FileItem node) throws IOException, FileTypeInvalidException {
		int beg = 0;
		int count = 0;

		while ((beg = byteBuffer.indexOf(RETURN)) < 0) {
			count = readBytes(in, byteBuffer);

			if (count <= 0)
				return count;
		}
		
		String contentDes = byteBuffer.get(0, beg, "UTF-8");
		byteBuffer.remove(beg + RETURN.length);
		if(StringUtils.isEmpty(contentDes)){
			byte[] data = byteBuffer.get();
			if(data != null){//如果数据过大,则不打印出来
				int l = data.length > 200 ? 200:data.length;
				logger.error("read content des error,leave data is:"+new String(data,0,l,"UTF-8"));
			}else{
				logger.error("read content des error.");
			}
//			return -2;//返回负数表示不在进行解析数据，由于客户端组装的协议有问题。
		}
		int paraIndex = contentDes.indexOf("name");
		if (paraIndex > 0)
		{
			int _beg = paraIndex + contentDes.substring(paraIndex).indexOf("\"") + 1;
			node.setName(contentDes.substring(_beg,_beg + contentDes.substring(_beg).indexOf("\"")));
		}
		

		paraIndex = contentDes.indexOf("filename=\"");
		if (paraIndex > 0) {
			node.setFile(true);
			node.setFileName(contentDes.substring(paraIndex
					+ "filename=\"".length(), contentDes.indexOf("\"",
					paraIndex + "filename=\"".length())));

			// 判断是否是合法的后缀名上传文件
			if (fileExts != null && fileExts.length > 0) {
				boolean isIllegal = true;

				for (String ext : fileExts) {
					if (node.getFileName().toLowerCase().endsWith(
							ext.toLowerCase())) {
						isIllegal = false;
						break;
					}
				}

				if (isIllegal)
					throw new FileTypeInvalidException();
			}

		}

		return count;
	}

	/**
	 * 解析Multipart的内容类型
	 * 
	 * @param byteBuffer
	 * @param in
	 * @param node
	 * @return
	 * @throws IOException
	 */
	private static int readContentType(BufferList byteBuffer, InputStream in,
			FileItem node) throws IOException {
		int beg = 0;
		int count = 0;

		// read content type
		while ((beg = byteBuffer.indexOf(RETURN)) < 0) {
			count = readBytes(in, byteBuffer);

			if (count <= 0)
				return count;
		}

		String contentType = byteBuffer.get(0, beg, "UTF-8");
		int paraIndex = -1;
		
		if (contentType != null)
			paraIndex = contentType.indexOf("Content-Type:");

		if (paraIndex >= 0)
		{
			node.setContentType(contentType.substring(
					paraIndex + "Content-Type:".length()).trim());
			byteBuffer.remove(beg + RETURN.length);
		}	

		return count;
	}
	
	/**
	 * 解析Multipart的内容类型
	 * 
	 * @param byteBuffer
	 * @param in
	 * @param node
	 * @return
	 * @throws IOException
	 */
	private static int readContentTransferEncoding(BufferList byteBuffer, InputStream in,
			FileItem node) throws IOException {
		int beg = 0;
		int count = 0;

		// read content type
		while ((beg = byteBuffer.indexOf(RETURN)) < 0) {
			count = readBytes(in, byteBuffer);

			if (count <= 0)
				return count;
		}

		String transEncoding = byteBuffer.get(0, beg, "UTF-8");
		int paraIndex = -1;
		
		if (transEncoding != null)
			paraIndex = transEncoding.indexOf("Transfer-Encoding:");

		if (paraIndex >= 0)
		{
			byteBuffer.remove(beg + RETURN.length);
		}

		byteBuffer.remove(RETURN.length);
		

		return count;
	}

	/**
	 * 解析Multipart的具体内容
	 * 
	 * @param byteBuffer
	 * @param boundry
	 * @param in
	 * @param node
	 * @return
	 * @throws IOException
	 * @throws FileSizeInvalidException
	 * @throws FileNumInvalidException
	 */
	private static int readContentValue(BufferList byteBuffer, byte[] boundry,
			InputStream in, FileItem node, Map<String, Object> params)
			throws IOException, FileSizeInvalidException,
			FileNumInvalidException {
		int beg = 0;
		int count = 0;
		int readCount = 0;
		
		if (node.isFile()) {
			// 判断上传文件数
			if (maxFileUploadCount > 0) {
				if (params.get(UPLOAD_FILE_COUNT) == null)
					params.put(UPLOAD_FILE_COUNT, 1);
				else {
					if ((Integer) params.get(UPLOAD_FILE_COUNT) >= maxFileUploadCount)
						throw new FileNumInvalidException();
					else
						params.put(UPLOAD_FILE_COUNT, (Integer) params
								.get(UPLOAD_FILE_COUNT) + 1);
				}

			}
			while ((beg = byteBuffer.indexOf(boundry)) < 0) {
				if (byteBuffer.getLength() > boundry.length) {
					node.getBout().write(
							byteBuffer.get(0, byteBuffer.getLength()
									- boundry.length));

					byteBuffer.remove(byteBuffer.getLength() - boundry.length);
				}

				count = readBytes(in, byteBuffer);
				readCount += count;

				// 超过流量限制了，抛出异常
				if (readCount > maxBufferSize)
					throw new FileSizeInvalidException();
				if (count <= 0) {
					return count;
				}

			}

			readCount += beg - RETURN.length;

			// 超过流量限制了，抛出异常
			if (readCount > maxBufferSize)
				throw new FileSizeInvalidException();

			if (beg - RETURN.length > 0)
				node.getBout().write(byteBuffer.get(0, beg - RETURN.length));

			byteBuffer.remove(beg);
		} else {
			while ((beg = byteBuffer.indexOf(boundry)) < 0) {
				count = readBytes(in, byteBuffer);

				if (count <= 0)
					return count;
			}

			node.setValue(byteBuffer.get(0, beg - RETURN.length, "UTF-8"));

			byteBuffer.remove(beg);

		}

		return count;
	}

	/**
	 * 解析普通的post消息
	 * 
	 * @param req
	 * @param params
	 * @return
	 * @throws IOException
	 */
	private static boolean parseRequest(HttpServletRequest req,
			Map<String, Object> params) throws IOException {
		boolean result;
		InputStream in = req.getInputStream();

		BufferList byteBuffer = null;

		if (params.get(LEAVE_BUF) != null) {
			byteBuffer = (BufferList) params.get(LEAVE_BUF);
		} else {
			byteBuffer = new BufferList();
			params.put(LEAVE_BUF, byteBuffer);
		}

		result = readContentValue(byteBuffer, in, params);

		lazyServletContext.set(params);

		return result;
	}

	/**
	 * 读取普通post消息的内容 
	 * 
	 * @param byteBuffer
	 * @param in
	 * @param params
	 * @return
	 * @throws IOException
	 */
	private static boolean readContentValue(BufferList byteBuffer,
			InputStream in, Map<String, Object> params) throws IOException {
		int beg = 0;
		int count = 0;
		int total = 0;

		while ((beg = byteBuffer.indexOf(BYTE_EQUAL, total - count)) < 0) {
			count = readBytes(in,byteBuffer);
			total += count;
			
			if (count <= 0)
				return false;
		}

		String key = byteBuffer.get(0, beg, "UTF-8");
		byteBuffer.remove(beg + 1);
		count = 0;
		total = 0;

		while ((beg = byteBuffer.indexOf(BYTE_AND, total - count)) < 0) {
			count = readBytes(in,byteBuffer);
			total += count;

			if (count <= 0) {
				if (byteBuffer.getLength() <= 0)
					return false;
				else
					break;
			}

		}

		String value = null;
		//before :beg <= 0
		if (beg < 0) {
			value = byteBuffer.get("UTF-8");
			byteBuffer.clean();
		} else {
			value = byteBuffer.get(0, beg, "UTF-8");
			byteBuffer.remove(beg + 1);
		}

		if (params.get(key) == null)
			params.put(key, urlDecode(value));

		return true;
	}

	/**
	 * 从输入流中读取数据到线程缓存中
	 * 
	 * @param in
	 * @param byteBuffer
	 * @return
	 * @throws IOException
	 */
	private static int readBytes(InputStream in, BufferList byteBuffer)
			throws IOException {
		int count = 0;
		count = byteBuffer.add(in, bufSize);

		return count;
	}

	/**
	 * 释放ThreadLocal资源防止出现资源泄露或者重复使用带来的问题
	 */
	public static void release() {
		lazyServletContext.set(null);
		lazyServletContext.remove();
	}

	/**
	 * 判断是否是Multipart的类型请求
	 * 
	 * @param request
	 * @return
	 */
	public static final boolean isMultipartContent(HttpServletRequest request) {
		
		if (lazyServletContext.get().get(MULTIPART_FLAG) != null)
			return (Boolean)lazyServletContext.get().get(MULTIPART_FLAG);
		else
		{
			if ("post".equalsIgnoreCase(request.getMethod()) && request.getContentType() != null 
					&& request.getContentType().toLowerCase().startsWith(MULTIPART))
			{
				lazyServletContext.get().put(MULTIPART_FLAG,true);
				return true;
			}
			else
			{
				lazyServletContext.get().put(MULTIPART_FLAG,false);
				return false;
			}
		}

	}

	public static Map<String, Object> filterSystemParameter(
			Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();

		if (params == null)
			return result;

		for (String key : params.keySet()) {
			if (key.equals(PARAMETERS_SUMMARY) || key.equals(QUERY_RESOLVED)
					|| key.equals(BODY_RESOLVED) || key.equals(BOUNDARY)
					|| key.equals(LEAVE_BUF) || key.equals(UPLOAD_FILE_COUNT)
					|| key.equals(MULTIPART_FLAG) || key.equals(BOUNDARY_RETURN))
				continue;

			result.put(key, params.get(key));
		}

		return result;
	}

	private static String urlDecode(String src) {
		if(src == null || src.length() == 0){
			return src;
		}
		try {
			return URLDecoder.decode(src, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return src;
		}
	}

	public static String[] getFileExts() {
		return fileExts;
	}

	public static void setFileExts(String[] fileExts) {
		LazyParser.fileExts = fileExts;
	}

	public static int getMaxBufferSize() {
		return maxBufferSize;
	}

	public static void setMaxBufferSize(int maxBufferSize) {
		LazyParser.maxBufferSize = maxBufferSize;
	}

	public static int getBufSize() {
		return bufSize;
	}

	public static void setBufSize(int bufSize) {
		LazyParser.bufSize = bufSize;
	}

	public static int getMaxFileUploadCount() {
		return maxFileUploadCount;
	}

	public static void setMaxFileUploadCount(int maxFileUploadCount) {
		LazyParser.maxFileUploadCount = maxFileUploadCount;
	}

}
