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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.jzaoralek.scb.dataservice.common.DataServiceConstants;
import com.jzaoralek.scb.dataservice.dao.PaymentDao;
import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Mail;
import com.jzaoralek.scb.dataservice.domain.Payment;
import com.jzaoralek.scb.dataservice.domain.PaymentInstruction;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.MailService;
import com.jzaoralek.scb.dataservice.service.MailUtilService;
import com.jzaoralek.scb.dataservice.service.PaymentService;
import com.jzaoralek.scb.dataservice.service.QRCodeService;
import com.jzaoralek.scb.dataservice.utils.DateUtils;

@Service("paymentService")
public class PaymentServiceImpl extends BaseAbstractService implements PaymentService {

	private static final Logger LOG = LoggerFactory.getLogger(PaymentServiceImpl.class);

	@Autowired
	private PaymentDao paymentDao;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private CourseApplicationService courseApplicationService;
	
	@Autowired
	private TemplateEngine emailTemplateEngine;
	
	@Autowired
	private QRCodeService qrCodeService;
	
	@Autowired
	private MailUtilService mailUtilService;
	
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
	public void deleteByCourseAndParticipant(UUID courseUuid, UUID courseParticipantUuid) {
		paymentDao.deleteByCourseAndParticipant(courseUuid, courseParticipantUuid);
	}

	@Async
	@Override
	public void processPaymentInstruction(List<PaymentInstruction> paymentInstructionList
			, String yearFromTo
			, Date dueDate
			, String optionalText
			, boolean firstSemester
			, CourseType courseType
			, String clientDBCtx) { 
		if (CollectionUtils.isEmpty(paymentInstructionList)) {
			return;
		}
		// StringBuilder mailToUser = null;
		int semester = firstSemester ? 1 :2;
		int counter = 0;
		Context ctx = null;
		String htmlContent = null;
		Mail mailHtml = null;
		
		String signHtml = mailUtilService.buildSignatureHtml();
		
		for (PaymentInstruction paymentInstruction : paymentInstructionList) {
			if (!StringUtils.hasText(paymentInstruction.getCourseParticReprEmail())) {
				LOG.warn("submitCmd():: No course participant representative email for courseParticipant: " + paymentInstruction.getCourseParticName());
				continue;
			}
			
			ctx = new Context(Locale.getDefault());
			ctx.setVariable("courseType", courseType.name());
			ctx.setVariable("courseName", paymentInstruction.getCourseName());		
			ctx.setVariable("semester", paymentInstruction.getSemester());
			ctx.setVariable("year", yearFromTo);
			ctx.setVariable("particiName", paymentInstruction.getCourseParticName());
			
			ctx.setVariable("amount", paymentInstruction.getPriceSemester());
			ctx.setVariable("currency", paymentInstruction.getPriceSemester());
			ctx.setVariable("accountNo", paymentInstruction.getBankAccountNumber());
			ctx.setVariable("varSymbol", paymentInstruction.getVarsymbol());
			ctx.setVariable("dueDate", DateUtils.dateAsString(dueDate));
			ctx.setVariable("messageToRecipient", paymentInstruction.getCourseParticName());
			
			ctx.setVariable("QRCode", qrCodeService.getPaymentQRCodeUrl(paymentInstruction, dueDate));
			ctx.setVariable("optionalText", optionalText);
			ctx.setVariable("signature", signHtml);
			
			htmlContent = this.emailTemplateEngine.process("html/email-payment-instruction.html", ctx);
			
			String subject = null;
			if (courseType == CourseType.STANDARD) {
				subject = messageSource.getMessage("msg.ui.mail.paymentInstruction.subject.standard", new Object[] {paymentInstruction.getCourseName(), yearFromTo, paymentInstruction.getCourseParticName()}, Locale.getDefault());
			} else {
				subject = messageSource.getMessage("msg.ui.mail.paymentInstruction.subject.twoSemester", new Object[] {paymentInstruction.getCourseName(), semester, yearFromTo, paymentInstruction.getCourseParticName()}, Locale.getDefault());
			}
			
			mailHtml = Mail.ofHtml(paymentInstruction.getCourseParticReprEmail(), null, subject, htmlContent, null, false, null);
			
			mailService.sendMail(mailHtml, clientDBCtx);
			// odeslani na platby@sportologic.cz
			mailService.sendMail(new Mail(DataServiceConstants.PLATBY_EMAIL, null, messageSource.getMessage("msg.ui.mail.paymentInstruction.subject", new Object[] {paymentInstruction.getCourseName(), semester, yearFromTo, paymentInstruction.getCourseParticName()}, Locale.getDefault()), htmlContent, null, false), clientDBCtx);			
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
	
	@Async
	@Override
	public void sendPaymentConfirmation(String mailTo, Attachment attachment, String clientDBCtx) {
		String subject = "Potvrzení o zaplacení kurzu";
		String htmlContent = "";
		Mail mailHtml = Mail.ofHtml(mailTo, null, subject, htmlContent, Arrays.asList(attachment), false, null);
		
		mailService.sendMail(mailHtml, clientDBCtx);
	}

}