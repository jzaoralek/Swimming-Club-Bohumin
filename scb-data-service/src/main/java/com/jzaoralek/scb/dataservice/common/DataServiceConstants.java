package com.jzaoralek.scb.dataservice.common;

import java.util.Date;
import java.util.GregorianCalendar;

public final class DataServiceConstants {

	private DataServiceConstants(){}
	
	public static final Date DEFAULT_DATE_FROM =  new GregorianCalendar(1900,0,31).getTime();
	public static final Date DEFAULT_DATE_TO =  new GregorianCalendar(4000,0,31).getTime();
}
