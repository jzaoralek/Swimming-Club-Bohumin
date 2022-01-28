package com.jzaoralek.scb.dataservice.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.jzaoralek.scb.dataservice.common.DataServiceConstants;
import com.jzaoralek.scb.dataservice.dao.MailSendDao;
import com.jzaoralek.scb.dataservice.datasource.ClientDatabaseContextHolder;
import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Mail;
import com.jzaoralek.scb.dataservice.domain.MailSend;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.ConfigurationService;
import com.jzaoralek.scb.dataservice.service.MailService;

@Service("mailService")
public class MailServiceImpl extends BaseAbstractService implements MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);

    @Value("${smtp.host}")
    private String mailSmtpHost;

	@Value("${smtp.port}")
    private String mailSmtpPort;

    private String mailSmtpUser;

    private String mailSmtpPassword;
    
    @Autowired
    private MailSendDao mailSendDao;

    @Autowired
	private ConfigurationService configurationService;
    
    @Async
    @Override
    public void sendMail(String to, 
    		String cc, 
    		String subject, 
    		String text, 
    		List<Attachment> attachmentList, 
    		boolean html, 
    		boolean storeToDb,
    		String toCompleteName,
    		String clientDBCtx) {
        if (LOG.isDebugEnabled()) {
        	LOG.debug("Send email subject: {}, to: {}.", subject, to);
        }
        // Get system properties
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", mailSmtpHost);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "false");
        properties.put("mail.smtp.port", mailSmtpPort);
        // SSL props
        properties.put("mail.smtp.ssl.trust", "*");
        properties.put("mail.smtp.ssl.enable", "true");

        // setting of client DB context
        if (LOG.isDebugEnabled()) {
        	LOG.debug("Settin client DB context: {}", clientDBCtx);
        }
        ClientDatabaseContextHolder.set(clientDBCtx);        	
        
        mailSmtpUser = configurationService.getSmtpUser();
    	mailSmtpPassword = configurationService.getSmtpPwd();
    	
       // Get the default Session object.
       Authenticator auth = new SMTPAuthenticator();
       Session session = Session.getDefaultInstance(properties, auth);

       if (LOG.isDebugEnabled()) {
    	   LOG.debug("Mail session created.");
       }
       
       // store send mail to database
       MailSend mailSend = new MailSend(to, cc, subject, text, attachmentList, toCompleteName);
       mailSend.setHtml(html);
       // fillIdentEntity(mailSend);
       mailSend.setUuid(UUID.randomUUID());
       mailSend.setModifAt(Calendar.getInstance().getTime());
       mailSend.setModifBy(ANONYM_USER_UUID);
       
       try{
          // Create a default MimeMessage object.
          MimeMessage message = new MimeMessage(session);
          // Set From: header field of the header.
          message.setFrom(new InternetAddress(mailSmtpUser));
          // Set To: header field of the header.
          message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
          // Set Cc: header field of the header.
          if (StringUtils.hasText(cc)) {
        	  message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));        	  
          }
          // Set Subject: header field
          message.setSubject(subject, "UTF-8");
          // Now set the actual message
          MimeBodyPart messageBodyPart = new MimeBodyPart();
          
          // Now set the actual message
          if (html) {
        	  // html email
        	  messageBodyPart.setContent(text,"text/html; charset=UTF-8"); 
          } else {
        	  // text email
        	  messageBodyPart.setText(text, "UTF-8");        	  
          }
          
          Multipart multipart = new MimeMultipart();
          // Set text message part
          multipart.addBodyPart(messageBodyPart);

          // Part two is attachment
          if (attachmentList != null && !attachmentList.isEmpty()) {
        	  for (Attachment attachment : attachmentList) {
        		  if (attachment != null) {
        			  DataSource dataSource = new ByteArrayDataSource(attachment.getByteArray(), attachment.getContentType());
        			  MimeBodyPart pdfBodyPart = new MimeBodyPart();
        			  pdfBodyPart.setDataHandler(new DataHandler(dataSource));
        			  pdfBodyPart.setFileName(attachment.getName());
        			  multipart.addBodyPart(pdfBodyPart);

        		  }        	          		  
        	  }
          }

          // Send the complete message parts
          message.setContent(multipart);

          // Send message
          Transport.send(message);
          mailSend.setSuccess(true);  

          if (LOG.isDebugEnabled()) {
        	  LOG.debug("Mail send - OK");
          }          
       } catch (MessagingException mex) {
    	   LOG.error("MessagingException. Check settings: ", mex);
    	   mailSend.setSuccess(false);
    	   mailSend.setDescription(mex.getMessage());
       } catch (Exception e) {
    	   LOG.error("Exception during sendMail processing: ", e);
    	   mailSend.setSuccess(false);
    	   mailSend.setDescription(e.getMessage());
       } finally {
    	   if (!storeToDb) {
    		   mailSend = null;
    	   } else {
    		   // store send mail to DB
    		   mailSendDao.insert(mailSend);
    	   }
       }
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        @Override
		public PasswordAuthentication getPasswordAuthentication() {
           String username = mailSmtpUser;
           String password = mailSmtpPassword;
           return new PasswordAuthentication(username, password);
        }
    }

    @Async
    @Override
	public void sendMail(Mail mail, String clientDBCtx) {
    	if (mail == null) {
    		return;
    	}
		
    	if (LOG.isDebugEnabled()) {
        	LOG.debug("Send email: {}", mail);
        }
		
		sendMail(mail.getTo(), 
				mail.getCc(), 
				mail.getSubject(), 
				mail.getText(), 
				mail.getAttachmentList(), 
				mail.isHtml(), 
				mail.isStoreToDb(), 
				mail.getToCompleteName(),
				clientDBCtx);
	}
    
    @Async
	@Override
	public void sendMailBatch(List<Mail> mailList, String clientDBCtx) {
    	if (CollectionUtils.isEmpty(mailList)) {
    		return;
    	}
    	if (LOG.isDebugEnabled()) {
     	   LOG.debug("Processiong mail count: {}", mailList.size());
        }
    	
    	int counter = 0;
		for (Mail item : mailList) {
			sendMail(item, clientDBCtx);
			counter++;
			if (counter%DataServiceConstants.MAIL_SENDER_BATCH_SIZE == 0) {
				try {
					Thread.sleep(DataServiceConstants.MAIL_SENDER_PAUSE_BETWEEN_BATCH);
				} catch (InterruptedException e) {
					LOG.error("InterruptedException caught", e);
					throw new RuntimeException(e);
				}				
			}
		}
	}
    
    @Override
	public List<MailSend> getMailSendListByCriteria(Date dateFrom, 
										Date dateTo, 
										String mailTo,
										String mailSubject,
										String mailText) {
    	if (dateFrom == null || dateTo == null) {
    		return null;
    	}
		return mailSendDao.getMailSendListByCriteria(dateFrom, dateTo, mailTo, mailSubject, mailText);
	}

	@Override
	public MailSend getByUuid(UUID uuid) {
		Objects.requireNonNull(uuid, "uuid is null");
		
		return mailSendDao.getByUuid(uuid);
	}
	
	@Override
	public void delete(List<MailSend> mailSendList) {
		mailSendDao.delete(mailSendList);
	}

	@Override
	public void deleteSendMailToDate(Date dateTo) {
		mailSendDao.deleteToDate(dateTo);
	}
}
