package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.CodeListItem;
import com.jzaoralek.scb.dataservice.domain.CodeListItem.CodeListType;
import com.jzaoralek.scb.dataservice.domain.Result;
import com.jzaoralek.scb.dataservice.service.CodeListService;
import com.jzaoralek.scb.dataservice.service.ResultService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class ResultWinVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(ResultWinVM.class);

	private final DateFormat timeFormat = new SimpleDateFormat(WebConstants.WEB_TIME_SECONDS_PATTERN);

	@WireVariable
	private ResultService resultService;

	@WireVariable
	private CodeListService codeListService;

	private Result result;
	private String resultTimeStr;
	private List<Listitem> swimStyleListitemList;
	private Listitem swimStyleListitemSelected;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Init
	public void init() {
		fillSwimStyleItemList();

		final EventQueue eq = EventQueues.lookup(ScbEventQueues.RESULT_QUEUE.name() , EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.RESULT_NEW_DATA_EVENT.name())) {
					initData((Result)event.getData());
					eq.unsubscribe(this);
				}
				if (event.getName().equals(ScbEvent.RESULT_DETAIL_DATA_EVENT.name())) {
					initData((Result)event.getData());
					eq.unsubscribe(this);
				}
			}
		});
	}

	@Command
	public void submitCmd(@BindingParam("window") Window window) {
		// prevest styl z Listitem
		CodeListItem style = new CodeListItem();
		style.setUuid(UUID.fromString((String)this.swimStyleListitemSelected.getValue()));
		this.result.setStyle(style);
		this.result.setResultTime((Long)getIntervaltomillsConverter().coerceToBean(this.resultTimeStr, null, null));

		// ulozit do databaze
		resultService.store(this.result);
		EventQueueHelper.publish(ScbEventQueues.RESULT_QUEUE, ScbEvent.RELOAD_RESULT_LIST_DATA_EVENT, null, null);
		window.detach();
	}

	private void initData(Result result) {
		if (result == null) {
			throw new IllegalArgumentException("result is null");
		}

		this.result = result;
		this.resultTimeStr = (String)getIntervaltomillsConverter().coerceToUi(this.result.getResultTime(), null, null);

		if (this.result.getStyle() != null) {
			for (Listitem swimStyle : this.swimStyleListitemList) {
				if (((String)swimStyle.getValue()).equals(this.result.getStyle().getUuid().toString())) {
					this.swimStyleListitemSelected = swimStyle;
					break;
				}
			}
		} else {
			this.swimStyleListitemSelected = this.swimStyleListitemList.get(0);
		}
		BindUtils.postNotifyChange(null, null, this, "result");
		BindUtils.postNotifyChange(null, null, this, "swimStyleListitemSelected");
		BindUtils.postNotifyChange(null, null, this, "resultTimeStr");
	}

	private void fillSwimStyleItemList() {
		List<CodeListItem> swimStyleList = codeListService.getItemListByType(CodeListType.SWIMMING_STYLE);
		this.swimStyleListitemList = new ArrayList<>();
		for (CodeListItem item : swimStyleList) {
			swimStyleListitemList.add(new Listitem(item.getName(), item.getUuid().toString()));
		}
	}

	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
	public Listitem getSwimStyleListitemSelected() {
		return swimStyleListitemSelected;
	}
	public void setSwimStyleListitemSelected(Listitem swimStyleListitemSelected) {
		this.swimStyleListitemSelected = swimStyleListitemSelected;
	}
	public List<Listitem> getSwimStyleListitemList() {
		return swimStyleListitemList;
	}
	public String getResultTimeStr() {
		return resultTimeStr;
	}
	public void setResultTimeStr(String resultTimeStr) {
		this.resultTimeStr = resultTimeStr;
	}
}
