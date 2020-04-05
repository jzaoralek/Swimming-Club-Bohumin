package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CourseParticipant implements IdentEntity {

	public enum PaymentNotifSendState {
		NO_NOTIFICATION,
		NOT_SENT_FIRST_SEMESTER,
		NOT_SENT_SECOND_SEMESTER,
		SENT_FIRST_SEMESTER,
		SENT_SECOND_SEMESTER,
		BOTH;
	}
	
	public enum IscusRole {
		ACTIVE_SPORTSMAN_PROFESSIONAL("1"),
		ACTIVE_SPORTSMAN("2"),
		OTHER("3");
		
		private String abbr;

		private IscusRole(String abbr) {
			this.abbr = abbr;
		}

		public String getAbbr() {
			return abbr;
		}
	}
	
	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private Contact contact;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "CET")
	private Date birthdate;
	private String  personalNo;
	private String healthInsurance;
	private String healthInfo;
	private List<Result> resultList;
	private List<Course> courseList;
	private UUID representativeUuid;
	private UUID courseUuid;
	private String courseName;
	private CoursePaymentVO coursePaymentVO;
	private int varsymbolCore;
	private Date notifiedSemester1PaymentAt;
	private Date notifiedSemester2PaymentAt;
	private Date courseParticipationInterruptedAt; 
	private IscusRole iscusRole;
	private String iscusParticId;
	private String iscusSystemId;

	/*
	 * Atribut neulozeny v databazi, pouzity ve statistice dochazka.
	 */
	private boolean lessonAttendance;

	public CourseParticipant(CourseParticipant courseParticipant) {
		super();
		this.uuid = courseParticipant.uuid;
		this.modifBy = courseParticipant.modifBy;
		this.modifAt = courseParticipant.modifAt;
		this.contact = courseParticipant.contact;
		this.birthdate = courseParticipant.birthdate;
		this.personalNo = courseParticipant.personalNo;
		this.healthInsurance = courseParticipant.healthInsurance;
		this.healthInfo = courseParticipant.healthInfo;
		this.resultList = courseParticipant.resultList;
		this.courseList = courseParticipant.courseList;
		this.lessonAttendance = courseParticipant.lessonAttendance;
		this.representativeUuid = courseParticipant.representativeUuid;
		this.courseUuid = courseParticipant.courseUuid;
		this.courseName = courseParticipant.courseName;
		this.coursePaymentVO = courseParticipant.coursePaymentVO;
		this.iscusRole = courseParticipant.getIscusRole();
		this.iscusParticId = courseParticipant.getIscusParticId();
		this.iscusSystemId = courseParticipant.getIscusSystemId();
	}

	public CourseParticipant() {
		this.contact = new Contact();
	}

	@Override
	public UUID getUuid() {
		return uuid;
	}
	@Override
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	@Override
	public String getModifBy() {
		return modifBy;
	}
	@Override
	public void setModifBy(String modifBy) {
		this.modifBy = modifBy;
	}
	@Override
	public Date getModifAt() {
		return modifAt;
	}
	@Override
	public void setModifAt(Date modifAt) {
		this.modifAt = modifAt;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	public List<Result> getResultList() {
		return resultList;
	}
	public void setResultList(List<Result> resultList) {
		this.resultList = resultList;
	}
	public Date getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
	public String getPersonalNo() {
		return personalNo;
	}
	public void setPersonalNo(String personalNo) {
		this.personalNo = personalNo;
	}
	public String getHealthInsurance() {
		return healthInsurance;
	}
	public void setHealthInsurance(String healthInsurance) {
		this.healthInsurance = healthInsurance;
	}
	public String getHealthInfo() {
		return healthInfo;
	}

	public void setHealthInfo(String healthInfo) {
		this.healthInfo = healthInfo;
	}

	public List<Course> getCourseList() {
		return courseList;
	}

	public void setCourseList(List<Course> courseList) {
		this.courseList = courseList;
	}

	public boolean inCourse() {
		return !CollectionUtils.isEmpty(this.courseList);
	}
	
	public boolean isLessonAttendance() {
		return lessonAttendance;
	}

	public void setLessonAttendance(boolean lessonAttendance) {
		this.lessonAttendance = lessonAttendance;
	}
	
	public UUID getRepresentativeUuid() {
		return representativeUuid;
	}

	public void setRepresentativeUuid(UUID representativeUuid) {
		this.representativeUuid = representativeUuid;
	}

	public UUID getCourseUuid() {
		return courseUuid;
	}

	public void setCourseUuid(UUID courseUuid) {
		this.courseUuid = courseUuid;
	}
	
	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public CoursePaymentVO getCoursePaymentVO() {
		return coursePaymentVO;
	}

	public void setCoursePaymentVO(CoursePaymentVO coursePaymentVO) {
		this.coursePaymentVO = coursePaymentVO;
	}
	
	public int getVarsymbolCore() {
		return varsymbolCore;
	}

	public void setVarsymbolCore(int varsymbolCore) {
		this.varsymbolCore = varsymbolCore;
	}
	
	public Date getNotifiedSemester1PaymentAt() {
		return notifiedSemester1PaymentAt;
	}

	public void setNotifiedSemester1PaymentAt(Date notifiedSemester1PaymentAt) {
		this.notifiedSemester1PaymentAt = notifiedSemester1PaymentAt;
	}

	public Date getNotifiedSemester2PaymentAt() {
		return notifiedSemester2PaymentAt;
	}

	public void setNotifiedSemester2PaymentAt(Date notifiedSemester2PaymentAt) {
		this.notifiedSemester2PaymentAt = notifiedSemester2PaymentAt;
	}
	
	public Date getCourseParticipationInterruptedAt() {
		return courseParticipationInterruptedAt;
	}

	public void setCourseParticipationInterruptedAt(Date courseParticipationInterruptedAt) {
		this.courseParticipationInterruptedAt = courseParticipationInterruptedAt;
	}
	
	public IscusRole getIscusRole() {
		return iscusRole;
	}

	public void setIscusRole(IscusRole iscusRole) {
		this.iscusRole = iscusRole;
	}
	
	public String getIscusParticId() {
		return iscusParticId;
	}

	public void setIscusParticId(String iscusParticId) {
		this.iscusParticId = iscusParticId;
	}
	
	public String getIscusSystemId() {
		return iscusSystemId;
	}

	public void setIscusSystemId(String iscusSystemId) {
		this.iscusSystemId = iscusSystemId;
	}
	
	public String getInCourseInfo() {
		String ret = null;
		String DELIMITER = ", ";
		if (!CollectionUtils.isEmpty(this.courseList)) {
			StringBuilder sb = new StringBuilder();
			for (Course course : this.courseList) {
				sb.append(course.getName() + DELIMITER);
			}

			ret = sb.substring(0, sb.length() - DELIMITER.length());
		}

		return ret;
	}
	
	public Set<PaymentNotifSendState> getPaymentNotifSendState() {
		Set<PaymentNotifSendState> ret = new HashSet<>();
		if (this.notifiedSemester1PaymentAt == null) {
			ret.add(PaymentNotifSendState.NOT_SENT_FIRST_SEMESTER);
		}
		if (this.notifiedSemester2PaymentAt == null) {
			ret.add(PaymentNotifSendState.NOT_SENT_SECOND_SEMESTER);
		}
		
		if (this.notifiedSemester1PaymentAt == null && this.notifiedSemester2PaymentAt == null) {
			ret.add(PaymentNotifSendState.NO_NOTIFICATION);
		}
		
		if (this.notifiedSemester1PaymentAt != null) {
			ret.add(PaymentNotifSendState.SENT_FIRST_SEMESTER);			
		}
		
		if (this.notifiedSemester2PaymentAt != null) {
			ret.add(PaymentNotifSendState.SENT_SECOND_SEMESTER);
		}
		
		if (this.notifiedSemester1PaymentAt != null && this.notifiedSemester2PaymentAt != null) {
			ret.add(PaymentNotifSendState.BOTH);			
		}
		return ret;
	}

	@Override
	public String toString() {
		return "CourseParticipant [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", contact="
				+ contact + ", birthdate=" + birthdate + ", personalNo=" + personalNo + ", healthInsurance="
				+ healthInsurance + ", healthInfo=" + healthInfo + ", resultList=" + resultList + ", courseList="
				+ courseList + ", representativeUuid=" + representativeUuid + ", courseUuid=" + courseUuid
				+ ", courseName=" + courseName + ", coursePaymentVO=" + coursePaymentVO + ", varsymbolCore="
				+ varsymbolCore + ", notifiedSemester1PaymentAt=" + notifiedSemester1PaymentAt
				+ ", notifiedSemester2PaymentAt=" + notifiedSemester2PaymentAt + ", courseParticipationInterruptedAt="
				+ courseParticipationInterruptedAt + ", iscusRole=" + iscusRole + ", iscusParticId=" + iscusParticId
				+ ", iscusSystemId=" + iscusSystemId + ", lessonAttendance=" + lessonAttendance + "]";
	}
}