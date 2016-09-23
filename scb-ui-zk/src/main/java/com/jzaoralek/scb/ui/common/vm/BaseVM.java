package com.jzaoralek.scb.ui.common.vm;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;
import org.zkoss.bind.Converter;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.service.ConfigurationService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.converter.Converters;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.validator.Validators;

public class BaseVM {

	private List<Boolean> booleanListItem = Arrays.asList(null, Boolean.TRUE, Boolean.FALSE);

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

	public Converter getDateConverter() {
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

	/**
	 * Kontrola povoleni podavani prihlasek.
	 * @return
	 */
    public boolean isCourseApplicationAllowed() {
    	return configurationService.isCourseApplicationsAllowed();
    }
}