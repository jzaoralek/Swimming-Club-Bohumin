package com.jzaoralek.scb.ui.pages.configuration.vm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.CodeListItem;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CodeListService;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CodeListItemWinVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(CodeListItemWinVM.class);

	@WireVariable
	private CodeListService codeListService;

	private CodeListItem codeListItem;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Init
	public void init() {
		final EventQueue eq = EventQueues.lookup(ScbEventQueues.CODE_LIST_QUEUE.name() , EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.CODELIST_NEW_DATA_EVENT.name())) {
					initData((CodeListItem)event.getData());
					eq.unsubscribe(this);
				}
				if (event.getName().equals(ScbEvent.CODELIST_DETAIL_DATA_EVENT.name())) {
					initData((CodeListItem)event.getData());
					eq.unsubscribe(this);
				}
			}
		});
	}

	@Command
	public void submitCmd(@BindingParam("window") Window window) {
		try {
			// ulozit do databaze
			codeListService.store(this.codeListItem);
			EventQueueHelper.publish(ScbEventQueues.CODE_LIST_QUEUE, ScbEvent.RELOAD_CODELIST_DATA_EVENT, null, null);
			window.detach();
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught during storing lcodeListItem: " + this.codeListItem, e);
			WebUtils.showNotificationError(e.getMessage());
		}
	}

	private void initData(CodeListItem codeListItem) {
		if (codeListItem == null) {
			throw new IllegalArgumentException("codeListItem is null");
		}

		this.codeListItem = codeListItem;
		BindUtils.postNotifyChange(null, null, this, "codeListItem");
	}

	public CodeListItem getCodeListItem() {
		return codeListItem;
	}

	public void setCodeListItem(CodeListItem codeListItem) {
		this.codeListItem = codeListItem;
	}
}
