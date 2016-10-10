package com.jzaoralek.scb.ui.common.vm;

import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import org.springframework.util.StringUtils;
import org.zkoss.bind.Converter;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Listitem;

import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.service.ConfigurationService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.converter.Converters;
import com.jzaoralek.scb.ui.common.utils.ManifestSolver;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.validator.Validators;

public class BaseVM {

	private final String appVersion = ManifestSolver.getMainAttributeValue("Application-version");

	private final List<Boolean> booleanListItem = Arrays.asList(null, Boolean.TRUE, Boolean.FALSE);
	private final List<Listitem> roleList = WebUtils.getMessageItemsFromEnum(EnumSet.allOf(ScbUserRole.class));
	private final List<Listitem> roleListWithEmptyItem = WebUtils.getMessageItemsFromEnumWithEmptyItem(EnumSet.allOf(ScbUserRole.class));

	@WireVariable
	protected ConfigurationService configurationService;

	private String returnToPage;

	public String getDateFormat() {
		return WebConstants.DATE_FORMAT;
	}
	public static int getFirstnameMaxlength() {
		return WebConstants.FIRSTNAME_MAXLENGTH;
	}
	public static int getSurnameMaxlength() {
		return WebConstants.SURNAME_MAXLENGTH;
	}
	public static int getHealthInsuranceMaxlength() {
		return WebConstants.HEALTH_INSURANCE_MAXLENGTH;
	}
	public static int getDateMaxlength() {
		return WebConstants.DATE_MAXLENGTH;
	}
	public static int getBirthNumberMaxlength() {
		return WebConstants.BIRTH_NUMBER_MAXLENGTH;
	}
	public static int getEmailMaxlength() {
		return WebConstants.EMAIL_MAXLENGTH;
	}
	public static int getPhoneMaxlength() {
		return WebConstants.PHONE_MAXLENGTH;
	}
	public static int getHealthInfoMaxlength() {
		return WebConstants.HEALTH_INFO_MAXLENGTH;
	}
	public static int getNameMaxlength() {
		return WebConstants.NAME_MAXLENGTH;
	}
	public static int getDescriptionMaxlength() {
		return WebConstants.DESCRIPTION_MAXLENGTH;
	}

	public Validator getEmailValidator() {
		return Validators.getEmailValidator();
	}

	public Validator getNotNullValidator() {
		return Validators.getNotNullValidator();
	}

	public Validator getNotNullObjectValidator() {
		return Validators.getNotNullObjectValidator();
	}

	public Validator getBirthNumberValidator() {
		return Validators.getBirthnumbervalidator();
	}

	public Validator getCaptchaValidator() {
		return Validators.getCaptchavalidator();
	}

	public Validator getTimeIntervalValidator() {
		return Validators.getTimeintervalvalidator();
	}

	public Validator getPasswordValidator() {
		return Validators.getPasswordvalidator();
	}

	public Converter<String, Date, Component> getDateConverter() {
		return Converters.getDateconverter();
	}

	public Converter getDateTimeConverter() {
		return Converters.getDateTimeConverter();
	}

	public Converter getTimeConverter() {
		return Converters.getTimeconverter();
	}

	public Converter getEnumLabelConverter() {
		return Converters.getEnumlabelconverter();
	}

	public Converter getTimeSecondConverter() {
		return Converters.getTimeSecondconverter();
	}

	public Converter getIntervaltomillsConverter() {
		return Converters.getIntervaltomillsconverter();
	}

	public Boolean isBackButtonVisible() {
		return StringUtils.hasText(this.returnToPage);
	}

	@Command
    public void backCmd() {
		if (StringUtils.hasText(this.returnToPage)) {
			Executions.sendRedirect(this.returnToPage);
		}
	}

	public List<Boolean> getBooleanListItem() {
		return booleanListItem;
	}

	public List<Listitem> getRoleList() {
		return roleList;
	}

	public List<Listitem> getRoleListWithEmptyItem() {
		return roleListWithEmptyItem;
	}

	protected Listitem getRoleListItem(ScbUserRole role) {
		if (role == null) {
			return null;
		}

		for (Listitem item : this.roleList) {
			if (item.getValue() == role) {
				return item;
			}
		}

		return null;
	}

	public String getNewCourseApplicationTitle() {
		String year = configurationService.getCourseApplicationYear();
		return Labels.getLabel("txt.ui.menu.applicationWithYear", new Object[] {year});
	}

	protected Boolean isSecuredPage() {
		return WebUtils.getCurrentUrl().contains(WebConstants.SECURED_PAGE_URL);
	}

	protected void setReturnPage(String fromPage) {
		this.returnToPage = StringUtils.hasText(fromPage) ? WebPages.valueOf(fromPage).getUrl() : null;
	}

	public String getAppVersion() {
		return appVersion;
	}

	/**
	 * Kontrola povoleni podavani prihlasek.
	 * @return
	 */
    public boolean isCourseApplicationAllowed() {
    	return configurationService.isCourseApplicationsAllowed();
    }
}