package com.jzaoralek.scb.ui.pages.configuration.vm;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.Config;
import com.jzaoralek.scb.dataservice.domain.Config.ConfigCategory;
import com.jzaoralek.scb.dataservice.domain.Config.ConfigName;
import com.jzaoralek.scb.dataservice.domain.CourseApplDynAttrConfig;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplDynAttrConfigService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.ConfigUtil;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class ConfigVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigVM.class);
	
	@WireVariable
	private CourseApplDynAttrConfigService courseApplDynAttrConfigService;
	
	private List<Config> configListBasic;
	private List<Config> configListCourseApplication;
	private List<CourseApplDynAttrConfig> courseApplDynAttrConfigList;
	private List<String> courseYearList;
	private String courseApplicationTitle;

	@Override
	@Init
	public void init() {
		initConfigBasic();
		initConfigCourseApplication();
		initCourseApplDynAttrConfig();
		this.courseYearList = configurationService.getCourseYearList();
		this.courseApplicationTitle = getConfigCourseApplicationTitle();
	}
	
	private void initConfigBasic() {
		this.configListBasic = configurationService.getByCategory(ConfigCategory.BASIC);
	}

	private void initConfigCourseApplication() {
		this.configListCourseApplication = configurationService.getByCategory(ConfigCategory.COURSE_APPLICATION);
	}
	
	private void initCourseApplDynAttrConfig() {
		this.courseApplDynAttrConfigList = courseApplDynAttrConfigService.getAll();
		BindUtils.postNotifyChange(null, null, this, "courseApplDynAttrConfigList");
	}
	
	@NotifyChange("configListBasic")
	@Command
	public void updateCmd(@BindingParam("config") Config config) {
		updateConfig(config);
		if (config.getName().equals(ConfigName.COURSE_APPLICATION_YEAR.name())) {
			// in case of change courseApplicationYear update courseApplicationTitle
			useStandardCourseApplicationTitleCmd();
			BindUtils.postNotifyChange(null, null, this, "courseApplicationTitle");
		}
	}

	@NotifyChange("configListBasic")
	@Command
	public void refreshDataBasicCmd() {
		initConfigBasic();
	}
	
	@NotifyChange("configListCourseApplication")
	@Command
	public void refreshDataCourseApplicationCmd() {
		initConfigCourseApplication();
	}
	
	@NotifyChange("courseApplDynAttrConfigList")
	@Command
	public void refreshcourseApplDynAttrConfigListCmd() {
		initCourseApplDynAttrConfig();
	}
	
	/**
	 * Save course application title.
	 */
	@NotifyChange("courseApplicationTitle")
	@Command
	public void courseApplicationTitleUpdateCmd() {
		updateCourseApplicationTitle();
	}
	
	/**
	 * Use standard course application title;
	 */
	@NotifyChange("courseApplicationTitle")
	@Command
	public void useStandardCourseApplicationTitleCmd() {
		this.courseApplicationTitle = getDefaultCourseApplicationTitle();
		updateCourseApplicationTitle();
	}
	
	@Command
	public void newDynAttrConfigCmd() {
		Map<String, Object> args = new HashMap<>();
		Consumer<CourseApplDynAttrConfig> callback = this::storeDynAttrConfig;
		args.put(WebConstants.CALLBACK_PARAM, callback);
		WebUtils.openModal(WebPages.COURSE_APPL_DYN_ATTR_CONFIG_WINDOW.getUrl(), 
				Labels.getLabel("txt.ui.common.DynamicAttribute"), 
				args);
	}
	
	@Command
	public void detailDynAttrConfigCmd(@BindingParam(WebConstants.ITEM_PARAM) CourseApplDynAttrConfig item) {
		Map<String, Object> args = new HashMap<>();
		Consumer<CourseApplDynAttrConfig> callback = this::storeDynAttrConfig;
		args.put(WebConstants.CALLBACK_PARAM, callback);
		args.put(WebConstants.ITEM_PARAM, item);
		WebUtils.openModal(WebPages.COURSE_APPL_DYN_ATTR_CONFIG_WINDOW.getUrl(), 
				Labels.getLabel("txt.ui.common.DynamicAttribute"), 
				args);
	}
	
	private void storeDynAttrConfig(final CourseApplDynAttrConfig item) {
		Objects.requireNonNull(item, "item is null");
		// check user role
		if (!isLoggedUserInRole(ScbUserRole.ADMIN.name())) {
			return;
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Storing CourseApplDynAttrConfig: " + item);
		}
		
		try {
			courseApplDynAttrConfigService.store(item);
			WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.dynAttrStored", new Object[] {item.getName()}));
			initCourseApplDynAttrConfig();
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught during storing CourseApplDynAttrConfig: " + item, e);
			WebUtils.showNotificationError(e.getMessage());
		}
	}
	
	
	/**
	 * Delete course application dynamic attribute.
	 * @param item
	 */
	@Command
    public void deleteDynAttrConfigCmd(@BindingParam(WebConstants.ITEM_PARAM) final CourseApplDynAttrConfig item) {
		if (item ==  null) {
			throw new IllegalArgumentException("CourseApplDynAttrConfig is null.");
		}
		
		// check user role
		if (!isLoggedUserInRole(ScbUserRole.ADMIN.name())) {
			return;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting CourseApplDynAttrConfig with uuid: " + item.getUuid());
		}
		
		final Object[] msgParams = new Object[] {item.getName()};
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.deleteDynAttrConfig",
			"msg.ui.title.deleteRecord",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					try {
						courseApplDynAttrConfigService.delete(item.getUuid());
						WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.dynAttrConfigDeleted", msgParams));
						initCourseApplDynAttrConfig();
					} catch (ScbValidationException e) {
						LOG.warn("ScbValidationException caught during deleting CourseApplDynAttrConfig uuid: " + item.getUuid(), e);
						WebUtils.showNotificationError(e.getMessage());
					}
				}
			},
			msgParams
		);
	}
	
	/**
	 * Change state (activate/deactivate) of course application dynamic attribute.
	 */
	@Command
	public void changeDynAttrConfigCmd(@BindingParam(WebConstants.ITEM_PARAM) final CourseApplDynAttrConfig item,
										@BindingParam(WebConstants.ACTIVE_PARAM) final Boolean active) {
		// check user role
		if (!isLoggedUserInRole(ScbUserRole.ADMIN.name())) {
			return;
		}
		
		item.setTerminatedAt(active ? null : Calendar.getInstance().getTime());
		courseApplDynAttrConfigService.terminate(item);
		initCourseApplDynAttrConfig();
				
		String msg = active ? "msg.ui.info.dynAttrActivated" : "msg.ui.info.dynAttrDeactivated";
		WebUtils.showNotificationInfo(Labels.getLabel(msg, new Object[]{item.getName()}));	
	}
	
	/**
	 * Update of course application title.
	 */
	private void updateCourseApplicationTitle() {
		Config courseApplicationTitleConfig = configurationService.getByName(ConfigName.COURSE_APPLICATION_TITLE.name());
		courseApplicationTitleConfig.setValue(this.courseApplicationTitle);
		updateConfig(courseApplicationTitleConfig);
	}
	
	private void updateConfig(Config config)  {
		configurationService.update(config);
		ConfigUtil.clearSessionConfigCache(config.getName());
		showNotifChangesSaved();
	}
	
	private void showNotifChangesSaved() {
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.changesSaved"));
	}
	
	public List<Config> getConfigListBasic() {
		return configListBasic;
	}
	public void setConfigListBasic(List<Config> configList) {
		this.configListBasic = configList;
	}
	public List<String> getCourseYearList() {
		return courseYearList;
	}
	public List<Config> getConfigListCourseApplication() {
		return configListCourseApplication;
	}
	public void setConfigListCourseApplication(List<Config> configListCourseApplication) {
		this.configListCourseApplication = configListCourseApplication;
	}
	public String getCourseApplicationTitle() {
		return courseApplicationTitle;
	}
	public void setCourseApplicationTitle(String courseApplicationTitle) {
		this.courseApplicationTitle = courseApplicationTitle;
	}
	public List<CourseApplDynAttrConfig> getCourseApplDynAttrConfigList() {
		return courseApplDynAttrConfigList;
	}
}