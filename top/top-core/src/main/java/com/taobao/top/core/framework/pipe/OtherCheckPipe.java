package com.taobao.top.core.framework.pipe;

import static com.taobao.top.core.ProtocolConstants.P_SIGN;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.common.lang.StringUtil;
import com.taobao.top.common.encrypt.EncryptUtil;
import com.taobao.top.core.ErrorCode;
import com.taobao.top.core.ProtocolConstants;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.core.framework.TopPipeResult;
import com.taobao.top.core.framework.TopPipeUtil;

/**
 * 其他校验，暂时有sign校验
 * @author zhenzi
 *
 */
public class OtherCheckPipe extends TopPipe<TopPipeInput, TopPipeResult> {
	private static final Log logger = LogFactory.getLog(OtherCheckPipe.class);
	
	@Override
	public void doPipe(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		ErrorCode errCode = checkSign(pipeInput);
		if(errCode != null){
			pipeResult.setErrorCode(errCode);
		}
	}
	
	@Override
	public boolean ignoreIt(TopPipeInput pipeInput,
			TopPipeResult pipeResult) {
		if(pipeResult.getErrorCode() != null || TopPipeUtil.isIgnore(pipeInput)){
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param pipeInput
	 * @return
	 */
	private ErrorCode checkSign(TopPipeInput pipeInput){
		String secret = pipeInput.getAppDO().getSecret();
		boolean checked = false;
		String signMethod = pipeInput.getSignMethod();
		if (ProtocolConstants.SIGN_METHOD_HMAC.equals(signMethod)) {
			// use hmac
			try {
				checked = checkSignAndApiInput(pipeInput, secret, false, true);
			} catch (Exception e) {
				logger.error("check sign error:", e);
				return ErrorCode.INVALID_SIGNATURE;
			}
		} else if (ProtocolConstants.SIGN_METHOD_MD5.equals(signMethod)) {
			// use new md5
			try {
				checked = checkSignAndApiInput(pipeInput, secret, true, false);
			} catch (Exception e) {
				logger.error("check sign error:", e);
				return ErrorCode.INVALID_SIGNATURE;	
			}
		} else {
			// default is old md5
			try {
				checked = checkSignAndApiInput(pipeInput, secret, false,
						false);
			} catch (Exception e) {
				logger.error("check sign error:", e);
				return ErrorCode.INVALID_SIGNATURE;
			}
		}
		if (!checked) {
			return ErrorCode.INVALID_SIGNATURE;
		}
		return null;
	}
	/**
	 * 检查签名
	 * 
	 * @author liupo
	 * @param apiInput
	 * @return
	 * @throws Exception
	 */
	private boolean checkSignAndApiInput(TopPipeInput pipeInput, String secret,
			boolean appendSecret, boolean isHMac) throws Exception {
		String[] names = pipeInput.getParameterNames().toArray(
				ArrayUtils.EMPTY_STRING_ARRAY);
		Arrays.sort(names);
		Map<String, String> params = new HashMap<String, String>(names.length);
		String sign = pipeInput.getSign();
		if (null == sign) {
			throw new IllegalStateException("Sign not exist!");
		}
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			//when check sign, value can't trim
			String value = pipeInput.getString(name, false); 
			if (value != null) {
				params.put(name, value);
			}
		}
		String checkedSign = EncryptUtil.signature2(params, secret,
				appendSecret, isHMac, P_SIGN);
		boolean result = StringUtil.equals(checkedSign, sign);
		if (!result) {
			if (logger.isWarnEnabled()) {
				logger.warn(new StringBuilder("checkedSign=").append(checkedSign).append(" but inputSign=").append(sign));
			}
		}
		return result;
	}
	
}
