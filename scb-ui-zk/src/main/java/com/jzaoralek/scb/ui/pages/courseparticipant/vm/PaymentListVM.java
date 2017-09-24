package com.jzaoralek.scb.ui.pages.courseparticipant.vm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.Payment;
import com.jzaoralek.scb.dataservice.service.PaymentService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.DateUtil;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class PaymentListVM extends BaseVM {

	private static final String PAYMENT_DETAIL_WINDOW = "/pages/secured/TRAINER/detail-platby.zul";
	
	@WireVariable
	private PaymentService paymentService;
	
	private List<Payment> paymentList;
	private String pageHeadline;
	private UUID courseCoourseParticUuid;

	@Init
	public void init(@QueryParam(WebConstants.COURSE_COURSE_PARTIC_UUID_PARAM) String courseCoourseParticUuid
			, @QueryParam(WebConstants.YEAR_FROM_PARAM) String yearFrom
			, @QueryParam(WebConstants.YEAR_TO_PARAM) String yearTo
			, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage) {
		this.paymentList = paymentService.getByCourseCourseParticipantUuid(UUID.fromString(courseCoourseParticUuid)
				, DateUtil.getFirstDayOfFirstSemester(Integer.valueOf(yearFrom))
				, DateUtil.getLastDayOfSecondSemester(Integer.valueOf(yearTo)));
		// TODO: nacist ucastnka a kurz na zaklade courseCoourseParticUuid a doplnit do labelu
		this.pageHeadline = Labels.getLabel("txt.ui.common.ParticipantPayments", new String[]{"ucastnik", "kurz"});
		this.courseCoourseParticUuid = UUID.fromString(courseCoourseParticUuid);
		setReturnPage(fromPage);
	}
	
	@Command
	public void newItemCmd() {
		Map<String, Object> args = new HashMap<>();
		args.put(WebConstants.COURSE_COURSE_PARTIC_UUID_PARAM, this.courseCoourseParticUuid);
		WebUtils.openModal(PAYMENT_DETAIL_WINDOW, Labels.getLabel("txt.ui.common.NewPayment"), args);
	}
	
	@Command
	public void detailCmd(@BindingParam(WebConstants.UUID_PARAM) UUID uuid) {
		Map<String, Object> args = new HashMap<>();
		args.put(WebConstants.UUID_PARAM, uuid);
		WebUtils.openModal(PAYMENT_DETAIL_WINDOW, Labels.getLabel("txt.ui.common.PaymentDetail"), args);
	}
	
	public List<Payment> getPaymentList() {
		return paymentList;
	}
	
	public String getPageHeadline() {
		return pageHeadline;
	}
}
