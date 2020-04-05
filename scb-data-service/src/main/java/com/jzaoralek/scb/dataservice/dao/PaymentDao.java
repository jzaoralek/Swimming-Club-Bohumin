package com.jzaoralek.scb.dataservice.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Payment;

public interface PaymentDao {
	
	Payment getByUuid(UUID uuid);
	List<Payment> getByCourseCourseParticipantUuid(UUID courseCourseParticipantUuid, UUID courseUuid, Date from, Date to);
	void insert(Payment course);
	void update(Payment course);
	void delete(Payment course);
	void deleteByCourseAndParticipant(UUID courseUuid, UUID courseParticipantUuid);
	Set<String> getAllBankTransIdPohybu();
	List<Payment> getBankPaymentByDateInterval(Date from, Date to);
}