package com.taobao.top.pageapi.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 签名处理
 * 
 * @version 2009-08-16
 * @author <a href="mailto:yueqian@taobao.com">yueqian</a>
 * 
 */
public class SignUtils {
	
	private static final transient Log logger = LogFactory.getLog(SignUtils.class);
	
	private static final String SIGN_MAP_PATH = "path";
	private static final String SIGN_MAP_QUERY_STRING = "query_string";
	private static final String SIGN_MAP_SIGN_STRING = "sign";

	private static final String SHOPEX_TEMP_SECRET = "7a4dd308823fcbcb53e9032baf0284db"; // 其实就是"ShopEX页面流程化"的MD5值
	
   /**
	 * 签名方法，用于生成签名。生成签名的描述见注3
	 * @param params 传给服务器的参数
	 * @param secret 分配给您的APP_SECRET
	 */	
	public static String sign(TreeMap<String, String> params, String secret) throws EncryptException{
		if (logger.isTraceEnabled()) {
			logger.trace("sign entered  params is " + params);
		}
		String result = null;
		if (params == null)
			throw new EncryptException("parameter map is null");
		String paraString = convertMapToBase64String(params);
		result = sign(paraString, secret);
		if (logger.isTraceEnabled()) {
			logger.trace("sign exit  result is " + result);
		}
		return result;
	} 
	
	public static String byte2hex(byte[] b) { 

        StringBuffer hs = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs.append("0").append(stmp);
            else
                hs.append(stmp);
        }
        return hs.toString().toUpperCase();
  } 
	
	/**
	 * 产生Token
	 * <li>在PC上产生100万个Token需要30秒左右
	 * 
	 * @param parameter
	 * @param secret
	 * @return
	 * @throws EncryptException 
	 * 
	 */
	public static String sign(String parameter, String secret) throws EncryptException
			{

		// 对参数+密钥做MD5运算
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
			throw new EncryptException(e);
		}
		byte[] digest = null;
		digest = md.digest((parameter + secret).getBytes());
        return new String(Base64.encodeBase64(digest));
	}


	/**
	 * 验证签名
	 * @param sign
	 * @param parameter
	 * @param secret
	 * @return
	 * @throws EncryptException
	 */
	public static boolean validateSign(String sign, String parameter,
			String secret) throws EncryptException {
		return sign!= null 
			&& parameter != null
			&& secret != null
			&& sign.equals(sign(parameter, secret));
	}

	/**
	 * 把Map里面的参数组成字符串，按BASE64编码
	 * Map中的Key和Value 不能包含&符号
	 * @param map
	 * @return
	 */
	public static String convertMapToBase64String(Map<String, String> map) {
		StringBuffer mapBuffer = new StringBuffer();    
	    if( map == null || map.isEmpty() )     
	        return "";    
	    Set<String> KeySet = map.keySet();    
	    Iterator<String> iterator = KeySet.iterator();    
	    while( iterator.hasNext() )    
	    {    
	        String key = (String) iterator.next();    
	        String value = (String) map.get(key);
	        if (value == null || "null".equalsIgnoreCase(value)){
	        	value = "";
	        }
			mapBuffer.append(key + "=" + value);
			if( iterator.hasNext() )    
		            mapBuffer.append("&");
	    }
	    return new String(Base64.encodeBase64(mapBuffer.toString().getBytes()));
	}
	
	/**
	 * 把经过BASE64编码的字符串转换为Map对象
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> convertBase64StringtoMap(String str){
		if(str == null) return null;
		String keyvalues = null;
		keyvalues = new String(Base64.decodeBase64(str.getBytes()));
		if(keyvalues == null || keyvalues.length() == 0)
			return null;
		String[] keyvalueArray = keyvalues.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for(String keyvalue:keyvalueArray){
			String[] s = keyvalue.split("=");
			if(s==null || s.length!=2)
				return null;
			map.put(s[0], s[1]);
		}
		return map;
	}
	// this map is a parameter map from a request
	public static String sign (Map <String ,Object > parameterMap ,String secret) throws EncryptException{
		String sign = null;
		Iterator<String> keySetIt = parameterMap.keySet().iterator();
		TreeMap<String, String> apiparamsMap = new TreeMap<String, String>();
		while (keySetIt.hasNext()) {
			String name = (String) keySetIt.next();
			if (logger.isDebugEnabled()){
				logger.debug("name is " + name);
			}
			String value = getString(parameterMap,name);
			if (value != null) {
				apiparamsMap.put(name, value);
			}
		}
		sign = SignUtils.sign(apiparamsMap,secret);
		return sign;
	}
	
	  private static String getString(Map <String,Object> parameterMap,String key) {
			String value = null ;
			if (parameterMap == null || key == null ){
				return value ;
			}
			Object valueObj = parameterMap.get(key);
			if (valueObj != null && valueObj instanceof String [] ){
				value = ((String[])valueObj)[0];
			}else {
				value = String.valueOf(valueObj);
			}
			return value;
		}
	  
	  public static boolean validateSign(String sign, Map parameterMap,
				String secret) throws EncryptException {
			Iterator keySetIt = parameterMap.keySet().iterator();
			TreeMap<String, String> apiparamsMap = new TreeMap<String, String>();
			while (keySetIt.hasNext()) {
				String name = (String) keySetIt.next();
				if ("top_sign".equals(name))
					continue;
				String value = getString(parameterMap, name);
				if (value != null) {
					apiparamsMap.put(name, value);
				}
			}
			return validateSign(sign, apiparamsMap, secret);
		}

		public static boolean validateSign(String sign, TreeMap params,
				String secret) throws EncryptException {
			Iterator it = params.keySet().iterator();
			StringBuilder sb = new StringBuilder("");
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = (String) params.get(key);
				sb.append(key + "=" + value);
				if (it.hasNext())
					sb.append("&");
			}
			String str = new String(Base64.encodeBase64(sb.toString().getBytes()));
			return sign != null && params != null && secret != null
					&& sign.equals(sign(str, secret));
		}
		
		/**
		 * 给URL加上sign签名
		 * 
		 * @param url
		 *            原始URL
		 * @return 签名后的URL
		 */
		public static String signURL(String url,String key ) {
			HashMap<String, String> signMap = getSignMap(url,key);
			if (signMap.get(SIGN_MAP_SIGN_STRING) == null)
				return url;
			return new StringBuffer(signMap.get(SIGN_MAP_PATH)).append("?").append(
					signMap.get(SIGN_MAP_QUERY_STRING)).append("&sign=").append(
					signMap.get(SIGN_MAP_SIGN_STRING)).toString();
		}
		private static HashMap<String, String> getSignMap(String url,String key) {
			HashMap<String, String> map = new HashMap<String, String>();
			StringBuffer s = new StringBuffer();
			StringBuffer ps = new StringBuffer();
			int pos = url.indexOf("?");
			if (pos == -1) {
				map.put(SIGN_MAP_PATH, url);
				map.put(SIGN_MAP_QUERY_STRING, "");
				return map;
			} else {
				map.put(SIGN_MAP_PATH, url.substring(0, pos));
			}
			String[] params = url.substring(pos + 1).split("\\&");
			Arrays.sort(params);
			try {
				for (String p : params) {
					if (p.startsWith("sign=")) // sign参数不参与计算
						continue;
					int equalPos = p.indexOf("=");
					if (equalPos == -1)
						continue; // 不正确的参数会被丢弃
					String pname = p.substring(0, equalPos);
					String pvalue = p.substring(equalPos + 1);
					if (s.length() > 0)
						s.append("&");
					s.append(pname).append("=").append(pvalue);
					ps.append(
							URLDecoder.decode(pname, "UTF-8")).append("=")
							.append(
									URLDecoder.decode(pvalue, "UTF-8"));
				}
			} catch (UnsupportedEncodingException e) {
				logger.error("URL参数编码[" + url + "]转换出错:" + e);
				map.put(SIGN_MAP_QUERY_STRING, "");
				return map;
			}
			map.put(SIGN_MAP_QUERY_STRING, s.toString());
			try {
				if (s.length() > 0)
					map.put(SIGN_MAP_SIGN_STRING, URLEncoder.encode(sign(ps
							.toString(), key), "UTF-8")); // 生成签名
			} catch (UnsupportedEncodingException e) {
				logger.error("URL参数解码[" + url + "]转换出错:" + e);
			} catch (EncryptException e) {
				logger.error("加密[" + url + "]出错:" + e);
			}
			return map;
		}
		
		public static void validateURLSign(String sign,String URL,String key) throws EncryptException{
			Map map = getSignMap(URL,key);
			String urlSign = (String) map.get(SIGN_MAP_SIGN_STRING);
			try {
				urlSign = URLDecoder.decode(urlSign,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("转换编码出错",e);
			}
			if (urlSign!= null && sign!=null && urlSign.equals(sign)){
			}else {
				throw new EncryptException("ValidateURLSign failed");
			}
		}
}
