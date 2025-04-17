package kr.co.pplus.store.api.jpa.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.co.pplus.store.api.jpa.model.CashBuy;
import kr.co.pplus.store.api.jpa.model.CashExchangeRate;
import kr.co.pplus.store.api.jpa.model.CashHistory;
import kr.co.pplus.store.api.jpa.model.CashLog;
import kr.co.pplus.store.api.jpa.model.bootpay.request.Token;
import kr.co.pplus.store.api.jpa.model.bootpay.response.ResToken;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidCashException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.CashBolService;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.User;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class CashService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(CashService.class);

	private final String BASE_URL = "https://api.bootpay.co.kr/";
	private final String URL_ACCESS_TOKEN = BASE_URL + "request/token";
	private final String URL_VERIFY = BASE_URL + "receipt";

	@Autowired
	CashBuyRepository cashBuyRepository;

	@Autowired
	CashLogRepository cashLogRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	CashBolService cashBolService;

	@Autowired
	CashHistoryRepository cashHistoryRepository;

	@Autowired
	CashExchangeRateRepository cashExchangeRateRepository;

	@Value("${STORE.BOOTPAY.CASH_APP_ID}")
	String CASH_APP_ID = "";

	@Value("${STORE.BOOTPAY.CASH_PRIVATE_KEY}")
	String CASH_PRIVATE_KEY = "";


	public String getAccessToken() throws Exception{

		logger.error("CASH_APP_ID : "+CASH_APP_ID);
		logger.error("CASH_PRIVATE_KEY : "+CASH_PRIVATE_KEY);

		Token token = new Token();
		token.application_id = CASH_APP_ID ;
		token.private_key = CASH_PRIVATE_KEY ;

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = getPost(URL_ACCESS_TOKEN, new StringEntity(new Gson().toJson(token), "UTF-8"));

		HttpResponse res = client.execute(post);
		String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
		ResToken resToken = new Gson().fromJson(str, ResToken.class);

		logger.debug("bootPay.getAccessToken() response : " + str) ;
		if(resToken.status == 200){
			return resToken.data.token;
		}else{
			return null;
		}
	}

	public HttpResponse verify(String receipt_id, String token) throws Exception {
		if(token == null || token.isEmpty()) throw new Exception("token 값이 비어있습니다.");

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = getGet(URL_VERIFY + "/" + receipt_id);
		get.setHeader("Authorization", token);
		return client.execute(get);
	}

	private HttpPost getPost(String url, StringEntity entity) {
		HttpPost post = new HttpPost(url);
		post.setHeader("Accept", "application/json");
		post.setHeader("Content-Type", "application/json");
		post.setHeader("Accept-Charset", "utf-8");
		post.setEntity(entity);
		return post;
	}

	private HttpGet getGet(String url) throws Exception {
		HttpGet get = new HttpGet(url);
		URI uri = new URIBuilder(get.getURI()).build();
		get.setURI(uri);
		return get;
	}


	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int insertCashBuy(User user, CashBuy cashBuy) throws ResultCodeException {

		try{
			String dateStr = AppUtil.localDatetimeNowString();
			String token = getAccessToken();

			if(token == null || token.isEmpty()){
				throw new InvalidCashException();
			}

			HttpResponse res = verify(cashBuy.getReceiptId(), token);
			String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
			JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
			JsonObject data = jsonObject.getAsJsonObject("data");

			if (!data.get("status").getAsString().equals("1")) {
				throw new Exception(" pay rejected !!!");
			}

			JsonObject payment_data = data.get("payment_data").getAsJsonObject();

			cashBuy.setSeqNo(null);
			cashBuy.setStatus(data.get("status").getAsInt());
			cashBuy.setCash(data.get("price").getAsInt());
			cashBuy.setCardName(payment_data.get("card_name").getAsString());
			cashBuy.setCardQuota(payment_data.get("card_quota").getAsString());
			cashBuy.setRegDatetime(dateStr);
			cashBuy.setModDatetime(dateStr);

			cashBuy = cashBuyRepository.saveAndFlush(cashBuy);

			CashLog cashLog = new CashLog();
			cashLog.setMemberSeqNo(cashBuy.getMemberSeqNo());
			cashLog.setPageSeqNo(cashBuy.getPageSeqNo());
			cashLog.setType("charge");
			cashLog.setCash(cashBuy.getCash());
			cashLog.setNote(cashBuy.getCash() + "원 캐시충전");
			cashLog.setRegDatetime(dateStr);
			cashLogRepository.save(cashLog);

			memberRepository.updateCash(user.getNo(), cashBuy.getCash().doubleValue());
//			cashBolService.increaseOnlyCash(user, Long.valueOf(cashBuy.getCash()));


			return Const.E_SUCCESS;
		}catch (Exception e){
			logger.error(e.toString());
			throw new InvalidCashException();
		}
	}

	public Page<CashLog> getCashLogList(Pageable pageable, Long pageSeqNo, String type){
		return cashLogRepository.findAllByPageSeqNoAndType(pageSeqNo, type, pageable);
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void updateCash(Long memberSeqNo, CashHistory cashHistory) {
		String dateStr = AppUtil.localDatetimeNowString();
		cashHistory.setRegDatetime(dateStr);
		cashHistoryRepository.save(cashHistory);
		Float cash = 0f;
		if (cashHistory.getType().equals("charge")) {
			cash = cashHistory.getCash();
		} else {
			cash = -cashHistory.getCash();
		}

		memberRepository.updateCash(memberSeqNo, cash.doubleValue());
	}

	public Page<CashHistory> getCashHistoryList(Pageable pageable, Long memberSeqNo) {
		return cashHistoryRepository.findAllByMemberSeqNo(memberSeqNo, pageable);
	}

	public CashHistory getCashHistory(Long seqNo) {
		return cashHistoryRepository.findBySeqNo(seqNo);
	}

	public List<CashExchangeRate> getCashExchangeRateList(){
		return cashExchangeRateRepository.findAll();
	}
}
