package com.jzaoralek.scb.ui.common.utils;

import org.springframework.util.StringUtils;

import com.jzaoralek.scb.dataservice.domain.Config.ConfigName;
import com.jzaoralek.scb.dataservice.service.ConfigurationService;

/**
 * Util class for cashing and reading values from configuration.
 *
 */
public final class ConfigUtil {

	private ConfigUtil() {}
	
	public static String getOrgName(ConfigurationService configurationService) {
		String orgNameSession = (String)WebUtils.getSessAtribute(ConfigName.ORGANIZATION_NAME.name());
		if (StringUtils.hasText(orgNameSession)) {
			return orgNameSession;
		} else {
			String value = configurationService.getOrgName();
			addToSessionConfigCache(ConfigName.ORGANIZATION_NAME, value); 
			return value;
		}
	}
	
	public static String getOrgEmail(ConfigurationService configurationService) {
		String orgEmailSession = (String)WebUtils.getSessAtribute(ConfigName.ORGANIZATION_EMAIl.name());
		if (StringUtils.hasText(orgEmailSession)) {
			return orgEmailSession;
		} else {
			String value = configurationService.getOrgEmail();
			addToSessionConfigCache(ConfigName.ORGANIZATION_EMAIl, value);
			return value;
		}
	}
	
	public static String getOrgPhone(ConfigurationService configurationService) {
		String orgPhoneSession = (String)WebUtils.getSessAtribute(ConfigName.ORGANIZATION_PHONE.name());
		if (StringUtils.hasText(orgPhoneSession)) {
			return orgPhoneSession;
		} else {
			String value = configurationService.getOrgPhone();
			addToSessionConfigCache(ConfigName.ORGANIZATION_PHONE, value);
			return value;
		}
	}
	
	public static String getWelcomeInfo(ConfigurationService configurationService) {
		String welcomeInfoSession = (String)WebUtils.getSessAtribute(ConfigName.WELCOME_INFO.name());
		if (StringUtils.hasText(welcomeInfoSession)) {
			return welcomeInfoSession;
		} else {
			String value = configurationService.getWelcomeInfo();
			addToSessionConfigCache(ConfigName.WELCOME_INFO, value);
			return value;
		}
	}
	
	public static boolean isPaymentsAvailable(ConfigurationService configurationService) {
		Boolean paymentsAvailableSession = (Boolean)WebUtils.getSessAtribute(ConfigName.PAYMENTS_AVAILABLE.name());
		if (paymentsAvailableSession != null) {
			return paymentsAvailableSession;
		} else {
			Boolean value = configurationService.isPaymentsAvailable();
			addToSessionConfigCache(ConfigName.PAYMENTS_AVAILABLE, value); 
			return value;
		}
	}
	
	public static void addToSessionConfigCache(ConfigName configName, Object value) {
		WebUtils.setSessAtribute(configName.name(), value);
	}
	
	public static void clearSessionConfigCache(String configName) {
		WebUtils.removeSessAtribute(configName);
	}
}
