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
    public static final String DATE_COL_WIDTH = "120px";

	public static final String WEB_DATE_PATTERN = "dd.MM.yyyy";
	public static final String WEB_DATE_ISCUS_PATTERN = "dd.MM.yyyy";
	public static final String WEB_DATETIME_PATTERN = "dd.MM.yyyy H:mm:ss";
	public static final String WEB_TIME_PATTERN = "H:mm";
	public static final String WEB_TIME_SECONDS_PATTERN = "mm:ss.ms";

	public static final String UUID_PARAM = "uuid";
	public static final String NEW_TAB_PARAM = "newTab";
	public static final String COURSE_COURSE_PARTIC_UUID_PARAM = "courseCourseParticUuid";
	public static final String MODE_PARAM = "mode";
	public static final String FROM_PAGE_PARAM = "fromPage";
	public static final String ATTACHMENT_PARAM = "attachment";
	public static final String ITEM_PARAM = "item";
	public static final String CHECKED_PARAM = "checked";
	public static final String TAB_PARAM = "tab";
	public static final String COURSE_UUID_PARAM = "courseUuid";
	public static final String COURSE_PARTIC_UUID_PARAM = "courseParticUuid";
	public static final String COURSE_APPLICATION_PARAM = "courseApplication";
	public static final String COURSE_APPLICATION_LIST_PARAM = "courseApplicationList";
	public static final String COURSE_PARTIC_PARAM = "coursePartic";
	public static final String COURSE_PARAM = "course";
	public static final String COURSE_PARTICIPANT_UUID_PARAM = "courseParticipant";
	public static final String COURSE_PARTICIPANT_REPRESENTATIVE_UUID_PARAM = "courseParticRepresentative";
	public static final String CALLBACK_PARAM = "callback";
	public static final String EMAIL_RECIPIENT_LIST_PARAM = "emailRecipientList";
	public static final String COURSE_LEARN_LESSONS_MONTH_SELECTED_PARAM = "courseLearnLessonsMonthSelected";
	public static final String USER_LIST_CACHE_PARAM = "userListCache";
	public static final String NAME_PARAM = "name";
	public static final String COMPONENT_PARAM = "component";
	public static final String ACTIVE_PARAM = "active";
	
	public static final String YEAR_FROM_PARAM = "yearFrom";
	public static final String YEAR_TO_PARAM = "yearTo";
	public static final String YEAR_FROM_TO_PARAM = "yearTo";
	public static final String YEAR_SELECTED_PARAM = "yearSelected";
	public static final String SEMESTER_PARAM = "semester";
	public static final String BANK_ACCOUNT_NO_PARAM = "bankAccountNo";
	public static final String COURSE_LIST_EXT_FILTER_PARAM = "courseListExtFilter";
	public static final String COURSE_TYPE_PARAM = "courseType";
	
	public static final String APP_MANIFEST = "/META-INF/MANIFEST.MF";

	public static final String MINUTE_SECOND_DELIM = ":";
	public static final String SECOND_MILISECOND_DELIM = ",";
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static final String SECURED_PAGE_URL = "/pages/secured/ADMIN";
	public static final String NOTIFICATION_MESSAGE = "notificationMessage";
	public static final String FROM_PAGE_URL = "fromPageUrl";
	
	public static final String EMAIL_PATTERN = "[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public static final String EMAIL_LIST_SEPARATOR = ";";
	
	public static final String COLON = ": ";
	public static final String SEMICOLON_DELIM = " | ";
	public static final String BIRTH_NO_DELIM = "/";
	public static final String APPLICATION_PDF_CONTENT_TYPE = "application/pdf";
	
}