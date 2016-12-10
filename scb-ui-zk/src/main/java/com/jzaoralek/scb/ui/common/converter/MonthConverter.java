package com.jzaoralek.scb.ui.common.converter;

import java.util.Calendar;
import java.util.Date;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;

/**
 * Vraci lokalizovany nazev mesice z Date
 *
 */
public class MonthConverter implements Converter<String, Date, Component> {

	@Override
	public Date coerceToBean(String arg0, Component arg1, BindContext arg2) {
		// not used
		return null;
	}
	
	@Override
	public String coerceToUi(Date arg0, Component arg1, BindContext arg2) {
		if (arg0 == null) {
			return "";
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(arg0);
		return Labels.getLabel("txt.ui.month." + cal.get(Calendar.MONTH));
	}
}
