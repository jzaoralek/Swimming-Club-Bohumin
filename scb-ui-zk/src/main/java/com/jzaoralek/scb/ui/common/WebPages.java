package com.jzaoralek.scb.ui.common;

public enum WebPages {
	APPLICATION_LIST("/pages/secured/seznam-prihlasek.zul"),
	APPLICATION_DETAIL("/pages/secured/prihlaska-do-klubu.zul"),
	COURSE_LIST("/pages/secured/seznam-kurzu.zul"),
	PARTICIPANT_LIST("/pages/secured/seznam-ucastniku.zul"),
	PARTICIPANT_DETAIL("/pages/secured/ucastnik.zul");

	private String url;

	WebPages(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
