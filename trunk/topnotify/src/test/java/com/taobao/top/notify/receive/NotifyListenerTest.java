package com.taobao.top.notify.receive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.taobao.hsf.notify.client.MessageStatus;
import com.taobao.hsf.notify.client.message.BytesMessage;
import com.taobao.hsf.notify.client.message.Message;
import com.taobao.item.constant.ClientAppName;
import com.taobao.item.constant.ItemNotifyConstants;
import com.taobao.item.constant.OpIdConstants;
import com.taobao.item.domain.ItemDO;
import com.taobao.item.domain.ItemUpdateDO;
import com.taobao.tc.domain.dataobject.BizOrderDO;
import com.taobao.tc.domain.dataobject.ModifyOrderDO;
import com.taobao.tc.domain.dataobject.ModifyOrderInfoTO;
import com.taobao.tc.domain.dataobject.OrderInfoTO;
import com.taobao.tc.message.convertor.ModifyOrderInfoMessageConverter;
import com.taobao.tc.message.convertor.OrderInfoMessageConverter;
import com.taobao.tc.refund.client.message.RefundInfoMessageConverter;
import com.taobao.tc.refund.domain.RefundDO;
import com.taobao.tc.refund.domain.message.RefundInfoTO;
import com.taobao.top.mq.TopMQService;
import com.taobao.top.notify.TestBase;
import com.taobao.top.notify.domain.TradeMsgType;
import com.taobao.top.notify.domain.Notify;
import com.taobao.top.notify.domain.NotifyEnum;
import com.taobao.top.notify.receive.builder.ItemNotifyBuilder;
import com.taobao.top.notify.receive.builder.RefundNotifyBuilder;
import com.taobao.top.notify.receive.builder.TradeNotifyBuilder;
import com.taobao.top.notify.receive.builder.TradePlusNotifyBuilder;
import com.taobao.top.notify.send.NotifySender;
import com.taobao.top.notify.util.NotifyFillUtilsTest;
import com.taobao.top.tim.service.SamService;
import com.taobao.top.tim.service.TIMServiceException;

/**
 * 
 * @author moling
 * @since 1.0, 2009-12-17
 */
public class NotifyListenerTest extends TestBase {

	private NotifyListener nl = (NotifyListener) ctx.getBean("notifyListener");
	private ItemNotifyBuilder ib = (ItemNotifyBuilder) ctx.getBean("itemNotifyBuilder");
	private TradeNotifyBuilder tb = (TradeNotifyBuilder) ctx.getBean("tradeNotifyBuilder");
	private TradePlusNotifyBuilder tpb = (TradePlusNotifyBuilder) ctx.getBean("tradePlusNotifyBuilder");
	private RefundNotifyBuilder rb = (RefundNotifyBuilder) ctx.getBean("refundNotifyBuilder");

	@Test
	public void testBuildTradeNotifys() throws TIMServiceException {
		// 填充AppKey前
		List<Notify> list = tb.build(getTradeMessage(), 0, 0);
		assertEquals(2, list.size());
		if (list.get(0).getUserName().equals("seller")) {
			assertEquals(Long.valueOf(111111), list.get(0).getUserId());
			assertEquals(Long.valueOf(222222), list.get(1).getUserId());
			assertEquals("buyer", list.get(1).getUserName());
		} else {
			assertEquals(Long.valueOf(111111), list.get(1).getUserId());
			assertEquals("seller", list.get(1).getUserName());
			assertEquals(Long.valueOf(222222), list.get(0).getUserId());
			assertEquals("buyer", list.get(0).getUserName());
		}

		assertEquals(NotifyEnum.TRADE.getCategory(), list.get(0).getCategory().intValue());
		assertEquals(Integer.valueOf(0), list.get(0).getBizType());
		assertEquals(Integer.valueOf(0), list.get(0).getStatus());
//		assertEquals(new Date(10000L), list.get(0).getModified());

		// 填充AppKey后
		list = nl.buildTradeNotifys(getTradeMessage());
		assertEquals(2, list.size());
		assertEquals(Long.valueOf(111111), list.get(0).getUserId());
		assertEquals("seller", list.get(0).getUserName());
		if ("appKey1".equals(list.get(0).getAppKey())) {
			assertEquals("appKey2", list.get(1).getAppKey());
		} else {
			assertEquals("appKey2", list.get(0).getAppKey());
			assertEquals("appKey1", list.get(1).getAppKey());
		}

	}

	@Test
	public void testBuildTradeNotifysWrongMsgType() throws TIMServiceException {
		List<Notify> list = nl.buildTradeNotifys(getWrongMsgTypeMessage());
		assertNull(list);
	}

	@Test
	public void testBuildRefundNotifys() throws TIMServiceException {
		// 填充AppKey前
		List<Notify> list = rb.build(getRefundMessage(), null, 0);
		assertEquals(2, list.size());
		if (list.get(0).getUserName().equals("seller")) {
			assertEquals(Long.valueOf(111111), list.get(0).getUserId());
			assertEquals(Long.valueOf(222222), list.get(1).getUserId());
			assertEquals("buyer", list.get(1).getUserName());
		} else {
			assertEquals(Long.valueOf(111111), list.get(1).getUserId());
			assertEquals("seller", list.get(1).getUserName());
			assertEquals(Long.valueOf(222222), list.get(0).getUserId());
			assertEquals("buyer", list.get(0).getUserName());
		}

		assertEquals(NotifyEnum.REFUND.getCategory(), list.get(0).getCategory().intValue());
		assertNull(list.get(0).getBizType());
		assertEquals(Integer.valueOf(0), list.get(0).getStatus());
//		assertEquals(new Date(10000L), list.get(0).getModified());

		// 填充AppKey后
		list = nl.buildTradeNotifys(getRefundMessage());
		assertEquals(2, list.size());
		assertEquals(Long.valueOf(111111), list.get(0).getUserId());
		assertEquals("seller", list.get(0).getUserName());
		assertEquals(Integer.valueOf(3), list.get(0).getStatus());
		if ("appKey1".equals(list.get(0).getAppKey())) {
			assertEquals("appKey2", list.get(1).getAppKey());
		} else {
			assertEquals("appKey2", list.get(0).getAppKey());
			assertEquals("appKey1", list.get(1).getAppKey());
		}

	}

	@Test
	public void testBuildTradePlusNotifys() throws TIMServiceException {
		// 填充AppKey前
		List<Notify> list = tpb.build(getTradePlusMessageMain(), 0, 0);
		assertEquals(4, list.size());
		if (list.get(0).getUserName().equals("seller")) {
			assertEquals(Long.valueOf(111111), list.get(0).getUserId());
			assertEquals(Long.valueOf(222222), list.get(1).getUserId());
			assertEquals("buyer", list.get(1).getUserName());
		} else {
			assertEquals(Long.valueOf(111111), list.get(1).getUserId());
			assertEquals("seller", list.get(1).getUserName());
			assertEquals(Long.valueOf(222222), list.get(0).getUserId());
			assertEquals("buyer", list.get(0).getUserName());
		}

		assertEquals(NotifyEnum.TRADE.getCategory(), list.get(0).getCategory().intValue());
		assertEquals(Integer.valueOf(0), list.get(0).getBizType());
		assertEquals(Integer.valueOf(0), list.get(0).getStatus());
//		assertEquals(new Date(10000L), list.get(0).getModified());

		// 填充AppKey后
		list = nl.buildTradePlusNotifys(getTradePlusMessageMain());
		assertEquals(4, list.size());
		assertEquals(Long.valueOf(111111), list.get(0).getUserId());
		assertEquals("seller", list.get(0).getUserName());
		if ("appKey1".equals(list.get(0).getAppKey())) {
			assertEquals("appKey2", list.get(1).getAppKey());
		} else {
			assertEquals("appKey2", list.get(0).getAppKey());
			assertEquals("appKey1", list.get(1).getAppKey());
		}

	}

	@Test
	public void testBuildTradePlusNotifysWrongMsgType() throws TIMServiceException {
		List<Notify> list = nl.buildTradePlusNotifys(getWrongMsgTypeMessage());
		assertNull(list);
	}

	@Test
	public void testBuildItemNotifysAdd() throws IOException, TIMServiceException {
		// 填充AppKey前
		List<Notify> list = ib.build(getItemAddMessage(), 0, 0);
		assertEquals(5, list.size());
		assertEquals(Long.valueOf(111111), list.get(0).getUserId());
		assertEquals("seller", list.get(0).getUserName());

		assertEquals(NotifyEnum.ITEM.getCategory(), list.get(0).getCategory().intValue());
		assertEquals(null, list.get(0).getBizType());
		assertEquals(Integer.valueOf(8), list.get(0).getStatus());
//		assertEquals(new Date(10000L), list.get(0).getModified());

		// 填充AppKey后
		list = nl.buildItemNotifys(getItemAddMessage());
		assertEquals(10, list.size());
		assertEquals(Long.valueOf(111111), list.get(0).getUserId());
		assertEquals("seller", list.get(0).getUserName());
		if ("appKey1".equals(list.get(0).getAppKey())) {
			assertEquals("appKey2", list.get(1).getAppKey());
		} else {
			assertEquals("appKey2", list.get(0).getAppKey());
			assertEquals("appKey1", list.get(1).getAppKey());
		}

	}

	@Test
	public void testBuildItemNotifysEdit() throws IOException, TIMServiceException {
		// 填充AppKey前
		List<Notify> list = ib.build(getItemEditMessage(), 0, 0);
		assertEquals(1, list.size());
		assertEquals(Long.valueOf(111111), list.get(0).getUserId());
		assertEquals("seller", list.get(0).getUserName());

		assertEquals(NotifyEnum.ITEM.getCategory(), list.get(0).getCategory().intValue());
		assertEquals(null, list.get(0).getBizType());
		assertEquals(Integer.valueOf(7), list.get(0).getStatus());
//		assertEquals(new Date(10000L), list.get(0).getModified());

		// 填充AppKey后
		list = nl.buildItemNotifys(getItemEditMessage());
		assertEquals(2, list.size());
		assertEquals(Long.valueOf(111111), list.get(0).getUserId());
		assertEquals("seller", list.get(0).getUserName());
		if ("appKey1".equals(list.get(0).getAppKey())) {
			assertEquals("appKey2", list.get(1).getAppKey());
		} else {
			assertEquals("appKey2", list.get(0).getAppKey());
			assertEquals("appKey1", list.get(1).getAppKey());
		}

	}

	@Test
	public void testBuildItemNotifysEditIds() throws IOException, TIMServiceException {
		// 填充AppKey前
		List<Notify> list = ib.build(getItemEditIdsMessage(), 0, 0);
		assertEquals(2, list.size());
		assertEquals(Long.valueOf(111111), list.get(0).getUserId());
		assertEquals("seller", list.get(0).getUserName());

		assertEquals(NotifyEnum.ITEM.getCategory(), list.get(0).getCategory().intValue());
		assertEquals(null, list.get(0).getBizType());
		assertEquals(Integer.valueOf(0), list.get(0).getStatus());
//		assertEquals(new Date(111L), list.get(0).getModified());
//		assertEquals(new Date(222L), list.get(1).getModified());
		assertEquals(1, list.get(0).getContent().size());
		assertEquals(1, list.get(1).getContent().size());
		assertEquals(Long.valueOf(111), list.get(0).getContent().get("num_iid"));
		assertEquals(Long.valueOf(222), list.get(1).getContent().get("num_iid"));

		// 填充AppKey后
		list = nl.buildItemNotifys(getItemEditIdsMessage());
		assertEquals(4, list.size());
		assertEquals(Long.valueOf(111111), list.get(0).getUserId());
		assertEquals("seller", list.get(0).getUserName());
		assertEquals(Integer.valueOf(5), list.get(0).getStatus());
		if ("appKey1".equals(list.get(0).getAppKey())) {
			assertEquals("appKey2", list.get(1).getAppKey());
		} else {
			assertEquals("appKey2", list.get(0).getAppKey());
			assertEquals("appKey1", list.get(1).getAppKey());
		}

	}

	@Test
	public void testBuildItemNotifysDelete() throws IOException, TIMServiceException {
		// 填充AppKey前
		List<Notify> list = ib.build(getItemDeleteIdsMessage(), 0, 0);
		assertEquals(2, list.size());
		assertEquals(Long.valueOf(111111), list.get(0).getUserId());
		assertEquals("seller", list.get(0).getUserName());

		assertEquals(NotifyEnum.ITEM.getCategory(), list.get(0).getCategory().intValue());
		assertEquals(null, list.get(0).getBizType());
		assertEquals(Integer.valueOf(0), list.get(0).getStatus());
//		assertEquals(new Date(111L), list.get(0).getModified());
//		assertEquals(new Date(222L), list.get(1).getModified());
		assertEquals(1, list.get(0).getContent().size());
		assertEquals(1, list.get(1).getContent().size());
		assertEquals(Long.valueOf(111), list.get(0).getContent().get("num_iid"));
		assertEquals(Long.valueOf(222), list.get(1).getContent().get("num_iid"));

		// 填充AppKey后
		list = nl.buildItemNotifys(getItemDeleteIdsMessage());
		assertEquals(4, list.size());
		assertEquals(Long.valueOf(111111), list.get(0).getUserId());
		assertEquals("seller", list.get(0).getUserName());
		assertEquals(Integer.valueOf(5), list.get(0).getStatus());
		if ("appKey1".equals(list.get(0).getAppKey())) {
			assertEquals("appKey2", list.get(1).getAppKey());
		} else {
			assertEquals("appKey2", list.get(0).getAppKey());
			assertEquals("appKey1", list.get(1).getAppKey());
		}

	}

	@Test
	public void testBuildItemNotifysNotMatch() throws IOException, TIMServiceException {
		// 填充AppKey前
		List<Notify> list = ib.build(getItemIdsDatesNotMatchMessage(), 0, 0);
		assertEquals(2, list.size());

		// 填充AppKey后
		list = nl.buildItemNotifys(getItemIdsDatesNotMatchMessage());
		assertEquals(4, list.size());

	}

	@Test
	public void testBuildItemNotifysWrongMsgType() throws IOException, TIMServiceException {
		List<Notify> list = nl.buildItemNotifys(getWrongMsgTypeMessage());
		assertNull(list);
	}

	@Test
	public void testReceiveMessage() throws IOException {
		MessageStatus status = new MessageStatus();
		nl.receiveMessage(getTradeMessage(), status);
		nl.receiveMessage(getRefundMessage(), status);
		nl.receiveMessage(getTradePlusMessageMain(), status);
		nl.receiveMessage(getItemAddMessage(), status);

		assertEquals(false, status.isRollbackOnly());
	}
	
	@Test
	public void testTopMQService(){
		TopMQService service = (TopMQService)ctx.getBean("topMQService");
		System.out.println(service);
		NotifySender sender = (NotifySender)ctx.getBean("notifySender");
		System.out.println(sender);
		SamService sam = (SamService)ctx.getBean("samService");
		System.out.println(sam);
	}
	
	public Message getTradeMessage() {
		OrderInfoMessageConverter converter = new OrderInfoMessageConverter(new TradeMsgType(200));
		OrderInfoTO orderInfo = new OrderInfoTO();
		BizOrderDO bizOrder = NotifyFillUtilsTest.mockBizOrderDOMain();
		orderInfo.setBizOrderDO(bizOrder);

		Message message = converter.toMessage(orderInfo);
		message.setTopic(NotifySubscriber.TRADE_TOPIC);
		message.setMessageType("200-trade-test-biztype");
		return message;
	}

	public Message getTradePlusMessageMain() {
		ModifyOrderInfoMessageConverter converter = new ModifyOrderInfoMessageConverter(new TradeMsgType(200));
		ModifyOrderInfoTO tradePlusMsg = new ModifyOrderInfoTO();
		List<ModifyOrderDO> modifyOrderList = new ArrayList<ModifyOrderDO>();

		ModifyOrderDO plus = new ModifyOrderDO();
		BizOrderDO bizOrder = NotifyFillUtilsTest.mockBizOrderDOMain();
		plus.setBizOrderId(12345L);
		plus.setSellerNick("seller");
		plus.setBuyerNick("buyer");
		plus.setBuyerRateStatus(5);
		plus.setSellerRateStatus(5);
		plus.setModifyBuyerMemo(true);
		plus.setModifyBuyerRateStatus(false);
		plus.setModifySellerMemo(false);
		plus.setModifySellerRateStatus(false);
		plus.setGmtModified(new Date());
		plus.setBizOrderDO(bizOrder);

		for (int i = 0; i < 2; ++i) {
			modifyOrderList.add(plus);
		}

		tradePlusMsg.setBizType(200);
		tradePlusMsg.setModifyOrderList(modifyOrderList);

		Message message = converter.toMessage(tradePlusMsg);

		message.setTopic(NotifySubscriber.TRADE_PLUS_TOPIC);
		message.setMessageType("200-tradeplust-test");
		return message;
	}

	public Message getRefundMessage() {
		RefundInfoMessageConverter converter = new RefundInfoMessageConverter("refund-test");
		RefundInfoTO refundMsg = new RefundInfoTO();

		RefundDO refund = NotifyFillUtilsTest.mockRefundDO();
		refund.setGmtCreate(new Date());
		refund.setRefundStatus(5);

		refundMsg.setRefundDO(refund);

		Message message = converter.toMessage(refundMsg);
		message.setTopic("TRADE");
		message.setMessageType("refund-test");
		return message;
	}

	public Message getItemAddMessage() throws IOException {
		BytesMessage message = new BytesMessage();
		message.setTopic(NotifySubscriber.ITEM_TOPIC);
		message.setMessageType("item-test");

		Map<String, Object> msgContent = new HashMap<String, Object>();
		msgContent.put(ItemNotifyConstants.sellerNick, "seller");
		msgContent.put(ItemNotifyConstants.sellerId, 111111L);
		msgContent.put(ItemNotifyConstants.clientAppName, ClientAppName.TOP);
		msgContent.put(ItemNotifyConstants.opId, OpIdConstants.sellerSaveItemRecommed);

		List<ItemDO> itemList = new ArrayList<ItemDO>();
		ItemDO item = NotifyFillUtilsTest.mockItemDO();
		for (int i = 0; i < 5; ++i) {
			itemList.add(item);
		}
		msgContent.put(ItemNotifyConstants.newItemList, itemList);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(msgContent);
		oos.close();
		message.setBody(baos.toByteArray());
		return message;
	}

	public Message getItemEditMessage() throws IOException {
		BytesMessage message = new BytesMessage();
		message.setTopic(NotifySubscriber.ITEM_TOPIC);
		message.setMessageType("item-test");

		Map<String, Object> msgContent = new HashMap<String, Object>();
		msgContent.put(ItemNotifyConstants.sellerNick, "seller");
		msgContent.put(ItemNotifyConstants.sellerId, 111111L);
		msgContent.put(ItemNotifyConstants.clientAppName, ClientAppName.TOP);
		msgContent.put(ItemNotifyConstants.opId, OpIdConstants.sellerSaveItemUnRecommed);

		List<ItemUpdateDO> itemList = new ArrayList<ItemUpdateDO>();
		ItemUpdateDO item = NotifyFillUtilsTest.mockItemUpdateDO();
		itemList.add(item);

		msgContent.put(ItemNotifyConstants.newUpdateList, itemList);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(msgContent);
		oos.close();
		message.setBody(baos.toByteArray());
		return message;
	}

	public Message getItemEditIdsMessage() throws IOException {
		BytesMessage message = new BytesMessage();
		message.setTopic(NotifySubscriber.ITEM_TOPIC);
		message.setMessageType("item-test");

		Map<String, Object> msgContent = new HashMap<String, Object>();
		msgContent.put(ItemNotifyConstants.sellerNick, "seller");
		msgContent.put(ItemNotifyConstants.sellerId, 111111L);
		msgContent.put(ItemNotifyConstants.clientAppName, ClientAppName.TOP);

		List<Long> ids = new ArrayList<Long>();
		ids.add(111L);
		ids.add(222L);
		List<Date> dates = new ArrayList<Date>();
		dates.add(new Date(111L));
		dates.add(new Date(222L));

		msgContent.put(ItemNotifyConstants.newUpdateIdList, ids);
		msgContent.put(ItemNotifyConstants.gmtModifiedList, dates);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(msgContent);
		oos.close();
		message.setBody(baos.toByteArray());
		return message;
	}

	public Message getItemDeleteIdsMessage() throws IOException {
		BytesMessage message = new BytesMessage();
		message.setTopic(NotifySubscriber.ITEM_TOPIC);
		message.setMessageType("item-test");

		Map<String, Object> msgContent = new HashMap<String, Object>();
		msgContent.put(ItemNotifyConstants.sellerNick, "seller");
		msgContent.put(ItemNotifyConstants.sellerId, 111111L);
		msgContent.put(ItemNotifyConstants.clientAppName, ClientAppName.TOP);

		List<Long> ids = new ArrayList<Long>();
		ids.add(111L);
		ids.add(222L);
		List<Date> dates = new ArrayList<Date>();
		dates.add(new Date(111L));
		dates.add(new Date(222L));

		msgContent.put(ItemNotifyConstants.delItemIdList, ids);
		msgContent.put(ItemNotifyConstants.gmtModifiedList, dates);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(msgContent);
		oos.close();
		message.setBody(baos.toByteArray());
		return message;
	}

	public Message getItemIdsDatesNotMatchMessage() throws IOException {
		BytesMessage message = new BytesMessage();
		message.setTopic(NotifySubscriber.ITEM_TOPIC);
		message.setMessageType("item-test");

		Map<String, Object> msgContent = new HashMap<String, Object>();
		msgContent.put(ItemNotifyConstants.sellerNick, "seller");
		msgContent.put(ItemNotifyConstants.sellerId, 111111L);
		msgContent.put(ItemNotifyConstants.clientAppName, ClientAppName.TOP);

		List<Long> ids = new ArrayList<Long>();
		ids.add(111L);
		ids.add(222L);
		List<Date> dates = new ArrayList<Date>();
		dates.add(new Date(111L));

		msgContent.put(ItemNotifyConstants.delItemIdList, ids);
		msgContent.put(ItemNotifyConstants.gmtModifiedList, dates);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(msgContent);
		oos.close();
		message.setBody(baos.toByteArray());
		return message;
	}

	public Message getWrongTopicMessage() {
		BytesMessage message = new BytesMessage();
		message.setTopic("Wrong-Topic");
		return message;
	}

	public Message getWrongMsgTypeMessage() {
		BytesMessage message = new BytesMessage();
		message.setTopic(NotifySubscriber.TRADE_TOPIC);
		message.setMessageType("wrong-msg-type");
		return message;
	}
}
