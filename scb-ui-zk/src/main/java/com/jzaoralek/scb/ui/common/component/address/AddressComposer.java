package com.jzaoralek.scb.ui.common.component.address;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;

/**
 * Composer je potreba validovat adresu pomoci tlacitka v adrese.
 * Slouzi pro forward onClick tlacitka pro validaci adresy na onClick tlacitka pro submit formulare.
 *
 */
public class AddressComposer extends SelectorComposer<Grid> {

	private static final long serialVersionUID = -1466444220516050781L;

	@Override
	public void doAfterCompose(Grid comp) throws Exception {
		Button submitBtn = (Button)comp.getAttribute("submitBtn");
		Button submitAddressBtn = (Button)comp.getFellowIfAny("submitAddressBtn");
		
		if (submitBtn != null && submitAddressBtn != null) {
			submitBtn.addForward(Events.ON_CLICK, submitAddressBtn, Events.ON_CLICK);
		}
		
		super.doAfterCompose(comp);
	}
}