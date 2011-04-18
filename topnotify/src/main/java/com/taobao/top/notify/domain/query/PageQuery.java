package com.taobao.top.notify.domain.query;

/**
 * 分页查询条件。
 * 
 * @author fengsheng
 * @since 1.0, Jan 28, 2010
 */
public class PageQuery {

	private static final int DEFAULT_PAGE_NO = 1;
	private static final int DEFAULT_PAGE_SIZE = 40;
	private static final int MAX_PAGE_SIZE = 200;

	private int pageNo;
	private int pageSize;

	public int getPageNo() {
		if (pageNo < 1) {
			return DEFAULT_PAGE_NO;
		} else {
			return pageNo;
		}
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		if (pageSize < 1) {
			return DEFAULT_PAGE_SIZE;
		} else if (pageSize > MAX_PAGE_SIZE) {
			return MAX_PAGE_SIZE;
		} else {
			return pageSize;
		}
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getOffset() {
		return (getPageNo() - 1) * getPageSize();
	}

}
