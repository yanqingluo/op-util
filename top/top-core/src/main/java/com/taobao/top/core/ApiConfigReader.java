/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.alibaba.common.lang.StringUtil;
import com.taobao.top.common.lang.StringKit;
import com.taobao.top.core.ApiApplicationParameter.Type;

/**
 * Convert api xml config file to {@link com.taobao.top.core.Api}
 * 
 * @version 2008-3-6
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class ApiConfigReader {
	private static final transient Log log = LogFactory
			.getLog(ApiConfigReader.class);

	private static final XPathFactory factory = XPathFactory.newInstance();

	private XPath xPath = factory.newXPath();
	/**
	 * Template id -> String content.
	 * <p>
	 * Since the template file definition is relative small and less extensive,
	 * it's safe to put all of them into memory.
	 */
	private Map<String, Template> templates = new Hashtable();

	// private Pattern pattern = Pattern.compile("(#[a-zA-Z0-9_]+#)");

	public Api xml2Api(InputStream inputStream) throws ApiConfigException {
		String string;
		
		try {
			BufferedInputStream bufferedStream = processBOM(inputStream);
			// 注意这里的字符编码
			string = IOUtils.toString(bufferedStream, "utf-8");
		} catch (IOException e) {
			throw new ApiConfigException(e);
		}
		return xmlString2Api(string);
	}

	BufferedInputStream processBOM(InputStream inputStream)
			throws IOException {
		BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
		bufferedStream.mark(3);
		byte[] bytes = new byte[3];
		int n = bufferedStream.read(bytes);
		if (n >= 3) { // feasible for checking.
			
			// must use byte casting, otherwise, the byte would be cast to integer.
			// then, we got negative integer.
			if (bytes[0] == (byte)0xEF && bytes[1] == (byte)0xBB && bytes[2] == (byte)0xBF ) {
				// bingo, we got the BOM
				return bufferedStream; // do not do the reset();
			}
		}
		
		// haven't find the BOM, reset the stream.
		bufferedStream.reset();
		return bufferedStream;
	}

	public Api xmlString2Api(String string) throws ApiConfigException {
		// Since JDK's XPath strictly applies namespace,
		// the simplest and safest way to support both styles (with or without
		// namespace)
		// is remove the namespace definition.
		string = removeNameSpace(string);
		DefaultApi api = new DefaultApi();
		try {
			Node node = (Node) xPath.evaluate("/api", new InputSource(
					new StringReader(string)), XPathConstants.NODE);
			NamedNodeMap attributes = node.getAttributes();
			api.setName(attributes.getNamedItem("name").getNodeValue());
			api.setSupportedVersions(parseSupportedVersions(attributes));
			api.setRedirectUrl(parseRedirectUrl(attributes));
			Node aliasesNode = attributes.getNamedItem("aliases");
			if (aliasesNode != null) {
				String[] aliasArray = StringUtils.split(aliasesNode
						.getNodeValue(), ',');
				api.setAliases(Arrays.asList(aliasArray));
			}
		} catch (XPathExpressionException e) {
			throw new ApiConfigException(e);
		}
		try {
			Node node = (Node) xPath.evaluate("/api/type", new InputSource(
					new StringReader(string)), XPathConstants.NODE);
			if (node != null) {
				ApiType type = ApiType.strptype(node.getTextContent());
				api.setApiType(type);
			}
		} catch (XPathExpressionException e) {
			throw new ApiConfigException(e);
		}

		// Parse hsf tag
		try {
			Node node = (Node) xPath.evaluate("/api/hsf", new InputSource(
					new StringReader(string)), XPathConstants.NODE);
			if (node != null) {
				NamedNodeMap attributes = node.getAttributes();
				api.setHsfInterfaceName(getAttributeValue(attributes,
						"interface_name"));
				api.setHsfInterfaceVersion(getAttributeValue(attributes,
						"interface_version"));
				api.setHsfMethodName(getAttributeValue(attributes,
						"method_name"));
				api.setHsfTimeout(getAttributeLong(attributes,
						"timeout"));
			}
		} catch (XPathExpressionException e) {
			throw new ApiConfigException(e);
		}

		try {
			// FIXME 每次必须new new InputSource new StringReader ?
			NodeList nodes = (NodeList) xPath.evaluate(
					"/api/params/protocol/must/param/@name", new InputSource(
							new StringReader(string)), XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				api.addProtocolMustParameter(node.getNodeValue());
			}
		} catch (XPathExpressionException e) {
			throw new ApiConfigException(e);
		}

		try {
			NodeList nodes = (NodeList) xPath.evaluate(
					"/api/params/protocol/private/param/@name",
					new InputSource(new StringReader(string)),
					XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				api.addProtocolPrivateParameter(node.getNodeValue());
			}
		} catch (XPathExpressionException e) {
			throw new ApiConfigException(e);
		}

		String templateName = null;
		try { // Get the template name
			templateName = (String) xPath.evaluate(
					"/api/params/application/@template", new InputSource(
							new StringReader(string)), XPathConstants.STRING);
			if (StringUtil.isEmpty(templateName)) {
				// XPath would treat 'not appear' as 'empty string'.
				templateName = null;
			}
		} catch (XPathExpressionException e) {
			throw new ApiConfigException(e);
		}

		Template template = null;

		if (templateName != null) {
			template = templates.get(templateName);
		}

		try { // Application Must Parameters.
			NodeList nodes = (NodeList) xPath.evaluate(
					"/api/params/application/must/param", new InputSource(
							new StringReader(string)), XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				ApiApplicationParameter param = parseParameter(node,
						Boolean.TRUE);

				// Apply template.
				if (template != null) {
					param.applyParameterTemplate(template.getParameter(param
							.getName()));
				}
				api.addApplicationMustParameter(param);

			}
		} catch (XPathExpressionException e) {
			throw new ApiConfigException(e);
		}

		try { // Application Combine Parameters.
			NodeList nodes = (NodeList) xPath.evaluate(
					"/api/params/application/combine/choise", new InputSource(
							new StringReader(string)), XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if (node.getNodeName().equals("choise")) {
					ParameterCombine pc = parseParameterCombine(node, api);
					api.addApplicationCombine(pc);
				}
			}
		} catch (XPathExpressionException e) {
			throw new ApiConfigException(e);
		}

		// Whether validate optional params, global switch, default to NOT
		// validate
		Boolean validateOptionalParams = Boolean.FALSE;
		try {
			Node node = (Node) xPath.evaluate(
					"/api/params/application/optional", new InputSource(
							new StringReader(string)), XPathConstants.NODE);
			if (node != null) {
				NamedNodeMap attributes = node.getAttributes();
				// If not set, default to false.
				String validateText = getAttributeValue(attributes, "validate");
				if (validateText != null) {
					validateOptionalParams = Boolean.parseBoolean(validateText);
				}
			}
		} catch (XPathExpressionException e) {
			throw new ApiConfigException(e);
		}

		try { // Application Optional Parameters.
			NodeList nodes = (NodeList) xPath.evaluate(
					"/api/params/application/optional/param", new InputSource(
							new StringReader(string)), XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				ApiApplicationParameter param = parseParameter(node,
						validateOptionalParams);

				// Apply template.
				if (template != null) {
					param.applyParameterTemplate(template.getParameter(param
							.getName()));
				}
				api.addApplicationOptionalParameter(param);

			}
		} catch (XPathExpressionException e) {
			throw new ApiConfigException(e);
		}

		/**
		 * added by yueqian support page api
		 */

		try {
			Node node = (Node) xPath.evaluate("/api/requesturl",
					new InputSource(new StringReader(string)),
					XPathConstants.NODE);
			if (node != null) {
				String url = node.getAttributes().getNamedItem("url")
						.getNodeValue();
				api.setRequestURL(url);
			}
		} catch (XPathExpressionException e) {
			throw new ApiConfigException(e);
		}

		return api;

	}

	/**
	 * @param node
	 * @param api
	 * @param true1
	 * @return
	 * @throws ApiConfigException
	 */
	private ParameterCombine parseParameterCombine(Node node, DefaultApi api)
			throws ApiConfigException {
		NodeList childNodes = node.getChildNodes();
		ParameterChoiser pc = new ParameterChoiser();
		for (int j = 0; j < childNodes.getLength(); j++) {
			Node cn = childNodes.item(j);
			if (cn.getNodeName().equals("together")) {
				ParameterTogether together = parseParameterTogether(cn, api);
				pc.addParameterTogether(together);
			}
		}
		return pc;
	}

	private ParameterTogether parseParameterTogether(Node node, DefaultApi api)
			throws ApiConfigException {
		NodeList childNodes = node.getChildNodes();
		ParameterTogether together = new ParameterTogether();
		for (int j = 0; j < childNodes.getLength(); j++) {
			Node cn = childNodes.item(j);
			if (cn.getNodeName().equals("param")) {
				ApiApplicationParameter param = parseParameter(cn, Boolean.TRUE);
				together.addApplicationParameter(param);
				api.addApplicationCombineParameter(param);
			}
		}
		return together;
	}

	private String parseRedirectUrl(NamedNodeMap attributes) {
		Node node = attributes.getNamedItem("redirectUrl");
		String text = null;
		if (node != null) {
			text = node.getNodeValue();
		}

		if (StringUtils.isEmpty(text)) {
			text = null;
		}

		return text;
	}

	private String[] parseSupportedVersions(NamedNodeMap attributes) {
		Node vertionNode = attributes.getNamedItem("v");
		String versionText = null;
		if (vertionNode != null) {
			versionText = vertionNode.getNodeValue();
		}
		if (StringUtils.isEmpty(versionText)) {
			versionText = "1.0";
		}

		String[] supportedVersions = StringKit.splitByComma(versionText);
		return supportedVersions;
	}

	private String removeNameSpace(String string) {
		string = string.replace("xmlns=\"http://apidefine.taobao.com\"", "");
		string = string.replace(
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
		string = string
				.replace(
						"xsi:schemaLocation=\"http://apidefine.taobao.com http://10.1.5.104/top/apidefine.xsd\"",
						"");
		return string;
	}

	/**
	 * parse a parameter node into ApiApplicationParameter.
	 * 
	 * @param node
	 * @return
	 * @throws ApiConfigException
	 */
	ApiApplicationParameter parseParameter(Node node, boolean defaultValidate)
			throws ApiConfigException {
		NamedNodeMap attributes = node.getAttributes();
		String name = attributes.getNamedItem("name").getNodeValue();

		String type = getAttributeValue(attributes, "type");
		// 中文参数名
		String cnName = getAttributeValue(attributes, "cn_name");
		// Size of type. It's simply the length of the string
		String length = getAttributeValue(attributes, "length");
		String maxValue = getAttributeValue(attributes, "max_value");
		String minValue = getAttributeValue(attributes, "min_value");
		String defaultValue = getAttributeValue(attributes, "default_value");
		String validate = getAttributeValue(attributes, "validate");
		String expected = getAttributeValue(attributes, "expected");
		String fileExt = getAttributeValue(attributes, "file_ext");
		String maxListSize = getAttributeValue(attributes, "max_list_size");
		String needTrim = getAttributeValue(attributes, "need_trim");

		Type paramType = null;
		if ("string".equals(type)) {
			paramType = Type.STRING;
		} else if ("int".equals(type)) {
			paramType = Type.INT;
		} else if ("number".equals(type)) {
			paramType = Type.NUMBER;
		} else if ("field_list".equals(type)) {
			paramType = Type.FIELD_LIST;
		} else if ("date".equals(type)) {
			paramType = Type.DATE;
		} else if ("boolean".equals(type)) {
			paramType = Type.BOOLEAN;
		} else if ("price".equals(type)) {
			paramType = Type.PRICE;
		} else if("byte[]".equals(type)){
			paramType= Type.BYTE_ARRAY;
		}

		ApiApplicationParameter param = new ApiApplicationParameter(name,
				paramType);

		param.setCnName(cnName);
		param.setDefaultValue(defaultValue);

		if (length != null) {
			param.setLength(Integer.parseInt(length));
		}

		if (maxValue != null) {
			param.setMaxValue(new BigInteger(maxValue));
		}

		if (minValue != null) {
			param.setMinValue(new BigInteger(minValue));
		}

		if (validate != null) {
			param.setValidate(Boolean.parseBoolean(validate));
		} else { // validate is null use default validate.
			param.setValidate(defaultValidate);
		}
		
		if(expected != null) {
			param.setExpected(expected);
		}
		
		if (fileExt != null) {
			String[] fileExtensions = StringKit.splitByComma(fileExt);
			
			for (int i = 0; i < fileExtensions.length; i ++) {
				fileExtensions[i] = "." + fileExtensions[i].toLowerCase();
			}
			
			param.setFileExtensions(fileExtensions);
		}
		
		if (maxListSize != null) {
			param.setMaxListSize(Integer.parseInt(maxListSize));
		}

		if(needTrim != null) {
			param.setNeedTrim(Boolean.parseBoolean(needTrim));
		} else {
			//else omitted, can be set by paramType, but the type is not final,
			//@see ApiApplicationParameter#isNeedTrim()	
		}
		
		return param;
	}
	
	Long getAttributeLong(NamedNodeMap attributes, String attributeName) {
		String attributeValue = getAttributeValue(attributes, attributeName);
		return attributeValue == null ? null : Long.valueOf(attributeValue);
	}

	/**
	 * Fetch the attribute value from the NamedNodeMap.
	 * <p>
	 * If the attribute node doesn't exist, return null.
	 * <p>
	 * Use 'friend' access privilege for easier unit test.
	 * 
	 * @param attributes
	 * @param attributeName
	 * @return
	 */
	String getAttributeValue(NamedNodeMap attributes, String attributeName) {
		Node node = attributes.getNamedItem(attributeName);
		String text = null;
		if (node != null) {
			text = node.getNodeValue();
		}

		if (StringUtils.isEmpty(text)) {
			text = null;
		}

		return text;
	}

	public Api xml2Api(String filename) throws ApiConfigException {
		try {
			return xml2Api(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			throw new ApiConfigException(e);
		}
	}

	/**
	 * There's a reason to use 'setXXX' method name.
	 * <p>
	 * It could be easily set by Spring, which would be convenient for unit
	 * test.
	 * 
	 * @param templateConfigs
	 */
	public void setTemplateConfigs(List<Resource> templateConfigs) {
		if (templateConfigs == null || templateConfigs.isEmpty()) {
			return;
		}
		parseTemplateConfigs(templateConfigs);

	}

	/**
	 * Parse the configure files into a template map.
	 * 
	 * @param templateConfigs
	 */
	void parseTemplateConfigs(List<Resource> templateConfigs) {
		for (Resource templateConfig : templateConfigs) {
			try {
				InputStream inputStream = templateConfig.getInputStream();
				String templateText = IOUtils.toString(inputStream, "utf-8");
				Template template = parseTemplateConfigString(templateText);
				templates.put(template.getName(), template);
			} catch (Exception e) {
				// Since we don't want a single failure stopping
				// the whole process, we simply catch the error and
				// move on.
				log.error("", e);
			}
		}
	}

	/**
	 * Parse a single template configure file.
	 * 
	 * @param string
	 * @return
	 * @throws ApiConfigException
	 */
	Template parseTemplateConfigString(String string) throws ApiConfigException {
		Template template = new Template();
		try {
			Node node = (Node) xPath.evaluate("/template", new InputSource(
					new StringReader(string)), XPathConstants.NODE);
			NamedNodeMap attributes = node.getAttributes();
			template.setName(attributes.getNamedItem("name").getNodeValue());
		} catch (XPathExpressionException e) {
			throw new ApiConfigException(e);
		}

		try {
			NodeList nodes = (NodeList) xPath.evaluate("/template/param",
					new InputSource(new StringReader(string)),
					XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				ApiApplicationParameter param = parseParameter(node, false);
				template.addParameter(param);
			}
		} catch (XPathExpressionException e) {
			throw new ApiConfigException(e);
		}

		return template;

	}

	static class Template {

		String name;

		Map<String, ApiApplicationParameter> params = new Hashtable<String, ApiApplicationParameter>();

		public String getName() {
			return name;
		}

		public ApiApplicationParameter getParameter(String paramName) {
			return params.get(paramName);
		}

		public void addParameter(ApiApplicationParameter param) {
			params.put(param.getName(), param);
		}

		public void setName(String name) {
			this.name = name;
		}

		public Map<String, ApiApplicationParameter> getParameterMap() {
			return params;
		}

	}

}
