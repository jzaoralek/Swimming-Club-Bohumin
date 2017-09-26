package com.jzaoralek.scb.ui.pages.courseparticipant.vm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.Payment;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.PaymentService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.DateUtil;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
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
		// TODO: odstranit vstupni argumenty yearFrom a yearTo
		// TODO: nacist ucastnka a kurz na zaklade courseCoourseParticUuid a doplnit do labelu
		this.pageHeadline = Labels.getLabel("txt.ui.common.ParticipantPayments", new String[]{"ucastnik", "kurz"});
		this.courseCoourseParticUuid = UUID.fromString(courseCoourseParticUuid);
		setReturnPage(fromPage);
		
		final EventQueue eq = EventQueues.lookup(ScbEventQueues.PAYMENT_QUEUE.name() , EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.RELOAD_PAYMENT_DATA_EVENT.name())) {
					loadData();
				}
			}
		});
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
							EventQueueHelper.publish(ScbEventQueues.PAYMENT_QUEUE, ScbEvent.RELOAD_PAYMENT_DATA_EVENT, null, null);
						}
						
					}
				},
				msgParams
			);
		
		
	}
	
	public void loadData() {
		this.paymentList = paymentService.getByCourseCourseParticipantUuid(this.courseCoourseParticUuid, null, null);
		BindUtils.postNotifyChange(null, null, this, "paymentList");
	}
	
	public List<Payment> getPaymentList() {
		return paymentList;
	}
	
	public String getPageHeadline() {
		return pageHeadline;
	}
}
