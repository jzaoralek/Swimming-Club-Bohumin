package com.jzaoralek.scb.dataservice.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "resultCode", "message"})
public class ActionResult {

	public static Integer OK_RESULT = 0;
	public static Integer FAIL_RESULT = 1;

	private Integer resultCode;
	private String message;

	public Integer getResultCode() {
		return resultCode;
	}
	public void setResultCode(Integer resultCode) {
		this.resultCode = resultCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}