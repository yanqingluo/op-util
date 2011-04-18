package com.taobao.top.notify.receive.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.hsf.notify.client.message.BytesMessage;
import com.taobao.hsf.notify.client.message.Message;
import com.taobao.item.constant.ItemNotifyConstants;
import com.taobao.item.constant.OpIdConstants;
import com.taobao.item.domain.ItemDO;
import com.taobao.item.domain.ItemUpdateDO;
import com.taobao.item.util.ItemNotifyUtil;
import com.taobao.top.notify.domain.Notify;
import com.taobao.top.notify.domain.NotifyEnum;
import com.taobao.top.notify.log.TopNotifyExceptionLog;
import com.taobao.top.notify.log.TopNotifyExceptionLogMonitor;
import com.taobao.top.notify.receive.NotifyBuilder;
import com.taobao.top.notify.util.NotifyBuilderUtils;
import com.taobao.top.notify.util.NotifyConstants;
import com.taobao.top.notify.util.NotifyFillUtils;

/**
 * 
 * @author moling
 * @since 1.0, 2009-12-14
 */
public class ItemNotifyBuilder implements NotifyBuilder {

	public static final Log log = LogFactory.getLog(ItemNotifyBuilder.class);
	
	private TopNotifyExceptionLogMonitor topNotifyExceptionLogMonitor;

	@SuppressWarnings("unchecked")
	public List<Notify> build(Message message, Integer bizType, Integer status) {
		// 获得商品消息内容
		byte[] msgBody = ((BytesMessage) message).getBody();
		Map<String, Object> msgContent = null;
		try {
			msgContent = ItemNotifyUtil.convertBodyToMap(msgBody);
		} catch (Exception e) {
//			log.error(NotifyBuilderUtils.logMessage(message, "Item消息体转换异常："));
			topNotifyExceptionLogMonitor.logParseException(populateTopNotifyExceptionLog(message, e));
			return null;
		}

		// 判断消息内容是否为空
		if (null == msgContent) {
			log.warn(NotifyBuilderUtils.logMessage(message, "Item消息体中内容为空："));
			return null;
		}

		List<Notify> notifyList = null;
		List<ItemDO> itemList = null;
		List<ItemUpdateDO> itemUpdateList = null;
		List<Long> delItemIdList = null;
		List<Long> newUpdateIdList = null;
		//ic的消息对于修改时间全部不做解析处理，通通采用notify发出的时间戳
//		List<Date> gmtModifiedList = null;

		if (msgContent.containsKey(ItemNotifyConstants.newItemList)) {
			// 发布商品的列表
			itemList = (List<ItemDO>) msgContent.get(ItemNotifyConstants.newItemList);
		} else if (msgContent.containsKey(ItemNotifyConstants.newUpdateList)) {
			// 编辑商品的列表
			itemUpdateList = (List<ItemUpdateDO>) msgContent.get(ItemNotifyConstants.newUpdateList);
		} else if (msgContent.containsKey(ItemNotifyConstants.delItemIdList)) {
			// 删除商品的列表，里面还有ItemNotifyConstants.itemOptionsList暂时不解析
			delItemIdList = (List<Long>) msgContent.get(ItemNotifyConstants.delItemIdList);
//			gmtModifiedList = (List<Date>) msgContent.get(ItemNotifyConstants.gmtModifiedList);
		} else if (msgContent.containsKey(ItemNotifyConstants.newUpdateIdList)) {
			// 上下架等操作对应的商品列表，里面还有ItemNotifyConstants.itemOptionsList暂时不解析
			newUpdateIdList = (List<Long>) msgContent.get(ItemNotifyConstants.newUpdateIdList);
//			gmtModifiedList = (List<Date>) msgContent.get(ItemNotifyConstants.gmtModifiedList);
		}

		// 如果没有完整的改动过的商品列表就不生成消息
		if ((null == itemList || itemList.isEmpty())
				&& (null == itemUpdateList || itemUpdateList.isEmpty())
				&& (null == delItemIdList || delItemIdList.isEmpty())
				&& (null == newUpdateIdList || newUpdateIdList.isEmpty())) {
			// 如果得到的消息不完全,记录,不处理
			log.warn(NotifyBuilderUtils.logMessage(message, "Item消息体中内容不完整："));
			return null;
		}

		notifyList = new ArrayList<Notify>();
		Notify notify = new Notify();

		// 过滤TOP发送的消息,具体策略未知，暂时不启用
//		String clientName = (String) msgContent.get(ItemNotifyConstants.clientAppName);
//		if (null != clientName && ClientAppName.TOP.equals(clientName)) {
//			String appKey = (String) msgContent.get(ItemNotifyConstants.customCarry);
//			if (null != appKey && appKey.length() > 0) {
//				notify.setAppKey(appKey);
//			}
//		}

		// 设置消息类别
		notify.setCategory(NotifyEnum.ITEM.getCategory());

		// 无交易类别

		// 设置消息状态
		notify.setStatus(status);
		// 判断是否橱窗推荐消息,如果是，重置status位
		String opId = (String) msgContent.get(ItemNotifyConstants.opId);
		if (OpIdConstants.sellerSaveItemRecommed.equals(opId)) {
			notify.setStatus(NotifyEnum.ITEM_RECOMMEND_ADD.getStatus());
		} else if (OpIdConstants.sellerSaveItemUnRecommed.equals(opId)) {
			notify.setStatus(NotifyEnum.ITEM_RECOMMEND_DELETE.getStatus());
		}

		// 设置用户的昵称
		String sellerNick = (String) msgContent.get(ItemNotifyConstants.sellerNick);
		notify.setUserName(sellerNick);
		//商品消息上缺少用户的nick和id，将nick加在content里面的发出去，主动取消息不解析无影响，接收消息parse兼容
		notify.addContent(NotifyConstants.NICK, sellerNick);

		// 设置用户id
		Long sellerId = (Long) msgContent.get(ItemNotifyConstants.sellerId);
		notify.setUserId(sellerId);
		//商品消息上缺少用户的nick和id，将userId加在content里面的发出去，主动取消息不解析无影响，接收消息parse兼容
		notify.addContent(NotifyConstants.USER_ID, sellerId);
		
		//设置商品类型的订阅消息用户类型都为所有应用,不区分买家还是卖家 moling 2010-12-03
		notify.setUserRole(NotifyConstants.USER_ROLE_ALL);

		// 处理添加商品的相关信息
		if (null != itemList) {
			for (int i = 0; i < itemList.size(); ++i) {
				// 加入新增商品
				Notify itemAddNotify = notify.clone();
				// 设置操作产生的时间，默认在listener里面统一用bornTime设置
//				itemAddNotify.setModified(itemList.get(i).getGmtModified());
				itemAddNotify.setContent(NotifyFillUtils.fillItem(itemList.get(i)));
				notifyList.add(itemAddNotify);
			}
		}
		// 处理更新商品的数字编号
		if (null != itemUpdateList) {
			for (int i = 0; i < itemUpdateList.size(); ++i) {
				// 加入更新商品
				Notify itemUpdateNotify = notify.clone();
				// 设置操作产生的时间，默认在listener里面统一用bornTime设置
//				itemUpdateNotify.setModified(itemUpdateList.get(i).getGmtModified());
				itemUpdateNotify.setContent(NotifyFillUtils.fillItem(itemUpdateList.get(i)));
				notifyList.add(itemUpdateNotify);
			}
		}
		// 处理删除商品的数字编号
		if (null != delItemIdList) {
			for (int i = 0; i < delItemIdList.size(); ++i) {
				// 加入删除商品
				Notify itemDeleteNotify = notify.clone();

				itemDeleteNotify.addContent(NotifyConstants.NUM_IID, delItemIdList.get(i));
				//moling 2010-4-7 同一条消息的删除时间取第一个的，默认在listener里面统一用bornTime设置
//				if (null != gmtModifiedList && gmtModifiedList.size() > 0) {
//					itemDeleteNotify.addContent(NotifyConstants.MODIFIED, gmtModifiedList.get(0));
//				}
				
				notifyList.add(itemDeleteNotify);
			}
		}

		// 处理上下架一类更新的商品的数字编号
		if (null != newUpdateIdList) {
			for (int i = 0; i < newUpdateIdList.size(); ++i) {
				// 加入删除商品
				Notify itemUpdateNotify = notify.clone();

				itemUpdateNotify.addContent(NotifyConstants.NUM_IID, newUpdateIdList.get(i));
				//moling 2010-4-7 同一条消息的更新时间取第一个的，默认在listener里面统一用bornTime设置
//				if (null != gmtModifiedList && gmtModifiedList.size() > 0) {
//					itemUpdateNotify.addContent(NotifyConstants.MODIFIED, gmtModifiedList.get(0));
//				}
				notifyList.add(itemUpdateNotify);
			}
		}

		return notifyList;
	}
	
	private TopNotifyExceptionLog populateTopNotifyExceptionLog(Message message, Exception e) {
		TopNotifyExceptionLog log = new TopNotifyExceptionLog();
		log.setMsgId(message.getMessageId());
		log.setExceptionMsg("Item消息体转换异常：" + e.getMessage());
		
		return log;
	}

	public void setTopNotifyExceptionLogMonitor(
			TopNotifyExceptionLogMonitor topNotifyExceptionLogMonitor) {
		this.topNotifyExceptionLogMonitor = topNotifyExceptionLogMonitor;
	}

	public TopNotifyExceptionLogMonitor getTopNotifyExceptionLogMonitor() {
		return topNotifyExceptionLogMonitor;
	}

}
