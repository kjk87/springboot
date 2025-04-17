//package kr.co.pplus.store.api.controller;
//
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.mvc.service.AdvertiseService;
//import kr.co.pplus.store.mvc.service.AuthService;
//import kr.co.pplus.store.mvc.service.CommonService;
//import kr.co.pplus.store.type.Const;
//import kr.co.pplus.store.type.model.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//public class AdvertiseController extends RootController {
//
//	@Autowired
//	AdvertiseService svc;
//
//	@Autowired
//	AuthService authSvc;
//
//	@Autowired
//	CommonService commonSvc;
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/like/**")
//	public Map<String,Object> like(Session session, Advertise advertise) throws ResultCodeException {
//		return result(svc.like(session, advertise));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/unlike/**")
//	public Map<String,Object> unlike(Session session, Advertise advertise) throws ResultCodeException {
//		return result(svc.unlike(session, advertise));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/downloadCoupon/**")
//	public Map<String,Object> downloadCoupon(Session session, CouponTemplate template) throws ResultCodeException {
//		return result(200, "row", svc.download(session, template));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/usePost/**")
//	public Map<String,Object> usePost(Session session, Advertise advertise) throws ResultCodeException {
//		return result(svc.useArticle(session, advertise));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/useCoupon/**")
//	public Map<String,Object> useCoupon(Session session, Advertise advertise, LuckyCoupon luckyCoupon) throws ResultCodeException {
//		return result(svc.useCoupon(session, advertise, luckyCoupon));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/contact/**")
//	public Map<String,Object> contact(Session session, Advertise advertise) throws ResultCodeException {
//		return result(svc.contact(advertise));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/addBudget/**")
//	public Map<String,Object> addBudget(Session session, Advertise advertise) throws ResultCodeException {
//		return result(svc.addBudget(session, advertise));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/updateStatus/**")
//	public Map<String,Object> updateStatus(Session session, Advertise advertise) throws ResultCodeException {
//		return result(svc.updateStatus(session, advertise));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/update/**")
//	public Map<String,Object> update(Session session, @RequestBody Advertise advertise) throws ResultCodeException {
//		return result(svc.update(session, advertise), "row", advertise);
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/delete/**")
//	public Map<String,Object> delete(Session session, Advertise advertise) throws ResultCodeException {
//		return result(svc.delete(session, advertise));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getAdvertise/**")
//	public Map<String,Object> getAdvertise(Advertise advertise) {
//		return result(200, "row", svc.getAdvertise(advertise));
//	}
//
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/advertisePost/**")
//	public Map<String,Object> advertisePost(Session session, @RequestBody ArticleAdvertise advertise) throws ResultCodeException {
//		Integer r = svc.advertise(session, advertise);
//
//		if (Const.E_SUCCESS.equals(r)) {
//			authSvc.getReloadSession(session);
//		}
//
//		return result(r, "row", advertise);
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getPostCount/**")
//	public Map<String,Object> getPostCount(Session session, PageCategory category, SearchOpt opt, Boolean plus, Boolean my) {
//		return result(Const.E_SUCCESS, "row", svc.getArticleAdvertiseCount(session, category, opt, plus, my));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getPostList/**")
//	public Map<String,Object> getPostList(Session session, PageCategory category, SearchOpt opt, GeoPosition pos, Boolean plus, Boolean my) {
//		return result(Const.E_SUCCESS, "rows", svc.getArticleAdvertiseList(session, category, opt, pos, plus, my));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getPostCountByPage/**")
//	public Map<String,Object> getPostCountByPage(Session session, Page page, SearchOpt opt) {
//		return result(Const.E_SUCCESS, "row", svc.getArticleAdvertiseCountByPage(page, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getPostListByPage/**")
//	public Map<String,Object> getPostListByPage(Session session, Page page, SearchOpt opt) {
//		return result(Const.E_SUCCESS, "rows", svc.getArticleAdvertiseListByPage(session, page, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getLikePostCount/**")
//	public Map<String,Object> getLikePostCount(Session session, PageCategory category, SearchOpt opt) {
//		return result(Const.E_SUCCESS, "row", svc.getLikeArticleAdvertiseCount(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getLikePostList/**")
//	public Map<String,Object> getLikePostList(Session session, PageCategory category, SearchOpt opt, GeoPosition pos) {
//		return result(Const.E_SUCCESS, "rows", svc.getLikeArticleAdvertiseList(session, opt, pos));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getMyPostCount/**")
//	public Map<String,Object> getMyPostCount(Session session, SearchOpt opt) {
//		return result(Const.E_SUCCESS, "row", svc.getAdvertiseArticleCountByUser(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getMyPostList/**")
//	public Map<String,Object> getMyPostList(Session session, SearchOpt opt) {
//		return result(Const.E_SUCCESS, "rows", svc.getAdvertiseArticleListByUser(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getPossibleMyPostCount/**")
//	public Map<String,Object> getPossibleMyPostCount(Session session, SearchOpt opt) {
//		return result(Const.E_SUCCESS, "row", svc.getPossibleArticleCountByUser(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getPossibleMyPostList/**")
//	public Map<String,Object> getPossibleMyPostList(Session session, SearchOpt opt) {
//		return result(Const.E_SUCCESS, "rows", svc.getPossibleArticleListByUser(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getPostAdvertise/**")
//	public Map<String,Object> getPostAdvertise(Session session, Advertise advertise, GeoPosition pos) {
//		return result(Const.E_SUCCESS, "row", svc.getArticleAdvertise(session, advertise, pos));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getPost/**")
//	public Map<String,Object> getPost(Session session, Article article) {
//		return result(Const.E_SUCCESS, "row", svc.getArticleWithAttachment(article));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getAdvertiseCountByPost/**")
//	public Map<String,Object> getAdvertiseCountByPost(Session session, Article article, Duration duration) {
//		return result(Const.E_SUCCESS, "row", svc.getAdvertiseCountByArticle(article, duration));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getAdvertiseListByPost/**")
//	public Map<String,Object> getAdvertiseListByPost(Session session, Article article, Duration duration) {
//		return result(Const.E_SUCCESS, "rows", svc.getAdvertiseListByArticle(article, duration));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getPostStatistics/**")
//	public Map<String,Object> getPostStatistics(Session session, Duration duration) {
//		return result(Const.E_SUCCESS, "row", svc.getArticleStatisticsByUser(session, duration));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getUsePostStatistics/**")
//	public Map<String,Object> getUsePostStatistics(Session session, Duration duration) {
//		return result(Const.E_SUCCESS, "row", svc.getUseArticleStatisticsByUser(session, duration));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getFirstArticleAdvertiseDate/**")
//	public Map<String,Object> getFirstArticleAdvertiseDate(Session session) {
//		return result(Const.E_SUCCESS, "row", svc.getFirstArticleAdvertiseDate(session));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getDailyPostUseCountList/**")
//	public Map<String,Object> getDailyPostUseCountList(Session session, Duration duration) {
//		return result(Const.E_SUCCESS, "rows", svc.getDailyArticleUseCountListByUser(session, duration));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getWeeklyPostUseCountList/**")
//	public Map<String,Object> getWeeklyPostUseCountList(Session session, Duration duration) {
//		return result(Const.E_SUCCESS, "rows", svc.getWeeklyArticleUseCountListByUser(session, duration));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/advertiseCouponTemplate/**")
//	public Map<String,Object> advertiseCouponTemplate(Session session, @RequestBody CouponTemplateAdvertise advertise) throws ResultCodeException {
//		Integer r = svc.advertise(session, advertise);
//
//		if (Const.E_SUCCESS.equals(r)) {
//			authSvc.getReloadSession(session);
//		}
//
//		return result(r, "row", advertise);
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getCouponTemplateCount/**")
//	public Map<String,Object> getCouponTemplateCount(Session session, PageCategory category, SearchOpt opt, Boolean plus, Boolean my) {
//		return result(Const.E_SUCCESS, "row", svc.getCouponTemplateAdvertiseCount(session, category, opt, plus, my));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getCouponTemplateList/**")
//	public Map<String,Object> getCouponTemplateList(Session session, PageCategory category, SearchOpt opt, GeoPosition pos, Boolean plus, Boolean my) {
//		return result(Const.E_SUCCESS, "rows", svc.getCouponTemplateAdvertiseList(session, category, opt, pos, plus, my));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getCouponTemplateCountByPage/**")
//	public Map<String,Object> getCouponTemplateCountByPage(Session session, Page page, SearchOpt opt) {
//		return result(Const.E_SUCCESS, "row", svc.getCouponTemplateAdvertiseCountByPage(page, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getCouponTemplateListByPage/**")
//	public Map<String,Object> getCouponTemplateListByPage(Session session, Page page, SearchOpt opt) {
//		return result(Const.E_SUCCESS, "rows", svc.getCouponTemplateAdvertiseListByPage(session, page, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getMyCouponTemplateCount/**")
//	public Map<String,Object> getMyCouponTemplateCount(Session session, SearchOpt opt) {
//		return result(Const.E_SUCCESS, "row", svc.getAdvertiseCouponTemplateCountByUser(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getMyCouponTemplateList/**")
//	public Map<String,Object> getMyCouponTemplateList(Session session, SearchOpt opt) {
//		return result(Const.E_SUCCESS, "rows", svc.getAdvertiseCouponTemplateListByUser(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getPossibleMyCouponTemplateCount/**")
//	public Map<String,Object> getPossibleMyCouponTemplateCount(Session session, SearchOpt opt) {
//		return result(Const.E_SUCCESS, "row", svc.getPossibleCouponTemplateCountByUser(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getPossibleMyCouponTemplateList/**")
//	public Map<String,Object> getPossibleMyCouponTemplateList(Session session, SearchOpt opt) {
//		return result(Const.E_SUCCESS, "rows", svc.getPossibleCouponTemplateListByUser(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getCouponTemplateAdvertise/**")
//	public Map<String,Object> getCouponTemplateAdvertise(Session session, Advertise advertise, GeoPosition pos) {
//		return result(Const.E_SUCCESS, "row", svc.getCouponTemplateAdvertise(session, advertise, pos));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getCouponTemplate/**")
//	public Map<String,Object> getCouponTemplate(Session session, CouponTemplate template) {
//		return result(Const.E_SUCCESS, "row", svc.getPageCouponTemplateWithIcon(template));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getAdvertiseCountByCouponTemplate/**")
//	public Map<String,Object> getAdvertiseCountByCouponTemplate(Session session, CouponTemplate template, Duration duration) {
//		return result(Const.E_SUCCESS, "row", svc.getAdvertiseCountByCouponTemplate(template, duration));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getAdvertiseListByCouponTemplate/**")
//	public Map<String,Object> getAdvertiseListByCouponTemplate(Session session, CouponTemplate template, Duration duration) {
//		return result(Const.E_SUCCESS, "rows", svc.getAdvertiseListByCouponTemplate(template, duration));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getCouponTemplateStatistics/**")
//	public Map<String,Object> getCouponTemplateStatistics(Session session, Duration duration) {
//		return result(Const.E_SUCCESS, "row", svc.getCouponTemplateStatisticsByUser(session, duration));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getUseCouponTemplateStatistics/**")
//	public Map<String,Object> getUseCouponTemplateStatistics(Session session, Duration duration) {
//		return result(Const.E_SUCCESS, "row", svc.getUseCouponTemplateStatisticsByUser(session, duration));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getFirstCouponTemplateAdvertiseDate/**")
//	public Map<String,Object> getFirstCouponTemplateAdvertiseDate(Session session) {
//		return result(Const.E_SUCCESS, "row", svc.getFirstCouponTemplateAdvertiseDate(session));
//	}
//
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getDailyCouponTemplateUseCountList/**")
//	public Map<String,Object> getDailyCouponTemplateUseCountList(Session session, Duration duration) {
//		return result(Const.E_SUCCESS, "rows", svc.getDailyCouponTemplateUseCountListByUser(session, duration));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getWeeklyCouponTemplateUseCountList/**")
//	public Map<String,Object> getWeeklyCouponTemplateUseCountList(Session session, Duration duration) {
//		return result(Const.E_SUCCESS, "rows", svc.getWeeklyCouponTemplateUseCountListByUser(session, duration));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getCouponCount/**")
//	public Map<String,Object> getCouponCount(Session session, SearchOpt opt) {
//		return result(200, "row", svc.getCouponCount(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/advertise/getCouponList/**")
//	public Map<String,Object> getCouponList(Session session, SearchOpt opt) {
//		return result(200, "rows", svc.getCouponList(session, opt));
//	}
//}
