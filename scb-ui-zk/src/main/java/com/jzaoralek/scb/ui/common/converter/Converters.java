package com.jzaoralek.scb.ui.common.converter;

public final class Converters {

	private Converters(){}

	private static final DateConverter dateConverter = new DateConverter();
	private static final DateTimeConverter dateTimeConverter = new DateTimeConverter();
	private static final TimeConverter timeConverter = new TimeConverter();
	private static final EnumLabelConverter enumLabelConverter = new EnumLabelConverter();
	private static final TimeSecondConverter timeSecondConverter = new TimeSecondConverter();
	private static final IntervalToMillsConverter intervalToMillsConverter = new IntervalToMillsConverter();
	private static final MonthConverter monthConverter = new MonthConverter();

	public static DateConverter getDateconverter() {
		return dateConverter;
	}

	public static DateTimeConverter getDateTimeConverter() {
		return dateTimeConverter;
	}

	public static TimeConverter getTimeconverter() {
		return timeConverter;
	}

	public static EnumLabelConverter getEnumlabelconverter() {
		return enumLabelConverter;
	}

	public static TimeSecondConverter getTimeSecondconverter() {
		return timeSecondConverter;
	}

	public static IntervalToMillsConverter getIntervaltomillsconverter() {
		return intervalToMillsConverter;
	}
	
	public static MonthConverter getMonthConverter() {
		return monthConverter;
	}
}