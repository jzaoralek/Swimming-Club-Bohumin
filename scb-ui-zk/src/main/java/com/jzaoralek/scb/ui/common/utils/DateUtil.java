package com.jzaoralek.scb.ui.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtil {
	public enum NoDate {
		MINUS_INFINITY,
		INFINITY
	}

	/**
	 * Vyctovy typ pro mesice v roce
	 * @author mruman
	 */
	public enum Month {
	    JANUARY(Calendar.JANUARY),
	    FEBRUARY(Calendar.FEBRUARY),
	    MARCH(Calendar.MARCH),
	    APRIL(Calendar.APRIL),
	    MAY(Calendar.MAY),
	    JUNE(Calendar.JUNE),
	    JULY(Calendar.JULY),
	    AUGUST(Calendar.AUGUST),
	    SEPTEMBER(Calendar.SEPTEMBER),
	    OCTOBER(Calendar.OCTOBER),
	    NOVEMBER(Calendar.NOVEMBER),
	    DECEMBER(Calendar.DECEMBER);

	    int calendarIndex;

	    private Month( int calendarIndex) {
	    	 this.calendarIndex = calendarIndex;
		}

	    public int getCalendarIndex() {
	    	return calendarIndex;
	    }

	    public static Month getByIndex(int index) {
	    	for (Month month : Month.values()) {
				if (month.getCalendarIndex() == index) {
					return month;
				}
			}
	    	return null;
	    }
	}

	private static final String DATE_PATTERN = "dd.MM.yyyy";
	private static final String DATETIME_PATTERN = "dd.MM.yyyy H:mm";
	private static final String TIMESTAMP_PATTERN = "dd.MM.yyyy H:mm:ss,SSS";

	private DateUtil() {}


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

	/**
	 * Vraci posledni vterinu v danem dnu ve formatu 23:59.59.999
	 * @param d
	 * @return
	 */
	public static Date normalizeToDayTimestamp(Date d) {
		if (d == null) {
			return null;
		}
		Calendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 23);            // set hour to midnight
		cal.set(Calendar.MINUTE, 59);                 // set minute in hour
		cal.set(Calendar.SECOND, 59);                 // set second in minute
		cal.set(Calendar.MILLISECOND, 999);            // set millis in second

		return cal.getTime();
	}

	/**
	 * Vraci posledni den v roce 31.12. 0:00.00.000
	 * @param year
	 * @return
	 */
	public static Date lastYearDate(int year) {
		Calendar calTo = Calendar.getInstance();
		calTo.set(Calendar.YEAR, year);
		calTo.set(Calendar.MONTH, 11); // 11 = december
		calTo.set(Calendar.DAY_OF_MONTH, 31); // new years eve
		calTo.set(Calendar.HOUR_OF_DAY, 0);
		calTo.set(Calendar.MINUTE, 0);
		calTo.set(Calendar.SECOND, 0);
		calTo.set(Calendar.MILLISECOND, 0);
		return calTo.getTime();
	}

	/**
	 * Vraci posledni den v roce 31.12. 23:59.59.999
	 * @param year
	 * @return
	 */
	public static Date lastYearTimestamp(int year) {
		Calendar calTo = Calendar.getInstance();
		calTo.set(Calendar.YEAR, year);
		calTo.set(Calendar.MONTH, 11); // 11 = december
		calTo.set(Calendar.DAY_OF_MONTH, 31); // new years eve
		calTo.set(Calendar.HOUR_OF_DAY, 23);
		calTo.set(Calendar.MINUTE, 59);
		calTo.set(Calendar.SECOND, 59);
		calTo.set(Calendar.MILLISECOND, 999);
		return calTo.getTime();
	}

	public static Date firstYearDate(int year) {
		Calendar calFrom = Calendar.getInstance();
		calFrom.set(Calendar.YEAR, year);
		calFrom.set(Calendar.MONTH, 0);
		calFrom.set(Calendar.DAY_OF_MONTH, 1);
		calFrom.set(Calendar.HOUR_OF_DAY, 0);
		calFrom.set(Calendar.MINUTE, 0);
		calFrom.set(Calendar.SECOND, 0);
		calFrom.set(Calendar.MILLISECOND, 0);
		return calFrom.getTime();
	}

	public static Date firstMonthDate(int month, int year) {
		Calendar calFrom = Calendar.getInstance();
		calFrom.set(Calendar.YEAR, year);
		calFrom.set(Calendar.MONTH, month);
		calFrom.set(Calendar.DAY_OF_MONTH, 1);
		calFrom.set(Calendar.HOUR_OF_DAY, 0);
		calFrom.set(Calendar.MINUTE, 0);
		calFrom.set(Calendar.SECOND, 0);
		calFrom.set(Calendar.MILLISECOND, 0);
		return calFrom.getTime();
	}

	public static Date lastMonthDate(int month, int year) {
		Calendar calTo = Calendar.getInstance();
		calTo.set(Calendar.YEAR, year);
		calTo.set(Calendar.MONTH, month);
		calTo.set(Calendar.DAY_OF_MONTH, 1);// tak toto nefunguje: calTo.getActualMaximum(Calendar.DAY_OF_MONTH));
		calTo.set(Calendar.HOUR_OF_DAY, 0);
		calTo.set(Calendar.MINUTE, 0);
		calTo.set(Calendar.SECOND, 0);
		calTo.set(Calendar.MILLISECOND, 0);

		calTo.add(Calendar.MONTH, 1);
		calTo.add(Calendar.DAY_OF_MONTH, -1);
		return calTo.getTime();
	}


	public static boolean isExactDay(Date d) {
		if (d == null) {
			return true;
		}
		Calendar cal = new GregorianCalendar();
		cal.setTime(d);
		return (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0
					&& cal.get(Calendar.SECOND) == 0 && cal.get(Calendar.MILLISECOND) == 0);
	}

	public static boolean isExactDayLastSecond(Date d) {
		if (d == null) {
			return true;
		}
		Calendar cal = new GregorianCalendar();
		cal.setTime(d);
		return (cal.get(Calendar.HOUR_OF_DAY) == 23 && cal.get(Calendar.MINUTE) == 59
					&& cal.get(Calendar.SECOND) == 59 && cal.get(Calendar.MILLISECOND) == 999);
	}

	public static void isExactDayExc(Date d) {
		if (!isExactDay(d)) {
			throw new IllegalArgumentException("not exact day: " + d);
		}
	}

	/**
	 * Return the equality of the day of given two dates.
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean equalsDay(Date d1, Date d2) {

		if (d1 == null && d2 == null) {
			return true;
		} else if (d1 == null || d2 == null) {
			return false;
		}
		Calendar c1 = new GregorianCalendar();
		c1.setTime(d1);
		Calendar c2 = new GregorianCalendar();
		c2.setTime(d2);
		int i = compareYear(c1, c2);
		if(i != 0) {
			return false;
		} else {
			return c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
		}
	}

	/**
	 * Return the equality of the day and month of given two dates.
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean equalsDayMonth(Date d1, Date d2) {

		if (d1 == null && d2 == null) {
			return true;
		} else if (d1 == null || d2 == null) {
			return false;
		}
		Calendar c1 = new GregorianCalendar();
		c1.setTime(d1);
		Calendar c2 = new GregorianCalendar();
		c2.setTime(d2);

		int mon1 = c1.get(Calendar.MONTH);
		int day1 = c1.get(Calendar.DAY_OF_MONTH);
		int mon2 = c2.get(Calendar.MONTH);
		int day2 = c2.get(Calendar.DAY_OF_MONTH);

		return mon1 == mon2 && day1 == day2;
	}

	public static Date addMinutes(Date d, int mins) {
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		c.add(Calendar.MINUTE, mins);
		return c.getTime();
	}

	public static Date addDays(Date d, int days) {
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		c.add(Calendar.DAY_OF_YEAR, days);
		return c.getTime();
	}

	public static Date addMonth(Date d, int months) {
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		c.add(Calendar.MONTH, months);
		return c.getTime();
	}

	/**
	 * Vraci vyssi datum, pokud jsou stejne vraci druhe datum.
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Date greaterDate(Date date1, Date date2) {
		if (date1 == null && date2 == null) {
			return null;
		} else if (date1 == null) {
			return date2;
		} else if (date2 == null) {
			return date1;
		}

		if (date1.compareTo(date2) > 0) {
			return date1;
		}
		else {
			return date2;
		}
	}

	private static int compareEra(Calendar calendar, Calendar calendar1) {
		if(calendar.getClass() != calendar1.getClass()) {
			throw new IllegalArgumentException("Cannot compare calendars of dissimilar classes: " + calendar + ", " + calendar1);
		} else {
			return calendar.get(Calendar.ERA) - calendar1.get(Calendar.ERA);
		}
	}

	private static int compareYear(Calendar calendar, Calendar calendar1) {
		int i = compareEra(calendar, calendar1);
		if(i != 0) {
			return i;
		} else {
			return calendar.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR);
		}
	}

	public static Date currDay() {
		return normalizeToDay(new Date());
	}

	public static Date currSecond() {
		return new Date(System.currentTimeMillis() / 1000 * 1000);
	}

	/**
	 * Vraci sysdate - 1 vterina.
	 * @return
	 */
	public static Date sysdateMinusSecond() {
		return new Date(System.currentTimeMillis() - 1000);
	}

	/**
	 * Vraci predane Date - 1 vterina.
	 * @return
	 */
	public static Date dateMinusSecond(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, -1);
		return cal.getTime();
	}

	/**
	 * Prevede Date na Calendar
	 * @param dt
	 * @return
	 */
	public static GregorianCalendar getGregorian(Date dt) {
		if (dt == null) {
			return null;
		}
		GregorianCalendar ret = new GregorianCalendar();
		ret.setTime(dt);
		return ret;
	}

	/**
	 * Prevede Date na Calendar s ignorovanim TZ.
	 * @param dt
	 * @return
	 */
	public static GregorianCalendar getGregorianNoTz(Date dt) {
		if (dt == null) {
			return null;
		}
		GregorianCalendar ret = new GregorianCalendar();
		long millis = dt.getTime();
		ret.setTimeInMillis(millis + ret.getTimeZone().getOffset(millis));
		ret.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
		return ret;
	}

	public static Date stringAsDate(String stringDate) {
		try {
			return new SimpleDateFormat(DATE_PATTERN).parse(stringDate);
		} catch (ParseException e) {
			throw new RuntimeException("stringDate=" + stringDate, e);
		}
	}

	public static String dateAsString(Date date) {
		return formatDate(date, DATE_PATTERN);
	}

	public static String datetimeAsString(Date date) {
		return formatDate(date, DATETIME_PATTERN);
	}

	public static String timestampAsString(Date date) {
		return formatDate(date, TIMESTAMP_PATTERN);
	}

	private static String formatDate(Date date, String pattern) {
		if (date == null) {
			return null;
		}
		return new SimpleDateFormat(pattern).format(date);
	}

	/**	checks if is an intersection in dates
	 * @param startA
	 * @param endA
	 * @param startB
	 * @param endB
	 * @return true if is an intersection
	 */
	public static boolean isIntersection(Date startA, Date endA, Date startB, Date endB) {
		if (startA == null || startB == null || endA == null || endB == null) {
			throw new IllegalArgumentException("some date is null");
		}

		if ((startA.compareTo(endB) <= 0) && endA.compareTo(startB) >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if dateB is a subset of dateA
	 * @param startA
	 * @param endA
	 * @param startB
	 * @param endB
	 * @return true if is subset
	 */
	public static boolean isSubSet(Date startA, Date endA, Date startB, Date endB) {

		if (startA == null || startB == null || endA == null || endB == null) {
			throw new IllegalArgumentException("some date is null");
		}

		if (startA.compareTo(startB) > 0 || endA.compareTo(endB) < 0) {
			return false;
		}

		return true;
	}

	/**
	 * Checks if date is between start and end
	 * @param date
	 * @param start
	 * @param end
	 * @return true if is between
	 */
	public static boolean isBetween(Date date, Date start, Date end) {

		if (date == null || start == null || end == null) {
			throw new IllegalArgumentException("some date is null");
		}

		if ((start.compareTo(date) <= 0) && date.compareTo(end) <= 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Return first day of first semester.
	 * @param year
	 * @return
	 */
	public static Date getFirstDayOfFirstSemester(Integer year) {
		if (year == null) {
			throw new IllegalArgumentException("year is null");
		}
		Calendar calendar = new GregorianCalendar(year,8,1);
		return calendar.getTime();
	}
	
	/**
	 * Return last day of second semester.
	 * @param year
	 * @return
	 */
	public static Date getLastDayOfSecondSemester(Integer year) {
		if (year == null) {
			throw new IllegalArgumentException("year is null");
		}
		Calendar calendar = new GregorianCalendar(year,7,31);
		return calendar.getTime();
	}
	
	/**
	 * Return first day of month.
	 * @param cal
	 * @return
	 */
	public static Date getFirstDateOfCurrentMonth(Calendar cal) {
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}
	
	/**
	 * Return last day of month.
	 * @param cal
	 * @return
	 */
	public static Date getLastDateOfCurrentMonth(Calendar cal) {
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}
}
