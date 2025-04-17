package kr.co.pplus.store.api.controller;

import java.util.Map;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.pplus.store.mvc.service.RankingService;
import kr.co.pplus.store.type.model.Duration;
import kr.co.pplus.store.type.model.Session;

@RestController
public class RankingController extends RootController {
	
	@Autowired
	RankingService svc;

	@SkipSessionCheck
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/ranking/getInviteRankingView/**")
	public Map<String,Object> getInviteRankingView(Duration duration) {
		return result(200, "rows", svc.inviteView(duration));
	}

	@SkipSessionCheck
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/ranking/getInviteRanking/**")
	public Map<String,Object> getInviteRanking(Duration duration) {
		return result(200, "rows", svc.invite(duration));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/ranking/getMyInviteRanking/**")
	public Map<String,Object> getMyInviteRanking(Session session, Duration duration) {
		return result(200, "row", svc.userInvite(session, duration));
	}

	@SkipSessionCheck
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/ranking/getRewardRanking/**")
	public Map<String,Object> getRewardRanking(Duration duration, String appType) {
		return result(200, "rows", svc.reward(appType, duration));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/ranking/getMyRewardRanking/**")
	public Map<String,Object> getMyRewardRanking(Session session, Duration duration) {
		return result(200, "row", svc.userReward(session, duration));
	}
}
