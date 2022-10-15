package com.jzaoralek.scb.ui.common.utils;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.jzaoralek.scb.dataservice.domain.Config.ConfigName;
import com.jzaoralek.scb.dataservice.service.AdmCustConfigService;
import com.jzaoralek.scb.dataservice.service.ConfigurationService;

/**
 * Util class for cashing and reading values from configuration.
 *
 */
public final class ConfigUtil {
	
	/** Config names cached in session. */
	private static EnumSet<ConfigName> CACHED_CONFIG_NAMES = EnumSet.of(ConfigName.ORGANIZATION_NAME,
																		ConfigName.ORGANIZATION_EMAIl,
																		ConfigName.ORGANIZATION_ADDRESS,
																		ConfigName.ORGANIZATION_IDENT_NO,
																		ConfigName.ORGANIZATION_PHONE,
																		ConfigName.WELCOME_INFO,
																		ConfigName.PAYMENTS_AVAILABLE);

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
		
//		return configurationService.getOrgName();
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
		
//		return configurationService.getOrgEmail();
	}
	
	public static String getOrgAddress(ConfigurationService configurationService) {
		String orgAddrSession = (String)WebUtils.getSessAtribute(ConfigName.ORGANIZATION_ADDRESS.name());
		if (StringUtils.hasText(orgAddrSession)) {
			return orgAddrSession;
		} else {
			String value = configurationService.getOrgAddress();
			addToSessionConfigCache(ConfigName.ORGANIZATION_ADDRESS, value); 
			return value;
		}
	}
	
	public static String getOrgIdentNo(ConfigurationService configurationService) {
		String orgIdentNoSession = (String)WebUtils.getSessAtribute(ConfigName.ORGANIZATION_IDENT_NO.name());
		if (StringUtils.hasText(orgIdentNoSession)) {
			return orgIdentNoSession;
		} else {
			String value = configurationService.getOrgIdentNo();
			addToSessionConfigCache(ConfigName.ORGANIZATION_IDENT_NO, value); 
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
		
//		return configurationService.getOrgPhone();
	}
	
	public static String getOrgContactPerson(ConfigurationService configurationService) {
		String orgContactPerson = (String)WebUtils.getSessAtribute(ConfigName.ORGANIZATION_CONTACT_PERSON.name());
		if (StringUtils.hasText(orgContactPerson)) {
			return orgContactPerson;
		} else {
			String value = configurationService.getOrgContactPerson();
			addToSessionConfigCache(ConfigName.ORGANIZATION_CONTACT_PERSON, value);
			return value;
		}
		
//		return configurationService.getOrgPhone();
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
		
//		return configurationService.getWelcomeInfo();
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
		
//		return configurationService.isPaymentsAvailable();
	}
	
	/* TODO: OneApp, vycistit po pridani noveho customera, 
	 * tzn zavolat ConfigUtil.clearSessionConfigCache(ConfigName.ADM_CUST_CONFIG_IDS.name() */
	public static Set<String> getCustomerConfigIds(AdmCustConfigService admCustConfigService) {
		Set<String> custConfigIds = (Set<String>)WebUtils.getSessAtribute(ConfigName.ADM_CUST_CONFIG_IDS.name());
		if (!CollectionUtils.isEmpty(custConfigIds)) {
			return custConfigIds;
		} else {
			Set<String> value = admCustConfigService.getCustCongigIds();
			addToSessionConfigCache(ConfigName.ADM_CUST_CONFIG_IDS, value);
			return value;
		}
	}
	
	/**
	 * Clearing all configes cached in session.
	 */
	public static void clearCachedCfgs(HttpSession session) {
		Objects.requireNonNull(session, "session is null");
		CACHED_CONFIG_NAMES.forEach(i -> session.removeAttribute(i.name()));
	}
	
	public static void addToSessionConfigCache(ConfigName configName, Object value) {
		WebUtils.setSessAtribute(configName.name(), value);
	}
	
	public static void clearSessionConfigCache(String configName) {
		WebUtils.removeSessAtribute(configName);
	}
}
