//package kr.co.pplus.store.api.controller;
//
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.mvc.service.PaymentService;
//import kr.co.pplus.store.type.model.Session;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.util.Map;
//
//@Controller
//public class PaymentController extends RootController {
//
//	private final static Logger logger = LoggerFactory.getLogger(PaymentController.class);
//
//	@Autowired
//	PaymentService svc;
//
//
//	@RequestMapping(produces = "text/plain;charset=UTF-8", value = baseUri+"/payment/notification/**")
//	public String notification(Session session, @RequestParam Map<String, Object> data) {
//		try {
//			svc.processApprovalNotification(data);
//		} catch (ResultCodeException ex) {
//			ex.printStackTrace();
//		}
//		return "ok";
//	}
//
//	/* MGK_IMSI : 추후 수정 필요...
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/payment/PaymentRequest/**")
//	public ModelAndView PaymentRequest(Session session,
//									   @RequestParam("noti") String noti,
//									   @RequestParam("oid") String oid,
//									   @RequestParam("goods") String goods,
//									   @RequestParam("amt") String amt,
//									   @RequestParam("uname") String username,
//									   @RequestParam("mname") String storename,
//									   @RequestParam("mobile") String mobile,
//									   @RequestParam("email") String email,
//									   @RequestParam("hppmethod") String hppmethod,
//									   @RequestParam("paymethod") String paymethod,
//									   @RequestParam("reserved") String reserved) {
//
//		try {
//
//
//			if (reserved == null || reserved.length() == 0)
//				reserved = "twotrs_isp=Y&block_isp=Y&twotrs_isp_noti=N&cp_yn=Y";
//
//
//			if (oid == null || oid.length() == 0)
//				oid = "PRNumber-" + KeyGenerator.generateOrderNo()+ "-" + KeyGenerator.generateKey() ;
//
//			if (email == null || email.length() == 0)
//				email = "";
//
//			if (mobile == null || mobile.length() == 0)
//				mobile = "";
//
//			if (username == null || username.length() == 0)
//				username = "";
//
//			if (storename == null || storename.length() == 0)
//				storename = "PRNumber";
//
//			if (hppmethod == null || hppmethod.length() == 0)
//				hppmethod = "1";
//
//			if (paymethod == null || paymethod.length() == 0)
//				paymethod = "card";
//
//			String returnUrl = impUrl + baseUri + "?P_OID=" + oid + "&paymethod=" + paymethod;
//
//			ModelAndView model = new ModelAndView();
//			model.addObject("impId", impId) ;
//			model.addObject("orderId", oid) ;
//			model.addObject("pg", "html5_inicis" ) ;
//			model.addObject("pay_method", paymethod) ;
//			model.addObject("merchant_uid", oid) ;
//			model.addObject("name", "PG결제 : " + storename + " : " +goods) ;
//			model.addObject("amount", amt) ;
//			model.addObject("buyer_email", email) ;
//			model.addObject("buyer_name", username) ;
//			model.addObject("buyer_tel", mobile) ;
//			model.addObject("buyer_addr", "") ;
//			model.addObject("buyer_postcode", "") ;
//			model.addObject("m_redirect_url", returnUrl) ;
//			model.setViewName("paygate/imp");
//
//			System.out.println("/buy/pg/imp : " + model.toString()) ;
//			return model ;
//		}
//		catch(Exception e){
//			logger.error(AppUtil.excetionToString(e)) ;
//			ModelAndView model = new ModelAndView();
//
//			model.addObject("errorTitle", "PG 결제 연동 오류") ;
//			model.addObject("errorMessage", e.getMessage()) ;
//			model.setViewName("paygate/error");
//
//			return model ;
//		}
//	}
//	*/
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/payment/getApprovalByTransactionId/**")
//	@ResponseBody
//	public Map<String,Object> getApprovalByTransactionId(Session session, String transactionId) {
//		return result(200, "row", svc.getApprovalByTransactionId(transactionId));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/payment/getApprovalByOrderKey/**")
//	@ResponseBody
//	public Map<String,Object> getApprovalByOrderKey(Session session, String orderKey) {
//		return result(200, "row", svc.getApprovalByOrderKey(orderKey));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/payment/getPendingApprovalAll/**")
//	@ResponseBody
//	public Map<String,Object> getPendingAll(Session session) {
//		return result(200, "rows", svc.getPendingAll(session));
//	}
//}
