package com.jzaoralek.scb.ui.common.vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zkoss.bind.Converter;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Listitem;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig.CourseApplicationFileType;
import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.service.ConfigurationService;
import com.jzaoralek.scb.dataservice.service.CourseApplicationFileConfigService;
import com.jzaoralek.scb.dataservice.service.MailService;
import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.dataservice.utils.PaymentUtils;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.converter.Converters;
import com.jzaoralek.scb.ui.common.template.SideMenuComposer.ScbMenuItem;
import com.jzaoralek.scb.ui.common.utils.ConfigUtil;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
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
	
	protected static String orgName;
	protected String orgEmail;
	protected String orgPhone;
	protected String welcomeInfo;

	@WireVariable
	protected ConfigurationService configurationService;
	
	@WireVariable
	protected ScbUserService scbUserService;
	
	@WireVariable
	protected MailService mailService;
	
	@WireVariable
	protected CourseApplicationFileConfigService courseApplicationFileConfigService;

	protected String returnToPage;
	protected String returnToUrl;
	
	private ExistingUsernameValidator existingUsernameValidator;
	
	@Init
	public void init() {
		this.existingUsernameValidator = new ExistingUsernameValidator(scbUserService);
		
		// naplneni cashovanych hodnot z konfigurace
		if (configurationService != null) {
			orgName = ConfigUtil.getOrgName(configurationService);			
		}
	}

	public static String getOrgNameStatic() {
		return orgName;
	}

    /**
     * Dodatečně označí položku hlavního menu
     * 
     * @param menuId
     */
    protected void setMenuSelected(ScbMenuItem menuItem) {
        EventQueueHelper.publish(ScbEvent.SET_MENU_SELECTED, menuItem);
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
	
	/**
	 * Sestavi variabilni symbol pro platbu ucastnika za kurz.
	 * @param yearFrom
	 * @param semester
	 * @param courseParticVarsymbolCore
	 * @return
	 */
	public String buildCoursePaymentVarsymbol(int yearFrom, int semester, int courseParticVarsymbolCore) {
		return PaymentUtils.buildCoursePaymentVarsymbol(yearFrom, semester, courseParticVarsymbolCore);
	}
	
	/**
	 * Prevede lekci na format zobrazitelny v tabulce se spravnym formatem dnu a casu.
	 * @param lesson
	 * @return
	 */
	public String getLessonToUi(Lesson lesson) {
		StringBuilder sb = new StringBuilder();
		sb.append(Converters.getEnumlabelconverter().coerceToUi(lesson.getDayOfWeek(), null, null));
		sb.append(" ");
		sb.append(Converters.getTimeconverter().coerceToUi(lesson.getTimeFrom(), null, null));
		sb.append(" - ");
		sb.append(Converters.getTimeconverter().coerceToUi(lesson.getTimeTo(), null, null));
		return sb.toString();
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
		if (StringUtils.hasText(this.returnToUrl)) {
			Executions.sendRedirect(this.returnToUrl);
		} else if (StringUtils.hasText(this.returnToPage)) {
			Executions.sendRedirect(this.returnToPage);
		}
	}

	protected void sendMailToNewUser(ScbUser user) {
		StringBuilder mailToUser = new StringBuilder();
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.newUserAdmin.text0"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.newUserAdmin.text1", new Object[] {configurationService.getOrgName(), configurationService.getBaseURL()}));
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
		mailToUser.append(buildMailSignature());
		
		mailService.sendMail(user.getContact().getEmail1(), null, Labels.getLabel("msg.ui.mail.subject.newUserAdmin", new Object[] {configurationService.getOrgName()}), mailToUser.toString(), null);
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
	
	/**
	 * Otevre stranku pro odeslani emailu na adresy na vstupu.
	 * @param recipientList
	 */
	protected void goToSendEmailCore(Set<String> recipientList) {
		if (CollectionUtils.isEmpty(recipientList)) {
			return;
		}
		
		WebUtils.setSessAtribute(WebConstants.EMAIL_RECIPIENT_LIST_PARAM, recipientList);
		Executions.getCurrent().sendRedirect(WebPages.MESSAGE.getUrl(), "_blank");
	}
	
	protected CourseApplicationFileConfig getByType(List<CourseApplicationFileConfig> cafcList, CourseApplicationFileType type) {
		if (cafcList == null || cafcList.isEmpty()) {
			return null;
		}
		
		CourseApplicationFileConfig ret = null;
		for (CourseApplicationFileConfig fileConfig : cafcList) {
			if (fileConfig.getType() == type) {
				ret = fileConfig;
				break;
			}
		}
		return ret;
	}
	
	/**
	 * Sestavi podlis do emailu.
	 * @return
	 */
	protected String buildMailSignature() {
		StringBuilder sb = new StringBuilder();		
		sb.append(configurationService.getOrgName());
		sb.append(WebConstants.LINE_SEPARATOR);
		sb.append(Labels.getLabel("txt.ui.common.contact"));
		sb.append(": ");
		sb.append(configurationService.getOrgContactPerson());
		sb.append(", ");
		sb.append(configurationService.getOrgPhone());
		sb.append(", ");
		sb.append(configurationService.getOrgEmail());

		return sb.toString();
	}

	public void sendMail(CourseApplication courseApplication, String headline) {
		byte[] byteArray = JasperUtil.getReport(courseApplication, headline, configurationService);
		this.attachment = buildCourseApplicationAttachment(courseApplication, byteArray);
		
        List<com.jzaoralek.scb.dataservice.domain.Attachment> attachmentList = new ArrayList<>();
        // attachment prihlaska
        attachmentList.add(new com.jzaoralek.scb.dataservice.domain.Attachment(byteArray, this.attachment.getName().toLowerCase()));

        // dynamic attachments (GDPR, HEALTH_INFO, HEALTH_EXAM, CLUB_RULES)
        List<CourseApplicationFileConfig> cafcList = courseApplicationFileConfigService.getListForEmail();
        for (CourseApplicationFileType type : CourseApplicationFileType.values()) {
        	CourseApplicationFileConfig gdprFileConfig = getByType(cafcList, type);
        	if (gdprFileConfig != null && gdprFileConfig.isEmailAttachment() && gdprFileConfig.getAttachment() != null) {
        		attachmentList.add(gdprFileConfig.getAttachment());
        	}
        }
        // attachment gdpr
//      byte[] gdprByteArray = WebUtils.getFileAsByteArray("/resources/docs/gdpr.docx");
//		if (gdprByteArray != null) {
//			attachmentList.add(new com.jzaoralek.scb.dataservice.domain.Attachment(gdprByteArray,"gdpr-souhlas.docx"));
//		}
//		byte[] gdprPdfByteArray = WebUtils.getFileAsByteArray("/resources/docs/gdpr.pdf");
//		if (gdprPdfByteArray != null) {
//			attachmentList.add(new com.jzaoralek.scb.dataservice.domain.Attachment(gdprPdfByteArray,"souhlas-clena-klubu.pdf"));
//		}
		// attachment lekarska prohlidka
//		byte[] lekarskaProhlidkaByteArray = WebUtils.getFileAsByteArray("/resources/docs/lekarska_prohlidka.docx");
//		if (lekarskaProhlidkaByteArray != null) {
//			attachmentList.add(new com.jzaoralek.scb.dataservice.domain.Attachment(lekarskaProhlidkaByteArray,"lekarska-prohlidka.docx"));
//		}
//		byte[] lekarskaProhlidkaPdfByteArray = WebUtils.getFileAsByteArray("/resources/docs/lekarska_prohlidka.pdf");
//		if (lekarskaProhlidkaPdfByteArray != null) {
//			attachmentList.add(new com.jzaoralek.scb.dataservice.domain.Attachment(lekarskaProhlidkaPdfByteArray,"lekarska-prohlidka.pdf"));
//		}
		
		StringBuilder mailToRepresentativeSb = new StringBuilder();
		mailToRepresentativeSb.append(Labels.getLabel("msg.ui.mail.courseApplication.text0"));
		mailToRepresentativeSb.append(System.getProperty("line.separator"));
		mailToRepresentativeSb.append(System.getProperty("line.separator"));
		mailToRepresentativeSb.append(Labels.getLabel("msg.ui.mail.courseApplication.text1"));
		mailToRepresentativeSb.append(System.getProperty("line.separator"));
		mailToRepresentativeSb.append(System.getProperty("line.separator"));
		
		// Ucastnik
		mailToRepresentativeSb.append(Labels.getLabel("msg.ui.mail.courseApplication.text3"));
		mailToRepresentativeSb.append(System.getProperty("line.separator"));
		mailToRepresentativeSb.append(courseApplication.getCourseParticipant().getContact().getCompleteName());
		
		// Kurz
		if (courseApplication.getCourseParticipant().getCourseList() != null && !courseApplication.getCourseParticipant().getCourseList().isEmpty()) {
			Course course = courseApplication.getCourseParticipant().getCourseList().get(0);
			mailToRepresentativeSb.append(System.getProperty("line.separator"));
			mailToRepresentativeSb.append(System.getProperty("line.separator"));
			mailToRepresentativeSb.append(Labels.getLabel("msg.ui.mail.courseApplication.text4"));
			mailToRepresentativeSb.append(System.getProperty("line.separator"));
			// nazev a popis
			mailToRepresentativeSb.append(course.getName() + (StringUtils.hasText(course.getDescription()) ? (", " + course.getDescription()) : ""));
			mailToRepresentativeSb.append(System.getProperty("line.separator"));
			if (course.getCourseLocation() != null) {
				// nazev a popis mista kurzu
				mailToRepresentativeSb.append(course.getCourseLocation().getName() + (StringUtils.hasText(course.getCourseLocation().getDescription()) ? (", " + course.getCourseLocation().getDescription()) : ""));
				mailToRepresentativeSb.append(System.getProperty("line.separator"));				
			}
			if (course.getLessonList() != null && !course.getLessonList().isEmpty()) {
				// lekce
				for (Lesson item : course.getLessonList()) {
					mailToRepresentativeSb.append(getLessonToUi(item));
					mailToRepresentativeSb.append(System.getProperty("line.separator"));
				}
			}
			
		}
		
		// specificky text z konfigurace
		String specText = configurationService.getCourseApplicationEmailSpecText();
		if (StringUtils.hasText(specText)) {
			mailToRepresentativeSb.append(System.getProperty("line.separator"));
			mailToRepresentativeSb.append(System.getProperty("line.separator"));
			mailToRepresentativeSb.append(specText);
			mailToRepresentativeSb.append(System.getProperty("line.separator"));
		}
		
//		if (attachmentList != null && !attachmentList.isEmpty()) {
//			mailToRepresentativeSb.append(System.getProperty("line.separator"));
//			mailToRepresentativeSb.append(System.getProperty("line.separator"));
//			if (attachmentList.size() == 1) {
//				mailToRepresentativeSb.append(Labels.getLabel("msg.ui.mail.courseApplication.text5"));
//			} else {
//				mailToRepresentativeSb.append(Labels.getLabel("msg.ui.mail.courseApplication.text6"));
//			}
//			mailToRepresentativeSb.append(System.getProperty("line.separator"));
//		}
		
		mailToRepresentativeSb.append(System.getProperty("line.separator"));
		mailToRepresentativeSb.append(System.getProperty("line.separator"));
		mailToRepresentativeSb.append(buildMailSignature());

		// mail to course participant representative
		mailService.sendMail(courseApplication.getCourseParticRepresentative().getContact().getEmail1()
				, null
				, Labels.getLabel("txt.ui.menu.application")
				, mailToRepresentativeSb.toString()
				, attachmentList);

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
		mailService.sendMail(ConfigUtil.getOrgEmail(configurationService), null, Labels.getLabel("msg.ui.mail.subject.newApplication", new Object[] {courseApplicationYear}), mailToClupSb.toString(), null);
	}
	
	protected void sendMailWithResetpassword(ScbUser user) {
		StringBuilder mailToUser = new StringBuilder();
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.reset.text0"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.reset.text1", new Object[] {configurationService.getBaseURL()}));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.reset.text2", new Object[] {user.getUsername()}));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.reset.text3", new Object[] {user.getPassword()}));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(buildMailSignature());

		mailService.sendMail(user.getContact().getEmail1(), null, Labels.getLabel("msg.ui.mail.subject.resetPassword"), mailToUser.toString(), null);
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
    
    public String getDateColWidth() {
    	return WebConstants.DATE_COL_WIDTH;
    }
    
    public String getOrgEmail() {
		return ConfigUtil.getOrgEmail(configurationService);
	}
	
	public String getOrgPhone() {
		return ConfigUtil.getOrgPhone(configurationService);
	}
	
	public String getOrgName2() {
		return ConfigUtil.getOrgName(configurationService);
	}
}