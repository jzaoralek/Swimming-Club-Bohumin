package com.jzaoralek.scb.ui.common;

public enum WebPages {
	APPLICATION_LIST("/pages/secured/ADMIN/seznam-prihlasek.zul"),
	APPLICATION_DETAIL("/pages/secured/ADMIN/prihlaska.zul"),
	COURSE_LIST("/pages/secured/TRAINER/seznam-kurzu.zul"),
	COURSE_DETAIL("/pages/secured/ADMIN/kurz.zul"),
	COURSE_LESSONS("/pages/secured/TRAINER/kurz-vyuka.zul"),
	LEARNING_LESSON("/pages/secured/TRAINER/learning-lesson.zul"),
	PARTICIPANT_LIST("/pages/secured/TRAINER/seznam-ucastniku.zul"),
	PARTICIPANT_DETAIL("/pages/secured/TRAINER/ucastnik.zul"),
	USER_PARTICIPANT_LIST("/pages/secured/USER/seznam-ucastniku.zul"),
	USER_APPLICATION_DETAIL("/pages/secured/USER/prihlaska.zul"),
	USER_PARTICIPANT_DETAIL("/pages/secured/USER/ucastnik.zul"),
	USER_LIST("/pages/secured/ADMIN/seznam-uzivatelu.zul"),
	USER_LOG_TO_COURSE("/pages/secured/USER/prihlaseni-na-kurz.zul"),
	USER_DETAIL("/pages/secured/ADMIN/uzivatel.zul"),
	PAYMENT_LIST("/pages/secured/TRAINER/seznam-plateb.zul"),
	PAYMENT_INSTRUCTION_WINDOW("/pages/secured/ADMIN/payment-instruction-window.zul"),
	MESSAGE("/pages/secured/ADMIN/message.zul"),
	
	LOGIN_PAGE("/pages/common/login.zul");
	
	private String url;

	WebPages(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
