package com.jzaoralek.scb.dataservice.service.impl;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.service.MailService;

@Service("mailService")
public class MailServiceImpl implements MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);

    private static final String MAIL_SMTP_HOST = "smtp.seznam.cz"; // "77.75.72.43";
    private static final String MAIL_SMTP_PORT = "25";
    private static final String SMTP_AUTH_USER = "plavanibohumin@seznam.cz";
    private static final String SMTP_AUTH_PWD = "bohumin2012";

//    private static final String MAIL_SMTP_HOST = "smtp.gmail.com";
//    private static final String MAIL_SMTP_PORT = "587";
//    private static final String SMTP_AUTH_USER = "swimmingclub.bohumin@gmail.com";
//    private static final String SMTP_AUTH_PWD = "9WzE1118npLu4rhhycBK";

    @Override
    public void sendMail(String to, String subject, String text, byte[] attachment, String attachmentName) {
        if (LOG.isDebugEnabled()) {
        	LOG.debug("Send email '" + subject + "' to '" + to + "'.");
        }
        // Get system properties
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", MAIL_SMTP_HOST);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.port", MAIL_SMTP_PORT);


       // Get the default Session object.
       Authenticator auth = new SMTPAuthenticator();
       Session session = Session.getDefaultInstance(properties, auth);

       if (LOG.isDebugEnabled()) {
    	   LOG.debug("Mail session created.");
       }
       try{
          // Create a default MimeMessage object.
          MimeMessage message = new MimeMessage(session);
          // Set From: header field of the header.
          message.setFrom(new InternetAddress(SMTP_AUTH_USER));
          // Set To: header field of the header.
          message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
          // Set Subject: header field
          message.setSubject(subject);
          // Now set the actual message
//          message.setText(text);
          BodyPart messageBodyPart = new MimeBodyPart();
          // Now set the actual message
          messageBodyPart.setText(text);

          Multipart multipart = new MimeMultipart();
          // Set text message part
          multipart.addBodyPart(messageBodyPart);

          // Part two is attachment

          DataSource dataSource = new ByteArrayDataSource(attachment, "application/pdf");
          MimeBodyPart pdfBodyPart = new MimeBodyPart();
          pdfBodyPart.setDataHandler(new DataHandler(dataSource));
          pdfBodyPart.setFileName(attachmentName);

//          messageBodyPart = new MimeBodyPart();
//          DataSource source = new FileDataSource(attachment);
//          messageBodyPart.setDataHandler(new DataHandler(source));
//          messageBodyPart.setFileName(attachment.getName());
          multipart.addBodyPart(pdfBodyPart);

          // Send the complete message parts
          message.setContent(multipart);

          // Send message
          Transport.send(message);

          if (LOG.isDebugEnabled()) {
        	  LOG.debug("Mail send - OK");
          }
       } catch (MessagingException mex) {
    	   LOG.error("MessagingException. Check settings: ", mex);
       } catch (Exception e) {
    	   LOG.error("Exception during sendMail processing: ", e);
       }
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        @Override
		public PasswordAuthentication getPasswordAuthentication() {
           String username = SMTP_AUTH_USER;
           String password = SMTP_AUTH_PWD;
           return new PasswordAuthentication(username, password);
        }
    }
}
