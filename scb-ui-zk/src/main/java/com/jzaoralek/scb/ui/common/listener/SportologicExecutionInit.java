package com.jzaoralek.scb.ui.common.listener;

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
		String sessionCustomerContext = (String)WebUtils.getSessAtribute(WebConstants.CUST_URI_ATTR);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Session customer context: {}", sessionCustomerContext);
		}
		String threadCustomerDatabase = ClientDatabaseContextHolder.getClientDatabase();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Thread customer database: {}", threadCustomerDatabase);
		}
		
		if (StringUtils.hasText(sessionCustomerContext) 
				&& !StringUtils.hasText(threadCustomerDatabase)) {
			LOG.info("Thread customer database is NULL, setting customer database from sessionContext: {}", sessionCustomerContext);
			ClientDatabaseContextHolder.set(sessionCustomerContext);
		}
		
		if (StringUtils.hasText(sessionCustomerContext) 
				&& StringUtils.hasText(threadCustomerDatabase)
				&& !sessionCustomerContext.equals(threadCustomerDatabase)) {
			LOG.info("Thread customer database differs from sessionContext, setting customer database from sessionContext: {}", sessionCustomerContext);
			ClientDatabaseContextHolder.set(sessionCustomerContext);
		}
	}
}