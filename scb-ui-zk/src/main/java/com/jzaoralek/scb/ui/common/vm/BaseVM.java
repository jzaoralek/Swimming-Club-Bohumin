package com.jzaoralek.scb.ui.common.vm;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.jzaoralek.scb.dataservice.domain.AddressValidationStatus;
import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig;
import com.jzaoralek.scb.dataservice.domain.CoursePaymentVO;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig.CourseApplicationFileType;
import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.domain.Mail;
import com.jzaoralek.scb.dataservice.domain.PaymentInstruction;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.service.ConfigurationService;
import com.jzaoralek.scb.dataservice.service.CourseApplicationFileConfigService;
import com.jzaoralek.scb.dataservice.service.MailService;
import com.jzaoralek.scb.dataservice.service.PaymentService;
import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.dataservice.service.impl.ConfigurationServiceImpl;
import com.jzaoralek.scb.dataservice.utils.PaymentUtils;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.converter.Converters;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.template.SideMenuComposer.ScbMenuItem;
import com.jzaoralek.scb.ui.common.utils.ConfigUtil;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.JasperUtil;
import com.jzaoralek.scb.ui.common.utils.ManifestSolver;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.validator.BirthNumberValidator;
import com.jzaoralek.scb.ui.common.validator.ExistingUsernameValidator;
import com.jzaoralek.scb.ui.common.validator.UsernameValidator;
import com.jzaoralek.scb.ui.common.validator.Validators;
import com.sportologic.ruianclient.model.RuianValidationResponse;
import com.sportologic.ruianclient.service.RuianService;

public class BaseVM {
	
	private static final Logger LOG = LoggerFactory.getLogger(BaseVM.class);

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
	
	@WireVariable
	protected RuianService ruianServiceRest;
	
	@WireVariable
	protected PaymentService paymentService;

	protected String returnToPage;
	protected String returnToUrl;
	
	private ExistingUsernameValidator existingUsernameValidator;
	private BirthNumberValidator birthNumberValidator;
	private UsernameValidator usernameValidator;

	@Init
	public void init() {
		this.existingUsernameValidator = new ExistingUsernameValidator(scbUserService);
		this.birthNumberValidator = new BirthNumberValidator(configurationService);
		this.usernameValidator = new UsernameValidator(scbUserService);
		
		// naplneni cashovanych hodnot z konfigurace
		if (configurationService != null) {
			orgName = ConfigUtil.getOrgName(configurationService);			
		}
	}

	public static String getOrgNameStatic() {
		return orgName;
	}
	
	@Command
    public void logoutCmd() {
    	Executions.sendRedirect("/logout");
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
		return WebUtils.getLessonToUi(lesson);
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
		return this.birthNumberValidator;
	}

	public UsernameValidator getUsernameValidator() {
		return usernameValidator;
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
	
	public Validator getSexMaleValidator() {
		return Validators.getSexmalevalidator();
	}
	
	public Validator getEqualValueValidator() {
		return Validators.getEqualValueValidator();
	}
	
	public Converter<String, Date, Component> getDateConverter() {
		return Converters.getDateconverter();
	}

	public Converter<String, Date, Component> getDateTimeConverter() {
		return Converters.getDateTimeConverter();
	}

	public Converter<String, Time, Component> getTimeConverter() {
		return Converters.getTimeconverter();
	}

	public Converter<String, Enum<?>, Component> getEnumLabelConverter() {
		return Converters.getEnumlabelconverter();
	}

	public Converter<String, Time, Component> getTimeSecondConverter() {
		return Converters.getTimeSecondconverter();
	}

	public Converter<String, Long, Component> getIntervaltomillsConverter() {
		return Converters.getIntervaltomillsconverter();
	}

	public Converter<String, Date, Component> getMonthConverter() {
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
		mailService.sendMail(buildMailToNewUser(user));
	}
	
	protected Mail buildMailToNewUser(ScbUser user) {
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
		
		return new Mail(user.getContact().getEmail1(), null, Labels.getLabel("msg.ui.mail.subject.newUserAdmin", new Object[] {configurationService.getOrgName()}), mailToUser.toString(), null);
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

	/**
	 * Return default course application title from resource bundle including year.
	 * @return
	 */
	protected String getDefaultCourseApplicationTitle() {
		String year = configurationService.getCourseApplicationYear();
		return Labels.getLabel("txt.ui.menu.applicationWithYear", new Object[] {year});
	}
	
	/**
	 * Return default title for course application from resource bundle including year.
	 * @return
	 */
	
	/**
	 * Return default title for course application from.
	 * In case of current year from configuration,
	 * in other cases default from resource bundle including year
	 * @param courseApplication
	 * @return
	 */
	protected String getTitleForCourseApplication(CourseApplication courseApplication) {
		Pair<Integer,Integer> currentYearFromTo = configurationService.getYearFromTo();
		Integer courseApplicationYearFrom = courseApplication.getYearFrom();
		Integer courseApplicationYearTo = courseApplication.getYearTo();
		
		boolean currentYearCourseApplication = (courseApplicationYearFrom.equals(currentYearFromTo.getValue0())
				&& courseApplicationYearTo.equals(currentYearFromTo.getValue1()));
		if (currentYearCourseApplication) {
			// prihlaska na aktualni rocnik, title z konfigurace
			return configurationService.getCourseApplicationTitle();
		} else {
			// prihlaska z predchoziho rocnihu, default title
			return Labels.getLabel("txt.ui.menu.applicationWithYear", new Object[] {courseApplicationYearFrom + "/" + courseApplicationYearTo});	
		}
	}
	
	/**
	 * Return course application title from configuration.
	 * @return
	 */
	public String getConfigCourseApplicationTitle() {
		return configurationService.getCourseApplicationTitle();
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
	 * Otevre stranku pro odeslani emailu na kontakty na vstupu.
	 * @param recipientList
	 */
	protected void goToSendEmailCore(Set<Contact> recipientList) {
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
		// mail to course participant representative
		mailService.sendMail(buildMailCourseParticRepresentative(courseApplication, headline));
		// mail to club
		mailService.sendMail(buildMailToClub(courseApplication));
		// send payment instructions
		processPaymentInstruction(courseApplication);
	}
	
	/**
	 * Send payment instructions to course participant representative.
	 * Conditions
	 * - module payment is available
	 * - course participant is in course
	 * - account number is filled
	 */
	public void processPaymentInstruction(CourseApplication courseApplication) {
		// check if module payment is available
		if (!configurationService.isPaymentsAvailable()) {
			return;
		}		
		// check if course participant is in course
		if (CollectionUtils.isEmpty(courseApplication.getCourseParticipant().getCourseList())) {
			return;
		}
		
		final String bankAccountNumber = configurationService.getBankAccountNumber();
		// check if account number is filled
		if (!StringUtils.hasText(bankAccountNumber)) {
			return;
		}
		
		// check if send payment instructions after course application create sis allowed
		if (!configurationService.isCourseApplicationPaymentAllowed()) {
			return;
		}
		
		// set payment deadline from configuration
		int paymentDeadlineDays = configurationService.getCourseApplPaymentDeadline();
		if (paymentDeadlineDays == 0) {
			paymentDeadlineDays = 5;
		}
		// payment deadline set to sysdate plus 2 weeks
		Calendar paymentDeadline = Calendar.getInstance();
		paymentDeadline.add(Calendar.DATE, paymentDeadlineDays);
		
		String yearFromTo = courseApplication.getYearFrom() + 
									ConfigurationServiceImpl.COURSE_YEAR_DELIMITER + 
									courseApplication.getYearTo();
		
		// set CoursePaymentVO
		Course course = courseApplication.getCourseParticipant().getCourseList().get(0);
		CoursePaymentVO coursePaymentVO = new CoursePaymentVO(0, 
													course.getPriceSemester1(), 
													course.getPriceSemester2());
		courseApplication.getCourseParticipant().setCoursePaymentVO(coursePaymentVO);
		
		// set course name
		courseApplication.getCourseParticipant().setCourseName(course.getName());
		
		List<PaymentInstruction> paymentInstructionList = Arrays.asList(PaymentInstruction.ofCourseApplication(courseApplication,
				course.getCourseType(),
				courseApplication.getYearFrom(), 
				true, 
				bankAccountNumber));
		
		paymentService.processPaymentInstruction(paymentInstructionList
				, yearFromTo
				, WebConstants.LINE_SEPARATOR
				, getDateConverter().coerceToUi(paymentDeadline.getTime(), null, null)
				, null
				, buildMailSignature()
				, true
				, course.getCourseType());
		
	}
	
	public Mail buildMailCourseParticRepresentative(CourseApplication courseApplication, String headline) {
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
		
		StringBuilder mailToRepresentativeSb = new StringBuilder();
		mailToRepresentativeSb.append(Labels.getLabel("msg.ui.mail.courseApplication.text0"));
		mailToRepresentativeSb.append(getLineSeparator());
		mailToRepresentativeSb.append(getLineSeparator());
		mailToRepresentativeSb.append(Labels.getLabel("msg.ui.mail.courseApplication.text1"));
		mailToRepresentativeSb.append(getLineSeparator());
		mailToRepresentativeSb.append(getLineSeparator());
		
		// Ucastnik
		mailToRepresentativeSb.append(Labels.getLabel("msg.ui.mail.courseApplication.text3"));
		mailToRepresentativeSb.append(" ");
		mailToRepresentativeSb.append(courseApplication.getCourseParticipant().getContact().getCompleteName());
		
		// Kurz
		mailToRepresentativeSb.append(WebUtils.buildCourseApplMailCourseInfo(courseApplication, getLineSeparator()));
		
		// specificky text z konfigurace
		String specText = configurationService.getCourseApplicationEmailSpecText();
		if (StringUtils.hasText(specText)) {
			mailToRepresentativeSb.append(getLineSeparator());
			mailToRepresentativeSb.append(getLineSeparator());
			mailToRepresentativeSb.append(specText);
			mailToRepresentativeSb.append(getLineSeparator());
		}
		
		mailToRepresentativeSb.append(getLineSeparator());
		mailToRepresentativeSb.append(getLineSeparator());
		mailToRepresentativeSb.append(buildMailSignature());

		// mail to course participant representative
		return new Mail(courseApplication.getCourseParticRepresentative().getContact().getEmail1()
				, null
				, Labels.getLabel("txt.ui.menu.application")
				, mailToRepresentativeSb.toString()
				, attachmentList);
	}
	
	private String getLineSeparator() {
		return System.getProperty("line.separator");
	}
	
	protected Mail buildMailToClub(CourseApplication courseApplication) {
		StringBuilder mailToClupSb = new StringBuilder();
		String courseApplicationYear = configurationService.getCourseApplicationYear();
		mailToClupSb.append(Labels.getLabel("msg.ui.mail.text.newApplication.text0", new Object[] {courseApplicationYear}));
		mailToClupSb.append(getLineSeparator());
		String participantInfo = courseApplication.getCourseParticipant().getContact().getFirstname() + " " + courseApplication.getCourseParticipant().getContact().getSurname() + ", " + getDateConverter().coerceToUi(courseApplication.getCourseParticipant().getBirthdate(), null, null);
		mailToClupSb.append(Labels.getLabel("msg.ui.mail.text.newApplication.text1", new Object[] {participantInfo}));
		mailToClupSb.append(getLineSeparator());
		String representativeInfo = courseApplication.getCourseParticRepresentative().getContact().getFirstname() + " " + courseApplication.getCourseParticRepresentative().getContact().getSurname() + ", " + courseApplication.getCourseParticRepresentative().getContact().getEmail1() + ", " + courseApplication.getCourseParticRepresentative().getContact().getPhone1();
		mailToClupSb.append(Labels.getLabel("msg.ui.mail.text.newApplication.text2", new Object[] {representativeInfo}));

		return new Mail(ConfigUtil.getOrgEmail(configurationService), null, Labels.getLabel("msg.ui.mail.subject.newApplication", new Object[] {courseApplicationYear}), mailToClupSb.toString(), null);
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

		mailService.sendMail(user.getContact().getEmail1(), null, Labels.getLabel("msg.ui.mail.subject.resetPassword"), mailToUser.toString(), null, false);
	}
	
	@SuppressWarnings("unchecked")
	protected List<ScbUser> getUserListFromCache() {
		List<ScbUser> userListSessionCache = (List<ScbUser>) WebUtils.getSessAtribute(WebConstants.USER_LIST_CACHE_PARAM);
		if (CollectionUtils.isEmpty(userListSessionCache)) {
			userListSessionCache = scbUserService.getAll();
			WebUtils.setSessAtribute(WebConstants.USER_LIST_CACHE_PARAM, userListSessionCache);
		}
		
		return userListSessionCache;
	}
	
	/**
	 * Validace adresy pred submitem formulare.
	 * @param application
	 * @param courseApplNotVerifiedAddressAllowed
	 * @param submitCore
	 */
	protected void addressValidationBeforeSubmit(Contact contact
			, boolean courseApplNotVerifiedAddressAllowed
			, Runnable submitCore) {
		// overeni validni adresy
		if (contact.isAddressValid()) {
			// adresa je validni, mozno ulozit
			submitCore.run();
		} else {
			// automaticke overeni nevalidní adresy
			placeValidation(contact);
			// kontrola platnosti adresy po automatickem overeni
			if (contact.isAddressValid()) {
				// adresa je validni, mozno ulozit
				submitCore.run();
			} else {
				if (!courseApplNotVerifiedAddressAllowed) {
					// neni povoleno odeslat prihlasku s nevalidni adresou
					// adresa neni validni, zastaveni odeslani
					MessageBoxUtils.showOkWarningDialog("msg.ui.warn.ProcessNotValidAddress", 
							"msg.ui.quest.title.NotValidAddress", 
							new SzpEventListener() {
						@Override
						public void onOkEvent() {
							// nothing
						}
					});
					
				} else {
					// je povoleno odeslat adresu s nevalidní adresou
					// adresa neni validni, dotaz jestli pokracovat
					MessageBoxUtils.showDefaultConfirmDialog(
						"msg.ui.quest.ProcessNotValidAddress",
						"msg.ui.quest.title.NotValidAddress",
						new SzpEventListener() {
							@Override
							public void onOkEvent() {
								submitCore.run();
							}
						}
					);							
				}	
			}					
		}
	}
	
	protected void placeValidation(Contact contact) {
		if (contact == null) {
			return;
		}
		try {
			RuianValidationResponse validationResponse = ruianServiceRest.validate(contact.getCity()
					, contact.getZipCode()
					, contact.getEvidenceNumber()
					, contact.getHouseNumber()
					, contact.getLandRegistryNumber() != null ? String.valueOf(contact.getLandRegistryNumber()) : ""
					, contact.getStreet());
			if (validationResponse != null && validationResponse.isValid()) {
				contact.setAddressValidationStatus(AddressValidationStatus.VALID);
			} else if (validationResponse != null && !validationResponse.isValid()) {
				contact.setAddressValidationStatus(AddressValidationStatus.INVALID);
			} else {
				contact.setAddressValidationStatus(AddressValidationStatus.NOT_VERIFIED);
			}
		} catch (RuntimeException e) {
			LOG.error("RuntimeException caught, ", e);
			WebUtils.showNotificationError(Labels.getLabel("msg.ui.address.AddressVerificationServiceNotAvailable"));
		}
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
    
    public Boolean isLoggedUserAdmin() {
    	return isUserLogged() && userInRole("ADMIN");
    }
    
    /**
     * Seznam typu kurzu.
     * @return
     */
    public List<CourseType> getCourseTypeList() {
		return Arrays.asList(CourseType.values());
	}
    
    /**
     * Popis pro typ kurzu.
     * @param courseType
     * @return
     */
    public String getCourseTypeDesc(CourseType courseType) {
		return Labels.getLabel("txt.ui.CourseType."+courseType.name()+".desc");
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