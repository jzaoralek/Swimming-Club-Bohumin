package com.jzaoralek.scb.ui.common.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import com.jzaoralek.scb.ui.common.utils.WebUtils;


public class EnumLabelConverter implements Converter<String, Enum<?>, Component> {

	@Override
	public Enum<?> coerceToBean(String compAttr, Component component, BindContext ctx) {
		//mr: metoda je (ve validation fázi) volána, ale s jejím výsledkem nepracujeme.
		//Vrátí-li null, OK. Vyhodí-li Exception, přeruší se zpracování requestu.
		//V danou chvíli ani nejde metodu rozumně implementovat
		//(pokud mi přijde hodnota Rozpracovaná, nevím, o který enum se jedná.)
		return null;
	}

	@Override
	public String coerceToUi(Enum<?> value, Component component, BindContext ctx) {
		return (value == null ? null : WebUtils.getMessageItemFromEnum(value));
	}
}
