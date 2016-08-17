package com.jzaoralek.scb.dataservice.domain.security;

import org.springframework.security.core.GrantedAuthority;

public enum SecuredUserProfileType implements GrantedAuthority {
	USER("USER"),
	ADMIN("ADMIN");

	String userProfileType;

	@Override
	public String getAuthority() {
		return "ROLE_" + this.name();
	}

	private SecuredUserProfileType(String userProfileType){
		this.userProfileType = userProfileType;
	}

	public String getUserProfileType(){
		return userProfileType;
	}
}
