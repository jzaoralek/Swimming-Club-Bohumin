package com.jzaoralek.scb.ui.pages.configuration.vm;

import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;

import com.jzaoralek.scb.dataservice.domain.Config;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class ConfigVM extends BaseVM {

	private List<Config> configList;

	@Init
	public void init() {
		this.configList = configurationService.getAll();
	}

	@NotifyChange("configList")
	@Command
	public void updateCmd(@BindingParam("config") Config config) {
		configurationService.update(config);
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.changesSaved"));
	}

	@NotifyChange("configList")
	@Command
	public void refreshDataCmd() {
		this.configList = configurationService.getAll();
	}

	public List<Config> getConfigList() {
		return configList;
	}

	public void setConfigList(List<Config> configList) {
		this.configList = configList;
	}
}
