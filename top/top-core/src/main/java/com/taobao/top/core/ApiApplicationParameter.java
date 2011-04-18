/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

import java.io.Serializable;
import java.math.BigInteger;

import org.apache.commons.lang.StringUtils;

import com.taobao.top.common.lang.StringKit;
import com.taobao.top.common.validate.ValidateUtil;
import com.taobao.top.core.framework.TopPipeInput;

/**
 * Api参数对象
 * 
 * @version 2008-3-11
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class ApiApplicationParameter implements Serializable {

	private static final long serialVersionUID = -1048819024556017502L;

	public static enum Type {
		STRING, STR_LIST, INT_LIST, INT, NUMBER, FIELD_LIST, BYTE_ARRAY, BOOLEAN, DATE, PRICE
	}

	private String name;

	private Type type = Type.STRING;

	/**
	 * 中文参数名
	 */
	private String cnName;

	/**
	 * Constraint of value length. -1 means no constraint.
	 */
	private int length = -1;

	private BigInteger minValue;

	private BigInteger maxValue;

	private boolean validate;

	/**
	 * Only applicable while this parameter is an 'optional' parameter.
	 */
	private Object defaultValue;

	private String expected;
	
	private String[] fileExtensions;
	
	private Integer maxListSize; // null, 0, or negative mean it's not a list
	
	/**
	 * whether the parameter value need trim when sending to isp.
	 */
	private Boolean needTrim; 

	// /**
	// * Whether this parameter is optional.
	// * <p>
	// * The behavior of optional parameter is assign the default value
	// * to the ApiInput if the ApiInput input misses that parameter and
	// * the defaultValue is not null.
	// */
	// boolean optional;

	public ApiApplicationParameter(String name, Type type) {
		super();
		this.name = name;
		this.type = type;
	}

	/**
	 * @param cnName
	 *            the cnName to set
	 */
	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public String getName() {
		return name;
	}

	/**
	 * 取中文参数名优先
	 * 
	 * @return
	 */
	public String getCnOrEnglishName() {
		return this.cnName == null ? this.name : this.cnName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public BigInteger getMinValue() {
		return minValue;
	}

	public void setMinValue(BigInteger minValue) {
		this.minValue = minValue;
	}

	public BigInteger getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(BigInteger maxValue) {
		this.maxValue = maxValue;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public Integer getMaxListSize() {
		return maxListSize;
	}

	public void setMaxListSize(Integer listMaxSize) {
		this.maxListSize = listMaxSize;
	}
	
	
	// public boolean isOptional() {
	// return optional;
	// }
	//
	// public void setOptional(boolean optional) {
	// this.optional = optional;
	// }



	public boolean isNeedTrim() {
		if (needTrim == null) { // use the default standard
			setNeedTrim(type == Type.NUMBER 
					|| type == Type.BOOLEAN
					|| type == Type.DATE 
					|| type == Type.PRICE
					|| type == Type.INT);
		} 
		return needTrim;
		
	}

	public void setNeedTrim(boolean needTrim) {
		this.needTrim = needTrim;
	}

	/**
	 * Apply parameter template to this parameter.
	 * <p>
	 * Only applicable for 'must' parameter.
	 * 
	 * @param getParameter
	 */
	public void applyParameterTemplate(ApiApplicationParameter parameterTemplate) {
		if (parameterTemplate == null) {
			return;
		}
		// Only when this parameter's field is null, we should apply the value.

		if (this.type == null) {
			this.type = parameterTemplate.type;
		}

		if (this.cnName == null) {
			this.cnName = parameterTemplate.cnName;
		}

		if (this.length == -1) {
			this.length = parameterTemplate.length;
		}

		if (this.defaultValue == null) {
			this.defaultValue = parameterTemplate.defaultValue;
		}

		if (this.maxValue == null) {
			this.maxValue = parameterTemplate.maxValue;
		}

		if (this.minValue == null) {
			this.minValue = parameterTemplate.minValue;
		}
		
		if (this.fileExtensions == null) {
			this.fileExtensions = parameterTemplate.fileExtensions;
		}

		// 'validate' attribute would not be applied.
	}

	/**
	 * Only make sense while this parameter is application optional parameter.
	 * <p>
	 * Since the application must parameter is always validated.
	 * 
	 * @return
	 */
	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	/**
	 * @param expected
	 */
	public void setExpected(String expected) {
		this.expected = expected;
	}

	public String getExpected() {
		return this.expected;
	}

	public String[] getFileExtensions() {
		return fileExtensions;
	}

	public void setFileExtensions(String[] fileExts) {
		this.fileExtensions = fileExts;
	}
	
	boolean isList() {
		// Field List is not a list in any away. Field List 
		// is just the primative type.
		return this.type != Type.FIELD_LIST && this.type != Type.BYTE_ARRAY && this.maxListSize != null && maxListSize > 0;
	}
	public ErrorCode checkInput(TopPipeInput pipeInput) {
		if (this.type == Type.BYTE_ARRAY) {
			return checkByteArray(pipeInput);
		}
		String value = pipeInput.getString(name, true);
		if (StringUtils.isEmpty(value)) {
			/*
			 * Should happen for optional param, since if we set the validate =
			 * true for an optional param, it must be set with optional value or
			 * should not bother to invoke this method.
			 */
			return ErrorCode.MISSING_REQUIRED_ARGUMENTS;
		}

		if (expected != null) {
			if (!expected.equals(value)) {
				return ErrorCode.INVALID_ARGUMENTS;
			}
		}

		if (length != -1) {
			if (value.length() > length) {
				return ErrorCode.INVALID_ARGUMENTS;
			}
		}
		
		if (isList()) {
			// splitByComma would kick out empty elements automatically.
			String[] fields = StringKit.splitByComma(value);
			
			if (fields.length > this.getMaxListSize()) {
				return ErrorCode.INVALID_ARGUMENTS;
			}

			for (String field : fields) {
				if (!checkPrimativeType(field)) {
					return ErrorCode.INVALID_ARGUMENTS;
				}
			}
		} else {
			if (!checkPrimativeType(value)) {
				return ErrorCode.INVALID_ARGUMENTS;
			}
		}
		
		if (!"1.0".equals(pipeInput.getVersion())) {
			// Only validate the field_list only when it's not 1.0.
			// check the filed list
			if (type == Type.FIELD_LIST) {
				value = value.trim();

				if (StringUtils.isEmpty(value)) {
					return ErrorCode.INVALID_ARGUMENTS;
				}
				// splitByComma would kick out empty elements automatically.
				String[] fields = StringKit.splitByComma(value);
				if (fields.length == 0) {
					return ErrorCode.INVALID_ARGUMENTS;
				}

				for (String field : fields) {
					if (!StringKit.onlyContainsAlphaDigitUnderlineDot(field)) {
						return ErrorCode.INVALID_ARGUMENTS;
					}
				}
			}
		}


		
		
		return null;
	}
	
	boolean checkPrimativeType(String value) {
		if (type == Type.NUMBER) {
			// ValidateUtil.isNumberInRange() would handle the null
			// situation of min/maxValue.
			if (!ValidateUtil.isNumberInRange(value, minValue, maxValue)) {
				return false;
			}
		} else if (type == Type.BOOLEAN) {
			if (!ValidateUtil.isBoolean(value)) {
				return false;
			}
		} else if (type == Type.DATE) {
			if (!ValidateUtil.isDate(value)) {
				return false;
			}
		} else if (type == Type.PRICE) {
			if (!ValidateUtil.isPrice(value)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * @param input
	 * @return
	 */
	ErrorCode checkByteArray(TopPipeInput pipeInput) {
		byte[] data = null;
		com.taobao.top.core.framework.FileItem fileItem = pipeInput.getFileData(); 
		if(fileItem != null){
			data = fileItem.getBout().toByteArray();
		}else{
			return ErrorCode.MISSING_REQUIRED_ARGUMENTS;
		}
		if (data == null || data.length == 0) {
			return ErrorCode.MISSING_REQUIRED_ARGUMENTS;
		}
		if (this.length > 0) {
			if (data.length > this.length) {
				return ErrorCode.INVALID_ARGUMENTS;
			}
		}		
		String fileName = fileItem.getFileName();
		if (this.fileExtensions != null && this.fileExtensions.length > 0) {
			if (fileName == null) {
				return ErrorCode.INVALID_ARGUMENTS;
			}
			
			boolean hit = false;
			for (String extension : fileExtensions) {
				if (fileName.toLowerCase().endsWith(extension)) {
					// we hit the extension, break.
					hit = true;
					break;
				}
			}
			
			if (!hit) {
				return ErrorCode.INVALID_ARGUMENTS;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder params = new StringBuilder();
		params.append("<param name=\"").append(name).append("\" type=\"").append(type).append("\" ");
		if(defaultValue != null){
			params.append(" default_value=\"").append(defaultValue.toString()).append("\" ");
		}
		if(length != -1){
			params.append(" length=\"").append(length).append("\" ");
		}
		if(minValue != null){
			params.append(" min_value=\"").append(minValue.longValue()).append("\" ");
		}
		if(maxValue != null){
			params.append(" max_value=\"").append(maxValue.longValue()).append("\" ");
		}
		if(maxListSize != null){
			params.append(" max_list_size=\"").append(maxListSize.intValue()).append("\" ");
		}
		if(fileExtensions != null){
			params.append(" file_ext=\"");
			for(int i = 0,n = fileExtensions.length;i < n;i++){
				params.append(fileExtensions[i]);
				if(i != n - 1){
					params.append(",");
				}
			}
		}
		params.append("/>");
		return params.toString();
	}
	
}
