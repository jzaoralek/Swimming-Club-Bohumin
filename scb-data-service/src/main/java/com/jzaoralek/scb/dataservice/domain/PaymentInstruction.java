package com.jzaoralek.scb.dataservice.domain;

import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.utils.PaymentUtils;

public class PaymentInstruction {
	
	private String courseParticName;
	private String courseParticReprEmail;
	private String courseName;
	private long priceSemester;
	private int semester;
	private String varsymbol;
	private String bankAccountNumber;
	private UUID courseParticipantUuid;
	private CourseType courseType;

	public PaymentInstruction(String courseParticName, 
			String courseParticReprEmail, 
			String courseName, 
			long priceSemester, 
			int semester, 
			String varsymbol, 
			String bankAccountNumber, 
			UUID courseParticipantUuid,
			CourseType courseType) {
		super();
		this.courseName = courseName;
		this.priceSemester = priceSemester;
		this.semester = semester;
		this.varsymbol = varsymbol;
		this.bankAccountNumber = bankAccountNumber;
		this.courseParticReprEmail = courseParticReprEmail;
		this.courseParticName = courseParticName;
		this.courseParticipantUuid = courseParticipantUuid;
		this.courseType = courseType;
	}
	
	/**
	 * Create PaymentInstruction from CourseApplication and other params.
	 * @param courseApplication
	 * @param yearFrom
	 * @param firstSemester
	 * @param bankAccountNumber
	 * @return
	 */
	public static PaymentInstruction ofCourseApplication(CourseApplication courseApplication,
											CourseType courseType,
											int yearFrom, 
											boolean firstSemester, 
											String bankAccountNumber) {
		
		int semester = firstSemester ? 1 :2;
		
		return new PaymentInstruction(courseApplication.getCourseParticipant().getContact().getCompleteName()
				, courseApplication.getCourseParticRepresentative().getContact().getEmail1()
				, courseApplication.getCourseParticipant().getCourseName()
				, firstSemester ? courseApplication.getCourseParticipant().getCoursePaymentVO().getPriceFirstSemester() : courseApplication.getCourseParticipant().getCoursePaymentVO().getPriceSecondSemester()
				, semester
				, PaymentUtils.buildCoursePaymentVarsymbol(yearFrom, semester, courseApplication.getCourseParticipant().getVarsymbolCore())
				, bankAccountNumber
				, courseApplication.getCourseParticipant().getUuid()
				, courseType);
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
