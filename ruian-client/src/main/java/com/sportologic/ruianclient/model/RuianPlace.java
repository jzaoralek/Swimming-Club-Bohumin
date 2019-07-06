package com.sportologic.ruianclient.model;

public class RuianPlace {

	private String placeCe;
	private String placeCp;
	private String placeCo;
	private String placeZip;
	private String placeId;
	
	public String getPlaceCe() {
		return placeCe;
	}
	public void setPlaceCe(String placeCe) {
		this.placeCe = placeCe;
	}
	public String getPlaceCp() {
		return placeCp;
	}
	public void setPlaceCp(String placeCp) {
		this.placeCp = placeCp;
	}
	public String getPlaceCo() {
		return placeCo;
	}
	public void setPlaceCo(String placeCo) {
		this.placeCo = placeCo;
	}
	public String getPlaceZip() {
		return placeZip;
	}
	public void setPlaceZip(String placeZip) {
		this.placeZip = placeZip;
	}
	public String getPlaceId() {
		return placeId;
	}
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
	
	@Override
	public String toString() {
		return "RuianPlace [placeCe=" + placeCe + ", placeCp=" + placeCp + ", placeCo=" + placeCo + ", placeZip="
				+ placeZip + ", placeId=" + placeId + "]";
	}
}
