package com.jzaoralek.scb.dataservice.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.jzaoralek.scb.dataservice.common.DataServiceConstants;
import com.jzaoralek.scb.dataservice.dao.PaymentDao;
import com.jzaoralek.scb.dataservice.domain.Mail;
import com.jzaoralek.scb.dataservice.domain.Payment;
import com.jzaoralek.scb.dataservice.domain.PaymentInstruction;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.service.MailService;
import com.jzaoralek.scb.dataservice.service.PaymentService;

@Service("paymentService")
public class PaymentServiceImpl extends BaseAbstractService implements PaymentService {

	private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

	@Autowired
	private PaymentDao paymentDao;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private CourseApplicationService courseApplicationService;
	
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

	@Async
	@Override
	public void processPaymentInstruction(List<PaymentInstruction> paymentInstructionList
			, String yearFromTo
			, String lineSeparator
			, String paymentDeadline
			, String optionalText
			, String mailSignature
			, boolean firstSemester
			, CourseType courseType) { 
		if (CollectionUtils.isEmpty(paymentInstructionList)) {
			return;
		}
		StringBuilder mailToUser = null;
		int semester = firstSemester ? 1 :2;
		int counter = 0;
		for (PaymentInstruction paymentInstruction : paymentInstructionList) {
			if (!StringUtils.hasText(paymentInstruction.getCourseParticReprEmail())) {
				LOG.warn("submitCmd():: No course participant representative email for courseParticipant: " + paymentInstruction.getCourseParticName());
				continue;
			}
			mailToUser = new StringBuilder();
			mailToUser.append(messageSource.getMessage("msg.ui.mail.paymentInstruction.text0", null, Locale.getDefault()));
			mailToUser.append(lineSeparator);
			mailToUser.append(lineSeparator);
			if (courseType == CourseType.STANDARD) {
				mailToUser.append(messageSource.getMessage("msg.ui.mail.paymentInstruction.text1.standard", new Object[] {paymentInstruction.getCourseName(), yearFromTo, paymentInstruction.getCourseParticName()}, Locale.getDefault()));
			} else {
				mailToUser.append(messageSource.getMessage("msg.ui.mail.paymentInstruction.text1.twoSemester", new Object[] {paymentInstruction.getCourseName(), paymentInstruction.getSemester(), yearFromTo, paymentInstruction.getCourseParticName()}, Locale.getDefault()));				
			}
			mailToUser.append(lineSeparator);
			mailToUser.append(lineSeparator);

			// cislo uctu
			mailToUser.append(messageSource.getMessage("txt.ui.common.AccountNo", null, Locale.getDefault()));
			mailToUser.append(": ");
			mailToUser.append(paymentInstruction.getBankAccountNumber());
			mailToUser.append(lineSeparator);
			
			// castka
			mailToUser.append(messageSource.getMessage("txt.ui.common.Amount", null, Locale.getDefault()));
			mailToUser.append(": ");
			mailToUser.append(paymentInstruction.getPriceSemester());
			mailToUser.append(" ");
			mailToUser.append(messageSource.getMessage("txt.ui.common.CZK", null, Locale.getDefault()));
			mailToUser.append(lineSeparator);
			
			// variabilni symbol
			mailToUser.append(messageSource.getMessage("txt.ui.common.VarSymbol", null, Locale.getDefault()));
			mailToUser.append(": ");
			mailToUser.append(paymentInstruction.getVarsymbol());
			mailToUser.append(lineSeparator);
			
			// zprava pro prijemce
			mailToUser.append(messageSource.getMessage("txt.ui.common.MessageToReceipent", new Object[] {paymentDeadline}, Locale.getDefault()));
			mailToUser.append(": ");
			mailToUser.append(paymentInstruction.getCourseParticName());
			mailToUser.append(lineSeparator);
			mailToUser.append(lineSeparator);
			
			// termin uhrazeni
			if (StringUtils.hasText(paymentDeadline)) {
				mailToUser.append(messageSource.getMessage("msg.ui.mail.paymentInstruction.text2", new Object[] {paymentDeadline}, Locale.getDefault()));
				mailToUser.append(lineSeparator);
				mailToUser.append(lineSeparator);
			}
			
			// volitelny text
			if (StringUtils.hasText(optionalText)) {
				mailToUser.append(optionalText);
				mailToUser.append(lineSeparator);
				mailToUser.append(lineSeparator);
			}
			
			// pokud jiz bylo uhrazeno, berte jako bezpredmetne
			mailToUser.append(messageSource.getMessage("msg.ui.mail.paymentInstruction.text4", new Object[] {paymentDeadline}, Locale.getDefault()));
			mailToUser.append(lineSeparator);
			mailToUser.append(lineSeparator);
			
			// podpis
			mailToUser.append(mailSignature);
			
			String subject = null;
			if (courseType == CourseType.STANDARD) {
				subject = messageSource.getMessage("msg.ui.mail.paymentInstruction.subject.standard", new Object[] {paymentInstruction.getCourseName(), yearFromTo, paymentInstruction.getCourseParticName()}, Locale.getDefault());
			} else {
				subject = messageSource.getMessage("msg.ui.mail.paymentInstruction.subject.twoSemester", new Object[] {paymentInstruction.getCourseName(), semester, yearFromTo, paymentInstruction.getCourseParticName()}, Locale.getDefault());
			}
			
			mailService.sendMail(new Mail(paymentInstruction.getCourseParticReprEmail(), null, subject, mailToUser.toString(), null));
			// odeslani na platby@sportologic.cz
			mailService.sendMail(new Mail(DataServiceConstants.PLATBY_EMAIL, null, messageSource.getMessage("msg.ui.mail.paymentInstruction.subject", new Object[] {paymentInstruction.getCourseName(), semester, yearFromTo, paymentInstruction.getCourseParticName()}, Locale.getDefault()), mailToUser.toString(), null));			
			// aktualizace odeslani notifikace v course_course_participant
			courseApplicationService.updateNotifiedPayment(Arrays.asList(paymentInstruction.getCourseParticipantUuid()), firstSemester);
			
			counter++;
			// sleeping after batch
			if (counter%DataServiceConstants.MAIL_SENDER_BATCH_SIZE == 0) {
				try {
					Thread.sleep(DataServiceConstants.MAIL_SENDER_PAUSE_BETWEEN_BATCH);
				} catch (InterruptedException e) {
					LOG.error("InterruptedException caught", e);
					throw new RuntimeException(e);
				}				
			}
		}
	}
}