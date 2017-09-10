package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Payment;

public interface PaymentDao {
	
	Payment getByUuid(UUID uuid);
	List<Payment> getByCourseCourseParticipantUuid(UUID courseCourseParticipantUuid);
	void insert(Payment course);
	void update(Payment course);
	void delete(Payment course);
}