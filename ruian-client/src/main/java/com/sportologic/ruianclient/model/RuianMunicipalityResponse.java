package com.sportologic.ruianclient.model;

import java.util.List;

public class RuianMunicipalityResponse {

	private List<RuianMunicipality> data;

	public List<RuianMunicipality> getData() {
		return data;
	}

	public void setData(List<RuianMunicipality> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "RuianRegionResponse [data=" + data + "]";
	}
}
