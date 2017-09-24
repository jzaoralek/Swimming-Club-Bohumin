package com.jzaoralek.scb.ui.pages.courseparticipant.vm;

import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.Payment;
import com.jzaoralek.scb.dataservice.domain.Payment.PaymentType;
import com.jzaoralek.scb.dataservice.service.PaymentService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class PaymentDetailVM extends BaseVM {

	@WireVariable
	private PaymentService paymentService;
	
	private Payment payment;
	private Listitem paymentType;

	@Init
	public void init() {
		// TODO, predat caalbact v argumentu
		UUID paymentUuid = (UUID) WebUtils.getArg(WebConstants.UUID_PARAM);
		
		boolean newMode = (paymentUuid == null);
		if (newMode) {
			UUID courseCourseParticipantUuid = (UUID) WebUtils.getArg(WebConstants.COURSE_COURSE_PARTIC_UUID_PARAM);
			Objects.requireNonNull(courseCourseParticipantUuid, "courseCourseParticipantUuid");
			this.payment = new Payment();
			this.payment.setPaymentDate(Calendar.getInstance().getTime());
			this.payment.setCourseCourseParticipantUuid(courseCourseParticipantUuid);
		} else {
			this.payment = paymentService.getByUuid(paymentUuid);
		}
	}
	
	@Command
	public void submitCmd(@BindingParam("window") Window window) {
		this.payment.setType((PaymentType)this.paymentType.getValue());
		paymentService.insert(this.payment);
		window.detach();
	}
	
	public List<Listitem> getPaymentTypeList() {
		return WebUtils.getMessageItemsFromEnum(EnumSet.allOf(PaymentType.class));
	}
	
	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	
	public Listitem getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(Listitem paymentType) {
		this.paymentType = paymentType;
	}
}