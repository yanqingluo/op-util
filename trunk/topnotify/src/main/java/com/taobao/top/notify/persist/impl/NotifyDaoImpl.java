package com.taobao.top.notify.persist.impl;

import java.util.Calendar;
import java.util.Date;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.taobao.top.notify.domain.NotifyDO;
import com.taobao.top.notify.domain.PageList;
import com.taobao.top.notify.domain.query.NotifyQuery;
import com.taobao.top.notify.persist.NotifyDao;

/**
 * 通知数据访问对象默认实现。
 * 
 * @author fengsheng
 * @since 1.0, Dec 15, 2009
 */
public class NotifyDaoImpl implements NotifyDao {

	private static final Date ROUTE_TIME = new Date(1284480000000L);

	private SqlMapClientTemplate ibatisTemplate;
	private SqlMapClientTemplate ibatisTemplateOld;

//	public static void main(String[] args) throws ParseException {
//		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		System.out.println(format.parse("2010-09-15 00:00:00"));
//		System.out.println(new Date(1284480000000L));
//	}

	public void setIbatisTemplate(SqlMapClientTemplate ibatisTemplate) {
		this.ibatisTemplate = ibatisTemplate;
	}

	public void setIbatisTemplateOld(SqlMapClientTemplate ibatisTemplateOld) {
		this.ibatisTemplateOld = ibatisTemplateOld;
	}

	public void addNotify(NotifyDO notify) {
		getTemplate().insert("addNotify", notify);
	}

	public NotifyDO getNotify(Long id) {
		return (NotifyDO) getTemplate().queryForObject("getNotify", id);
	}

	@SuppressWarnings("unchecked")
	public PageList<NotifyDO> queryNotifys(NotifyQuery query) {
		PageList<NotifyDO> result = new PageList<NotifyDO>();
		result.setPageSize(query.getPageSize());

		result.setData(getTemplate().queryForList("queryNotifys", query));
		Object total = getTemplate().queryForObject("queryNotifysTotal", query);

		if (total instanceof Integer) {
			result.setTotal((Integer) total);
		}
		return result;
	}

	private SqlMapClientTemplate getTemplate() {
		Date now = Calendar.getInstance().getTime();
		if (now.after(ROUTE_TIME)) {
			return this.ibatisTemplate;
		} else {
			return this.ibatisTemplateOld;
		}
	}

}
