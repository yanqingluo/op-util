package com.taobao.top.notify.receive.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.hsf.notify.client.message.Message;
import com.taobao.top.notify.domain.Notify;
import com.taobao.top.notify.domain.NotifyEnum;
import com.taobao.top.notify.log.TopNotifyExceptionLog;
import com.taobao.top.notify.log.TopNotifyExceptionLogMonitor;
import com.taobao.top.notify.receive.NotifyBuilder;
import com.taobao.top.notify.util.NotifyBuilderUtils;
import com.taobao.top.notify.util.NotifyConstants;
import com.taobao.top.notify.util.NotifyFillUtils;
import com.taobao.upp.rating.client.dto.billing.BillingImpactPackage;
import com.taobao.upp.switching.notify.bean.ServiceOpen;

/**
 * 
 * @author moling
 * @since 1.0, 2011-01-06
 */
public class VasNotifyBuilder implements NotifyBuilder {

	public static final Log log = LogFactory.getLog(VasNotifyBuilder.class);
	
	private TopNotifyExceptionLogMonitor topNotifyExceptionLogMonitor;
	private ObjectMessageConverter objectMessageConverter;
	private PacketMessageConverter packetMessageConverter;
	//由于无法知道目标授权用户是谁，收费所有的消息强制映射到一个用户上，通过配置固定指定一个用户
	private String savingNick;
	private Long savingUserId;

	public List<Notify> build(Message message, Integer bizType, Integer status) {
		//收费消息比较零碎，需要按照各种消息单独解析流程
		Notify notify;
		if (NotifyEnum.VAS_BILLING_SUCCESS.getStatus() == status) {
			//如果是扣费信息，按照扣费信息组装消息
			BillingImpactPackage billingMsg;
			// 获得收费消息内容
			try {
				billingMsg = (BillingImpactPackage) objectMessageConverter.fromMessage(message);
			} catch (Exception e) {
				topNotifyExceptionLogMonitor.logParseException(populateTopNotifyExceptionLog(message, e));
				return null;
			}

			// 判断消息内容是否为空
			if (null == billingMsg) {
				log.warn(NotifyBuilderUtils.logMessage(message, "Vas扣款消息体中内容为空："));
				return null;
			}
			
			notify = fillBaseMsg(status);
			notify.setContent(NotifyFillUtils.fillVasBillingMsg(billingMsg));
			
		} else if (NotifyEnum.VAS_SERVICE_OPEN.getStatus() == status) {
			//如果是服务开通信息，按照服务开通信息组装消息
			ServiceOpen serviceOpenMsg;
			// 获得收费消息内容
			try {
				serviceOpenMsg = (ServiceOpen) packetMessageConverter.fromMessage(message);
			} catch (Exception e) {
				topNotifyExceptionLogMonitor.logParseException(populateTopNotifyExceptionLog(message, e));
				return null;
			}

			// 判断消息内容是否为空
			if (null == serviceOpenMsg) {
				log.warn(NotifyBuilderUtils.logMessage(message, "Vas开通服务消息体中内容为空："));
				return null;
			}
			
			notify = fillBaseMsg(status);
			notify.setContent(NotifyFillUtils.fillVasServiceOpenMsg(serviceOpenMsg));
			
		} else {
			//如果不是已知的消息，直接返回不做解析
			log.warn(NotifyBuilderUtils.logMessage(message, "Vas消息类型无法解析："));
			return null;
		}
		
		//只要服务码为短信服务的消息
		if (null != notify && null != notify.getContent() && 
				NotifyConstants.SERVICE_CODE_DXFW.equals(notify.getContent().get(NotifyConstants.SERVICE_CODE))) {
			List<Notify> notifyList = new ArrayList<Notify>();
			notifyList.add(notify);
			return notifyList;
		} else {
			return null;
		}
	}
	
	private TopNotifyExceptionLog populateTopNotifyExceptionLog(Message message, Exception e) {
		TopNotifyExceptionLog log = new TopNotifyExceptionLog();
		log.setMsgId(message.getMessageId());
		log.setExceptionMsg("Vas消息体转换异常：" + e.getMessage());
		
		return log;
	}

	public void setTopNotifyExceptionLogMonitor(
			TopNotifyExceptionLogMonitor topNotifyExceptionLogMonitor) {
		this.topNotifyExceptionLogMonitor = topNotifyExceptionLogMonitor;
	}

	public TopNotifyExceptionLogMonitor getTopNotifyExceptionLogMonitor() {
		return topNotifyExceptionLogMonitor;
	}

	public ObjectMessageConverter getObjectMessageConverter() {
		return this.objectMessageConverter;
	}

	public void setObjectMessageConverter(
			ObjectMessageConverter objectMessageConverter) {
		this.objectMessageConverter = objectMessageConverter;
	}

	public PacketMessageConverter getPacketMessageConverter() {
		return this.packetMessageConverter;
	}

	public void setPacketMessageConverter(
			PacketMessageConverter packetMessageConverter) {
		this.packetMessageConverter = packetMessageConverter;
	}

	public String getSavingNick() {
		return this.savingNick;
	}

	public void setSavingNick(String savingNick) {
		this.savingNick = savingNick;
	}

	public Long getSavingUserId() {
		return this.savingUserId;
	}

	public void setSavingUserId(Long savingUserId) {
		this.savingUserId = savingUserId;
	}
	
	private Notify fillBaseMsg(int status) {
		Notify notify = new Notify();
		// 设置消息类别
		notify.setCategory(NotifyEnum.VAS.getCategory());
		// 无交易类别
		// 设置消息状态
		notify.setStatus(status);
		//设置notify所属的用户为规定的用户
		notify.setUserId(savingUserId);
		notify.setUserName(savingNick);
		//设置收费类型的订阅消息用户类型为所有应用,不区分买家还是卖家
		notify.setUserRole(NotifyConstants.USER_ROLE_ALL);
		
		return notify;
	}

}
