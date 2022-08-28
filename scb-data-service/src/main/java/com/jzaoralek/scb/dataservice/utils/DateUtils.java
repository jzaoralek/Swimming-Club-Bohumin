package com.jzaoralek.scb.dataservice.utils;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class DateUtils {

	private static final String DATE_PATTERN = "dd.MM.yyyy";
	
	private DateUtils(){}

	/**
	 * Returns YEAR grom date.
	 * @param date
	 * @return
	 */
	public static int getYearFromDate(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("date is null");
		}

		Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    return cal.get(Calendar.YEAR);
	}

	public static boolean isIntersection(Time startA, Time endA, Time startB, Time endB) {
		if (startA == null || startB == null || endA == null || endB == null) {
			throw new IllegalArgumentException("some date is null");
		}

		if(endA.compareTo(startB) <= 0 || startA.compareTo(endB) >= 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Converts given Date to normalized form.
	 * <p>
	 * Normalized form has zero time part in default GregorianCalendar.
	 *
	 * @param d date to be normalized or null
	 * @return new instance of java.util.Date with normalized time part
	 */
	public static Date normalizeToDay(Date d) {
		if (d == null) {
			return null;
		}
		Calendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
		cal.set(Calendar.MINUTE, 0);                 // set minute in hour
		cal.set(Calendar.SECOND, 0);                 // set second in minute
		cal.set(Calendar.MILLISECOND, 0);            // set millis in second

		return cal.getTime();
	}
	
	public static String dateAsString(Date date) {
		return formatDate(date, DATE_PATTERN);
	}

	private static String formatDate(Date date, String pattern) {
		if (date == null) {
			return null;
		}
		return new SimpleDateFormat(pattern).format(date);
	}
}