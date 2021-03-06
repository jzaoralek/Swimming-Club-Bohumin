package com.jzaoralek.scb.ui.pages.payments.vm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.PaymentInstruction;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class PaymentInstructionWinVM extends BaseVM {

	@WireVariable
	private CourseApplicationService courseApplicationService;
	
	private List<PaymentInstruction> paymentInstructionList;
	private boolean firstSemester;
	private String bankAccountNumber;
	private String yearFromTo;
	private Date paymentDeadline;
	private String optionalText;
	private CourseType courseType;

	@Override
	@SuppressWarnings("unchecked")
	@Init
	public void init() {
		List<CourseApplication> courseApplicationList = (List<CourseApplication>) WebUtils.getArg(WebConstants.COURSE_APPLICATION_LIST_PARAM);
		int yearFrom = (int)WebUtils.getArg(WebConstants.YEAR_FROM_PARAM);
		this.firstSemester = (boolean)WebUtils.getArg(WebConstants.SEMESTER_PARAM);
		this.bankAccountNumber = (String)WebUtils.getArg(WebConstants.BANK_ACCOUNT_NO_PARAM);
		this.yearFromTo = (String)WebUtils.getArg(WebConstants.YEAR_FROM_TO_PARAM);
		this.courseType = (CourseType)WebUtils.getArg(WebConstants.COURSE_TYPE_PARAM);
		
		if (this.courseType == CourseType.STANDARD) {
			this.pageHeadline = Labels.getLabel("txt.ui.common.PaymentInstruction");
		} else {
			this.pageHeadline = this.firstSemester ? Labels.getLabel("txt.ui.common.PaymentInstructionsFirstSemester") : Labels.getLabel("txt.ui.common.PaymentInstructionsSecondSemester");			
		}

		initData(courseApplicationList, yearFrom, this.firstSemester, bankAccountNumber);
	}
	
	@Command
	public void submitCmd(@BindingParam("window") Window window) {
		if (CollectionUtils.isEmpty(this.paymentInstructionList)) {
			return;
		}
		
		paymentService.processPaymentInstruction(this.paymentInstructionList
				, this.yearFromTo
				, WebConstants.LINE_SEPARATOR
				, getDateConverter().coerceToUi(this.paymentDeadline, null, null)
				, this.optionalText
				, buildMailSignature()
				, this.firstSemester
				, this.courseType);
		
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.paymentInstructionSent"));
		EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.RELOAD_COURSE_APPLICATION_DATA_EVENT, null, null);
		window.detach();
	}
	
	private void initData(List<CourseApplication> courseApplicationList, int yearFrom, boolean firstSemester, String bankAccountNumber) {
		if (CollectionUtils.isEmpty(courseApplicationList)) {
			return;
		}
		
		this.paymentInstructionList = new ArrayList<>();
		for (CourseApplication courseApplication : courseApplicationList) {
			this.paymentInstructionList.add(
					PaymentInstruction.ofCourseApplication(courseApplication,
													this.courseType,
													yearFrom, 
													firstSemester, 
													bankAccountNumber));
		}
	}
	
	public List<PaymentInstruction> getPaymentInstructionList() {
		return paymentInstructionList;
	}
	public String getBankAccountNumber() {
		return bankAccountNumber;
	}
	public Date getPaymentDeadline() {
		return paymentDeadline;
	}
	public void setPaymentDeadline(Date paymentDeadline) {
		this.paymentDeadline = paymentDeadline;
	}
	public String getOptionalText() {
		return optionalText;
	}
	public void setOptionalText(String optionalText) {
		this.optionalText = optionalText;
	}
}