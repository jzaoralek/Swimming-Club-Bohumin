package com.sportologic.ruianclient.model;

import java.util.List;

public class RuianRegionResponse {

	private List<RuianRegion> data;

	public List<RuianRegion> getData() {
		return data;
	}

	public void setData(List<RuianRegion> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "RuianRegionResponse [data=" + data + "]";
	}
}