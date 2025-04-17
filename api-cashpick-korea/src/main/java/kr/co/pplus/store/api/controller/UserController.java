package kr.co.pplus.store.api.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.UserService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.BaseResponse;
import kr.co.pplus.store.type.dto.JoinUser;
import kr.co.pplus.store.type.model.SearchOpt;
import kr.co.pplus.store.type.model.Session;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.type.model.Verification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController extends RootController {
	@Autowired
	UserService svc;

	@Value("${STORE.REDIS_PREFIX}")
	String REDIS_PREFIX = "pplus-";

	@SkipSessionCheck
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/check")
	@ApiResponse(responseCode = "200", description = "row : string", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> checkLoginId(@RequestParam("loginId") String loginId, @RequestParam(value = "appType", required = false) String appType) {

			return result(200, "row", svc.checkLoginId(loginId, appType));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/getUser/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = User.class)))
	public Map<String,Object> getUser(Long seqNo) {
		return result(200, "row", svc.getUser(seqNo));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/getExistsNicknameUserCount/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> getExistsNicknameUserCount(Session session, SearchOpt opt) {
		return result(200, "row", svc.getExistsNicknameUserCount(opt));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/getExistsNicknameUserList/**")
	@ApiResponse(responseCode = "200", description = "rows : []", content = @Content(schema = @Schema(implementation = User.class)))
	public Map<String,Object> getExistsNicknameUserList(Session session, SearchOpt opt) {
		return result(200, "rows", svc.getExistsNicknameUserList(opt));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/getUserByRecommendationCode/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = User.class)))
	public Map<String,Object> getUserByRecommendationCode(Session session, String recommendationCode) {
		User user = svc.getUserByRecommendKey(recommendationCode);
		if (user == null)
			return result(Const.E_NOTFOUND);
		
		return result(Const.E_SUCCESS, "row", user);
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/updatePayPassword/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> updatePayPassword(Session session, String payPassword, Boolean encrypted) {
		return result(svc.updatePayPassword(session, payPassword, encrypted));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/updatePayPasswordWithVerification/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> updatePayPassword(Session session, String payPassword, Boolean encrypted, Verification verification) throws ResultCodeException {
		return result(svc.updatePayPasswordWithVerification(session, payPassword, verification, encrypted));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/checkPayPassword/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> checkPayPassword(Session session, String payPassword, Boolean encrypted) {
		return result(svc.checkPayPassword(session, payPassword, encrypted));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/getUserCountByRecommendKey/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> getUserCountByRecommendKey(Session session, String recommendKey) {
		return result(Const.E_SUCCESS, "row", svc.getUserCountByRecommendationCode(recommendKey));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/getUserListByRecommendKey/**")
	@ApiResponse(responseCode = "200", description = "rows : []", content = @Content(schema = @Schema(implementation = User.class)))
	public Map<String,Object> getUserListByRecommendKey(Session session, String recommendKey) {
		return result(Const.E_SUCCESS, "rows", svc.getUserListByRecommendationCode(recommendKey));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/updateBuyPlusTerms/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> updateBuyPlusTerms(Session session, Boolean buyPlusTerms) {
		return result(svc.updateBuyPlusTerms(session, buyPlusTerms));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/updatePlusPush/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> updatePlusPush(Session session, Boolean plusPush) {
		return result(svc.updatePlusPush(session, plusPush));
	}

	@PutMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/updateActiveArea1/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> updateActiveArea1(Session session, String activeArea1Value, String activeArea1Name) {
		return result(svc.updateActiveArea1(session, activeArea1Value, activeArea1Name));
	}

	@PutMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/updateActiveArea2/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> updateActiveArea2(Session session, String activeArea2Value, String activeArea2Name) {
		return result(svc.updateActiveArea2(session, activeArea2Value, activeArea2Name));
	}

	@PutMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/updateQrImage/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> updateQrImage(Session session, String qrImage) {
		return result(svc.updateQrImage(session, qrImage));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/sns/account")
	@ApiResponse(responseCode = "200", description = "row : string", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> updateSnsUser(Session session, User user) {

	    String snsId = session.getLoginId() ;
        String loginId = user.getLoginId() ;
        String password = user.getPassword() ;

		int ret = svc.updateSnsUser(snsId, loginId, password) ;
		if( ret > 0 )
			return result(Const.E_SUCCESS, "row", "success");
		else
			return result(Const.E_SUCCESS, "row", "fail");
	}

	@SkipSessionCheck
	@PutMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/user/useStatusWithAgreeTerms")
	@ApiResponse(responseCode = "200", description = "row : string", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> updateUserUseStatus(Session session, @RequestBody JoinUser user) {

		int ret = svc.updateUseStatusWithAgreeTerms(user) ;

		if( ret > 0 )
			return result(Const.E_SUCCESS, "row", "success");
		else
			return result(Const.E_SUCCESS, "row", "fail");
	}
}
