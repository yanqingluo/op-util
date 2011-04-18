package com.taobao.top.notify.persist;

import org.junit.Test;

import com.taobao.top.notify.NotifyException;
import com.taobao.top.notify.TestBase;
import com.taobao.top.notify.domain.Notify;
import com.taobao.top.notify.util.DateUtils;

/**
 * 消息写入测试类。
 * 
 * @author fengsheng
 * @since 1.0, Dec 17, 2009
 */
public class NotifyWriterTest extends TestBase {

	private NotifyWriter writer = (NotifyWriter) ctx.getBean("notifyWriter");

	@Test
	public void writeNotify() throws NotifyException {
		writer.write(mockNotify());
	}

	@Test
	public void writeNotifyWithIllegalString() throws NotifyException {
		Notify notify = mockNotify();
		notify.addContent("title", "B030 ●睫眊吔流淚monplay蒙巴拉魔幻亮彩液4g 香槟金色");
		writer.write(notify);
	}

	@Test
	public void writeNotifyWithEmptyAppKey() throws NotifyException {
		Notify notify = mockNotify();
		notify.setModified(DateUtils.getPrev7DaysEnd());
		writer.write(notify);
	}

}
