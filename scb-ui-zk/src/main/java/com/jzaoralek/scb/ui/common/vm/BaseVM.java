package com.jzaoralek.scb.ui.common.vm;

import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import org.springframework.util.StringUtils;
import org.zkoss.bind.Converter;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Listitem;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.service.ConfigurationService;
import com.jzaoralek.scb.dataservice.service.MailService;
import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.converter.Converters;
import com.jzaoralek.scb.ui.common.utils.JasperUtil;
import com.jzaoralek.scb.ui.common.utils.ManifestSolver;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.validator.ExistingUsernameValidator;
import com.jzaoralek.scb.ui.common.validator.Validators;

public class BaseVM {

	private final String appVersion = ManifestSolver.getMainAttributeValue("Application-version");
	protected String pageHeadline;

	protected Attachment attachment;
	private final List<Boolean> booleanListItem = Arrays.asList(null, Boolean.TRUE, Boolean.FALSE);
	private final List<Listitem> roleList = WebUtils.getMessageItemsFromEnum(EnumSet.allOf(ScbUserRole.class));
	private final List<Listitem> roleListWithEmptyItem = WebUtils.getMessageItemsFromEnumWithEmptyItem(EnumSet.allOf(ScbUserRole.class));

	@WireVariable
	protected ConfigurationService configurationService;
	
	@WireVariable
	private ScbUserService scbUserService;
	
	@WireVariable
	protected MailService mailService;

	protected String returnToPage;
	
	private ExistingUsernameValidator existingUsernameValidator;
	
	@Init
	public void init() {
		this.existingUsernameValidator = new ExistingUsernameValidator(scbUserService);
	}

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

	public Validator getExistingusernameValidator() {
		return this.existingUsernameValidator;
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

	public Converter getMonthConverter() {
		return Converters.getMonthConverter();
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

	protected void sendMailToNewUser(ScbUser user) {
		StringBuilder mailToUser = new StringBuilder();
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.newUserAdmin.text0"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.newUserAdmin.text1"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.newUserAdmin.text2", new Object[] {user.getUsername()}));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.newUserAdmin.text3", new Object[] {user.getPassword()}));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		
		// pro roli USER souhrn funkcionalit aplikace
		if (user.getRole() == ScbUserRole.USER) {
			mailToUser.append(Labels.getLabel("msg.ui.mail.text.newUser.text0"));
			mailToUser.append(WebConstants.LINE_SEPARATOR);
			mailToUser.append(Labels.getLabel("msg.ui.mail.text.newUser.text1"));
			mailToUser.append(WebConstants.LINE_SEPARATOR);
			mailToUser.append(Labels.getLabel("msg.ui.mail.text.newUser.text2"));
			mailToUser.append(WebConstants.LINE_SEPARATOR);
			mailToUser.append(Labels.getLabel("msg.ui.mail.text.newUser.text3"));
			mailToUser.append(WebConstants.LINE_SEPARATOR);
			mailToUser.append(Labels.getLabel("msg.ui.mail.text.newUser.text4"));
			mailToUser.append(WebConstants.LINE_SEPARATOR);
			mailToUser.append(Labels.getLabel("msg.ui.mail.text.newUser.text5"));
//			mailToUser.append(WebConstants.LINE_SEPARATOR);
//			mailToUser.append(Labels.getLabel("msg.ui.mail.text.newUser.text6"));	
		}
		
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.newUser.text7"));
		
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.newUserAdmin.text4"));
		
		mailService.sendMail(user.getContact().getEmail1(), Labels.getLabel("msg.ui.mail.subject.newUserAdmin"), mailToUser.toString(), null, null);
	}
	
	protected void sendEndOfSeasonMailToUser(ScbUser user) {
		StringBuilder mailToUser = new StringBuilder();
		mailToUser.append(Labels.getLabel("msg.ui.mail.endOfSeason.text0"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.endOfSeason.text1"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.endOfSeason.text2"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.endOfSeason.text3"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.endOfSeason.text4"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.endOfSeason.text5"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.endOfSeason.text6"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.endOfSeason.text7"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.endOfSeason.text8"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.endOfSeason.text9"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.endOfSeason.text10"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.endOfSeason.text11"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.endOfSeason.text12"));
		
		mailService.sendMail(user.getContact().getEmail1(), Labels.getLabel("msg.ui.mail.endOfSeason.subject"), mailToUser.toString(), null, null);
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
	
	protected Attachment buildCourseApplicationAttachment(CourseApplication courseApplication, byte[] byteArray) {
		if (courseApplication == null) {
			throw new IllegalArgumentException("courseApplication is null");
		}
		if (byteArray == null) {
			throw new IllegalArgumentException("byteArray is null");
		}
		StringBuilder fileName = new StringBuilder();
		fileName.append("prihlaska_do_klubu");
		fileName.append("_" + courseApplication.getCourseParticRepresentative().getContact().getEmail1());
		fileName.append(".pdf");

		// create attachment for FileDownloadServlet
		Attachment attachment = new Attachment();
		attachment.setByteArray(byteArray);
		attachment.setContentType("application/pdf");
		attachment.setName(fileName.toString());
		return attachment;
	}

	public void sendMail(CourseApplication courseApplication, String headline) {
		StringBuilder mailToRepresentativeSb = new StringBuilder();
		mailToRepresentativeSb.append(Labels.getLabel("msg.ui.mail.courseApplication.text0"));
		mailToRepresentativeSb.append(System.getProperty("line.separator"));
		mailToRepresentativeSb.append(System.getProperty("line.separator"));
		mailToRepresentativeSb.append(Labels.getLabel("msg.ui.mail.courseApplication.text1"));
		mailToRepresentativeSb.append(System.getProperty("line.separator"));
		mailToRepresentativeSb.append(System.getProperty("line.separator"));
		mailToRepresentativeSb.append(Labels.getLabel("msg.ui.mail.courseApplication.text2"));

		byte[] byteArray = JasperUtil.getReport(courseApplication, headline);
		this.attachment = buildCourseApplicationAttachment(courseApplication, byteArray);

		// mail to course participant representative
		mailService.sendMail(courseApplication.getCourseParticRepresentative().getContact().getEmail1(), Labels.getLabel("txt.ui.menu.application"), mailToRepresentativeSb.toString(), byteArray,this.attachment.getName().toLowerCase());

		StringBuilder mailToClupSb = new StringBuilder();
		String courseApplicationYear = configurationService.getCourseApplicationYear();
		mailToClupSb.append(Labels.getLabel("msg.ui.mail.text.newApplication.text0", new Object[] {courseApplicationYear}));
		mailToClupSb.append(System.getProperty("line.separator"));
		String participantInfo = courseApplication.getCourseParticipant().getContact().getFirstname() + " " + courseApplication.getCourseParticipant().getContact().getSurname() + ", " + getDateConverter().coerceToUi(courseApplication.getCourseParticipant().getBirthdate(), null, null);
		mailToClupSb.append(Labels.getLabel("msg.ui.mail.text.newApplication.text1", new Object[] {participantInfo}));
		mailToClupSb.append(System.getProperty("line.separator"));
		String representativeInfo = courseApplication.getCourseParticRepresentative().getContact().getFirstname() + " " + courseApplication.getCourseParticRepresentative().getContact().getSurname() + ", " + courseApplication.getCourseParticRepresentative().getContact().getEmail1() + ", " + courseApplication.getCourseParticRepresentative().getContact().getPhone1();
		mailToClupSb.append(Labels.getLabel("msg.ui.mail.text.newApplication.text2", new Object[] {representativeInfo}));

		// mail to club
		mailService.sendMail(Labels.getLabel("txt.ui.organization.email"), Labels.getLabel("msg.ui.mail.subject.newApplication", new Object[] {courseApplicationYear}), mailToClupSb.toString(), null, null);
	}
	
	protected void sendMailWithResetpassword(ScbUser user) {
		StringBuilder mailToUser = new StringBuilder();
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.reset.text0"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.reset.text1"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.reset.text2", new Object[] {user.getUsername()}));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.reset.text3", new Object[] {user.getPassword()}));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.reset.text4"));

		mailService.sendMail(user.getContact().getEmail1(), Labels.getLabel("msg.ui.mail.subject.resetPassword"), mailToUser.toString(), null, null);
	}
	
	@Command
	public void downloadCmd() {
		WebUtils.downloadAttachment(this.attachment);
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

	public String getPageHeadline() {
		return pageHeadline;
	}

    public Boolean isUserLogged() {
    	return SecurityUtils.isUserLogged();
    }

    public Boolean userInRole(String role) {
    	return SecurityUtils.userInRole(role);
    }

    public Boolean isLoggedUserInRole(String role) {
    	return isUserLogged() && userInRole(role);
    }
    
	/**
	 * Kontrola povoleni podavani prihlasek.
	 * @return
	 */
    public boolean isCourseApplicationAllowed() {
    	return configurationService.isCourseApplicationsAllowed();
    }
}