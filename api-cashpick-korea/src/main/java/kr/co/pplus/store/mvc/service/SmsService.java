package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.helper.Sender;
import kr.co.pplus.store.type.model.SmsMsg;
import kr.co.pplus.store.type.model.SysTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(transactionManager = "transactionManager")
public class SmsService implements Sender {

	@Value("${STORE.SMS_CERT_SENDER_NUM}")
	private String SMS_CERT_SENDER_NUM;

	@Value("${STORE.SMS_CLIENT_KEY}")
	String SMS_CLIENT_KEY = "PRNUMBERAUTHKEY";
	
	@Value("${STORE.SMS_GROUP_KEY}")
	String SMS_GROUP_KEY = "pplus-dev";

	@Autowired
	MsgService msgSvc ;
	

	@Override
	public void send(String target, SysTemplate template) throws Exception {
		SmsMsg msg = new SmsMsg();
		msg.setSender(SMS_CERT_SENDER_NUM);
		msg.setReceiver(target);
		msg.setMsg(template.getContents());
		send(msg);
	}
	
	public int send(SmsMsg msg) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("authKey", SMS_CLIENT_KEY);
		params.put("sendNumber", msg.getSender());
		params.put("content", msg.getMsg());
		params.put("recvNumber", msg.getReceiver());
		params.put("groupKey", SMS_GROUP_KEY)
;		//return sqlSession.insert("SMS.send", msg);
		try {
			msgSvc.insertSKBroadbandMsg(msgSvc.generateSKBroadbandMsg("PRNUMBER"
					, msg.getSender()
					, msg.getReceiver()
					, null, msg.getMsg()
					, null, null, null, null, null, null, null, null));

			return 1;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return 0;
	}
	
}
