package com.jzaoralek.scb.ui.pages.security.vm;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Filedownload;

import com.jzaoralek.scb.dataservice.datasource.ClientDatabaseContextHolder;
import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.domain.PaymentInstruction;
import com.jzaoralek.scb.dataservice.service.QRCodeService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class SendMailVM extends BaseVM {

	@WireVariable
	private QRCodeService qrCodeService;
	
	@Command
	public void sendPaymentInstructCmd() {
		Date dueDate = buildDueDate();
		CourseType courseType = CourseType.TWO_SEMESTER;
		String yearFromTo = "2022/2023";
		String optionalText = "Volitelný text instrukce k platbě.";
		String signature = "Adrian Kuder";
		
		/*
		final Context ctx = new Context(new Locale("cs","CZ"));
		ctx.setVariable("courseType", courseType.name());
		ctx.setVariable("courseName", instruct.getCourseName());		
		ctx.setVariable("semester", instruct.getSemester());
		ctx.setVariable("year", yearFromTo);
		ctx.setVariable("particiName", instruct.getCourseParticName());
		
		ctx.setVariable("amount", instruct.getPriceSemester());
		ctx.setVariable("currency", instruct.getPriceSemester());
		ctx.setVariable("accountNo", instruct.getBankAccountNumber());
		ctx.setVariable("varSymbol", instruct.getVarsymbol());
		ctx.setVariable("dueDate", dueDate);
		ctx.setVariable("messageToRecipient", instruct.getCourseParticName());
		
		ctx.setVariable("QRCode", qrCodeService.getPaymentQRCodeUrl(instruct, dueDate));
		ctx.setVariable("optionalText", optionalText);
		ctx.setVariable("signature", signature);
		*/
		
		paymentService.processPaymentInstruction(Arrays.asList(buildPaymentInstruction()), 
											yearFromTo, 
											dueDate, 
											optionalText, 
											signature, 
											true, 
											courseType, 
											ClientDatabaseContextHolder.getClientDatabase());
		
		/*
		final String htmlContent = this.emailTemplateEngine.process("html/email-payment-instruction.html", ctx);
		Mail mail = Mail.ofHtml("jakub.zaoralek@gmail.com", null, "Instrukce k platbě", htmlContent, null, false, null);
		mailService.sendMail(mail, ClientDatabaseContextHolder.getClientDatabase());
		*/
		
		/*
		mailService.sendMail("jakub.zaoralek@gmail.com"
				, null
				, "Instrukce k platbě"
				, htmlContent
				, null
				, true
				, false
				, null
				, ClientDatabaseContextHolder.getClientDatabase());
		*/
	}
	
	@Command
	public void sendMail2Cmd() {
		mailService.sendMailBatch(null, ClientDatabaseContextHolder.getClientDatabase());
		WebUtils.showNotificationInfo("Odeslani dokonceno.");
	}
	
	@Command
	public void downloadCmd() {
		Attachment attachment = courseApplicationFileConfigService.getFileByUuid(UUID.fromString("fd33a4d4-7e99-11e6-ae22-56b6b6499628"));
		if (attachment != null) {
			Executions.getCurrent().getSession().setAttribute(WebConstants.ATTACHMENT_PARAM, attachment);
			WebUtils.downloadAttachment(attachment);			
		}
	}
	
	@Command
	public void getQRCodeCmd() {
			byte[] qrCodeBytes = qrCodeService.getPaymentQRCode(buildPaymentInstruction(), buildDueDate());
			/*
			byte[] qrCodeBytes = restExecutor.execute("/paylibo/generator/czech/image?accountNumber=1472527163&bankCode=0800&amount=100.00&currency=CZK&vs=333",
													HttpMethod.GET, 
													null, 
													byte[].class);*/
			Filedownload.save(qrCodeBytes, "image/png", "QRPlatba" + System.currentTimeMillis() + ".png");
	}
	
	private PaymentInstruction buildPaymentInstruction() {
		return new PaymentInstruction("Dustin Henderson"
				, null
				, "Kurz Stranger Things"
				, 1000
				, 1
				, "34567"
				, "670100-2213791191/6210" // 1472527163/0800 
				, UUID.randomUUID()
				, CourseType.STANDARD);
	}
	
	private Date buildDueDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date()); 
		cal.add(Calendar.DATE, 1);
		
		return cal.getTime();
	}
}
