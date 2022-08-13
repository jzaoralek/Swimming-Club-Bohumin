package com.jzaoralek.scb.ui.common.listener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ExecutionInit;

import com.jzaoralek.scb.dataservice.datasource.ClientDatabaseContextHolder;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;

/**
 * Invokes when the ZK Loader and Update Engine created a new execution.
 * Used for dynamic setting of customer datasource.
 *
 */
public class SportologicExecutionInit implements ExecutionInit {

	private static final Logger LOG = LoggerFactory.getLogger(SportologicExecutionInit.class);
	
	@Override
	public void init(Execution exec, Execution parent) throws Exception {
		
		// Setting of customer database from session context.
		String sessionCookieCustomerContext = (String)WebUtils.getSessAtribute(WebConstants.CUST_URI_ATTR);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Session customer context: {}", sessionCookieCustomerContext);
		}
		String cookieCustomerContext = WebUtils.readCookieValue(WebConstants.CUST_URI_COOKIE, (HttpServletRequest)exec.getNativeRequest());
		if (LOG.isDebugEnabled()) {
			LOG.debug("Cookie customer context: {}", cookieCustomerContext);
		}
		
		if (!StringUtils.hasText(sessionCookieCustomerContext) 
				&& StringUtils.hasText(cookieCustomerContext)) {
			sessionCookieCustomerContext = cookieCustomerContext;
		}
		
		String threadCustomerDatabase = ClientDatabaseContextHolder.getClientDatabase();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Thread customer database: {}", threadCustomerDatabase);
		}
		
		if (StringUtils.hasText(threadCustomerDatabase) 
					&& !StringUtils.hasText(cookieCustomerContext)) {
			LOG.info("Thread customer database is not NULL, cookieCustomerContext is NULL, setting cookieCustomerContext: {}", cookieCustomerContext);
			WebUtils.setCookie(WebConstants.CUST_URI_COOKIE, 
							threadCustomerDatabase, 
							(HttpServletRequest)exec.getNativeRequest(),
							(HttpServletResponse)exec.getNativeResponse());
		}
		
		if (StringUtils.hasText(sessionCookieCustomerContext) 
				&& StringUtils.hasText(threadCustomerDatabase)
				&& sessionCookieCustomerContext.equals(threadCustomerDatabase)) {
			LOG.info("Thread customer database is same as sessionCookieContext, setting customer database from sessionCookieContext: {}", sessionCookieCustomerContext);
			ClientDatabaseContextHolder.set(sessionCookieCustomerContext);
			return;
		}
		
		if (StringUtils.hasText(sessionCookieCustomerContext) 
				&& !StringUtils.hasText(threadCustomerDatabase)) {
			LOG.info("Thread customer database is NULL, setting customer database from sessionCookieContext: {}", sessionCookieCustomerContext);
			ClientDatabaseContextHolder.set(sessionCookieCustomerContext);
			return;
		}
		
		if (StringUtils.hasText(sessionCookieCustomerContext) 
				&& StringUtils.hasText(threadCustomerDatabase)
				&& !sessionCookieCustomerContext.equals(threadCustomerDatabase)) {
			LOG.info("Thread customer database differs from sessionContext, setting customer database from sessionCookiContext: {}", sessionCookieCustomerContext);
			ClientDatabaseContextHolder.set(sessionCookieCustomerContext);
		}
	}
}