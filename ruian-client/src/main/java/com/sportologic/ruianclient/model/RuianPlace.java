package com.sportologic.ruianclient.model;

import java.util.Comparator;

import org.springframework.util.StringUtils;

public class RuianPlace {

	public static final Comparator<RuianPlace> PLACE_COMP =
			new Comparator<RuianPlace>() {
				public int compare(RuianPlace place1, RuianPlace place2) {
					Long place1Cp = StringUtils.hasText(place1.getPlaceCp()) ? Long.valueOf(place1.getPlaceCp()) : 0L;
					Long place2Cp = StringUtils.hasText(place2.getPlaceCp()) ? Long.valueOf(place2.getPlaceCp()) : 0L;
		            return place1Cp.compareTo(place2Cp);
				}
		};
		
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
	public String getPlaceStr() {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.hasText(this.placeCp)) {
			sb.append(this.placeCp);			
		}
		if (StringUtils.hasText(this.placeCo)) {
			if (StringUtils.hasText(sb.toString())) {
				sb.append("/");				
			}
			sb.append(this.placeCo);			
		}
		if (StringUtils.hasText(this.placeCe)) {
			if (StringUtils.hasText(sb.toString())) {
				sb.append(" ");
			}
			sb.append(this.placeCe);			
		}
		if (StringUtils.hasText(this.placeZip)) {
			if (StringUtils.hasText(sb.toString())) {
				sb.append(", ");
			}
			sb.append(this.placeZip);			
		}
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return "RuianPlace [placeCe=" + placeCe + ", placeCp=" + placeCp + ", placeCo=" + placeCo + ", placeZip="
				+ placeZip + ", placeId=" + placeId + "]";
	}
}
