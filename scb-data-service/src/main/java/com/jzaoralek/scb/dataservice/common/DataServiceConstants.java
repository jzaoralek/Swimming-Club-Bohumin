package com.jzaoralek.scb.dataservice.common;

import java.util.Date;
import java.util.GregorianCalendar;

public final class DataServiceConstants {

	private DataServiceConstants(){}
	
	public static final Date DEFAULT_DATE_FROM =  new GregorianCalendar(1900,0,31).getTime();
	public static final Date DEFAULT_DATE_TO =  new GregorianCalendar(4000,0,31).getTime();
	public static final int MAIL_SENDER_PAUSE_BETWEEN_BATCH = 15000;
	public static final int MAIL_SENDER_BATCH_SIZE = 25;
	public static final String ADMIN_EMAIL = "jakub.zaoralek@gmail.com";
	public static final String PLATBY_EMAIL = "platby@sportologic.cz";
	
}
