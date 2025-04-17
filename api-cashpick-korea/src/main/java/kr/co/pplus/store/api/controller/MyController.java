package kr.co.pplus.store.api.controller;

import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.AuthService;
import kr.co.pplus.store.mvc.service.CouponService;
import kr.co.pplus.store.mvc.service.PageService;
import kr.co.pplus.store.mvc.service.UserService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Attachment;
import kr.co.pplus.store.type.model.InstalledApp;
import kr.co.pplus.store.type.model.Session;
import kr.co.pplus.store.type.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MyController extends RootController {

	@Autowired
	PageService pageSvc;
	
	@Autowired
	UserService userSvc;
	
	@Autowired
	AuthService authSvc;
	
	@Autowired
	CouponService couponSvc;
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/my/getPage/**")
	public Map<String,Object> getPage(Session session) {
		return result(200, "row", pageSvc.getPageByUser(session));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/my/getMe/**")
	public Map<String,Object> getMe(Session session) {
		User user = userSvc.getUser(session.getNo());
		if (user != null) {
			//세션 정보를 갱신한다.
			session.setCash(user.getCash());
			session.setUseStatus(user.getUseStatus());
			userSvc.reloadSession(session);
		}
		return result(200, "row", user);
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/my/updateProfileImage/**")
	public Map<String,Object> updateProfileImage(Session session, Attachment attachment) {
		return result(userSvc.updateProfileImage(session, attachment));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/my/insertAuthedNumber/**")
	public Map<String,Object> insertAuthedNumber(Session session, String mobile) {
		return result(userSvc.insertUserAuthedNumber(session, mobile));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/my/deleteAuthedNumber/**")
	public Map<String,Object> deleteAuthedNumber(Session session, String mobile) {
		return result(userSvc.deleteUserAuthedNumber(session, mobile));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/my/getAuthedNumberAll/**")
	public Map<String,Object> getAuthedNumberAll(Session session) {
		return result(200, "rows", userSvc.getUserAuthedNumberAll(session));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/my/updateNickname/**")
	public Map<String,Object> updateNickname(Session session, String nickname) throws ResultCodeException {
		session.setNickname(nickname);
		Integer ret = userSvc.updateNickname(session);
		if (ret.equals(Const.E_SUCCESS)) {
			userSvc.reloadSession(session);
		}
		return result(ret, "session", session);
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/my/updateGender/**")
	public Map<String,Object> updateGender(Session session, String gender) throws ResultCodeException {
		session.setGender(gender);
		Integer ret = userSvc.updateGender(session);
		if (ret.equals(Const.E_SUCCESS)) {
			userSvc.reloadSession(session);
		}
		return result(ret, "session", session);
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/my/updateBirthday/**")
	public Map<String,Object> updateBirthday(Session session, String birthday) throws ResultCodeException {
		session.setBirthday(birthday);
		Integer ret = userSvc.updateBirthday(session);
		if (ret.equals(Const.E_SUCCESS)) {
			userSvc.reloadSession(session);
		}
		return result(ret, "session", session);
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/my/updatePushConfig/**")
	public Map<String,Object> updatePushConfig(Session session, InstalledApp app) throws ResultCodeException {
		session.getDevice().getInstalledApp().setPushMask(app.getPushMask());
		session.getDevice().getInstalledApp().setPushActivate(app.getPushActivate());
		Integer ret = userSvc.updatePushConfig(session.getDevice());
		if (ret.equals(Const.E_SUCCESS)) {
			userSvc.reloadSession(session);
		}
		return result(ret, "session", session);
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/my/updateUser/**")
	public Map<String,Object> updateUser(Session session, @RequestBody User user) {
		Integer ret = userSvc.update(user);
		return result(ret, "row", user);
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/my/updateExternal/**")
	public Map<String,Object> updateExternal(Session session, @RequestBody User user) {
		user.setCountry(session.getCountry());
		Integer ret = userSvc.updateExternal(user);
		return result(ret, "row", user);
	}
}
