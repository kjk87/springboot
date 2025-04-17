package kr.co.pplus.store.api.controller;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kr.co.pplus.store.type.dto.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.UserService;
import kr.co.pplus.store.mvc.service.VerificationService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.type.model.Verification;

@RestController
public class VerificationController extends RootController {
	@Autowired
	VerificationService svc;
	
	@Autowired
	UserService userSvc;
	
	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/verification/request/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Verification.class)))
	public Map<String,Object> request(Session sesion, Verification verification) throws Exception {
		Integer r = svc.request(verification);
		verification.setNumber(null);
		return result(r, "row", verification);
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/verification/confirm/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> confirm(Verification verification) throws ResultCodeException {
		return result(svc.confirm(verification));
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/verification/getUser/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = User.class)))
	public Map<String,Object> getUser(Verification verification) throws ResultCodeException {
		User user = svc.getUserByVerification(verification);
		return result(Const.E_SUCCESS, "row", user);
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/verification/leave/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> leave(Session session, Verification verification) throws ResultCodeException {
		Verification saved = svc.get(verification);
		if (saved != null 
				&& saved.getNumber().equals(verification.getNumber())
				&& saved.getMobile().equals(verification.getMobile())
				) {
			session.setUseStatus("waitingToLeave");

			if(StringUtils.isNotEmpty(verification.getAppType()) && !verification.getAppType().equals("pplus")){
				saved.setMobile(verification.getAppType()+"##"+saved.getMobile());
			}

			if(saved.getMobile().equals(session.getMobile())){
				return result(userSvc.updateUseStatus(session));
			}

		}
		return result(Const.E_UNKNOWN);
	}

	@SkipSessionCheck
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/verification/cancelLeave/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> cancelLeave(Verification verification) throws ResultCodeException {
		Verification saved = svc.get(verification);
		if (saved != null 
				&& saved.getNumber().equals(verification.getNumber())
				&& saved.getMobile().equals(verification.getMobile())
				) {

			if(StringUtils.isNotEmpty(verification.getAppType()) && !verification.getAppType().equals("pplus")){
				if(!verification.getLoginId().startsWith(verification.getAppType()+"##")){
					verification.setLoginId(verification.getAppType()+"##"+verification.getLoginId());
				}

				if(!saved.getMobile().startsWith(verification.getAppType()+"##")){
					saved.setMobile(verification.getAppType()+"##"+saved.getMobile());
				}
			}

			User user = userSvc.getUserByLoginId(verification.getLoginId());
			user.setUseStatus("normal");

			if(saved.getMobile().equals(user.getMobile())){
				return result(userSvc.updateUseStatus(user));
			}
		}
		return result(Const.E_UNKNOWN);
	}
}
