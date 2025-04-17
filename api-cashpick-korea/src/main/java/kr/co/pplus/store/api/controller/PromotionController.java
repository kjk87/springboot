package kr.co.pplus.store.api.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.PromotionService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Promotion;
import kr.co.pplus.store.type.model.SearchOpt;
import kr.co.pplus.store.type.model.Session;

@RestController
public class PromotionController extends RootController {

	@Autowired
	PromotionService svc;
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/promotion/getList/**")
	public Map<String,Object> getList(Session session, SearchOpt opt) {
		return result(200, "rows", svc.getList(opt));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/promotion/getByNumber/**")
	public Map<String,Object> getByNumber(Session session, String number) {
		Promotion r = svc.getByNumber(number);
		if (r == null)
			return result(Const.E_NOTFOUND);
		return result(Const.E_SUCCESS, "row", r);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/promotion/get/**")
	public Map<String,Object> get(Session session, Long no) {
		Promotion r = svc.get(no);
		if (r == null)
			return result(Const.E_NOTFOUND);
		return result(Const.E_SUCCESS, "row", r);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/promotion/join/**")
	public Map<String,Object> join(Session session, Promotion promotion) throws ResultCodeException {
		return result(Const.E_SUCCESS, "row", svc.join(session, promotion));
	}
}
