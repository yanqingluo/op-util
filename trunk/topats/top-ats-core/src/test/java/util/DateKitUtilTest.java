package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.taobao.top.ats.util.DateKit;

/**
 * 
 * @author moling
 * @since 1.0, 2010-8-24
 */
public class DateKitUtilTest {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	@Test
	public void ymdTest() throws ParseException {
		Date date = DateKit.ymdOrYmdhms2Date("2010-08-24");
		Assert.assertEquals("2010-08-24 00:00:00.000", sdf.format(date));
	}
	
	@Test
	public void ymdhmsTest() throws ParseException {
		Date date = DateKit.ymdOrYmdhms2Date("2010-08-24 01:12:10");
		Assert.assertEquals("2010-08-24 01:12:10.000", sdf.format(date));
	}
	
	@Test
	public void ymdhmsSTest() throws ParseException {
		Date date = DateKit.ymdOrYmdhms2Date("2010-08-24 01:12:10.033");
		Assert.assertEquals("2010-08-24 01:12:10.033", sdf.format(date));
	}
	
	@Test (expected=ParseException.class)
	public void ymdhmsErrorLengthTest() throws ParseException {
		DateKit.ymdOrYmdhms2Date("2010-08-24 1:12:1");
	}
	
	@Test (expected=ParseException.class)
	public void ymdhmsErrorNumberTest() throws ParseException {
		DateKit.ymdOrYmdhms2Date("2010-08-24 aa:12:01");
	}
}
