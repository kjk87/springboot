package kr.co.pplus.store.api.jpa.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.co.pplus.store.api.jpa.model.BuffCoinHistory;
import kr.co.pplus.store.api.jpa.model.BuffCoinInfo;
import kr.co.pplus.store.api.jpa.model.PointHistory;
import kr.co.pplus.store.api.jpa.repository.BuffCoinHistoryRepository;
import kr.co.pplus.store.api.jpa.repository.BuffCoinInfoRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.LackCostException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.exception.UnknownException;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.util.KeyGenerator;
import kr.co.pplus.store.util.SecureUtil;
import kr.co.pplus.store.util.WalletSecureUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class BuffWalletService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(BuffWalletService.class);

	@Value("${STORE.TYPE}")
	private String storeType = "STAGE";
	private String prodUrl = "https://user-api.lockup.buffwallet.net/";
	private String devUrl = "http://175.126.82.89:11080/";

	@Autowired
	BuffCoinInfoRepository buffCoinInfoRepository;

	@Autowired
	BuffCoinHistoryRepository buffCoinHistoryRepository;

	@Autowired
	PointService pointService;


	public String walletSignUp(User user, String password){

		password = SecureUtil.decryptMobileNumber(password);

		JsonObject jsonObject = new JsonObject();
		if(user.getAccountType().equals("pplus")){
			jsonObject.addProperty("type", "bunny");
		}else{
			jsonObject.addProperty("type", user.getAccountType());
		}

		jsonObject.addProperty("id", user.getLoginId().replace(user.getAppType() + "##", ""));
		jsonObject.addProperty("password", password);
		jsonObject.addProperty("key", user.getNo());
		jsonObject.addProperty("name", user.getNickname());
		jsonObject.addProperty("hp", user.getMobile().replace(user.getAppType() + "##", ""));
		jsonObject.addProperty("reqTime", System.currentTimeMillis());

		JsonObject params = new JsonObject();

		params.addProperty("data", WalletSecureUtil.encrypt(jsonObject.toString(), storeType));

		StringEntity entity = new StringEntity(params.toString() , "utf-8");


		CloseableHttpClient client = null;
		try {
			String url = "";
			if (storeType.equals("PROD")) {
				url = prodUrl + "user/bunny/signUp";
			} else {
				url = devUrl + "user/bunny/signUp";
			}

			if (StringUtils.isNotEmpty(url)) {
				logger.debug(url);

				client = HttpClients.createDefault();

				HttpPost post = AppUtil.getPost(url, entity);
				HttpResponse res = client.execute(post);

				String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
				logger.debug("result : " + str);
				JsonObject resObject = new JsonParser().parse(str).getAsJsonObject();

				String data = WalletSecureUtil.decrypt(resObject.get("data").getAsString(), storeType);

				logger.debug("walletSignUp : " + data);

				if(data != null){
					return new JsonParser().parse(data).getAsJsonObject().get("result").getAsString();
				}

			}
		} catch (IOException e) {
			logger.error("walletSignUp error : " + e.toString());
		} finally {
			if(client != null){

				try {
					client.close();
				}catch (IOException e){
					logger.error("client.close : " + e.toString());
				}

			}

		}

		return null;

	}

	public String walletSync(User user, String password){

		password = SecureUtil.decryptMobileNumber(password);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", user.getLoginId().replace(user.getAppType() + "##", ""));
		jsonObject.addProperty("password", password);
		jsonObject.addProperty("key", user.getNo());
		jsonObject.addProperty("name", user.getNickname());
		jsonObject.addProperty("hp", user.getMobile().replace(user.getAppType() + "##", ""));
		jsonObject.addProperty("reqTime", System.currentTimeMillis());

		JsonObject params = new JsonObject();

		params.addProperty("data", WalletSecureUtil.encrypt(jsonObject.toString(), storeType));

		StringEntity entity = new StringEntity(params.toString() , "utf-8");


		CloseableHttpClient client = null;
		try {
			String url = "";
			if (storeType.equals("PROD")) {
				url = prodUrl + "user/bunny/sync";
			} else {
				url = devUrl + "user/bunny/sync";
			}

			if (StringUtils.isNotEmpty(url)) {
				logger.debug(url);

				client = HttpClients.createDefault();

				HttpPost post = AppUtil.getPost(url, entity);
				HttpResponse res = client.execute(post);

				String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
				logger.debug("result : " + str);
				JsonObject resObject = new JsonParser().parse(str).getAsJsonObject();

				String data = WalletSecureUtil.decrypt(resObject.get("data").getAsString(), storeType);

				logger.debug("walletSync : " + data);

				if(data != null){
					return new JsonParser().parse(data).getAsJsonObject().get("result").getAsString();
				}

			}
		} catch (IOException e) {
			logger.error("walletSync error : " + e.toString());
		} finally {
			if(client != null){

				try {
					client.close();
				}catch (IOException e){
					logger.error("client.close : " + e.toString());
				}

			}

		}

		return null;

	}

	public Map<String, Object> walletBalance(User user){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", user.getLoginId().replace(user.getAppType() + "##", ""));
		jsonObject.addProperty("key", user.getNo());
		jsonObject.addProperty("reqTime", System.currentTimeMillis());

		JsonObject params = new JsonObject();

		params.addProperty("data", WalletSecureUtil.encrypt(jsonObject.toString(), storeType));

		StringEntity entity = new StringEntity(params.toString() , "utf-8");


		CloseableHttpClient client = null;
		try {
			String url = "";
			if (storeType.equals("PROD")) {
				url = prodUrl + "user/bunny/balance";
			} else {
				url = devUrl + "user/bunny/balance";
			}

			if (StringUtils.isNotEmpty(url)) {
				logger.debug(url);

				client = HttpClients.createDefault();

				HttpPost post = AppUtil.getPost(url, entity);
				HttpResponse res = client.execute(post);

				String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
				logger.debug("result : " + str);
				JsonObject resObject = new JsonParser().parse(str).getAsJsonObject();

				String data = WalletSecureUtil.decrypt(resObject.get("data").getAsString(), storeType);

				logger.debug("walletBalance : " + data);

				if(data != null){

					Map<String,Object> map = new HashMap<String,Object>();
					map = (Map<String,Object>) new Gson().fromJson(data, map.getClass());
					return map;

				}

			}
		} catch (IOException e) {
			logger.error("walletBalance error : " + e.toString());
		} finally {
			if(client != null){

				try {
					client.close();
				}catch (IOException e){
					logger.error("client.close : " + e.toString());
				}

			}

		}

		return null;

	}

	public BigDecimal coinBalance(User user){

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", user.getLoginId().replace(user.getAppType() + "##", ""));
		jsonObject.addProperty("key", user.getNo());
		jsonObject.addProperty("reqTime", System.currentTimeMillis());

		JsonObject params = new JsonObject();

		params.addProperty("data", WalletSecureUtil.encrypt(jsonObject.toString(), storeType));

		StringEntity entity = new StringEntity(params.toString() , "utf-8");


		CloseableHttpClient client = null;

		try {
			String url = "";
			if (storeType.equals("PROD")) {
				url = prodUrl + "user/bunny/balance";
			} else {
				url = devUrl + "user/bunny/balance";
			}

			if (StringUtils.isNotEmpty(url)) {
				logger.debug(url);

				client = HttpClients.createDefault();

				HttpPost post = AppUtil.getPost(url, entity);
				HttpResponse res = client.execute(post);

				String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
				logger.debug("result : " + str);
				JsonObject resObject = new JsonParser().parse(str).getAsJsonObject();

				String data = WalletSecureUtil.decrypt(resObject.get("data").getAsString(), storeType);

				logger.debug("walletBalance : " + data);

				if(data != null){

					BigDecimal balance = new JsonParser().parse(data).getAsJsonObject().get("balances").getAsJsonObject().get("BUFF").getAsJsonObject().get("balance").getAsBigDecimal();
					return balance;
				}

			}
		} catch (IOException e) {
			logger.error("walletBalance error : " + e.toString());
		} finally {
			if(client != null){

				try {
					client.close();
				}catch (IOException e){
					logger.error("client.close : " + e.toString());
				}

			}

		}

		return null;

	}

	public Map<String, Object> getBuffCoinBalance(User user){

		Map<String, Object> map = new HashMap<>();

		BigDecimal balance = coinBalance(user);

		BuffCoinInfo buffCoinInfo = buffCoinInfoRepository.findBySeqNo(1L);
		int krw = balance.multiply(new BigDecimal(buffCoinInfo.getKrw())).intValue();
		map.put("buff", balance);
		map.put("krw", buffCoinInfo.getKrw());
		map.put("totalKrw", krw);

		return map;

	}

	public void exchangeBuffCoinToPoint(User user, BigDecimal exchangeCoin) throws ResultCodeException {
		BigDecimal balance = coinBalance(user);
		if(exchangeCoin.compareTo(balance) > 0){
			throw new LackCostException();
		}

		BuffCoinInfo buffCoinInfo = buffCoinInfoRepository.findBySeqNo(1L);
		Integer point = exchangeCoin.multiply(new BigDecimal(buffCoinInfo.getKrw())).intValue();
		String result = updateCoin(user, "POINT", false, exchangeCoin);
		if(result.equals("SUCCESS")){
			PointHistory pointHistory = new PointHistory();
			pointHistory.setMemberSeqNo(user.getNo());
			pointHistory.setType("charge");
			pointHistory.setPoint(point.floatValue());
			pointHistory.setSubject("버프코인 교환");
			pointService.updatePoint(user.getNo(), pointHistory);
		}else{
			throw new UnknownException();
		}
	}

	public String updateCoin(User user, String type, boolean isIncrease, BigDecimal amount){

		amount = amount.setScale(4, BigDecimal.ROUND_DOWN);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", user.getLoginId().replace("luckyball##", ""));
		jsonObject.addProperty("key", user.getNo());
		jsonObject.addProperty("type", type);//(PAYMENT: 상품 결제, SIGNUP: 회원가입, EVENT: 이벤트, POINT: 포인트 전환, TOKEN: 토큰 변환)
		jsonObject.addProperty("amount", amount);
		jsonObject.addProperty("isIncrease", isIncrease);
		jsonObject.addProperty("reqTime", System.currentTimeMillis());

		logger.error(jsonObject.toString());
		JsonObject params = new JsonObject();

		params.addProperty("data", WalletSecureUtil.encrypt(jsonObject.toString(), storeType));

		StringEntity entity = new StringEntity(params.toString() , "utf-8");


		CloseableHttpClient client = null;
		try {
			String url = "";
			if (storeType.equals("PROD")) {
				url = prodUrl + "user/bunny/buff";
			} else {
				url = devUrl + "user/bunny/buff";
			}

			if (StringUtils.isNotEmpty(url)) {
				logger.debug(url);

				client = HttpClients.createDefault();

				HttpPost post = AppUtil.getPost(url, entity);
				HttpResponse res = client.execute(post);

				String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
				logger.debug("result : " + str);
				JsonObject resObject = new JsonParser().parse(str).getAsJsonObject();

				String data = WalletSecureUtil.decrypt(resObject.get("data").getAsString(), storeType);

				logger.debug("walletSync : " + data);

				if(data != null){
					String result =  new JsonParser().parse(data).getAsJsonObject().get("result").getAsString();

					if(result.equals("SUCCESS")){
						BuffCoinHistory buffCoinHistory = new BuffCoinHistory();
						buffCoinHistory.setMemberSeqNo(user.getNo());
						if(isIncrease){
							buffCoinHistory.setType("increase");
						}else{
							buffCoinHistory.setType("decrease");
						}

						buffCoinHistory.setCoin(amount);
						//(PAYMENT: 상품 결제, SIGNUP: 회원가입, EVENT: 이벤트, POINT: 포인트 전환, TOKEN: 토큰 변환)
						switch (type){
							case "PAYMENT":
								buffCoinHistory.setSubject("상품 결제");
								break;
							case "SIGNUP":
								buffCoinHistory.setSubject("회원가입");
								break;
							case "EVENT":
								buffCoinHistory.setSubject("이벤트");
								break;
							case "POINT":
								buffCoinHistory.setSubject("포인트");
								break;
							case "TOKEN":
								buffCoinHistory.setSubject("토큰 변환");
								break;
						}
						buffCoinHistory.setRegDatetime(AppUtil.localDatetimeNowString());
						buffCoinHistoryRepository.save(buffCoinHistory);
					}

					return result;
				}

			}
		} catch (IOException e) {
			logger.error("walletSync error : " + e.toString());
		} finally {
			if(client != null){

				try {
					client.close();
				}catch (IOException e){
					logger.error("client.close : " + e.toString());
				}

			}

		}

		return null;

	}

	public String duplicateUser(User user){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", user.getLoginId().replace(user.getAppType() + "##", ""));
		jsonObject.addProperty("reqTime", System.currentTimeMillis());

		JsonObject params = new JsonObject();

		params.addProperty("data", WalletSecureUtil.encrypt(jsonObject.toString(), storeType));

		StringEntity entity = new StringEntity(params.toString() , "utf-8");


		CloseableHttpClient client = null;
		try {
			String url = "";
			if (storeType.equals("PROD")) {
				url = prodUrl + "user/bunny/duplicateUser";
			} else {
				url = devUrl + "user/bunny/duplicateUser";
			}

			if (StringUtils.isNotEmpty(url)) {
				logger.debug(url);

				client = HttpClients.createDefault();

				HttpPost post = AppUtil.getPost(url, entity);
				HttpResponse res = client.execute(post);
				String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
				logger.debug("result : " + str);
				JsonObject resObject = new JsonParser().parse(str).getAsJsonObject();

				String data = WalletSecureUtil.decrypt(resObject.get("data").getAsString(), storeType);

				logger.debug("duplicateUser : " + data);

				if(data != null){
					return new JsonParser().parse(data).getAsJsonObject().get("result").getAsString();
				}

			}
		} catch (IOException e) {
			logger.error("duplicateUser error : " + e.toString());
		} finally {
			if(client != null){

				try {
					client.close();
				}catch (IOException e){
					logger.error("client.close : " + e.toString());
				}

			}

		}

		return null;

	}

	public void getCoinValue(){

		Long buffKrw = null;

		CloseableHttpClient client = null;

		try {
			String apiKey = "a46cb656e96c186d";
			String secretKey = "bcd7bf8978c461ae";

			String pairName = "BUFF/BTC";
			String type = "0";
			String min = "1";
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String startDateTime = sdf.format(now);
			String cnt = "1";

			client = HttpClients.createDefault();

			String secretHash = KeyGenerator.sha256(apiKey + pairName + type + min + startDateTime + cnt + secretKey);

			List<NameValuePair> form = new ArrayList<>();
			form.add(new BasicNameValuePair("apiKey", apiKey));
			form.add(new BasicNameValuePair("pairName", pairName));
			form.add(new BasicNameValuePair("type", type));
			form.add(new BasicNameValuePair("min", min));
			form.add(new BasicNameValuePair("startDateTime", startDateTime));
			form.add(new BasicNameValuePair("cnt", cnt));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);

			logger.debug(entity.toString());

			HttpPost post = new HttpPost("https://api2.foblgate.com/api/chart/selectChart");
			post.setHeader("SecretHeader", secretHash);
			post.setEntity(entity);

			HttpResponse res = client.execute(post);
			String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
			logger.debug("result : " + str);
			JsonObject resObject = new JsonParser().parse(str).getAsJsonObject();
			String series = resObject.get("data").getAsJsonObject().get("series").getAsJsonArray().get(0).getAsString();
			String price = series.split("\\|")[4];

			HttpGet get = new HttpGet("https://api.upbit.com/v1/ticker?markets=KRW-BTC");
			get.setHeader("accept", "application/json");
			res = client.execute(get);
			str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
			logger.debug("result : " + str);
			double btcPrice = new JsonParser().parse(str).getAsJsonArray().get(0).getAsJsonObject().get("trade_price").getAsDouble();
			double buffPrice =  Double.parseDouble(price) * btcPrice;

			buffKrw = Math.round(buffPrice);

			BuffCoinInfo buffCoinInfo = buffCoinInfoRepository.findBySeqNo(1L);
			if(buffCoinInfo == null){
				buffCoinInfo = new BuffCoinInfo();
				buffCoinInfo.setSeqNo(1L);
			}
			buffCoinInfo.setBtc(Double.parseDouble(price));
			buffCoinInfo.setKrw(buffKrw.intValue());
			buffCoinInfo.setModDatetime(AppUtil.localDatetimeNowString());
			buffCoinInfoRepository.save(buffCoinInfo);

		}catch (Exception e){
			logger.error(e.toString());
		}finally {
			if(client != null){

				try {
					client.close();
				}catch (IOException e){
					logger.error("client.close : " + e.toString());
				}

			}

		}

	}


    public static void main(String args[]) {
		BigDecimal amount = new BigDecimal(33.32);
		amount = amount.setScale(4, BigDecimal.ROUND_DOWN);
		System.out.println(amount);
    }
}
