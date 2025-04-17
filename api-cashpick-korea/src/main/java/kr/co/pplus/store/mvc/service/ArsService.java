package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.helper.Sender;
import kr.co.pplus.store.type.model.ArsRequest;
import kr.co.pplus.store.type.model.SysTemplate;
import kr.co.pplus.store.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@Transactional(transactionManager = "transactionManager")
public class ArsService extends RootService implements Sender {
	private final static Logger logger = LoggerFactory.getLogger(ArsService.class);

	@Value("${STORE.ARS_URL}")
	private String ARS_URL = "http://121.156.104.161";
	
	@Value("${STORE.ARS_UID}")
	private String ARS_UID = "YAF8";
	
	@Override
	public void send(String target, SysTemplate template) throws Exception {
		ArsRequest req = new ArsRequest();
		req.setProperties(new HashMap<String, Object>());
		req.getProperties().put("receiver", target);
		
		
		int effected = sqlSession.insert("Common.insertArsRequest", req);
		if (effected > 0) {
			String callid = StringUtils.leftPad("" + req.getNewNo(), 5, "0"); 
			StringBuffer buf = new StringBuffer();
			buf.append(ARS_URL).append("/ctype/BrandServlet?mode=sign")
				.append("&cid=").append(target)
				.append("&uid=").append(ARS_UID)
				.append("&callid=").append(callid)
				.append("&eno=").append(template.getCode());
			
			String url = buf.toString();
			logger.debug("ARS Request : " + url);
			logger.debug("ARS response: " + HttpUtil.requestGetString(url, "euc-kr", 30, 30, null));
		}
	}

}
