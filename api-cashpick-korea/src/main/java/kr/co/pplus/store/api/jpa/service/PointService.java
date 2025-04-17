package kr.co.pplus.store.api.jpa.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.model.bootpay.request.Cancel;
import kr.co.pplus.store.api.jpa.model.bootpay.request.Token;
import kr.co.pplus.store.api.jpa.model.bootpay.response.ResToken;
import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkCancelRequest;
import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkCancelResponse;
import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkPayRequest;
import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkPayResponse;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.util.FTLinkPayApi;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class PointService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(PointService.class);

    private final String BASE_URL = "https://api.bootpay.co.kr/";
    private final String URL_ACCESS_TOKEN = BASE_URL + "request/token";
    private final String URL_VERIFY = BASE_URL + "receipt";
    private final String URL_CANCEL = BASE_URL + "cancel";

    @Autowired
    PointBuyRepository pointBuyRepository;

    @Autowired
    PointHistoryRepository pointHistoryRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EventJoinRepository eventJoinRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    LpngCallbackRepository lpngCallbackRepository;

    @Autowired
    LpngCallbackResultRepository lpngCallbackResultRepository;

    @Autowired
    EventJpaService eventJpaService;

    @Autowired
    BolService bolService;

    @Autowired
    CashExchangeRepository cashExchangeRepository;

    @Autowired
    MemberOnlyPointRepository memberOnlyPointRepository;

    @Autowired
    CashExchangeRateRepository cashExchangeRateRepository;

    @Value("${STORE.BOOTPAY.CASH_APP_ID}")
    String CASH_APP_ID = "";

    @Value("${STORE.BOOTPAY.CASH_PRIVATE_KEY}")
    String CASH_PRIVATE_KEY = "";

    @Value("${STORE.TYPE}")
    String storeType = "STAGE";

    @Value("${STORE.DANAL.CPID}")
    String CPID = "9810030929";

    @Value("${STORE.EXCHANGE_BOL_LIMIT}")
    Long EXCHANGE_BOL_LIMIT = 50000L;


    public String getAccessToken() throws Exception {

        Token token = new Token();
        token.application_id = CASH_APP_ID;
        token.private_key = CASH_PRIVATE_KEY;

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = getPost(URL_ACCESS_TOKEN, new StringEntity(new Gson().toJson(token), "UTF-8"));

        HttpResponse res = client.execute(post);
        String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
        ResToken resToken = new Gson().fromJson(str, ResToken.class);

        logger.debug("bootPay.getAccessToken() response : " + str);
        if (resToken.status == 200) {
            return resToken.data.token;
        } else {
            return null;
        }
    }

    public HttpResponse verify(String receipt_id, String token) throws Exception {
        if (token == null || token.isEmpty()) throw new Exception("token 값이 비어있습니다.");

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = getGet(URL_VERIFY + "/" + receipt_id);
        get.setHeader("Authorization", token);
        return client.execute(get);
    }

    private HttpPost getPost(String url, StringEntity entity) {
        HttpPost post = new HttpPost(url);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Accept-Charset", "utf-8");
        post.setEntity(entity);
        return post;
    }

    private HttpGet getGet(String url) throws Exception {
        HttpGet get = new HttpGet(url);
        URI uri = new URIBuilder(get.getURI()).build();
        get.setURI(uri);
        return get;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void bootPayCallback(Map<String, String> map) throws ResultCodeException {
        if (map.get("status").equals("1")) {
            String receiptId = map.get("receipt_id");
            PointBuy pointBuy = pointBuyRepository.findByReceiptId(receiptId);
            if (pointBuy != null && pointBuy.getPayMethod().equals("vbank")) {

                String dateStr = AppUtil.localDatetimeNowString();

                pointBuy.setStatus(Integer.valueOf(map.get("status")));
                pointBuy.setBankName(map.get("payment_data[bankname]"));

                pointBuy.setAccountHolder(map.get("payment_data[accountholder]"));
                pointBuy.setAccount(map.get("payment_data[account]"));
                pointBuy.setExpireDate(map.get("payment_data[expiredate]"));
                pointBuy.setUserName(map.get("payment_data[username]"));
                pointBuy.setCashResult(map.get("payment_data[cash_result]"));
                pointBuy.setModDatetime(dateStr);

                pointBuy = pointBuyRepository.saveAndFlush(pointBuy);

                PointHistory pointHistory = new PointHistory();
                pointHistory.setMemberSeqNo(pointBuy.getMemberSeqNo());
                pointHistory.setType("charge");
                pointHistory.setPoint(pointBuy.getCash().floatValue());
                pointHistory.setSubject("플레이 구매적립");
                updatePoint(pointBuy.getMemberSeqNo(), pointHistory);
            }
        }
    }

    public boolean bootPayCancel(Cancel cancel) {
        try {

            String token = getAccessToken();
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = getPost(URL_CANCEL, new StringEntity(new Gson().toJson(cancel), "UTF-8"));
            post.setHeader("Authorization", token);
            HttpResponse res = client.execute(post);
            String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
            logger.debug("bootPayCancel Cancel : response data : " + str);

            if (res.getStatusLine().getStatusCode() == 200) {
                return true;
            } else {
                logger.error(convertStreamToString(res.getEntity().getContent()));
                throw new Exception("bootPayCancel Error");
            }
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            return false;
        }
    }

    public boolean pointCancel(Long seqNo) {
        LpngCallback callback = lpngCallbackRepository.findByPointBuySeqNo(seqNo);

        try {
            FTLinkCancelRequest ftLinkCancelRequest = new FTLinkCancelRequest();
            ftLinkCancelRequest.setShopcode("200002659");
            ftLinkCancelRequest.setLoginId("test08");
            ftLinkCancelRequest.setCancelAmt(callback.getPrice().toString());
            ftLinkCancelRequest.setOrderNo(callback.getLpngOrderNo());
            ftLinkCancelRequest.setTranNo(callback.getPgTranId());

            FTLinkCancelResponse res = FTLinkPayApi.cancelRequest(ftLinkCancelRequest);

            if (res.getErrCode().equals("00") || res.getErrCode().equals("0000")) {

                LpngCallbackResult callbackResult = callback.getResult();

                callbackResult.setErrorCode(res.getErrCode());
                callbackResult.setErrorMsg(res.getErrMessage());
                lpngCallbackResultRepository.save(callbackResult);

                callback.setStatus(false);
                callback.setProcess(LpngProcess.CANCEL.getType());
                callback = lpngCallbackRepository.saveAndFlush(callback);

                return true;
            } else {
                logger.error("ftlinkCancel Error " + res.getErrMessage());
                return false;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public void danalNoti(User user, JsonObject jsonObject) {

        try {

	        /*
	         {"status":200,"code":0,"message":"",
	         "data":{"receipt_id":"5fbb11d50627a8002bbcec21","order_id":"O20112335179329","name":"단일형 상품","item_name":"단일형 상품","price":30000,"tax_free":0,"remain_price":30000,"remain_tax_free":0,"cancelled_price":0,"cancelled_tax_free":0,"receipt_url":"https://www.danalpay.com/receipt/creditcard/view.aspx?datatype=receipt&cpid=9810030929&data=xYSaEE7psDG%2FV7o%2FyzZzfHg50lhXOyFpIqGB3vTRk85q0AbKj%2B6IYjqSN7tb%0An2zo%0A",
	         "unit":"krw","pg":"danal","method":"card","pg_name":"다날","method_name":"ISP / 앱카드 결제",
	         "payment_data":{"card_name":"KB국민","card_no":"5598690000005020","card_quota":"00","card_code":"0300","card_auth_no":"30043797","receipt_id":"5fbb11d50627a8002bbcec21","n":"단일형 상품","p":30000,"tid":"202011231035196083903400","pg":"다날","pm":"ISP / 앱카드 결제","pg_a":"danal","pm_a":"card","o_id":"O20112335179329","p_at":"2020-11-23 10:35:44","s":1,"g":2},
	         "requested_at":"2020-11-23 10:35:17","purchased_at":"2020-11-23 10:35:44","status":1,"status_en":"complete","status_ko":"결제완료"}}
	         */


	        /*
	        0 - 결제 대기 상태입니다. 승인이 나기 전의 상태입니다.
	        1 - 결제 완료된 상태입니다.
	        2 - 결제승인 전 상태입니다. transactionConfirm() 함수를 호출하셔서 결제를 승인해야합니다.
	        3 - 결제승인 중 상태입니다. PG사에서 transaction 처리중입니다.
	        20 - 결제가 취소된 상태입니다.
	        -20 - 결제취소가 실패한 상태입니다.
	        -30 - 결제취소가 진행중인 상태입니다.
	        -1 - 오류로 인해 결제가 실패한 상태입니다.
	        -2 - 결제승인이 실패하였습니다.
	        */

            Integer status = jsonObject.get("status").getAsInt();
            Integer code = jsonObject.get("code").getAsInt();


            if (status.equals(200) && code.equals(0)) {

                JsonObject data = jsonObject.getAsJsonObject("data");

                String orderNo = data.get("order_id").getAsString();
                Integer process = data.get("status").getAsInt();
                String pg = data.get("pg").getAsString().toUpperCase();
                Integer paymentPrice = data.get("price").getAsInt();

                JsonObject payment = data.get("payment_data").getAsJsonObject();

                String cardNo = payment.get("card_no").getAsString();
                String authNo = payment.get("card_auth_no").getAsString();
                String cardName = payment.get("card_name").getAsString();
                String cardQuota = payment.get("card_quota").getAsString();
                String cardCode = payment.get("card_code").getAsString();
                String tid = payment.get("tid").getAsString();


                String payMethod = "CARD";

                String cpid = CPID;

                String daouTrx = tid;

                String settDate = payment.get("p_at").getAsString(); // 2020-11-23 10:35:44


                String email = "";


                // 2020-11-23 10:35:44
                settDate = settDate.replaceAll("-", "");
                settDate = settDate.replaceAll(":", "");
                settDate = settDate.replaceAll(" ", "");

                String setDay = settDate.substring(0, 8);
                String setTime = settDate.substring(8);

                String reqdephold = "N";

                String type = "TEST";
                if (storeType.equals("PROD")) {
                    type = "USE";
                }

                String[] mobiles = user.getMobile().split("##");
                String mobile = null;
                if (mobiles.length == 2) {
                    mobile = mobiles[1];
                } else {
                    mobile = mobiles[0];
                }

                URIBuilder uriBuilder = new URIBuilder("http://pay.ftlink.co.kr/payalert/pplus/noti_cert.asp");
                uriBuilder
                        .addParameter("PAYMETHOD", URLEncoder.encode(payMethod, "EUC-KR"))
                        .addParameter("CPID", URLEncoder.encode(cpid, "EUC-KR"))
                        .addParameter("DAOUTRX", URLEncoder.encode(daouTrx, "EUC-KR"))
                        .addParameter("ORDERNO", URLEncoder.encode(orderNo, "EUC-KR"))
                        .addParameter("AMOUNT", URLEncoder.encode(paymentPrice + "", "EUC-KR"))
                        .addParameter("PRODUCTNAME", URLEncoder.encode("포인트 구매", "EUC-KR"))
                        .addParameter("SETDATE", URLEncoder.encode(settDate, "EUC-KR"))
                        .addParameter("AUTHNO", URLEncoder.encode(authNo, "EUC-KR"))
                        .addParameter("CARDCODE", URLEncoder.encode(cardCode, "EUC-KR"))
                        .addParameter("CARDNAME", URLEncoder.encode(cardName, "EUC-KR"))
                        .addParameter("CARDNO", URLEncoder.encode(cardNo, "EUC-KR"))
                        .addParameter("EMAIL", URLEncoder.encode(email, "EUC-KR"))
                        .addParameter("USERID", URLEncoder.encode(mobile, "EUC-KR"))
                        .addParameter("USERNAME", URLEncoder.encode(user.getName(), "EUC-KR"))
                        .addParameter("RESERVEDINDEX1", URLEncoder.encode("200002659", "EUC-KR"))
                        .addParameter("RESERVEDINDEX2", URLEncoder.encode("", "EUC-KR"))
                        .addParameter("RESERVEDINDEX3", URLEncoder.encode("", "EUC-KR"))
                        .addParameter("RESERVEDSTRING", URLEncoder.encode("", "EUC-KR"))
                        .addParameter("ISTEST", URLEncoder.encode(type, "EUC-KR"))
                        .addParameter("reqdephold", URLEncoder.encode(reqdephold, "EUC-KR"))
                        .addParameter("MANUAL_USED", "N")
                        .addParameter("PGCODE", "20"); // 다우-30, 다날-20


                URI uri = uriBuilder.build();

                HttpGet getMethod = new HttpGet(uri);

                getMethod.addHeader(new BasicHeader("Accept", "application/json"));
                getMethod.addHeader(new BasicHeader("Accept-Charset", "EUC-KR"));

                CloseableHttpClient httpclient = HttpClients.createDefault();

                logger.info("params ==> " + getMethod.toString());

                try {

                    CloseableHttpResponse response = httpclient.execute(getMethod);

                    String resultData2 = EntityUtils.toString(response.getEntity(), "UTF-8");


                    logger.info("finteck result ==> " + resultData2);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertPointBuy(User user, PointBuy pointBuy) throws ResultCodeException {

        try {

            EventJpa eventJpa = eventRepository.findBySeqNo(pointBuy.getEventSeqNo());
            Integer count = pointBuy.getCount();

            if (!eventJpaService.checkBuyJoinPossible(user.getNo(), eventJpa)) {
                if (pointBuy.getPayMethod().equals("card")) {
                    Cancel cancel = new Cancel();
                    cancel.receipt_id = pointBuy.getReceiptId();
                    cancel.cancel_username = "피플러스";
                    cancel.cancel_message = "수량 초과";
                    cancel.cancel_price = pointBuy.getCash();
                    bootPayCancel(cancel);
                }

                throw new AlreadyLimitException("max-join-count", "limited");
            }

            String dateStr = AppUtil.localDatetimeNowString();
            Date date = AppUtil.localDatetimeNowDate();

            pointBuy.setSeqNo(null);
            pointBuy.setMemberSeqNo(user.getNo());
            pointBuy.setStatus(0);

            if (pointBuy.getPayMethod().equals("card")) {

                String token = getAccessToken();

                if (token == null || token.isEmpty()) {
                    throw new InvalidCashException();
                }

                HttpResponse res = verify(pointBuy.getReceiptId(), token);
                String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
                JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");

                if (!data.get("status").getAsString().equals("1")) {
                    throw new Exception(" pay rejected !!!");
                }

                JsonObject payment_data = data.get("payment_data").getAsJsonObject();

                pointBuy.setStatus(data.get("status").getAsInt());
                pointBuy.setCash(data.get("price").getAsInt());
                pointBuy.setCardName(payment_data.get("card_name").getAsString());
                pointBuy.setCardQuota(payment_data.get("card_quota").getAsString());
                pointBuy.setCardAuthNo(payment_data.get("card_auth_no").getAsString());

                if (pointBuy.getPg().equals("danal")) {
                    danalNoti(user, jsonObject);
                }

                pointBuy.setRegDatetime(dateStr);
                pointBuy.setModDatetime(dateStr);
                pointBuy = pointBuyRepository.saveAndFlush(pointBuy);

            } else if (pointBuy.getPayMethod().equals("ftlink")) {
                FTLinkPayRequest ftLinkPayRequest = new FTLinkPayRequest();
                ftLinkPayRequest.setShopcode("200002659");//피플러스 샵코드
                ftLinkPayRequest.setOrder_req_amt(pointBuy.getCash().toString());
                ftLinkPayRequest.setOrder_hp(user.getMobile().replace(user.getAppType() + "##", ""));
                ftLinkPayRequest.setOrder_name(user.getName());
                ftLinkPayRequest.setComp_memno(user.getName());
                ftLinkPayRequest.setOrder_goodsname("포인트 구매");
                ftLinkPayRequest.setReq_installment(pointBuy.getInstallment());
                ftLinkPayRequest.setComp_orderno(pointBuy.getOrderId());
                ftLinkPayRequest.setAutokey(pointBuy.getAutoKey());
                ftLinkPayRequest.setReq_cardcode(pointBuy.getCardCode());
                ftLinkPayRequest.setManual_used("N");
                ftLinkPayRequest.setLoginId("test08");//피플러스 id
                ftLinkPayRequest.setServerType("");
                ftLinkPayRequest.setRoomId("");
                ftLinkPayRequest.setReqdephold("N");
                if (storeType.equals("PROD")) {
                    ftLinkPayRequest.setISTEST("USE");
                } else {
                    ftLinkPayRequest.setISTEST("TEST");
                }
                FTLinkPayResponse res = FTLinkPayApi.payRequest(ftLinkPayRequest);


                LpngCallbackResult callbackResult = new LpngCallbackResult();
                callbackResult.setErrorMsg(res.getErrMessage());
                callbackResult.setShopCode(res.getShopcode());
                callbackResult.setOrderNo(res.getOrderno());
                callbackResult.setErrorCode(res.getErrCode());
                callbackResult.setCompOrderNo(res.getComp_orderno());
                callbackResult.setCompMemNo(res.getComp_memno());
                callbackResult.setOrderGoodsname(res.getOrder_goodsname());
                callbackResult.setOrderReqAmt(res.getOrder_req_amt());
                callbackResult.setOrderName(res.getOrder_name());
                callbackResult.setOrderHp(res.getOrder_hp());
                callbackResult.setOrderEmail(res.getOrder_email());
                callbackResult.setCompTemp1(res.getComp_temp1());
                callbackResult.setCompTemp2(res.getComp_temp2());
                callbackResult.setCompTemp3(res.getComp_temp3());
                callbackResult.setCompTemp4(res.getComp_temp4());
                callbackResult.setCompTemp5(res.getComp_temp5());
                callbackResult.setReqInstallment(res.getReq_installment());
                callbackResult.setApprNo(res.getAppr_no());
                callbackResult.setApprTranNo(res.getAppr_tranNo());
                callbackResult.setApprShopCode(res.getAppr_shopCode());
                callbackResult.setApprDate(res.getAppr_date());
                callbackResult.setApprTime(res.getAppr_time());
                callbackResult.setCardtxt(res.getCardtxt());


                LpngCallback callback = new LpngCallback();
                callback.setSeqNo(null);
                callback.setMemberSeqNo(user.getNo());
                callback.setPgTranId(res.getAppr_tranNo());
                callback.setApprDate(res.getAppr_date());
                callback.setApprTime(res.getAppr_time());
                callback.setOrderId(ftLinkPayRequest.getComp_orderno());
                callback.setName(res.getOrder_name());
                callback.setPrice(Integer.parseInt(res.getOrder_req_amt()));
                callback.setPaymentData(AppUtil.ConverObjectToMap(res));
                callback.setRegDatetime(dateStr);
                callback.setLpngOrderNo(res.getOrderno());

                if (res.getErrCode().equals("0000") || res.getErrCode().equals("00")) {

                    pointBuy.setStatus(1);
                    pointBuy.setRegDatetime(dateStr);
                    pointBuy.setModDatetime(dateStr);
                    pointBuy = pointBuyRepository.saveAndFlush(pointBuy);
                    callback.setPointBuySeqNo(pointBuy.getSeqNo());
                    callbackResult = lpngCallbackResultRepository.saveAndFlush(callbackResult);

                    callback.setResultSeqNo(callbackResult.getSeqNo());

                    callback.setStatus(true);
                    callback.setProcess(LpngProcess.PAY.getType());
                    callback = lpngCallbackRepository.saveAndFlush(callback);
                } else {
                    throw new InvalidCashException();
                }
            }


            if (pointBuy.getStatus() == 1) {

                PointHistory pointHistory = new PointHistory();
                pointHistory.setPointBuySeqNo(pointBuy.getSeqNo());
                pointHistory.setMemberSeqNo(user.getNo());
                pointHistory.setType("charge");
                pointHistory.setPoint(pointBuy.getCash().floatValue());
                pointHistory.setSubject("플레이 구매적립");
                updatePoint(user.getNo(), pointHistory);

                for (int i = 0; i < count; i++) {

                    EventJoinJpa eventJoinJpa = new EventJoinJpa();
                    eventJoinJpa.setEventSeqNo(eventJpa.getSeqNo());
                    eventJoinJpa.setMemberSeqNo(user.getNo());
                    eventJoinJpa.setJoinDatetime(date);
                    eventJoinJpa.setIsBuy(true);
                    Integer maxSeqNo = eventJoinRepository.findMaxSeqNo(eventJpa.getSeqNo());
                    if (maxSeqNo == null) {
                        maxSeqNo = 0;
                    }
                    eventJoinJpa.setSeqNo(maxSeqNo + 1);
                    eventJoinRepository.save(eventJoinJpa);

                    if (eventJpa.getEarnedPoint() != null && eventJpa.getEarnedPoint() > 0) {
                        BolHistory bolHistory = new BolHistory();
                        bolHistory.setAmount(eventJpa.getEarnedPoint());
                        bolHistory.setMemberSeqNo(user.getNo());
                        bolHistory.setSubject("이벤트 참여");
                        bolHistory.setPrimaryType("increase");
                        bolHistory.setSecondaryType("joinEvent");
                        bolHistory.setTargetType("event");
                        bolHistory.setTargetSeqNo(eventJpa.getSeqNo());
                        bolHistory.setHistoryProp(new HashMap<String, Object>());
                        bolHistory.getHistoryProp().put("적립 유형", eventJpa.getTitle() + " 참여");
                        bolHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
                        bolService.increaseBol(user.getNo(), bolHistory);
                    }
                }

                eventJpa.setJoinCount(eventJpa.getJoinCount() + count);
                eventJpa = eventRepository.saveAndFlush(eventJpa);

                if (eventJpa.getWinAnnounceType().equals("immediately")) {
                    EventWin eventWin = eventJpaService.lot(user, eventJpa);
                } else if (eventJpa.getJoinCount() >= eventJpa.getMaxJoinCount()) {
                    eventJpa.setEndDatetime(dateStr);
                    String prevStatus = eventJpa.getStatus();
                    eventJpa = eventRepository.saveAndFlush(eventJpa);
                    if (!eventJpa.getPrimaryType().equals("randomluck") || eventJpa.getWinAnnounceRandomDatetime() == null) {
                        eventJpaService.lot(eventJpa);
                        eventJpa.setStatus("pending");
                        eventJpaService.updateEventWinAnnounceDateTime(eventJpa.getSeqNo());
                    }
                }
            }

            return Const.E_SUCCESS;
        } catch (Exception e) {
            logger.error(e.toString());
            throw new InvalidCashException();
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int exchangePointByBol(User user, Integer point) throws ResultCodeException {

        Member member = memberRepository.findBySeqNo(user.getNo());

        if (point <= 0) {
            throw new LackCostException("point lack");
        }

        if (point > member.getBol()) {
            throw new LackCostException("bol lack");
        }

        BolHistory bolHistory = new BolHistory();
        bolHistory.setAmount(point.floatValue());
        bolHistory.setMemberSeqNo(member.getSeqNo());
        bolHistory.setSubject("포인트 전환");
        bolHistory.setPrimaryType("decrease");
        bolHistory.setSecondaryType("exchangePoint");
        bolHistory.setTargetType("event");
        bolHistory.setTargetSeqNo(member.getSeqNo());
        bolHistory.setHistoryProp(new HashMap<String, Object>());
        bolHistory.getHistoryProp().put("사용유형", "포인트 전환");
        bolService.decreaseBol(member.getSeqNo(), bolHistory);

        PointHistory pointHistory = new PointHistory();
        pointHistory.setMemberSeqNo(member.getSeqNo());
        pointHistory.setType("charge");
        pointHistory.setPoint(point.floatValue());
        pointHistory.setSubject("포인트 전환");
        updatePoint(member.getSeqNo(), pointHistory);
        return Const.E_SUCCESS;
    }


    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public CashExchange cashExchange(User user, CashExchange cashExchange) throws ResultCodeException {

        Member member = memberRepository.findBySeqNo(user.getNo());

        CashExchangeRate cashExchangeRate = cashExchangeRateRepository.findBySeqNo(cashExchange.getCashExchangeRateSeqNo());

        if (cashExchangeRate.getPoint() > member.getPoint()) {
            throw new LackCostException();
        }


        if (cashExchange.getBankName() == null || cashExchange.getBankAccountHolderName() == null ||
                cashExchange.getBankAccountId() == null) {
            throw new InvalidArgumentException("bankInfo", "입금계좌 정보(은행명, 이름, 계좌번호)를 모두 입력하셔야 합니다.");
        }


        String dateStr = AppUtil.localDatetimeNowString();
        cashExchange.setSeqNo(null);
        cashExchange.setMemberSeqNo(member.getSeqNo());
        cashExchange.setPoint(cashExchangeRate.getPoint().longValue());
        cashExchange.setCash(cashExchangeRate.getPoint().longValue());

        cashExchange.setRefundCash(cashExchangeRate.getCash().longValue());
        cashExchange.setStatus(1);
        cashExchange.setRegDatetime(dateStr);
        cashExchange.setModDatetime(dateStr);
        cashExchange.setMemberType(member.getAppType());
        cashExchange = cashExchangeRepository.saveAndFlush(cashExchange);

        Map<String, Object> property = new HashMap<String, Object>();
        property.put("사용캐시", getMoneyType(cashExchange.getPoint().toString()) + "원");
        property.put("사용유형", "현금교환신청");
        property.put("현금교환금액", getMoneyType(cashExchange.getRefundCash().toString()) + "원");
        String bankAccountDetail = "- 예금주 : " + cashExchange.getBankAccountHolderName() + "\n";
        bankAccountDetail += "- 은행명 : " + cashExchange.getBankName() + "\n";
        bankAccountDetail += "- 계좌번호 : " + cashExchange.getBankAccountId();
        property.put("계좌정보", bankAccountDetail);

        PointHistory pointHistory = new PointHistory();
        pointHistory.setMemberSeqNo(member.getSeqNo());
        pointHistory.setType("used");
        pointHistory.setPoint(cashExchange.getPoint().floatValue());
        pointHistory.setSubject("현금교환");
        pointHistory.setHistoryProp(property);
        updatePoint(member.getSeqNo(), pointHistory);


        return cashExchange;
    }

    public String getMoneyType(String result) {

        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator(',');

        DecimalFormat df = new DecimalFormat("###,###");
        df.setDecimalFormatSymbols(dfs);

        try {

            double inputNum = Double.parseDouble(result);
            result = df.format(inputNum).toString();

        } catch (NumberFormatException e) {
            // TODO: handle exception
        }

        return result;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updatePoint(Long memberSeqNo, PointHistory pointHistory) {
        String dateStr = AppUtil.localDatetimeNowString();
        pointHistory.setRegDatetime(dateStr);
        pointHistoryRepository.save(pointHistory);
        Float point = 0f;
        if (pointHistory.getType().equals("charge")) {
            point = pointHistory.getPoint();
        } else {
            point = -pointHistory.getPoint();
        }

        memberRepository.updatePoint(memberSeqNo, point.doubleValue());
    }

    public Page<PointHistory> getPointHistoryList(Pageable pageable, Long memberSeqNo) {
        return pointHistoryRepository.findAllByMemberSeqNo(memberSeqNo, pageable);
    }

    public PointHistory gePointHistory(Long seqNo) {
        return pointHistoryRepository.findBySeqNo(seqNo);
    }

    public void givePoint(Long seqNo, Float amount, String subject) {

        PointHistory pointHistory = new PointHistory();
        pointHistory.setMemberSeqNo(seqNo);
        pointHistory.setType("charge");
        pointHistory.setPoint(amount);
        pointHistory.setSubject(subject);
        updatePoint(seqNo, pointHistory);
    }


}
