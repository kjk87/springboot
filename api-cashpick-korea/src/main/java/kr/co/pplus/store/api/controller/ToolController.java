package kr.co.pplus.store.api.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.jpa.model.Lottery;
import kr.co.pplus.store.api.jpa.service.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.exception.SessionNotFoundException;
import kr.co.pplus.store.mvc.service.*;
import kr.co.pplus.store.queue.MsgProducer;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.QueueRequest;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.util.*;
import org.apache.commons.io.IOUtils;
import org.jivesoftware.smack.util.StringUtils;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.*;


@RestController
public class ToolController extends RootController {

    private static final String REQUEST_PREFIX = "Request: ";
    private static final String CRLF = "<BR>";

    @Autowired
    private MsgProducer producer;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private CashBolService cashBolSvc;

    @Autowired
    private CommonService commSvc;

    @Autowired
    private QueueService queueSvc;

    @Autowired
    private UserService userSvc;

    @Autowired
    private AuthService authSvc;

    @Autowired
    private PlusService plusService;

    @Autowired
    private ShippingService shippingService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    BolService bolService;

//	@Autowired
//	BuyService buyService;

    @Autowired
    @Qualifier("scheduler")
    private Scheduler scheduler;

    @Autowired
    GiftishowService giftishowService;

    @Autowired
    CpeReportService cpeReportService;

    @Autowired
    ContactJpaService contactJpaService;

    @Autowired
    BuffService buffService;

    @Autowired
    PointService pointService;

    @Autowired
    ReapPayService reapPayService;

    @Autowired
    LuckyBoxService luckyBoxService;

    @Autowired
    LuckyPickService luckyPickService;

    @Autowired
    LuckyCouponService luckyCouponService;

    @Autowired
    BuffWalletService buffWalletService;

    @Autowired
    AuthService authService;

    @Autowired
    LotteryService lotteryService;

    @Autowired
    EventService eventService;

    @Value("${STORE.REDIS_PREFIX}")
    String REDIS_PREFIX = "pplus-";


    @Value("${MAIL.FROM}")
    private String FROM;

    @Value("${MAIL.FROM.NICKNAME}")
    private String NICKNAME;

    @Value("${STORE.APPLE_RECEIPTVALIDATION_URL}")
    String APPLE_RECEIPTVALIDATION_URL = "https://buy.itunes.apple.com/verifyReceipt";


    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/about/**")
    public Map<String, Object> about(Session session) {
        return super.about();
    }

    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/pushQueue/**")
    public Map<String, Object> pushQueue(Session session, String reqType, String msg) {
        Article post = new Article();
        post.setNo((long) 1231231);
        if (!StringUtils.isEmpty(msg))
            post.setContents(msg);
        QueueRequest req = new QueueRequest(reqType);
        req.setMsg(post);
        producer.push(req);
        return result(200);
    }

    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/generatePassword/**")
    public Map<String, Object> generatePassword(Session session, String loginId, String password) {
        return result(200, "password", SecureUtil.encryptPassword(loginId, password));
    }

    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/error/**")
    public Map<String, Object> error(Session session, HttpServletRequest request,
                                     HttpServletResponse response, Model model) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        Throwable throwable = (Throwable) request
                .getAttribute("javax.servlet.error.exception");

        String title = throwable.getMessage();
        String requestUri = (String) request
                .getAttribute("javax.servlet.error.request_uri");
        if (requestUri == null) {
            requestUri = "Unknown";
        }
        response.setStatus(statusCode);
        String message = MessageFormat.format(
                "{0} returned for {1} with message {2}", statusCode,
                requestUri, title);
        model.addAttribute("errorMessage", message);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String server = "";

        StringBuffer reqBuffer = new StringBuffer();
        if (!AppUtil.isMultipart(request)) {
//            reqBuffer.append(CRLF).append("################################")
//                    .append(CRLF).append(REQUEST_PREFIX)
//                    .append(request.getRequestURI());

            if (request.getQueryString() != null && !request.getQueryString().isEmpty())
                reqBuffer.append('?').append(request.getQueryString());

            reqBuffer.append(" ").append(request.getMethod());

            reqBuffer.append(CRLF).append("Request Body: ").append(CRLF);

            if (AppUtil.isFormSubmit(request)) {
                boolean firstParam = true;
                for (@SuppressWarnings("rawtypes")
                     Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
                    String pn = (String) e.nextElement();
                    String pv[] = request.getParameterValues(pn);
                    for (int l = 0; l < pv.length; l++) {
                        if (firstParam) {
                            reqBuffer.append(pn + "=" + pv[l]);
                            firstParam = false;
                        } else {
                            reqBuffer.append("&").append(pn).append("=")
                                    .append(pv[l]);
                        }
                    }

                }
            } else {
                if ("POST".equalsIgnoreCase(request.getMethod())) {
                    try {
                        reqBuffer.append(IOUtils.toString(request.getReader()));
                    } catch (Exception ex) {

                    }
                }
            }

            reqBuffer.append(CRLF).append("################################").append(CRLF);

        }
        String out = server + "<br><br>" + reqBuffer.toString()
                + sw.toString().replaceAll("\\r\\n", "<br>");
        try {
            if (!skipMail(throwable))
                mailSender.sendToHTML("sykim91@mindwareworks.com", FROM, NICKNAME, "P+ Error", out);
        } catch (Exception ex) {

        }

        if (throwable instanceof ResultCodeException) {
            return ((ResultCodeException) throwable);
        }
        return result(501, "message", message, "statusCode", statusCode);
    }

    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/printSearchOpt/**")
    public Map<String, Object> printSearchOpt(Session session, SearchOpt opt) {
        return result(200, "opt", opt);
    }

    private boolean skipMail(Throwable t) {
        if (t instanceof SessionNotFoundException)
            return true;

        return false;
    }

    @RequestMapping(value = baseUri + "/tool/push1/**")
    public void push1(Session session, @RequestBody PushMsg msg) {
        producer.push(msg);
    }

    @RequestMapping(produces = "text/plain;charset=UTF-8", value = baseUri + "/tool/encMobile/**")
    public String encMobile(Session session, String src) {
        return SecureUtil.encryptMobileNumber(src);
    }

    @RequestMapping(produces = "text/plain;charset=UTF-8", value = baseUri + "/tool/decMobile/**")
    public String decMobile(Session session, String src) {
        return SecureUtil.decryptMobileNumber(src);
    }

    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/transactionalTest/**")
    public Map<String, Object> transactionalTest(Session session, NoOnlyKey val) throws ResultCodeException {
        return result(commSvc.insertTrTest(val));
    }

    @RequestMapping(value = baseUri + "/tool/printTaskAll/**")
    public void printTaskAll(Session session) throws Exception {
        StoreUtil.printTaskAll(scheduler);
    }

    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/saveTerms/**")
    public Map<String, Object> saveTerms(Session session, Terms terms) {
        return result(commSvc.saveTerms(terms));
    }

    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/ios/receiptVerification/**")
    public Map<String, Object> receiptVerification(Session session, @RequestBody Map<String, Object> properties) throws Exception {
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/json");
        Map res = HttpUtil.requestJsonObject(APPLE_RECEIPTVALIDATION_URL, "UTF-8", 5000, 5000, header, properties, Map.class);

        return res;
    }

    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/getRedisKeysAll/**")
    public Map<String, Object> getRedisKeysAll(Session session) {
        String pattern = REDIS_PREFIX + "*";
        Set<String> redisKeys = RedisUtil.getInstance().getObj(pattern); //redisTemplate.keys(pattern);
        return result(200, "keys", redisKeys);
    }

    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/getRedisValue/**")
    public Map<String, Object> getRedisValue(Session session, String key) {
        return result(200, "row", RedisUtil.getInstance().getObj(key));
    }

    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/getRedisHashValue/**")
    public Map<String, Object> getRedisHashValue(Session session, String key, String hashKey) {
        return result(200, "row", RedisUtil.getInstance().getOpsHash(key, hashKey));
    }

    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/executeTask/**")
    public Map<String, Object> executeTask(Session session, String taskName) {
        JobKey jobKey = new JobKey(taskName);
        try {
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException ex) {
            ex.printStackTrace();
        }
        return result(200);
    }

//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/tool/expired/**")
//	public Map<String,Object> executeExpired() {
//		queueSvc.checkExpired();
//		return result(200);
//	}

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/leave/**")
    public Map<String, Object> leave() {
        userSvc.leaveAll();
        return result(200);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/getUserMobileNumberList/**")
    public Map<String, Object> getUserMobileNumberList() {
        return result(200, "rows", userSvc.getUserMobileNumberList());
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/updatetMobileLuckyball/**")
    public Map<String, Object> updateMobileLuckyball() {
        userSvc.updatetMobileLuckyball();
        return result(200);
    }

//	@SkipSessionCheck
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/tool/expiredGoods/**")
//	public Map<String,Object> executeExpiredGoods() {
//		queueSvc.checkExpiredGoods();
//		return result(200);
//	}

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/updateMobile/**")
    public Map<String, Object> executeUpdateMobile(User user) throws ResultCodeException {
        User savedUser = userSvc.getUserByLoginId(user.getLoginId());
        savedUser.setMobile(savedUser.getMobile().replace("-", ""));
        Integer r = userSvc.updateMobile(savedUser);
        return result(200);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/initializationPassword/**")
    public Map<String, Object> initializationPassword(String loginId) throws ResultCodeException {
        authSvc.initializationPassword(loginId);
        return result(200);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/deletePlusLeaveUser/**")
    public Map<String, Object> deletePlusLeaveUser(User user) throws ResultCodeException {
        plusService.deleteByUser(user);
        return result(200);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/deliveryComplete/**")
    public Map<String, Object> deliveryPurchaseComplete() throws ResultCodeException {
        shippingService.deliveryPurchaseComplete();
        return result(200);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/luckyBoxDeliveryComplete/**")
    public Map<String, Object> luckyBoxDeliveryComplete() throws ResultCodeException {
        luckyBoxService.deliveryComplete();
        luckyPickService.deliveryComplete();
        return result(200);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/purchaseComplete/**")
    public Map<String, Object> setCompletePurchaseProductList() throws ResultCodeException {
        purchaseService.setCompletePurchaseProductList();
        return result(200);
    }

//	@SkipSessionCheck
//	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/tool/noMemberCancelTest/**")
//	public Map<String,Object> noMemberCancelTest(String tranId, String price, String shopCode) throws ResultCodeException {
//
//		String resultCode = null;
//		try{
//			resultCode = buyService.cancelTest(tranId, price, shopCode);
//		}catch (Exception e){
//
//		}
//
//		return result(Const.E_SUCCESS, "row", resultCode);
//	}

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/syncGiftishow/**")
    public Map<String, Object> syncGiftishow() throws ResultCodeException {
        giftishowService.syncGoodsList();
        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/updatePurchaseProductExpired/**")
    public Map<String, Object> updatePurchaseProductExpired() throws ResultCodeException {
        purchaseService.updatePurchaseProductExpired();
        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/countCpeReport")
    public Map<String, Object> cpeReport(String type, String startDuration, String endDuration) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", cpeReportService.countCpeReport(type, startDuration, endDuration));
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/getUserAllMobileByStatus")
    public Map<String, Object> getUserAllMobileByStatus() throws ResultCodeException {

        SearchOpt opt = new SearchOpt();
        opt.setFilter(new ArrayList<String>());
        opt.getFilter().add("normal");
        List<User> userList = userSvc.getUserAllMobileByStatus(opt, Const.APP_TYPE_LUCKYBOL);
        return result(Const.E_SUCCESS, "rows", userList);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/deleteContact")
    public Map<String, Object> deleteContact() throws ResultCodeException {

        contactJpaService.delete();
        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/reapPayLogin")
    public Map<String, Object> givePoint() throws ResultCodeException {

        reapPayService.login(1L);
        reapPayService.login(2L);
        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/sendCoupon")
    public Map<String, Object> sendCoupon() throws ResultCodeException {

        luckyCouponService.sendCoupon();
        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/redisTest")
    public Map<String, Object> redisTest(String value) throws ResultCodeException {

        List<String> keyList = RedisUtil.getInstance().getKeys(value);
        return result(Const.E_SUCCESS, "rows", keyList);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/returnTest")
    public String returnTest() throws ResultCodeException {

        return "SUCCESS";
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/getCoinValue")
    public Map<String, Object> getCoinValue() throws ResultCodeException {

        buffWalletService.getCoinValue();
        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/joinLotteryTest")
    public Map<String, Object> joinLottery(Long memberSeqNo) throws ResultCodeException {

        lotteryService.joinNative(memberSeqNo, 9L, 10000, "pickWin");
        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/lotteryTableNoTest")
    public Map<String, Object> joinLottery(Long memberSeqNo, Long lotterySeqNo, Integer tableNo) throws ResultCodeException {

        lotteryService.lotteryTableNoTest(memberSeqNo, lotterySeqNo, 10000, tableNo);
        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/joinLotteryTest2")
    public Map<String, Object> joinLotteryTest2(Long memberSeqNo) throws ResultCodeException {
        eventService.joinLotto(memberSeqNo, 10, "eventJoin");
        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/totalGive")
    public Map<String, Object> totalGiveTest() throws ResultCodeException {

        lotteryService.totalGive();
        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/tool/totalPointGive")
    public Map<String, Object> totalPointGive() throws ResultCodeException {

        lotteryService.totalPointGive();
        return result(Const.E_SUCCESS);
    }

}
