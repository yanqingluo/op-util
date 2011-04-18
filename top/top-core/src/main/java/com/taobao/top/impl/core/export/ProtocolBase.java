/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.impl.core.export;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;

import com.alibaba.common.lang.StringUtil;
import com.taobao.top.core.ProtocolConstants;
import com.taobao.top.core.ResultSet;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.util.CollectionUtil;

/**
 * 1.0版本的export输出。由DefaultApiExcutor抽离出来。
 * 
 * @version 2008-2-29
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * @author jiangyongyuan.tw
 */
public class ProtocolBase {
	String datePattern = "yyyy-MM-dd HH:mm:ss";
	FastDateFormat fdf = FastDateFormat.getInstance(datePattern);


	/**
	 * 输出系统返回值,json格式
	 * 
	 * @param writer
	 * @param apiResult
	 * @param isUnderLine
	 *            是否下划线
	 * @throws IOException
	 */
	protected void writeSystemResponseJson(Writer writer, TopPipeResult apiResult,
			boolean isUnderLine) throws IOException {
		ResultSet result = apiResult.getResultSet();
		if (result != null && result.getTime() != null) {
			writer.write("\"time\":\"");
			writer.write(fdf.format(result.getTime()));
			writer.write("\",");
		}
		if (null != result && CollectionUtil.isNotEmpty(result.getResult())) {
			if (result.getCount() != null && result.getCount() > 0) { // count
				// 2008-9-8
				String totalresult = "totalResults";

				if (isUnderLine)
					totalresult = StringUtil
							.toLowerCaseWithUnderscores(totalresult);

				writer.write("\"" + totalresult + "\":\"");
				writer.write(result.getCount().toString());
				writer.write("\",");
			}
			if (result.getModified() != null) { // modified
				// 2008-12-10
				String lastModift = "lastModified";

				if (isUnderLine)
					lastModift = StringUtil
							.toLowerCaseWithUnderscores(lastModift);

				writer.write("\"" + lastModift + "\":\"");
				writer.write(fdf.format(result.getModified()));
				writer.write("\",");
			}
		}
	}

	/**
	 * 输出系统返回值,xml格式
	 * 
	 * @param writer
	 * @param apiResult
	 * @param isUnderLine
	 *            是否下划线
	 * @throws IOException
	 */
	protected void writeSystemResponseXml(Writer writer, TopPipeResult apiResult,
			boolean isUnderline) throws IOException {
		ResultSet result = apiResult.getResultSet();
		if (null != result) {
			if (result.getCount() != null && result.getCount() > 0) { // count
				// 2008-9-8
				String totalresult = "totalResults";

				if (isUnderline)
					totalresult = StringUtil
							.toLowerCaseWithUnderscores(totalresult);

				writer.write("<" + totalresult + ">");
				writer.write(result.getCount().toString());
				writer.write("</" + totalresult + ">");
			}
			if (result.getModified() != null) { // modified
				// 2008-12-10

				String lastModift = "lastModified";

				if (isUnderline)
					lastModift = StringUtil
							.toLowerCaseWithUnderscores(lastModift);

				writer.write("<" + lastModift + ">");
				writer.write(fdf.format(result.getModified()));
				writer.write("</" + lastModift + ">");
			}
			if (result.getTime() != null) { // modified
				// 2008-12-10
				writer.write("<time>");
				writer.write(fdf.format(result.getTime()));
				writer.write("</time>");
			}
		}
	}

	/**
	 * 转换response根节点名字
	 * 
	 * @param methodName
	 * @return
	 */
	public String toResponseRootNameV2(String methodName) {
		int pos = methodName.indexOf('.');
		if (pos != -1) {
			// filtrate the first level
			// then replace dot (.) to underline(_)
			methodName = methodName.substring(pos + 1).replace('.', '_');
		}
		return methodName + "_response";
	}

	/**
	 * Check the sensitive fields
	 * <p>
	 * 
	 * @param name
	 * @return
	 */
	public boolean isSensitiveField(String name) {
		if (ProtocolConstants.P_SESSION_NICK.equals(name)) {
			return true;
		}
		if (ProtocolConstants.P_SESSION_UID.equals(name)) {
			return true;
		}
		return false;
	}
	/**
	 * 针对mhtml漏洞过滤掉敏感字段
	 * @param value
	 * @return
	 */
	protected boolean isFilterValue(String key,String value){
		if(StringUtils.isBlank(value) || StringUtils.isBlank(key) || ProtocolConstants.systemParams.contains(key)){
			return false;
		}
		String temp = value.toLowerCase(); 
		if(temp.indexOf("content-location") > -1 || temp.indexOf("content-id") > -1){
			return true;
		}
		return false;
	}
}
