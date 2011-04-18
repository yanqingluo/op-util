package com.taobao.top.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.taobao.top.core.ApiApplicationParameter.Type;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.timwrapper.manager.TadgetManager;

public class ApiApplicationParameterTest {

	@Test
	public void testCheckInput_StringType() {
		ApiApplicationParameter param = new ApiApplicationParameter(
				"paramString", Type.STRING);		
		// /////////////////
		// Check String type.

		// Null and empty.
		{
			
			MockHttpServletRequest req = new MockHttpServletRequest();
			MockHttpServletResponse resp = new MockHttpServletResponse();
			TadgetManager tad = new TadgetManager();
			TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
		assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, null));
		}
		{
			MockHttpServletRequest req = new MockHttpServletRequest();
			MockHttpServletResponse resp = new MockHttpServletResponse();
			TadgetManager tad = new TadgetManager();
			TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
			assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, ""));
		}
		{
			MockHttpServletRequest req = new MockHttpServletRequest();
			MockHttpServletResponse resp = new MockHttpServletResponse();
			TadgetManager tad = new TadgetManager();
			TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
			// Length default to -1 means no checking.
		String valueString = "VeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongString";
		assertNull(checkInput(pipeInput, param, valueString));
		}
		// Then assign the length constraint.
		param.setLength(10);
		{
			String valueString = "VeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongString";
			MockHttpServletRequest req = new MockHttpServletRequest();
			MockHttpServletResponse resp = new MockHttpServletResponse();
			TadgetManager tad = new TadgetManager();
			TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));
		}
		// Now, we make it as expected.
		{
			MockHttpServletRequest req = new MockHttpServletRequest();
			MockHttpServletResponse resp = new MockHttpServletResponse();
			TadgetManager tad = new TadgetManager();
			TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
			String valueString = "TenAsciiLT"; // Exactly 10 letters.
		assertNull(checkInput(pipeInput, param, valueString));
		}
		{
			MockHttpServletRequest req = new MockHttpServletRequest();
			MockHttpServletResponse resp = new MockHttpServletResponse();
			TadgetManager tad = new TadgetManager();
			TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
			String valueString = "不是正好十个汉字的吗"; // Exactly 10 Chinese Character.
		assertNull(checkInput(pipeInput, param, valueString));
		}
		{
			MockHttpServletRequest req = new MockHttpServletRequest();
			MockHttpServletResponse resp = new MockHttpServletResponse();
			TadgetManager tad = new TadgetManager();
			TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
			// Now, we make it as expected.
			String valueString = "TenAsciiLT."; // Exactly 11 letters.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));
		}
		{
			MockHttpServletRequest req = new MockHttpServletRequest();
			MockHttpServletResponse resp = new MockHttpServletResponse();
			TadgetManager tad = new TadgetManager();
			TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
			String valueString = "不是正好十个汉字的吗？"; // Exactly 11 Chinese characters.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));
		}
		
		{
			MockHttpServletRequest req = new MockHttpServletRequest();
			MockHttpServletResponse resp = new MockHttpServletResponse();
			TadgetManager tad = new TadgetManager();
			TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
			// OK, we now mix English and Chinese characters up.		
			String valueString = "长度为11的Str吧"; // Exactly 10 characters.
		assertNull(checkInput(pipeInput, param, valueString));
		}
		{
			MockHttpServletRequest req = new MockHttpServletRequest();
			MockHttpServletResponse resp = new MockHttpServletResponse();
			TadgetManager tad = new TadgetManager();
			TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
			// A longer mixture.
			String valueString = "长度为11的Str吧?"; // Exactly 11 characters.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));
		}
		{
			MockHttpServletRequest req = new MockHttpServletRequest();
			MockHttpServletResponse resp = new MockHttpServletResponse();
			TadgetManager tad = new TadgetManager();
			TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
			// ensure no change with string type
			String valueString = "a , b,, c,";

		assertNull(checkInput(pipeInput, param, valueString));
		assertEquals("a , b,, c,", pipeInput.getString(param.getName(),true));
		}
	}

	@Test
	public void testCheckInput_DateType() {
		ApiApplicationParameter param = new ApiApplicationParameter(
				"paramDate", Type.DATE);
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		TadgetManager tad = new TadgetManager();
		TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
		// /////////////////
		// Check String type.

		// Null and empty.
		assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, null));
		assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, ""));

		// Length default to -1 means no checking.
		String valueString = "2012-12-24 00:00:00";
		assertNull(checkInput(pipeInput, param, valueString));

		valueString = "2012-12-24 00:00:00.123";
		assertNull(checkInput(pipeInput, param, valueString));

		// Then assign the length constraint.
		param.setLength(10);
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));

		param.setLength(100);
		valueString = "2012-12-2X 00:00:0.123";
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));

		param.setLength(100);
		valueString = "2012-12-2 0:0";
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));

	}

	@Test
	public void testCheckInput_BooleanType() {
		ApiApplicationParameter param = new ApiApplicationParameter(
				"paramDate", Type.BOOLEAN);
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		TadgetManager tad = new TadgetManager();
		TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
		// /////////////////
		// Check String type.

		// Null and empty.
		assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, null));
		assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, ""));

		// Length default to -1 means no checking.
		String valueString = "true";
		assertNull(checkInput(pipeInput, param, valueString));

		valueString = "false";
		assertNull(checkInput(pipeInput, param, valueString));

		// Then assign the length constraint.
		valueString = "yes";
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));

		valueString = "no";
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));

		valueString = "不";
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));

	}

	@Test
	public void testCheckInput_FieldListType() {
		ApiApplicationParameter param = new ApiApplicationParameter(
				"paramFieldList", Type.FIELD_LIST);
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		TadgetManager tad = new TadgetManager();
		TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
		// /////////////////
		// Check String type.

		// Null and empty.
		assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, null));
		assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, ""));

		// Length default to -1 means no checking.
		String valueString = "VeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongStringVeryLongString";
		assertNull(checkInput(pipeInput, param, valueString));
		// Then assign the length constraint.
		param.setLength(10);
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));

		// Now, we make it as expected.
		valueString = "TenAsciiLT"; // Exactly 10 letters.

		valueString = "TenAsciiLT"; // Exactly 10 letters.
		assertNull(checkInput(pipeInput, param, valueString));

		valueString = "不是正好十个汉字的吗"; // Exactly 10 Chinese Character.
		assertNull(checkInput(pipeInput, param, valueString));

		// Now, we make it as expected.
		valueString = "TenAsciiLT."; // Exactly 11 letters.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));

		valueString = "不是正好十个汉字的吗？"; // Exactly 11 Chinese characters.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));

		// OK, we now mix English and Chinese characters up.
		valueString = "长度为11的Str吧"; // Exactly 10 characters.
		assertNull(checkInput(pipeInput, param, valueString));

		// A longer mixture.
		valueString = "长度为11的Str吧?"; // Exactly 11 characters.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));

		// Real field business starts here.
		valueString = "with,with";
		assertNull(checkInput(pipeInput, param, valueString));

		// other than [a-zA-z0-9_]
		valueString = "with?with";
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));

		// other than [a-zA-z0-9_]
		valueString = "with?,!";
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueString));

		// Some of fields are other than [a-zA-z0-9_]
		TopPipeInput input = pipeInput;
		valueString = "w?w, h";
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(input, param,
				valueString));

		// Single element list
		valueString = "with_with";
		input = pipeInput;
		assertNull(checkInput(input, param, valueString));
		assertEquals("with_with", input.getString(param.getName(),true));

		// space trim
		valueString = "a , b , c";
		input = pipeInput;
		assertNull(checkInput(input, param, valueString));
		assertEquals("a , b , c", input.getString(param.getName(),true));

		// extra comma
		valueString = "a , b , c,";
		input = pipeInput;
		assertNull(checkInput(input, param, valueString));
		assertEquals("a , b , c,", input.getString(param.getName(),true));

		// consecutive comma
		valueString = "a , b,, c,";
		input = pipeInput;
		assertNull(checkInput(input, param, valueString));
		assertEquals("a , b,, c,", input.getString(param.getName(),true));

		// space element, ignored
		valueString = ",a,b, , c,";
		input = pipeInput;
		assertNull(checkInput(input, param, valueString));
		assertEquals(",a,b, , c,", input.getString(param.getName(),true));
		
		// Set the maxListSize, would be ignored.
		// space element, ignored
		valueString = ",a,b, , c,";
		input = pipeInput;
		param.setMaxListSize(2);
		assertNull(checkInput(input, param, valueString));
		assertEquals(",a,b, , c,", input.getString(param.getName(),true));
	}

	@Test
	public void testCheckInput_NumberType() {

		ApiApplicationParameter param = new ApiApplicationParameter(
				"paramNumber", Type.NUMBER);
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		TadgetManager tad = new TadgetManager();
		TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
		// Null and empty.
		assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, null));
		assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, ""));

		String valueNumber = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456";
		assertNull(checkInput(pipeInput, param, valueNumber));

		param.setLength(10);

		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		// Now, we make it as expected.
		valueNumber = "1234567890"; // Exactly 10 digits.
		assertNull(checkInput(pipeInput, param, valueNumber));

		valueNumber = "一二三四五六七八九十"; // Exactly 10 Chinese Character.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		// Now, we make it as expected.
		valueNumber = "12345678901"; // Exactly 11 digits.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		valueNumber = "一二三四五六七八九十 零"; // Exactly 11 Chinese Character.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		// ////////////////////
		// Range checking. Since the ValidateUtil.isInRange() has been fully
		// tested,
		// we would simplify the test here.

		valueNumber = "1234567890";

		// ensure no change with number type
		valueNumber = "1234567890";
		TopPipeInput input = pipeInput;
		assertNull(checkInput(input, param, valueNumber));
		assertEquals("1234567890", input.getString(param.getName(),true));

		// Min set.
		param.setMinValue(new BigInteger("1234567889"));
		assertNull(checkInput(pipeInput, param, valueNumber));

		// Both Min Max set
		param.setMaxValue(new BigInteger("1234567891"));
		assertNull(checkInput(pipeInput, param, valueNumber));

		// Min not satisfied.
		param.setMinValue(new BigInteger("1234567891"));
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		// Both Min and Max are not satisfied.
		param.setMaxValue(new BigInteger("1234567889"));
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

	}

	@Test
	public void testCheckInput_PriceType() {

		ApiApplicationParameter param = new ApiApplicationParameter(
				"paramPrice", Type.PRICE);
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		TadgetManager tad = new TadgetManager();
		TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
		// Null and empty.
		assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, null));
		assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, ""));

		String valueNumber = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456";
		assertNull(checkInput(pipeInput, param, valueNumber));
		
		valueNumber = "124.23";
		assertNull(checkInput(pipeInput, param, valueNumber));
		
		valueNumber = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456";
		param.setLength(10);

		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		// Now, we make it as expected.
		valueNumber = "1234567890"; // Exactly 10 digits.
		assertNull(checkInput(pipeInput, param, valueNumber));

		valueNumber = "一二三四五六七八九十"; // Exactly 10 Chinese Character.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		// Now, we make it as expected.
		valueNumber = "12345678901"; // Exactly 11 digits.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		valueNumber = "一二三四五六七八九十 零"; // Exactly 11 Chinese Character.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		// ////////////////////
		// Range checking is not applicable for PRICE type

		valueNumber = "1234567890";

		// ensure no change with number type
		valueNumber = "1234567890";
		TopPipeInput input = pipeInput;
		assertNull(checkInput(input, param, valueNumber));
		assertEquals("1234567890", input.getString(param.getName(),true));

		// Min set.
		param.setMinValue(new BigInteger("1234567889"));
		assertNull(checkInput(pipeInput, param, valueNumber));

		// Both Min Max set
		param.setMaxValue(new BigInteger("1234567891"));
		assertNull(checkInput(pipeInput, param, valueNumber));

		// Min not satisfied.
		param.setMinValue(new BigInteger("1234567891"));
		assertNull(checkInput(pipeInput, param, valueNumber));

	}

	@Test
	public void testApplyParameterTemplate() {
		// Since the logic of applyParameterTemplate() doesn't care about the
		// Type.
		// Just do the test for NUMBER type.

		ApiApplicationParameter param = new ApiApplicationParameter(
				"paramNumber", null);
		ApiApplicationParameter template = new ApiApplicationParameter(
				"paramNumber", null);

		// ///////////////
		// Param only have name, as well as template.

		param.applyParameterTemplate(template);

		// Name would never changed.
		assertEquals("paramNumber", param.getName());
		assertNull(param.getType());
		// Since cn_name is note assigned, just use the name.
		assertEquals("paramNumber", param.getCnOrEnglishName());
		assertNull(param.getDefaultValue());
		assertEquals(-1, param.getLength());
		assertNull(param.getDefaultValue());
		assertNull(param.getMinValue());
		assertNull(param.getMaxValue());

		// ///////////////
		// Param has everything; Template has nothing.
		param = new ApiApplicationParameter("paramNumber", null);
		template = new ApiApplicationParameter("paramNumber", null);

		param.setCnName("中文名");
		param.setDefaultValue("9999");
		param.setLength(100);
		param.setType(Type.NUMBER);
		param.setMinValue(new BigInteger("-1000"));
		param.setMaxValue(new BigInteger("1000"));

		param.applyParameterTemplate(template);

		// Name would never changed.
		assertEquals("paramNumber", param.getName());
		assertEquals(Type.NUMBER, param.getType());
		// Since cn_name is note assigned, just use the name.
		assertEquals("中文名", param.getCnOrEnglishName());
		assertEquals(100, param.getLength());
		assertEquals("9999", param.getDefaultValue());
		assertEquals(new BigInteger("-1000"), param.getMinValue());
		assertEquals(new BigInteger("1000"), param.getMaxValue());

		// ///////////////
		// Param has nothing; Template has everything.
		param = new ApiApplicationParameter("paramNumber", null);
		template = new ApiApplicationParameter("paramNumber", null);

		template.setCnName("中文名");
		template.setDefaultValue("9999");
		template.setLength(100);
		template.setType(Type.NUMBER);
		template.setMinValue(new BigInteger("-1000"));
		template.setMaxValue(new BigInteger("1000"));

		param.applyParameterTemplate(template);

		// Name would never changed.
		assertEquals("paramNumber", param.getName());
		assertEquals(Type.NUMBER, param.getType());
		// Since cn_name is note assigned, just use the name.
		assertEquals("中文名", param.getCnOrEnglishName());
		assertEquals(100, param.getLength());
		assertEquals("9999", param.getDefaultValue());
		assertEquals(new BigInteger("-1000"), param.getMinValue());
		assertEquals(new BigInteger("1000"), param.getMaxValue());

	}

	ErrorCode checkInput(TopPipeInput input, ApiApplicationParameter param,
			final String value) {
		// DefaultApiInput input = pipeInput;
		MockHttpServletRequest mockReq = (MockHttpServletRequest)input.getRequest();
		mockReq.setParameter(param.getName(), value);
		return param.checkInput(input);
	}

	@Test
	public void testCheckPrimativeTypeWithinList() {
		ApiApplicationParameter param = new ApiApplicationParameter(
				"paramNumber", Type.NUMBER);
		param.setMaxListSize(2);
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		TadgetManager tad = new TadgetManager();
		TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
		// Null and empty.
		assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, null));
		assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, ""));

		String valueNumber = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456";
		assertNull(checkInput(pipeInput, param, valueNumber));

		param.setLength(10);

		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		// Now, we make it as expected.
		valueNumber = "1234567890"; // Exactly 10 digits.
		assertNull(checkInput(pipeInput, param, valueNumber));

		valueNumber = "一二三四五六七八九十"; // Exactly 10 Chinese Character.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		// Now, we make it as expected.
		valueNumber = "12345678901"; // Exactly 11 digits.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		valueNumber = "一二三四五六七八九十 零"; // Exactly 11 Chinese Character.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		// ////////////////////
		// Range checking. Since the ValidateUtil.isInRange() has been fully
		// tested,
		// we would simplify the test here.

		valueNumber = "1234567890";

		// ensure no change with number type
		valueNumber = "1234567890";
		TopPipeInput input = pipeInput;
		assertNull(checkInput(input, param, valueNumber));
		assertEquals("1234567890", input.getString(param.getName(),true));

		// Min set.
		param.setMinValue(new BigInteger("1234567889"));
		assertNull(checkInput(pipeInput, param, valueNumber));

		// Both Min Max set
		param.setMaxValue(new BigInteger("1234567891"));
		assertNull(checkInput(pipeInput, param, valueNumber));

		// Min not satisfied.
		param.setMinValue(new BigInteger("1234567891"));
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		// Both Min and Max are not satisfied.
		param.setMaxValue(new BigInteger("1234567889"));
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		// -------- Two number in the list.
		// Min not satisfied.
		param.setMinValue(new BigInteger("1"));
		// Both Min and Max are not satisfied.
		param.setMaxValue(new BigInteger("3"));

		valueNumber = "1, 2";
		assertNull(checkInput(pipeInput, param, valueNumber));

		// -------- Three number in the list.
		valueNumber = "1, 2,3";
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(pipeInput, param, valueNumber));

	}
	
	@Test
	public void testCheckInput_PriceTypeWithinList() {

		ApiApplicationParameter param = new ApiApplicationParameter(
				"paramPrice", Type.PRICE);
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		TadgetManager tad = new TadgetManager();
		TopPipeInput pipeInput = new TopPipeInput(req,resp,tad);
		param.setMaxListSize(2);
		
		// Null and empty.
		assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, null));
		assertEquals(ErrorCode.MISSING_REQUIRED_ARGUMENTS, checkInput(
				pipeInput, param, ""));

		String valueNumber = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456";
		assertNull(checkInput(pipeInput, param, valueNumber));
		
		valueNumber = "124.23";
		assertNull(checkInput(pipeInput, param, valueNumber));
		
		valueNumber = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456";
		param.setLength(10);

		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		// Now, we make it as expected.
		valueNumber = "1234567890"; // Exactly 10 digits.
		assertNull(checkInput(pipeInput, param, valueNumber));

		valueNumber = "一二三四五六七八九十"; // Exactly 10 Chinese Character.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		// Now, we make it as expected.
		valueNumber = "12345678901"; // Exactly 11 digits.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		valueNumber = "一二三四五六七八九十 零"; // Exactly 11 Chinese Character.
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(
				pipeInput, param, valueNumber));

		// ////////////////////
		// Range checking is not applicable for PRICE type

		valueNumber = "1234567890";

		// ensure no change with number type
		valueNumber = "1234567890";
		TopPipeInput input = pipeInput;
		assertNull(checkInput(input, param, valueNumber));
		assertEquals("1234567890", input.getString(param.getName(),true));

		// Min set.
		param.setMinValue(new BigInteger("1234567889"));
		assertNull(checkInput(pipeInput, param, valueNumber));

		// Both Min Max set
		param.setMaxValue(new BigInteger("1234567891"));
		assertNull(checkInput(pipeInput, param, valueNumber));

		// Min not satisfied.
		param.setMinValue(new BigInteger("1234567891"));
		assertNull(checkInput(pipeInput, param, valueNumber));


		// -------- Two number in the list.
		// Min not satisfied.

		valueNumber = "1.12, 2.23";
		assertNull(checkInput(pipeInput, param, valueNumber));

		// -------- Three number in the list.
		valueNumber = "1.12, 2.23,300";
		assertEquals(ErrorCode.INVALID_ARGUMENTS, checkInput(pipeInput, param, valueNumber));
		
		
	}

}
