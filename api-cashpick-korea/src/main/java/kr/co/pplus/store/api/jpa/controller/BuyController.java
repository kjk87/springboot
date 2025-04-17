//package kr.co.pplus.store.api.jpa.controller;
//
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import kr.co.pplus.store.StoreApplication;
//import kr.co.pplus.store.api.annotation.GuestSessionUser;
//import kr.co.pplus.store.api.annotation.SkipSessionCheck;
//import kr.co.pplus.store.api.controller.RootController;
//import kr.co.pplus.store.api.jpa.model.*;
//import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkCancelResponse;
//import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkPayRequest;
//import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkPayResponse;
//import kr.co.pplus.store.api.jpa.model.lpng.*;
//import kr.co.pplus.store.api.jpa.model.udonge.UdongeCancelRequest;
//import kr.co.pplus.store.api.jpa.model.udonge.UdongeRequest;
//import kr.co.pplus.store.api.jpa.repository.*;
//import kr.co.pplus.store.api.jpa.service.BuyService;
//import kr.co.pplus.store.api.util.AppUtil;
//import kr.co.pplus.store.exception.InvalidBuyException;
//import kr.co.pplus.store.exception.LpngCancelPeriodException;
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.exception.SqlException;
//import kr.co.pplus.store.mvc.service.CashBolService;
//import kr.co.pplus.store.mvc.service.QueueService;
//import kr.co.pplus.store.mvc.service.UserService;
//import kr.co.pplus.store.type.Const;
//import kr.co.pplus.store.type.model.BolHistory;
//import kr.co.pplus.store.type.model.*;
//import kr.co.pplus.store.util.*;
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.http.HttpResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.transaction.annotation.Isolation;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.servlet.http.HttpServletRequest;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//@RestController
//public class BuyController extends RootController {
//
//    private static final Logger logger = LoggerFactory.getLogger(BuyController.class);
//
//    @Autowired
//    BuyRepository buyRepository;
//
//    @Autowired
//    BuyDetailRepository buyDetailRepository;
//
//    @Autowired
//    BuyDetailGuestRepository buyGuestRepository;
//
//    @Autowired
//    BuyRefDetailRepository buyRefDetailRepository;
//
//    @Autowired
//    CartRepository cartRepository;
//
//    @Autowired
//    CartDetailRepository cartDetailRepository;
//
//    @Autowired
//    BuyGoodsRepository buyGoodsRepository;
//
//    @Autowired
//    GoodsRepository goodsRepository;
//
//    @Autowired
//    GoodsRefDetailRepository goodsRefDetailRepository;
//
//    @Autowired
//    GoodsLikeRepository goodsLikeRepository;
//
//    @Autowired
//    BuyCallbackRepository buyCallbackRepository;
//
//    @Autowired
//    LpngCallbackRepository lpngCallbackRepository;
//
//    @Autowired
//    LpngCallbackResultRepository lpngCallbackResultRepository;
//
//    @Autowired
//    CashBolService cashBolSvc;
//
//    @Autowired
//    UserService userSvc;
//
//    @Autowired
//    PageRepository pageRepository;
//
//    @Autowired
//    QueueService queueSvc;
//
//    @Autowired
//    BuyService buyService;
//
//    @PersistenceContext(unitName = "store")
//    private EntityManager entityManager;
//
//
//    @Value("${STORE.REDIS_PREFIX}")
//    String REDIS_PREFIX = "pplus-";
//
//    @Value("${STORE.GOODS_BUY_TIMEOUT}")
//    Integer buyTimeout = 30; // minutes
//
//    @Value("${SYSTEM.BASE_URL}")
//    String systemBaseUrl;
//
//
//    @Value("${STORE.GOODS_BUY_EXPIRE_DAYS}")
//    public static Integer expireDays = 30; // days
//
//    @Value("${SYSTEM.BASE_URL}")
//    private String baseUrl;
//
//
//    //일단 결과 연동위해 임시생성 - 결과값들 인풋아웃풋보고 수정예정
//    @CrossOrigin
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/lpng/lpngtag")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> lpngPayTag(HttpServletRequest request, Session session, @RequestBody LpngRequestNew data) throws ResultCodeException {
//        try {
////            data.setComp_memno("" + session.getNo());
//            data.setCallback_url(baseUrl + "/store/api/buy/lpng/result");
//            LpngResponseNew res = LpngPayApi.payRequestNew(data);
//            String dateStr = AppUtil.localDatetimeNowString();
//            res.setComp_orderno(data.getComp_orderno());
//
//            Buy buy = buyRepository.findByOrderId(data.getComp_orderno());
//
//            LpngCallbackResult callbackResult = new LpngCallbackResult();
//            callbackResult.setReturnCode(res.getReturncode());
//            callbackResult.setErrorMsg(res.getErrormsg());
//            callbackResult.setShopCode(res.getShopcode());
//            callbackResult.setOrderNo(res.getOrderno());
//            callbackResult.setOrderStatus(res.getOrderstatus());
//            callbackResult.setOrderexpiredt(res.getOrderexpiredt());
//            callbackResult.setResultCode(res.getResult_code());
//            callbackResult.setErrorCode(res.getErrCode());
//            callbackResult.setCompOrderNo(res.getComp_orderno());
//            callbackResult.setCompMemNo(res.getComp_memno());
//            callbackResult.setOrderGoodsname(res.getOrder_goodsname());
//            callbackResult.setOrderReqAmt(res.getOrder_req_amt());
//            callbackResult.setOrderName(res.getOrder_name());
//            callbackResult.setOrderHp(res.getOrder_hp());
//            callbackResult.setOrderEmail(res.getOrder_email());
//            callbackResult.setCompTemp1(res.getComp_temp1());
//            callbackResult.setCompTemp2(res.getComp_temp2());
//            callbackResult.setCompTemp3(res.getComp_temp3());
//            callbackResult.setCompTemp4(res.getComp_temp4());
//            callbackResult.setCompTemp5(res.getComp_temp5());
//            callbackResult.setReqCardNo(res.getReq_cardNo());
//            callbackResult.setReqCardMonth(res.getReq_cardMonth());
//            callbackResult.setReqCardYear(res.getReq_cardYear());
//            callbackResult.setReqInstallment(res.getReq_installment());
//            callbackResult.setApprNo(res.getAppr_no());
//            callbackResult.setApprTranNo(res.getAppr_tranNo());
//            callbackResult.setApprShopCode(res.getAppr_shopCode());
//            callbackResult.setApprDate(res.getAppr_date());
//            callbackResult.setApprTime(res.getAppr_time());
//            callbackResult.setCardtxt(res.getCardtxt());
//
//
//            LpngCallback callback = new LpngCallback();
//            callback.setSeqNo(null);
//            callback.setBuySeqNo(buy.getSeqNo());
//            callback.setMemberSeqNo(session.getNo());
//            callback.setApprDate(DateUtil.getDateString("yyyyMMdd", new Date()));
//            callback.setApprTime(DateUtil.getDateString("HHmmss", new Date()));
//            callback.setOrderId(data.getComp_orderno());
//            callback.setName(data.getOrder_name());
//            callback.setPrice(Integer.parseInt(data.getOrder_req_amt()));
//            callback.setPaymentData(AppUtil.ConverObjectToMap(res));
//            callback.setRegDatetime(dateStr);
//            callback.setPgTranId(res.getOrderno());
//            callback.setLpngOrderNo(res.getOrderno());
//            callback.setProcess(LpngProcess.WAIT.getType());
//
//
//            if (res.getReturncode().equals("0000") || res.getReturncode().equals("00")) {
//
//                callback.setStatus(true);
//
//                callbackResult = lpngCallbackResultRepository.saveAndFlush(callbackResult);
//                callback.setResultSeqNo(callbackResult.getSeqNo());
//                callback = lpngCallbackRepository.saveAndFlush(callback);
//
//                res.setReq_cardNo(null);
//                res.setReq_cardMonth(null);
//                res.setReq_cardYear(null);
//                res.setReq_installment(null);
//                buy.setInstallment(data.getReq_install());
////                buy.setPgTranId(res.getOrderno());
//                buyRepository.saveAndFlush(buy);
//                return result(Const.E_SUCCESS, "row", res);
//            } else {
//
//                try {
//                    callback.setStatus(false);
//                    if (!AppUtil.isEmpty(callback.getPgTranId())) {
//                        callback = lpngCallbackRepository.saveAndFlush(callback);
//                    }
//                    int process = BuyProcess.DENIED.getProcess();
//                    buy.setProcess(process);
//                } catch (Exception e) {
//                    int process = BuyProcess.ERROR.getProcess();
//                    buy.setProcess(process);
//                }
//
//                //상품(Goods.soldCount) 원복
//                BuyRefDetail buyRefDetail = buyRefDetailRepository.findBySeqNo(buy.getSeqNo());
//                List<BuyGoods> buyGoodsList = buyRefDetail.getBuyGoodsList();
//                for (BuyGoods buyGoods : buyGoodsList) {
//                    Goods goods = goodsRepository.findBySeqNo(buyGoods.getGoodsSeqNo());
//                    if (goods != null) {
//                        goods.setSoldCount(goods.getSoldCount() - buyGoods.getCount());
//                        goods.setModDatetime(dateStr);
//                        goodsRepository.saveAndFlush(goods);
//                    }
//                }
//
//                buyRepository.delete(buy);
//
//                res.setReq_cardNo(null);
//                res.setReq_cardMonth(null);
//                res.setReq_cardYear(null);
//                res.setReq_installment(null);
//                ;
//                return result(Const.E_INVALID_BUY, "row", res);
//            }
//        } catch (Exception e) {
//            throw new InvalidBuyException("[POST]/buy/lpng/pay", e);
//        }
//    }
//
//    @CrossOrigin
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buybiz") // 주문/배달 상품 구매
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> addBuyAndOrderByBiz(HttpServletRequest request, Session session, @RequestBody Buy buy) throws ResultCodeException {
//        try {
//
//            String dateStr = AppUtil.localDatetimeNowString();
//            buy.setSeqNo(null);
//            if (!buy.isValidOrderId(REDIS_PREFIX)) {
//                throw new Exception("/buy[POST] : The orderId is not valid ");
//            }
//            buy.setPg("NFC");
//            if (buy.getPayMethod() == null) {
//                buy.setPayMethod("card");
//            }
//            buy.setRegDatetime(dateStr);
//            buy.setModDatetime(dateStr);
//
////            buy.setMemberSeqNo(session.getNo());
//            buy.setCash(false);
//            kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(buy.getPageSeqNo());
//            buy.setAgentSeqNo(page.getAgentSeqNo());
//            float point = buy.getPrice() * (page.getPoint() / 100);
//            buy.setPointRatio(page.getPoint());
//            buy.setSavedPoint((int) point);
//            buy.setCommissionRatio(page.getAgent().getPartner().getCommission());
//
//            buy.setVat(buy.calculateVat());
//            buy.setProcess(BuyProcess.WAIT.getProcess());
//
//
//            {
//                buy.setOrderProcess(OrderProcess.WAIT.getProcess());
//            }
//            buy.setType(GoodsType.MENU_GOODS.getType());
//
//
//            buy = buyRepository.saveAndFlush(buy);
//
//
//            return result(Const.E_SUCCESS, "row", buy);
//        } catch (InvalidBuyException e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw e;
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new SqlException("[POST]/buy error", "insert error");
//        }
//    }
//
//    @CrossOrigin
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/qr") // 주문/배달 상품 구매
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> addBuyQr(HttpServletRequest request, Session session, @RequestBody Buy buy) throws ResultCodeException {
//
//        return result(Const.E_SUCCESS, "row", buyService.addBuyQr(session, buy));
//    }
//
//    @CrossOrigin
//    @SkipSessionCheck
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/admin/result") // 주문/배달 상품 구매
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> adminResult(HttpServletRequest request, String type, String roomId, String orderId) throws ResultCodeException {
//        buyService.adminResult(type, "0000", roomId, orderId);
//        return result(Const.E_SUCCESS, "row", "");
//    }
//
//    @CrossOrigin
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/ftlink/pay") // 주문/배달 상품 구매
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> buyFtLinkPay(HttpServletRequest request, Session session, @RequestBody FTLinkPayRequest ftLinkPayRequest) throws ResultCodeException {
//
//        FTLinkPayResponse res = buyService.ftlinkPay(ftLinkPayRequest);
//
//        if (res.getErrCode().equals("0000") || res.getErrCode().equals("00")) {
//            return result(Const.E_SUCCESS, "row", res);
//        } else {
//            return result(Const.E_INVALID_BUY, "row", res);
//        }
//
//    }
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/callbackByBuySeqNo")
//    // lpngcallback 데이터
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> getLpngCallbackByBuySeqNo(HttpServletRequest request, Session session, @RequestParam(value = "buySeqNo", required = true) Long buySeqNo) throws ResultCodeException {
//
//        LpngCallback callback = buyService.getLpngCallbackByBuySeqNo(buySeqNo);
//        return result(Const.E_SUCCESS, "row", callback);
//
//    }
//
//    @CrossOrigin
//    @SkipSessionCheck
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/ftlink/cancel/result")
//    public Map<String, Object> ftLinkCancelRelust(FTLinkCancelResponse data) throws ResultCodeException {
//        logger.debug(" orderNo : " + data.getOrderNo() + " errorCode : " + data.getErrCode() + " errorMessage : " + data.getErrMessage());
//        buyService.ftLinkCancelResult(data);
//        return result(Const.E_SUCCESS);
//    }
//
//    @CrossOrigin
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/lpng/pay")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> lpngPay(HttpServletRequest request, Session session, @RequestBody LpngRequest data) throws ResultCodeException {
//        try {
////            data.setComp_memno("" + session.getNo());
//
//            LpngResponse res = LpngPayApi.payRequest(data);
//            String dateStr = AppUtil.localDatetimeNowString();
//
//            Buy buy = buyRepository.findByOrderId(data.getComp_orderno());
//
//            LpngCallbackResult callbackResult = new LpngCallbackResult();
//            callbackResult.setErrorMsg(res.getErrMessage());
//            callbackResult.setShopCode(res.getShopcode());
//            callbackResult.setOrderNo(res.getOrderno());
//            callbackResult.setErrorCode(res.getErrCode());
//            callbackResult.setCompOrderNo(res.getComp_orderno());
//            callbackResult.setCompMemNo(res.getComp_memno());
//            callbackResult.setOrderGoodsname(res.getOrder_goodsname());
//            callbackResult.setOrderReqAmt(res.getOrder_req_amt());
//            callbackResult.setOrderName(res.getOrder_name());
//            callbackResult.setOrderHp(res.getOrder_hp());
//            callbackResult.setOrderEmail(res.getOrder_email());
//            callbackResult.setCompTemp1(res.getComp_temp1());
//            callbackResult.setCompTemp2(res.getComp_temp2());
//            callbackResult.setCompTemp3(res.getComp_temp3());
//            callbackResult.setCompTemp4(res.getComp_temp4());
//            callbackResult.setCompTemp5(res.getComp_temp5());
//            callbackResult.setReqCardNo(res.getReq_cardNo());
//            callbackResult.setReqCardMonth(res.getReq_cardMonth());
//            callbackResult.setReqCardYear(res.getReq_cardYear());
//            callbackResult.setReqInstallment(res.getReq_installment());
//            callbackResult.setApprNo(res.getAppr_no());
//            callbackResult.setApprTranNo(res.getAppr_tranNo());
//            callbackResult.setApprShopCode(res.getAppr_shopCode());
//            callbackResult.setApprDate(res.getAppr_date());
//            callbackResult.setApprTime(res.getAppr_time());
//            callbackResult.setCardtxt(res.getCardtxt());
//
//            callbackResult = lpngCallbackResultRepository.saveAndFlush(callbackResult);
//
//            LpngCallback callback = new LpngCallback();
//            callback.setSeqNo(null);
//            callback.setBuySeqNo(buy.getSeqNo());
//            callback.setMemberSeqNo(session.getNo());
//            callback.setPgTranId(res.getAppr_tranNo());
//            callback.setApprDate(res.getAppr_date());
//            callback.setApprTime(res.getAppr_time());
//            callback.setOrderId(data.getComp_orderno());
//            callback.setName(res.getOrder_name());
//            callback.setPrice(Integer.parseInt(res.getOrder_req_amt()));
//            callback.setPaymentData(AppUtil.ConverObjectToMap(res));
//            callback.setRegDatetime(dateStr);
//            callback.setLpngOrderNo(res.getOrderno());
//            callback.setResultSeqNo(callbackResult.getSeqNo());
//
//            if (res.getErrCode().equals("0000") || res.getErrCode().equals("00")) {
//
//
//                callback.setStatus(true);
//                callback.setProcess(LpngProcess.PAY.getType());
//                callback = lpngCallbackRepository.saveAndFlush(callback);
//
//                int process = BuyProcess.PAY.getProcess(); // 결제 승인
//
//                BuyRefDetail buyRefDetail = buyRefDetailRepository.findBySeqNo(buy.getSeqNo());
//                List<BuyGoods> buyGoodsList = buyRefDetail.getBuyGoodsList();
//
//                if (buyGoodsList == null) {
//                    throw new Exception("NOK: buyGoods list is not found");
//                }
//
//                //String expireDateStr = AppUtil.localTodayString(expireDays) ;
//                int i = 0;
//                for (BuyGoods buyGoods : buyGoodsList) {
//
////                    String expireDateStr = null;
////                    Goods goods = goodsRepository.findBySeqNo(buyGoods.getGoodsSeqNo());
////                    if (goods.getExpireDay() != null)
////                        expireDateStr = AppUtil.localTodayString(goods.getExpireDay());
//                /*
//                else if( goods.getExpireDatetime() != null )
//                    expireDateStr = AppUtil.utcFromZoneTimeString("Asia/Seoul", goods.getExpireDatetime()) ;
//                    */
//
//                    if (buyGoods.getProcess() < process) {
//                        buyGoods.setProcess(process);
//                        buyGoods.setModDatetime(dateStr);
//                        buyGoods.setPayDatetime(dateStr);
////                        buyGoods.setExpireDatetime(expireDateStr);
//                        buyGoods = buyGoodsRepository.saveAndFlush(buyGoods);
//                    }
//
//                    if (buyGoods == null)
//                        throw new Exception("NOK : buyGoods update error ");
//                    buyGoodsList.set(i, buyGoods);
//                    i++;
//                }
//
//                buy.setProcess(process);
//                buy.setModDatetime(dateStr);
//                buy.setPgTranId(res.getAppr_tranNo());
//                buy.setPayMethod("card");
//                buy = buyRepository.saveAndFlush(buy);
//                if (buy == null)
//                    throw new Exception("NOK : buy update error ");
//
//                res.setReq_cardNo(null);
//                res.setReq_cardMonth(null);
//                res.setReq_cardYear(null);
//                res.setReq_installment(null);
//
//                if (buy.getMemberSeqNo() != null) {
//                    User user = userSvc.getUser(buy.getMemberSeqNo());
//                    kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(buy.getPageSeqNo());
//                    User pageUser = userSvc.getUser(page.getMemberSeqNo());
//
//                    UdongeRequest udongeRequest = new UdongeRequest();
//                    udongeRequest.setGubun("AP");
//                    udongeRequest.setUserid(user.getLoginId());
//                    udongeRequest.setGa_userid(pageUser.getLoginId().replace(pageUser.getAppType() + "##", ""));
//                    udongeRequest.setAmount(res.getOrder_req_amt());
//                    udongeRequest.setOrd_num(res.getOrderno());
//                    udongeRequest.setTr_num(res.getAppr_tranNo());
//                    udongeRequest.setTrdate(res.getAppr_date() + res.getAppr_time());
//                    LpngPayApi.callUdonge(udongeRequest);
//
//                    if (buy.getType() == GoodsType.MINI_SHOP_GOODS.getType()) {
//                        MsgOnly msg = new MsgOnly();
//                        msg.setIncludeMe(false);
//                        msg.setInput(Const.MSG_INPUT_SYSTEM);
//                        msg.setStatus(Const.MSG_STATUS_READY);
//                        msg.setType(Const.MSG_TYPE_PUSH);
//                        msg.setMoveType1(Const.MOVE_TYPE_INNER);
//                        msg.setMoveType2(Const.MOVE_TYPE_BUY);
//                        msg.setMoveTarget(new NoOnlyKey(buy.getSeqNo()));
//                        msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
//                        msg.setAppType(Const.APP_TYPE_BIZ);
//                        msg.setSubject(session.getNickname() + "님이 상품을 결제하였습니다.");
//                        msg.setContents(buy.getTitle());
//
//                        queueSvc.insertMsgBox(session, msg, user, Const.APP_TYPE_BIZ);
//                    }
//                }
//
//                return result(Const.E_SUCCESS, "row", res);
//            } else {
//
//                try {
//                    callback.setStatus(false);
//                    callback.setProcess(LpngProcess.CANCEL.getType());
//                    callback.setMemo("결제 실패");
//                    if (!AppUtil.isEmpty(callback.getPgTranId())) {
//                        callback = lpngCallbackRepository.saveAndFlush(callback);
//                    }
//                    int process = BuyProcess.DENIED.getProcess();
//                    buy.setProcess(process);
//                } catch (Exception e) {
//                    int process = BuyProcess.ERROR.getProcess();
//                    buy.setProcess(process);
//                }
//
//                //상품(Goods.soldCount) 원복
//                BuyRefDetail buyRefDetail = buyRefDetailRepository.findBySeqNo(buy.getSeqNo());
//                List<BuyGoods> buyGoodsList = buyRefDetail.getBuyGoodsList();
//                for (BuyGoods buyGoods : buyGoodsList) {
//                    Goods goods = goodsRepository.findBySeqNo(buyGoods.getGoodsSeqNo());
//                    if (goods != null) {
//                        goods.setSoldCount(goods.getSoldCount() - buyGoods.getCount());
//                        goods.setModDatetime(dateStr);
//                        goodsRepository.saveAndFlush(goods);
//                    }
//                }
//
//                buyRepository.delete(buy);
//
//                res.setReq_cardNo(null);
//                res.setReq_cardMonth(null);
//                res.setReq_cardYear(null);
//                res.setReq_installment(null);
//
//                return result(Const.E_INVALID_BUY, "row", res);
//            }
//        } catch (Exception e) {
//            throw new InvalidBuyException("[POST]/buy/lpng/pay", e);
//        }
//    }
//
//    @CrossOrigin
//    @SkipSessionCheck
//    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/lpng/cancel")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> lpngPayCancel(@RequestBody Buy buy) throws ResultCodeException {
//
//        Boolean status = this.lpngCancel(buy);
//        if (status)
//            return result(Const.E_SUCCESS, "row", "성공");
//        else
//            return result(Const.E_INVALID_BUY, "row", "실패");
//    }
//
//    Boolean lpngCancel(Buy buy) throws ResultCodeException {
//        try {
//
//
//            LpngCallback callback = lpngCallbackRepository.findByOrderId(buy.getOrderId());
//
//            String apprDate = callback.getApprDate();
//            String today = AppUtil.localTodayYYYYMMDD();
//            String dateStr = AppUtil.localDatetimeNowString();
//            if (!apprDate.equals(today)) {
//                throw new LpngCancelPeriodException("LpngCancel", "오늘 결제한 상품이 아닙니다. 상점주에게 직접 문의해 주세요 !!!");
//            }
//            LpngCancelRequest data = new LpngCancelRequest();
//            data.setOrderNo(callback.getPaymentData().get("orderno"));
//            data.setTranNo(callback.getPgTranId());
//            data.setCancelAmt("" + callback.getPrice());
//            data.setShopcode(callback.getPaymentData().get("shopcode"));
//            LpngCancelResponse res = LpngPayApi.cancelRequest(data);
//
//            if (res.getErrCode().equals("00") || res.getErrCode().equals("0000")) {
//                callback.setStatus(false);
//
//                LpngCallbackResult callbackResult = callback.getResult();
//
//                if (callbackResult != null) {
//                    callbackResult.setErrorCode(res.getErrCode());
//                    callbackResult.setErrorMsg(res.getErrMessage());
//                    lpngCallbackResultRepository.save(callbackResult);
//                }
//
////                Map<String, String> map = callback.getPaymentData();
////                map.put("errCode", "-1");
////                map.put("errMessage", "결제를 취소하였습니다.");
////                callback.setPaymentData(map);
//
//                callback.setProcess(LpngProcess.CANCEL.getType());
//                callback = lpngCallbackRepository.saveAndFlush(callback);
//
//                buy = buyRepository.findByOrderId(buy.getOrderId());
//                buy.setProcess(BuyProcess.USER_CANCEL.getProcess());
//                buy.setCancelDatetime(dateStr);
//                buy.setPgTranId(callback.getPgTranId());
//                buy.setModDatetime(dateStr);
//                buy = buyRepository.saveAndFlush(buy);
//                buyGoodsRepository.updateCancelByBuySeqNo(BuyProcess.USER_CANCEL.getProcess(), buy.getSeqNo(), dateStr);
//
//                try {
//
//                    kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(buy.getPageSeqNo());
//                    if (buy.getMemberSeqNo() != null) {
//                        User user = userSvc.getUser(buy.getMemberSeqNo());
//                        User pageUser = userSvc.getUser(page.getMemberSeqNo());
//
//                        UdongeCancelRequest udongeRequest = new UdongeCancelRequest();
//                        udongeRequest.setGubun("CP");
//                        udongeRequest.setUserid(user.getLoginId());
//                        udongeRequest.setGa_userid(pageUser.getLoginId());
//                        udongeRequest.setAmount(buy.getPrice().toString());
//                        udongeRequest.setOrd_num(callbackResult.getOrderNo());
//                        udongeRequest.setTr_num(callbackResult.getApprTranNo());
//                        udongeRequest.setTrdate(callbackResult.getApprDate() + callbackResult.getApprTime());
//                        udongeRequest.setCanceldate(dateStr);
//                        LpngPayApi.callCancelUdonge(udongeRequest);
//                    }
//
//                } catch (Exception e) {
//
//                }
//
//                return true;
//            } else {
//                callback.setStatus(true);
//                callback = lpngCallbackRepository.saveAndFlush(callback);
//                return false;
//            }
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            //throw new InvalidBuyException("[POST]/buy/lpng/cancel", e) ;
//            return false;
//        }
//    }
//
//    @CrossOrigin
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/hot")
//    // 핫딜 상품 구매 : type = 0 and 핫딜, 플러스 상품
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> addBuyHot(HttpServletRequest request, Session session, @RequestBody Buy buy) throws ResultCodeException {
////        return addBuyHotOrShop(request, session, buy, "hot");
//        return result(Const.E_SUCCESS, "row", buyService.addBuyShop(session, buy));
//    }
//
//    @CrossOrigin
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/shop") // 일반 상품 구매 : type = 1
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> addBuyShop(HttpServletRequest request, Session session, @RequestBody Buy buy) throws ResultCodeException {
//
//        return result(Const.E_SUCCESS, "row", buyService.addBuyShop(session, buy));
//    }
//
//    @CrossOrigin
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/ship") // 일반 상품 구매 : type = 1
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> addBuyShip(HttpServletRequest request, Session session, @RequestBody Buy buy) throws ResultCodeException {
//
//        return result(Const.E_SUCCESS, "row", buyService.addBuyShip(session, buy));
//    }
//
//    @CrossOrigin
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/cash")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> addBuyCash(HttpServletRequest request, Session session, @RequestBody Buy buy) throws ResultCodeException {
//
//        try {
//            String dateStr = AppUtil.localDatetimeNowString();
//            buy.setSeqNo(null);
//            if (!buy.isValidOrderId(REDIS_PREFIX)) {
//                throw new Exception(getUri(request) + " The orderId is not valid ");
//            }
//            if (buy.getPg() == null)
//                buy.setPg("DANAL");
//            if (buy.getPayMethod() == null) {
//                buy.setPayMethod("card");
//            }
//            buy.setTitle("현금 충전");
//            buy.setRegDatetime(dateStr);
//            buy.setModDatetime(dateStr);
//            buy.setMemberSeqNo(session.getNo());
//            //buy.setPrice(totalPrice);  // app 에서 price 값 넣어서 와야 함...
//            buy.setVat(0.0f);
//            buy.setProcess(BuyProcess.WAIT.getProcess());
//            buy.setRegDatetime(dateStr);
//            buy.setModDatetime(dateStr);
//            buy.setPageSeqNo(StoreUtil.getCommonAdminPage().getNo());
//            buy.setCash(true);
//            buy.setOrderType(null);
//            buy.setOrderProcess(null);
//            buy.setType(GoodsType.CASH_GOODS.getType());
//            buy = buyRepository.saveAndFlush(buy);
//
//            logger.debug(getUri(request), buy.toString() + "\nbuyerName : " + buy.getBuyerName());
//
//            return result(Const.E_SUCCESS, "row", buy);
//        } catch (InvalidBuyException e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw e;
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new SqlException(getUri(request), e);
//        }
//    }
//
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/bootpay/check")
//    public Map<String, Object> bootpayCheck(Session session, @RequestParam("receiptId") String receiptId) throws ResultCodeException {
//        Buy buy = null;
//        try {
//            String dateStr = AppUtil.localDatetimeNowString();
//            BootPayApi api = new BootPayApi();
//            api.getAccessToken();
//            HttpResponse res = api.verify(receiptId);
//            String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
//            JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
//            JsonObject data = jsonObject.getAsJsonObject("data");
//
//
//            if (!data.get("status").getAsString().equals("1")) {
//                throw new Exception(" pay rejected !!!");
//            }
//
//            buy = buyRepository.findByOrderId(data.get("order_id").getAsString());
//            if (buy == null) {
//                throw new Exception("NOK: buy.not.registered");
//            }
//
//            BuyCallback buyCallback = new BuyCallback();
//            buyCallback.setSeqNo(null);
//            buyCallback.setMemberSeqNo(buy.getMemberSeqNo());
//            buyCallback.setBuySeqNo(buy.getSeqNo());
//            buyCallback.setOrderId(data.get("order_id").getAsString());
//            buyCallback.setPg(data.get("pg").getAsString());
//            buyCallback.setPgName(data.get("pg_name").getAsString());
//            buyCallback.setMethod(data.get("method").getAsString());
//            buyCallback.setMethodName(data.get("method_name").getAsString());
//            buyCallback.setApplicationId(StoreApplication.bootpayAppId);
//            buyCallback.setName(data.get("name").getAsString());
//            buyCallback.setPrivateKey(StoreApplication.bootpayAppId);
//            buyCallback.setPrice(Integer.parseInt(data.get("price").getAsString()));
//            buyCallback.setPgTranId(receiptId);
//
//            JsonObject payment_data = data.get("payment_data").getAsJsonObject();
//
//            Map<String, String> paymentMap = new HashMap<String, String>();
//            paymentMap.put("card_name", payment_data.get("card_name").getAsString());
//            paymentMap.put("card_no", payment_data.get("card_no").getAsString());
//            paymentMap.put("card_auth_no", payment_data.get("card_auth_no").getAsString());
//            paymentMap.put("receipt_id", payment_data.get("receipt_id").getAsString());
//            paymentMap.put("n", payment_data.get("n").getAsString());
//            paymentMap.put("p", payment_data.get("p").getAsString());
//            paymentMap.put("pg", payment_data.get("pg").getAsString());
//            paymentMap.put("pm", payment_data.get("pm").getAsString());
//            paymentMap.put("pg_a", payment_data.get("pg_a").getAsString());
//            paymentMap.put("pm_a", payment_data.get("pm_a").getAsString());
//            paymentMap.put("o_id", payment_data.get("o_id").getAsString());
//            paymentMap.put("s", payment_data.get("s").getAsString());
//            paymentMap.put("g", payment_data.get("g").getAsString());
//            System.out.println("paymentMap : " + paymentMap.toString());
//
//            buyCallback.setPaymentData(paymentMap);
//            buyCallback.setRegDatetime(dateStr);
//            buyCallback.setStatus(data.get("status").getAsString());
//            buyCallback = buyCallbackRepository.saveAndFlush(buyCallback);
//
//            Integer process = BuyProcess.PAY.getProcess(); // 결제 승인
//
//            BuyRefDetail buyRefDetail = buyRefDetailRepository.findBySeqNo(buy.getSeqNo());
//            List<BuyGoods> buyGoodsList = buyRefDetail.getBuyGoodsList();
//
////            if (buyGoodsList == null) {
////                throw new Exception("NOK: buyGoods list is not found");
////            }
//
//            //String expireDateStr = AppUtil.localTodayString(expireDays) ;
//            for (BuyGoods buyGoods : buyGoodsList) {
//
////                String expireDateStr = null;
////                Goods goods = goodsRepository.findBySeqNo(buyGoods.getGoodsSeqNo());
////                if (goods.getExpireDay() != null) {
////                    expireDateStr = AppUtil.localTodayString(goods.getExpireDay());
////                }
//
//                if (buyGoods.getProcess() < process) {
//                    buyGoods.setProcess(process);
//                    buyGoods.setModDatetime(dateStr);
//                    buyGoods.setPayDatetime(dateStr);
////                    buyGoods.setExpireDatetime(expireDateStr);
//                    buyGoods = buyGoodsRepository.saveAndFlush(buyGoods);
//                }
//            }
//
//            buy.setProcess(process);
//            buy.setModDatetime(dateStr);
//            buy.setPgTranId(receiptId);
//            buy.setPayMethod(data.get("method").getAsString());
//            buy = buyRepository.saveAndFlush(buy);
//            if (buy == null)
//                throw new Exception("NOK : buy update error ");
//
//
//        } catch (Exception e) {
//            buy = null;
//            logger.error(AppUtil.excetionToString(e));
//        }
//        return result(Const.E_SUCCESS, "row", buy);
//    }
//
//
////    @CrossOrigin
////    @SkipSessionCheck
////    @RequestMapping(value = baseUri + "/buy/bootpay/callback", method = {RequestMethod.GET, RequestMethod.POST})
////    public String bootpayCallback(HttpServletRequest request, @RequestParam Ma
////    p<String, String> map) throws ResultCodeException {
////
////        Buy buy = null;
////        try {
////
////            logger.debug(getUri(request) + map.toString());
////
////            if (!map.get("status").equals("1")) {
////                throw new Exception(getUri(request) + " pay rejected !!!");
////            }
////
////
////            buy = buyRepository.findByOrderId((String) map.get("order_id"));
////            if (buy == null) {
////                throw new Exception("NOK: buy.not.registered");
////            }
////
////            String dateStr = AppUtil.localDatetimeNowString();
////
////            BuyCallback buyCallback = new BuyCallback();
////            buyCallback.setSeqNo(null);
////            buyCallback.setMemberSeqNo(buy.getMemberSeqNo());
////            buyCallback.setBuySeqNo(buy.getSeqNo());
////            buyCallback.setOrderId((String) map.get("order_id"));
////            buyCallback.setPg((String) map.get("pg"));
////            buyCallback.setPgName((String) map.get("pg_name"));
////            buyCallback.setMethod((String) map.get("method"));
////            buyCallback.setMethodName((String) map.get("method_name"));
////            buyCallback.setApplicationId((String) map.get("application_id"));
////            buyCallback.setName((String) map.get("name"));
////            buyCallback.setPrivateKey((String) map.get("private_key"));
////            buyCallback.setPrice(Integer.parseInt(map.get("price")));
////            buyCallback.setRetryCount(Integer.parseInt(map.get("retry_count")));
////
////            buyCallback.setPgTranId((String) map.get("payment_data[receipt_id]"));
////
////            Map<String, String> paymentMap = new HashMap<String, String>();
////            paymentMap.put("card_name", (String) map.get("payment_data[card_name]"));
////            paymentMap.put("card_no", (String) map.get("payment_data[card_no]"));
////            paymentMap.put("card_auth_no", (String) map.get("payment_data[card_auth_no]"));
////            paymentMap.put("receipt_id", (String) map.get("payment_data[receipt_id]"));
////            paymentMap.put("n", (String) map.get("payment_data[n]"));
////            paymentMap.put("p", (String) map.get("payment_data[p]"));
////            paymentMap.put("pg", (String) map.get("payment_data[pg]"));
////            paymentMap.put("pm", (String) map.get("payment_data[pm]"));
////            paymentMap.put("pg_a", (String) map.get("payment_data[pg_a]"));
////            paymentMap.put("pm_a", (String) map.get("payment_data[pm_a]"));
////            paymentMap.put("o_id", (String) map.get("payment_data[o_id]"));
////            paymentMap.put("s", (String) map.get("payment_data[s]"));
////            paymentMap.put("g", (String) map.get("payment_data[g]"));
////
////            System.out.println("paymentMap : " + paymentMap.toString());
////            buyCallback.setPaymentData(paymentMap);
////            buyCallback.setRegDatetime(dateStr);
////            buyCallback.setStatus(map.get("status"));
////
////            try {
////                buyCallback = buyCallbackRepository.saveAndFlush(buyCallback);
////            } catch (Exception e) {
////                BuyCallback tmp = buyCallbackRepository.findByOrderId(buyCallback.getOrderId());
////                buyCallback.setSeqNo(tmp.getSeqNo());
////                buyCallback = buyCallbackRepository.saveAndFlush(buyCallback);
////            }
////            logger.debug(getUri(request) + " buyCallback : " + buyCallback);
////
////            logger.debug(getUri(request) + buy.toString());
////            Long seqNo = buy.getSeqNo();
////            Integer process = BuyProcess.WAIT.getProcess();
////            if (((String) map.get("status")).equals("1")) {
////                process = BuyProcess.PAY.getProcess(); // 결제 승인
////                if (buy.getCash() && buy.getProcess() == BuyProcess.WAIT.getProcess()) {
////
////                    User user = userSvc.getUser(buy.getMemberSeqNo());
////                    CashHistory history = new CashHistory();
////                    history.setUser(user);
////                    history.setAmount(Long.parseLong(String.valueOf(Math.round(buy.getPrice()))));
////                    history.setSubject("캐쉬 충전");
////                    history.setPrimaryType("increase");
////                    history.setSecondaryType("buy");
////                    cashBolSvc.increaseCash(user, history);
////                }
////            } else {
////                process = BuyProcess.DENIED.getProcess(); //결제 승인 안됨
////            }
////
////
////            if (process == BuyProcess.DENIED.getProcess()) { //결제 승인 안됨
////
////                //상품(Goods.soldCount) 원복
////                BuyRefDetail buyRefDetail = buyRefDetailRepository.findBySeqNo(seqNo);
////                List<BuyGoods> buyGoodsList = buyRefDetail.getBuyGoodsList();
////                for (BuyGoods buyGoods : buyGoodsList) {
////                    Goods goods = goodsRepository.findBySeqNo(buyGoods.getGoodsSeqNo());
////                    if (goods != null) {
////                        goods.setSoldCount(goods.getSoldCount() - buyGoods.getCount());
////                        goods.setModDatetime(dateStr);
////                        goodsRepository.saveAndFlush(goods);
////                    }
////                }
////
////                buyRepository.delete(buy);
////                return "OK";
////            }
////
////
////            BuyRefDetail buyRefDetail = buyRefDetailRepository.findBySeqNo(seqNo);
////            List<BuyGoods> buyGoodsList = buyRefDetail.getBuyGoodsList();
////
////            if (buyGoodsList == null) {
////                throw new Exception("NOK: buyGoods list is not found");
////            }
////
////            //String expireDateStr = AppUtil.localTodayString(expireDays) ;
////            int i = 0;
////            for (BuyGoods buyGoods : buyGoodsList) {
////
////                String expireDateStr = null;
////                Goods goods = goodsRepository.findBySeqNo(buyGoods.getGoodsSeqNo());
////                if (goods.getExpireDay() != null)
////                    expireDateStr = AppUtil.localTodayString(goods.getExpireDay());
////                /*
////                else if( goods.getExpireDatetime() != null )
////                    expireDateStr = AppUtil.utcFromZoneTimeString("Asia/Seoul", goods.getExpireDatetime()) ;
////                    */
////
////                if (buyGoods.getProcess() < process) {
////                    buyGoods.setProcess(process);
////                    buyGoods.setModDatetime(dateStr);
////                    buyGoods.setPayDatetime(dateStr);
////                    buyGoods.setExpireDatetime(expireDateStr);
////                    buyGoods = buyGoodsRepository.saveAndFlush(buyGoods);
////                }
////
////                if (buyGoods == null)
////                    throw new Exception("NOK : buyGoods update error ");
////                buyGoodsList.set(i, buyGoods);
////                i++;
////            }
////
////            buy.setProcess(process);
////            buy.setModDatetime(dateStr);
////            buy.setPgTranId((String) map.get("receipt_id"));
////            buy.setPayMethod((String) map.get("method"));
////            buy = buyRepository.saveAndFlush(buy);
////            if (buy == null)
////                throw new Exception("NOK : buy update error ");
////
////            buyRefDetail.setProcess(process);
////            buyRefDetail.setBuyGoodsList(buyGoodsList);
////            logger.debug(getUri(request) + " OK : " + buyRefDetail.toString());
////            return "OK";
////        } catch (Exception e) {
////            logger.error(AppUtil.excetionToString(e));
////
////            /*
////            try{
////
////                bootPayCancel(map, "PRNumber : Server Logic Error") ;
////                String dateStr = AppUtil.localDatetimeNowString() ;
////                buy.setModDatetime(dateStr);
////                buy.setProcess(BuyProcess.ERROR.getProcess()) ; //서버 에러...
////                buy = buyRepository.saveAndFlush(buy) ;
////                logger.debug("/buy/bootpay/callback OK but Server Error : -1 : " + buy.toString()) ;
////            }catch(Exception ee){
////                logger.error(AppUtil.excetionToString(ee));
////                System.out.println(AppUtil.excetionToString(ee)) ;
////            }
////            */
////
////            return "OK";
////        }
////    }
//
//    @CrossOrigin
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy")
//    public Map<String, Object> updateBuy(Session session, @RequestBody Buy buy) throws ResultCodeException {
//        try {
//            String dateStr = AppUtil.localDatetimeNowString();
//            buy.setRegDatetime(null);
//            buy.setModDatetime(dateStr);
//            buy = buyRepository.saveAndFlush(buy);
//            return result(Const.E_SUCCESS, "row", buy);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("buy data", e);
//        }
//    }
//
////    @CrossOrigin
////    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/process")
////    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
////    public Map<String, Object> updateBuy(Session session, @RequestParam("seqNo") Long seqNo,
////                                         @RequestParam("process") Integer process) throws ResultCodeException {
////        try {
////            String dateStr = AppUtil.localDatetimeNowString();
////            BuyRefDetail buyRefDetail = buyRefDetailRepository.findBySeqNo(seqNo);
////            Buy buy = buyRepository.findBySeqNo(seqNo);
////            if (buyRefDetail.getType().intValue() == GoodsType.MENU_GOODS.getType() &&
////                    process.intValue() == BuyProcess.USE.getProcess()) {
////
////                try {
////                    Goods goods = null;
////                    if (process == BuyProcess.USE.getProcess()) {
////                        goods = new Goods();
////                        goods.setSeqNo(buyRefDetail.getBuyGoodsList().get(0).getGoodsSeqNo());
////                        goods = goodsRepository.findBySeqNo(goods.getSeqNo());
////
////                        buy.setBookDatetime(buyRefDetail.getBookDatetime());
////                        if (!isOpenTime(buyRefDetail.getPageSeqNo(), buy, goods, BuyProcess.USE.getProcess())) {
////                            return result(Const.E_STORE_IS_CLOSED, "errorMessage", "상점 영업 시간이 아닙니다.");
////                        }
////                    }
////
////                } catch (Exception e) {
////                    return result(Const.E_STORE_IS_CLOSED, "errorMessage", e.getMessage());
////                }
////
////            }
////            List<BuyGoods> buyGoodsList = buyRefDetail.getBuyGoodsList();
////
////
////            GoodsRefDetail goods = null;
////            for (BuyGoods buyGoods : buyGoodsList) {
////
////                buyGoods.setModDatetime(dateStr);
////                if (goods == null) {
////                    goods = goodsRefDetailRepository.findBySeqNo(buyGoods.getSeqNo());
////                }
////
////                if (process.intValue() == BuyProcess.PAY.getProcess()) {
////                    buyGoods.setPayDatetime(dateStr);
////                } else if (process.intValue() == BuyProcess.USER_CANCEL.getProcess() || process.intValue() == BuyProcess.BIZ_CANCEL.getProcess()) {
////                    buyGoods.setCancelDatetime(dateStr);
////                } else if (process.intValue() == BuyProcess.USE.getProcess()) {
////                    if (buyRefDetail.getType() == 0) {
////                        buyGoods.setOrderProcess(OrderProcess.WAIT.getProcess());
////                        buyGoods.setOrderDatetime(dateStr);
////                        buyRefDetail.setOrderDatetime(dateStr);
////                        buy.setOrderDatetime(dateStr);
////                    }
////                    buyGoods.setUseDatetime(dateStr);
////                } else if (process.intValue() == BuyProcess.EXPIRED.getProcess()) {
////                    buyGoods.setExpireDatetime(dateStr);
////                } else {
////                    throw new Exception("/buy/process : process parameter value is out of range");
////                }
////            }
////
////
////            try {
////                int i = 0;
////                for (BuyGoods buyGoods : buyGoodsList) {
////                    buyGoodsList.set(i, buyGoodsRepository.save(buyGoodsList.get(i)));
////                    i++;
////                }
////
////                buyGoodsRepository.flush();
////                buyRepository.saveAndFlush(buy);
////
////                //ToDo 메뉴 상품인 경우 OrderProcess =0 으로 세팅하고 psuh 메세지를 Biz 에 보내주어야 함.
//////                if( buyRefDetail.getType() == 0 && process.intValue() == BuyProcess.USE.getProcess() ) {
//////                    Buy buy = buyRepository.findBySeqNo(buyRefDetail.getSeqNo()) ;
//////                    buy.setOrderProcess(OrderProcess.WAIT.getProcess());
//////                    buy.setModDatetime(dateStr);
//////                    buy = buyRepository.saveAndFlush(buy) ;
//////                    buyRefDetail.setOrderProcess(OrderProcess.WAIT.getProcess());
//////
//////                    MsgOnly msg = new MsgOnly() ;
//////                    msg.setIncludeMe(false);
//////                    msg.setInput(Const.MSG_INPUT_SYSTEM);
//////                    msg.setStatus(Const.MSG_STATUS_READY) ;
//////                    msg.setType(Const.MSG_TYPE_PUSH);
//////                    msg.setMoveType1(Const.MOVE_TYPE_INNER);
//////                    msg.setMoveType2("buy") ;
//////                    msg.setMoveTarget(new NoOnlyKey(goods.getSeqNo()));
//////                    msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
//////                    msg.setAppType(Const.APP_TYPE_BIZ);
//////                    String imgPath = null ;
//////                    List<String> imgIdList = (List<String>)goods.getAttachments().get("images") ;
//////                    if( imgIdList != null && imgIdList.size() > 0 ) {
//////                        imgPath = systemBaseUrl + "/store/api/attachment/image?id=" + imgIdList.get(0);
//////                    }
//////                    if( imgPath != null ) {
//////                        msg.setProperties(new HashMap<String,Object>());
//////                        msg.getProperties().put("image_path", imgPath);
//////                    }
//////                    msg.setContents("구매자 전화번호 : "  + buy.getBuyerTel() + "," +
//////                            "구매자 : "  + buy.getBuyerName() + ", 구매금액 : "  + buy.getPrice().intValue());
//////
//////                    //            msg.setMoveTargetString(buy.getTitle());
//////                    msg.setSubject(session.getNickname() + ":" + "님이 구매 상품을 사용처리 하였습니다.") ;
//////
//////                    kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(buy.getPageSeqNo()) ;
//////                    User user = new User() ;
//////                    user.setNo(page.getMemberSeqNo()) ;
//////                    queueSvc.insertMsgBox(session, msg, user, Const.APP_TYPE_BIZ);
//////                }
////            } catch (Exception e) {
////                logger.error(AppUtil.excetionToString(e));
////                throw new InvalidBuyException("buy data", "save error");
////            }
////
////            buyRefDetail.setBuyGoodsList(buyGoodsList);
////
////            return result(Const.E_SUCCESS, "row", buyRefDetail);
////        } catch (Exception e) {
////            logger.error(AppUtil.excetionToString(e));
////            throw new InvalidBuyException("buy data", e);
////        }
////    }
//
//    @CrossOrigin
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/orderProcess")
//    public Map<String, Object> updateBuyOrderProcess(HttpServletRequest request, Session session, @RequestParam(value = "seqNo", required = true) Long seqNo,
//                                                     @RequestParam(value = "orderProcess", required = true) Integer process) throws ResultCodeException {
//        try {
//            String dateStr = AppUtil.localDatetimeNowString();
//            Buy buy = buyRepository.findBySeqNo(seqNo);
//            buy.setOrderProcess(process);
//            logger.debug(getUri(request) + process);
//
//            if (process.intValue() == OrderProcess.WAIT.getProcess()) {
//
////                try {
////                    Goods goods = goodsRepository.findTop1ByBuySeqNo(buy.getSeqNo());
////                    if (!isOpenTime(buy.getPageSeqNo(), buy, goods, BuyProcess.USE.getProcess())) {
////                        return result(Const.E_STORE_IS_CLOSED, "errorMessage", "상점 영업 시간이 아닙니다.");
////                    }
////                } catch (Exception e) {
////                    return result(Const.E_STORE_IS_CLOSED, "errorMessage", e.getMessage());
////                }
//                logger.debug(getUri(request) + " buySeqNo(" + seqNo + "):" + OrderProcess.WAIT.getName());
//            } else if (process.intValue() == OrderProcess.CONFIRM.getProcess()) {
//                logger.debug(getUri(request) + " buySeqNo(" + seqNo + "):" + OrderProcess.CONFIRM.getName());
//                buy.setConfirmDatetime(dateStr);
//            } else if (process.intValue() == OrderProcess.COMPLETE.getProcess()) {
//                logger.debug(getUri(request) + " buySeqNo(" + seqNo + "):" + OrderProcess.COMPLETE.getName());
//                buy.setCompleteDatetime(dateStr);
//            } else if (process.intValue() == OrderProcess.REFUND.getProcess()) {
//
//                String today = AppUtil.localTodayString();
//
//                List<BuyCallback> list = buyCallbackRepository.findAllByBuySeqNo(seqNo);
//                if (list == null) {
//                    throw new Exception("결제 확인정보(BuyCallback)를 DB 에서 찾을 수 없습니다 .");
//                }
//
//                if (buy.getPg().equals("LPNG") || buy.getPg().equals("NFC")) {
//                    Boolean success = this.lpngCancel(buy);
//                    if (!success) {
////                        buy.setModDatetime(dateStr);
////                        buy.setProcess(BuyProcess.ERROR.getProcess()); //서버 에러...
////                        buy = buyRepository.saveAndFlush(buy);
//                        return new LpngCancelPeriodException("lpngCancel Error", "결제 취소 중 오류가 발생하였습니다.");
//                    }
//                } else { // inicis 결제
//                    BuyCallback buyCallback = list.get(0);
//                    Boolean success = bootPayCancel(buyCallback, "상점주가 주문 취소");
//                    if (!success) {
//                        buy.setModDatetime(dateStr);
//                        buy.setProcess(BuyProcess.ERROR.getProcess()); //서버 에러...
//                        buy = buyRepository.saveAndFlush(buy);
//                        throw new Exception("결제 취소 중 오류가 발생하였습니다.");
//                    }
//                }
//                buy.setProcess(BuyProcess.BIZ_CANCEL.getProcess());
//                buy.setCancelDatetime(dateStr);
//
//            } else if (process.intValue() == OrderProcess.DELIVERY.getProcess()) {
//                logger.debug(getUri(request) + " buySeqNo(" + seqNo + "):" + OrderProcess.DELIVERY.getName());
//            } else {
//
//                throw new Exception("unknown order process value : " + process);
//            }
//
//
//            buy.setModDatetime(AppUtil.localDatetimeNowString());
//
//
//            BuyDetail buyDetail = buyDetailRepository.findBySeqNo(seqNo);
//            List<BuyGoodsDetail> buyGoodsList = buyDetail.getBuyGoodsList();
//            GoodsRefDetail goods = null; // 문자 보낼때 상품 이미지 추출용
//            for (BuyGoodsDetail buyGoodsDetail : buyGoodsList) {
//                if (goods == null) {
//                    goods = buyGoodsDetail.getGoods();
//                }
//                BuyGoods buyGoods = buyGoodsDetail.cloneToBuyGoods();
//                buyGoods.setOrderProcess(process);
//                if (process == OrderProcess.WAIT.getProcess()) {
//                    buyGoods.setOrderDatetime(dateStr);
//                    buy.setOrderDatetime(dateStr);
//                }
//                buyGoodsRepository.save(buyGoods);
//            }
//
//            buy = buyRepository.saveAndFlush(buy);
//
//            if (process == OrderProcess.CONFIRM.getProcess() || process == OrderProcess.REFUND.getProcess()) {
//
//                if (buy.getMemberSeqNo() != null) {
//                    MsgOnly msg = new MsgOnly();
//                    msg.setIncludeMe(false);
//                    msg.setInput(Const.MSG_INPUT_SYSTEM);
//                    msg.setStatus(Const.MSG_STATUS_READY);
//                    msg.setType(Const.MSG_TYPE_PUSH);
//                    msg.setMoveType1(Const.MOVE_TYPE_INNER);
//                    if (process == OrderProcess.CONFIRM.getProcess()) {
//                        msg.setMoveType2(Const.MOVE_TYPE_ORDER_CONFIRM);
//                    } else if (process == OrderProcess.REFUND.getProcess()) {
//                        msg.setMoveType2(Const.MOVE_TYPE_ORDER_CANCEL);
//                    }
//                    msg.setMoveTarget(new NoOnlyKey(buy.getSeqNo()));
//                    msg.setPushCase(Const.USER_PUSH_SENDPUSH);
//                    msg.setAppType(Const.APP_TYPE_USER);
//
//                    String imgPath = null;
//                    if (goods.getAttachments() != null) {
//                        List<String> imgIdList = (List<String>) goods.getAttachments().get("images");
//                        if (imgIdList != null && imgIdList.size() > 0) {
//                            imgPath = systemBaseUrl + "/store/api/attachment/image?id=" + imgIdList.get(0);
//                        }
//                    }
//
//                    if (imgPath != null) {
//
//                        msg.setProperties(new HashMap<String, Object>());
//                        msg.getProperties().put("image_path", imgPath);
//                    }
//                    msg.setContents("구매확인 : " + buy.getTitle() + ", 구매금액 : " + buy.getPrice().intValue());
//
////            msg.setMoveTargetString(buy.getTitle());
//                    kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(buy.getPageSeqNo());
//                    if (process == OrderProcess.CONFIRM.getProcess()) {
//                        msg.setSubject(page.getName() + ":" + "상점에서 주문을 확인하셨습니다.");
//                    } else if (process == OrderProcess.REFUND.getProcess()) {
//                        msg.setSubject(page.getName() + ":" + "상점에서 주문을 취소하셨습니다.");
//                    }
//
//
//                    User user = new User();
//                    user.setNo(buy.getMemberSeqNo());
//                    queueSvc.insertMsgBox(session, msg, user, Const.APP_TYPE_USER);
//                }
//
//            }
//            return result(Const.E_SUCCESS, "row", buy);
//        } catch (LpngCancelPeriodException e) {
//            logger.error(AppUtil.excetionToString(e));
//            return e;
//        } catch (InvalidBuyException e) {
//            logger.error(AppUtil.excetionToString(e));
//            return e;
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException(getUri(request), e);
//        }
//    }
//
//    /*
//    @CrossOrigin
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/buy/order/process")
//    public Map<String,Object> updateBuy(Session session, @RequestParam("seqNo") Long seqNo,
//                              @RequestParam("process") Integer process) throws ResultCodeException {
//        try {
//            String dateStr = AppUtil.localDatetimeNowString() ;
//            BuyRefDetail buyRefDetail = buyRefDetailRepository.findBySeqNo(seqNo) ;
//            List<BuyGoods> buyGoodsList = buyRefDetail.getBuyGoodsList() ;
//
//            for(BuyGoods buyGoods : buyGoodsList) {
//
//                buyGoods.setModDatetime(dateStr);
//
//                if (process.intValue() == BuyProcess.PAY.getProcess())
//                    buyGoods.setPayDatetime(dateStr);
//                else if (process.intValue() == BuyProcess.CANCEL.getProcess())
//                    buyGoods.setCancelDatetime(dateStr);
//                else if (process.intValue() == BuyProcess.USE.getProcess())
//                    buyGoods.setUseDatetime(dateStr);
//                else if (process.intValue() == BuyProcess.REFUND.getProcess())
//                    buyGoods.setRefundDatetime(dateStr);
//                else if (process.intValue() == BuyProcess.EXPIRE.getProcess())
//                    buyGoods.setExpireDatetime(dateStr);
//                else
//                    throw new Exception("/buy/process : process parameter value is out of range");
//            }
//
//            int i=0;
//            for(BuyGoods buyGoods : buyGoodsList) {
//                buyGoodsList.set(i, buyGoodsRepository.save(buyGoodsList.get(i)));
//                i++;
//            }
//
//            try {
//                buyGoodsRepository.flush() ;
//            }
//            catch(Exception e){
//                logger.error(AppUtil.excetionToString(e)) ;
//                throw new InvalidBuyException("buy data", "save error");
//            }
//
//            buyRefDetail.setBuyGoodsList(buyGoodsList);
//
//            return result(Const.E_SUCCESS, "row", buyRefDetail);
//        }
//        catch(Exception e){
//            logger.error(AppUtil.excetionToString(e)) ;
//            throw new InvalidBuyException("buy data", "save error");
//        }
//    }
//    */
//
//    @CrossOrigin
//    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy")
//    public Map<String, Object> deleteBuy(Session session, @RequestParam(value = "seqNo", required = true) Long seqNo) throws ResultCodeException {
//        try {
//            Buy buy = new Buy();
//            buy.setSeqNo(seqNo);
//            buyRepository.delete(buy);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("buy data", e);
//        }
//        return result(Const.E_SUCCESS, "row", null);
//    }
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/price")
//    public Map<String, Object> selectBuyPrice(HttpServletRequest request, Session session, Pageable pageable,
//                                              @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                              @RequestParam(value = "seqNo", required = false) Long seqNo,
//                                              @RequestParam(value = "orderId", required = false) String orderId,
//                                              @RequestParam(value = "pgTranId", required = false) String pgTranId,
//                                              @RequestParam(value = "pgAcceptId", required = false) String pgAcceptId,
//                                              @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                              @RequestParam(value = "process", required = false) Integer process,
//                                              @RequestParam(value = "type", required = false) Integer type,
//                                              @RequestParam(value = "orderType", required = false) Integer orderType,
//                                              @RequestParam(value = "orderProcess", required = false) Integer orderProcess,
//                                              @RequestParam(value = "startDuration", required = false) String startDuration,
//                                              @RequestParam(value = "endDuration", required = false) String endDuration,
//                                              @RequestParam(value = "manual", required = false) Integer manual) throws ResultCodeException {
//
//        Float price = null;
//        try {
//
//            Date startTime = null;
//            Date endTime = null;
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (startDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                    startTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (endDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                    endTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//            if (manual == null) {
//                manual = 0;
//            }
//            if (type == null) {
//                type = 0;
//            }
//            if (manual == 0) {
//                price = buyRepository.priceAllByWith(seqNo, memberSeqNo, orderId, pgTranId, pgAcceptId,
//                        pageSeqNo, process, type, orderType, orderProcess, startTime, endTime);
//            } else {
//                price = buyRepository.priceAllByWithCustom(seqNo, memberSeqNo, orderId, pgTranId, pgAcceptId,
//                        pageSeqNo, process, type, orderType, orderProcess, startTime, endTime);
//            }
//            return result(Const.E_SUCCESS, "row", price);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException(getUri(request), e);
//        }
//    }
//
//    @CrossOrigin
//    @GetMapping(value = baseUri + "/buybiz")
//    public Map<String, Object> selectBuy(HttpServletRequest request, Session session, Pageable pageable,
//                                         @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                         @RequestParam(value = "seqNo", required = false) Long seqNo,
//                                         @RequestParam(value = "type", required = false) Integer type,
//                                         @RequestParam(value = "isHotdeal", required = false) Boolean isHotdeal,
//                                         @RequestParam(value = "isPlus", required = false) Boolean isPlus,
//                                         @RequestParam(value = "orderId", required = false) String orderId,
//                                         @RequestParam(value = "pgTranId", required = false) String pgTranId,
//                                         @RequestParam(value = "pgAcceptId", required = false) String pgAcceptId,
//                                         @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                         @RequestParam(value = "process", required = false) Integer process,
//                                         @RequestParam(value = "orderType", required = false) Integer orderType,
//                                         @RequestParam(value = "orderProcess", required = false) Integer orderProcess,
//                                         @RequestParam(value = "startDuration", required = false) String startDuration,
//                                         @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        return _selectBuy(request, session, pageable, memberSeqNo, seqNo, type, isHotdeal, isPlus, orderId, pgTranId, pgAcceptId, pageSeqNo, process, orderType
//                , orderProcess, startDuration, endDuration, false);
//    }
//
//    private Map<String, Object> _selectBuy(HttpServletRequest request, Session session, Pageable pageable, Long memberSeqNo, Long seqNo, Integer type, Boolean isHotdeal, Boolean isPlus, String orderId, String pgTranId, String pgAcceptId,
//                                           Long pageSeqNo, Integer process, Integer orderType, Integer orderProcess, String startDuration, String endDuration, Boolean ori) throws ResultCodeException {
//        if (isHotdeal != null || isPlus != null) {
//            if (isHotdeal == null && isPlus == true) {
//                isHotdeal = false;
//            } else if (isPlus == null && isHotdeal == true) {
//                isPlus = false;
//            }
//        }
//
//        Page<Buy> page = null;
//        try {
//
//            if (seqNo != null) {
//                Buy buy = buyRepository.findBySeqNo(seqNo);
//                return result(Const.E_SUCCESS, "row", buy);
//            } else if (orderId != null) {
//                Buy buy = buyRepository.findByOrderId(orderId);
//                return result(Const.E_SUCCESS, "row", buy);
//            } else if (pgTranId != null) {
//                Buy buy = buyRepository.findByPgTranId(pgTranId);
//                return result(Const.E_SUCCESS, "row", buy);
//            } else if (pgAcceptId != null) {
//                Buy buy = buyRepository.findByPgAcceptId(pgAcceptId);
//                return result(Const.E_SUCCESS, "row", buy);
//            } else {
//                Date startTime = null;
//                Date endTime = null;
//                try {
//                    //ToDo : 나라별 시간대 Zone 추가 필요...
//                    if (startDuration != null) {
//                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                        ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                        startTime = Date.from(zdt.toInstant());
//                    }
//                } catch (Exception e) {
//
//                }
//
//                try {
//                    //ToDo : 나라별 시간대 Zone 추가 필요...
//                    if (endDuration != null) {
//                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                        ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                        endTime = Date.from(zdt.toInstant());
//                    }
//                } catch (Exception e) {
//
//                }
//
//
//                Map<String, String> sortMap = new HashMap<String, String>();
//                pageable = this.nativePageable(request, pageable, sortMap);
//                if (ori) {
//                    page = buyRepository.findAllByWith(seqNo, memberSeqNo, type, isHotdeal, isPlus,
//                            orderId, pgTranId, pgAcceptId,
//                            pageSeqNo, process, orderType, orderProcess, startTime, endTime, pageable);
//                } else {
//                    page = buyRepository.findAllByWithCustom(seqNo, memberSeqNo, type, isHotdeal, isPlus,
//                            orderId, pgTranId, pgAcceptId,
//                            pageSeqNo, process, orderType, orderProcess, startTime, endTime, pageable);
//                }
//                return result(Const.E_SUCCESS, "row", page);
//            }
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("select.buy", e);
//        }
//    }
//
//    @CrossOrigin
//    @GetMapping(value = baseUri + "/buy")
//    public Map<String, Object> selectBuy(Session session, Pageable pageable, HttpServletRequest request,
//                                         @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                         @RequestParam(value = "seqNo", required = false) Long seqNo,
//                                         @RequestParam(value = "type", required = false) Integer type,
//                                         @RequestParam(value = "isHotdeal", required = false) Boolean isHotdeal,
//                                         @RequestParam(value = "isPlus", required = false) Boolean isPlus,
//                                         @RequestParam(value = "orderId", required = false) String orderId,
//                                         @RequestParam(value = "pgTranId", required = false) String pgTranId,
//                                         @RequestParam(value = "pgAcceptId", required = false) String pgAcceptId,
//                                         @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                         @RequestParam(value = "process", required = false) Integer process,
//                                         @RequestParam(value = "orderType", required = false) Integer orderType,
//                                         @RequestParam(value = "orderProcess", required = false) Integer orderProcess,
//                                         @RequestParam(value = "startDuration", required = false) String startDuration,
//                                         @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        return _selectBuy(request, session, pageable, memberSeqNo, seqNo, type, isHotdeal, isPlus, orderId, pgTranId, pgAcceptId, pageSeqNo, process, orderType, orderProcess, startDuration, endDuration, true);
//    }
//
//    @CrossOrigin
//    @GetMapping(value = baseUri + "/buy/countAll")
//    public Map<String, Object> selectBuyDetail(Session session) {
//        return result(Const.E_SUCCESS, "row", buyDetailRepository.countAllByMemberSeqNo(session.getNo()));
//    }
//
//    @CrossOrigin
//    @GetMapping(value = baseUri + "/buy/detail")
//    public Map<String, Object> selectBuyDetail(Session session, Pageable pageable, HttpServletRequest request,
//                                               @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                               @RequestParam(value = "seqNo", required = false) Long seqNo,
//                                               @RequestParam(value = "type", required = false) Integer type,
//                                               @RequestParam(value = "isHotdeal", required = false) Boolean isHotdeal,
//                                               @RequestParam(value = "isPlus", required = false) Boolean isPlus,
//                                               @RequestParam(value = "orderId", required = false) String orderId,
//                                               @RequestParam(value = "pgTranId", required = false) String pgTranId,
//                                               @RequestParam(value = "pgAcceptId", required = false) String pgAcceptId,
//                                               @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                               @RequestParam(value = "process", required = false) Integer process,
//                                               @RequestParam(value = "orderType", required = false) Integer orderType,
//                                               @RequestParam(value = "orderProcess", required = false) Integer orderProcess,
//                                               @RequestParam(value = "startDuration", required = false) String startDuration,
//                                               @RequestParam(value = "endDuration", required = false) String endDuration,
//                                               @RequestParam(value = "sort", required = false) String sort,
//                                               @RequestParam(value = "exclude", required = false) Integer exclude,
//                                               @RequestParam(value = "price", required = false) Integer price,
//                                               @RequestParam(value = "payType", required = false) String payType) throws ResultCodeException {
//
////        if (isHotdeal != null || isPlus != null) {
////            if (isHotdeal == null && isPlus == true) {
////                isHotdeal = false;
////            } else if (isPlus == null && isHotdeal == true) {
////                isPlus = false;
////            }
////        }
//
//        Page<BuyDetail> page = null;
//        try {
//
//            if (sort != null) {
//                sort = sort.replaceAll(",asc", " asc")
//                        .replaceAll(",ASC", " ASC")
//                        .replaceAll(",desc", " desc")
//                        .replaceAll(",DESC", " DESC");
//            }
//
//            if (seqNo != null) {
//                BuyDetail buyDetail = buyDetailRepository.findBySeqNo(seqNo);
//                return result(Const.E_SUCCESS, "row", buyDetail);
//            } else if (orderId != null) {
//                BuyDetail buyDetail = buyDetailRepository.findByOrderId(orderId);
//                return result(Const.E_SUCCESS, "row", buyDetail);
//            } else if (pgTranId != null) {
//                BuyDetail buyDetail = buyDetailRepository.findByPgTranId(pgTranId);
//                return result(Const.E_SUCCESS, "row", buyDetail);
//            } else if (pgAcceptId != null) {
//                BuyDetail buyDetail = buyDetailRepository.findByPgAcceptId(pgAcceptId);
//                return result(Const.E_SUCCESS, "row", buyDetail);
//            } else {
//                Date startTime = null;
//                Date endTime = null;
//                try {
//                    //ToDo : 나라별 시간대 Zone 추가 필요...
//                    if (startDuration != null) {
//                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                        ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                        startTime = Date.from(zdt.toInstant());
//                    }
//                } catch (Exception e) {
//
//                }
//
//                try {
//                    //ToDo : 나라별 시간대 Zone 추가 필요...
//                    if (endDuration != null) {
//                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                        ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                        endTime = Date.from(zdt.toInstant());
//                    }
//                } catch (Exception e) {
//
//                }
//
//                Map<String, String> sortMap = new HashMap<String, String>();
//                if (request.getParameter("sort") == null) {
//                    sortMap.put("#SORT#", "seq_no");
//                }
//
//                if (price == null) {
//                    price = 0;
//                }
//
//                if (price == 0) {
//                    pageable = this.nativePageable(request, pageable, sortMap);
//                    page = buyDetailRepository.findAllOrderByWith(seqNo, memberSeqNo, type, isHotdeal,
//                            isPlus, orderId, pgTranId, pgAcceptId,
//                            pageSeqNo, process, orderType, orderProcess, startTime, endTime, exclude, payType, pageable);
//                } else {
//                    Float money = buyDetailRepository.findAllOrderByWithPrice(seqNo, memberSeqNo, type, isHotdeal,
//                            isPlus, orderId, pgTranId, pgAcceptId,
//                            pageSeqNo, process, orderType, orderProcess, startTime, endTime, exclude, payType);
//                    return result(Const.E_SUCCESS, "row", money);
//                }
//                return result(Const.E_SUCCESS, "row", page);
//            }
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("select.buy", e);
//        }
//    }
//
//    @GuestSessionUser
//    @CrossOrigin
//    @GetMapping(value = baseUri + "/buy/detail/guest")
//    public Map<String, Object> selectBuyDetailGuest(Session session, Pageable pageable, HttpServletRequest request,
//                                                    @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                                    @RequestParam(value = "seqNo", required = false) Long seqNo,
//                                                    @RequestParam(value = "type", required = false) Integer type,
//                                                    @RequestParam(value = "isHotdeal", required = false) Boolean isHotdeal,
//                                                    @RequestParam(value = "isPlus", required = false) Boolean isPlus,
//                                                    @RequestParam(value = "orderId", required = false) String orderId,
//                                                    @RequestParam(value = "pgTranId", required = false) String pgTranId,
//                                                    @RequestParam(value = "pgAcceptId", required = false) String pgAcceptId,
//                                                    @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                                    @RequestParam(value = "process", required = false) Integer process,
//                                                    @RequestParam(value = "orderType", required = false) Integer orderType,
//                                                    @RequestParam(value = "orderProcess", required = false) Integer orderProcess,
//                                                    @RequestParam(value = "startDuration", required = false) String startDuration,
//                                                    @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        if (isHotdeal != null || isPlus != null) {
//            if (isHotdeal == null && isPlus == true) {
//                isHotdeal = false;
//            } else if (isPlus == null && isHotdeal == true) {
//                isPlus = false;
//            }
//        }
//
//        Page<BuyDetailGuest> page = null;
//        try {
//
//            if (seqNo != null) {
//                BuyDetailGuest buy = buyGuestRepository.findBySeqNo(seqNo);
//                return result(Const.E_SUCCESS, "row", buy);
//            } else if (orderId != null) {
//                BuyDetailGuest buy = buyGuestRepository.findByOrderId(orderId);
//                return result(Const.E_SUCCESS, "row", buy);
//            } else if (pgTranId != null) {
//                BuyDetailGuest buy = buyGuestRepository.findByPgTranId(pgTranId);
//                return result(Const.E_SUCCESS, "row", buy);
//            } else if (pgAcceptId != null) {
//                BuyDetailGuest buy = buyGuestRepository.findByPgAcceptId(pgAcceptId);
//                return result(Const.E_SUCCESS, "row", buy);
//            } else {
//                Date startTime = null;
//                Date endTime = null;
//                try {
//                    //ToDo : 나라별 시간대 Zone 추가 필요...
//                    if (startDuration != null) {
//                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                        ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                        startTime = Date.from(zdt.toInstant());
//                    }
//                } catch (Exception e) {
//
//                }
//
//                try {
//                    //ToDo : 나라별 시간대 Zone 추가 필요...
//                    if (endDuration != null) {
//                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                        ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                        endTime = Date.from(zdt.toInstant());
//                    }
//                } catch (Exception e) {
//
//                }
//
//                memberSeqNo = session.getNo();
//
//                Map<String, String> sortMap = new HashMap<String, String>();
//                if (request.getParameter("sort") == null) {
//                    sortMap.put("sort", "seq_no,desc");
//                }
//                pageable = this.nativePageable(request, pageable, sortMap);
//                page = buyGuestRepository.findAllByWith(seqNo, memberSeqNo, type, isHotdeal, isPlus, orderId, pgTranId, pgAcceptId,
//                        pageSeqNo, process, orderType, orderProcess, startTime, endTime, pageable);
//                return result(Const.E_SUCCESS, "row", page);
//            }
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("[GET]/buy/detail/guest ERROR", "select error");
//        }
//    }
//
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy")
//    public Map<String, Object> selectBuy(Session session, Pageable pageable,
//                                         @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                         @RequestParam(value = "seqNo", required = false) Long seqNo,
//                                         @RequestParam(value = "orderId", required = false) String orderId,
//                                         @RequestParam(value = "pgTranId", required = false) String pgTranId,
//                                         @RequestParam(value = "pgAcceptId", required = false) String pgAcceptId) throws ResultCodeException {
//        Page<Buy> page = null;
//        try {
//
//
//            if (seqNo != null) {
//                Buy buy = buyRepository.findBySeqNo(seqNo);
//                return result(Const.E_SUCCESS, "row", buy);
//            } else if (orderId != null) {
//
//                Buy buy = buyRepository.findByOrderId(orderId);
//                return result(Const.E_SUCCESS, "row", buy);
//            } else if (pgTranId != null) {
//                Buy buy = buyRepository.findByPgTranId(pgTranId);
//                return result(Const.E_SUCCESS, "row", buy);
//            } else if (pgAcceptId != null) {
//                Buy buy = buyRepository.findByPgAcceptId(pgAcceptId);
//                return result(Const.E_SUCCESS, "row", buy);
//            } else if (memberSeqNo != null) {
//
//                page = buyRepository.findAllByMemberSeqNo(memberSeqNo, pageable);
//            } else {
//                page = buyRepository.findAll(pageable);
//            }
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("select.buy", e);
//        }
//    }
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/count/orderProcess")
//    public Map<String, Object> countBuyOrderProcess(Session session,
//                                                    @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                                    @RequestParam(value = "seqNo", required = false) Long seqNo,
//                                                    @RequestParam(value = "orderId", required = false) String orderId,
//                                                    @RequestParam(value = "pgTranId", required = false) String pgTranId,
//                                                    @RequestParam(value = "pgAcceptId", required = false) String pgAcceptId,
//                                                    @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                                    @RequestParam(value = "type", required = false) Integer type,
//                                                    @RequestParam(value = "process", required = false) Integer process,
//                                                    @RequestParam(value = "orderType", required = false) Integer orderType,
//                                                    @RequestParam(value = "orderProcess", required = false) Integer orderProcess,
//                                                    @RequestParam(value = "startDuration", required = false) String startDuration,
//                                                    @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        try {
//            Date startTime = null;
//            Date endTime = null;
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (startDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                    startTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (endDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                    endTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//
//            List<Map<String, Object>> countList = buyRepository.countAllPerOrderProcess(seqNo, memberSeqNo, orderId, pgTranId, pgAcceptId,
//                    pageSeqNo, type, orderType, orderProcess, startTime, endTime);
//
//
//            Integer readyCount = 0;
//            Integer ingCount = 0;
//            Integer completeCount = 0;
//
//            for (int i = 0; i < countList.size(); i++) {
//                Map<String, Object> map = countList.get(i);
//                Integer op = (Integer) map.get("orderProcess");
//                java.math.BigInteger count = (java.math.BigInteger) map.get("count");
//                switch (op) {
//                    case 0:
//                        readyCount = count.intValue();
//                        break;
//                    case 1:
//                        ingCount = count.intValue();
//                        break;
//                    case 2:
//                        completeCount = count.intValue();
//                }
//            }
//
//
//            return result(Const.E_SUCCESS, "row", new BuyOrderProcessCount(readyCount, ingCount, completeCount));
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("select.buy/count/orderProcess", e);
//        }
//    }
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/count/orderType")
//    public Map<String, Object> countBuyOrderType(Session session,
//                                                 @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                                 @RequestParam(value = "seqNo", required = false) Long seqNo,
//                                                 @RequestParam(value = "orderId", required = false) String orderId,
//                                                 @RequestParam(value = "pgTranId", required = false) String pgTranId,
//                                                 @RequestParam(value = "pgAcceptId", required = false) String pgAcceptId,
//                                                 @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                                 @RequestParam(value = "type", required = false) Integer type,
//                                                 @RequestParam(value = "process", required = false) Integer process,
//                                                 @RequestParam(value = "orderType", required = false) Integer orderType,
//                                                 @RequestParam(value = "orderProcess", required = false) Integer orderProcess,
//                                                 @RequestParam(value = "startDuration", required = false) String startDuration,
//                                                 @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        try {
//            Date startTime = null;
//            Date endTime = null;
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (startDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                    startTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (endDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                    endTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//
//            Integer payCount = 0;
//            Integer wrapgCount = 0;
//            Integer deliveryCount = 0;
//
//
//            List<Map<String, Object>> countList = buyRepository.countAllPerOrderType(seqNo, memberSeqNo, orderId, pgTranId, pgAcceptId,
//                    pageSeqNo, type, process, orderType, orderProcess, startTime, endTime);
//
//
//            for (int i = 0; i < countList.size(); i++) {
//                Map<String, Object> map = countList.get(i);
//                Integer op = (Integer) map.get("orderType");
//                java.math.BigInteger count = (java.math.BigInteger) map.get("count");
//                switch (op) {
//                    case 0:
//                        payCount = count.intValue();
//                        break;
//                    case 1:
//                        wrapgCount = count.intValue();
//                        break;
//                    case 2:
//                        deliveryCount = count.intValue();
//                }
//            }
//
//
//            return result(Const.E_SUCCESS, "row", new BuyOrderTypeCount(payCount, wrapgCount, deliveryCount));
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("select.buy/count/orderProcess", e);
//        }
//    }
//
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/price/goodsType")
//    public Map<String, Object> priceBuyGoodsType(Session session, HttpServletRequest request,
//                                                 @RequestParam(value = "withCount", required = false) Boolean withCount,
//                                                 @RequestParam("pageSeqNo") Long pageSeqNo,
//                                                 @RequestParam("type") Integer type,
//                                                 @RequestParam("startDuration") String startDuration,
//                                                 @RequestParam("endDuration") String endDuration) throws ResultCodeException {
//
//        try {
//            Date startTime = null;
//            Date endTime = null;
//            logger.debug(super.getUri(request) + "withCount : " + withCount);
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (startDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                    startTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (endDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                    endTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//
//            Map<String, Object> generalMap = buyRepository.priceAllByGoodsType(pageSeqNo, type, false, false, startTime, endTime);
//
//            Map<String, Object> hotdealMap = buyRepository.priceAllByGoodsType(pageSeqNo, type, true, false, startTime, endTime);
//
//            Map<String, Object> plusMap = buyRepository.priceAllByGoodsType(pageSeqNo, type, false, true, startTime, endTime);
//
//
//            if (withCount != null && withCount) {
//                List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
//                arr.add(generalMap);
//                arr.add(hotdealMap);
//                arr.add(plusMap);
//                return result(Const.E_SUCCESS, "rows", arr);
//            } else {
//                return result(Const.E_SUCCESS, "row", new BuyGoodsTypePrice(((Double) generalMap.get("price")).floatValue(), ((Double) hotdealMap.get("price")).floatValue(), ((Double) plusMap.get("price")).floatValue()));
//            }
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("select.buy/price/goodsType", e);
//        }
//    }
//
//    /*
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/buy/price2/goodsType")
//    public Map<String,Object> price2BuyGoodsType(Session session,
//                                                @RequestParam("pageSeqNo") Long pageSeqNo,
//                                                @RequestParam("goodsPlusType") Integer goodsPlusType,
//                                                @RequestParam("type") Integer type,
//                                                @RequestParam("startDuration") String startDuration,
//                                                @RequestParam("endDuration") String endDuration)throws ResultCodeException {
//
//        try {
//            Date startTime = null ;
//            Date endTime = null ;
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if( startDuration != null ) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                    startTime = Date.from(zdt.toInstant());
//                }
//            } catch(Exception e){
//
//            }
//
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if( endDuration != null ) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                    endTime = Date.from(zdt.toInstant());
//                }
//            } catch(Exception e){
//
//            }
//
//
//
//            Float generalPrice = buyRepository.priceAllByGoodsType(pageSeqNo, type, false, false, startTime, endTime);
//
//            Float hotdealPrice = buyRepository.priceAllByGoodsType( pageSeqNo, type, true, false, startTime, endTime);
//
//            Float plusPrice = buyRepository.priceAllByGoodsType(pageSeqNo, type, false, true, startTime, endTime);
//
//            return result(Const.E_SUCCESS, "row", new BuyGoodsTypePrice(generalPrice, hotdealPrice, plusPrice));
//        }catch (Exception e){
//            logger.error(AppUtil.excetionToString(e)) ;
//            throw new InvalidBuyException("select.buy/price/goodsType", e);
//        }
//    }
//    */
//
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/totalCount")
//    public Map<String, Object> countBuyTotal(Session session,
//                                             @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                             @RequestParam(value = "seqNo", required = false) Long seqNo,
//                                             @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                             @RequestParam(value = "type", required = false) Integer type,
//                                             @RequestParam(value = "orderType", required = false) Integer orderType,
//                                             @RequestParam(value = "orderProcess", required = false) Integer orderProcess,
//                                             @RequestParam(value = "startDuration", required = false) String startDuration,
//                                             @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        Integer count = null;
//        try {
//
//            Date startTime = null;
//            Date endTime = null;
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (startDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                    startTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (endDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                    endTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//
//            if (type == null) {
//                type = 0;
//            }
//            count = buyRepository.countAllTotal(memberSeqNo, pageSeqNo, type, startTime, endTime);
//
//            return result(Const.E_SUCCESS, "row", new Count(count));
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("select.buy", e);
//        }
//    }
//
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/count")
//    public Map<String, Object> countBuy(Session session,
//                                        @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                        @RequestParam(value = "seqNo", required = false) Long seqNo,
//                                        @RequestParam(value = "orderId", required = false) String orderId,
//                                        @RequestParam(value = "pgTranId", required = false) String pgTranId,
//                                        @RequestParam(value = "pgAcceptId", required = false) String pgAcceptId,
//                                        @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                        @RequestParam(value = "process", required = false) Integer process,
//                                        @RequestParam(value = "type", required = false) Integer type,
//                                        @RequestParam(value = "orderType", required = false) Integer orderType,
//                                        @RequestParam(value = "orderProcess", required = false) Integer orderProcess,
//                                        @RequestParam(value = "startDuration", required = false) String startDuration,
//                                        @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        Integer count = null;
//        try {
//
//            Date startTime = null;
//            Date endTime = null;
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (startDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                    startTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (endDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                    endTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//
//            if (type == null) {
//                type = 0;
//            }
//            count = buyRepository.countAllByWith(seqNo, memberSeqNo, orderId, pgTranId, pgAcceptId,
//                    pageSeqNo, process, type, orderType, orderProcess, startTime, endTime);
//
//            return result(Const.E_SUCCESS, "row", new Count(count));
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("select.buy", e);
//        }
//    }
//
//    @CrossOrigin
//    @GuestSessionUser
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/orderId/guest")
//    public Map<String, Object> buyOrderIdByGuest(Session session) throws ResultCodeException {
//
//        Integer count = null;
//        try {
//
//            String orderId = "PRNumber-Guest-" + KeyGenerator.generateOrderNo() + "-" + KeyGenerator.generateKey();
//
//            //orderId Redis 캐쉬 저장
//            String key = REDIS_PREFIX + orderId;
//            RedisUtil.getInstance().putOpsHash(key, "orderId", orderId);
//            RedisUtil.getInstance().hashExpire(key, buyTimeout, TimeUnit.MINUTES);
//
//            return result(Const.E_SUCCESS, "row", orderId);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("[GET]/buy/orderId/guest", "select error");
//        }
//    }
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/orderId")
//    public Map<String, Object> buyOrderId(Session session) throws ResultCodeException {
//
//        Integer count = null;
//        try {
//
//            String orderId = StoreUtil.getRandomOrderId();
//
//            //orderId Redis 캐쉬 저장
//            String key = REDIS_PREFIX + orderId;
//            RedisUtil.getInstance().putOpsHash(key, "orderId", orderId);
//            RedisUtil.getInstance().hashExpire(key, buyTimeout, TimeUnit.MINUTES);
//
//            return result(Const.E_SUCCESS, "row", orderId);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("select.buy", e);
//        }
//    }
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/orderId/lpng")
//    public Map<String, Object> buyOrderIdOfLpng(Session session) throws ResultCodeException {
//
//        Integer count = null;
//        try {
//
//            String orderId = StoreUtil.getRandomOrderId();
//
//            //orderId Redis 캐쉬 저장
//            String key = REDIS_PREFIX + orderId;
//            RedisUtil.getInstance().putOpsHash(key, "orderId", orderId);
//            RedisUtil.getInstance().hashExpire(key, buyTimeout, TimeUnit.MINUTES);
//
//            return result(Const.E_SUCCESS, "row", orderId);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("select.buy", e);
//        }
//    }
//
//    @CrossOrigin
//    @SkipSessionCheck
//    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/lpng/canceltag")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> lpngPayCancelTag(Session session, @RequestParam(value = "orderId", required = true) String orderId) throws ResultCodeException {
//
//        Boolean status = false;
//        try {
//
//            LpngCallback callback = lpngCallbackRepository.findByOrderId(orderId);
//
//            String apprDate = callback.getApprDate();
//            String today = AppUtil.localTodayYYYYMMDD();
//
//            if (!apprDate.equals(today)) {
//                throw new LpngCancelPeriodException("LpngCancel", "오늘 결제한 상품이 아닙니다. 상점주에게 직접 문의해 주세요 !!!");
//            }
//            LpngCancelRequestNew data = new LpngCancelRequestNew();
//            data.setOrderno(callback.getPaymentData().get("orderno"));
//            data.setOrder_req_amt("" + callback.getPrice());
//            data.setShopcode(callback.getPaymentData().get("shopcode"));
//            LpngCancelResponseNew res = LpngPayApi.cancelRequestNew(data);
//
//            if (res.getReturncode().equals("00") || res.getReturncode().equals("0000")) {
//                callback.setStatus(false);
//
//                LpngCallbackResult callbackResult = callback.getResult();
//
//                if (callbackResult != null) {
//                    callbackResult.setReturnCode(res.getReturncode());
//                    callbackResult.setErrorMsg(res.getErrormsg());
//                    callbackResult.setOrderStatus(res.getOrderstatus());
//                    lpngCallbackResultRepository.save(callbackResult);
//                }
//
////                Map<String, String> map = callback.getPaymentData();
////                map.put("errCode", "-1");
////                map.put("errMessage", "결제를 취소하였습니다.");
////                callback.setPaymentData(map);
//
////                callback.setProcess(LpngProcess.CANCEL.getType());
////                callback.setMemo("상점주 취소");
////                callback = lpngCallbackRepository.saveAndFlush(callback);
//                buyRepository.deleteByOrderId(orderId);
//                lpngCallbackRepository.delete(callback);
//                lpngCallbackResultRepository.delete(callbackResult);
//                status = true;
//            } else {
//                callback.setStatus(true);
//                callback = lpngCallbackRepository.saveAndFlush(callback);
//                status = false;
//            }
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            //throw new InvalidBuyException("[POST]/buy/lpng/cancel", e) ;
//            status = false;
//        }
//        if (status)
//            return result(Const.E_SUCCESS, "row", "성공");
//        else
//            return result(Const.E_INVALID_BUY, "row", "실패");
//    }
//
//    @CrossOrigin
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/lpng/check")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> lpngPayCheck(Session session, @RequestBody LpngCheckRequest data) throws ResultCodeException {
//        try {
//
//            LpngResultResponse res = LpngPayApi.payCheck(data);
////            if (res.getReturncode().equals("00") || res.getReturncode().equals("0000")) {
////                if (res.getOrderstatus().equals("12")) {
////                    LpngCallback callback = lpngCallbackRepository.findAllByLpngOrderNo(data.getOrderno());
////                    Buy buy = buyRepository.findByOrderId(callback.getOrderId());
////
////                    if (buy == null) {
////                        return result(Const.E_NOTFOUND, "row", "buy not found");
////                    }
////
//////                    if (buy.getProcess() != BuyProcess.PAY.getProcess()) {
//////                        buy.setProcess(BuyProcess.PAY.getProcess());
//////                        buy.setModDatetime(dateStr);
//////                        buy.setCompleteDatetime(dateStr);
//////                        buy.setPgTranId(res.getTranno());
//////                        buy.setPayMethod("card");
//////                        buy.setOrderProcess(OrderProcess.COMPLETE.getProcess());
//////                        buy.setCompleteDatetime(dateStr);
//////                        if (!StringUtils.isEmpty(res.getUserno())) {
//////                            Long memberSeqNo = Long.valueOf(res.getUserno());
//////
//////                            User user = userSvc.getUser(memberSeqNo);
//////
//////                            buy.setMemberSeqNo(memberSeqNo);
//////                            if (StringUtils.isEmpty(user.getName())) {
//////                                buy.setBuyerName(user.getNickname());
//////                            } else {
//////                                buy.setBuyerName(user.getName());
//////                            }
//////                            buy.setBuyerTel(user.getMobile());
//////
//////                            callback.setMemberSeqNo(memberSeqNo);
//////
//////                            kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(buy.getPageSeqNo());
//////                            CommissionPoint commissionPoint = page.getCommissionPoint();
//////                            if (!commissionPoint.getWoodongyi() && !buy.getIsPaymentPoint()) {
//////                                Integer point = buy.getSavedPoint();
//////                                if (point != null && point > 0) {
//////
//////                                    User actor = StoreUtil.getCommonAdmin();
//////
//////                                    BolHistory history = new BolHistory();
//////                                    history.setAmount((long) point);
//////                                    history.setUser(user);
//////                                    history.setSubject("상품구매 적립");
//////                                    history.setPrimaryType("increase");
//////                                    history.setSecondaryType("buy");
//////                                    history.setTargetType("member");
//////                                    history.setTarget(user);
//////                                    history.setProperties(new HashMap<String, Object>());
//////                                    history.getProperties().put("지급처", "오리마켓 운영팀");
//////                                    cashBolSvc.increaseBol(user, history);
//////
//////                                    MsgOnly msg = new MsgOnly();
//////                                    msg.setInput("system");
//////                                    msg.setStatus("ready");
//////                                    msg.setType("push");
//////                                    msg.setMoveType1("inner");
//////                                    msg.setMoveType2("bolHistory");
//////                                    msg.setMoveTarget(history);
//////                                    msg.setSubject("포인트가 적립되었습니다.");
//////                                    msg.setContents(point + "P가 적립되었습니다.");
//////                                    msg.setAppType(Const.APP_TYPE_USER);
//////                                    queueSvc.insertMsgBox(actor, msg, user, Const.APP_TYPE_USER);
//////                                    buy.setIsPaymentPoint(true);
//////                                }
//////
//////                            }
//////                        }
//////
//////                        buy = buyRepository.saveAndFlush(buy);
//////
//////                        callback.setPgTranId(res.getTranno());
//////
//////                        LpngCallbackResult callbackResult = callback.getResult();
//////                        callbackResult.setOrderStatus(res.getOrderstatus());
//////                        callbackResult.setApprTranNo(res.getTranno());
//////                        callbackResult.setReqCardNo(res.getCardno());
//////                        callbackResult.setApprDate(res.getPaydate());
//////                        callbackResult.setApprTime(res.getPaytime());
//////                        lpngCallbackResultRepository.save(callbackResult);
//////                        lpngCallbackRepository.saveAndFlush(callback);
//////                    }
////
////                }
////            }
//            return result(Const.E_SUCCESS, "row", res);
//        } catch (Exception e) {
//            throw new InvalidBuyException("[POST]/buy/lpng/check", e);
//        }
//
//    }
//
//    @CrossOrigin
//    @SkipSessionCheck
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/getPaymentData")
//    public Map<String, Object> getPaymentData(@RequestParam(value = "seqNo", required = true) Long seqNo) throws ResultCodeException {
//
//        try {
//
//            LpngCallback callback = lpngCallbackRepository.findBySeqNo(seqNo);
//
//            if (callback != null) {
//                return result(Const.E_SUCCESS, "row", callback.getPaymentData());
//            } else {
//                return result(Const.E_NOTFOUND, "row", null);
//            }
//
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("select.LpngCallback", e);
//        }
//    }
//
//    @CrossOrigin
//    @SkipSessionCheck
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/lpng/result")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> lpngPay(HttpServletRequest request, Session session, @RequestBody LpngResultResponse data) throws ResultCodeException {
//
//
//        LpngCallback callback = lpngCallbackRepository.findAllByLpngOrderNo(data.getOrderno());
//
//
//        String dateStr = AppUtil.localDatetimeNowString();
//
//        if (callback != null) {
////            callback.setPaymentData(AppUtil.ConverObjectToMap(data));
//            if (data.getReturncode().equals("00") || data.getReturncode().equals("0000")) {
//
//                Buy buy = buyRepository.findByOrderId(callback.getOrderId());
//                if (buy == null) {
//                    return result(Const.E_NOTFOUND, "row", "buy not found");
//                }
//
//                if (data.getOrderstatus().equals("12")) {
//                    if (buy.getProcess() != BuyProcess.PAY.getProcess()) {
//                        callback.setPgTranId(data.getTranno());
//                        buy.setProcess(BuyProcess.PAY.getProcess());
//                        buy.setModDatetime(dateStr);
//                        buy.setPayDatetime(dateStr);
//                        buy.setPgTranId(data.getTranno());
//                        buy.setPayMethod("card");
//
//                        buy.setOrderProcess(OrderProcess.COMPLETE.getProcess());
//
//                        User actor = StoreUtil.getCommonAdmin();
//                        kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(buy.getPageSeqNo());
//                        if (!StringUtils.isEmpty(data.getUserno())) {
//                            Long memberSeqNo = Long.valueOf(data.getUserno());
//
//                            User user = userSvc.getUser(memberSeqNo);
//
//                            buy.setMemberSeqNo(memberSeqNo);
//                            if (StringUtils.isEmpty(user.getName())) {
//                                buy.setBuyerName(user.getNickname());
//                            } else {
//                                buy.setBuyerName(user.getName());
//                            }
//                            buy.setBuyerTel(user.getMobile());
//
//                            MsgOnly msg = new MsgOnly();
//                            msg.setInput("system");
//                            msg.setStatus("ready");
//                            msg.setType("push");
//                            msg.setMoveType1("inner");
//                            msg.setMoveType2("buyHistory");
//                            msg.setMoveTarget(new NoOnlyKey(buy.getSeqNo()));
//                            msg.setSubject("결제가 완료되었습니다.");
//                            msg.setContents("앱카드 결제가 완료되었습니다.");
//                            msg.setAppType(Const.APP_TYPE_USER);
//
//                            if (!page.getWoodongyi() && !buy.getIsPaymentPoint()) {
//                                Integer point = buy.getSavedPoint();
//                                if (point != null && point > 0) {
//
//                                    BolHistory history = new BolHistory();
//                                    history.setAmount((long) point);
//                                    history.setUser(user);
//                                    history.setSubject("상품구매 적립");
//                                    history.setPrimaryType("increase");
//                                    history.setSecondaryType("buy");
//                                    history.setTargetType("member");
//                                    history.setTarget(user);
//                                    history.setProperties(new HashMap<String, Object>());
//                                    history.getProperties().put("지급처", "오리마켓 운영팀");
//                                    cashBolSvc.increaseBol(user, history);
//
//                                    msg.setContents(point + "P가 적립되었습니다.");
//                                    buy.setIsPaymentPoint(true);
//                                }
//
//                            }
//
//                            queueSvc.insertMsgBox(actor, msg, user, Const.APP_TYPE_USER);
//                        }
//                        callback.setStatus(true);
//                        callback.setProcess(LpngProcess.PAY.getType());
//                        callback.setPgTranId(data.getTranno());
//
//                        LpngCallbackResult callbackResult = callback.getResult();
//                        callbackResult.setOrderStatus(data.getOrderstatus());
//                        callbackResult.setApprTranNo(data.getTranno());
//                        callbackResult.setReqCardNo(data.getCardno());
//                        callbackResult.setApprDate(data.getPaydate());
//                        callbackResult.setApprTime(data.getPaytime());
//                        callbackResult = lpngCallbackResultRepository.saveAndFlush(callbackResult);
//
//                        try {
//                            if (buy.getMemberSeqNo() != null) {
//                                User user = userSvc.getUser(buy.getMemberSeqNo());
//                                User pageUser = userSvc.getUser(page.getMemberSeqNo());
//
//                                UdongeRequest udongeRequest = new UdongeRequest();
//                                udongeRequest.setGubun("NP");
//                                udongeRequest.setUserid(user.getLoginId());
//                                udongeRequest.setGa_userid(pageUser.getLoginId());
//                                udongeRequest.setAmount(callback.getPrice().toString());
//                                udongeRequest.setOrd_num(callbackResult.getOrderNo());
//                                udongeRequest.setTr_num(callbackResult.getApprTranNo());
//                                udongeRequest.setTrdate(callbackResult.getApprDate() + callbackResult.getApprTime());
//                                LpngPayApi.callUdonge(udongeRequest);
//                            }
//
//                        } catch (Exception e) {
//
//                        }
//
//
//                        Map<String, String> map = callback.getPaymentData();
//                        map.put("tranno", data.getTranno());
////                        map.put("cardname", data.getCardname());
//                        map.put("cardno", data.getCardno());
//                        map.put("paydate", data.getPaydate());
//                        map.put("paytime", data.getPaytime());
//                        callback.setPaymentData(map);
//
//                        MsgOnly msg = new MsgOnly();
//                        msg.setIncludeMe(false);
//                        msg.setInput(Const.MSG_INPUT_SYSTEM);
//                        msg.setStatus(Const.MSG_STATUS_READY);
//                        msg.setType(Const.MSG_TYPE_PUSH);
//                        msg.setMoveType1(Const.MOVE_TYPE_INNER);
//                        msg.setMoveType2(Const.MOVE_TYPE_PAY_COMPLETE);
//                        msg.setMoveTarget(new NoOnlyKey(buy.getSeqNo()));
//                        msg.setMoveTargetString(buy.getOrderId());
//                        msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
//                        msg.setAppType(Const.APP_TYPE_BIZ);
//
//                        msg.setContents("앱카드결제가 완료되었습니다.");
//
//                        //            msg.setMoveTargetString(buy.getTitle());
//                        msg.setSubject("앱카드결제");
//
//                        User user = new User();
//                        user.setNo(page.getMemberSeqNo());
//
//                        queueSvc.insertMsgBox(actor, msg, user, Const.APP_TYPE_BIZ);
//                    }
//
//
//                }
////                else if (data.getOrderstatus().equals("82")) {
////                    buy.setProcess(BuyProcess.USER_CANCEL.getProcess());
////                    buy.setModDatetime(dateStr);
////
////                    callback.setStatus(false);
////                    Map<String, String> map = callback.getPaymentData();
////                    map.put("errCode", "-1");
////                    map.put("errMessage", "결제를 취소하였습니다.");
////                    callback.setPaymentData(map);
////                }
//
//                buy = buyRepository.saveAndFlush(buy);
//            }
//
//            lpngCallbackRepository.saveAndFlush(callback);
//            return result(Const.E_SUCCESS, "row", "success");
//        }
//
//        return result(Const.E_NOTFOUND, "row", "fail");
//
//    }
//
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/completePrice")
//    public Map<String, Object> priceBuyComplete(HttpServletRequest request, Session session,
//                                                @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                                @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                                @RequestParam(value = "startDuration", required = false) String startDuration,
//                                                @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        Float price = null;
//        try {
//
//            Date startTime = null;
//            Date endTime = null;
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (startDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                    startTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (endDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                    endTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//            price = buyRepository.priceCompleteQrOrUse(memberSeqNo, pageSeqNo, startTime, endTime);
//            return result(Const.E_SUCCESS, "row", price);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException(getUri(request), e);
//        }
//    }
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buy/completeCount")
//    public Map<String, Object> countCompleteQrOrUseOrShipping(HttpServletRequest request, Session session,
//                                                              @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                                              @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                                              @RequestParam(value = "startDuration", required = false) String startDuration,
//                                                              @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        try {
//
//            Date startTime = null;
//            Date endTime = null;
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (startDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                    startTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//
//            try {
//                //ToDo : 나라별 시간대 Zone 추가 필요...
//                if (endDuration != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                    ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                    endTime = Date.from(zdt.toInstant());
//                }
//            } catch (Exception e) {
//
//            }
//            return result(Const.E_SUCCESS, "row", buyRepository.countCompleteQrOrUseOrShipping(memberSeqNo, pageSeqNo, startTime, endTime));
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException(getUri(request), e);
//        }
//    }
//}
