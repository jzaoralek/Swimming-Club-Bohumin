package com.jzaoralek.scb.dataservice.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Payment;

public interface PaymentService {

	Payment getByUuid(UUID uuid);
	List<Payment> getByCourseCourseParticipantUuid(UUID courseParticipantUuid, UUID courseUuid, Date from, Date to);
	Payment store(Payment payment);
	void delete(Payment payment);
}
