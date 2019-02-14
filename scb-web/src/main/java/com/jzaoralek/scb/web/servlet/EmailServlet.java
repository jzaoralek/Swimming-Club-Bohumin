package com.jzaoralek.scb.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.jzaoralek.scb.web.service.MailService;
import com.jzaoralek.scb.web.vo.Mail;

/**
 * Servlet for email sending.
 * 
 * @author jakub.zaoralek
 *
 */

/*
 * TODO:
 * - čeština pokud je nasazeno na hostingu
 */
@Component
public class EmailServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(EmailServlet.class);

	private static final long serialVersionUID = 2156809132635293205L;

	private static final String MESSAGE_CONTENT_PARAM = "message";
	private static final String CONTACT_NAME_PARAM = "name";
	private static final String CONTACT_EMAIL_PARAM = "email";
	private static final String G_RECAPTCHA_RESPONSE_PARAM = "g-recaptcha-response";
	private static final String SPORTOLOGIC_CONTACT_EMAIL = "info@sportologic.cz";
	private static final String LINE_SEPARATOR_PROP = "line.separator";

	private static final String EMAIl_SENT_CONFIRM_PAGE = "email-sent-confirm.html";
//	test: 6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe
//	v2 pkbohumin.cz:   6LfPjX8UAAAAABGYbnl8aYCV26GDbi42V_f8bZd9
//	v2 sportologic.cz:   6LftpX8UAAAAANwPOmxWUhOY-xSE6Bp601yj5meE
	private static final String GRECAPTCHA_TEST_SECRET_KEY = "6LftpX8UAAAAANwPOmxWUhOY-xSE6Bp601yj5meE";
	
	@Autowired
	private MailService mailService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			doPost(request, response);
		} catch (Exception e) {
			logger.error("Unexpected exception caught during EmailServlet.doGet()", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		
		String message = request.getParameter(MESSAGE_CONTENT_PARAM);
		String contactName = request.getParameter(CONTACT_NAME_PARAM);
		String contactEmail = request.getParameter(CONTACT_EMAIL_PARAM);
		String gRecaptchaResponse = request.getParameter(G_RECAPTCHA_RESPONSE_PARAM);
		

		logger.info("Processing email message: {}, contact name: {}, contact email: {}, gRecaptchaResponse: {}",
				message, contactName, contactEmail, gRecaptchaResponse);
		
		if (!StringUtils.hasText(gRecaptchaResponse)) {
			logger.warn("gRecaptchaResponse request param is null, processing is stopped.");
			return;
		}

		if (isCaptchaValid(GRECAPTCHA_TEST_SECRET_KEY, gRecaptchaResponse)) {
			logger.info("Recaptcha verification result: SUCCESS");
			mailService.sendMail(new Mail(SPORTOLOGIC_CONTACT_EMAIL, "Zpráva z kontaktního formuláře sportologic.cz",buildMessage(contactName,contactEmail, message), null));
			response.sendRedirect(request.getContextPath() + "/" + EMAIl_SENT_CONFIRM_PAGE);			
		} else {
			logger.warn("Recaptcha verification result: FAILED, processing is stopped.");
			return;
		}
	}

	private String buildMessage(String contactName, String contactEmail, String messageContent) {
		StringBuilder ret = new StringBuilder();
		ret.append("Přijata zpráva z kontaktního formuláře sportologic.cz");
		ret.append(System.getProperty(LINE_SEPARATOR_PROP));
		ret.append(System.getProperty(LINE_SEPARATOR_PROP));

		ret.append("Kontaktní jméno: " + contactName);
		ret.append(System.getProperty(LINE_SEPARATOR_PROP));
		ret.append("Kontaktní email: " + contactEmail);
		ret.append(System.getProperty(LINE_SEPARATOR_PROP));
		ret.append(System.getProperty(LINE_SEPARATOR_PROP));
		ret.append(messageContent);

		return ret.toString();
	}

	/**
	 * Validates Google reCAPTCHA V2 or Invisible reCAPTCHA.
	 * 
	 * @param secretKey
	 *            Secret key (key given for communication between your site and
	 *            Google)
	 * @param response
	 *            reCAPTCHA response from client side. (g-recaptcha-response)
	 * @return true if validation successful, false otherwise.
	 */
	public static boolean isCaptchaValid(String secretKey, String response) {
		try {
			String url = "https://www.google.com/recaptcha/api/siteverify?" + "secret=" + secretKey + "&response=" + response;
			InputStream res = new URL(url).openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(res, Charset.forName("UTF-8")));

			StringBuilder sb = new StringBuilder();
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}
			String jsonText = sb.toString();
			res.close();
			
			JSONObject json = new JSONObject(jsonText);
			
			logger.info("Recaptcha verify response: {}", json);
			
			return json.getBoolean("success");
		} catch (Exception e) {
			logger.error("Unexpected exception caught during captcha validation.", e);
			return false;
		}
	}

	@Override
	public void init(ServletConfig config) {
		try {
			super.init(config);
			SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
		} catch (ServletException e) {
			logger.error("Unexpected exception caught during EmailServlet.init()", e);
		}
	}
}
