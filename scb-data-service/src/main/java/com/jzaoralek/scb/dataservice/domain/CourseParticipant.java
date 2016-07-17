package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CourseParticipant implements IdentEntity {

	private UUID uuid;
	private Contact contact;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "CET")
	private Date birthdate;
	private String  personalNo;
	private List<Result> resultList;

	@Override
	public UUID getUuid() {
		return uuid;
	}
	@Override
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
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

	@Override
	public String toString() {
		return "CourseParticipant [uuid=" + uuid + ", contact=" + contact + ", birthdate=" + birthdate + ", personalNo="
				+ personalNo + ", resultList=" + resultList + "]";
	}
}