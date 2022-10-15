package com.jzaoralek.scb.ui.common;

public enum WebPages {
	APPLICATION_LIST("/pages/secured/ADMIN/seznam-prihlasek.zul"),
	APPLICATION_DETAIL_PUBLIC("/pages/public/prihlaska.zul"),
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
	USER_REPRESENTATIVE_DETAIL("/pages/secured/USER/uzivatel.zul"),
	PAYMENT_LIST("/pages/secured/TRAINER/seznam-plateb.zul"),
	PAYMENT_LIST_USER("/pages/secured/USER/seznam-plateb.zul"),
	PAYMENT_INSTRUCTION_WINDOW("/pages/secured/ADMIN/payment-instruction-window.zul"),
	COURSE_APPL_DYN_ATTR_CONFIG_WINDOW("/pages/secured/ADMIN/course-appl-dyn-attr-config-window.zul"),
	COURSE_APPL_FILE_CONFIG_WINDOW("/pages/secured/ADMIN/course-appl-file-config-window.zul"),
	MESSAGE("/pages/secured/ADMIN/message.zul"),
	CHANGE_USERNAME_ADMIN("/pages/secured/ADMIN/change-username.zul"),
	CHANGE_USERNAME_USER("/pages/secured/USER/change-username.zul"),
	LOGIN_PAGE("/pages/common/login.zul");
	
	private String url;

	WebPages(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
