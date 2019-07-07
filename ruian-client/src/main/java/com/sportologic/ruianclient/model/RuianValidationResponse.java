package com.sportologic.ruianclient.model;

public class RuianValidationResponse {

	private String status;
	private String message;
	private RuianPlaceValidation place;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public RuianPlaceValidation getPlace() {
		return place;
	}
	public void setPlace(RuianPlaceValidation place) {
		this.place = place;
	}
	
	@Override
	public String toString() {
		return "RuianValidationResponse [status=" + status + ", message=" + message + ", place=" + place + "]";
	}
}
