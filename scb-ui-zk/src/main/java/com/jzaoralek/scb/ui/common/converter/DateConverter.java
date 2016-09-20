package com.jzaoralek.scb.ui.common.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.DateUtil;


public class DateConverter implements Converter<String, Date, Component> {

	@Override
	public Date coerceToBean(String val, Component comp, BindContext ctx) {
		if (!StringUtils.hasText(val)) {
			return null;
		} else {
			try {
				return DateUtil.normalizeToDay(new SimpleDateFormat(WebConstants.WEB_DATE_PATTERN).parse(val));
			} catch (ParseException e) {
				throw UiException.Aide.wrap(e);
			}
		}
	}

	@Override
	public String coerceToUi(Date val, Component comp, BindContext ctx) {
		return (val == null ? null : new SimpleDateFormat(WebConstants.WEB_DATE_PATTERN).format(val));
	}
}
