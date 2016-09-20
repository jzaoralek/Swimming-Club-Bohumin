package com.jzaoralek.scb.ui.common.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.vm.Attachment;

public class FileDownloadServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLogger(FileDownloadServlet.class);

	private static final long serialVersionUID = -9066356224878907319L;
	public static final String URL = "/downloadAttachment";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Attachment attachment = (Attachment)request.getSession().getAttribute(WebConstants.ATTACHMENT_PARAM);

		if (attachment == null) {
			LOG.error("Attachment is null.");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Downloading attachment: " + attachment);
		}

		try{
			if(attachment.getByteArray() != null && attachment.getByteArray().length > 0) {
				OutputStream out = response.getOutputStream();

				//set response header
				response.setContentType("application/octet-stream");
		        response.setHeader("Content-Disposition","attachment;filename="+attachment.getName()+"");
		        response.setContentLength(attachment.getByteArray().length);

				//write and flush content of attachment into response
				out.write(attachment.getByteArray());
				out.flush();
				out.close();
			}
		}
		catch (Exception e) {
			LOG.error("Unexpected exception during processing attachment:" + attachment, e);
		}
	}

}
