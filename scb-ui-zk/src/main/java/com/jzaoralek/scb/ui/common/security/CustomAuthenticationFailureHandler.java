package com.jzaoralek.scb.ui.common.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.utils.WebUtils;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		response.sendRedirect(WebUtils.getCustomerCtx(request) + WebPages.LOGIN_PAGE.getUrl() + "?loginFailure=true");
	}
}
