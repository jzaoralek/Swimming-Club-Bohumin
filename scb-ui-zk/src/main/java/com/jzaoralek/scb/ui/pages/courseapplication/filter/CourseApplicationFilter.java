package com.jzaoralek.scb.ui.pages.courseapplication.filter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;
import org.zkoss.zul.Listitem;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant.PaymentNotifSendState;
import com.jzaoralek.scb.dataservice.domain.CoursePaymentVO.CoursePaymentState;
import com.jzaoralek.scb.ui.common.WebConstants;

public class CourseApplicationFilter {
	private DateFormat dateFormat = new SimpleDateFormat(WebConstants.WEB_DATE_PATTERN);
	private DateFormat dateTimeFormat = new SimpleDateFormat(WebConstants.WEB_DATETIME_PATTERN);

	private String code;

	private String courseParticName;
	private String courseParticNameLc;
	private String birthDate;
	private String birthNo;
	private String courseParticRepresentative;
	private String courseParticRepresentativeLc;
	private String phone;
	private String email;
	private String emailLc;
	private String modifAt;
	private String course;
	private String courseLc;
	private Boolean inCourse;
	private Listitem coursePaymentState;
	private Listitem paymentNotifSendState;
	private Boolean newParticipant;

	public boolean matches(String courseParticNameIn
			, String birthDateIn
			, String birthNoIn
			, String courseParticRepresentativeIn
			, String phoneIn
			, String emailIn
			, String modifAtIn
			, String courseIn
			, boolean inCourseIn
			, CoursePaymentState coursePaymentStateIn
			, boolean newParticipantIn
			, Set<PaymentNotifSendState> paymentNotifSendStateIn
			, boolean emptyMatch) {
		if (courseParticName == null
				&& birthDate == null 
				&& birthNo == null
				&& courseParticRepresentative == null 
				&& phone == null 
				&& email == null 
				&& modifAt == null 
				&& course == null 
				&& inCourse == null 
				&& coursePaymentState == null
				&& paymentNotifSendState == null
				&& newParticipant == null) {
			return emptyMatch;
		}
		if (courseParticName != null && !courseParticNameIn.toLowerCase().contains(courseParticNameLc)) {
			return false;
		}
		if (birthDate != null && !birthDateIn.contains(birthDate)) {
			return false;
		}
		if (birthNo != null && !birthNoIn.contains(birthNo)) {
			return false;
		}
		if (courseParticRepresentative != null && !courseParticRepresentativeIn.toLowerCase().contains(courseParticRepresentativeLc)) {
			return false;
		}
		if (phone != null && !phoneIn.contains(phone)) {
			return false;
		}
		if (email != null && !emailIn.toLowerCase().contains(emailLc)) {
			return false;
		}
		if (modifAt != null && !modifAtIn.contains(modifAt)) {
			return false;
		}
		if (course != null && !courseIn.toLowerCase().contains(courseLc)) {
			return false;
		}
		if (inCourse != null && (inCourse != inCourseIn)) {
			return false;
		}			
		if (coursePaymentState != null && coursePaymentState.getValue() != null && ((CoursePaymentState)coursePaymentState.getValue()) != coursePaymentStateIn) {
			return false;
		}
		if (paymentNotifSendState != null && paymentNotifSendState.getValue() != null && !paymentNotifSendStateIn.contains((PaymentNotifSendState)paymentNotifSendState.getValue())) {
			return false;
		}
		
		if (newParticipant != null && (newParticipant != newParticipantIn)) {
			return false;
		}
		
		return true;
	}

	public List<CourseApplication> getApplicationListFiltered(List<CourseApplication> codelistModelList) {
		if (codelistModelList == null || codelistModelList.isEmpty()) {
			return Collections.<CourseApplication>emptyList();
		}
		List<CourseApplication> ret = new ArrayList<CourseApplication>();
		for (CourseApplication item : codelistModelList) {
			if (matches(item.getCourseParticipant().getContact().getSurname() + " " + item.getCourseParticipant().getContact().getFirstname()
					, item.getCourseParticipant().getBirthdate() != null ? dateFormat.format(item.getCourseParticipant().getBirthdate()) : null
					, item.getCourseParticipant().getPersonalNo()
					, item.getCourseParticRepresentative().getContact().getSurname() + " " + item.getCourseParticRepresentative().getContact().getFirstname()
					, item.getCourseParticRepresentative().getContact().getPhone1()
					, item.getCourseParticRepresentative().getContact().getEmail1()
					, dateTimeFormat.format(item.getModifAt())
					, item.getCourseParticipant().getCourseName()
					, item.getCourseParticipant().inCourse()
					, (item.getCourseParticipant().getCoursePaymentVO() != null) ? (item.getCourseParticipant().getCoursePaymentVO().isOverpayed() ? CoursePaymentState.OVERPAYED : item.getCourseParticipant().getCoursePaymentVO().getStateTotal()) : null
					, !item.isCurrentParticipant()
					, item.getCourseParticipant().getPaymentNotifSendState()
					, true)) {
				ret.add(item);
			}
		}
		return ret;
	}

	public String getCode() {
		return code == null ? "" : code;
	}
	public void setCode(String code) {
		this.code = StringUtils.hasText(code) ? code.trim() : null;
	}
	public String getCourseParticName() {
		return courseParticName == null ? "" : courseParticName;
	}
	public void setCourseParticName(String name) {
		this.courseParticName = StringUtils.hasText(name) ? name.trim() : null;
		this.courseParticNameLc = this.courseParticName == null ? null : this.courseParticName.toLowerCase();
	}

	public String getCourseParticRepresentative() {
		return courseParticRepresentative == null ? "" : courseParticRepresentative;
	}

	public void setCourseParticRepresentative(String courseParticRepresentative) {
		this.courseParticRepresentative = StringUtils.hasText(courseParticRepresentative) ? courseParticRepresentative.trim() : null;
		this.courseParticRepresentativeLc = this.courseParticRepresentative == null ? null : this.courseParticRepresentative.toLowerCase();
	}

	public String getBirthDate() {
		return birthDate == null ? "" : birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = StringUtils.hasText(birthDate) ? birthDate.trim() : null;
	}

	public String getBirthNo() {
		return birthNo == null ? "" : birthNo;
	}

	public void setBirthNo(String birthNo) {
		this.birthNo = StringUtils.hasText(birthNo) ? birthNo.trim() : null;
	}

	public String getPhone() {
		return phone == null ? "" : phone;
	}

	public void setPhone(String phone) {
		this.phone = StringUtils.hasText(phone) ? phone.trim() : null;
	}

	public String getEmail() {
		return email == null ? "" : email;
	}

	public void setEmail(String email) {
		this.email = StringUtils.hasText(email) ? email.trim() : null;
		this.emailLc = this.email == null ? null : this.email.toLowerCase();
	}

	public String getModifAt() {
		return modifAt == null ? "" : modifAt;
	}

	public void setModifAt(String modifAt) {
		this.modifAt = StringUtils.hasText(modifAt) ? modifAt.trim() : null;
	}

	public String getCourse() {
		return course == null ? "" : course;
	}
	public void setCourse(String name) {
		this.course = StringUtils.hasText(name) ? name.trim() : null;
		this.courseLc = this.course == null ? null : this.course.toLowerCase();
	}
	public Boolean getInCourse() {
		return inCourse;
	}

	public void setInCourse(Boolean inCourse) {
		this.inCourse = inCourse;
	}

	public Listitem getCoursePaymentState() {
		return coursePaymentState;
	}

	public void setCoursePaymentState(Listitem roleItem) {
		this.coursePaymentState = roleItem;
	}
	
	public Boolean getNewParticipant() {
		return newParticipant;
	}

	public void setNewParticipant(Boolean newParticipant) {
		this.newParticipant = newParticipant;
	}

	public Listitem getPaymentNotifSendState() {
		return paymentNotifSendState;
	}

	public void setPaymentNotifSendState(Listitem paymentNotifSendState) {
		this.paymentNotifSendState = paymentNotifSendState;
	}
	
	public void setEmptyValues() {
		code = null;
		courseParticName = null;
		courseParticNameLc = null;
		birthDate = null;
		birthNo = null;
		courseParticRepresentative = null;
		phone = null;
		email = null;
		emailLc = null;
		modifAt = null;
		course = null;
		courseLc = null;
		inCourse = null;
		coursePaymentState = null;
		paymentNotifSendState = null;
		newParticipant = null;
	}
}