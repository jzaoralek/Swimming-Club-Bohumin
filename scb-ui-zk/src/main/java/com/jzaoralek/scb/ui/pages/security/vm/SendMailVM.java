package com.jzaoralek.scb.ui.pages.security.vm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.service.CourseApplicationFileConfigService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class SendMailVM extends BaseVM {

	@WireVariable
	private CourseApplicationFileConfigService courseApplicationFileConfigService;
	
	@Init
	public void init() {
		super.init();
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
				, attachmentList);        
	}
	
	@Command
	public void sendMail2Cmd() {
		mailService.sendMailBatch(null);
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
}
