package com.jzaoralek.scb.ui.pages.configuration.vm;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.CodeListItem;
import com.jzaoralek.scb.dataservice.domain.CodeListItem.CodeListType;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CodeListService;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class SwimStyleAdminVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(SwimStyleAdminVM.class);

	private static final String CODE_LIST_ITEM_WINDOW= "/pages/secured/codelist-item-window.zul";

	@WireVariable
	private CodeListService codeListService;

	private List<CodeListItem> codeListItemList;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Init
	public void init() {
		loadData();

		final EventQueue eq = EventQueues.lookup(ScbEventQueues.CODE_LIST_QUEUE.name() , EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.RELOAD_CODELIST_DATA_EVENT.name())) {
					loadData();
				}
			}
		});
	}

	@NotifyChange("codeListItemList")
	@Command
	public void refreshDataCmd() {
		loadData();
	}

	@NotifyChange("codeListItemList")
	@Command
	public void deleteCmd(@BindingParam("uuid") final UUID uuid) {
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting codeListItem with uuid: " + uuid);
		}
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.deleteItem",
			"msg.ui.title.deleteRecord",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					try {
						codeListService.delete(uuid);
						EventQueueHelper.publish(ScbEventQueues.CODE_LIST_QUEUE, ScbEvent.RELOAD_CODELIST_DATA_EVENT, null, null);
						WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.itemDeleted"));
					} catch (ScbValidationException e) {
						LOG.warn("ScbValidationException caught for codeListItem with uuid: " + uuid, e);
						WebUtils.showNotificationError(e.getMessage());
					}
				}
			}
		);
	}

	@Command
	public void newItemCmd() {
		CodeListItem item = new CodeListItem();
		item.setType(CodeListType.SWIMMING_STYLE);
		EventQueueHelper.publish(ScbEventQueues.CODE_LIST_QUEUE, ScbEvent.CODELIST_NEW_DATA_EVENT, null, item);
		WebUtils.openModal(CODE_LIST_ITEM_WINDOW);
	}

	@Command
    public void detailCmd(@BindingParam("item") final CodeListItem item) {
		if (item == null) {
			throw new IllegalArgumentException("codeListItem is null");
		}
		EventQueueHelper.publish(ScbEventQueues.CODE_LIST_QUEUE, ScbEvent.CODELIST_DETAIL_DATA_EVENT, null, item);
		WebUtils.openModal(CODE_LIST_ITEM_WINDOW);
	}

	private void loadData() {
		this.codeListItemList = codeListService.getItemListByType(CodeListType.SWIMMING_STYLE);
		BindUtils.postNotifyChange(null, null, this, "codeListItemList");
	}

	public List<CodeListItem> getCodeListItemList() {
		return codeListItemList;
	}

	public void setCodeListItemList(List<CodeListItem> codeListItemList) {
		this.codeListItemList = codeListItemList;
	}
}