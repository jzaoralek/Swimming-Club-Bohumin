package com.jzaoralek.scb.ui.common.converter;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.util.StringUtils;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import com.jzaoralek.scb.ui.common.WebConstants;

public class TimeSecondConverter implements Converter<String, Time, Component> {

	@Override
	public Time coerceToBean(String val, Component comp, BindContext ctx) {
		if (!StringUtils.hasText(val)) {
			return null;
		} else {
			try {
				return new Time(new SimpleDateFormat(WebConstants.WEB_TIME_SECONDS_PATTERN).parse(val).getTime());
			} catch (ParseException e) {
				throw UiException.Aide.wrap(e);
			}
		}
	}

	@Override
	public String coerceToUi(Time val, Component comp, BindContext ctx) {
		return (val == null ? null : new SimpleDateFormat(WebConstants.WEB_TIME_SECONDS_PATTERN).format(val));
	}
}
