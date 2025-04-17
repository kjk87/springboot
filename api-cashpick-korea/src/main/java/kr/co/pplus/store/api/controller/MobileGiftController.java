//package kr.co.pplus.store.api.controller;
//
//import java.util.Map;
//
//import kr.co.pplus.store.api.annotation.SkipSessionCheck;
//import kr.co.pplus.store.type.model.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.mvc.service.AuthService;
//import kr.co.pplus.store.mvc.service.MobileGiftService;
//import kr.co.pplus.store.queue.MsgProducer;
//import kr.co.pplus.store.type.Const;
//
//@RestController
//public class MobileGiftController extends RootController {
//	@Autowired
//	MobileGiftService svc;
//
//	@Autowired
//	AuthService authSvc;
//
//	@Autowired
//	MsgProducer producer;
//
//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/getCategoryAll/**")
//	public Map<String,Object> getCategoryAll() {
//		return result(200, "rows", svc.getCategoryAll());
//	}
//
//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/getCount/**")
//	public Map<String,Object> getCount(Session session, MobileGiftCategory category, SearchOpt opt) {
//		return result(200, "row", svc.getCount(category, opt));
//	}
//
//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/getList/**")
//	public Map<String,Object> getList(Session session, MobileGiftCategory category, SearchOpt opt) {
//		return result(200, "rows", svc.getList(category, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/prepareOrder/**")
//	public Map<String,Object> prepareOrder(Session session, @RequestBody MobileGiftPurchase purchase) throws ResultCodeException {
//		Integer r = svc.prepareOrder(session, session.getDevice().getInstalledApp(), purchase);
//		return result(r, "row", purchase);
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/completeOrder/**")
//	public Map<String,Object> completeOrder(Session session, MobileGiftPurchase purchase) throws Exception {
//		try {
//			Integer r = svc.completeOrder(session, purchase);
//			if (Const.E_SUCCESS.equals(r)) {
//				producer.push(purchase);
//				authSvc.getReloadSession(session);
//			}
//			return result(r, "row", purchase);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			svc.removeOrder(session, purchase);
//			throw ex;
//		}
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/cancelPrepareOrder/**")
//	public Map<String,Object> cancelPrepareOrder(Session session, MobileGiftPurchase purchase) throws ResultCodeException {
//		svc.removeOrder(session, purchase);
//		return result(Const.E_SUCCESS);
//	}
//
//	//@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/isPossibleCancel/**")
////	public Map<String,Object> isPossibleCancel(Session session, MobileGiftSend send) throws Exception {
////		return result(200, "row", svc.isPossibleCancelGiftiEl(send));
////	}
//
//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/cancel/**")
//	public Map<String,Object> cancel(Session session, MobileGiftSend send) throws Exception {
//		return result(200, "row", svc.cancelSmartCon(send));
//	}
//
//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/getStatus/**")
//	public Map<String,Object> getStatus(Session session, Long purchaseSeqNo) throws Exception {
//		return result(200, "row", svc.getStatusSmartCon(purchaseSeqNo));
//	}
//
//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/resend/**")
//	public Map<String,Object> resend(Session session, Long purchaseSeqNo) throws Exception {
//		return result(Const.E_SUCCESS, "row", svc.resendSmartCon(purchaseSeqNo));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/getPurchaseCount/**")
//	public Map<String,Object> getPurchaseCount(Session session) {
//		return result(200, "row", svc.getPurchaseCount(session));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/getPurchaseGiftCount/**")
//	public Map<String,Object> getPurchaseGiftCount(Session session) {
//		return result(200, "row", svc.getPurchaseGiftCount(session));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/getPurchaseList/**")
//	public Map<String,Object> getPurchaseList(Session session, SearchOpt opt) {
//		return result(200, "rows", svc.getPurchaseList(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/getPurchaseWithTargetAll/**")
//	public Map<String,Object> getPurchaseWithTargetAll(Session session, MobileGiftPurchase purchase) {
//		return result(200, "row", svc.getPurchaseWithTargetAll(purchase.getNo()));
//	}
//
//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/syncSmartConList/**")
//	public Map<String,Object> getSmartConGiftList() throws Exception {
//		return result(200, "rows", svc.syncSmartConGiftList());
//	}
//
//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/mobilegift/updateMobileGiftStatus/**")
//	public Map<String,Object> updateMobileGiftStatus(MobileGift gift) throws Exception {
//		return result(200, "rows", svc.updateMobileGiftSaleStatus(gift));
//	}
//}
