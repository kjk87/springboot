package kr.co.pplus.store.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
	public static final String YEAR_FORMAT = "yyyy";

	public static final String MONTH_FORMAT = "MM";
	
	public static final String DAY_FORMAT = "dd";

	public final static String PATTERN = "yyyyMMddHHmmss";
	
	public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
	  
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	
	public static final String TIME_FORMAT = "HH:mm:ss";
	
	public static final String YEAR_MONTH_FORMAT = "yyyyMM";
	
	public static final String BASE_FORMAT = "yyyyMMddHHmmss";
	
	public static final String BASE_DATE_FORMAT = "yyyyMMdd";
	
	public static final String BASE_TIME_FORMAT = "HHmmss";

	public final static int HOUR = Calendar.HOUR;
	public final static int DATE = Calendar.DATE;
	public final static int MONTH = Calendar.MONTH;
	public final static int MINUTE = Calendar.MINUTE;
	public final static int SECOND = Calendar.SECOND;
	public final static int YEAR = Calendar.YEAR;
	public final static int MILLISECOND = Calendar.MILLISECOND;

	
	public static Date getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}
	
	public static int getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
	
	public static int getMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH);
	}
	
	public static int getDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DATE);
	}
	
	public static int getWeekOfYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.WEEK_OF_YEAR);
	}
	
	public static Date getDate(String fmt, String src) throws ParseException {
		SimpleDateFormat f = new SimpleDateFormat(fmt);
		return f.parse(src);
	}

	public static String getDate(String fmt, Date src) {
		SimpleDateFormat f = new SimpleDateFormat(fmt);
		return f.format(src);
	}

	public static String getDateString(String fmt, Date src) {
		SimpleDateFormat f = new SimpleDateFormat(fmt);
		return f.format(src);
	}

	public static Date getFirstDayOfMonth(Date date) throws ParseException {
		String strDate = getDateString(DateUtil.BASE_DATE_FORMAT, date);
		String strFirstDate = strDate.substring(0, 6) + "01";
		return getDate(DateUtil.BASE_DATE_FORMAT, strFirstDate);
	}
	
	public static Date getStartDateOfDay(Date date) throws ParseException {
		String strDate = getDateString(DateUtil.BASE_DATE_FORMAT, date);
		String startDate = strDate + "000000";
		return getDate(DateUtil.BASE_FORMAT, startDate);
	}

	public static Date getEndDateOfDay(Date date) throws ParseException {
		String strDate = getDateString(DateUtil.BASE_DATE_FORMAT, date);
		String endDate = strDate + "235959";
		return getDate(DateUtil.BASE_FORMAT, endDate);
	}
	
	public static Date getLastDateOfPrevMonth(Date baseDate) {
		Calendar calendar = Calendar.getInstance();
		if (baseDate != null)
			calendar.setTime(baseDate);
		calendar.add(5, -1 * calendar.get(5));
		Date lastDateOfLastMonth = new Date(calendar.getTimeInMillis());
		return lastDateOfLastMonth;
	}

	public static Date getLastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, DateUtil.getYear(date));
		cal.set(Calendar.MONTH, DateUtil.getMonth(date));
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		return cal.getTime();
	}

	
	public static Date getDateAdd(Date src, int unit, int diff) {
		Calendar cal = Calendar.getInstance();
		if (src != null)
			cal.setTime(src);

		if (diff != 0)
			cal.add(unit, diff);

		return cal.getTime();
	}
	
	public static long getDateDiff(Date date1, Date date2, int unit) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		long offset = 0;

		switch (unit) {
		case DateUtil.SECOND:
			offset = date1.getTime() - date2.getTime();
			return offset / 1000;
		case DateUtil.MINUTE:
			offset = date1.getTime() - date2.getTime();
			return offset / (1000 * 60);
		case DateUtil.HOUR:
			offset = date1.getTime() - date2.getTime();
			return offset / (1000 * 60 * 60);
		case DateUtil.DATE:
			offset = date1.getTime() - date2.getTime();
			return offset / (1000 * 60 * 60 * 24);
		case DateUtil.MONTH:
			offset = (cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR)) * 12;
			offset += cal1.get(Calendar.MONDAY) - cal2.get(Calendar.MONTH);
			return offset;
		case DateUtil.YEAR:
			return cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		default:
			return date1.getTime() - date2.getTime();
		}
	}
	
	public static List<String> getDateListInDuration(Date start, Date end, int unit) {
		List<String> dates = new ArrayList<String>();
		Date date = start;
		while (true) {
			if (date.getTime() > end.getTime())
				break;
			
			switch (unit) {
			case Calendar.DATE:
				dates.add(getDate("yyyy-MM-dd", date));
				date = getDateAdd(date, DateUtil.DATE, 1);
				break;
			case Calendar.WEEK_OF_YEAR:
				dates.add(getDate("yyyy", date) + "-" + getWeekOfYear(date));
				date = getDateAdd(date, Calendar.DATE, 7);
				break;
			case Calendar.MONTH:
				dates.add(getDate("yyyy-MM", date));
				date = getDateAdd(date, Calendar.MONTH, 1);
				break;
			case Calendar.YEAR:
				dates.add(getDate("yyyy", date));
				date = getDateAdd(date, Calendar.YEAR, 1);
			}
		}
		return dates;
	}

}
