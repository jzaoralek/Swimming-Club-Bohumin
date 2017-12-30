package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.UUID;

import bank.fioclient.dto.Transaction;

public class Payment implements IdentEntity {

	public enum PaymentType {
		CASH,
		BANK_TRANS,
		DONATE,
		OTHER;
	}
	
	public enum PaymentProcessType {
		AUTOMATIC,
		PAIRED,
		MANUAL;
	}
	
	public Payment() {}
	
	public Payment(Transaction transaction, Course course, CourseParticipant courseParticipant, PaymentProcessType processType) {
		this.amount = transaction.getObjem().longValue();
		this.paymentDate = transaction.getDatumPohybu().getTime();
		this.description = transaction.getKomentar();
		this.type = PaymentType.BANK_TRANS;
		this.course = course;
		this.courseParticipant = courseParticipant;
		this.processType = processType;
		this.bankTransactionIdPohybu = transaction.getIdPohybu();
	}
	
	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private Long amount;
	private Date paymentDate;
	private String description;
	private PaymentType type;
	private Course course;
	private CourseParticipant courseParticipant;
	private PaymentProcessType processType;
	private Long bankTransactionIdPohybu;
	
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
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public PaymentType getType() {
		return type;
	}
	public void setType(PaymentType type) {
		this.type = type;
	}
	public PaymentProcessType getProcessType() {
		return processType;
	}
	public void setProcessType(PaymentProcessType processType) {
		this.processType = processType;
	}
	public Course getCourse() {
		return course;
	}
	public void setCourse(Course course) {
		this.course = course;
	}
	public CourseParticipant getCourseParticipant() {
		return courseParticipant;
	}
	public void setCourseParticipant(CourseParticipant courseParticipant) {
		this.courseParticipant = courseParticipant;
	}
	public Long getBankTransactionIdPohybu() {
		return bankTransactionIdPohybu;
	}
	public void setBankTransactionIdPohybu(Long bankTransactionIdPohybu) {
		this.bankTransactionIdPohybu = bankTransactionIdPohybu;
	}
	
	@Override
	public String toString() {
		return "Payment [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", amount=" + amount
				+ ", paymentDate=" + paymentDate + ", description=" + description + ", type=" + type + ", course="
				+ course + ", courseParticipant=" + courseParticipant + ", processType=" + processType
				+ ", bankTransactionIdPohybu=" + bankTransactionIdPohybu + "]";
	}
}
