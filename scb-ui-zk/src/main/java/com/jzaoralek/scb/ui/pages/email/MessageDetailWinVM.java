package com.jzaoralek.scb.ui.pages.email;

import org.zkoss.bind.annotation.Init;

import com.jzaoralek.scb.dataservice.domain.MailSend;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class MessageDetailWinVM extends BaseVM {

	private MailSend mailSendSelected;
	
	@Init
	public void init() {
		this.mailSendSelected = (MailSend) WebUtils.getArg(WebConstants.ITEM_PARAM);
	}

	public MailSend getMailSendSelected() {
		return mailSendSelected;
	}
}
