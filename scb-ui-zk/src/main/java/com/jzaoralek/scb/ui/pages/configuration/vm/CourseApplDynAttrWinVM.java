package com.jzaoralek.scb.ui.pages.configuration.vm;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttrConfig;
import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttrConfig.CourseApplDynAttrConfigType;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplDynAttrConfigService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseApplDynAttrWinVM extends BaseVM  {

	private static final Logger LOG = LoggerFactory.getLogger(CourseApplDynAttrWinVM.class);
	
	@WireVariable
	private CourseApplDynAttrConfigService courseApplDynAttrConfigService;
	
	private Consumer<CourseApplDynAttrConfig> callback;
	private CourseApplDynAttrConfig item;
	private boolean editMode = false;
	private final List<Listitem> dynAttrTypeList = WebUtils.getMessageItemsFromEnum(EnumSet.allOf(CourseApplDynAttrConfigType.class));
	private Listitem dynAttrTypeSelected;

	@Override
	@SuppressWarnings("unchecked")
	@Init
	public void init() {
		this.callback = (Consumer<CourseApplDynAttrConfig>) WebUtils.getArg(WebConstants.CALLBACK_PARAM);
		Objects.requireNonNull(this.callback, "callback is null");
		
		this.item = (CourseApplDynAttrConfig) WebUtils.getArg(WebConstants.ITEM_PARAM);
		if (item == null) {
			this.item = new CourseApplDynAttrConfig();
			this.dynAttrTypeSelected = this.dynAttrTypeList.get(0);
		} else {
			this.editMode = true;
			this.dynAttrTypeSelected = getTypeListItem(this.item.getType());
		}
	}
	
	@Command
	public void submitCmd(@BindingParam("window") Window window) {
		try {
			courseApplDynAttrConfigService.validateUniquieCourseAppDynAttrConfigname(this.item, !this.editMode);
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught during storing CourseApplDynAttrConfig: " + item, e);
			WebUtils.showNotificationError(e.getMessage());
			return;
		}
		this.item.setType(this.dynAttrTypeSelected.getValue());		
		this.callback.accept(this.item);
		window.detach();
	}
	
	private Listitem getTypeListItem(CourseApplDynAttrConfigType type) {
		if (type == null) {
			return null;
		}

		List<Listitem> filtered = this.dynAttrTypeList.
			stream().
			filter(i -> i.getValue() == type).
			collect(Collectors.toList());
		
		return filtered.get(0);
	}
	
	public boolean isEditMode() {
		return editMode;
	}
	public CourseApplDynAttrConfig getItem() {
		return item;
	}
	public void setItem(CourseApplDynAttrConfig item) {
		this.item = item;
	}
	public List<Listitem> getDynAttrTypeList() {
		return dynAttrTypeList;
	}
	public Listitem getDynAttrTypeSelected() {
		return dynAttrTypeSelected;
	}
	public void setDynAttrTypeSelected(Listitem dynAttrTypeSelected) {
		this.dynAttrTypeSelected = dynAttrTypeSelected;
	}
}
