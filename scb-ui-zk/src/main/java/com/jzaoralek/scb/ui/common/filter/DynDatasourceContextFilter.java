package com.jzaoralek.scb.ui.common.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class DynDatasourceContextFilter implements Filter {

	public static final String SLASH = "/";
	public static final String CUST_URI_ATTR = "customerUri";
	
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
		if (servletPathPartArr.length == 0) {
			chain.doFilter(request, response);
			return;
		}
		
		String customerUri = servletPathPartArr[1];
		String servletPathFirstPart = SLASH + customerUri;
		
		if(isExcludedUrl(servletPathFirstPart) || SLASH.equals(servletPath)) {
			// url is excluded (e.g. /zkau, /resources etc.) no need to check customer url part
			chain.doFilter(request, response);
			return;
        }
		
		String customerUriSession = (String)WebUtils.getSessAtribute(CUST_URI_ATTR);
		
		// Pokud null nebo stejný jako v session -> redirect na url bez customer, nic neresit
		
		HttpServletResponse resp = (HttpServletResponse)response;
		if (!customerUri.equals(customerUriSession)) {
			// customer url jiny nez v session, -> kontrola zda-li je customer povolen
			if (SecurityUtils.isUserLogged()) {
				// zmena customer url v prihlasenem uzivateli -> nepovolit 403
				resp.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			} else {
				// zmena customer url v neprihlasenem uzivateli
				// TODO: kontrola zda-li je customer povolen, pokud ne 403
				// TODO: nastavit DS
				// uložit do session
				WebUtils.setSessAtribute(CUST_URI_ATTR, customerUri);
			}
		}
		
		// remove customer uri and redirect
		String uriToRedirect = servletPath.replace(servletPathFirstPart, "");
		
//		TODO: logging
		
		resp.sendRedirect(uriToRedirect);
	}

	@Override
	public void destroy() {
		// not implemented
	}
	
	/**
	 * Check if url us excluded.
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
