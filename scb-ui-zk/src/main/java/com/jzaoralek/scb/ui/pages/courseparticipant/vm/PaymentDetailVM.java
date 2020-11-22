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
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.Payment;
import com.jzaoralek.scb.dataservice.domain.Payment.PaymentProcessType;
import com.jzaoralek.scb.dataservice.domain.Payment.PaymentType;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class PaymentDetailVM extends BaseVM {

	private final List<Listitem> paymentTypeList = WebUtils.getMessageItemsFromEnum(EnumSet.allOf(PaymentType.class));;
	
	private Payment payment;
	private Listitem paymentType;
	

	@Init
	public void init() {
		UUID paymentUuid = (UUID) WebUtils.getArg(WebConstants.UUID_PARAM);
		boolean newMode = (paymentUuid == null);
		if (newMode) {
			CourseParticipant courseParticipant = (CourseParticipant) WebUtils.getArg(WebConstants.COURSE_PARTIC_PARAM);
			Course course = (Course) WebUtils.getArg(WebConstants.COURSE_PARAM);
			Objects.requireNonNull(courseParticipant, "courseParticipant");
			Objects.requireNonNull(course, "course");
			
			this.payment = new Payment();
			this.payment.setProcessType(PaymentProcessType.MANUAL);
			this.payment.setPaymentDate(Calendar.getInstance().getTime());
			this.payment.setCourseParticipant(courseParticipant);
			this.payment.setCourse(course);
		} else {
			this.payment = paymentService.getByUuid(paymentUuid);
			this.paymentType = getPaymentTypeListItem(this.payment.getType());
		}
	}
	
	public boolean isPaymentTypeAutomatic() {
		return this.payment != null && (EnumSet.of(PaymentProcessType.AUTOMATIC, PaymentProcessType.PAIRED).contains(this.payment.getProcessType()));
	}
	
	private Listitem getPaymentTypeListItem(PaymentType role) {
		if (role == null) {
			return null;
		}

		for (Listitem item : this.paymentTypeList) {
			if (item.getValue() == role) {
				return item;
			}
		}

		return null;
	}
	
	@Command
	public void submitCmd(@BindingParam("window") Window window) {
		this.payment.setType((PaymentType)this.paymentType.getValue());
		paymentService.store(this.payment);
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.changesSaved"));
		EventQueueHelper.publish(ScbEvent.RELOAD_PAYMENT_DATA_EVENT, null);
		window.detach();
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
	
	public List<Listitem> getPaymentTypeList() {
		return paymentTypeList;
	}
}