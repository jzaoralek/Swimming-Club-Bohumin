package com.jzaoralek.scb.ui.pages.security.vm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpMethod;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Filedownload;

import com.jzaoralek.scb.dataservice.datasource.ClientDatabaseContextHolder;
import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.domain.PaymentInstruction;
import com.jzaoralek.scb.dataservice.service.CourseApplicationFileConfigService;
import com.jzaoralek.scb.dataservice.service.QRCodeService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

import webtools.rest.RestExecutor;
import webtools.rest.exception.RestException;

public class SendMailVM extends BaseVM {

	@WireVariable
	private CourseApplicationFileConfigService courseApplicationFileConfigService;
	
	@WireVariable
	private QRCodeService qrCodeService;
	
	private RestExecutor restExecutor;
	
	@Init
	public void init() {
		super.init();
		restExecutor = new RestExecutor("https://api.paylibo.com", null);
	}
	
	@Command
	public void sendMailCmd() {
		List<com.jzaoralek.scb.dataservice.domain.Attachment> attachmentList = new ArrayList<>();
		byte[] gdprByteArray = WebUtils.getFileAsByteArray("/resources/docs/gdpr.docx");
		byte[] lekarskaProhlidkaByteArray = WebUtils.getFileAsByteArray("/resources/docs/lekarska_prohlidka.docx");
		if (gdprByteArray != null) {
			attachmentList.add(new com.jzaoralek.scb.dataservice.domain.Attachment(gdprByteArray,"gdpr-souhlas.docx"));
		}
		if (lekarskaProhlidkaByteArray != null) {
			attachmentList.add(new com.jzaoralek.scb.dataservice.domain.Attachment(lekarskaProhlidkaByteArray,"lekarska-prohlidka.docx"));
		}
		
		mailService.sendMail("jakub.zaoralek@gmail.com"
				, null
				, Labels.getLabel("txt.ui.menu.application")
				, "text mailu"
				, attachmentList
				, false
				, false
				, null
				, ClientDatabaseContextHolder.getClientDatabase());        
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
			PaymentInstruction paymentInstr = new PaymentInstruction("Dustin Henderson"
																		, null
																		, "Kurz Stranger Things"
																		, 1000
																		, 1
																		, "34567"
																		, "670100-2213791191/6210" // 1472527163/0800 
																		, UUID.randomUUID()
																		, CourseType.STANDARD);
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date()); 
			cal.add(Calendar.DATE, 1);
			
			byte[] qrCodeBytes = qrCodeService.getPaymentQRCode(paymentInstr, cal.getTime());
			/*
			byte[] qrCodeBytes = restExecutor.execute("/paylibo/generator/czech/image?accountNumber=1472527163&bankCode=0800&amount=100.00&currency=CZK&vs=333",
													HttpMethod.GET, 
													null, 
													byte[].class);*/
			Filedownload.save(qrCodeBytes, "image/png", "QRPlatba" + System.currentTimeMillis() + ".png");
	}
}
