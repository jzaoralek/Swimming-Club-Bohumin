package com.jzaoralek.scb.ui.pages.courseparticipant.vm;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;

import com.jzaoralek.scb.dataservice.datasource.ClientDatabaseContextHolder;
import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.CoursePaymentVO;
import com.jzaoralek.scb.dataservice.domain.Payment;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.template.SideMenuComposer.ScbMenuItem;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.ExcelUtil;
import com.jzaoralek.scb.ui.common.utils.JasperUtil;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

/**
 * VM for course participant payment list.
 * @author jakub.zaoralek
 *
 */
public class PaymentListVM extends BaseVM {

	private static final String PAYMENT_DETAIL_WINDOW = "/pages/secured/TRAINER/detail-platby.zul";
	private static final String MANUAL_PAYMENT_PAIR_WINDOW = "/pages/secured/ADMIN/seznam-bank-plateb-nezpar-window.zul";
	
	@WireVariable
	private CourseService courseService;
	
	private List<Payment> paymentList;
	private UUID courseParticUuid;
	private UUID courseUuid;
	private Course course;
	private CourseParticipant coursePartic;
	private long paymentSum;
	private CoursePaymentVO coursePaymentVO;
	private ScbUser courseParticRepresentative;

	@SuppressWarnings({ "unchecked" })
	@Init
	public void init(@QueryParam(WebConstants.COURSE_PARTIC_UUID_PARAM) String courseParticUuid
			, @QueryParam(WebConstants.COURSE_UUID_PARAM) String courseUuid
			, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage) {
		
		setReturnToUrl();
        
		// single page mode
		if (StringUtils.hasText(courseParticUuid) 
				&& StringUtils.hasText(courseUuid) 
				&& (StringUtils.hasText(fromPage) || StringUtils.hasText(returnToUrl))) {
			initSinglePageMode(courseParticUuid, courseUuid, fromPage);			
		}
		
		// listener for nested page mode
		EventQueueHelper.queueLookup(ScbEventQueues.PAYMENT_QUEUE).subscribe(ScbEvent.RELOAD_COURSEPARTIC_PAYMENT_DATA_EVENT, data -> 
			initNestedPageMode((Pair<CourseParticipant,String>) data)
        );
		
		// listener for load data
		EventQueueHelper.queueLookup(ScbEventQueues.PAYMENT_QUEUE).subscribe(ScbEvent.RELOAD_PAYMENT_DATA_EVENT, data -> 
			loadData()
	    );
	}
	
	/**
	 * Init data for single page mode.
	 * @param courseParticUuid
	 * @param courseUuid
	 * @param fromPage
	 */
	private void initSinglePageMode(String courseParticUuid, 
			String courseUuid, 
			String fromPage) {
		// highlight side menu item by user role
		if (isLoggedUserInRole(ScbUserRole.USER.name())) {
			setMenuSelected(ScbMenuItem.SEZNAM_UCASTNIKU_U);			
		} else {
			setMenuSelected(ScbMenuItem.PLATBY);			
		}
		
		this.courseParticUuid = UUID.fromString(courseParticUuid);
		this.courseUuid = UUID.fromString(courseUuid);
		this.coursePartic = courseService.getCourseParticipantByUuid(this.courseParticUuid);
		this.courseParticRepresentative = scbUserService.getByUuid(this.coursePartic.getRepresentativeUuid());
		this.course = courseService.getPlainByUuid(this.courseUuid);
		this.pageHeadline = buildPageHeadline(this.coursePartic, this.course);
		loadData();
		setReturnPage(fromPage);
	}
	
	/**
	 * Init data for page as part or tab content of another page.
	 * @param courseParticUuid
	 * @param courseUuid
	 */
	private void initNestedPageMode(Pair<CourseParticipant,String> data) {
		this.courseParticUuid = data.getValue0().getUuid();
		this.coursePartic = data.getValue0();
		this.courseUuid = UUID.fromString(data.getValue1());
		this.course = courseService.getPlainByUuid(this.courseUuid);
		loadData();
		BindUtils.postNotifyChange(null, null, this, "courseParticUuid");
		BindUtils.postNotifyChange(null, null, this, "coursePartic");
		BindUtils.postNotifyChange(null, null, this, "courseUuid");
		BindUtils.postNotifyChange(null, null, this, "course");
	}
	
	@Command
	public void newItemCmd() {
		Map<String, Object> args = new HashMap<>();
		args.put(WebConstants.COURSE_PARTIC_PARAM, this.coursePartic);
		args.put(WebConstants.COURSE_PARAM, this.course);
		WebUtils.openModal(PAYMENT_DETAIL_WINDOW, Labels.getLabel("txt.ui.common.NewPayment"), args);
	}
	
	@Command
	public void detailCmd(@BindingParam(WebConstants.UUID_PARAM) UUID uuid) {
		Map<String, Object> args = new HashMap<>();
		args.put(WebConstants.UUID_PARAM, uuid);
		WebUtils.openModal(PAYMENT_DETAIL_WINDOW, Labels.getLabel("txt.ui.common.PaymentDetail"), args);
	}
	
	@Command
	public void pairPaymentManualCmd() {
		Map<String, Object> args = new HashMap<>();
		args.put(WebConstants.COURSE_PARTIC_PARAM, this.coursePartic);
		args.put(WebConstants.COURSE_PARAM, this.course);
		String modalTitle = Labels.getLabel("txt.ui.common.ZparovaniPlatbyUcastnika", new Object[] {this.coursePartic.getContact().getCompleteName() + ", " + this.coursePartic.getPersonalNo()});
		WebUtils.openModal(MANUAL_PAYMENT_PAIR_WINDOW, modalTitle, args);
	}
	
	@Command
	public void deleteCmd(@BindingParam(WebConstants.ITEM_PARAM) final Payment payment) {
		final Object[] msgParams = new Object[] {String.valueOf(payment.getAmount())};
		MessageBoxUtils.showDefaultConfirmDialog(
				"msg.ui.quest.deletePayment",
				"msg.ui.title.deleteRecord",
				new SzpEventListener() {
					@Override
					public void onOkEvent() {
						if (payment != null) {
							paymentService.delete(payment);
							WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.paymentDeleted", msgParams));
							EventQueueHelper.publish(ScbEvent.RELOAD_PAYMENT_DATA_EVENT, null);
						}
						
					}
				},
				msgParams
			);
	}
	
	@Command
	public void exportToExcel(@BindingParam("listbox") Listbox listbox) {
		ExcelUtil.exportToExcel("seznam_plateb.xls", buildExcelRowData(listbox));
	}
	
	@NotifyChange("*")
	@Command
	public void refreshDataCmd() {
		loadData();
	}
	
	/**
	 * Download payment confirmation for standard course.
	 */
	@Command
	public void paymentConfirmStandardDownloadCmd() {
		// check if course has been payed
		if (!isPayedTotal()) {
			return;
		}
		WebUtils.downloadAttachment(buildPaymentConfirmAttachment(this.coursePaymentVO.getPaymentSum(), 
																getLastPaymentDate()));
	}
	
	/**
	 * Send payment confirmation email for standard course.
	 */
	@Command
	public void paymentConfirmStandardSendCmd() {
		// check if course has been payed
		if (!isPayedTotal()) {
			return;
		}
		String mailTo = this.courseParticRepresentative.getContact().getEmail1();
		paymentService.sendPaymentConfirmation(mailTo,
											this.course,
											true,
											this.coursePartic,
											buildPaymentConfirmAttachment(this.coursePaymentVO.getPaymentSum(), 
																		getLastPaymentDate()), 
											ClientDatabaseContextHolder.getClientDatabase());
		
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.paymentConfirmSent", new String[] {mailTo}));
	}
	
	/**
	 * Download payment confirmation for two semester course.
	 */
	@Command
	public void paymentConfirmTwoSemesterDownloadCmd(@BindingParam("firstSemester")Boolean firstSemester) {
		Attachment attachment = buildPaymentConfirmTwoSemesterAttachment(firstSemester);
		if (attachment != null) {
			WebUtils.downloadAttachment(attachment);			
		}
	}
	
	/**
	 * Send payment confirmation email for two semester course.
	 */
	@Command
	public void paymentConfirmTwoSemesterSendCmd(@BindingParam("firstSemester")Boolean firstSemester) {
		String mailTo = this.courseParticRepresentative.getContact().getEmail1();
		paymentService.sendPaymentConfirmation(mailTo,
											this.course,
											firstSemester,
											this.coursePartic,
											buildPaymentConfirmTwoSemesterAttachment(firstSemester), 
											ClientDatabaseContextHolder.getClientDatabase());
		
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.paymentConfirmSent", new String[] {mailTo}));
	}
	
	/**
	 * Build paymemnt attachment for semester.
	 * @param firstSemester
	 * @return
	 */
	private Attachment buildPaymentConfirmTwoSemesterAttachment(boolean firstSemester) {
		long payment = 0;
		Date paymentDate = null;
		if (firstSemester && isFirstSemesterPayed()) {
			// first semester price
			payment = course.getPriceSemester1();
			// date of last payment for the first semester
			paymentDate = getAmountPaymentDate(course.getPriceSemester1());
		} else if (!firstSemester && isSecondSemesterPayed()) {
			// total payment sum minus first semester price
			payment = this.coursePaymentVO.getPaymentSum() - course.getPriceSemester1();
			// last payment date for sum of first and second semester
			paymentDate = getAmountPaymentDate(course.getPriceSemester1() + course.getPriceSemester2());
		}
		
		if (payment == 0 || paymentDate == null) {
			return null;
		}
		
		return buildPaymentConfirmAttachment(payment, paymentDate);
	}
	
	private Attachment buildPaymentConfirmAttachment(long paymentSum, Date paymentDate) {
		String title = Labels.getLabel("txt.ui.paymentConfirmReport.title");
		byte[] byteArray = JasperUtil.getPaymentConfirmation(this.course, 
															this.coursePartic, 
															this.courseParticRepresentative.getContact().getCompleteName(),
															paymentSum,
															paymentDate,
															title, 
															configurationService);
		
		StringBuilder fileName = new StringBuilder();
		fileName.append("potvrzeni-o-platbe");
		fileName.append("_" + this.coursePartic.getContact().getCompleteName());
		fileName.append(".pdf");
		
		Attachment attachment = new Attachment();
		attachment.setByteArray(byteArray);
		attachment.setContentType("application/pdf");
		attachment.setName(fileName.toString());
		
		return attachment;
	}
	
	public void loadData() {
		this.paymentList = paymentService.getByCourseCourseParticipantUuid(this.courseParticUuid, this.courseUuid, null, null);
		buildPaymentsState();
		BindUtils.postNotifyChange(null, null, this, "paymentList");
	}
	
	public String getSendToCourseParticReprMailAddressDesc() {
		if (this.courseParticRepresentative != null 
				&& this.courseParticRepresentative.getContact() != null
				&& StringUtils.hasText(this.courseParticRepresentative.getContact().getEmail1())) {
			return Labels.getLabel("txt.ui.common.PaymentConfirmSendToReprDesc", new Object[] {this.courseParticRepresentative.getContact().getEmail1()});
		}
		return null;
	}
	
	private String buildPageHeadline(CourseParticipant coursePartic, Course course) {
		return Labels.getLabel("txt.ui.common.ParticipantPayments"
				, new String[]{coursePartic.getContact().getCompleteName()
				, course.getName(), course.getYearFrom() + "/" + course.getYearTo()});
	}
	
	private void buildPaymentsState() {
		long paymentSum = 0;
		if (this.paymentList != null && !this.paymentList.isEmpty()) {
			for (Payment payment : this.paymentList) {
				paymentSum += payment.getAmount();
			}
		}
		this.paymentSum = paymentSum;
		if (this.coursePaymentVO == null) {
			this.coursePaymentVO = new CoursePaymentVO(this.paymentSum, course.getPriceSemester1(), course.getPriceSemester2());			
		} else {
			this.coursePaymentVO.rebuildCoursePaymentState(this.paymentSum);
		}
		
		BindUtils.postNotifyChange(null, null, this, "paymentSum");
		BindUtils.postNotifyChange(null, null, this, "coursePaymentVO");
	}
	
	private Map<String, Object[]> buildExcelRowData(@BindingParam("listbox") Listbox listbox) {
		Map<String, Object[]> data = new LinkedHashMap<>();

		// header
		Listhead lh = listbox.getListhead();
		Object[] headerArray = new Object[lh.getChildren().size()];
		for (int i = 0; i < lh.getChildren().size(); i++) {
			headerArray[i] = ((Listheader) lh.getChildren().get(i)).getLabel();
		}
		data.put("0", headerArray);

		// rows
		ListModel<Object> model = listbox.getListModel();
		Payment item = null;
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i) instanceof Payment) {
				item = (Payment)model.getElementAt(i);
				data.put(String.valueOf(i+1),
						new Object[] {getDateTimeConverter().coerceToUi(item.getPaymentDate(), null, null),
								getEnumLabelConverter().coerceToUi(item.getType(), null, null),
								getEnumLabelConverter().coerceToUi(item.getProcessType(), null, null),
								item.getAmount() + Labels.getLabel("txt.ui.common.CZK")});
			}
		}

		return data;
	}
	
	/**
	 * Vraci datum posledni platby.
	 * @return
	 */
	private Date getLastPaymentDate() {
		if (CollectionUtils.isEmpty(paymentList)) {
			return null;
		}
		
		Optional<Payment> lastPayment = paymentList.stream()
				.sorted(Comparator.comparing(Payment::getPaymentDate))
				.reduce((first, second) -> second);
		
		if (!lastPayment.isPresent()) {
			return null;
		}
		
		return lastPayment.get().getPaymentDate();
	}
	
	/**
	 * Vraci datum platby kdy byla dosazena castka.
	 * @return
	 */
	private Date getAmountPaymentDate(long amount) {
		if (CollectionUtils.isEmpty(paymentList)) {
			return null;
		}
		
		List<Payment> paymentSortedList = paymentList.stream()
				.sorted(Comparator.comparing(Payment::getPaymentDate))
				.collect(Collectors.toList());
		
		long paymentSum = 0;
		for (Payment payment : paymentSortedList) {
			paymentSum = paymentSum + payment.getAmount();
			if (paymentSum >= amount) {
				return payment.getPaymentDate();
			}
		}
		
		return null;
	}
	
	public List<Payment> getPaymentList() {
		return paymentList;
	}
	
	public long getPaymentSum() {
		return paymentSum;
	}
	
	public Course getCourse() {
		return course;
	}
	
	public CoursePaymentVO getCoursePaymentVO() {
		return coursePaymentVO;
	}
	
	public boolean isPayedTotal() {
		return coursePaymentVO.isPayed();
	}
	
	public boolean isCourseStandard() {
		return this.course.isCourseStandard();
	}
	
	public boolean isCourseTwoSemester() {
		return this.course.isCourseTwoSemester();
	}
	
	@DependsOn("paymentList")
	public boolean isFirstSemesterPayed() {
		return coursePaymentVO.isFirstSemesterPayed();
	}
	
	@DependsOn("paymentList")
	public boolean isSecondSemesterPayed() {
		return coursePaymentVO.isSecondSemesterPayed();
	}
}
