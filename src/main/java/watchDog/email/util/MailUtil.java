
package watchDog.email.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import watchDog.email.bean.MailAddresser;
import watchDog.email.bean.MailContent;
import watchDog.email.bean.MailRecipient;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 24, 2020
 */
public class MailUtil {
	
	public static final int MAIL_SEND_TYPE_PWD = 0;
	
	public static final int MAIL_SEND_TYPE_AUTH = 1;
	
	public void sendMail(int mailSendType, MailContent mailContent, MailAddresser mailAddresser, MailRecipient mailRecipient){
	        try {
	        	Properties props = System.getProperties(); // 获得系统属性配置，用于连接邮件服务器的参数配置
		        props.setProperty("mail.smtp.host", mailAddresser.getMailSmtpHost()); // 发送邮件的主机
		        if(mailSendType == MAIL_SEND_TYPE_PWD){
		        	props.setProperty("mail.user", mailAddresser.getMailUser());
			        props.setProperty("mail.password", mailAddresser.getMailPassword());
		        }else if(mailSendType == MAIL_SEND_TYPE_AUTH){
		        	 props.setProperty("mail.smtp.auth", String.valueOf(mailAddresser.getMailSmtpAuth()));
		        }
		       
		        Session session = Session.getInstance(props, null);// 获得Session对象
		        session.setDebug(true); // 设置是否显示debug信息,true 会在控制台显示相关信息

		        Message message = new MimeMessage(session);
		        message.setFrom(new InternetAddress(mailAddresser.getMailAddress()));
		        message.setRecipients(MimeMessage.RecipientType.TO, getToRecipientAddress(mailRecipient));
		        message.addRecipients(MimeMessage.RecipientType.CC, getCcRecipientAddress(mailRecipient));
		        message.addRecipients(MimeMessage.RecipientType.BCC, getBccRecipientAddress(mailRecipient));
		        message.setSubject(mailContent.getTitle()); 
		        message.setText(mailContent.getText()); 
		        
		        if(mailSendType == MAIL_SEND_TYPE_PWD){
		        	Transport.send(message);
		        }else if(mailSendType == MAIL_SEND_TYPE_AUTH){
		        	Transport.send(message, mailAddresser.getMailAddress(), mailAddresser.getAuthorizationCode());
		        }
			} catch (Exception e) {
			}
	    }
	
	private void sendMailByPwd(String username){
		
	}
	
	private Address[] getToRecipientAddress(MailRecipient mailRecipient){
		List<Address> list = new ArrayList<>();
			try {
				for (String toRecipient : mailRecipient.getToRecipients())
					list.add(new InternetAddress(toRecipient));
			} catch (AddressException e) {
				e.printStackTrace();
			}
			return list.toArray(new Address[list.size()]);
	}
	
	private Address[] getCcRecipientAddress(MailRecipient mailRecipient){
		List<Address> list = new ArrayList<>();
			try {
				for (String toRecipient : mailRecipient.getCcRecipient())
					list.add(new InternetAddress(toRecipient));
			} catch (AddressException e) {
				e.printStackTrace();
			}
			return list.toArray(new Address[list.size()]);
	}
	
	private Address[] getBccRecipientAddress(MailRecipient mailRecipient){
		List<Address> list = new ArrayList<>();
			try {
				for (String toRecipient : mailRecipient.getBccRecipient())
					list.add(new InternetAddress(toRecipient));
			} catch (AddressException e) {
				e.printStackTrace();
			}
			return list.toArray(new Address[list.size()]);
	}
	
	
}
