package com.jzaoralek.scb.ui.common.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.utils.WebUtils;

public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	@Override
    public void onLogoutSuccess(HttpServletRequest request,
    							HttpServletResponse response, 
    							Authentication authentication) throws IOException, ServletException {
        
		response.sendRedirect(WebUtils.getCustomerCtx(request) + WebPages.LOGIN_PAGE.getUrl());
        //super.onLogoutSuccess(request, response, authentication);
    }
}
