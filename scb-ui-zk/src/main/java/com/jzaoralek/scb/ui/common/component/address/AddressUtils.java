package com.jzaoralek.scb.ui.common.component.address;

import com.jzaoralek.scb.ui.common.utils.WebUtils;

public final class AddressUtils {

	private static final String ADDRESS_BVALID_ATTR = "addressValid";
	
	private AddressUtils() {}
	
	public static boolean isAddressValid() {
		Boolean addressValid = (Boolean)WebUtils.getDesktopAtribute(ADDRESS_BVALID_ATTR);
		return addressValid != null && addressValid;
	}
	
	public static void setAddressValid() {
		WebUtils.setDesktopAtribute(ADDRESS_BVALID_ATTR, Boolean.TRUE);
	}
	
	public static void setAddressInvalid() {
		WebUtils.setDesktopAtribute(ADDRESS_BVALID_ATTR, Boolean.FALSE);
	}
}
