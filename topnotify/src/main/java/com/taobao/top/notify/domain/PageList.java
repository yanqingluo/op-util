package com.taobao.top.notify.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页列表。
 * 
 * @author fengsheng
 * @since 1.0, Dec 17, 2009
 */
public class PageList<T> implements Serializable {

	private static final long serialVersionUID = -5811532094220207732L;

	private int total;
	private List<T> data;
	private int pageSize;

	public int getTotal() {
		return this.total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getData() {
		return this.data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public void addRecord(T record) {
		if (this.data == null) {
			this.data = new ArrayList<T>();
		}
		this.data.add(record);
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isEmpty() {
		return this.data == null || this.data.isEmpty();
	}

	public T getFirst() {
		if (isEmpty()) {
			return null;
		} else {
			return this.data.get(0);
		}
	}

	public int getPageCount() {
		if (this.total % this.pageSize == 0) {
			return this.total / this.pageSize;
		} else {
			return this.total / this.pageSize + 1;
		}
	}

}
