package kr.co.pplus.store.api.jpa.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.co.pplus.store.api.jpa.model.Supplier;
import kr.co.pplus.store.api.jpa.repository.SupplierRepository;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.util.XmlUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class DomeggookService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(DomeggookService.class);

	@Value("${STORE.TYPE}")
	String storeType ;

	@Autowired
	SupplierRepository supplierRepository;

	public JsonObject order(String seqNo, String deliveryType, String option, String memo, String deliinfo) {
		Supplier supplier = supplierRepository.findBySeqNo(2L);
		String sId = supplier.getSid();

		try {
			if(sId != null){
				List<NameValuePair> nameValuePairList = new ArrayList<>();
				nameValuePairList.add(new BasicNameValuePair("ver", "4.3"));
				nameValuePairList.add(new BasicNameValuePair("mode", "setOrder"));
				nameValuePairList.add(new BasicNameValuePair("aid", "61430ee02a56a22f62a40d872bb20034"));
				nameValuePairList.add(new BasicNameValuePair("id", "pplus1"));
				nameValuePairList.add(new BasicNameValuePair("sId", sId));
				nameValuePairList.add(new BasicNameValuePair("receipt", "1"));
				nameValuePairList.add(new BasicNameValuePair("item["+seqNo+"]", "supply||"+deliveryType+"||"+option+"||||"+memo));
				nameValuePairList.add(new BasicNameValuePair("deliinfo", deliinfo));
				nameValuePairList.add(new BasicNameValuePair("ie", "utf-8"));
				nameValuePairList.add(new BasicNameValuePair("oe", "utf-8"));
				nameValuePairList.add(new BasicNameValuePair("om", "json"));

				CloseableHttpClient client = HttpClients.createDefault();
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairList, "UTF-8");

				HttpPost post = new HttpPost("https://domeggook.com/ssl/api/");
				post.setEntity(entity);
				HttpResponse res = client.execute(post);
				String strResult = IOUtils.toString(res.getEntity().getContent(), "UTF-8");

				logger.debug(strResult);
				return new JsonParser().parse(strResult).getAsJsonObject();
			}
			return null;

		}catch (Exception e){
			return null;
		}
	}

	public String cancel(Long domemeOrderNo, String memo) {
		Supplier supplier = supplierRepository.findBySeqNo(2L);
		String sId = supplier.getSid();

		try {
			if(sId != null){
				List<NameValuePair> nameValuePairList = new ArrayList<>();
				nameValuePairList.add(new BasicNameValuePair("ver", "1.0"));
				nameValuePairList.add(new BasicNameValuePair("mode", "setOrderDeny"));
				nameValuePairList.add(new BasicNameValuePair("aid", "61430ee02a56a22f62a40d872bb20034"));
				nameValuePairList.add(new BasicNameValuePair("id", "pplus1"));
				nameValuePairList.add(new BasicNameValuePair("sId", sId));
				nameValuePairList.add(new BasicNameValuePair("type", "buy"));
				nameValuePairList.add(new BasicNameValuePair("no", domemeOrderNo.toString()));
				nameValuePairList.add(new BasicNameValuePair("memo", memo));

				CloseableHttpClient client = HttpClients.createDefault();
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairList, "UTF-8");

				HttpPost post = new HttpPost("https://domeggook.com/ssl/api/");
				post.setEntity(entity);
				HttpResponse res = client.execute(post);
				String strResult = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
				Document doc = XmlUtil.parseXml(strResult);
				Node root = XmlUtil.selectOne(doc, "/domeggook");
				if(root != null){
					String result = XmlUtil.getSafeNodeTextContent(root, "result");
					return result;
				}else{
					root = XmlUtil.selectOne(doc, "/errors");
					if(root != null){
						return XmlUtil.getSafeNodeTextContent(root, "decode");
					}
				}
				logger.error(strResult);
			}
			return null;

		}catch (Exception e){
			return null;
		}
	}

	public static void main(String argv[]) {
//		String deliveryType = "P";
//
//		StringBuilder option = new StringBuilder();
//		option.append("01_00|1");
//
//		String memo = "메모입니다.";
//
//		StringBuilder deliinfo = new StringBuilder();
//
//		deliinfo.append("김종경|");//받는사람
//		deliinfo.append("|");//이메일
//		deliinfo.append("21356|");//우편번호
//		deliinfo.append("인천광역시 부평구 장제로195번길 50|");//주소
//		deliinfo.append("영빈빌리지 302호|");//주소 상세
//		deliinfo.append(AppUtil.getPhoneNumber("01038007428")+"|");//연락처
//		deliinfo.append("|");//추가전화번호
//		deliinfo.append("럭키볼|");//상호명
//		deliinfo.append("|");//통관고유번호
//
//
//		order("11804743", deliveryType, option.toString(), memo, deliinfo.toString());
	}

}
