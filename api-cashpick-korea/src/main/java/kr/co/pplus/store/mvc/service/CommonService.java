package kr.co.pplus.store.mvc.service;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import kr.co.pplus.store.api.jpa.model.zzal.ZzalRequest;
import kr.co.pplus.store.api.jpa.model.zzal.ZzalResponse;
import kr.co.pplus.store.exception.AlreadyExistsException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.exception.UnknownException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.util.DateUtil;
import kr.co.pplus.store.util.KeyGenerator;
import kr.co.pplus.store.util.S3;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional(transactionManager = "transactionManager")
public class CommonService extends RootService {
	private static Logger logger = LoggerFactory.getLogger(CommonService.class);
	
	
//	@Autowired
//	private CommonDao dao;

	@Value("${STORE.TYPE}")
	private String STORE_TYPE;

	@Value("${STORE.CDN_PATH}")
	private String CDN_PATH;

	@Value("${STORE.CDN_URL}")
	private String CDN_URL;
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer reporting(Report report) throws ResultCodeException {
		int exists = sqlSession.selectOne("Common.existsReport", report);
		if (exists > 0)
			throw new AlreadyExistsException("report", "exists");
		
		int effected = sqlSession.insert("Common.insertReport", report);
		
		if (effected == 0)
			throw new UnknownException();
		
		return Const.E_SUCCESS;
	}
	
	public NoOnlyKey getTrTest(NoOnlyKey value) {
		return sqlSession.selectOne("Common.getTrTest", value);
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int insertTrTest(NoOnlyKey value) throws ResultCodeException {
		try {
			int effected = sqlSession.insert("Common.insertTrTest", value);
			
			NoOnlyKey saved = getTrTest(value);

			ParamMap map = new ParamMap() ;
			map.put("no", value.getNo()) ;
			map.put("name", value.getNo().toString()) ;
			effected = sqlSession.insert("Common.insertSubTrTest", map) ;
			
			value.setNo(value.getNo() + 1);
			effected = insertSubTrTest(value);
			return effected;
		} catch (Exception ex) {
			throw new UnknownException("test", "test");
		}
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int insertSubTrTest(NoOnlyKey value) throws ResultCodeException {
		try {
			ParamMap map = new ParamMap() ;
			map.put("no", value.getNo()) ;
			map.put("name", value.getNo().toString()) ;
			int effected = sqlSession.insert("Common.insertSubTrTest", map) ;
			return effected;
		} catch (Exception ex) {
			throw new UnknownException("test1", "test1");
		}
	}
	
	public List<CountryConfig> getCountryConfigAll() {
		return sqlSession.selectList("Common.getCountryConfigAll");
	}
	
	public CountryConfig getCountryConfig(Country country) {
		return sqlSession.selectOne("Common.getCountryConfig",country.getNo());
	}

	public String getShippingKey() {
		Country country = new Country();
		country.setNo(1L);
		CountryConfig config = getCountryConfig(country);
		if (config != null && config.getProperties() != null && config.getProperties().containsKey("shippingKey"))
			return ((String)config.getProperties().get("shippingKey"));
		return null;
	}

	public int getPurchaseWait() {
		Country country = new Country();
		country.setNo(1L);
		CountryConfig config = getCountryConfig(country);
		if (config != null && config.getProperties() != null && config.getProperties().containsKey("purchaseWait"))
			return ((Integer)config.getProperties().get("purchaseWait"));
		return 0;
	}
	
	public int getSmsPrice(Country country) {
		CountryConfig config = getCountryConfig(country);
		if (config != null && config.getProperties() != null && config.getProperties().containsKey("smsPrice"))
			return ((Integer)config.getProperties().get("smsPrice"));
		return 0;
	}
	
	public int getLmsPrice(Country country) {
		CountryConfig config = getCountryConfig(country);
		if (config != null && config.getProperties() != null && config.getProperties().containsKey("lmsPrice"))
			return ((Integer)config.getProperties().get("lmsPrice"));
		return 0;
	}

	public int getAdvertiseSmsPrice(Country country) {
		CountryConfig config = getCountryConfig(country);
		if (config != null && config.getProperties() != null && config.getProperties().containsKey("adSmsPrice"))
			return ((Integer)config.getProperties().get("adSmsPrice"));
		return 0;
	}
	
	public int getAdvertiseLmsPrice(Country country) {
		CountryConfig config = getCountryConfig(country);
		if (config != null && config.getProperties() != null && config.getProperties().containsKey("adLmsPrice"))
			return ((Integer)config.getProperties().get("adLmsPrice"));
		return 0;
	}

	public int getPushPrice(Country country) {
		CountryConfig config = getCountryConfig(country);
		if (config != null && config.getProperties() != null && config.getProperties().containsKey("pushPrice"))
			return ((Integer)config.getProperties().get("pushPrice"));
		return 0;
	}
	
	public int getBolRatio(Country country) {
		CountryConfig config = getCountryConfig(country);
		if (config != null && config.getProperties() != null && config.getProperties().containsKey("bolRatio"))
			return (Integer)config.getProperties().get("bolRatio");
		return 0;
	}
	
	public int getTaxRatio(Country country) {
		CountryConfig config = getCountryConfig(country);
		if (config != null && config.getProperties() != null && config.getProperties().containsKey("taxRatio"))
			return (Integer)config.getProperties().get("taxRatio");
		return 0;
	}


	public int getProfileReward(Country country){
		CountryConfig config = getCountryConfig(country);
		if (config != null && config.getProperties() != null && config.getProperties().containsKey("profileReward"))
			return (Integer)config.getProperties().get("profileReward");
		return 0;
	}
	
	public int getNoticeCount(App app) {
		return sqlSession.selectOne("Common.getNoticeCount", app);
	}
	
	public List<Notice> getNoticeList(App app, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("app", app) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Common.getNoticeList", map) ;
	}
	
	public Notice getNotice(Notice notice) {
		return sqlSession.selectOne("Common.getNotice",notice);
	}
	
	public List<FaqGroup> getFaqGroupAll(App app) {
		ParamMap map = new ParamMap() ;
		map.put("app", app) ;
		return sqlSession.selectList("Common.getFaqGroupAll", map);
	}

	public int getFaqCountByAppType(String appType, String platform, FaqGroup group) {
		ParamMap map = new ParamMap() ;
		map.put("appType", appType) ;
		map.put("platform", platform);
		map.put("group", group) ;
		return sqlSession.selectOne("Common.getFaqCountByAppType", map) ;
	}

	public List<Faq> getFaqListByAppType(String appType, String platform, FaqGroup group, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("appType", appType) ;
		map.put("platform", platform);
		map.put("group", group) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Common.getFaqListByAppType", map) ;
	}

	public int getFaqCount(App app, FaqGroup group) {
		ParamMap map = new ParamMap() ;
		map.put("app", app) ;
		map.put("group", group) ;
		return sqlSession.selectOne("Common.getFaqCount", map) ;
	}
	
	public List<Faq> getFaqList(App app, FaqGroup group, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("app", app) ;
		map.put("group", group) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Common.getFaqList", map) ;
	}
	
	public Faq getFaq(Faq faq) {
		return sqlSession.selectOne("Common.getFaq", faq);
	}
	
	public Integer saveTerms(Terms terms) {
		if (terms.getCompulsory() == null)
			terms.setCompulsory(false);
		
		if (terms.getStatus() == null)
			terms.setStatus("active");
		
		terms.setRegUser(new User());
		terms.getRegUser().setNo(3L);
		int exists = sqlSession.selectOne("Common.existsTerms", terms.getCode());
		
		int effected = 0;
		if (exists > 0) {
			effected = sqlSession.update("Common.updateTerms", terms);
		} else {
			effected = sqlSession.insert("Common.insertTerms", terms);
		}
		
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer insertUserRequest(User user, UserRequest req) throws ResultCodeException {
		req.setUser(user);
		int effected = sqlSession.insert("Common.insertUserRequest", req);
		if (effected == 0)
			throw new UnknownException();
		
		UserRequestProc proc = new UserRequestProc();
		proc.setRequest(req);
		proc.setNo(1);
		proc.setProcStatus(req.getStatus());
		proc.setActor(user);
		
		effected = sqlSession.insert("Common.insertUserRequestProc", proc);
		if (effected == 0)
			throw new UnknownException();
		
		return Const.E_SUCCESS;
	}
	
	
	public boolean receiptValidation(Map<String,Object> req) throws Exception {
		return false;
	}
	
	public int getTotalFriendCount() {
		return sqlSession.selectOne("Common.getTotalFriendCount");
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ZzalResponse requestZzal(String participateID) throws ResultCodeException {

		try {
			ZzalRequest data = new ZzalRequest();
			data.setParticipateID(participateID);
			data.setAdvertiseID("a1aeba26216909d9e0010b9f7bb352d1");
//			data.setAdvertiseID("ecd54f60fe55c2e7d72ca3f6c89b3bd6");
			data.setActionResult(1);
			Gson gson = new Gson() ;
			String jsonStr = gson.toJson(data) ;
			logger.debug("Zzal Request: " + jsonStr) ;
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost("https://api.zzal.funple.com/advertising/offerwall/complete/action");
//			HttpPost post = new HttpPost("https://api-test.zzal.funple.com/advertising/offerwall/complete/action");
			post.setEntity(new StringEntity(jsonStr));
			post.addHeader("content-type", "application/json");
			post.addHeader("x-client-id", "269021d135654a86809ca5849b6b188b");
			post.addHeader("authentication", "Basic MjY5MDIxZDEzNTY1NGE4NjgwOWNhNTg0OWI2YjE4OGI6MmYyNGNlNDk3YTA2NDRjMDgwZDdmNzQyNjYzOTE5NWFiMjQ4NTMwOTg4NTc0NjIyOTlmM2IwOThkOWY0MzAwMTBjMGM1ZTdjYjQ0NzRkNjlhNTI4NjlmMmY0ZTY4YWFj");
			post.addHeader("accept-version", "1.0.0");
			HttpResponse res = client.execute(post) ;

			ZzalResponse zzalRes  = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), ZzalResponse.class) ;
			client.close() ;
			logger.debug("zzal resutcode : "+res.getStatusLine().getStatusCode());
			if(res.getStatusLine().getStatusCode() == 200){
				insertCpeReport(participateID, "zzal");
			}
			logger.debug("Zzal Response: " + gson.toJson(zzalRes)) ;
			return zzalRes;
		}catch (Exception e){
			logger.debug("Zzal Response: " + e.toString()) ;
			return null;
		}
	}

	public int getCpeReportCount(String id, String type) {
		ParamMap map = new ParamMap() ;
		map.put("id", id) ;
		map.put("type", type) ;
		return sqlSession.selectOne("Common.getCpeReportCount", map) ;
	}

	public int insertCpeReport(String id, String type) {
		ParamMap map = new ParamMap() ;
		map.put("id", id) ;
		map.put("type", type) ;

		return sqlSession.insert("Common.insertCpeReport", map) ;
	}

	public int getCpaReportCount(String id, String type, String actionType) {
		ParamMap map = new ParamMap() ;
		map.put("id", id) ;
		map.put("type", type) ;
		map.put("actionType", actionType) ;
		return sqlSession.selectOne("Common.getCpaReportCount", map) ;
	}

	public int insertCpaReport(String id, String type, String actionType){
		ParamMap map = new ParamMap() ;
		map.put("id", id) ;
		map.put("type", type) ;
		map.put("actionType", actionType) ;
		return sqlSession.insert("Common.insertCpaReport", map) ;
	}

	public String makeQrCode(String dataString) throws ResultCodeException{


		try {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();

			String url = new String(dataString.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
			BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 300, 300);

			String strPath = "/tmp/" + KeyGenerator.generateKey() + "_download_qr.png";

			File tempFile = new File(strPath);

			Path path = FileSystems.getDefault().getPath(strPath);
			MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

			Date now = DateUtil.getCurrentDate();
			String fileSubPath = "QR/"+DateUtil.getDate("yyyy/MM/dd", now);
			String filePath = CDN_PATH + fileSubPath;

			long currentMillis = System.currentTimeMillis();
			String fileName = currentMillis+"_download_qr.png";

			S3.getInstance(STORE_TYPE).putS3(tempFile, filePath + File.separator + fileName) ;
			tempFile.delete();

			return CDN_URL + fileSubPath + File.separator + fileName;

		}catch (Exception e){
			logger.error(e.toString());
			throw new UnknownException("makeQrCode");
		}

	}
}
