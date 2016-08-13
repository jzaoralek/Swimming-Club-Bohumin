package com.jzaoralek.scb.ui.common;

public enum WebPages {
	APPLICATION_LIST("/pages/secured/seznam-prihlasek.zul");

	private String url;

	WebPages(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
