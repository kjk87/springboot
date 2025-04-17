package kr.co.pplus.store.api.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.exception.NotFoundTargetException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.ArticleService;
import kr.co.pplus.store.mvc.service.AuthService;
import kr.co.pplus.store.mvc.service.UserService;
import kr.co.pplus.store.queue.MsgProducer;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.BaseResponse;
import kr.co.pplus.store.type.dto.JoinUser;
import kr.co.pplus.store.type.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "AuthController", description = "인증 api")
public class AuthController extends RootController {
	
	@Autowired
	AuthService svc;
	
	@Autowired
	UserService userSvc;

	@Autowired
	ArticleService articleSvc;
	
	@Autowired
	MsgProducer producer;

	@Value("${STORE.REDIS_PREFIX}")
	static String REDIS_PREFIX = "pplus-";

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/getAppVersion/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = AppVersion.class)))
	public Map<String,Object> getAppVersion(AppVersion appVersion) throws ResultCodeException {
		AppVersion saved = svc.getAppVersion(appVersion);
		return result(Const.E_SUCCESS, "row", saved);
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/existsUser/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> existsUser(User user) throws ResultCodeException {
		return result(svc.existsUser(user));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/getUserByLoginIdAndMobile/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = User.class)))
	public Map<String,Object> getUserByLoginIdAndMobile(User user) throws ResultCodeException {
		return result(Const.E_SUCCESS, "row", svc.getUserByLoginIdAndMobile(user));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/join/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = JoinUser.class)))
	public Map<String,Object> join(@RequestBody JoinUser user) throws ResultCodeException {

		Integer ret = svc.join(user);
		if (Const.E_SUCCESS.equals(ret)) {

			producer.push(user);
		}

		return result(ret, "row", user);
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/updateUserAccount/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = User.class)))
	public Map<String,Object> updateUserAccount(Session session, String loginId, String password, String accountType) throws ResultCodeException {
		Integer ret = svc.updateUserAccount(session, loginId, password, accountType);
		return result(ret, "row", session);
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/updateUserAccountForAdmin/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = User.class)))
	public Map<String,Object> updateUserAccountForAdmin(Long memberSeqNo, String loginId, String password) throws ResultCodeException {
		Integer ret = svc.updateUserAccountForAdmin(memberSeqNo, loginId, password);
		return result(ret);
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/joinNotVerification/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = JoinUser.class)))
	public Map<String,Object> joinNotVerification(@RequestBody JoinUser user) throws ResultCodeException {
		Integer ret = svc.joinWithVerification(user, false);
		return result(ret, "row", user);
	}


	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/levelup/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = JoinUser.class)))
	public Map<String,Object> levelup(@RequestBody JoinUser user) throws ResultCodeException {
		Integer ret = svc.levelup(user);
		return result(ret, "row", user);
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/login/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = User.class)))
	public Map<String,Object> login(User user, Boolean encrypted) {
		return svc.login(user, encrypted);
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/existsDevice/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Session.class)))
	public Map<String,Object> existsDevice(UserDevice device) throws ResultCodeException {
		return svc.existsDevice(device);
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/registDevice/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Session.class)))
	public Map<String,Object> registDevice(@RequestBody Session session) throws ResultCodeException {
		return svc.registDevice(session);
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/updatePushKey/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> updatePushKey(Session session, @RequestParam("pushKey") String pushKey) throws ResultCodeException {
		svc.updatePushKey(session, pushKey);
		return result(Const.E_SUCCESS);
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/getUserByLoginId/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = User.class)))
	public Map<String,Object> getUserByLoginId(User user) {
		User exists = svc.getUserByLoginId(user);
		return result(exists == null ? 501 : 200, "row", exists);
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/getSession/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Session.class)))
	public Map<String,Object> getSession(HttpServletRequest req, String sessionKey) {
		if (sessionKey == null || sessionKey.isEmpty()) {
			sessionKey = req.getHeader("sessionKey");
			if (sessionKey == null || sessionKey.isEmpty()) {
				Cookie[] cookies = req.getCookies();
				for (Cookie cookie : cookies) {
					if (cookie.getName().equalsIgnoreCase("sessionKey")) {
						sessionKey = cookie.getValue();
						break;
					}
				}

				if (sessionKey == null || sessionKey.isEmpty())
					sessionKey = req.getParameter("sessionKey");

			}
		}

		try {
			Session session = svc.getSession(sessionKey);
			return result(Const.E_SUCCESS, "row", session);
		} catch (ResultCodeException ex) {
			return result(ex.getResultCode());
		}
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/reloadSession/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Session.class)))
	public Map<String,Object> reloadSession(Session session) throws ResultCodeException {
		return result(Const.E_SUCCESS, "row", svc.getReloadSession(session));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/refreshSessionKey/**")
	public Map<String,Object> refreshSessionKey(String sessionKey, String refreshKey, String appKey, String deviceId) throws ResultCodeException {

		return result(Const.E_SUCCESS, "row", svc.refreshSessionKey(sessionKey, refreshKey, appKey, deviceId));

	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/getActiveTermsAll/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Terms.class))))
	public Map<String,Object> getActiveTermsAll(App app) {

		if(app.getAppKey().equals("com.pplus.prnumberuser") || app.getAppKey().equals("com.PPleCompany.PNumber")){
			return result(200, "rows", svc.getActiveTermsAllByAppType("pplus", null));
		}else{
			return result(200, "rows", svc.getActiveTermsAll(app));
		}
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/getNotSignedActiveTermsAll/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Terms.class))))
	public Map<String,Object> getNotSignedActiveTermsAll(Session session, User user, App app) {
		if(app.getAppKey().equals("com.pplus.prnumberuser") || app.getAppKey().equals("com.PPleCompany.PNumber")){
			if (user.getNo() != null){
				return result(200, "rows", svc.getNotSignedActiveTermsAllByAppType(user, "pplus"));
			}

			return result(200, "rows", svc.getNotSignedActiveTermsAllByAppType(session, "pplus"));
		}else{
			if (user.getNo() != null){
				return result(200, "rows", svc.getNotSignedActiveTermsAll(user, app));
			}

			return result(200, "rows", svc.getNotSignedActiveTermsAll(session, app));
		}

	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/getActiveTermsAllByAppType/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Terms.class))))
	public Map<String,Object> getActiveTermsAllByAppType(String appType, String type) {
		return result(200, "rows", svc.getActiveTermsAllByAppType(appType, type));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/getNotSignedActiveTermsAllByAppType/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Terms.class))))
	public Map<String,Object> getNotSignedActiveTermsAll(Session session, User user, String appType) {
		if (user.getNo() != null){
			return result(200, "rows", svc.getNotSignedActiveTermsAllByAppType(user, appType));
		}

		return result(200, "rows", svc.getNotSignedActiveTermsAllByAppType(session, appType));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/agreeTerms/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> agreeTerms(Session session, Terms terms) {
		return result(svc.agreeTerms(session, terms));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/agreeTermsList/**")
	public Map<String,Object> agreeTermsList(Session session, @RequestParam("termsNo") Long[] nos) {
		List<Terms> termsList = new ArrayList<Terms>();
		for (Long no : nos) {
			Terms t = new Terms(no);
			termsList.add(t);
		}
		return result(svc.agreeTermsList(session, termsList));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/getAgent/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Agent.class)))
	public Map<String,Object> getAgent(Session session, @RequestParam("code") String code) {
		Agent agent = new Agent() ;
		agent.setCode(code) ;
		Agent saved = svc.getAgentByCode(agent);
		if (saved != null)
			return result(Const.E_SUCCESS, "row", saved);
		return result(Const.E_NOTFOUND);
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/activatePage/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = JoinUser.class)))
	public Map<String,Object> activatePage(Session session, @RequestBody JoinUser user) throws ResultCodeException {
		return result(svc.activatePage(user), "row", user);
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/startPage/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Page.class)))
	public Map<String,Object> startPage(Session session) throws ResultCodeException {
		return result(svc.startPage(session), "row", session.getPage());
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/changePasswordByVerification/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> changePasswordByVerification(Verification verification, Boolean encrypted) throws ResultCodeException {
		return result(svc.changePasswordByVerification(verification, encrypted));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/updateMobileByVerification/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> updateMobileByVerification(Session session, Verification verification) throws ResultCodeException {
		Integer ret = svc.updateMobileByVerification(session, verification);
		if (ret.equals(Const.E_SUCCESS)) {
			svc.getReloadSession(session);
		}
		return result(ret);
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/updateEmailByVerification/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> updateEmailByVerification(Session session, Verification verification) throws ResultCodeException {
		Integer ret = svc.updateEmailByVerification(session, verification);
		if (ret.equals(Const.E_SUCCESS)) {
			svc.getReloadSession(session);
		}
		return result(ret);
	}


	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/updateAuthCodeByVerification/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> updateAuthCodeByVerification(Session session, Verification verification) throws ResultCodeException {
		return result(svc.updateAuthCodeByVerification(session, verification));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/getUserByVerification/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = User.class)))
	public Map<String,Object> getUserByVerification(Verification verification) throws ResultCodeException {
		User user = svc.getUserByVerification(verification);
		if (user == null)
			return result(Const.E_NOTMATCHEDUSER);
		return result(Const.E_SUCCESS, "row", user);
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/deleteUser/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> deleteUser(Session session) throws ResultCodeException {
		return result(userSvc.deleteUser(session));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/leave/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> leave(Session session, Verification verification) throws ResultCodeException {
		session.setUseStatus("waitingToLeave");
		return result(userSvc.updateUseStatusAfterVerification(session, verification));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/cancelLeave/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> cancelLeave(Verification verification) throws ResultCodeException {
		User user = userSvc.getUserByLoginId(verification.getLoginId());
		if (user == null)
			throw new NotFoundTargetException(verification.getLoginId(), "not found");
		
		user.setUseStatus("normal");
		return result(userSvc.updateUseStatusAfterVerification(user, verification));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/checkUser/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> checkLoginId(@RequestParam("type") String type ,@RequestParam("value") String value)  {
		User exists = new User();
		if (type.equals("id")) {
			 exists = userSvc.getUserByLoginId(value);
		} else {
			 exists = userSvc.getUserByMobile(value);
		}
		if( exists != null  )
			return result(200, "row", "fail");
		else
			return result(200, "row", "success");
	}


	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/auth/storyjoin/**")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = JoinUser.class)))
	public Map<String,Object> storyjoin(@RequestParam("id") String id ,@RequestParam("pw") String pw,
										@RequestParam("mobile") String mobile) throws ResultCodeException {
		Long imgSeqNo = null;
		JoinUser user = new JoinUser();
		user.setAccountType("pplus");
		user.setLoginId(id);
		user.setNickname(id);
		user.setPassword(pw);
		user.setMemberType("normal");
		user.setUseStatus("normal");
		user.setPlatform("aos");
		Attachment attachment = new Attachment();
		attachment.setNo(imgSeqNo);
		user.setProfileImage(attachment);
		user.setName(id);
		user.setMobile(mobile);

		user.setZipCode(null);
		user.setBaseAddr(null) ;
		user.setRestrictionStatus("none");
		Verification verification = new Verification() ;
		verification.setMedia("sms");
		user.setVerification(verification);
		user.setLatitude(37.0);
		user.setLongitude(127.0);
		user.setWoodongyi(true);

//		user.setRecommendationCode("A2820008");

		Integer ret = null ;
		ret = svc.joinWithVerification(user, false);

		if (Const.E_SUCCESS.equals(ret)) {
			producer.push(user);
		}
		return result(ret, "row", user);

	}



}
