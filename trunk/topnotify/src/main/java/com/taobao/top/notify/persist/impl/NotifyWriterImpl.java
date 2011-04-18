package com.taobao.top.notify.persist.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.dao.DataAccessException;

import com.taobao.monitor.alert.client.AlertAgent;
import com.taobao.top.notify.NotifyException;
import com.taobao.top.notify.domain.Notify;
import com.taobao.top.notify.domain.NotifyDO;
import com.taobao.top.notify.domain.NotifyEnum;
import com.taobao.top.notify.monitor.NotifyMonitor;
import com.taobao.top.notify.monitor.PersistLog;
import com.taobao.top.notify.persist.NotifyDao;
import com.taobao.top.notify.persist.NotifyWriter;
import com.taobao.top.notify.util.DateUtils;
import com.taobao.top.notify.util.IdCreator;
import com.taobao.top.notify.util.MapUtil;

/**
 * 消息持久器默认实现。
 * 
 * @author fengsheng
 * @since 1.0, Dec 15, 2009
 */
public class NotifyWriterImpl implements NotifyWriter {

	private static final Log log = LogFactory.getLog(NotifyWriterImpl.class);
	private static final String NOR_CHAR_REG = "[^\\x00-\\xFF\u2E80-\u9FFF]";
	public long ip;

	private NotifyDao notifyDao;
	private AlertAgent alertAgent;

	public void setNotifyDao(NotifyDao notifyDao) {
		this.notifyDao = notifyDao;
	}

	public void setAlertAgent(AlertAgent alertAgent) {
		this.alertAgent = alertAgent;
	}
	
	public void init() throws UnknownHostException {
		//初始化ip地址标记
		byte[] ips = InetAddress.getLocalHost().getAddress();
		//取2进制第21-30位标记机器ip，21-28标记ip的最后一段，29和30标记属于第几机房
		ip = (((int)ips[1]) & 3) << 28;
		ip = ip | ((((int)ips[3]) & 255) << 20);
	}
	
	public void write(Notify notify) throws NotifyException {
		long begin = System.currentTimeMillis();

		if (notify == null) {
			return;
		}

		if (!notify.getModified().after(DateUtils.getPrev7DaysEnd())) {
			log.warn("Ignore notify which was modified at 7 days before: "
					+ DateUtils.formatDate(notify.getModified()));
			return;
		}

		PersistLog persistLog = new PersistLog();
		NotifyEnum notifyEnum = NotifyEnum.getInstance(notify.getCategory(), notify.getStatus());
		persistLog.setAppKey(notify.getAppKey());
		persistLog.setStatus(notifyEnum.getMessage());

		NotifyDO notifyDo = null;

		try {
			notifyDo = populateNotifyDO(notify);
			notify.setId(notifyDo.getId());
			notifyDao.addNotify(notifyDo);
		} catch (DataAccessException de) {
			if (notifyDo != null) {
				log.warn("转换后消息内容：" + ToStringBuilder.reflectionToString(notifyDo));
			}
			alertAgent.alertWithAutoDismiss("DATABASE_ACCESS_ERROR");
			throw de;
		} finally {
			long end = System.currentTimeMillis();
			persistLog.setResponseTime(end - begin);
			NotifyMonitor.log(persistLog);
		}
	}

	public void write(List<Notify> notifys) throws NotifyException {
		if (notifys == null || notifys.isEmpty()) {
			return;
		}

		for (Notify notify : notifys) {
			write(notify);
		}
	}

	private NotifyDO populateNotifyDO(Notify notify) throws NotifyException {
		NotifyDO notifyDo = new NotifyDO();

		notifyDo.setId(IdCreator.createId(ip));
		notifyDo.setAppKey(notify.getAppKey());
		notifyDo.setCategory(notify.getCategory());
		notifyDo.setStatus(notify.getStatus());
		notifyDo.setUserId(notify.getUserId());
		if (StringUtils.isNotBlank(notify.getUserName())) {
			notifyDo.setUserName(notify.getUserName().replaceAll(NOR_CHAR_REG, ""));
		}
		notifyDo.setGmtCreate(new Date());
		notifyDo.setGmtModified(notify.getModified());

		if (notify.getContent() != null && !notify.getContent().isEmpty()) {
			JSONObject content = new JSONObject(MapUtil.normalizeMap(notify.getContent()));
			String text = content.toString();
			if (StringUtils.isNotBlank(text)) {
				notifyDo.setContent(text.replaceAll(NOR_CHAR_REG, ""));
			}
		}

		if (notify.getBizType() == null) {
			notifyDo.setBizType(0); // biz_type is required
		} else {
			notifyDo.setBizType(notify.getBizType());
		}

		if (log.isDebugEnabled()) {
			log.debug("Persist Notify：" + ToStringBuilder.reflectionToString(notify));
		}

		return notifyDo;
	}

}
