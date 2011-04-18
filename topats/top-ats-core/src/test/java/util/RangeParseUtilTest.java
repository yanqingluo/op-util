package util;

import java.text.ParseException;

import junit.framework.Assert;

import org.junit.Test;

import com.taobao.top.ats.util.RangeParseUtil;


/**
 * 
 * @author moling
 * @since 1.0, 2010-11-23
 */
public class RangeParseUtilTest {
	@Test
	public void getNextDateHourTest() throws ParseException {
		Assert.assertEquals("2010-11-23 04:00:00", RangeParseUtil.getNextDate("2010-11-23 00:00:00", "2010-11-23 23:29:59", 4, "hour"));
	}
	
	@Test
	public void getNextDateDayTest() throws ParseException {
		Assert.assertEquals("2010-11-24 00:00:00", RangeParseUtil.getNextDate("2010-11-23 00:00:00", "2010-11-25 23:29:59", 1, "day"));
	}
	
	@Test
	public void getNextDateMonthTest() throws ParseException {
		Assert.assertEquals("2010-12-23 00:00:00", RangeParseUtil.getNextDate("2010-11-23 00:00:00", "2010-12-25 23:29:59", 1, "month"));
	}
	
	@Test
	public void getNextDateStartLargerThanEndTest() throws ParseException {
		Assert.assertNull(RangeParseUtil.getNextDate("2010-11-23 00:00:00", "2010-10-22 23:29:59", 1, "hour"));
	}
	
	@Test
	public void getNextDateWrongMeasureTest() throws ParseException {
		Assert.assertNull(RangeParseUtil.getNextDate("2010-11-23 00:00:00", "2010-11-23 23:29:59", 1, "year"));
	}
	
	@Test
	public void getNextDateAtLastTest() throws ParseException {
		Assert.assertEquals("2010-11-23 23:29:59", RangeParseUtil.getNextDate("2010-11-23 00:00:00", "2010-11-23 23:29:59", 1, "day"));
	}
	
	@Test
	public void getNextDateWrongStartFormatTest() throws ParseException {
		Assert.assertNull(RangeParseUtil.getNextDate("2010-11-2 00:00:00", "2010-12-25 23:29:59", 1, "month"));
	}
	
	@Test
	public void getNextDateWrongEndFormatTest() throws ParseException {
		Assert.assertNull(RangeParseUtil.getNextDate("2010-11-23 00:00:00", "2010-12-25 2:29:59", 1, "month"));
	}
	
	@Test
	public void getNextNumberTest() throws ParseException {
		Assert.assertEquals("51", RangeParseUtil.getNextNumber("1", "100", 50));
	}
	
	@Test
	public void getNextNumberStartEqualEndTest() throws ParseException {
		Assert.assertEquals("101", RangeParseUtil.getNextNumber("100", "100", 50));
	}
	
	@Test
	public void getNextNumberStartLargerThanEndTest() throws ParseException {
		Assert.assertNull(RangeParseUtil.getNextNumber("101", "100", 50));
	}
	
	@Test
	public void getNextNumberWrongStartTest() throws ParseException {
		Assert.assertNull(RangeParseUtil.getNextNumber("ab", "100", 50));
	}
	
	@Test
	public void getNextNumberWrongEndTest() throws ParseException {
		Assert.assertNull(RangeParseUtil.getNextNumber("1", "cd", 50));
	}
	
	@Test
	public void getNextNumberLastPartTest() throws ParseException {
		Assert.assertEquals("101", RangeParseUtil.getNextNumber("55", "100", 50));
	}
}
