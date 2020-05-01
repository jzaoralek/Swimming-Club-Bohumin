package com.jzaoralek.scb.ui.pages.configuration.vm;

import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;

import com.jzaoralek.scb.dataservice.domain.Config;
import com.jzaoralek.scb.dataservice.domain.Config.ConfigCategory;
import com.jzaoralek.scb.ui.common.utils.ConfigUtil;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class ConfigVM extends BaseVM {

	private List<Config> configListBasic;
	private List<Config> configListCourseApplication;
	private List<String> courseYearList;
	private String courseApplicationTitle;

	@Init
	public void init() {
		initConfigBasic();
		initConfigCourseApplication();
		this.courseYearList = configurationService.getCourseYearList();
		this.courseApplicationTitle = "TODO";
	}
	
	private void initConfigBasic() {
		this.configListBasic = configurationService.getByCategory(ConfigCategory.BASIC);
	}

	private void initConfigCourseApplication() {
		this.configListCourseApplication = configurationService.getByCategory(ConfigCategory.COURSE_APPLICATION);
	}
	
	@NotifyChange("configListBasic")
	@Command
	public void updateCmd(@BindingParam("config") Config config) {
		configurationService.update(config);
		ConfigUtil.clearSessionConfigCache(config.getName());
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.changesSaved"));
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
	
	/**
	 * Save course application title.
	 */
	@NotifyChange("courseApplicationTitle")
	@Command
	public void courseApplicationTitleUpdateCmd() {
		this.courseApplicationTitle = "TODO: updated";
	}
	
	/**
	 * Use standard course application title;
	 */
	@NotifyChange("courseApplicationTitle")
	@Command
	public void useStandardCourseApplicationTitleCmd() {
		this.courseApplicationTitle = "TODO: standard title";
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
}