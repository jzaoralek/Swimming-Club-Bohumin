package com.jzaoralek.scb.dataservice.utils;

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
}