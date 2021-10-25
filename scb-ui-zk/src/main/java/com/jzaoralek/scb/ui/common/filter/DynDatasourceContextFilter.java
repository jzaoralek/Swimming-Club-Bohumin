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
// 1. Přejmenovat na CustomerContextFilter, řeší jen nastavení customer conetxtu do session a cookie
// 2. Nový servlet/filter CustomerDatasourceFilter, který vytáhne ze session nebo cookie a nastaví datasource,
// 3. Problem: pokud je v nové session zadána url bez customer contextu, např. http://localhost:7002/pages/common/login.zul, nenastavi se CustomerUri
public class DynDatasourceContextFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(DynDatasourceContextFilter.class);
	
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
		String servletPathFirstPart = null;
		
		if (servletPathPartArr.length > 0) {
			servletPathFirstPart = SLASH + servletPathPartArr[1];
			if(isExcludedUrl(servletPathFirstPart)) {
				// url is excluded (e.g. /zkau, /resources etc.) no need to check customer url part
				chain.doFilter(request, response);
				return;
			}			
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
				LOG.debug("Customer URI get from cookie: {}", customerUriSessionOrCookie);
			}
			// uložit do session
			WebUtils.setSessAtribute(CUST_URI_ATTR, customerUriSessionOrCookie, req);
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Customer URI stored in session|cookie: {}", customerUriSessionOrCookie);
		}
		
		if (servletPathPartArr.length == 0 || SLASH.equals(servletPath)) {
			// url without customerUri, no need to check customer url part
			if (LOG.isDebugEnabled()) {
				LOG.debug("Empty servlet path, customer URI stored in session|cookie: {} -> continue.", customerUriSessionOrCookie);
			}
			chain.doFilter(request, response);
			return;
        }
		
		String customerUriContext = servletPathPartArr[1];
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Customer URI from servletPath: {}", customerUriContext);
		}
		
		HttpServletResponse resp = (HttpServletResponse)response;
		
		// customerUri je null a není v sesion ani cookies -> 403
		if (!StringUtils.hasText(customerUriContext) && !StringUtils.hasText(customerUriSessionOrCookie)) {
			// TODO: OneApp - nahradit za redirect na stranku kde si vybere customera
			LOG.warn("Customer URI entered in servletPath and stored in session|cookie are NULL, redirect to 403 Forbidden.");
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		// Pokud customerUri null nebo stejný jako v session -> redirect na url bez customer, nic neresit
		if (customerUriContext != null && !customerUriContext.equals(customerUriSessionOrCookie)) {
			// customer url jiny nez v session -> kontrola zda-li je customer povolen
			if (SecurityUtils.isUserLogged()) {
				// zmena customer url v prihlasenem uzivateli -> nepovolit 403
				LOG.warn("Change customer URI by logged user from {} to {}, redirect to 403 Forbidden.", customerUriSessionOrCookie, customerUriContext);
				resp.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			} else {
				// zmena customer url v neprihlasenem uzivateli
				// TODO: OneApp - kontrola zda-li je customer povolen, pokud ne 403
				// TODO: OneApp - nastavit DS
				LOG.warn("Change customer URI by unlogged user from {} to {}", customerUriSessionOrCookie, customerUriContext);
				if (LOG.isDebugEnabled()) {
					LOG.debug("Storing customer URI to session and cookie: {}.", customerUriContext);
				}
				// uložit do session a cookie
				WebUtils.setSessAtribute(CUST_URI_ATTR, customerUriContext, req);
				WebUtils.setCookie(CUST_URI_ATTR, customerUriContext, resp);				
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
