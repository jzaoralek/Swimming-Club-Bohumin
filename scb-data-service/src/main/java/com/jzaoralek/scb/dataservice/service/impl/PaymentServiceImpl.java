package com.jzaoralek.scb.dataservice.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.common.DataServiceConstants;
import com.jzaoralek.scb.dataservice.dao.PaymentDao;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.Payment;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.PaymentService;

@Service("paymentService")
public class PaymentServiceImpl extends BaseAbstractService implements PaymentService {

	private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

	@Autowired
	private PaymentDao paymentDao;
	
	@Override
	public List<Payment> getByCourseCourseParticipantUuid(UUID courseParticipantUuid, UUID courseUuid, Date from, Date to) {
		Date dateFrom = from;
		Date dateTo = to;
		
		if (dateFrom == null) {
			dateFrom = DataServiceConstants.DEFAULT_DATE_FROM;
		}
		if (dateTo == null) {
			dateTo = DataServiceConstants.DEFAULT_DATE_TO;
		}
		
		return paymentDao.getByCourseCourseParticipantUuid(courseParticipantUuid, courseUuid, dateFrom, dateTo);
	}

	@Override
	public void delete(Payment payment) {
		paymentDao.delete(payment);
	}

	@Override
	public Payment getByUuid(UUID uuid) {
		return paymentDao.getByUuid(uuid);
	}
	
	@Override
	public Payment store(Payment payment) {
		if (payment == null) {
			throw new IllegalArgumentException("payment is null");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Storing payment: " + payment);
		}

		boolean insert = payment.getUuid() == null;
		fillIdentEntity(payment);
		if (insert) {
			paymentDao.insert(payment);
		} else {
			paymentDao.update(payment);
		}

		return payment;
	}

	@Override
	public void processPayments() {
		System.out.println("Method executed at every 5 seconds. Current time is :: "+ new Date());	
	}
}