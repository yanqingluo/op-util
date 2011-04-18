package com.taobao.top.ats.domain;

/**
 * 
 * @author moling
 * @since 1.0, 2010-11-18
 */
public class RangeParameterDO {
	//分片类型
	private String rangeType;
	//分片周期
	private Integer parsePeriod;
	//分片单位
	private String parseMeasure;
	//最大周期
	private Integer maxPeriod;
	//最大周期单位
	private String maxMeasure;
	//分片入参的最小值，目前只支持数字类型和日期类型。日期类型此字段将日期转成long存入
	private Long minValue;
	//分片入参的最大值
	private Long maxValue;
	//入参中最小值的key
	private String minName;
	//入参中最大值的key
	private String maxName;
	//向后传到api层的分片后入参最小值key
	private String targetMinName;
	//向后传到api层的分片后入参最大值key
	private String targetMaxName;
	
	//后面部分功能为分片后再分页操作需要
	//标记是否需要再分页支持
	private Boolean enablePage;
	//向后传到api层的页码的key
	private String pageNoName;
	//向后传到api层的每页大小的key
	private String pageSizeName;
	//后方api返回的总结果数的key
	private String totalResultName;
	//默认分页大小
	private Integer pageSize;
	public String getRangeType() {
		return this.rangeType;
	}
	public void setRangeType(String rangeType) {
		this.rangeType = rangeType;
	}
	public Integer getParsePeriod() {
		return this.parsePeriod;
	}
	public void setParsePeriod(Integer parsePeriod) {
		this.parsePeriod = parsePeriod;
	}
	public String getParseMeasure() {
		return this.parseMeasure;
	}
	public void setParseMeasure(String parseMeasure) {
		this.parseMeasure = parseMeasure;
	}
	public Integer getMaxPeriod() {
		return this.maxPeriod;
	}
	public void setMaxPeriod(Integer maxPeriod) {
		this.maxPeriod = maxPeriod;
	}
	public String getMaxMeasure() {
		return this.maxMeasure;
	}
	public void setMaxMeasure(String maxMeasure) {
		this.maxMeasure = maxMeasure;
	}
	public Long getMinValue() {
		return this.minValue;
	}
	public void setMinValue(Long minValue) {
		this.minValue = minValue;
	}
	public Long getMaxValue() {
		return this.maxValue;
	}
	public void setMaxValue(Long maxValue) {
		this.maxValue = maxValue;
	}
	public String getMinName() {
		return this.minName;
	}
	public void setMinName(String minName) {
		this.minName = minName;
	}
	public String getMaxName() {
		return this.maxName;
	}
	public void setMaxName(String maxName) {
		this.maxName = maxName;
	}
	public String getTargetMinName() {
		return this.targetMinName;
	}
	public void setTargetMinName(String targetMinName) {
		this.targetMinName = targetMinName;
	}
	public String getTargetMaxName() {
		return this.targetMaxName;
	}
	public void setTargetMaxName(String targetMaxName) {
		this.targetMaxName = targetMaxName;
	}
	public Boolean getEnablePage() {
		return this.enablePage;
	}
	public void setEnablePage(Boolean enablePage) {
		this.enablePage = enablePage;
	}
	public String getPageNoName() {
		return this.pageNoName;
	}
	public void setPageNoName(String pageNoName) {
		this.pageNoName = pageNoName;
	}
	public String getPageSizeName() {
		return this.pageSizeName;
	}
	public void setPageSizeName(String pageSizeName) {
		this.pageSizeName = pageSizeName;
	}
	public String getTotalResultName() {
		return this.totalResultName;
	}
	public void setTotalResultName(String totalResultName) {
		this.totalResultName = totalResultName;
	}
	public Integer getPageSize() {
		return this.pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
}
