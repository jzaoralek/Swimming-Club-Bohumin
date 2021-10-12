package com.jzaoralek.scb.ui.common.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.jzaoralek.scb.dataservice.service.ScbUserService;

/**
 * Filter to check customer url, add datasource and redirect to url withou customer part.
 *
 */
public class DynamicDatasourceFilter implements Filter {

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
		
		if(isExcludedUrl(servletPath) || "/".equals(servletPath)) {
			// url is excluded (e.g. /zkau, /resources etc.) no need to check customer url part
			chain.doFilter(request, response);
			return;
        }
		
		// check customer url part
		System.out.println("-----------------------");
		System.out.println(req.getServletPath());
		System.out.println(req.getContextPath());
		System.out.println(req.getRequestURL());
		System.out.println(req.getRequestURI());
		System.out.println("-----------------------");
		
		int slashIndexOf = servletPath.indexOf("/");
		String customerUri = servletPath.substring(slashIndexOf);
		String uriToRedirect = customerUri.replace(customerUri, "");
		
		// TODO 
		// - logovani
		// - url customer id uvnitr url
		
//		String strippedPath = req.getServletPath().substring(0, slashIndexOf);a
		((HttpServletResponse)response).sendRedirect(uriToRedirect);
		
//		chain.doFilter(request, response);
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
		for (String url : excludedUrls) {
			if (servletPath.contains(url)) {
				return true;
			}
		}
		return false;
	}
}
