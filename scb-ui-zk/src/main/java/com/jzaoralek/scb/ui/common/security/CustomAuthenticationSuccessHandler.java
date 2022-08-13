package com.jzaoralek.scb.ui.common.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jzaoralek.scb.ui.common.utils.WebUtils;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    
    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
    	
        String targetUrl = canUseCachedRequestUrl(request, response)
    			? requestCache.getRequest(request, response).getRedirectUrl()
    			: determineTargetUrl(authentication, request);

        if (response.isCommitted()) {
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    /*
     * This method extracts the roles of currently logged-in user and returns
     * appropriate URL according to his/her role.
     */
    protected String determineTargetUrl(Authentication authentication, HttpServletRequest request) {
        String url = "";

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<String> roles = new ArrayList<>();

        for (GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }

        if (isDba(roles)) {
            url = "/db";
        } else if (isAdmin(roles)) {
            url = "/pages/secured/TRAINER/seznam-kurzu.zul";
        } else if (isTrainer(roles)) {
            url = "/pages/secured/TRAINER/seznam-kurzu.zul";
        } else if (isUser(roles)) {
            url = "/pages/secured/USER/seznam-ucastniku.zul";
        } else {
            url = "/accessDenied";
        }

        return WebUtils.buildCustCtxUri(url, request);
    }
    
    private boolean canUseCachedRequestUrl(HttpServletRequest request, HttpServletResponse response) {
    	SavedRequest originalRequest = requestCache.getRequest(request, response);
    	logger.debug("originalRequest =" + originalRequest);
    	if (!(originalRequest instanceof DefaultSavedRequest) 
    			|| !StringUtils.hasText(originalRequest.getRedirectUrl())) {
    		return false;
    	}
    	
    	return true;
    }

    private static boolean isUser(List<String> roles) {
        if (roles.contains("ROLE_USER")) {
            return true;
        }
        return false;
    }

    private static boolean isTrainer(List<String> roles) {
        if (roles.contains("ROLE_TRAINER")) {
            return true;
        }
        return false;
    }
    
    private static boolean isAdmin(List<String> roles) {
        if (roles.contains("ROLE_ADMIN")) {
            return true;
        }
        return false;
    }

    private static boolean isDba(List<String> roles) {
        if (roles.contains("ROLE_DBA")) {
            return true;
        }
        return false;
    }

    @Override
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    @Override
	protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }
}