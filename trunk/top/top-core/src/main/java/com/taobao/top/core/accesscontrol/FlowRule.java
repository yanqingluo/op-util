package com.taobao.top.core.accesscontrol;


/**
 * The flow rule class.
 * It applies this rule:
 * In <tt>{@link #checkInterval}</tt> seconds, when the {@link #flowRuleKey} has 
 * reached more than <tt>{@link #threshould}</tt> times, 
 * the {@link #checkKey} will be banned for <tt>{@link #banDuration}</tt> seconds.
 * 
 * The flowRule will be considered the same with same checkKey and same inteval,
 * thus using the flowRuleKey, which is generated from checkKey and inteval.
 * 
 * @author <a href="mailto:huaisu@taobao.com">huaisu</a>
 * @since 2010-6-3
 */
public class FlowRule {
	
	/**
	 * china time zone is 8 hour after the gmt time.
	 * to cut the unixtime into china time split, 
	 * just add it to the unixtime.
	 */
	private static final int ZONE_OFFSET_SECONDS = 8 * 3600;
	
	private final String checkKey;
	private final long threshold;
	private final int banDuration;
	
	/**
	 * the core property. generated from the checkKey and checkInteval.
	 */
	private final String flowRuleKey;
	
	private final int checkInteval;
	
	/**
	 * @return the key
	 */
	public String getCheckKey() {
		return checkKey;
	}

	/**
	 * @return the threshold
	 */
	public long getThreshold() {
		return threshold;
	}

	/**
	 * @return the flowRuleKey
	 */
	public String getFlowRuleKey() {
		return flowRuleKey;
	}

	/**
	 * @return the banDuration
	 */
	public int getBanDuration() {
		return banDuration;
	}

	/**
	 * @param key
	 * @param checkInterval: in second, should bigger than 0
	 * @param threshould: should not be less than 0, if equals to 0, it means this key 
	 * 					  can not be accessed.
	 * @param banDuration : in second, can be null or 0
	 * @param blackListManager
	 * @param callLimitManager
	 * @throws IllegalArgumentException
	 */
	public FlowRule(String checkKey, Integer checkInterval, Long threshold,
			Integer additionalBanDuration) {
		if(threshold == null || checkInterval == null){
			throw new IllegalArgumentException();
		}
		if(threshold.longValue() < 0 || checkInterval.intValue() <= 0){
			throw new IllegalArgumentException();
		}
		this.checkKey = checkKey;
		this.threshold = threshold;
		this.checkInteval = checkInterval.intValue();
		int currTime = (int) (System.currentTimeMillis() / 1000);
		//it is a little hard to understand this, just assume the utc time is  
		//counting from the 1970-0-0 00:00:00 of china zone
		currTime += ZONE_OFFSET_SECONDS;
		int timeUnit = currTime / checkInterval;
		this.flowRuleKey = checkKey + ":" + timeUnit;	
		int defaultBanDuration = (timeUnit + 1) * checkInterval - currTime;
		if(additionalBanDuration != null && additionalBanDuration.intValue() > 0) {
			defaultBanDuration += additionalBanDuration;
		} 
		this.banDuration = defaultBanDuration;
	}	
	
	/**
	 * @return the checkInteval
	 */
	public int getCheckInteval() {
		return checkInteval;
	}

	@Override
	public int hashCode() {
		return flowRuleKey.hashCode();
	}
	
	/* 
	 * if two flowRule has same checkKey and same checkInteval, 
	 * they are considered the same, so use flowRuleKey to judge.
	 * @see #flowRuleKey
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof FlowRule 
			&& flowRuleKey.equalsIgnoreCase(((FlowRule)obj).flowRuleKey);
	}
	
	@Override
	public String toString() {
		return "checkKey:[" + checkKey + "], " +
				"flowRuleKey:[" + flowRuleKey +"], " +
				 "checkInteval:[" + checkInteval +"], " +
				  "threshould:[" + threshold + "], " +
				   "banDuration:[" + banDuration + "]"; 
	}
	
}
