package com.taobao.top.notify.util;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import com.taobao.item.constant.ItemUpdateChangedField;
import com.taobao.item.domain.ItemDO;
import com.taobao.item.domain.ItemUpdateDO;
import com.taobao.tc.domain.dataobject.BizOrderDO;
import com.taobao.tc.refund.domain.RefundDO;

/**
 * 
 * @author moling
 * @since 1.0, 2009-12-17
 */
public class NotifyFillUtilsTest {

	private static final long[] UIDS = { 65753805L, 65753807L, 65753809L, 175755171L, 175755173L,
			65753811L, 65753813L };

	@Test
	public void fillTradeTest() {
		Map<String, Object> contentMain = NotifyFillUtils.fillTrade(mockBizOrderDOMain());
		Map<String, Object> contentNotMain = NotifyFillUtils.fillTrade(mockBizOrderDONotMain());

		assertEquals(6, contentMain.size());
		assertEquals(5, contentNotMain.size());

		assertEquals(1234567L, contentMain.get(NotifyConstants.TID));
		assertEquals("seller", contentMain.get(NotifyConstants.SELLER_NICK));
		assertEquals("buyer", contentMain.get(NotifyConstants.BUYER_NICK));
		assertEquals("99.99", contentMain.get(NotifyConstants.PAYMENT));
		assertEquals(true, contentMain.get(NotifyConstants.IS_3D));
		assertEquals("499.40", contentNotMain.get(NotifyConstants.PAYMENT));
		assertEquals(false, contentNotMain.get(NotifyConstants.IS_3D));
	}

	@Test
	public void fillRefundTest() {
		Map<String, Object> content = NotifyFillUtils.fillRefund(mockRefundDO());

		assertEquals(5, content.size());
		assertEquals(1234567L, content.get(NotifyConstants.TID));
		assertEquals(2234567L, content.get(NotifyConstants.REFUND_ID));
		assertEquals("seller", content.get(NotifyConstants.SELLER_NICK));
		assertEquals("buyer", content.get(NotifyConstants.BUYER_NICK));
		assertEquals("2.00", content.get(NotifyConstants.REFUND_FEE));
	}

	@Test
	public void fillItemDOTest() {
		Map<String, Object> content = NotifyFillUtils.fillItem(mockItemDO());

		assertEquals(5, content.size());
		assertEquals(3234567L, content.get(NotifyConstants.NUM_IID));
		assertEquals("abcdefghijklmn", content.get(NotifyConstants.IID));
		assertEquals("item title", content.get(NotifyConstants.TITLE));
		assertEquals("100.00", content.get(NotifyConstants.PRICE));
		assertEquals(10, content.get(NotifyConstants.NUM));
	}

	@Test
	public void fillItemUpdateDOTest() {
		Map<String, Object> content = NotifyFillUtils.fillItem(mockItemUpdateDO());

		assertEquals(6, content.size());
		assertEquals(4234567L, content.get(NotifyConstants.NUM_IID));
		assertEquals("abcdefghijklmn", content.get(NotifyConstants.IID));
		assertEquals("item title", content.get(NotifyConstants.TITLE));
		assertEquals("100.00", content.get(NotifyConstants.PRICE));
		assertEquals(10, content.get(NotifyConstants.NUM));
		assertEquals("title,price,item_img,prop_img,sku", content.get(NotifyConstants.CHANGED_FIELDS));
	}

	public static BizOrderDO mockBizOrderDOMain() {
		BizOrderDO order = new BizOrderDO();

		order.setBizOrderId(1234567L);
		order.setSellerId(getRandomUserId());
		order.setBuyerId(getRandomUserId());
		Random random = new Random();
		order.setSellerNick("tbtest106" + (random.nextInt(8) + 1));
		order.setBuyerNick("tbtest106" + (random.nextInt(8) + 1));
		order.setMain(BizOrderDO.IS_MAIN);
		order.setAttribute(BizOrderDO.ATTRIBUTE_ACTUAL_TOTAL_FEE, "9999");
		order.setAttribute(BizOrderDO.ATTRIBUTE_ORDER_FROM, NotifyConstants.IS_3D_VALUE);
		order.setAuctionPrice(10000);
		order.setBuyAmount(5);
		order.setAdjustFee(-10);
		order.setDiscountFee(20);
		order.setRefundFee(30);
		order.setGmtModified(new Date());

		return order;
	}

	protected static long getRandomUserId() {
		Random random = new Random();
		return UIDS[random.nextInt(UIDS.length)];
	}

	public static BizOrderDO mockBizOrderDONotMain() {
		BizOrderDO order = mockBizOrderDOMain();
		order.setMain(BizOrderDO.NOT_MAIN);
		order.removeAttributes(BizOrderDO.ATTRIBUTE_ORDER_FROM);
		return order;
	}

	public static RefundDO mockRefundDO() {
		RefundDO refund = new RefundDO();

		refund.setBizOrderId(1234567L);
		refund.setSellerNick("seller");
		refund.setBuyerNick("buyer");
		refund.setSellerId(111111L);
		refund.setBuyerId(222222L);
		refund.setRefundId(2234567L);
		refund.setReturnFee(200);
		refund.setGmtModified(new Date());

		return refund;
	}

	public static ItemDO mockItemDO() {
		ItemDO item = new ItemDO();

		item.setItemIdStr("abcdefghijklmn");
		item.setItemId(3234567L);
		item.setUserId(111111L);
		item.setTitle("item title");
		item.setReservePrice(10000L);
		item.setQuantity(10);
		item.setGmtModified(new Date());

		return item;
	}

	public static ItemUpdateDO mockItemUpdateDO() {
		ItemUpdateDO item = new ItemUpdateDO(4234567L);

		item.setItemIdStr("abcdefghijklmn");
		item.setTitle("item title");
		item.setReservePrice(10000L);
		item.setQuantity(10);
		long option = 0L;
		option = option | ItemUpdateChangedField.TITLE;
		option = option | ItemUpdateChangedField.PRICE;
		option = option | ItemUpdateChangedField.SKU;
		option = option | ItemUpdateChangedField.IMAGE;
		item.setChangedField(option);
		item.setGmtModified(new Date());

		return item;
	}
}
