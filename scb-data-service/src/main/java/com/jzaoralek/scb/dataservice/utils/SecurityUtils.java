package com.jzaoralek.scb.dataservice.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.security.SecuredUser;
import com.jzaoralek.scb.dataservice.domain.security.SecuredUserProfileType;

public final class SecurityUtils {

//	private static Logger LOG = Logger.getLogger(SecurityUtils.class);

	private SecurityUtils() {}

	public static ScbUser getLoggedUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal == null) {
			return null;
		}
		if (principal instanceof ScbUser) {
			return (ScbUser)principal;
		}

		return null;
	}

	public static String getLoggedUserUsername() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = null;
		if (principal instanceof UserDetails) {
			username = ((UserDetails)principal).getUsername();
		} else {
			username = principal.toString();
		}

		return username;
	}

    public static String getLoggedUserCompleteName() {
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal == null) {
			return null;
		}
		if (principal instanceof SecuredUser) {
			return ((SecuredUser)principal).getCompleteName();
		} else {
			return principal.toString();
		}
	}

	public static Boolean isUserLogged() {
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return (principal instanceof UserDetails);
    }

	public static Boolean userInRole(String role) {
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal == null) {
			return false;
		}
		SecuredUser user = null;
		if (principal instanceof SecuredUser) {
			user = (SecuredUser)principal;
		}

		if ((user == null) || (user.getAuthorities() == null)) {
			return false;
	    }

		GrantedAuthority authority = SecuredUserProfileType.valueOf(role);
		return user.getAuthorities().contains(authority);
    }
}
