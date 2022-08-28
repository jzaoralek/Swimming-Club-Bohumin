package com.jzaoralek.scb.dataservice.service.impl;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.jzaoralek.scb.dataservice.service.ConfigurationService;
import com.jzaoralek.scb.dataservice.service.MailUtilService;

@Service("mailUtilService")
public class MailUtilServiceImpl implements MailUtilService {

	@Autowired
	private ConfigurationService configurationService;
	
	@Autowired
	private TemplateEngine emailTemplateEngine;
	
	@Override
	public String buildSignatureHtml() {
		Context ctx = new Context(Locale.getDefault());
		ctx.setVariable("orgName", configurationService.getOrgName());
		ctx.setVariable("contactPerson", configurationService.getOrgContactPerson());		
		ctx.setVariable("orgPhone", configurationService.getOrgPhone());
		ctx.setVariable("orgEmail", configurationService.getOrgEmail());
		
		return this.emailTemplateEngine.process("html/email-signature.html", ctx);
	}
}
