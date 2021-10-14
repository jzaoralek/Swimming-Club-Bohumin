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

/**
 * Filter to check customer url, add datasource and redirect to url withou customer part.
 *
 */
public class DynDatasourceContextFilter implements Filter {

	public static final String SLASH = "/";
	
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
		String servletPathFirstPart = SLASH + servletPathPartArr[1];
		
		if(isExcludedUrl(servletPathFirstPart) || SLASH.equals(servletPath)) {
			// url is excluded (e.g. /zkau, /resources etc.) no need to check customer url part
			chain.doFilter(request, response);
			return;
        }
		
		// TODO: check customer url part
		
		// remove customer uri and redirect
		String uriToRedirect = servletPath.replace(servletPathFirstPart, "");
		
		// TODO: logging
		
		((HttpServletResponse)response).sendRedirect(uriToRedirect);
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
