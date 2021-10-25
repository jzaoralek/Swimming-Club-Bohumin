package com.jzaoralek.scb.ui.common.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;

/**
 * Filter to check customer url, add datasource and redirect to url withou customer part.
 *
 */

// TODO: OneApp
// Problem: pokud je po otevreni prohlizece, tzn. nova session zadana url s pages, např. http://localhost:7002/pages/common/login.zul nezkontroluje se cookie context
// Dořešit ukladani /pages do cookie.
public class DynDatasourceContextFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(DynDatasourceContextFilter.class);
	
	private static final String PAGES_PREFIX = "/pages";
	private static final String SLASH = "/";
	private static final String CUST_URI_ATTR = "customerUri";
	
	@Autowired
	private ScbUserService scbUserService;
	
	private List<String> excludedUrls;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// bean injection 
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		
		// exclude urls
		String excludePattern = filterConfig.getInitParameter("excludedUrls");
	    excludedUrls = Arrays.asList(excludePattern.split(","));
	}

	@Override
	public void doFilter(ServletRequest request, 
						ServletResponse response, 
						FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest)request;
		String servletPath = req.getServletPath();
		String[] servletPathPartArr = servletPath.split(SLASH);
		String customerUri = null;
		String servletPathFirstPart = null;
		
		if (servletPathPartArr.length > 0) {
			customerUri = servletPathPartArr[1];
			servletPathFirstPart = SLASH + customerUri;
			
			if(isExcludedUrl(servletPathFirstPart)) {
				// url is excluded (e.g. /zkau, /resources etc.) no need to check customer url part
				chain.doFilter(request, response);
				return;
			}			
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Customer URI entered in servletPath: {}", customerUri);
		}
		
		// ziskat customerUri ze session
		String customerUriSessionOrCookie = (String)WebUtils.getSessAtribute(CUST_URI_ATTR, req);
		// pokud customerUri není v session získat z cookies
		if (!StringUtils.hasText(customerUriSessionOrCookie)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Customer URI stored in session is NULL, trying to get from cookie.");
			}
			Optional<String> cookieOpt = WebUtils.readCookie(CUST_URI_ATTR, req);
			if (cookieOpt.isPresent()) {
				customerUriSessionOrCookie = cookieOpt.get();
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("Customer URI from cookie: {}", customerUriSessionOrCookie);
			}
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Customer URI stored in session|cookie: {}", customerUriSessionOrCookie);
		}
		
		HttpServletResponse resp = (HttpServletResponse)response;
		if (servletPath.startsWith(PAGES_PREFIX)) {
			if (!StringUtils.hasText(customerUriSessionOrCookie)) {
				// enter direct URL e.g. /pages/common/login.zul without customer URI and previous value in cookies -> 403
				LOG.warn("Entered direct URL with servlet path: {} without customerUri value in cookies, redirect to 403 Forbidden.", servletPath);
				resp.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;				
			} else {
				// url starts with /pages, in cookie customer URI, continue
				chain.doFilter(request, response);
				return;
			}
		}
		
		// customerUri je null a není v sesion ani cookies -> 403
		if (!StringUtils.hasText(customerUri) && !StringUtils.hasText(customerUriSessionOrCookie)) {
			// TODO: OneApp - nahradit za redirect na stranku kde si vybere customera
			LOG.warn("Customer URI entered in servletPath and stored in session|cookie are NULL, redirect to 403 Forbidden.");
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		
//		if (servletPathPartArr.length == 0) {
//			chain.doFilter(request, response);
//			return;
//		}
		
		if (SLASH.equals(servletPath)) {
			// url without customerUri, no need to check customer url part
			if (LOG.isDebugEnabled()) {
				LOG.debug("Empty servlet path, customer URI stored in session|cookie: {} -> continue.", customerUriSessionOrCookie);
			}
			chain.doFilter(request, response);
			return;
        }
		
		// Pokud customerUri null nebo stejný jako v session -> redirect na url bez customer, nic neresit
		if (customerUri != null && !customerUri.equals(customerUriSessionOrCookie)) {
			// customer url jiny nez v session -> kontrola zda-li je customer povolen
			if (SecurityUtils.isUserLogged()) {
				// zmena customer url v prihlasenem uzivateli -> nepovolit 403
				LOG.warn("Change customer URI by logged user from {} to {}, redirect to 403 Forbidden.", customerUriSessionOrCookie, customerUri);
				resp.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			} else {
				// zmena customer url v neprihlasenem uzivateli
				// TODO: OneApp - kontrola zda-li je customer povolen, pokud ne 403
				// TODO: OneApp - nastavit DS
				LOG.warn("Change customer URI by unlogged user from {} to {}", customerUriSessionOrCookie, customerUri);
				// uložit do session
				WebUtils.setSessAtribute(CUST_URI_ATTR, customerUri, req);
				// uložit do cookies
				WebUtils.setCookie(CUST_URI_ATTR, customerUri, resp);
			}
		}
		
		// remove customer uri and redirect
		String uriToRedirect = servletPath.replace(servletPathFirstPart, "");
		LOG.info("Remove customer URI: {} from servlet path: {}.", servletPathFirstPart, servletPath);
		LOG.info("Redirect to {}", uriToRedirect);
		resp.sendRedirect(uriToRedirect);
	}

	@Override
	public void destroy() {
		// not implemented
	}
	
	/**
	 * Check if url is excluded.
	 * @param servletPath
	 * @return
	 */
	private boolean isExcludedUrl(String servletPath) {
		if (!StringUtils.hasText(servletPath)) {
			return true;
		}
		for (String url : excludedUrls) {
			if (servletPath.startsWith(url)) {
				return true;
			}
		}
		return false;
	}
}
