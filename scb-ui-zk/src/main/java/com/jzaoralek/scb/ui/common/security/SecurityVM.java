package com.jzaoralek.scb.ui.common.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.zk.ui.Executions;

public class SecurityVM {

//	private static final Logger logger = Logger.getLogger(SecurityVM.class);

	private String username;
	private String password;

	private Boolean loginFailure;

	@Init
	public void init(@QueryParam("loginFailure") String loginFailure) {
		// zobrazeni alertu o neuspesnem prihlaseni
		this.loginFailure = StringUtils.hasText(loginFailure);
	}

	@Command
    public void loginCmd() {
//		logger.info("Login user with username: "+this.username);
		//FIXME: toto se bude muset udelat bud cele jinak, nebo jako POST, takto je heslo
		//v adrese a tedy napr. v access logu
    	Executions.sendRedirect("/pages/loginRedirect.jsp?username="+this.username+"&password="+this.password);
    }

    @Command
    public void logoutCmd() {
    	Executions.sendRedirect("/logout");
    }

    public String getLoggedUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = null;
		if (principal instanceof UserDetails) {
			username = ((UserDetails)principal).getUsername();
		} else {
			username = principal.toString();
		}

		return username;
	}

    public String getLoggedUserCompleteName() {
//		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		if (principal == null) {
//			return null;
//		}
//		if (principal instanceof SdatUser) {
//			return ((SdatUser)principal).getCompleteName();
//		} else {
//			return principal.toString();
//		}
//
//		return principal.toString();

    	return null;
	}

    public Boolean isUserLogged() {
//    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    	return (principal instanceof UserDetails);

    	return false;
    }

    public Boolean getLoginFailure() {
		return this.loginFailure;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}