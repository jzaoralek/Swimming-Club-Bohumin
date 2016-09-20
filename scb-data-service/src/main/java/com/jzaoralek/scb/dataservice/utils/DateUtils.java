package com.jzaoralek.scb.dataservice.utils;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public final class DateUtils {

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
}