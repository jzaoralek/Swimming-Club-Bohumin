package com.jzaoralek.scb.ui.pages.configuration.vm;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig.CourseApplicationFileType;
import com.jzaoralek.scb.dataservice.service.CourseApplicationFileConfigService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseApplFileConfigWinVM extends BaseVM  {

	private static final Logger LOG = LoggerFactory.getLogger(CourseApplFileConfigWinVM.class);
	
	@WireVariable
	private CourseApplicationFileConfigService CourseApplicationFileConfigService;
	
	private Consumer<CourseApplicationFileConfig> callback;
	private CourseApplicationFileConfig item;
	private boolean editMode = false;

	@Override
	@SuppressWarnings("unchecked")
	@Init
	public void init() {
		this.callback = (Consumer<CourseApplicationFileConfig>) WebUtils.getArg(WebConstants.CALLBACK_PARAM);
		Objects.requireNonNull(this.callback, "callback is null");
		
		this.item = (CourseApplicationFileConfig) WebUtils.getArg(WebConstants.ITEM_PARAM);
		if (item == null) {
			this.item = new CourseApplicationFileConfig();
			// potreba aby se file download zobrazil na prihlasce
			this.item.setPageText(true);
			this.item.setType(CourseApplicationFileType.OTHER);
		} else {
			this.editMode = true;
		}
	}
	
	/**
	 * Save changes.
	 * @param window
	 */
	@Command
	public void submitCmd(@BindingParam("window") Window window) {
		// validation, attachment is required if pageAttachment or emailAttachment
		if ((this.item.isPageAttachment() || this.item.isEmailAttachment()) 
				&& !isAttachmentPresent()) {
			WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.fileIsRequired"));
			return;
		}
		
		this.callback.accept(this.item);
		window.detach();
	}
	
	/**
	 * Upload attachment.
	 */
	@NotifyChange("item")
	@Command
	public void uploadAttachmentCmd(@BindingParam("event") UploadEvent event) {
		Media media = event.getMedia();
		
		Attachment attachment = this.item.getAttachment();
		if (attachment == null) {
			attachment = new Attachment();
			UUID attachmentUuid = UUID.randomUUID();
			attachment.setUuid(attachmentUuid);	
			this.item.setAttachmentUuid(attachmentUuid);
		}
		attachment.setByteArray(media.getByteData());
//		attachment.setContentType(WebConstants.APPL_FILE_CONTENT_TYPE);
		attachment.setContentType(media.getContentType());
		attachment.setDescription(this.item.getDescription());
		attachment.setName(media.getName());
		this.item.setAttachment(attachment);
	}
	
	/**
	 * Download attachment if present.
	 */
	@Command
	public void downloadAttachmentCmd() {
		if (!isAttachmentPresent()) {
			return;
		}
//		Attachment attachment = courseApplicationFileConfigService.getFileByUuid(this.item.getAttachmentUuid());
		WebUtils.downloadAttachment(this.item.getAttachment());			
	}

	/**
	 * Remove attachment if present.
	 */
	@NotifyChange("item")
	@Command
	public void removeAttachmentCmd() {
		if (!isAttachmentPresent()) {
			return;
		}
		
		this.item.setAttachment(null);
	}
	
	@DependsOn("item")
	public boolean isAttachmentPresent() {
		return this.item.getAttachment() != null
				&& this.item.getAttachment().getByteArray() != null;
	}
	
	@DependsOn("item")
	public String getUploadBtnLabel() {
		if (isAttachmentPresent()) {
			return Labels.getLabel("txt.ui.common.ChangeFile");
		} else {
			return Labels.getLabel("txt.ui.common.FileUpload");
		}
	}
	
	public boolean isEditMode() {
		return editMode;
	}
	public CourseApplicationFileConfig getItem() {
		return item;
	}
	public void setItem(CourseApplicationFileConfig item) {
		this.item = item;
	}
}
