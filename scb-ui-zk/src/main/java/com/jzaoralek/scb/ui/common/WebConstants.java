package com.jzaoralek.scb.ui.common;

public final class WebConstants {

	private WebConstants(){}

	public static final String DATE_FORMAT = "dd.MM.yyyy";
	public static final int FIRSTNAME_MAXLENGTH = 240;
	public static final int SURNAME_MAXLENGTH = 240;
	public static final int HEALTH_INSURANCE_MAXLENGTH = 240;
	public static final int DATE_MAXLENGTH = 10;
	public static final int BIRTH_NUMBER_MAXLENGTH = 12;
	public static final int EMAIL_MAXLENGTH = 100;
	public static final int PHONE_MAXLENGTH = 14;
	public static final int HEALTH_INFO_MAXLENGTH = 524;
	public static final int NAME_MAXLENGTH = 100;
	public static final int DESCRIPTION_MAXLENGTH = 240;

	public static final String WEB_DATE_PATTERN = "dd.MM.yyyy";
	public static final String WEB_DATETIME_PATTERN = "dd.MM.yyyy H:mm:ss";
	public static final String WEB_TIME_PATTERN = "H:mm";
	public static final String WEB_TIME_SECONDS_PATTERN = "mm:ss.ms";

	public static final String UUID_PARAM = "uuid";
	public static final String MODE_PARAM = "mode";
	public static final String FROM_PAGE_PARAM = "fromPage";
	public static final String ATTACHMENT_PARAM = "attachment";

	public static final String MINUTE_SECOND_DELIM = ":";
	public static final String SECOND_MILISECOND_DELIM = ",";

	public static final String SECURED_PAGE_URL = "/pages/secured";
}