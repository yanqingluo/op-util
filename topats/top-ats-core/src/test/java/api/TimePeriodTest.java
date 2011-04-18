package api;

import junit.framework.Assert;

import org.junit.Test;

import com.taobao.top.ats.domain.TimePeriod;
import com.taobao.top.ats.task.api.TaskParser;
import com.taobao.top.ats.util.DateKit;

/**
 * 
 * @author moling
 * @since 1.0, 2010-11-22
 */
public class TimePeriodTest {
	@Test
	public void timePeriodTest() throws Exception {
		TimePeriod timePeriod = TaskParser.parseString2TimePeriod("11:10:00-12:00:00");
		Assert.assertEquals(DateKit.ymdOrYmdhms2Date("2010-11-18 11:10:00"),timePeriod.getStartDate());
		Assert.assertEquals(DateKit.ymdOrYmdhms2Date("2010-11-18 12:00:00"),timePeriod.getEndDate());
		Assert.assertTrue(timePeriod.isInTimePeriod(DateKit.ymdOrYmdhms2Date("2012-12-20 11:10:01")));
		Assert.assertFalse(timePeriod.isInTimePeriod(DateKit.ymdOrYmdhms2Date("2012-12-20 11:10:00")));
		Assert.assertFalse(timePeriod.isInTimePeriod(DateKit.ymdOrYmdhms2Date("2012-12-20 12:00:00")));
		Assert.assertFalse(timePeriod.isInTimePeriod(DateKit.ymdOrYmdhms2Date("2012-12-20 10:00:00")));
		Assert.assertFalse(timePeriod.isInTimePeriod(DateKit.ymdOrYmdhms2Date("2012-12-20 13:10:00")));
	}
	
}
