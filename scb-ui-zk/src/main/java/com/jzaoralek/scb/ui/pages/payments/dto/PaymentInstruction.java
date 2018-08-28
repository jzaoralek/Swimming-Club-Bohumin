package com.jzaoralek.scb.ui.pages.payments.dto;

import java.util.UUID;

public class PaymentInstruction {
	
	private String courseParticName;
	private String courseParticReprEmail;
	private String courseName;
	private long priceSemester;
	private int semester;
	private String varsymbol;
	private String bankAccountNumber;
	private UUID courseParticipantUuid;

	public PaymentInstruction(String courseParticName, String courseParticReprEmail, String courseName, long priceSemester, int semester, String varsymbol, String bankAccountNumber, UUID courseParticipantUuid) {
		super();
		this.courseName = courseName;
		this.priceSemester = priceSemester;
		this.semester = semester;
		this.varsymbol = varsymbol;
		this.bankAccountNumber = bankAccountNumber;
		this.courseParticReprEmail = courseParticReprEmail;
		this.courseParticName = courseParticName;
		this.courseParticipantUuid = courseParticipantUuid;
	}
	
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public long getPriceSemester() {
		return priceSemester;
	}
	public void setPriceSemester(long priceSemester) {
		this.priceSemester = priceSemester;
	}
	public int getSemester() {
		return semester;
	}
	public void setSemester(int semester) {
		this.semester = semester;
	}
	public String getVarsymbol() {
		return varsymbol;
	}
	public void setVarsymbol(String varsymbol) {
		this.varsymbol = varsymbol;
	}
	public String getBankAccountNumber() {
		return bankAccountNumber;
	}
	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}
	public String getCourseParticReprEmail() {
		return courseParticReprEmail;
	}
	public void setCourseParticReprEmail(String courseParticReprEmail) {
		this.courseParticReprEmail = courseParticReprEmail;
	}
	public String getCourseParticName() {
		return courseParticName;
	}
	public void setCourseParticName(String courseParticName) {
		this.courseParticName = courseParticName;
	}
	public UUID getCourseParticipantUuid() {
		return courseParticipantUuid;
	}
	public void setCourseParticipantUuid(UUID courseParticipantUuid) {
		this.courseParticipantUuid = courseParticipantUuid;
	}

	@Override
	public String toString() {
		return "PaymentInstruction [courseParticName=" + courseParticName + ", courseParticReprEmail="
				+ courseParticReprEmail + ", courseName=" + courseName + ", priceSemester=" + priceSemester
				+ ", semester=" + semester + ", varsymbol=" + varsymbol + ", bankAccountNumber=" + bankAccountNumber
				+ "]";
	}
}
