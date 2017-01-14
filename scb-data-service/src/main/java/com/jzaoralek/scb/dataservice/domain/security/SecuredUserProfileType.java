package com.jzaoralek.scb.dataservice.domain.security;

import org.springframework.security.core.GrantedAuthority;

import com.jzaoralek.scb.dataservice.domain.ScbUserRole;

public enum SecuredUserProfileType implements GrantedAuthority {
	USER("USER"),
	ADMIN("ADMIN"),
	TRAINER("TRAINER");

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

	public static SecuredUserProfileType fromScbUserRole(ScbUserRole role) {
		if (role == null) {
			throw new IllegalArgumentException("role is null");
		}

		switch (role) {
			case ADMIN: return SecuredUserProfileType.ADMIN;
			case USER: return SecuredUserProfileType.USER;
			case TRAINER: return SecuredUserProfileType.TRAINER;
			default: throw new IllegalArgumentException("Unsupported ScbUserRole: " + role);
		}
	}
}
