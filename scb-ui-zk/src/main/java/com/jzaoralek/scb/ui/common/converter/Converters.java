package com.jzaoralek.scb.ui.common.converter;

public final class Converters {

	private Converters(){}

	private static final DateConverter dateConverter = new DateConverter();
	private static final DateTimeConverter dateTimeConverter = new DateTimeConverter();
	private static final TimeConverter timeConverter = new TimeConverter();
	private static final EnumLabelConverter enumLabelConverter = new EnumLabelConverter();

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
}
