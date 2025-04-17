//package kr.co.pplus.store.api.controller;
//
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.mvc.service.OfferService;
//import kr.co.pplus.store.queue.MsgProducer;
//import kr.co.pplus.store.type.Const;
//import kr.co.pplus.store.type.model.Offer;
//import kr.co.pplus.store.type.model.OfferResponse;
//import kr.co.pplus.store.type.model.SearchOpt;
//import kr.co.pplus.store.type.model.Session;
//
//@RestController
//public class OfferController extends RootController {
//	@Autowired
//	OfferService svc;
//
//	@Autowired
//    MsgProducer producer;
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/offer/existsTodayOffer/**")
//	public Map<String,Object> existsTodayOffer(Session session) throws Exception {
//		return result(200, "row", svc.existsTodayOffer(session));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/offer/insert/**")
//	public Map<String,Object> insert(Session session, @RequestBody Offer offer) throws Exception  {
//		Integer ret = svc.insert(session, offer);
//		if (Const.E_SUCCESS.equals(ret)) {
//			producer.push(offer);
//		}
//		return result(ret, "row", offer);
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/offer/insertResponse/**")
//	public Map<String,Object> insertResponse(Session session, @RequestBody OfferResponse response) throws ResultCodeException {
//		Integer ret = svc.insertResponse(session, response);
//		if (Const.E_SUCCESS.equals(ret)) {
//			producer.push(response);
//		}
//		return result(ret, "row", response);
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/offer/getCountByUser/**")
//	public Map<String,Object> getCountByUser(Session session, SearchOpt opt) {
//		return result(200, "row", svc.getCountByUser(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/offer/getListByUser/**")
//	public Map<String,Object> getListByUser(Session session, SearchOpt opt) {
//		return result(200, "rows", svc.getListByUser(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/offer/getCountByPage/**")
//	public Map<String,Object> getCountByPage(Session session, SearchOpt opt) throws ResultCodeException {
//		return result(200, "row", svc.getCountByPage(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/offer/getListByPage/**")
//	public Map<String,Object> getListByPage(Session session, SearchOpt opt) throws ResultCodeException {
//		return result(200, "rows", svc.getListByPage(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/offer/getResponseAll/**")
//	public Map<String,Object> getResponseAll(Session session, Offer offer) {
//		return result(200, "rows", svc.getResponseAllByOffer(session, offer));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/offer/getMyResponseAll/**")
//	public Map<String,Object> getMyResponseAll(Session session, Offer offer) {
//		return result(200, "rows", svc.getMyResponseAllByOffer(session, offer));
//	}
//
//}
