package com.jzaoralek.scb.ui.common.converter;

public final class Converters {

	private Converters(){}

	private static final DateConverter dateConverter = new DateConverter();
	private static final DateTimeConverter dateTimeConverter = new DateTimeConverter();

	public static DateConverter getDateconverter() {
		return dateConverter;
	}

	public static DateTimeConverter getDateTimeConverter() {
		return dateTimeConverter;
	}
}
