package com.jzaoralek.scb.dataservice.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonPropertyOrder({ "resultCode", "message"})
public class ActionResult {

	public static String OK_RESULT = "OK";
	public static String FAIL_RESULT = "FAIL";

	private String resultCode;
	private String message;

	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}