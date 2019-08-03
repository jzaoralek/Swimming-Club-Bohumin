package com.sportologic.ruianclient.model;

import java.io.Serializable;
import java.util.Comparator;

public class RuianStreet implements Serializable {

	private static final long serialVersionUID = 1007445285835273835L;

	public static final Comparator<RuianStreet> STREET_COMP =
			new Comparator<RuianStreet>() {
				public int compare(RuianStreet street1, RuianStreet street2) {
					String street1Name = street1.getStreetName();
					String street2Name = street2.getStreetName();
		            return street1Name.compareTo(street2Name);
				}
		};
		
	private String streetName;

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	@Override
	public String toString() {
		return "RuianStreet [streetName=" + streetName + "]";
	}
}
