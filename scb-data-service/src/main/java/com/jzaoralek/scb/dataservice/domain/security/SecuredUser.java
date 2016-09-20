package com.jzaoralek.scb.dataservice.domain.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.jzaoralek.scb.dataservice.domain.ScbUser;

public class SecuredUser extends User {

	private static final long serialVersionUID = 1;

	private ScbUser scbUser;
	private Set<SecuredUserProfile> userProfiles = new HashSet<SecuredUserProfile>();

	public SecuredUser(String username,
			String password,
			boolean enabled,
			boolean accountNonExpired,
			boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}

	public ScbUser getScbUser() {
		return scbUser;
	}

	public void setScbUser(ScbUser scbUser) {
		this.scbUser = scbUser;
	}

	public Set<SecuredUserProfile> getUserProfiles() {
		return userProfiles;
	}

	public void setUserProfiles(Set<SecuredUserProfile> userProfiles) {
		this.userProfiles = userProfiles;
	}

	public String getCompleteName() {
		return (this.scbUser != null && this.scbUser.getContact() != null) ? this.scbUser.getContact().getFirstname() + " " + this.scbUser.getContact().getSurname() : "";
	}

	@Override
	public String toString() {
		return "SecuredUser [scbUser=" + scbUser + ", userProfiles=" + userProfiles + "]";
	}
}