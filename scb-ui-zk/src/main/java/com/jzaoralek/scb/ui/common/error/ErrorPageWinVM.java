package com.jzaoralek.scb.ui.common.error;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Popup;

import com.jzaoralek.scb.dataservice.common.DataServiceConstants;
import com.jzaoralek.scb.dataservice.datasource.ClientDatabaseContextHolder;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.service.MailService;
import com.jzaoralek.scb.dataservice.utils.ExcUtil;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class ErrorPageWinVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(ErrorPageWinVM.class);

	@WireVariable
	private MailService mailService;

	private Throwable exception;
	private String errorReport;
	private boolean canBuildReport = true;

	@Init
	public void init() {
		Object o = WebUtils.getRequest().getAttribute("javax.servlet.error.exception");
		if (o instanceof Throwable) {
			LOG.error("Unexpected exception caught, ", o);
			this.exception = (Throwable) o;
			this.errorReport = buildErrorReport();

			// mail to administrator
			if (mailService != null) {
				mailService.sendMail(DataServiceConstants.ADMIN_EMAIL, null, Labels.getLabel("txt.ui.internalError"), this.errorReport, null, false, false, null, ClientDatabaseContextHolder.getClientDatabase());				
			}
		}
	}

	/**
	 * Sestavi hlaseni
	 * Akt. cas
	 * Akt. instance (info z config_portal)
	 * Verze aplikace (analogicky okynku v aplikaci)
	 * Prihlaseny uzivatel + v zavorce jeho role (analogicky okynku v aplikaci)
	 * Kompletni stacktrace
	 */
	private String buildErrorReport() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println(Labels.getLabel("txt.ui.common.date") + ": " + LocalDateTime.now());
		pw.println(Labels.getLabel("txt.ui.common.version") + ": " + getAppVersion());
		pw.println(Labels.getLabel("txt.ui.common.user") + ": " + getUserWithRole());
		pw.println(Labels.getLabel("txt.ui.common.page") + ": " + Executions.getCurrent().getDesktop().getRequestPath());

		pw.println();
		pw.println(ExcUtil.traceMessage(this.exception).toString());

		return sw.toString();
	}

	private String getUserWithRole() {
		StringBuilder sb = new StringBuilder();
		ScbUser user = SecurityUtils.getLoggedUser();
		if (user != null) {
			sb.append(user.getUsername());
			sb.append("("+user.getRole()+")");
		}
		return sb.toString();
	}

	@Command
	public void downloadToFileCmd(@BindingParam("popup") Popup popup) {
		byte[] content = this.errorReport.getBytes(StandardCharsets.UTF_8);
		String nowStr = LocalDateTime.now().toString();
		String reportFilename = "err" + nowStr + ".txt";
		Filedownload.save(content, "", reportFilename);
		popup.close();
	}

	public boolean isCanBuildReport() {
		return canBuildReport;
	}

	public String getErrorReport() {
		return errorReport;
	}
}
