package com.bigboxer23.utils.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** */
public class MailSender {

	private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

	private static Session mailSession;

	public static void sendGmail(
			String toEmail, String fromEmail, String sendingPW, String subject, String body, List<File> files) {
		if (fromEmail == null || sendingPW == null || toEmail == null) {
			logger.info("Not sending email, not configured");
			return;
		}
		if (mailSession == null) {
			Properties properties = new Properties();
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.host", "smtp.gmail.com");
			properties.put("mail.smtp.port", "587");
			mailSession = Session.getInstance(properties, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(fromEmail, sendingPW);
				}
			});
		}
		logger.info("Sending mail... " + (files != null ? files.get(0) : ""));
		try {
			Message message = new MimeMessage(mailSession);
			message.setFrom(new InternetAddress(fromEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			message.setSubject(subject);
			List<MimeBodyPart> fileList = new ArrayList<>();
			if (body != null) {
				MimeBodyPart aMimeBodyPart = new MimeBodyPart();
				aMimeBodyPart.setText(body);
				fileList.add(aMimeBodyPart);
			}
			if (files != null) {
				for (File aFile : files) {
					MimeBodyPart aMimeBodyPart = new MimeBodyPart();
					aMimeBodyPart.setDataHandler(new DataHandler(new FileDataSource(aFile)));
					aMimeBodyPart.setFileName(aFile.getName());
					fileList.add(aMimeBodyPart);
				}
			}
			message.setContent(new MimeMultipart(fileList.toArray(new MimeBodyPart[0])));
			Transport.send(message);
		} catch (MessagingException e) {
			logger.error("sendGmail:", e);
			mailSession = null;
		}
	}
}
