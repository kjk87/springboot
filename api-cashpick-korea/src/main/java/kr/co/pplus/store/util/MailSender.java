package kr.co.pplus.store.util;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailSender {
	  
	  private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

	  private JavaMailSender javaMailSender;
	  
	  @Value("${MAIL.CHARSET}")
	  private String charset;

	  public MailSender(JavaMailSender javaMailSender) {
	    this.javaMailSender = javaMailSender;
	  }

	  /**
	   *
	   * 받는사람 메일, 보내는 사람 메일, 제목, HTML(String)
	   *
	   * @throws AddressException 
	   * @Author            : SiHoon.Lee
	   * @Date              : 오후 4:32:22
	   */
	  public void sendToHTML(String to, String from, String subject, String contents) throws AddressException {
	    this.sendToHTML(to, new InternetAddress(from), subject, contents);
	  }
	  
	  /**
	  *
	  * 받는사람 메일, 보내는 사람 메일, 제목, HTML(String)
	  *
	  * @throws AddressException 
	   * @throws UnsupportedEncodingException 
	  * @Author            : SiHoon.Lee
	  * @Date              : 오후 4:32:22
	  */
	 public void sendToHTML(String to, String from, String nickname, String subject, String contents) throws AddressException, UnsupportedEncodingException {
	   this.sendToHTML(to, new InternetAddress(from, nickname), subject, contents);
	 }

	  /**
	   *
	   * 받는사람 메일, 보내는 사람 메일, 제목, HTML(String)
	   *
	   * @Author			: SiHoon.Lee
	   * @Date				: 오후 4:32:22
	   */
	  public void sendToHTML(String to, InternetAddress from, String subject, String contents) {
	    
	    MimeMessage mimeMessage = this.javaMailSender.createMimeMessage(); 
	    
	    try {
	      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, charset);
	      messageHelper.setTo(to);
	      messageHelper.setFrom(from);
	      messageHelper.setSubject(subject);
	      messageHelper.setText(contents, true);
	      
	      this.javaMailSender.send(mimeMessage);

	    } catch (MessagingException e) {
	      e.printStackTrace();
	      logger.error(e.getMessage());
	    }
	  }
}
