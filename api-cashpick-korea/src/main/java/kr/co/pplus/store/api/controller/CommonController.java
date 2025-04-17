package kr.co.pplus.store.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kr.co.pplus.store.api.annotation.AgentSessionUser;
import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.exception.InvalidArgumentException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.exception.UnknownException;
import kr.co.pplus.store.mvc.service.*;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.BaseResponse;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.util.RedisUtil;
import kr.co.pplus.store.util.StoreUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CommonController extends RootController {
	private final static Logger logger = LoggerFactory.getLogger(CommonController.class);
	@Autowired
	CommonService svc;

	@Autowired
	AttachmentService attachSvc;
	
	@Autowired
	UserService userSvc;
	
	@Autowired
	PageService pageSvc;
	
	@Autowired
	ObjectMapper om;
	
	@Autowired
	CacheManager cacheMgr;
	
	@Value("${STORE.DAUM_COORD_URL}")
	String DAUM_COORD_URL = "https://dapi.kakao.com/v2/local/search/address.json";
	
	@Value("${STORE.DAUM_COORD_KEY}")
	String DAUM_COORD_KEY = "8da7c5baa82bdb282190a323710ca8cf";

	String DAUM_COORD_2_ADDRESS_URL = "https://dapi.kakao.com/v2/local/geo/coord2address.json";
	
	@Value("${STORE.JUSO_URL}")
	private String JUSO_URL;
	
	@Value("${STORE.JUSO_KEY}")
	private String JUSO_KEY;

	@Value("${STORE.REDIS_PREFIX}")
	String REDIS_PREFIX = "pplus-";
	
	private String STEP_ADDR_ACCESS_TOKEN;


	@Autowired
	RankingService rankingSvc;

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/postAttachment/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Attachment.class)))
	public Map<String,Object> postAttachment(Session session, @RequestBody Attachment attachment, MultipartFile file) throws Exception {
		return result(Const.E_SUCCESS, "row", attachSvc.insert(attachment));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/saveOnlyS3/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Attachment.class)))
	public Map<String,Object> saveOnlyS3(Session session, Attachment attachment, MultipartFile file) throws Exception {
		attachSvc.saveOnlyS3(attachment, file);
		return result(Const.E_SUCCESS, "row", attachment);
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/uploadAttachment/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Attachment.class)))
	public Map<String,Object> uploadAttachment(Session session, Attachment attachment, MultipartFile file) throws Exception {
		attachSvc.upload(attachment, file);
		return result(Const.E_SUCCESS, "row", attachment);
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/uploadAttachment2/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Attachment.class)))
	public Map<String,Object> uploadAttachment(Session session, @RequestParam("file") MultipartFile file, @RequestParam("targetType") String targetType) throws Exception {
		Attachment attachment = new Attachment() ;
		attachment.setTargetType(targetType);
		attachSvc.upload(attachment, file);
		return result(Const.E_SUCCESS, "row", attachment);
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/deleteAttachment/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> deleteAttachment(Session session, Attachment attachment) throws ResultCodeException {
		attachSvc.delete(attachment);
		return result(Const.E_SUCCESS);
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/copyAttachment/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Attachment.class)))
	public Map<String,Object> copyAttachment(Session session, Attachment attachment) throws ResultCodeException {
		return result(Const.E_SUCCESS, "row", attachSvc.copy(attachment));
	}
	
	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/getDefaultImageList/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Attachment.class))))
	public Map<String,Object> getDefaultImageList(Attachment attachment) {
		//return result(200, "rows", attachSvc.getDefaultImageList(attachment));
		return result(Const.E_SUCCESS, "rows",RedisUtil.getInstance().getOpsHash(REDIS_PREFIX + "pageBackground", "defaultList")) ;
	}


//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/getRandomImage/**")
//	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Attachment.class)))
//	public Map<String,Object> getRandomImage(Attachment attachment) {
//		return result(Const.E_SUCCESS, "row", attachSvc.getRandomImage(attachment));
//	}
	
	/*@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/setDefaultImage/**")
	public Map<String,Object> setDefaultImage(Session session, DefaultImage defaultImage) throws Exception {
		Attachment attachment = new Attachment();
		attachSvc.saveByDefaultImage(defaultImage, attachment);
		return result(200, "row", attachment);
	}*/
	
	/*@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/setDefaultPageImage/**")
	public Map<String,Object> setDefaultPageImage(Session session, DefaultImage defaultImage) throws Exception {
		Attachment attachment = new Attachment();
		attachSvc.saveByDefaultPageImage(defaultImage, attachment);
		return result(200, "row", attachment);
	}*/
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/reporting/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> reporting(Session session, @RequestBody Report report) throws ResultCodeException {
		report.setReporter(session);
		return result(svc.reporting(report), "row", report);
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/searchAddress/**")
	@ApiResponse(responseCode = "200", description = "address", content = @Content(schema = @Schema(implementation = ResultAddress.class)))
	public Map<String,Object> searchAddress(SearchOpt opt) throws Exception {
		if (StringUtils.isEmpty(opt.getSearch()))
			throw new InvalidArgumentException("search", "empty");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("confmKey", JUSO_KEY);
		params.put("currentPage", opt.getPg() == null ? "1" : opt.getPg().toString());
		params.put("countPerPage", opt.getSz() == null ? "10" : opt.getSz().toString());
		params.put("keyword", opt.getSearch());
		params.put("resultType", "json");
		String res = StoreUtil.postRequest(JUSO_URL, params, "UTF-8", 30000, 30000);
		return om.readValue(res, Map.class);
	}




	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/getCountryConfigAll/**")
	@ApiResponse(responseCode = "200", description = "address", content = @Content(schema = @Schema(implementation = CountryConfig.class)))
	public Map<String,Object> getCountryConfigAll() {
		return result(Const.E_SUCCESS, "rows", svc.getCountryConfigAll());
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/removeCacheAll/**")
	public Map<String,Object> flushCache(Session session, String cache) {
		try{
			cacheMgr.getCache(cache).clear();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return result(Const.E_SUCCESS);
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/getCoordByAddress/**")
	@ApiResponse(responseCode = "200", description = "Coord", content = @Content(schema = @Schema(implementation = Coord.class)))
	public Map<String,Object> getCoordByAddress(Session session, String address) throws Exception {
		Coord coord = StoreUtil.converAddressToCoord(om, DAUM_COORD_URL, DAUM_COORD_KEY, address);
		if (coord == null)
			throw new UnknownException("coordinate", "null");
		return result(Const.E_SUCCESS, "row", coord);
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/getCoord2Address/**")
	@ApiResponse(responseCode = "200", description = "Coord", content = @Content(schema = @Schema(implementation = Coord.class)))
	public Map<String,Object> getCoordByAddress(Session session, String x, String y) throws Exception {
		Map map = StoreUtil.convertCoordToAddress(om, DAUM_COORD_2_ADDRESS_URL, DAUM_COORD_KEY, x, y);

		if (map == null)
			throw new UnknownException("convertCoordToAddress", "null");

		String address = ((Map)map.get("address")).get("address_name").toString();
		logger.debug("address : "+map.toString());
		logger.debug("address : "+address);
		Map<String, String> params = new HashMap<String, String>();
		params.put("confmKey", JUSO_KEY);
		params.put("currentPage", "1");
		params.put("countPerPage", "1");
		params.put("keyword", address);
		params.put("resultType", "json");
		String res = StoreUtil.postRequest(JUSO_URL, params, "UTF-8", 30000, 30000);
		Map<String,Object> result = om.readValue(res, Map.class);
		result.put("oldAddress", address);
		return result;
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/getNoticeCount/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> getNoticeCount(App app) {
		return result(Const.E_SUCCESS, "row", svc.getNoticeCount(app));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/getNoticeList/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Notice.class))))
	public Map<String,Object> getNoticeList(App app, SearchOpt opt) {
		return result(Const.E_SUCCESS, "rows", svc.getNoticeList(app, opt));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/getNotice/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Notice.class)))
	public Map<String,Object> getNotice(Notice notice) {
		return result(Const.E_SUCCESS, "row", svc.getNotice(notice));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/getFaqGroupAll/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FaqGroup.class))))
	public Map<String,Object> getFaqGroupApp(App app) {
		return result(Const.E_SUCCESS, "rows", svc.getFaqGroupAll(app));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/getFaqCount/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> getFaqCount(App app, FaqGroup group) {
		return result(Const.E_SUCCESS, "row", svc.getFaqCount(app, group));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/getFaqList/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Faq.class))))
	public Map<String,Object> getFaqList(App app, FaqGroup group, SearchOpt opt) {
		return result(Const.E_SUCCESS, "rows", svc.getFaqList(app, group, opt));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/getFaqCountByAppType/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> getFaqCountByAppType(String appType, String platform, FaqGroup group) {
		return result(Const.E_SUCCESS, "row", svc.getFaqCountByAppType(appType, platform, group));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/getFaqListByAppType/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Faq.class))))
	public Map<String,Object> getFaqListByAppType(String appType, String platform, FaqGroup group, SearchOpt opt) {
		return result(Const.E_SUCCESS, "rows", svc.getFaqListByAppType(appType, platform, group, opt));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/getFaq/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Faq.class)))
	public Map<String,Object> geteFaq(Faq faq) {
		return result(Const.E_SUCCESS, "row", svc.getFaq(faq));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/getTotalFriendCount/**")
	public Map<String,Object> getTotalFriendCount(Session session) {
		return result(Const.E_SUCCESS, "row", svc.getTotalFriendCount());
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/cpeReport/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> cpeReport(@RequestParam(value = "type", required = true)String type, @RequestParam(value = "id", required = true)String id) throws ResultCodeException {

		if(type.equals("zzal")){
			return result(Const.E_SUCCESS, "row", svc.requestZzal(id));
		}else{

			if(svc.getCpeReportCount(id, type) == 0){
				svc.insertCpeReport(id, type);
			}

			return result(Const.E_SUCCESS, "row", type + " : " + id);
		}
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/cpaReport/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> cpaReport(@RequestParam(value = "type", required = true)String type, @RequestParam(value = "id", required = true)String id, @RequestParam(value = "actionType", required = true)String actionType) throws ResultCodeException {

		if(svc.getCpaReportCount(id, type, actionType) == 0){
			svc.insertCpaReport(id, type, actionType);
		}

		return result(Const.E_SUCCESS, "row", "success");
	}

//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/refreshRankingWeek/**")
//	public void executeWeekTask() {
//		rankingSvc.updateWeekRanking();
//	}
//
//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/refreshRankingAll/**")
//	public void executeAllTask() {
//		rankingSvc.updateAllRanking();
//	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/makeQrCode")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> makeQrCode(String dataString) throws ResultCodeException{
		return result(Const.E_SUCCESS, "row", svc.makeQrCode(dataString));
	}


	/*
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/existsMobile/**")
	public Map<String,Object> existsMobile(String mobile) throws DuplicateMobileException {
		if (userSvc.existsUserByMobile(mobile))
			throw new DuplicateMobileException();
		return result(Const.E_SUCCESS);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/existsNickname/**")
	public Map<String,Object> existsNickname(String nickname) throws DuplicateNicknameException {
		if (userSvc.existsUserByNickname(nickname))
			throw new DuplicateNicknameException();
		return result(Const.E_SUCCESS);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/common/existsLoginId/**")
	public Map<String,Object> existsLoginId(String loginId) throws DuplicateLoginIdException {
		if (userSvc.existsUserByLoginId(loginId))
			throw new DuplicateLoginIdException();
		return result(Const.E_SUCCESS);
	}
	*/
}
