package com.taobao.top.pageapi.services;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.common.payway.core.dsa.DSAUtil;
import com.taobao.top.pageapi.framework.TopPagePipeInput;

public class PaywaySignCheckUtil {

	private static final Log log = LogFactory.getLog(PaywaySignCheckUtil.class);
	private DSAUtil dsaUtil;

	public boolean checkSign(TopPagePipeInput pipeInput) {
		HttpServletRequest request = pipeInput.getRequest();
		String signType = request.getParameter("sign_type");
		String sign = request.getParameter("sign");
		//签名和签名参数未传，视为不合法
		if(StringUtils.isBlank(sign) || StringUtils.isBlank(signType)) {
			return false;
		}
		
		return true;
		/*Map<String, String> signParameterMap = new HashMap<String, String>();
		try {
			Enumeration names = request.getParameterNames();
			while (names.hasMoreElements()) {
				String key = (String) names.nextElement();
				if("sign".equals(key)||"signType".equals(key)) {
					continue;
				}
				signParameterMap.put(key, request.getParameter(key));
			}
			if (!getDsaUtil().check(signParameterMap,
					request.getParameter("sign"))) {
				if (log.isErrorEnabled()) {
					log.error("Sign Error!" + signParameterMap);
				}
				return false;
			}
		} catch (Exception e) {
			log.error("Sign Error!" + signParameterMap);
			log.error(e.getMessage(), e);
			return false;
		}
		return true;*/
	}

	public void setDsaUtil(DSAUtil dsaUtil) {
		this.dsaUtil = dsaUtil;
	}

	public DSAUtil getDsaUtil() {
		return dsaUtil;
	}
}
