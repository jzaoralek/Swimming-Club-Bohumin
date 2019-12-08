package com.jzaoralek.scb.ui.common.converter;

import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import com.jzaoralek.scb.ui.common.WebConstants;

public class IntervalToMillsConverter implements Converter<String, Long, Component> {

	@Override
	public Long coerceToBean(String compAttr, Component component, BindContext ctx) {
		int minuteSecondDelimIdndex = compAttr.indexOf(WebConstants.MINUTE_SECOND_DELIM);
		int secondMillisDelimIndex = compAttr.indexOf(WebConstants.SECOND_MILISECOND_DELIM);

		String minutes = "";
		if (minuteSecondDelimIdndex >= 0) {
			minutes = compAttr.substring(0, minuteSecondDelimIdndex);
		}

		String seconds = "";
		if (minuteSecondDelimIdndex >= 0 && secondMillisDelimIndex >= 0) {
			seconds = compAttr.substring(minuteSecondDelimIdndex + 1, secondMillisDelimIndex);
		} else if (minuteSecondDelimIdndex < 0 && secondMillisDelimIndex >= 0) {
			seconds = compAttr.substring(0, secondMillisDelimIndex);
		} else if (minuteSecondDelimIdndex < 0 && secondMillisDelimIndex < 0) {
			seconds = compAttr;
		}

		String millis = "";
		if (secondMillisDelimIndex >= 0) {
			millis = compAttr.substring(secondMillisDelimIndex + 1);
		}

		long minuteslMills = StringUtils.hasText(minutes) ? TimeUnit.MINUTES.toMillis(Long.valueOf(minutes)) : 0;
		long secondMills = StringUtils.hasText(seconds) ? TimeUnit.SECONDS.toMillis(Long.valueOf(seconds)) : 0;
		long millsMills = StringUtils.hasText(millis) ? Long.parseLong(millis) : 0;

		return minuteslMills + secondMills + millsMills;
	}

	@Override
	public String coerceToUi(Long beanProp, Component component, BindContext ctx) {
		if (beanProp == null) {
			return null;
		}

		if (beanProp < 0) {
			throw new IllegalArgumentException("millis less then zero");
		}

		long minutes = TimeUnit.MILLISECONDS.toMinutes(beanProp);
		beanProp -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(beanProp);
        beanProp -= TimeUnit.SECONDS.toMillis(seconds);
        long milisecundes = TimeUnit.MILLISECONDS.toMillis(beanProp);

        StringBuilder sb = new StringBuilder();

        if (minutes > 0) {
        	sb.append(minutes + WebConstants.MINUTE_SECOND_DELIM);
        }
        sb.append(parseLongToStr(seconds));
        sb.append(WebConstants.SECOND_MILISECOND_DELIM + parseLongToStr(milisecundes));

        return sb.toString();
	}
	
	/**
	 * Prevede long na String, pokud ma jit o dvouciferne cislo doplni nulu pred cislo.
	 * @param value
	 * @return
	 */
	private String parseLongToStr(long value) {
		String valueStr = String.valueOf(value);
		if (!StringUtils.hasText(valueStr)) {
			return "00";
		}
		if  (valueStr.length() == 1) {
			return "0" + value;
		}
		
		return valueStr;
	}
}
