package kr.co.pplus.store.mvc.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kr.co.pplus.store.helper.Sender;
import kr.co.pplus.store.type.model.SysTemplate;
import kr.co.pplus.store.util.MailSender;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "transactionManager")
public class EmailService implements Sender {
	
	@Value("${MAIL.FROM}")
	private String FROM;
	  
	@Value("${MAIL.FROM.NICKNAME}")
	private String NICKNAME;
	
	@Resource
	private MailSender sender;
	
	@Override
	public void send(String target, SysTemplate template) throws Exception {
		send(target, FROM, template.getSubject(), NICKNAME, template.getContents());
	}
	
	
	public void send(String to, String from, String subject, String nickname, String body) throws Exception {
		sender.sendToHTML(to, from, nickname, subject, body);
	}

}
