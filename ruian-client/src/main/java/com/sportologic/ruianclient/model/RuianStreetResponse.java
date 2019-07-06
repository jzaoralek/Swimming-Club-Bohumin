package com.sportologic.ruianclient.model;

import java.util.List;

public class RuianStreetResponse {
	
	private List<RuianStreet> data;

	public List<RuianStreet> getData() {
		return data;
	}

	public void setData(List<RuianStreet> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "RuianStreet [data=" + data + "]";
	}
}
