package com.jzaoralek.scb.dataservice.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.Payment;
import com.jzaoralek.scb.dataservice.domain.Payment.PaymentProcessType;
import com.jzaoralek.scb.dataservice.domain.PaymentInstruction;

public interface PaymentService {

	Payment getByUuid(UUID uuid);
	List<Payment> getByCourseCourseParticipantUuid(UUID courseParticipantUuid, UUID courseUuid, Date from, Date to);
	Payment store(Payment payment);
	void deleteByCourseAndParticipant(UUID courseUuid, UUID courseParticipantUuid, PaymentProcessType processType);
	void updateCourseByCourseAndParticipant(UUID courseUuidOrig, 
			UUID courseUuidDest, 
			UUID courseParticipantUuid, 
			PaymentProcessType processType);
	void delete(Payment payment);
	void processPaymentInstruction(List<PaymentInstruction> paymentInstructionList
							, String yearFromTo
							, Date dueDate
							, String optionalText
							, boolean firstSemester
							, CourseType courseType
							, String clientDBCtx);
	void sendPaymentConfirmation(String mailTo
							, Course course
							, Boolean firstSemester
							, CourseParticipant coursePartic
							, Attachment attachment
							, String clientDBCtx);
}
