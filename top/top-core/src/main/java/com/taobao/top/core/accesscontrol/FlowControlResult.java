package com.taobao.top.core.accesscontrol;

import org.apache.commons.lang.StringUtils;


/**
 * The detail of flow control result
 * @author <a href="mailto:huaisu@taobao.com">huaisu</a>
 * @since 2010-7-19
 */
public class FlowControlResult {	
	/**
	 * the detail reason of why it is forbidden
	 */
	private final ResultType result;
	/**
	 * such access will be banned how long time, in second unit
	 */
	private final Integer timeToLive;
	
	public static final FlowControlResult NOT_FORBIDDEN = new FlowControlResult(ResultType.NOT_FORBIDDEN, 0);
	
	/**
	 * @param forbidKeyId
	 * @param banEndTime the end time of this ban
	 */
	public FlowControlResult(String forbidKeyId,
			Integer timeToLive) {
		this(ResultType.getTypeByForbidKeyId(forbidKeyId), 
				timeToLive);
	}
	
	private FlowControlResult(ResultType result, Integer timeToLive) {
		this.result = result;
		this.timeToLive = timeToLive;
	} 
	
	public boolean canAccess() {
		return result == ResultType.NOT_FORBIDDEN;
	}
	
	/**
	 * @return the subErrorCode
	 */
	public ResultType getResult() {
		return result;
	}

	/**
	 * @return the timeToLive
	 */
	public Integer getTimeToLive() {
		return timeToLive;
	}

	@Override
	public String toString() {
		return result.detail + ", will last " + timeToLive + " more seconds";
	}

	public static enum ResultType {
		NOT_FORBIDDEN("not forbidden"),
		FORBIDDEN_BY_APP_RULE("accesscontrol.limited-by-app-access-count"),
		FORBIDDEN_BY_API_RULE("accesscontrol.limited-by-api-access-count"),
		FORBIDDEN_BY_APP_API_RULE("accesscontrol.limited-by-app-api-access-count");
		private final String detail;
		
		private ResultType(String detail) {
			this.detail = detail;
		}
		
		public String getDetail() {
			return detail;
		}
		
		public static ResultType getTypeByForbidKeyId(String keyId) {
			if(StringUtils.isBlank(keyId)) {
				return NOT_FORBIDDEN;
			} else if(keyId.startsWith("0:")) {
				return FORBIDDEN_BY_API_RULE;
			} else if(keyId.endsWith(":0")) {
				return FORBIDDEN_BY_APP_RULE;
			} else {
				return FORBIDDEN_BY_APP_API_RULE;				
			}
		}
	}
}


