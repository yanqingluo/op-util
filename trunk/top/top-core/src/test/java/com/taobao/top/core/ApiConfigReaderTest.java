package com.taobao.top.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.top.core.ApiApplicationParameter.Type;
import com.taobao.top.core.ApiConfigReader.Template;
import com.taobao.top.core.mock.MockedNamedMap;
import com.taobao.top.core.mock.MockedNode;

/**
 * 
 * @author haishi
 * 
 */
public class ApiConfigReaderTest {
	ApiConfigReader reader = new ApiConfigReader();

	@Test
	public void testXml2ApiInputStream() {
	}

	@Test
	public void testXmlString2Api_NoAgpplicationParam()
			throws ApiConfigException {
		Api api = reader
				.xml2Api(getXml("/com/taobao/top/core/taobao.jianghu.poke.poke2User_ForTest.xml"));

		assertNotNull(api);

		assertTrue(api.getApplicationMustParams().isEmpty());
		assertTrue(api.getApplicationOptionalParams().isEmpty());
	}

	@Test
	public void testXmlString2Api_ApplicationMust() throws ApiConfigException {
		Api api = reader
				.xml2Api(getXml("/com/taobao/top/core/ApiConfigReaderTest.api.xml"));
		Map<String, ApiApplicationParameter> nameToParam = new Hashtable<String, ApiApplicationParameter>();
		// Test Application must
		for (ApiApplicationParameter param : api.getApplicationMustParams()) {
			nameToParam.put(param.getName(), param);
		}

		// String
		ApiApplicationParameter param = nameToParam.get("string");
		assertEquals(Type.STRING, param.getType());
		assertEquals(-1, param.getLength());
		assertEquals("我是字符串", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());
		assertTrue(param.isNeedTrim());

		// String with length
		param = nameToParam.get("stringLength");
		assertEquals(Type.STRING, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我是字符串", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());
		assertFalse(param.isNeedTrim());

		// Number
		param = nameToParam.get("number");
		assertEquals(Type.NUMBER, param.getType());
		assertNull(param.getMaxListSize());
		assertEquals(-1, param.getLength());
		assertEquals("我是数字", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());
		assertTrue(param.isNeedTrim());

		// Number List of two
		param = nameToParam.get("numberList");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(new Integer(2), param.getMaxListSize());
		assertEquals(-1, param.getLength());
		assertEquals("我是数字列表", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());

		// Number with length
		param = nameToParam.get("numberLength");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我是数字", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());

		// Number with length and min_value
		param = nameToParam.get("numberLengthMin");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我是数字", param.getCnOrEnglishName());
		assertEquals(new BigInteger("0"), param.getMinValue());
		assertNull(param.getMaxValue());
		assertFalse(param.isNeedTrim());

		// Number with length and max_value
		param = nameToParam.get("numberLengthMax");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我是数字", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertEquals(new BigInteger("1"), param.getMaxValue());

		// Number with length , min_value, and max_value
		param = nameToParam.get("numberLengthMinMax");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我是数字", param.getCnOrEnglishName());
		assertEquals(new BigInteger("0"), param.getMinValue());
		assertEquals(new BigInteger("1"), param.getMaxValue());

		// Field List
		param = nameToParam.get("fieldList");
		assertEquals(Type.FIELD_LIST, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我是Field List", param.getCnOrEnglishName());
		assertFalse(param.isNeedTrim());

		// Byte Array
		param = nameToParam.get("byteArray");
		assertEquals(Type.BYTE_ARRAY, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我是Byte Array", param.getCnOrEnglishName());
		String[] fileExtensions = param.getFileExtensions();
		assertEquals(2, fileExtensions.length);
		String jpg = fileExtensions[0];
		String bmp = fileExtensions[1];
		assertFalse(param.isNeedTrim());

		assertEquals(".jpg", jpg);
		assertEquals(".bmp", bmp);

	}

	@Test
	public void testXmlString2Api_Hsf() throws ApiConfigException {
		Api api = reader
				.xml2Api(getXml("/com/taobao/top/core/ApiConfigReaderTest.api.xml"));

		assertEquals("MyInterfaceName", api.getHsfInterfaceName());
		assertEquals("1.0.0", api.getHsfInterfaceVersion());
		assertEquals("MyMethodName", api.getHsfMethodName());
	}

	@Test
	public void testXmlString2Api_ApplicationOptional()
			throws ApiConfigException {
		Api api = reader
				.xml2Api(getXml("/com/taobao/top/core/ApiConfigReaderTest.api.xml"));
		Map<String, ApiApplicationParameter> nameToParam = new Hashtable<String, ApiApplicationParameter>();
		// Test Application must
		for (ApiApplicationParameter param : api.getApplicationOptionalParams()) {
			nameToParam.put(param.getName(), param);
		}

		// String, validate
		ApiApplicationParameter param = nameToParam.get("stringOptional");
		assertEquals(Type.STRING, param.getType());
		assertEquals(10, param.getLength());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());
		assertTrue(param.isValidate());

		// String, not validate
		param = nameToParam.get("stringOptionalNotValidate");
		assertEquals(Type.STRING, param.getType());
		assertEquals(10, param.getLength());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());
		assertFalse(param.isValidate());

		// Number, validate
		param = nameToParam.get("numberOptional");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(-1, param.getLength());
		assertEquals(new BigInteger("0"), param.getMinValue());
		assertNull(param.getMaxValue());
		assertTrue(param.isValidate());

		param = nameToParam.get("numberOptionalNotValidate");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(-1, param.getLength());
		assertEquals(new BigInteger("0"), param.getMinValue());
		assertNull(param.getMaxValue());
		assertFalse(param.isValidate());

		// Field List
		param = nameToParam.get("fieldListOptional");
		assertEquals(Type.FIELD_LIST, param.getType());
		assertEquals(10, param.getLength());
		assertTrue(param.isValidate());

		param = nameToParam.get("fieldListOptionalNotValidate");
		assertEquals(Type.FIELD_LIST, param.getType());
		assertEquals(10, param.getLength());
		assertFalse(param.isValidate());

	}

	@Test
	public void testGetAttributeValue() {
		MockedNamedMap map = new MockedNamedMap();
		assertNull(reader.getAttributeValue(map, "length"));

		map.getMap().put("length", new MockedNode("TheValue"));

		assertEquals("TheValue", reader.getAttributeValue(map, "length"));

	}

	@Test
	public void testParseTemplateConfig() throws ApiConfigException,
			IOException {
		FileInputStream fis = new FileInputStream(
				getXml("/com/taobao/top/core/api-template-item_ApiConfigReaderTest.xml"));
		Template template = reader.parseTemplateConfigString(IOUtils.toString(
				fis, "utf-8"));
		assertEquals("item", template.getName());
		Map<String, ApiApplicationParameter> nameToParam = template
				.getParameterMap();

		// String
		ApiApplicationParameter param = nameToParam.get("string");
		assertEquals(Type.STRING, param.getType());
		assertEquals(-1, param.getLength());
		assertEquals("我是字符串", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());
		assertEquals("stringDefault", param.getDefaultValue());

		// String with length
		param = nameToParam.get("stringLength");
		assertEquals(Type.STRING, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我是字符串", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());
		assertEquals("stringLengthDefault", param.getDefaultValue());

		// Number
		param = nameToParam.get("number");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(-1, param.getLength());
		assertEquals("我是数字", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());
		assertEquals("numberDefault", param.getDefaultValue());

		// Number with length
		param = nameToParam.get("numberLength");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我是数字", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());
		assertEquals("numberLengthDefault", param.getDefaultValue());

		// Number with length and min_value
		param = nameToParam.get("numberLengthMin");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我是数字", param.getCnOrEnglishName());
		assertEquals(new BigInteger("0"), param.getMinValue());
		assertNull(param.getMaxValue());
		assertNull(param.getDefaultValue());

		// Number with length and max_value
		param = nameToParam.get("numberLengthMax");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我是数字", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertEquals(new BigInteger("1"), param.getMaxValue());
		assertNull(param.getDefaultValue());

		// Number with length , min_value, and max_value
		param = nameToParam.get("numberLengthMinMax");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我是数字", param.getCnOrEnglishName());
		assertEquals(new BigInteger("0"), param.getMinValue());
		assertEquals(new BigInteger("1"), param.getMaxValue());
		assertNull(param.getDefaultValue());

		fis.close();
	}

	@Test
	public void testParseTemplateConfigs_AndTemplateApplication()
			throws ApiConfigException, IOException {
		ApplicationContext springContext = new ClassPathXmlApplicationContext(
				"/com/taobao/top/core/spring-api_ApiConfigReaderTest.xml");
		ApiConfigReader reader = (ApiConfigReader) springContext
				.getBean("apiConfigReaderForTest");

		// NOTE: setTemplateConfigs() would be invoked by Spring injection
		// automatically.
		// FileInputStream fileInp
		FileInputStream fis = new FileInputStream(
				getXml("/com/taobao/top/core/ApiConfigReaderTest_extented.api.xml"));

		// Check api
		Api api = reader.xml2Api(fis);
		Map<String, ApiApplicationParameter> nameToParam = new Hashtable<String, ApiApplicationParameter>();
		for (ApiApplicationParameter param : api.getApplicationMustParams()) {
			nameToParam.put(param.getName(), param);
		}

		// String
		ApiApplicationParameter param = nameToParam.get("string");
		assertEquals(Type.STRING, param.getType());
		assertEquals(-1, param.getLength());
		assertEquals("我是字符串", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());
		assertEquals("stringDefault", param.getDefaultValue());

		// String with length
		param = nameToParam.get("stringLength");
		assertEquals(Type.STRING, param.getType());

		// 5 overwrites 10 in template.
		assertEquals(5, param.getLength());
		assertEquals("我要覆盖", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());
		assertEquals("stringLengthDefault", param.getDefaultValue());

		// Number
		param = nameToParam.get("number");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(-1, param.getLength());
		assertEquals("我是数字", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());
		assertEquals("numberDefault", param.getDefaultValue());

		// Number with length
		param = nameToParam.get("numberLength");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(5, param.getLength());
		assertEquals("我也要覆盖", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());
		assertEquals("numberLengthDefault", param.getDefaultValue());

		// Number with length and min_value
		param = nameToParam.get("numberLengthMin");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我是数字", param.getCnOrEnglishName());
		assertEquals(new BigInteger("0"), param.getMinValue());
		assertNull(param.getMaxValue());
		assertNull(param.getDefaultValue());

		// Number with length and max_value
		param = nameToParam.get("numberLengthMax");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我是数字", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertEquals(new BigInteger("1"), param.getMaxValue());
		assertNull(param.getDefaultValue());

		// Number with length , min_value, and max_value
		param = nameToParam.get("numberLengthMinMax");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我是数字", param.getCnOrEnglishName());
		assertEquals(new BigInteger("0"), param.getMinValue());
		assertEquals(new BigInteger("1"), param.getMaxValue());
		assertNull(param.getDefaultValue());

		// Not shown in template.
		param = nameToParam.get("notInTheTemplate");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(10, param.getLength());
		assertEquals("我不在模板中", param.getCnOrEnglishName());
		assertEquals(new BigInteger("-1000"), param.getMinValue());
		assertEquals(new BigInteger("1000"), param.getMaxValue());
		assertEquals("500", param.getDefaultValue());

		// /////////
		// Optional
		nameToParam = new Hashtable<String, ApiApplicationParameter>();
		for (ApiApplicationParameter optionalParam : api
				.getApplicationOptionalParams()) {
			nameToParam.put(optionalParam.getName(), optionalParam);
		}

		param = nameToParam.get("stringOptional");
		assertEquals(Type.STRING, param.getType());
		assertEquals(-1, param.getLength());
		assertEquals("我是可选字符串", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());
		assertEquals("stringOptionalDefault", param.getDefaultValue());

		param = nameToParam.get("numberOptional");
		assertEquals(Type.NUMBER, param.getType());
		assertEquals(-1, param.getLength());
		assertEquals("我是可选数字", param.getCnOrEnglishName());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());
		assertEquals("numberOptionalDefault", param.getDefaultValue());

		fis.close();

	}

	private String getXml(java.lang.String name) {
		return this.getClass().getResource(name).getFile();
	}

	@Test
	public void testProcessBOM() throws IOException {
		String filePathWithoutBOM = "/com/taobao/top/core/ApiConfigReaderTest_utf-8_file_withoutBOM.xml";
		String filePathWithBOM = "/com/taobao/top/core/ApiConfigReaderTest_utf-8_file_withBOM.xml";
		
		String encoding = "utf-8";

		BufferedInputStream stream = reader
				.processBOM(getClass().getResourceAsStream(filePathWithoutBOM));
		String expectedResult = IOUtils.toString(stream, encoding);
		//assertEquals(expected, result);

		stream = reader.processBOM(getClass().getResourceAsStream(filePathWithBOM));
		String result = IOUtils.toString(stream, encoding);
		assertEquals(expectedResult, result);

	}
}
