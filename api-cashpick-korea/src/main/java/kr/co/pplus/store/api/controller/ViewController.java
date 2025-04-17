package kr.co.pplus.store.api.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.jpa.controller.BootPayApi;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.service.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidBuyException;
import kr.co.pplus.store.exception.NotPermissionException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.UserService;
import kr.co.pplus.store.mvc.service.VerificationService;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.type.model.Verification;
import kr.co.pplus.store.util.StoreUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ViewController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(ViewController.class);

    static final String[] COUPON_CHARS = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"
            , "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"
            , "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
            , "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m"
            , "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};


    @Value("${SYSTEM.BASE_URL}")
    String systemBaseUrl;

    @Value("${STORE.BOOTPAY.WEB_ID}")
    String WEB_ID;

    @Value("${STORE.TYPE}")
    String storeType;

    @Value("${STORE.REAPPAY.PAY_URL}")
    String reapPayUrl = "https://stdpay-dev-pg.reappay.net/ReapPay/ReapPayForm";

    @Value("${STORE.REAPPAY.ID}")
    private String ID;


    @Value("${STORE.REAPPAY.TEST_PAY}")
    private String TEST_PAY;

    @Autowired
    VerificationService verficationService;

    @Autowired
    PurchaseService purchaseService;

    @Autowired
    MemberService memberService;

    @Autowired
    LuckyBoxService luckyBoxService;

    @Autowired
    LuckyPickService luckyPickService;

    @Autowired
    LuckyBolService luckyBolService;

    @Autowired
    UserService userService;

	/*
	@RequestMapping(produces = "application/json;charset=UTF-8", value = value = baseUri + "/jsp/{page}", produces={"text/html; charset=UTF-8"})
	public ModelAndView page(@PathVariable String page) {

		ModelAndView model = new ModelAndView();
		model.addObject("systemBaseUrl", systemBaseUrl) ;
		model.setViewName(page);
		return model;
	}


	@RequestMapping(produces = "application/json;charset=UTF-8", value = value = baseUri + "/jsp/{dir}/{page}", produces={"text/html; charset=UTF-8"})
	public ModelAndView dirpage(@PathVariable String dir, @PathVariable String page, Map params) {

		ModelAndView model = new ModelAndView();
		model.addObject("systemBaseUrl", systemBaseUrl) ;
		model.setViewName(dir + "/" + page);
		return model;
	}
	*/


    @SkipSessionCheck
    @RequestMapping(value = baseUri + "/view/{page}", produces = {"text/html; charset=UTF-8"})
    public ModelAndView viewPage(@PathVariable String page) {


        ModelAndView model = new ModelAndView();
        model.addObject("systemBaseUrl", systemBaseUrl);
        model.setViewName(page);
        return model;
    }

    @SkipSessionCheck
    @RequestMapping(value = baseUri + "/view/{dir}/{page}", produces = {"text/html; charset=UTF-8"})
    public ModelAndView dirViewPage(@PathVariable String dir, @PathVariable String page, Map params) {

        ModelAndView model = new ModelAndView();
        model.addObject("systemBaseUrl", systemBaseUrl);
        model.setViewName(dir + "/" + page);
        return model;
    }


    public Boolean paramChk(String patn, String param) {
        Pattern pattern = Pattern.compile(patn);
        Matcher matcher = pattern.matcher(param);
        Boolean b = matcher.matches();
        return b;
    }

    @SkipSessionCheck
    @RequestMapping(value = baseUri + "/jsp/withdrawal/step1", produces = {"text/html; charset=UTF-8"})
    public ModelAndView withdrawalStep1() {
        ModelAndView model = new ModelAndView();
        model.addObject("systemBaseUrl", systemBaseUrl);

        try {
            model.setViewName("withdrawal/step1");
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
        }

        return model;
    }

    @SkipSessionCheck
    @RequestMapping(value = baseUri + "/jsp/withdrawal/step2", produces = {"text/html; charset=UTF-8"})
    public ModelAndView withdrawalStep2(HttpServletRequest request,
                                        @RequestParam(value = "mobile", required = true) String mobile) {
        ModelAndView model = new ModelAndView();
        model.addObject("systemBaseUrl", systemBaseUrl);

        try {
            Verification verification = new Verification();
            verification.setMedia("sms");
            verification.setMobile(mobile);
            verification.setType("leave");
            verification.setAppType("luckyball");
            verficationService.request(verification);

            Map<String, String> map = new HashMap<String, String>();
            map.put("mobile", mobile);
            map.put("token", verification.getToken());
            model.setViewName("withdrawal/step2");
            model.addAllObjects(map);

        }catch (Exception e){
            logger.error(AppUtil.excetionToString(e));
            model.setViewName("withdrawal/step2_error");
        }

        return model;
    }

    @SkipSessionCheck
    @RequestMapping(value = baseUri + "/jsp/withdrawal/step3", produces = {"text/html; charset=UTF-8"})
    public ModelAndView withdrawalStep3(HttpServletRequest request,
                                        @RequestParam(value = "mobile") String mobile,
                                        @RequestParam(value = "number") String number,
                                        @RequestParam(value = "token") String token) {
        ModelAndView model = new ModelAndView();
        model.addObject("systemBaseUrl", systemBaseUrl);
        Verification verification = new Verification();
        verification.setMobile(mobile);
        verification.setNumber(number);
        verification.setToken(token);

        Verification saved = verficationService.get(verification);

        if (saved != null && saved.getNumber().equals(verification.getNumber()) && saved.getMobile().equals(verification.getMobile())) {
            mobile = "luckyball##"+mobile;
            User user = userService.getUserByMobile(mobile);
            if(user != null){
                user.setUseStatus("waitingToLeave");
                userService.updateUseStatus(user);

                try {
                    model.setViewName("withdrawal/step3");
                }catch (Exception e){
                    logger.error(AppUtil.excetionToString(e));
                }
            }else{
                try {
                    model.setViewName("withdrawal/step3_error");
                }catch (Exception e){
                    logger.error(AppUtil.excetionToString(e));
                }
            }
        }else{
            try {
                model.setViewName("withdrawal/step3_error");
            }catch (Exception e){
                logger.error(AppUtil.excetionToString(e));
            }
        }

        return model;
    }

    @SkipSessionCheck
    @RequestMapping(value = baseUri + "/jsp/auth", produces = {"text/html; charset=UTF-8"})
    public ModelAndView auth() {
        ModelAndView model = new ModelAndView();
        model.addObject("systemBaseUrl", systemBaseUrl);

        try {
            String orderId = StoreUtil.getRandomOrderId();
            Map<String, String> map = new HashMap<String, String>();
            map.put("order_id", orderId);
            map.put("web_id", WEB_ID);

            model.setViewName("danal/auth");
            model.addAllObjects(map);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
        }

        return model;
    }

//	{
//		"status": 200,
//			"code": 0,
//			"message": "",
//			"data": {
//		"receipt_id": "5df8d2d265bb6f001ea39522",
//				"order_id": "1576587986490",
//				"pg": "danal",
//				"method": "auth",
//				"pg_name": "다날",
//				"method_name": "본인인증",
//				"certificate": {
//			"username": "[[ 인증자 성명 ]]",
//					"phone": "[[ 인증한 전화번호 ]]",
//					"birth": "[[ 생년월일 ]]",
//					"gender": "[[ 성별 ]]",
//					"unique": "[[ unique ( CI값 ) ]]",
//					"di": "[[ DI값 ]]"
//		},
//		"payment_data": {
//			"username": "[[ 인증자 성명 ]]",
//					"phone": "[[ 인증한 전화번호 ]]",
//					"birth": "[[ 생년월일 ]]",
//					"gender": "[[ 성별 ]]",
//					"unique": "[[ unique ( CI값 ) ]]",
//					"di": "[[ DI값 ]]",
//					"receipt_id": "5df8d2d265bb6f001ea39522",
//					"n": "본인인증서비스",
//					"p": 0,
//					"tid": "201912172206270669213010",
//					"pg": "다날",
//					"pm": "본인인증",
//					"pg_a": "danal",
//					"pm_a": "auth",
//					"o_id": "1576587986490",
//					"p_at": "2019-12-17 22:06:49",
//					"s": 1,
//					"g": 10
//		}
//	}
//	}

    @SkipSessionCheck
    @RequestMapping(value = baseUri + "/jsp/auth_certificate", method = {RequestMethod.GET, RequestMethod.POST}, produces = {"text/html; charset=UTF-8"})
    public ModelAndView authCertificate(HttpServletRequest request,
                                        @RequestParam(value = "receipt_id", required = true) String receipt_id) {
        ModelAndView model = new ModelAndView();
        model.addObject("systemBaseUrl", systemBaseUrl);
        try {
            BootPayApi api = new BootPayApi();
            api.getAccessToken();
            HttpResponse res = api.certificate(receipt_id);
            String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
            logger.debug(str);
            JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
            JsonObject certificate = jsonObject.getAsJsonObject("data").getAsJsonObject("certificate");

            String orderId = jsonObject.getAsJsonObject("data").get("order_id").getAsString();
            String username = certificate.get("username").getAsString();
            String phone = certificate.get("phone").getAsString();
            String birth = certificate.get("birth").getAsString();
            String gender = certificate.get("gender").getAsString();
            String unique = certificate.get("unique").getAsString();
            String di = certificate.get("di").getAsString();

            //서버에 인증 정보를 저장한다. 인증 정보가 조작 되는 것을 방지하기 위함이다.
            Verification verification = new Verification();
            verification.setMedia("external");
            verification.setToken(orderId + unique);
            verification.setMobile(phone);
            verification.setNumber(orderId);
            verification.setName(username);
            verficationService.request(verification);

            Map<String, String> map = new HashMap<String, String>();
            map.put("order_id", orderId);
            map.put("username", username);
            map.put("phone", phone);
            map.put("birth", birth);
            map.put("gender", gender);
            map.put("unique", unique);
            map.put("di", di);
            map.put("token", orderId + unique);


            model.addAllObjects(map);

        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
        }

        model.setViewName("danal/auth_certificate");
        return model;
    }

    @SkipSessionCheck
    @RequestMapping(value = baseUri + "/jsp/echoss", method = {RequestMethod.GET, RequestMethod.POST}, produces = {"text/html; charset=UTF-8"})
    public ModelAndView echoss(HttpServletRequest request, String merchantCode, Long memberSeqNo) {
        ModelAndView model = new ModelAndView();

        String licenseId;
        if (storeType.equals("PROD")) {
            licenseId = "p2b00d801f56c42e0b16c53f10b42e148";
        } else {
            licenseId = "d7ce78d9b50b34679ae7b3c48800b9f36";
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("licenseId", licenseId);
        map.put("memberSeqNo", memberSeqNo.toString());
        map.put("merchantCode", merchantCode);

        model.addAllObjects(map);
        model.addObject("systemBaseUrl", systemBaseUrl);
        model.setViewName("echoss/echoss");
        return model;
    }

    @SkipSessionCheck
    @RequestMapping(value = baseUri + "/jsp/auth_cancel", method = {RequestMethod.GET, RequestMethod.POST}, produces = {"text/html; charset=UTF-8"})
    public ModelAndView authCertificate(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        model.addObject("systemBaseUrl", systemBaseUrl);
        model.setViewName("danal/auth_cancel");
        return model;
    }

    @SkipSessionCheck
    @RequestMapping(value = baseUri + "/jsp/reapPay", method = {RequestMethod.GET, RequestMethod.POST}, produces = {"text/html; charset=UTF-8"})
    public ModelAndView reapPay(HttpServletRequest request, Long purchaseSeqNo, Long luckyBolPurchaseSeqNo, Long luckyboxPurchaseSeqNo, Long luckyBoxDeliveryPurchaseSeqNo, Long luckyPickPurchaseSeqNo, Long luckyPickDeliveryPurchaseSeqNo) throws ResultCodeException {

//        지급ID - OKPM000000000581
//        UID - OKPT000000001129

        ModelAndView model = new ModelAndView();

        Map<String, String> map = new HashMap<String, String>();

        map.put("userId", ID);
        map.put("testPay", TEST_PAY);
        map.put("actionUrl", reapPayUrl);
        map.put("pgMerchName", "루트37");
        map.put("storeUrl", "https://www.root37.net");
        map.put("storeBizNo", "1568702734");
        map.put("productType", "R");
        map.put("currencyType", "KRW");
        map.put("payMethod", "CARD");

        if(luckyBolPurchaseSeqNo != null){
            LuckyBolPurchase luckyBolPurchase = luckyBolService.getLuckyBolPurchase(luckyBolPurchaseSeqNo);

            if (!luckyBolPurchase.getStatus().equals("before")) {
                throw new NotPermissionException();
            }

            map.put("orderSeq", luckyBolPurchase.getOrderId());


            map.put("goodsCode", "BL"+luckyBolPurchase.getLuckyBolSeqNo());

            String title = luckyBolPurchase.getTitle();
            if (title.length() > 10) {
                title = title.substring(0, 10);
            }

            map.put("goodsName", "럭키쇼핑 상품구매");
            map.put("totAmt", String.valueOf(luckyBolPurchase.getEngagedPrice()));
            Float vat = (luckyBolPurchase.getEngagedPrice() * 0.1f);
            map.put("vatAmt", String.valueOf(vat.intValue()));
            //        map.put("svcAmt", "10");

            Member member = memberService.getMemberBySeqNo(luckyBolPurchase.getMemberSeqNo());

            String match = "[^\uAC00-\uD7A30-9a-zA-Z\\s]";
            String name = member.getNickname().replaceAll(match, "");
            map.put("custName", name);
            if (!AppUtil.isEmpty(member.getEmail())) {
                map.put("custEmail", member.getEmail());
            }

            map.put("custPhone", luckyBolPurchase.getBuyerTel());
            map.put("returnUrl", systemBaseUrl + "/store/api/luckyBol/paymentLuckyBolPurchase");


        }else if (purchaseSeqNo != null) {

            Purchase purchase = purchaseService.getPurchaseBySeqNo(purchaseSeqNo);

            if (purchase.getStatus() != 1) {
                throw new NotPermissionException();
            }

            map.put("orderSeq", purchase.getOrderId());

            List<PurchaseProduct> purchaseProductList = purchaseService.getPurchaseProductList(purchase.getSeqNo());

            if (purchaseProductList == null || purchaseProductList.isEmpty()) {
                throw new InvalidBuyException();
            }

            map.put("goodsCode", purchaseProductList.get(0).getProductPriceCode());

            String title = purchase.getTitle();
            if (title.length() > 10) {
                title = title.substring(0, 10);
            }

            map.put("goodsName", title);
            map.put("totAmt", String.valueOf(purchase.getPgPrice()));
            Float vat = (purchase.getPgPrice() * 0.1f);
            map.put("vatAmt", String.valueOf(vat.intValue()));
            //        map.put("svcAmt", "10");

            Member member = memberService.getMemberBySeqNo(purchase.getMemberSeqNo());

            String match = "[^\uAC00-\uD7A30-9a-zA-Z\\s]";
            String name = member.getNickname().replaceAll(match, "");
            map.put("custName", name);
            if (!AppUtil.isEmpty(member.getEmail())) {
                map.put("custEmail", member.getEmail());
            }

            map.put("custPhone", purchase.getBuyerTel());
            map.put("returnUrl", systemBaseUrl + "/store/api/purchase/paymentPurchase");

        } else if (luckyboxPurchaseSeqNo != null) {
            LuckyBoxPurchase luckyBoxPurchase = luckyBoxService.getLuckyBoxPurchase(luckyboxPurchaseSeqNo);

            if (luckyBoxPurchase.getStatus() != 1) {
                throw new NotPermissionException();
            }

            map.put("orderSeq", luckyBoxPurchase.getOrderNo());
            map.put("goodsCode", "LB" + luckyBoxPurchase.getLuckyBoxSeqNo());
            map.put("goodsName", luckyBoxPurchase.getTitle());

            if(luckyBoxPurchase.getPgPrice() == null){
                luckyBoxPurchase.setPgPrice(luckyBoxPurchase.getPrice().intValue());
            }
            map.put("totAmt", String.valueOf(luckyBoxPurchase.getPgPrice().intValue()));
            Float vat = (luckyBoxPurchase.getPgPrice() * 0.1f);

            map.put("vatAmt", String.valueOf(vat.intValue()));
            //        map.put("svcAmt", "10");
            String match = "[^\uAC00-\uD7A30-9a-zA-Z\\s]";
            String name = luckyBoxPurchase.getMember().getNickname().replaceAll(match, "");
            map.put("custName", name);
            if (!AppUtil.isEmpty(luckyBoxPurchase.getMember().getEmail())) {
                map.put("custEmail", luckyBoxPurchase.getMember().getEmail());
            }

            map.put("custPhone", luckyBoxPurchase.getMember().getMobileNumber().replace("luckyball##", ""));
            map.put("returnUrl", systemBaseUrl + "/store/api/luckyBox/paymentLuckyBoxPurchase");

        } else if (luckyBoxDeliveryPurchaseSeqNo != null) {

            LuckyBoxDeliveryPurchase luckyBoxDeliveryPurchase = luckyBoxService.getLuckyBoxDeliveryPurchase(luckyBoxDeliveryPurchaseSeqNo);

            if (luckyBoxDeliveryPurchase.getStatus() != 1) {
                throw new NotPermissionException();
            }

            map.put("orderSeq", luckyBoxDeliveryPurchase.getOrderNo());
            map.put("goodsCode", "LBD" + luckyBoxDeliveryPurchase.getLuckyBoxPurchaseItemSeqNo());
            map.put("goodsName", luckyBoxDeliveryPurchase.getLuckyBoxPurchaseItem().getLuckyBoxTitle());
            map.put("totAmt", String.valueOf(luckyBoxDeliveryPurchase.getPgPrice().intValue()));
            Float vat = (luckyBoxDeliveryPurchase.getPgPrice() * 0.1f);
            map.put("vatAmt", String.valueOf(vat.intValue()));
            //        map.put("svcAmt", "10");
            String match = "[^\uAC00-\uD7A30-9a-zA-Z\\s]";
            String name = luckyBoxDeliveryPurchase.getMember().getNickname().replaceAll(match, "");
            map.put("custName", name);
            if (!AppUtil.isEmpty(luckyBoxDeliveryPurchase.getMember().getEmail())) {
                map.put("custEmail", luckyBoxDeliveryPurchase.getMember().getEmail());
            }

            map.put("custPhone", luckyBoxDeliveryPurchase.getMember().getMobileNumber().replace("luckyball##", ""));
            map.put("returnUrl", systemBaseUrl + "/store/api/luckyBox/paymentLuckyBoxDeliveryPurchase");

        } else if (luckyPickPurchaseSeqNo != null) {
            LuckyPickPurchase luckyPickPurchase = luckyPickService.getLuckyPickPurchase(luckyPickPurchaseSeqNo);

            if (luckyPickPurchase.getStatus() != 1) {
                throw new NotPermissionException();
            }

            map.put("orderSeq", luckyPickPurchase.getOrderNo());
            map.put("goodsCode", "LB" + luckyPickPurchase.getLuckyPickSeqNo());
            map.put("goodsName", luckyPickPurchase.getTitle());
            map.put("totAmt", String.valueOf(luckyPickPurchase.getPrice().intValue()));
            Float vat = (luckyPickPurchase.getPrice() * 0.1f);
            map.put("vatAmt", String.valueOf(vat.intValue()));
            //        map.put("svcAmt", "10");
            String match = "[^\uAC00-\uD7A30-9a-zA-Z\\s]";
            String name = luckyPickPurchase.getMember().getNickname().replaceAll(match, "");
            map.put("custName", name);
            if (!AppUtil.isEmpty(luckyPickPurchase.getMember().getEmail())) {
                map.put("custEmail", luckyPickPurchase.getMember().getEmail());
            }

            map.put("custPhone", luckyPickPurchase.getMember().getMobileNumber().replace("luckyball##", ""));
            map.put("returnUrl", systemBaseUrl + "/store/api/luckyPick/paymentLuckyPickPurchase");
        } else if (luckyPickDeliveryPurchaseSeqNo != null) {

            LuckyPickDeliveryPurchase luckyPickDeliveryPurchase = luckyPickService.getLuckyPickDeliveryPurchase(luckyPickDeliveryPurchaseSeqNo);

            if (luckyPickDeliveryPurchase.getStatus() != 1) {
                throw new NotPermissionException();
            }

            map.put("orderSeq", luckyPickDeliveryPurchase.getOrderNo());
            map.put("goodsCode", "LBD" + luckyPickDeliveryPurchase.getLuckyPickPurchaseItemSeqNo());
            map.put("goodsName", luckyPickDeliveryPurchase.getLuckyPickPurchaseItem().getLuckyPickTitle());
            map.put("totAmt", String.valueOf(luckyPickDeliveryPurchase.getPgPrice().intValue()));
            Float vat = (luckyPickDeliveryPurchase.getPgPrice() * 0.1f);
            map.put("vatAmt", String.valueOf(vat.intValue()));
            //        map.put("svcAmt", "10");

            String match = "[^\uAC00-\uD7A30-9a-zA-Z\\s]";
            String name = luckyPickDeliveryPurchase.getMember().getNickname().replaceAll(match, "");
            map.put("custName", name);
            if (!AppUtil.isEmpty(luckyPickDeliveryPurchase.getMember().getEmail())) {
                map.put("custEmail", luckyPickDeliveryPurchase.getMember().getEmail());
            }

            map.put("custPhone", luckyPickDeliveryPurchase.getMember().getMobileNumber().replace("luckyball##", ""));
            map.put("returnUrl", systemBaseUrl + "/store/api/luckyPick/paymentLuckyPickDeliveryPurchase");

        }

        map.put("successUrl", systemBaseUrl + "/store/api/jsp/reapPaySuccess");
        map.put("failureUrl", systemBaseUrl + "/store/api/jsp/reapPayFail");
        logger.info("param", map.toString());
        model.addObject("data", map);
//        model.addAllObjects(map);
        model.addObject("systemBaseUrl", systemBaseUrl);
        model.setViewName("reapPay/pay");
        return model;
    }

    @SkipSessionCheck
    @RequestMapping(value = baseUri + "/jsp/reapPaySuccess", method = {RequestMethod.GET, RequestMethod.POST}, produces = {"text/html; charset=UTF-8"})
    public ModelAndView reapPaySuccess(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();


        String tranSeq = request.getParameter("tranSeq");
        String status = request.getParameter("status");
        String appDt = request.getParameter("appDt");
        String appTm = request.getParameter("appTm");
        String issCd = request.getParameter("issCd");
        String appNo = request.getParameter("appNo");
        String message1 = request.getParameter("message1");
        String message2 = request.getParameter("message2");
        String amount = request.getParameter("amount");
        String goodsName = request.getParameter("goodsName");
        String currencyType = request.getParameter("currencyType");
        String installment = request.getParameter("installment");
        String orderNumber = request.getParameter("orderNumber");
        String custName = request.getParameter("custName");
        String custEmail = request.getParameter("custEmail");
        String custPhone = request.getParameter("custPhone");

        Map<String, String> map = new HashMap<String, String>();

        model.addAllObjects(map);
        model.addObject("systemBaseUrl", systemBaseUrl);
        model.setViewName("reapPay/paySuccess");
        return model;
    }

    @SkipSessionCheck
    @RequestMapping(value = baseUri + "/jsp/reapPayFail", method = {RequestMethod.GET, RequestMethod.POST}, produces = {"text/html; charset=UTF-8"})
    public ModelAndView payFailure(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();

        Map<String, String> map = new HashMap<String, String>();

        model.addAllObjects(map);
        model.addObject("systemBaseUrl", systemBaseUrl);
        model.setViewName("reapPay/payFailure");
        return model;
    }
}
