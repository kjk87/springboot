//package kr.co.pplus.store.api.controller;
//
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import kr.co.pplus.store.mvc.service.CooperationService;
//import kr.co.pplus.store.type.model.CooperationGroup;
//import kr.co.pplus.store.type.model.Page;
//import kr.co.pplus.store.type.model.SearchOpt;
//import kr.co.pplus.store.type.model.Session;
//
//@RestController
//public class CooperationController extends RootController {
//	@Autowired
//	CooperationService svc;
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/cooperation/getByPage/**")
//	public Map<String,Object> getByPage(Session session, Page page) {
//		return result(200, "row", svc.getByPage(page));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/cooperation/getGroupAll/**")
//	public Map<String,Object> getGroupAll(Session session) {
//		return result(200, "rows", svc.getGroupAll());
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/cooperation/getCount/**")
//	public Map<String,Object> getCount(Session session, CooperationGroup group, SearchOpt opt) {
//		return result(200, "row", svc.getCount(group, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/cooperation/getList/**")
//	public Map<String,Object> getList(Session session, CooperationGroup group, SearchOpt opt) {
//		return result(200, "rows", svc.getList(group, opt));
//	}
//
//}
