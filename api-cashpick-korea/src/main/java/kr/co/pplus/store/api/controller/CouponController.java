//package kr.co.pplus.store.api.controller;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import kr.co.pplus.store.api.annotation.SkipSessionCheck;
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.mvc.service.CouponService;
//import kr.co.pplus.store.mvc.service.QueueService;
//import kr.co.pplus.store.type.Const;
//import kr.co.pplus.store.type.dto.GiveCoupon;
//import kr.co.pplus.store.type.model.Cooperation;
//import kr.co.pplus.store.type.model.LuckyCoupon;
//import kr.co.pplus.store.type.model.CouponTemplate;
//import kr.co.pplus.store.type.model.Page;
//import kr.co.pplus.store.type.model.SearchOpt;
//import kr.co.pplus.store.type.model.Session;
//
//@RestController
//public class CouponController extends RootController {
//
//	@Autowired
//	CouponService svc;
//
//	@Autowired
//	QueueService queueSvc;
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/insertTemplate/**")
//	public Map<String,Object> insertTemplate(Session session, @RequestBody CouponTemplate template) throws ResultCodeException {
//		return result(svc.insertCouponTemplate(session, template), "row", template);
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/updateTemplate/**")
//	public Map<String,Object> updateTemplate(Session session, @RequestBody CouponTemplate template) throws ResultCodeException {
//		return result(svc.updateCouponTemplate(session, template), "row", template);
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/deleteTemplate/**")
//	public Map<String,Object> deleteTemplate(Session session, CouponTemplate template) throws ResultCodeException {
//		return result(svc.deleteCouponTemplate(session, template));
//	}
//
//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/getPageTemplate/**")
//	public Map<String,Object> getPageTemplate(CouponTemplate template) {
//		return result(200, "row", svc.getPageCouponTemplate(template));
//	}
//
//
//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/getPageTemplateCount/**")
//	public Map<String,Object> getPageTemplateCount(Session session, Page page, CouponTemplate template, SearchOpt opt) {
//		return result(200, "row", svc.getPageCouponTemplateCount(session, page, template, opt));
//	}
//
//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/getPageTemplateList/**")
//	public Map<String,Object> getPageTemplateList(Session session, Page page, CouponTemplate template, SearchOpt opt) {
//		return result(200, "rows", svc.getPageCouponTemplateList(session, page, template, opt));
//	}
//
//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/getFranchiseTemplateCount/**")
//	public Map<String,Object> getFranchiseTemplateCount(Session session, Cooperation cooperation, SearchOpt opt) {
//		return result(200, "row", svc.getFranchiseCouponTemplateCount(session, cooperation, opt));
//	}
//
//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/getFranchiseTemplateList/**")
//	public Map<String,Object> getFranchiseTemplateList(Session session, Cooperation cooperation, SearchOpt opt) {
//		return result(200, "rows", svc.getFranchiseCouponTemplateList(session, cooperation, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/downloadCoupon/**")
//	public Map<String,Object> downloadCoupon(Session session, CouponTemplate template) throws ResultCodeException {
//		return result(200, "row", svc.download(session, template));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/giveCoupon/**")
//	public Map<String,Object> giveCoupon(Session session, @RequestBody GiveCoupon luckyCoupon) throws ResultCodeException {
//		return result(Const.E_NOTIMPLEMENT);
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/activateCoupon/**")
//	public Map<String,Object> activateCoupon(Session session, LuckyCoupon luckyCoupon) throws ResultCodeException {
//		return result(svc.activate(session, luckyCoupon));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/useCoupon/**")
//	public Map<String,Object> useCoupon(Session session, LuckyCoupon luckyCoupon) throws ResultCodeException {
//		return result(svc.use(session, luckyCoupon));
//	}
//
//
//
//	@Deprecated
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/requestUseCoupon/**")
//	public Map<String,Object> requestUseCoupon(Session session, LuckyCoupon luckyCoupon) throws ResultCodeException {
//		return result(queueSvc.requestCouponUse(session, luckyCoupon));
//	}
//
//	@Deprecated
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/confirmUseCoupon/**")
//	public Map<String,Object> confirmUseCoupon(Session session, LuckyCoupon luckyCoupon) throws ResultCodeException {
//		return result(queueSvc.confirmCouponUse(session,  luckyCoupon));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/getPublishCouponCount/**")
//	public Map<String,Object> getPublishCouponCount(Session session, CouponTemplate template, SearchOpt opt) throws ResultCodeException {
//		return result(200, "row", svc.getPublishCouponCount(session, template, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/getPublishCouponList/**")
//	public Map<String,Object> getPublishCouponList(Session session, CouponTemplate template, SearchOpt opt) throws ResultCodeException {
//		return result(200, "rows", svc.getPublishCouponList(session, template, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/getCouponCount/**")
//	public Map<String,Object> getCouponCount(Session session, SearchOpt opt) {
//		return result(200, "row", svc.getCouponCount(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/getCouponList/**")
//	public Map<String,Object> getCouponList(Session session, SearchOpt opt) {
//		return result(200, "rows", svc.getCouponList(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/setPlusCouponTemplate/**")
//	public Map<String,Object> setPlusCouponTemplate(Session session, CouponTemplate template) throws ResultCodeException {
//		return result(svc.setPlusCoupon(session, template));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyCoupon/deleteCoupons/**")
//	public Map<String,Object> deleteCoupons(Session session, String[] code) throws ResultCodeException {
//		List<LuckyCoupon> couponList = new ArrayList<LuckyCoupon>();
//		for (String c : code) {
//			LuckyCoupon luckyCoupon = svc.getCouponByCode(c);
//			couponList.add(luckyCoupon);
//		}
//		return result(svc.deleteCoupons(session, couponList), "rows", couponList);
//	}
//}
