package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CourseParticipant implements IdentEntity {

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

	public String inCourseInfo() {
		String ret = null;
		if (!CollectionUtils.isEmpty(this.courseList)) {
			StringBuilder sb = new StringBuilder();
			for (Course course : this.courseList) {
				sb.append(course.getName() + "\n");
			}

			ret = sb.toString();
		}

		return ret;
	}

	@Override
	public String toString() {
		return "CourseParticipant [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", contact="
				+ contact + ", birthdate=" + birthdate + ", personalNo=" + personalNo + ", healthInsurance="
				+ healthInsurance + ", healthInfo=" + healthInfo + ", resultList=" + resultList + ", courseList="
				+ courseList + "]";
	}
}