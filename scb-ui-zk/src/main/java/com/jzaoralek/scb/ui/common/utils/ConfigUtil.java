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
			return configurationService.getOrgName();
		}
	}
	
	public static String getOrgEmail(ConfigurationService configurationService) {
		String orgEmailSession = (String)WebUtils.getSessAtribute(ConfigName.ORGANIZATION_EMAIl.name());
		if (StringUtils.hasText(orgEmailSession)) {
			return orgEmailSession;
		} else {
			return configurationService.getOrgEmail();
		}
	}
	
	public static String getOrgPhone(ConfigurationService configurationService) {
		String orgPhoneSession = (String)WebUtils.getSessAtribute(ConfigName.ORGANIZATION_PHONE.name());
		if (StringUtils.hasText(orgPhoneSession)) {
			return orgPhoneSession;
		} else {
			return configurationService.getOrgPhone();
		}
	}
	
	public static String getWelcomeInfo(ConfigurationService configurationService) {
		String welcomeInfoSession = (String)WebUtils.getSessAtribute(ConfigName.WELCOME_INFO.name());
		if (StringUtils.hasText(welcomeInfoSession)) {
			return welcomeInfoSession;
		} else {
			return configurationService.getWelcomeInfo();
		}
	}
	
	public static void clearSessionConfigCache(String configName) {
		WebUtils.removeSessAtribute(configName);
	}
}
