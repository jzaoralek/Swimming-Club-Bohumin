package com.jzaoralek.scb.dataservice.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.common.DataServiceConstants;
import com.jzaoralek.scb.dataservice.dao.PaymentDao;
import com.jzaoralek.scb.dataservice.domain.Payment;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.PaymentService;

@Service("paymentService")
public class PaymentServiceImpl extends BaseAbstractService implements PaymentService {

	@Autowired
	private PaymentDao paymentDao;
	
	@Override
	public List<Payment> getByCourseCourseParticipantUuid(UUID courseCourseParticipantUuid, Date from, Date to) {
		Date dateFrom = from;
		Date dateTo = to;
		
		if (dateFrom == null) {
			dateFrom = DataServiceConstants.DEFAULT_DATE_FROM;
		}
		if (dateTo == null) {
			dateTo = DataServiceConstants.DEFAULT_DATE_TO;
		}
		
		return paymentDao.getByCourseCourseParticipantUuid(courseCourseParticipantUuid, dateFrom, dateTo);
	}

	@Override
	public void insert(Payment payment) {
		fillIdentEntity(payment);
		paymentDao.insert(payment);
		
	}

	@Override
	public void update(Payment payment) {
		fillIdentEntity(payment);
		paymentDao.update(payment);
		
	}

	@Override
	public void delete(Payment payment) {
		paymentDao.delete(payment);
	}

	@Override
	public Payment getByUuid(UUID uuid) {
		return paymentDao.getByUuid(uuid);
	}
}