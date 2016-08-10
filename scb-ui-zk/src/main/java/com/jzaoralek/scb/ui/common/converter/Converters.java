package com.jzaoralek.scb.ui.common.converter;

public final class Converters {

	private Converters(){}

	private static final DateConverter dateConverter = new DateConverter();

	public static DateConverter getDateconverter() {
		return dateConverter;
	}
}
