package com.sportologic.ruianclient.model;

import java.util.List;

public class RuianPlaceResponse {

	private List<RuianPlace> data;

	public List<RuianPlace> getData() {
		return data;
	}

	public void setData(List<RuianPlace> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "RuianPlaceResponse [data=" + data + "]";
	}
}
