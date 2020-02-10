package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;

import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant.PaymentNotifSendState;
import com.jzaoralek.scb.dataservice.domain.CoursePaymentVO.CoursePaymentState;
import com.jzaoralek.scb.dataservice.domain.Mail;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.CSVUtil;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.ExcelUtil;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseContextVM;
import com.jzaoralek.scb.ui.pages.courseapplication.filter.CourseApplicationFilter;

public class CourseApplicationListVM extends BaseContextVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseApplicationListVM.class);

	public enum PageMode {
		COURSE_APPLICATION_LIST,
		CORSE_PARTICIPANT_LIST;
	}

	@WireVariable
	private CourseApplicationService courseApplicationService;

	private List<CourseApplication> courseApplicationList;
	private List<CourseApplication> courseApplicationListBase;
	private CourseApplicationFilter filter = new CourseApplicationFilter();
	private PageMode pageMode;
	private boolean unregToCurrYear;
	private String unregToCurrYearLabel;
	private final List<Listitem> coursePaymentStateListWithEmptyItem = WebUtils.getMessageItemsFromEnumWithEmptyItem(EnumSet.allOf(CoursePaymentState.class));
	private final List<Listitem> paymentNotifStateListWithEmptyItem = WebUtils.getMessageItemsFromEnumWithEmptyItem(EnumSet.of(PaymentNotifSendState.NOT_SENT_FIRST_SEMESTER, PaymentNotifSendState.NOT_SENT_SECOND_SEMESTER, PaymentNotifSendState.SENT_FIRST_SEMESTER, PaymentNotifSendState.SENT_SECOND_SEMESTER));
	private String bankAccountNumber;
	private int yearFrom;
	private CourseType courseType;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Init
	public void init() {
		initYearContext();
		initCourseTypeCache();

		setPageMode();
		loadData();		
		this.bankAccountNumber = configurationService.getBankAccountNumber();


		final EventQueue eq = EventQueues.lookup(ScbEventQueues.COURSE_APPLICATION_QUEUE.name() , EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.RELOAD_COURSE_APPLICATION_DATA_EVENT.name())) {
					loadData();
				}
			}
		});
	}

	private void initCourseTypeCache() {
		this.courseType = (CourseType)WebUtils.getSessAtribute(WebConstants.COURSE_TYPE_PARAM);
		if (this.courseType == null)  {
			this.courseType = CourseType.TWO_SEMESTER;
			updateCourseTypeCache(this.courseType);
		}
	}
	
	private void updateCourseTypeCache(CourseType courseType) {
		WebUtils.setSessAtribute(WebConstants.COURSE_TYPE_PARAM, courseType);
	}
	
	private void setPageMode() {
		if (WebUtils.getCurrentUrl().contains(WebPages.APPLICATION_LIST.getUrl())) {
			this.pageMode = PageMode.COURSE_APPLICATION_LIST;
		} else if (WebUtils.getCurrentUrl().contains(WebPages.PARTICIPANT_LIST.getUrl())) {
			this.pageMode = PageMode.CORSE_PARTICIPANT_LIST;
		} else {
			throw new IllegalStateException("Unexpected page url: " + WebUtils.getCurrentUrl());
		}
	}

	@NotifyChange("*")
	@Command
    public void deleteCmd(@BindingParam(WebConstants.ITEM_PARAM) final CourseApplication item) {
		if (item ==  null) {
			throw new IllegalArgumentException("CourseApplication is null");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting application with uuid: " + item.getUuid());
		}
		final Object[] msgParams = new Object[] {item.getCourseParticipant().getContact().getCompleteName()};
		final UUID uuid = item.getUuid();
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.deleteApplication",
			"msg.ui.title.deleteRecord",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					try {
						courseApplicationService.delete(uuid);
						EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.RELOAD_COURSE_APPLICATION_DATA_EVENT, null, null);
						WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.applicationDeleted", msgParams));
					} catch (ScbValidationException e) {
						LOG.warn("ScbValidationException caught for application with uuid: " + uuid);
						WebUtils.showNotificationError(e.getMessage());
					}
				}
			},
			msgParams
		);
	}

	@Command
    public void detailCmd(@BindingParam(WebConstants.UUID_PARAM) final UUID uuid, @BindingParam(WebConstants.NEW_TAB_PARAM) final Boolean newTab) {
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}
		String targetPage = (this.pageMode == PageMode.COURSE_APPLICATION_LIST) ? WebPages.APPLICATION_DETAIL.getUrl() : WebPages.PARTICIPANT_DETAIL.getUrl();
		WebPages fromPage = (this.pageMode == PageMode.COURSE_APPLICATION_LIST) ? WebPages.APPLICATION_LIST : WebPages.PARTICIPANT_LIST;
		if (newTab != null && newTab) {
			Executions.getCurrent().sendRedirect(targetPage + "?"+WebConstants.UUID_PARAM+"="+uuid.toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + fromPage, "_blank");
		} else {
			Executions.getCurrent().sendRedirect(targetPage + "?"+WebConstants.UUID_PARAM+"="+uuid.toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + fromPage);
		}
	}
	
	@Command
    public void paymentsCmd(@BindingParam(WebConstants.ITEM_PARAM) final CourseApplication item) {
		if (item ==  null) {
			throw new IllegalArgumentException("CourseApplication is null");
		}
		Executions.sendRedirect(WebPages.PAYMENT_LIST.getUrl() + "?"+WebConstants.COURSE_PARTIC_UUID_PARAM+"="+item.getCourseParticipant().getUuid().toString() + "&" +WebConstants.COURSE_UUID_PARAM+"="+item.getCourseParticipant().getCourseUuid().toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + WebPages.PARTICIPANT_LIST);
	}

	@Command
	@NotifyChange("courseApplicationList")
	public void filterDomCmd() {
		this.courseApplicationList = filter.getApplicationListFiltered(this.courseApplicationListBase);
	}

	@NotifyChange("courseApplicationList")
	@Command
	public void exportToExcel(@BindingParam("listbox") Listbox listbox) {
		String filename = this.pageMode == PageMode.COURSE_APPLICATION_LIST ? "seznam_prihlasek.xls" : "seznam_ucastniku.xls";
		ExcelUtil.exportToExcel(filename, buildExcelRowData(listbox));
	}
	
	@Command
	public void exportToExcelNotValidAddrCmd(@BindingParam("listbox") Listbox listbox) {
		String filename = this.pageMode == PageMode.COURSE_APPLICATION_LIST ? "seznam_prihlasek_neoverena_adresa.xls" : "seznam_ucastniku_neoverena_adresa.xls";
		ExcelUtil.exportToExcel(filename, buildExcelNotValidAddrRowData(listbox));
	}
	
	@Command
	public void exportToExcelISCUSCmd(@BindingParam("listbox") Listbox listbox) {
		String filename = "seznam_ucastniku_iscus.xls";
		ExcelUtil.exportToExcel(filename, buildExcelISCUSRowData(listbox));
	}
	
	@Command
	public void exportToCSVISCUSCmd(@BindingParam("listbox") Listbox listbox) {
		String filename = "seznam_ucastniku_iscus.csv";
		CSVUtil.exportToCSV(filename, buildExcelISCUSRowData(listbox));
	}

	@NotifyChange("*")
	@Command
	public void refreshDataCmd() {
		loadData();
		filter.setEmptyValues();
	}

	protected void courseYearChangeCmdCore() {
		loadData();
	}
	
	@NotifyChange("*")
	@Command
	public void loadUnregisteredCurrYearCmd() {
		loadData();
	}

//	@NotifyChange("*")
//	@Command
//	public void updatePayedCmd(@BindingParam(WebConstants.CHECKED_PARAM) final Boolean checked
//			, @BindingParam(WebConstants.UUID_PARAM) final UUID uuid) {
//		if (checked == null) {
//			throw new IllegalArgumentException("checked is null");
//		}
//		if (uuid == null) {
//			throw new IllegalArgumentException("uuid is null");
//		}
//
//		try {
//			courseApplicationService.updatePayed(uuid, checked);
//			EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.RELOAD_COURSE_APPLICATION_DATA_EVENT, null, null);
//		} catch (ScbValidationException e) {
//			LOG.warn("ScbValidationException caught for application with uuid: " + uuid);
//			WebUtils.showNotificationError(e.getMessage());
//		}
//
//	}
	
	@Command
	public void sendnMailToUnregisteredFromPrevSeasonCmd() {
		if (CollectionUtils.isEmpty(this.courseApplicationList)) {
			return;
		}
		final List<CourseApplication> courseApplicationList = this.courseApplicationList;
		final String courseYearSelected = getCourseYearSelected();
		final Object[] msgParams = new Object[] {this.courseApplicationList.size()};
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.sendMailToUnregisteredParticipant",
			"msg.ui.title.sendMail",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					StringBuilder mailToUser = null;
					List<Mail> mailList = new ArrayList<>();
					for (CourseApplication courseApplication : courseApplicationList) {
						mailToUser = new StringBuilder();
						mailToUser.append(Labels.getLabel("msg.ui.mail.unregisteredToCurrSeason.text0"));
						mailToUser.append(WebConstants.LINE_SEPARATOR);
						mailToUser.append(WebConstants.LINE_SEPARATOR);
						mailToUser.append(Labels.getLabel("msg.ui.mail.unregisteredToCurrSeason.text1", new Object[] {courseApplication.getCourseParticipant().getContact().getCompleteName(), courseYearSelected}));
						mailToUser.append(WebConstants.LINE_SEPARATOR);
						mailToUser.append(WebConstants.LINE_SEPARATOR);
						mailToUser.append(Labels.getLabel("msg.ui.mail.unregisteredToCurrSeason.text2", new Object[] {configurationService.getBaseURL(), courseApplication.getCourseParticipant().getUuid(), courseApplication.getCourseParticRepresentative().getUuid()}));
						mailToUser.append(WebConstants.LINE_SEPARATOR);
						mailToUser.append(WebConstants.LINE_SEPARATOR);
						mailToUser.append(Labels.getLabel("msg.ui.mail.unregisteredToCurrSeason.text3"));
						mailToUser.append(WebConstants.LINE_SEPARATOR);
						mailToUser.append(WebConstants.LINE_SEPARATOR);
						mailToUser.append(buildMailSignature());						
						mailList.add(new Mail(courseApplication.getCourseParticRepresentative().getContact().getEmail1(), null, Labels.getLabel("msg.ui.mail.unregisteredToCurrSeason.subject", new Object[] {courseYearSelected}), mailToUser.toString(), null));	
					}
					
					mailService.sendMailBatch(mailList);
					WebUtils.showNotificationInfo("Obeslání uživatelů úspěšně dokončeno.");
				}
			},
			msgParams
		);
	}
	
	/**
	 * Otevre stranku pro odeslani emailu na emailove adresy vybranych ucastniku.
	 */
	@Command
	public void goToSendEmailCmd() {
		goToSendEmailCore(buildCourseParticipantContactSet(this.courseApplicationList));
	}
	
	/**
	 * Odesle email s prihlaskou na vybrane ucastniky.
	 */
	@Command
	public void sendCourseApplicationEmailCmd() {
		if (CollectionUtils.isEmpty(this.courseApplicationList)) {
			return;
		}
		
		final List<CourseApplication> courseApplicationListToSend = this.courseApplicationList;
		
		// dotaz
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.sendMailCourseApplication",
			"msg.ui.title.sendMail",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					List<Mail> mailToSendList = new ArrayList<>();
					
					for (CourseApplication item : courseApplicationListToSend) {
						// potvrzujici mailu o prihlasce course participant representative
						mailToSendList.add(buildMailCourseParticRepresentative(item, getNewCourseApplicationTitle()));
						// potvrzujici mail o prihlasce klubu
						mailToSendList.add(buildMailToClub(item));
						if (!item.isCurrentParticipant()) {
							ScbUser user = scbUserService.getByUsername(item.getCourseParticRepresentative().getContact().getEmail1());
							if (user != null) {
								// potvrzujiciho maila o zalozeni uzivatele
								mailToSendList.add(buildMailToNewUser(user));
							}
						}
					}
					
					// odeslani emailu
					mailService.sendMailBatch(mailToSendList);
					
					WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.courseApplicationMailSent"));
				}
			}
		);
	}
	
	/**
	 * Sestavi seznam emailovych adres ucastniku.
	 * @param courseApplicationList
	 * @return
	 */
	private Set<Contact> buildCourseParticipantContactSet(List<CourseApplication> courseApplicationList) {
		if (CollectionUtils.isEmpty(courseApplicationList)) {
			return Collections.emptySet();
		}
		
		Set<Contact> ret = new HashSet<>();
		for (CourseApplication item : courseApplicationList) {
			if (item.getCourseParticRepresentative() != null 
					&& item.getCourseParticRepresentative().getContact() != null
					&& StringUtils.hasText(item.getCourseParticRepresentative().getContact().getEmail1())) {
				ret.add(item.getCourseParticRepresentative().getContact());				
			}
		}
		return ret;
	}
	
	@NotifyChange("courseType")
	@Command
	public void courseTypeChangeCmd(@BindingParam("radio") Radio radio) {
		this.courseType = radio.getValue();
		updateCourseTypeCache(this.courseType);
		loadData();
	}

	/**
	 * Na emailove adresy zastupcu ybranych ucastniku odesle instrukce k platbe za dany kurz.
	 */
	@Command
	public void sendPaymentInstructionCmd(@BindingParam("firstSemester") final boolean firstSemester) {
		if (CollectionUtils.isEmpty(this.courseApplicationList)) {
			return;
		}
		
		// validace, ze v konfiguraci je vyplneno cislo bankovniho uctu
		final String bankAccountNumber = configurationService.getBankAccountNumber();
		if (!StringUtils.hasText(bankAccountNumber)) {
			WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.noBankAccountInConfig"));
			return;
		}
		
		String[] years = getYearsFromContext();
		final String yearFromTo = years[0] + "/" + years[1];
		
		Map<String, Object> args = new HashMap<>();
		args.put(WebConstants.COURSE_APPLICATION_LIST_PARAM, this.courseApplicationList);
		args.put(WebConstants.YEAR_FROM_PARAM, Integer.parseInt(years[0]));
		args.put(WebConstants.SEMESTER_PARAM, firstSemester);
		args.put(WebConstants.BANK_ACCOUNT_NO_PARAM, bankAccountNumber);
		args.put(WebConstants.YEAR_FROM_TO_PARAM, yearFromTo);
		args.put(WebConstants.COURSE_TYPE_PARAM, this.courseType);
		
		WebUtils.openModal(WebPages.PAYMENT_INSTRUCTION_WINDOW.getUrl(), null, args);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object[]> buildExcelRowData(@BindingParam("listbox") Listbox listbox) {
		Map<String, Object[]> data = new LinkedHashMap<String, Object[]>();

		DateFormat dateFormat = new SimpleDateFormat(WebConstants.WEB_DATETIME_PATTERN);

		// header
		Listhead lh = listbox.getListhead();
		Object[] headerArray = new Object[lh.getChildren().size() + 12];
		for (int i = 0; i < lh.getChildren().size(); i++) {
			headerArray[i] = ((Listheader) lh.getChildren().get(i)).getLabel();
		}
		
		String currency = " " + Labels.getLabel("txt.ui.common.CZK");
		
		if (this.pageMode == PageMode.COURSE_APPLICATION_LIST)  {
			headerArray[lh.getChildren().size()-1] = Labels.getLabel("txt.ui.common.birthNumber");
			headerArray[lh.getChildren().size()] = Labels.getLabel("txt.ui.common.phone");
			headerArray[lh.getChildren().size()+1] = Labels.getLabel("txt.ui.common.email");
			headerArray[lh.getChildren().size()+2] = Labels.getLabel("txt.ui.common.street");
			headerArray[lh.getChildren().size()+3] = Labels.getLabel("txt.ui.common.landRegNo");
			headerArray[lh.getChildren().size()+4] = Labels.getLabel("txt.ui.common.houseNo");
			headerArray[lh.getChildren().size()+5] = Labels.getLabel("txt.ui.common.city");
			headerArray[lh.getChildren().size()+6] = Labels.getLabel("txt.ui.common.zipCode");
			headerArray[lh.getChildren().size()+7] = Labels.getLabel("txt.ui.common.course");	
			headerArray[lh.getChildren().size()+8] = Labels.getLabel("txt.ui.common.courseLocation2");
		} else {
			headerArray[lh.getChildren().size()-1] = Labels.getLabel("txt.ui.common.payed") + currency;	
			headerArray[lh.getChildren().size()] = Labels.getLabel("txt.ui.common.PriceTotal") + currency;
			headerArray[lh.getChildren().size()+1] = Labels.getLabel("txt.ui.common.phone");
			headerArray[lh.getChildren().size()+2] = Labels.getLabel("txt.ui.common.email");
			headerArray[lh.getChildren().size()+3] = Labels.getLabel("txt.ui.common.street");
			headerArray[lh.getChildren().size()+4] = Labels.getLabel("txt.ui.common.landRegNo");
			headerArray[lh.getChildren().size()+5] = Labels.getLabel("txt.ui.common.houseNo");
			headerArray[lh.getChildren().size()+6] = Labels.getLabel("txt.ui.common.city");
			headerArray[lh.getChildren().size()+7] = Labels.getLabel("txt.ui.common.zipCode");	
		}
		data.put("0", headerArray);

		
		// rows
		ListModel<Object> model = listbox.getListModel();
		CourseApplication item = null;
		Course course = null;
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i) instanceof CourseApplication) {
				item = (CourseApplication)model.getElementAt(i);
				course = getCourseByCourseParticipant(item.getCourseParticipant());
				if (this.pageMode == PageMode.COURSE_APPLICATION_LIST) {
					data.put(String.valueOf(i+1),
						new Object[] { item.getCourseParticipant().getContact().getCompleteName(),
								getDateConverter().coerceToUi(item.getCourseParticipant().getBirthdate(), null, null),
								item.getCourseParticRepresentative().getContact().getCompleteName(),								
								dateFormat.format(item.getModifAt()),
								item.getCourseParticipant().getInCourseInfo(),
								!item.isCurrentParticipant() ? Labels.getLabel("txt.ui.common.yes") : Labels.getLabel("txt.ui.common.no"),
								item.getCourseParticipant().getPersonalNo().replace("/", ""),
								item.getCourseParticRepresentative().getContact().getPhone1(),
								item.getCourseParticRepresentative().getContact().getEmail1(),								
								getNotNullString(item.getCourseParticipant().getContact().getStreet()),
								getNotNullLong(item.getCourseParticipant().getContact().getLandRegistryNumber()),
								getNotNullString(item.getCourseParticipant().getContact().getHouseNumber()),
								getNotNullString(item.getCourseParticipant().getContact().getCity()),
								getNotNullString(item.getCourseParticipant().getContact().getZipCode()),
								course != null ? course.getName() : "",
								course != null && course.getCourseLocation() != null ? course.getCourseLocation().getName() : "",
					});
				} else {
					data.put(String.valueOf(i+1),
						new Object[] { item.getCourseParticipant().getContact().getCompleteName(),
								item.getCourseParticipant().getCourseName(),
								buildPaymentNotifiedInfo(item.getCourseParticipant()),
								item.getCourseParticipant().getCoursePaymentVO() != null ? (item.getCourseParticipant().getCoursePaymentVO().isOverpayed() ? Labels.getLabel("enum.CoursePaymentState.OVERPAYED") : getEnumLabelConverter().coerceToUi(item.getCourseParticipant().getCoursePaymentVO().getStateTotal(), null, null)) : null,
								item.getCourseParticipant().getCoursePaymentVO() != null ? item.getCourseParticipant().getCoursePaymentVO().getPaymentSum() : 0,
								item.getCourseParticipant().getCoursePaymentVO() != null ? item.getCourseParticipant().getCoursePaymentVO().getTotalPrice() : 0,
								item.getCourseParticRepresentative().getContact().getPhone1(),
								item.getCourseParticRepresentative().getContact().getEmail1(),	
								getNotNullStringEmptyChar(item.getCourseParticipant().getContact().getStreet()),
								getNotNullLongEmptyChar(item.getCourseParticipant().getContact().getLandRegistryNumber()),
								getNotNullStringEmptyChar(item.getCourseParticipant().getContact().getHouseNumber()),
								getNotNullStringEmptyChar(item.getCourseParticipant().getContact().getCity()),
								getNotNullStringEmptyChar(item.getCourseParticipant().getContact().getZipCode())
								});
				}
			}
		}

		return data;
	}
	
	private Course getCourseByCourseParticipant(CourseParticipant cp) {
		if (!CollectionUtils.isEmpty(cp.getCourseList())) {
			Course firstCourse = cp.getCourseList().get(0);
			if (firstCourse != null) {
				return firstCourse;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object[]> buildExcelNotValidAddrRowData(@BindingParam("listbox") Listbox listbox) {
		Map<String, Object[]> data = new LinkedHashMap<String, Object[]>();
		
		DateFormat dateFormat = new SimpleDateFormat(WebConstants.WEB_DATETIME_PATTERN);

		// header
		Listhead lh = listbox.getListhead();
		Object[] headerArray = new Object[lh.getChildren().size() + 12];
		for (int i = 0; i < lh.getChildren().size(); i++) {
			headerArray[i] = ((Listheader) lh.getChildren().get(i)).getLabel();
		}
		
		String currency = " " + Labels.getLabel("txt.ui.common.CZK");
		
		if (this.pageMode == PageMode.COURSE_APPLICATION_LIST)  {
			headerArray[lh.getChildren().size()-1] = Labels.getLabel("txt.ui.common.birthNumber");
			headerArray[lh.getChildren().size()] = Labels.getLabel("txt.ui.common.phone");
			headerArray[lh.getChildren().size()+1] = Labels.getLabel("txt.ui.common.email");
			headerArray[lh.getChildren().size()+2] = Labels.getLabel("txt.ui.common.street");
			headerArray[lh.getChildren().size()+3] = Labels.getLabel("txt.ui.common.landRegNo");
			headerArray[lh.getChildren().size()+4] = Labels.getLabel("txt.ui.common.houseNo");
			headerArray[lh.getChildren().size()+5] = Labels.getLabel("txt.ui.common.city");
			headerArray[lh.getChildren().size()+6] = Labels.getLabel("txt.ui.common.zipCode");
			headerArray[lh.getChildren().size()+7] = Labels.getLabel("txt.ui.common.course");	
			headerArray[lh.getChildren().size()+8] = Labels.getLabel("txt.ui.common.courseLocation2");
		} else {
			headerArray[lh.getChildren().size()-1] = Labels.getLabel("txt.ui.common.payed") + currency;	
			headerArray[lh.getChildren().size()] = Labels.getLabel("txt.ui.common.PriceTotal") + currency;
			headerArray[lh.getChildren().size()+1] = Labels.getLabel("txt.ui.common.phone");
			headerArray[lh.getChildren().size()+2] = Labels.getLabel("txt.ui.common.email");
			headerArray[lh.getChildren().size()+3] = Labels.getLabel("txt.ui.common.street");
			headerArray[lh.getChildren().size()+4] = Labels.getLabel("txt.ui.common.landRegNo");
			headerArray[lh.getChildren().size()+5] = Labels.getLabel("txt.ui.common.houseNo");
			headerArray[lh.getChildren().size()+6] = Labels.getLabel("txt.ui.common.city");
			headerArray[lh.getChildren().size()+7] = Labels.getLabel("txt.ui.common.zipCode");	
		}
		data.put("0", headerArray);

		
		// rows
		ListModel<Object> model = listbox.getListModel();
		CourseApplication item = null;
		Course course = null;
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i) instanceof CourseApplication) {
				item = (CourseApplication)model.getElementAt(i);
				course = getCourseByCourseParticipant(item.getCourseParticipant());
				if (this.pageMode == PageMode.COURSE_APPLICATION_LIST) {
					if (!item.getCourseParticipant().getContact().isAddressValid()) {
						data.put(String.valueOf(i+1),
								new Object[] { item.getCourseParticipant().getContact().getCompleteName(),
										getDateConverter().coerceToUi(item.getCourseParticipant().getBirthdate(), null, null),
										item.getCourseParticRepresentative().getContact().getCompleteName(),								
										dateFormat.format(item.getModifAt()),
										item.getCourseParticipant().getInCourseInfo(),
										!item.isCurrentParticipant() ? Labels.getLabel("txt.ui.common.yes") : Labels.getLabel("txt.ui.common.no"),
												item.getCourseParticipant().getPersonalNo().replace("/", ""),
												item.getCourseParticRepresentative().getContact().getPhone1(),
												item.getCourseParticRepresentative().getContact().getEmail1(),								
												getNotNullString(item.getCourseParticipant().getContact().getStreet()),
												getNotNullLong(item.getCourseParticipant().getContact().getLandRegistryNumber()),
												getNotNullString(item.getCourseParticipant().getContact().getHouseNumber()),
												getNotNullString(item.getCourseParticipant().getContact().getCity()),
												getNotNullString(item.getCourseParticipant().getContact().getZipCode()),
												course != null ? course.getName() : "",
														course != null && course.getCourseLocation() != null ? course.getCourseLocation().getName() : "",
						});						
					}
				} else {
					if (!item.getCourseParticipant().getContact().isAddressValid()) {
						data.put(String.valueOf(i+1),
								new Object[] { item.getCourseParticipant().getContact().getCompleteName(),
										item.getCourseParticipant().getCourseName(),
										buildPaymentNotifiedInfo(item.getCourseParticipant()),
										item.getCourseParticipant().getCoursePaymentVO() != null ? (item.getCourseParticipant().getCoursePaymentVO().isOverpayed() ? Labels.getLabel("enum.CoursePaymentState.OVERPAYED") : getEnumLabelConverter().coerceToUi(item.getCourseParticipant().getCoursePaymentVO().getStateTotal(), null, null)) : null,
												item.getCourseParticipant().getCoursePaymentVO() != null ? item.getCourseParticipant().getCoursePaymentVO().getPaymentSum() : 0,
														item.getCourseParticipant().getCoursePaymentVO() != null ? item.getCourseParticipant().getCoursePaymentVO().getTotalPrice() : 0,
																item.getCourseParticRepresentative().getContact().getPhone1(),
																item.getCourseParticRepresentative().getContact().getEmail1(),	
																getNotNullStringEmptyChar(item.getCourseParticipant().getContact().getStreet()),
																getNotNullLongEmptyChar(item.getCourseParticipant().getContact().getLandRegistryNumber()),
																getNotNullStringEmptyChar(item.getCourseParticipant().getContact().getHouseNumber()),
																getNotNullStringEmptyChar(item.getCourseParticipant().getContact().getCity()),
																getNotNullStringEmptyChar(item.getCourseParticipant().getContact().getZipCode())
						});						
					}
				}
			}
		}

		return data;
	}
	
	private Map<String, Object[]> buildExcelISCUSRowData(@BindingParam("listbox") Listbox listbox) {
		Map<String, Object[]> data = new LinkedHashMap<>();

		DateFormat dateFormat = new SimpleDateFormat(WebConstants.WEB_DATE_ISCUS_PATTERN);

		// header
		Object[] headerArray = new Object[19];
		headerArray[0] = Labels.getLabel("txt.ui.common.surname");
		headerArray[1] = Labels.getLabel("txt.ui.common.firstname");
		headerArray[2] = Labels.getLabel("txt.ui.common.CzechCitizen");
		headerArray[3] = Labels.getLabel("txt.ui.common.birthNumber");
		headerArray[4] = Labels.getLabel("txt.ui.common.birthDate");
		headerArray[5] = Labels.getLabel("txt.ui.common.Sex");
		headerArray[6] = Labels.getLabel("txt.ui.iscus.particRole");
		headerArray[7] = Labels.getLabel("txt.ui.iscus.IsTrainer");
		headerArray[8] = Labels.getLabel("txt.ui.iscus.IsReferee");
		headerArray[9] = Labels.getLabel("txt.ui.common.street");
		headerArray[10] = Labels.getLabel("txt.ui.common.landRegNo.abbr");
		headerArray[11] = Labels.getLabel("txt.ui.common.houseNo.abbr");
		headerArray[12] = Labels.getLabel("txt.ui.common.city");
		headerArray[13] = Labels.getLabel("txt.ui.common.zipCode");
		headerArray[14] = Labels.getLabel("txt.ui.common.phone");
		headerArray[15] = Labels.getLabel("txt.ui.common.email");
		headerArray[16] = Labels.getLabel("txt.ui.iscus.particId");
		headerArray[17] = Labels.getLabel("txt.ui.iscus.ClubLabels");
		headerArray[17] = Labels.getLabel("txt.ui.iscus.systemIdNotEdit");
		
		data.put("0", headerArray);

		// rows
		ListModel<Object> model = listbox.getListModel();
		CourseApplication item = null;
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i) instanceof CourseApplication) {
				item = (CourseApplication)model.getElementAt(i);
				data.put(item.getCourseParticipant().getPersonalNo(),
						new Object[] {
								item.getCourseParticipant().getContact().getSurname(),
								item.getCourseParticipant().getContact().getFirstname(),
								item.getCourseParticipant().getContact().isCzechCitizenship() ? "1" : "0",
								item.getCourseParticipant().getPersonalNo(),
								item.getCourseParticipant().getBirthdate() != null ? dateFormat.format(item.getCourseParticipant().getBirthdate()) : "",
								item.getCourseParticipant().getContact().isSexMale() ? "M" : "Z",
								item.getCourseParticipant().getIscusRole() != null ? item.getCourseParticipant().getIscusRole().getAbbr() : "",
								"0",
								"0",
								getNotNullStringEmptyChar(item.getCourseParticipant().getContact().getStreet()),
								getNotNullLongEmptyChar(item.getCourseParticipant().getContact().getLandRegistryNumber()),
								getNotNullStringEmptyChar(item.getCourseParticipant().getContact().getHouseNumber()),
								getNotNullStringEmptyChar(item.getCourseParticipant().getContact().getCity()),
								getNotNullStringEmptyChar(item.getCourseParticipant().getContact().getZipCode()),
								item.getCourseParticRepresentative().getContact().getPhone1(),
								item.getCourseParticRepresentative().getContact().getEmail1(),
								item.getCourseParticipant().getIscusParticId(),
								"",
								item.getCourseParticipant().getIscusSystemId()});
			}
		}

		return data;
	}
	
	/**
	 * Sestavi text s informaci o odeslani instrukci k platbe za prvni a druhe pololeti.
	 * @param cp
	 * @return
	 */
	private String buildPaymentNotifiedInfo(CourseParticipant cp) {
		if (cp == null) {
			return null;
		}
		StringBuilder ret = new StringBuilder();
		if (cp.getNotifiedSemester1PaymentAt() != null) {
			ret.append(Labels.getLabel("txt.ui.common.yes"));
		} else {
			ret.append(Labels.getLabel("txt.ui.common.no"));
		}
		ret.append(", ");
		if (cp.getNotifiedSemester2PaymentAt() != null) {
			ret.append(Labels.getLabel("txt.ui.common.yes"));
		} else {
			ret.append(Labels.getLabel("txt.ui.common.no"));
		}

		return ret.toString();
	}

	@SuppressWarnings("unchecked")
	public void loadData() {
		String[] years = getYearsFromContext();
		
		int yearFrom = Integer.parseInt(years[0]);
		int yearTo = Integer.parseInt(years[1]);
		this.yearFrom = yearFrom;
		
		this.unregToCurrYearLabel = Labels.getLabel("txt.ui.common.unregisteredFrom")+" "+String.valueOf(yearFrom-1)+"/"+String.valueOf(yearTo-1);

		if (this.unregToCurrYear) {
			this.courseApplicationList = (this.pageMode == PageMode.COURSE_APPLICATION_LIST) ? courseApplicationService.getUnregisteredToCurrYear(yearFrom, yearTo) : Collections.EMPTY_LIST;
		} else {
			this.courseApplicationList = (this.pageMode == PageMode.COURSE_APPLICATION_LIST) ? courseApplicationService.getAll(yearFrom, yearTo) : courseApplicationService.getAssignedToCourse(yearFrom, yearTo, this.courseType);
		}
		this.courseApplicationListBase = this.courseApplicationList;

		BindUtils.postNotifyChange(null, null, this, "courseApplicationList");
	}
	
	@DependsOn("courseType")
	public boolean isCourseStandard() {
		return this.courseType == CourseType.STANDARD;
	}

	public List<CourseApplication> getCourseApplicationList() {
		return courseApplicationList;
	}

	public CourseApplicationFilter getFilter() {
		return filter;
	}

	public void setFilter(CourseApplicationFilter filter) {
		this.filter = filter;
	}

	public boolean isUnregToCurrYear() {
		return unregToCurrYear;
	}

	public void setUnregToCurrYear(boolean unregToCurrYear) {
		this.unregToCurrYear = unregToCurrYear;
	}
	
	public String getUnregToCurrYearLabel() {
		return unregToCurrYearLabel;
	}
	
	public List<Listitem> getCoursePaymentStateListWithEmptyItem() {
		return coursePaymentStateListWithEmptyItem;
	}
	
	public List<Listitem> getPaymentNotifStateListWithEmptyItem() {
		return paymentNotifStateListWithEmptyItem;
	}
	
	public String getBankAccountNumber() {
		return bankAccountNumber;
	}
	
	public int getYearFrom() {
		return yearFrom;
	}
	
	public CourseType getCourseType() {
		return courseType;
	}

	public void setCourseType(CourseType courseType) {
		this.courseType = courseType;
	}
}
