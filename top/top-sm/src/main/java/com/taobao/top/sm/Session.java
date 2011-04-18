/**
 * 
 */
package com.taobao.top.sm;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * Session对象
 * 目前session对象会转化为String存到tair里
 * 生成String的策略是（已换行分隔）: 
 * v method userId nick startTime endTime//这七个必选
 * 七个之后会还是以换行分隔，但是会以key=value的形式保存，还原成对象的时候，会还原成session的properties属性
 * 
 * 例如：下面就是一个合法的session String
 * 1 all 124234 朱棣 1324234324 43534532423 key1=hello key2=value2
 * 
 * 下面也是一个合法的的session String
 * 1 all 124234 朱棣 1324234324 43534532423 key3=values
 * 
 * 
 * @version 2008-11-20
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public class Session implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -431249460236590257L;
	
	/*******session的属性名,由于属性名会被store入tair，名字在保证不冲突的情况下，尽量短,节约流量********/
	
	public static final String SESSION_DEAD_LINE = "SDL";
	
	
	/*******************************session的属性名**************************************/
	
	/**
	 * 缓存项分隔符
	 */
	public static final char valueSplit = '\n';

	/**
	 * 缓存项api_key列表分隔符
	 */
	public static final char apiKeySplit = ';';
	/**
	 * 缓存值版本号
	 */
	public static final int version = 1;
	

	private int v;

	/**
	 * session type
	 */
	private int type;

	/**
	 * session id
	 */
	private String sessionId;

	private Set<String> appKeys;

	/**
	 * 绑定的方法
	 */
	private String method;

	/**
	 * 绑定的用户昵称
	 */
	private String nick;

	/**
	 * 绑定的用户id
	 */
	private String userId;

	/**
	 * 有效期起始时间
	 */
	private Date validFrom;

	/**
	 * 有效期截止时间
	 */
	private Date validEnd;
	/**
	 * session 的可选属性
	 */
	private Map<String,String> properties = new HashMap<String, String>();

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidEnd() {
		return validEnd;
	}

	public long getValidThru() {
		return this.validEnd.getTime() - this.validFrom.getTime();
	}

	public void setValidEnd(Date validEnd) {
		this.validEnd = validEnd;
	}
	/**
	 * 获取session的可选属性
	 * @param propertyName
	 * @return
	 */
	public String getProperty(String propertyName){
		return properties.get(propertyName);
	}
	/**
	 * 设置属性，请注意，key和value里不要使用换行（'\n'）和"=",会引起解析错误。
	 * @param propertyName
	 * @param propertyValue
	 */
	public void setProperty(String propertyName,String propertyValue){
		properties.put(propertyName, propertyValue);
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}
	/**
	 * 如果key相同，会覆盖原来的properties
	 * @param properties
	 */
	public void setProperties(Map<String, String> properties) {
		this.properties.putAll(properties);
	}
	
	
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId
	 *            the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the v
	 */
	public int getV() {
		return v;
	}

	/**
	 * @param v
	 *            the v to set
	 */
	public void setV(int v) {
		this.v = v;
	}

	/**
	 * @return the apiKeys
	 */
	public Set<String> getAppKeys() {
		return appKeys;
	}

	/**
	 * @param apiKeys
	 *            the apiKeys to set
	 */
	public void setAppKeys(Set<String> appKeys) {
		this.appKeys = appKeys;
	}
	public void setAppKeys(String... appKeys) {
		if(appKeys!=null){
			this.appKeys = new HashSet<String>(Arrays.asList(appKeys));
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
	
	public void init(String sessionId,String sessionValue) throws SessionValueInvalidException{
		if (StringUtils.isEmpty(sessionValue)) {
			throw new SessionValueInvalidException(sessionId + ":" + sessionValue);
		}
		int sessionType = Integer.valueOf(sessionId.substring(0, 1));
		// 2个空行之间算一个空的缓存项
		String[] infos = StringUtils.splitPreserveAllTokens(sessionValue, valueSplit);
		
		if (infos.length < 7) {
			throw new SessionValueInvalidException(sessionValue);
		}
		int v = Integer.valueOf(infos[0]);
		// ""表示任意方法
		String method = infos[1];
		String apiKeys = infos[2];
		String[] apiKeyArr = StringUtils.split(apiKeys, apiKeySplit);
		if (ArrayUtils.isEmpty(apiKeyArr)) {
			throw new SessionValueInvalidException("Need apiKeys!");
		}
		String userId = infos[3];
		String nick = infos[4];
		if (StringUtils.isEmpty(nick)) {
			throw new SessionValueInvalidException("Need nick!");
		}
		Date validFrom = new Date(Long.valueOf(infos[5]));
		Date validEnd = new Date(Long.valueOf(infos[6]));
		this.v = v;
		this.type = sessionType;
		this.sessionId = sessionId;
		this.method = method;
		this.nick = nick;
		this.userId = userId;
		this.validFrom = validFrom;
		this.validEnd = validEnd;
		this.appKeys = new HashSet<String>(Arrays.asList(apiKeyArr));
		
		for (int i = 7; i < infos.length; i++) {
			String[] proPair = StringUtils.splitPreserveAllTokens(infos[i], "=");
			if(proPair.length!=2){
				throw new SessionValueInvalidException("properties read error!");
			}
			this.setProperty(proPair[0], proPair[1]);
		}
				
	}
	/**
	 * value生成
	 * 
	 * value组成顺序（已换行分隔）: v method userId nick startTime endTime
	 * @throws SessionGenerateException 
	 */
	public String convertToStr() throws SessionGenerateException{
		// 不允许nick为空
		if (StringUtils.isEmpty(this.nick)) {
			throw new SessionGenerateException("Need nick!");
		}
		if (appKeys==null||appKeys.isEmpty()) {
			throw new SessionGenerateException("Need apiKeys!");
		}
		if(this.v==0){
			this.v=version;
		}
		long validThru  = this.getValidThru();
		if (validThru < 0) {
			throw new SessionGenerateException("Invalid validThru:" + validThru);
		}
		
		if (null == this.method) {
			this.method = "all";
		}
		if (null == this.userId) {
			this.userId = "";
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(this.v);
		sb.append(valueSplit);
		sb.append(this.method);
		sb.append(valueSplit);
		int i= 0;
		for (String appKey : this.appKeys) {
			sb.append(appKey);
			if (i != appKeys.size() - 1) {
				sb.append(apiKeySplit);
			}
			i++;
		}
		
		sb.append(valueSplit);
		sb.append(this.userId);
		sb.append(valueSplit);
		sb.append(this.nick);
		sb.append(valueSplit);
		sb.append(this.validFrom.getTime());
		sb.append(valueSplit);
		sb.append(this.validEnd.getTime());
		
		if (properties!=null) {
			for (Map.Entry<String, String> entry : properties.entrySet()) {
				sb.append(valueSplit);
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
			}
		}
		return sb.toString();
		
	}

}
