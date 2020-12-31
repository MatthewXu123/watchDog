package watchDog.util;
 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import watchDog.bean.config.MailDTO;
 
public class MailUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MailUtil.class);
	
	public static void sendMail(MailDTO mailDTO, String title, String content){
		Properties p = new Properties();
		p.setProperty("mail.smtp.host", mailDTO.getMailSmtpHost());
		p.setProperty("mail.smtp.port", mailDTO.getMailSmtpPort());
		p.setProperty("mail.smtp.socketFactory.port", mailDTO.getMailSmtpPort());
		p.setProperty("mail.smtp.auth", "true");
		//p.put("mail.smtp.ssl.enable", true);
		p.setProperty("mail.smtp.socketFactory.class", "SSL_FACTORY");
		
		Session session = Session.getInstance(p, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailDTO.getFromAddress(), mailDTO.getAuthCode());
			}
		});
		//session.setDebug(true);
		
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(mailDTO.getFromAddress()));
			message.setRecipients(Message.RecipientType.TO, getToAddresses(mailDTO));
			message.setSubject(title);
			message.setContent(content, "text/html;charset=UTF-8");
			message.setSentDate(new Date());
			message.saveChanges();
			Transport.send(message);
		} catch (MessagingException e) {
			LOGGER.error("",e);
		}
		
	}
	
	private static Address[] getToAddresses(MailDTO mailDTO){
		List<Address> list = new ArrayList<>();
			try {
				for (String toAddress : mailDTO.getToAddresses())
					list.add(new InternetAddress(toAddress));
			} catch (AddressException e) {
				LOGGER.error("",e);
			}
			return list.toArray(new Address[list.size()]);
	}
	
}