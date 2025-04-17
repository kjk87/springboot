package kr.co.pplus.store.mvc.bean;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.pplus.store.helper.Sender;
import kr.co.pplus.store.helper.VerificationHelper;
import kr.co.pplus.store.mvc.service.ArsService;
import kr.co.pplus.store.mvc.service.EmailService;
import kr.co.pplus.store.mvc.service.SmsService;

@Component
public class VerificationHelperManager {
	@Autowired
	private SmsService smsSvc;
	
	@Autowired
	private EmailService emailSvc;
	
	@Autowired
	private ArsService arsSvc;
	
	
	private Map<String, VerificationHelper> cache  = new HashMap<String, VerificationHelper>();
	
	@PostConstruct
	public void initialize() {
		cache.put("sms", new VerificationHelper() {

			@Override
			public String getTemplateCode() {
				return "SM";
			}

			@Override
			public Sender getSender() {
				return smsSvc;
			}
			
		});
		
		cache.put("email", new VerificationHelper() {
			
			@Override
			public String getTemplateCode() {
				return "EM";
			}
			
			@Override
			public Sender getSender() {
				return emailSvc;
			}
		});
		
		cache.put("ars", new VerificationHelper() {

			@Override
			public String getTemplateCode() {
				return "AR";
			}

			@Override
			public Sender getSender() {
				return arsSvc;
			}
			
		});
	}
	
	public VerificationHelper get(String media) {
		return cache.get(media);
	}
}
