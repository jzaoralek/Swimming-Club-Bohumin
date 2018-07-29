package com.jzaoralek.scb.ui.pages.security.vm;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.resource.Labels;

import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class SendMailVM extends BaseVM{

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
				, Labels.getLabel("txt.ui.menu.application")
				, "text mailu"
				, attachmentList);        
	}
}
