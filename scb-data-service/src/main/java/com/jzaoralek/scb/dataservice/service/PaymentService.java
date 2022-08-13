package com.jzaoralek.scb.dataservice.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.domain.Payment;
import com.jzaoralek.scb.dataservice.domain.PaymentInstruction;

public interface PaymentService {

	Payment getByUuid(UUID uuid);
	List<Payment> getByCourseCourseParticipantUuid(UUID courseParticipantUuid, UUID courseUuid, Date from, Date to);
	Payment store(Payment payment);
	void deleteByCourseAndParticipant(UUID courseUuid, UUID courseParticipantUuid);
	void delete(Payment payment);
	void processPayments();
	void processPaymentInstruction(List<PaymentInstruction> paymentInstructionList
			, String yearFromTo
			, String lineSeparator
			, String paymentDeadline
			, String optionalText
			, String mailSignature
			, boolean firstSemester
			, CourseType courseType
			, String clientDBCtx);
}
