//package kr.co.pplus.store.api.jpa.controller;
//
//import kr.co.pplus.store.api.annotation.SkipSessionCheck;
//import kr.co.pplus.store.api.controller.RootController;
//import kr.co.pplus.store.api.jpa.model.*;
//import kr.co.pplus.store.api.jpa.model.udonge.UdongeRequest;
//import kr.co.pplus.store.api.jpa.repository.*;
//import kr.co.pplus.store.api.jpa.service.BolService;
//import kr.co.pplus.store.api.jpa.service.BuyGoodsService;
//import kr.co.pplus.store.api.jpa.service.BuyService;
//import kr.co.pplus.store.api.jpa.service.GoodsService;
//import kr.co.pplus.store.api.util.AppUtil;
//import kr.co.pplus.store.api.util.Message;
//import kr.co.pplus.store.exception.InvalidBuyException;
//import kr.co.pplus.store.exception.LpngCancelPeriodException;
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.mvc.service.*;
//import kr.co.pplus.store.type.Const;
//import kr.co.pplus.store.type.model.MsgOnly;
//import kr.co.pplus.store.type.model.NoOnlyKey;
//import kr.co.pplus.store.type.model.Session;
//import kr.co.pplus.store.type.model.User;
//import kr.co.pplus.store.util.LpngPayApi;
//import kr.co.pplus.store.util.StoreUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
//@RestController
//public class BuyGoodsController extends RootController {
//
//    private static final Logger logger = LoggerFactory.getLogger(BuyGoodsController.class);
//
//    @Autowired
//    BuyGoodsRepository buyGoodsRepository;
//
//    @Autowired
//    BuyRepository buyRepository;
//
//    @Autowired
//    QueueService queueSvc;
//
//    @Autowired
//    Message message;
//
//    @Autowired
//    UserService userSvc;
//
//    @Autowired
//    CommonService commonSvc;
//
//
//    @Autowired
//    PageRepository pageRepository;
//
//    @Autowired
//    BuyRefDetailRepository buyRefDetailRepository;
//
//    @Autowired
//    GoodsRepository goodsRepository;
//
//    @Autowired
//    BuyGoodsDetailRepository buyGoodsDetailRepository;
//
//    @Autowired
//    GoodsDetailRepository goodsDetailRepository;
//
//    @Autowired
//    BuyCallbackRepository buyCallbackRepository;
//
//    @Autowired
//    CashBolService cashBolSvc;
//
//    @Autowired
//    LpngCallbackRepository lpngCallbackRepository;
//
//    @Autowired
//    LpngCallbackResultRepository lpngCallbackResultRepository;
//
//    @Autowired
//    BuyService buyService;
//
//    @Autowired
//    PlusService plusService;
//
//    @Autowired
//    CashBolService cashBolService;
//
//    @Autowired
//    BuyGoodsService buyGoodsService;
//
//    @Autowired
//    GoodsService goodsService;
//
//    @Autowired
//    BolService bolService;
//
//    @Value("${SYSTEM.BASE_URL}")
//    String systemBaseUrl;
//
//
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buyGoods")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> addBuyGoods(Session session, @RequestBody BuyGoods buyGoods) throws ResultCodeException {
//
//
//        try {
//            String dateStr = AppUtil.localDatetimeNowString();
//            buyGoods.setSeqNo(null);
//            buyGoods.setRegDatetime(dateStr);
//            buyGoods.setModDatetime(dateStr);
//            buyGoods.setProcessRollback(0);
//            buyGoods = buyGoodsRepository.saveAndFlush(buyGoods);
//            return result(Const.E_SUCCESS, "row", buyGoods);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("/buyGoods[POST]", e);
//        }
//    }
//
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buyGoods/transportNumber")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> updateTransportNumber(Session session, @RequestBody BuyGoods buyGoods) throws ResultCodeException {
//        try {
//
//            buyGoodsService.updateTransportNumber(buyGoods);
//            return result(Const.E_SUCCESS);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("/buyGoods/transportNumber", e);
//        }
//    }
//
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buyGoods/shippingComplete")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> updateTransportNumber(Session session, Long seqNo) throws ResultCodeException {
//        try {
//
//            buyGoodsService.updateDeliveryCompleteBySeqNo(seqNo);
//            return result(Const.E_SUCCESS);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("/buyGoods/shippingComplete", e);
//        }
//    }
//
//    @SkipSessionCheck
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buyGoods/buyComplete")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> updateComplete(Long seqNo) throws ResultCodeException {
//        try {
//
//            buyGoodsService.updateCompleteBySeqNo(seqNo);
//            return result(Const.E_SUCCESS);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("/buyGoods/shippingComplete", e);
//        }
//    }
//
//    @SkipSessionCheck
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buyGoods/getCountReady")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> getCountReadBuyGoods(Session session, Long pageSeqNo) throws ResultCodeException {
//        try {
//            buyGoodsService.getCountReadBuyGoods(pageSeqNo);
//            return result(Const.E_SUCCESS, "row", buyGoodsService.getCountReadBuyGoods(pageSeqNo));
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("/buyGoods/getCountReady", e);
//        }
//    }
//
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buyGoods/use/list")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> useBuyGoodsList(Session session, @RequestParam(value = "buySeqNo") Long buySeqNo) throws ResultCodeException {
//        String dateStr = AppUtil.localDatetimeNowString();
//
//        try {
//            Buy buy = buyRepository.findBySeqNo(buySeqNo);
//            buy.setModDatetime(dateStr);
//            buy.setProcess(BuyProcess.USE.getProcess());
//
//            kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(buy.getPageSeqNo());
//
//            int isPaymentPoint = 0;//buy_goods 포인트지급여부
//            if (!buy.getIsPaymentPoint()) {
//                Integer point = buy.getSavedPoint();
//                if (point != null && point > 0) {
//
//                    if (buy.getMemberSeqNo() != null) {
//                        kr.co.pplus.store.api.jpa.model.BolHistory bolHistory = new kr.co.pplus.store.api.jpa.model.BolHistory();
//                        bolHistory.setAmount(Long.valueOf(point));
//                        bolHistory.setMemberSeqNo(buy.getMemberSeqNo());
//                        bolHistory.setSubject("상품구매 적립");
//                        bolHistory.setPrimaryType("increase");
//                        bolHistory.setSecondaryType("buy");
//                        bolHistory.setTargetType("member");
//                        bolHistory.setTargetSeqNo(buy.getMemberSeqNo());
//                        bolHistory.setHistoryProp(new HashMap<String, Object>());
//                        bolHistory.getHistoryProp().put("지급처", page.getName());
//                        bolHistory.getHistoryProp().put("적립유형", "상품 사용처리(" + buy.getTitle() + ")");
//
//                        bolService.increaseBol(buy.getMemberSeqNo(), bolHistory);
//                    }
//
//                    buy.setIsPaymentPoint(true);
//                    isPaymentPoint = 1;
//                }
//
//            }
//
//            buy = buyRepository.saveAndFlush(buy);
//            buyGoodsRepository.updateUseBuyGoodsByBuySeqNo(BuyProcess.USE.getProcess(), buySeqNo, dateStr, isPaymentPoint);
//
//            kr.co.pplus.store.type.model.Plus plus = new kr.co.pplus.store.type.model.Plus();
//            plus.setPageNo(buy.getPageSeqNo());
//            plus.setBlock(false);
//            if (plusService.existsPlus(session, plus)) {
//                plusService.updateBuyCount(session, plus);
//            }
//
//            try {
//
//                if (page.getWoodongyi() && buy.getMemberSeqNo() != null) {
//                    LpngCallback callback = lpngCallbackRepository.findAllByBuySeqNo(buy.getSeqNo());
//                    LpngCallbackResult callbackResult = callback.getResult();
//
//                    User user = userSvc.getUser(buy.getMemberSeqNo());
//                    User pageUser = userSvc.getUser(page.getMemberSeqNo());
//
//                    UdongeRequest udongeRequest = new UdongeRequest();
//                    udongeRequest.setGubun("VP");
//                    udongeRequest.setUserid(user.getLoginId());
//                    udongeRequest.setGa_userid(pageUser.getLoginId());
//                    udongeRequest.setAmount(buy.getPrice().toString());
//                    udongeRequest.setOrd_num(callbackResult.getOrderNo());
//                    udongeRequest.setTr_num(callbackResult.getApprTranNo());
//                    udongeRequest.setTrdate(callbackResult.getApprDate() + callbackResult.getApprTime());
//                    LpngPayApi.callUdonge(udongeRequest);
//                }
//
//            } catch (Exception e) {
//
//            }
//
//            MsgOnly msg = new MsgOnly();
//            msg.setIncludeMe(false);
//            msg.setInput(Const.MSG_INPUT_SYSTEM);
//            msg.setStatus(Const.MSG_STATUS_READY);
//            msg.setType(Const.MSG_TYPE_PUSH);
//            msg.setMoveType1(Const.MOVE_TYPE_INNER);
//            msg.setMoveType2(Const.MOVE_TYPE_BUY_USE);
//            msg.setMoveTarget(new NoOnlyKey(buy.getSeqNo()));
//            msg.setMoveTargetString(buy.getOrderId());
//            msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
//            msg.setAppType(Const.APP_TYPE_BIZ);
//            msg.setSubject(buy.getTitle());
//            msg.setContents("상품의 사용처리가 완료 되었습니다.");
//
//            User user = new User();
//            user.setNo(page.getMemberSeqNo());
//
//            queueSvc.insertMsgBox(session, msg, user, Const.APP_TYPE_BIZ);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("/buyGoods/use/list", e);
//        }
//
//
//        return result(Const.E_SUCCESS, "row", "success");
//    }
//
//    @SkipSessionCheck
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buyGoods/cancel/list")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> cancelBuyGoodsList(@RequestParam(value = "buySeqNo", required = true) Long buySeqNo) throws ResultCodeException {
//
//        try {
//
//            String dateStr = AppUtil.localDatetimeNowString();
//
//            Buy buy = buyRepository.findBySeqNo(buySeqNo);
//
//            if (buy == null) {
//                return result(Const.E_NOTFOUND, "row", "fail");
//            }
//
//            if (buy.getPg().equals("DAOU")) {
//                Boolean success = buyService.ftlinkCancel(buy, "사용자가 주문 취소");
//
//                if (success == null) {
//                    return new LpngCancelPeriodException("LpngCancel", "오늘 결제한 상품이 아닙니다. 상점주에게 직접 문의해 주세요.");
//                }
//
//                if (!success) {
//                    return new LpngCancelPeriodException("lpngCancel Error", "결제 취소 중 오류가 발생하였습니다.");
//                } else {
//
//                    try {
//                        if (buy.getIsPaymentPoint()) {
//                            Integer point = buy.getSavedPoint();
//                            if (point != null && point > 0) {
//
//                                if (buy.getMemberSeqNo() != null) {
//                                    kr.co.pplus.store.api.jpa.model.BolHistory bolHistory = new kr.co.pplus.store.api.jpa.model.BolHistory();
//                                    bolHistory.setAmount(Long.valueOf(point));
//                                    bolHistory.setMemberSeqNo(buy.getMemberSeqNo());
//                                    bolHistory.setSubject("상품구매 취소");
//                                    bolHistory.setPrimaryType("decrease");
//                                    bolHistory.setSecondaryType("buyCancel");
//                                    bolHistory.setTargetType("member");
//                                    bolHistory.setTargetSeqNo(buy.getMemberSeqNo());
//                                    bolHistory.setHistoryProp(new HashMap<String, Object>());
//                                    bolHistory.getHistoryProp().put("사유", "구매취소에 의한 환불");
//                                    bolService.decreaseBol(buy.getMemberSeqNo(), bolHistory);
//                                }
//                            }
//                        }
//                    } catch (Exception e) {
//                        logger.error(e.toString());
//                    }
//
//
//                    buy.setCancelDatetime(dateStr);
//                    buy.setProcess(BuyProcess.USER_CANCEL.getProcess());
//                    buyRepository.saveAndFlush(buy);
//                    buyGoodsRepository.updateCancelByBuySeqNo(BuyProcess.USER_CANCEL.getProcess(), buySeqNo, dateStr);
//                    List<BuyGoods> buyGoodsList = buyGoodsRepository.findAllByBuySeqNo(buy.getSeqNo());
//                    for (BuyGoods buyGoods : buyGoodsList) {
////                        goodsService.updateGoodsMinusSoldCount(buyGoods.getGoodsSeqNo(), buyGoods.getCount());
//
//                        Goods goods = goodsService.getGoodsBySeqNo(buyGoods.getGoodsSeqNo());
//                        goods.setSoldCount(goods.getSoldCount() - buyGoods.getCount());
//                        if (goods.getStatus() == GoodsStatus.SOLD_OUT.getStatus() && goods.getSoldCount() < goods.getCount()) {
//                            goods.setStatus(GoodsStatus.SELL.getStatus());
//                            goodsService.updateGoodsPriceStatusByGoodsSeqNoAndSoldOut(goods.getSeqNo(), goods.getStatus());
//                        }
//                        goods = goodsRepository.saveAndFlush(goods);
//
//                        List<BuyGoodsOption> buyGoodsOptionList = buyGoodsService.getBuyGoodsOptionList(buyGoods.getSeqNo());
//                        if (buyGoodsOptionList != null) {
//                            for (BuyGoodsOption buyGoodsOption : buyGoodsOptionList) {
//                                goodsService.updateGoodsOptionDetailMinusSoldCount(buyGoodsOption.getGoodsOptionDetailSeqNo(), buyGoodsOption.getAmount());
//                            }
//                        }
//
//                        if (buyGoods.getOrderType() == OrderType.SHIPPING_ORDER.getType()) {
//                            MsgOnly msg = new MsgOnly();
//                            msg.setIncludeMe(false);
//                            msg.setInput(Const.MSG_INPUT_SYSTEM);
//                            msg.setStatus(Const.MSG_STATUS_READY);
//                            msg.setType(Const.MSG_TYPE_PUSH);
//                            msg.setMoveType1(Const.MOVE_TYPE_INNER);
//                            msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
//                            msg.setAppType(Const.APP_TYPE_BIZ);
//                            msg.setMoveType2(Const.MOVE_TYPE_CANCEL_SHIPPING);
//                            msg.setSubject("상품결제가 취소되었습니다.");
//                            msg.setContents(buy.getTitle());
//                            msg.setMoveTarget(new NoOnlyKey(buyGoods.getSeqNo()));
//                            User actor = StoreUtil.getCommonAdmin();
//                            User pageUser = userSvc.getUser(pageRepository.findBySeqNo(buyGoods.getPageSeqNo()).getMemberSeqNo());
//                            queueSvc.insertMsgBox(actor, msg, pageUser, Const.APP_TYPE_BIZ);
//                        }
//
//                    }
//                }
//            } else {
//                // 부트 페이 결제 취소
//                List<BuyCallback> list = buyCallbackRepository.findAllByBuySeqNo(buySeqNo);
//                if (list == null) {
//                    throw new Exception("결제 확인정보(BuyCallback)를 DB 에서 찾을 수 없습니다 .");
//                }
//                BuyCallback buyCallback = list.get(0);
//
//                Boolean success = bootPayCancel(buyCallback, "사용자가 주문 취소");
//                if (!success) {
//                    throw new Exception("결제 취소 중 오류가 발생하였습니다.");
//                } else {
//                    buy.setCancelDatetime(dateStr);
//                    buy.setProcess(BuyProcess.USER_CANCEL.getProcess());
//                    buyRepository.saveAndFlush(buy);
//                    buyGoodsRepository.updateCancelByBuySeqNo(BuyProcess.USER_CANCEL.getProcess(), buySeqNo, dateStr);
//
//                }
//            }
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("/buyGoods/cancel/list", e);
//        }
//
//
//        return result(Const.E_SUCCESS, "row", "success");
//    }
//
//    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buyGoods")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidBuyException.class)
//    public Map<String, Object> deleteBuyGoods(Session session, @RequestParam(value = "seqNo", required = false) Long seqNo,
//                                              @RequestParam(value = "buySeqNo", required = false) Long buySeqNo) throws ResultCodeException {
//        try {
//            if (seqNo != null) {
//                buyGoodsRepository.deleteBySeqNo(seqNo);
//            } else if (buySeqNo != null) {
//                buyGoodsRepository.deleteByBuySeqNo(buySeqNo);
//
//            }
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("buyGoods data", e);
//        }
//        return result(Const.E_SUCCESS, "row", null);
//    }
//
//    @GetMapping(value = baseUri + "/buyGoods")
//    public Map<String, Object> selectBuyGoods(Session session, HttpServletRequest request, Pageable pageable,
//                                              @RequestParam(value = "buySeqNo", required = false) Long buySeqNo,
//                                              @RequestParam(value = "goodsSeqNo", required = false) Long goodsSeqNo,
//                                              @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                              @RequestParam(value = "isReviewExist", required = false) Boolean isReviewExist,
//                                              @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                              @RequestParam(value = "process", required = false) Integer process,
//                                              @RequestParam(value = "orderType", required = false) Integer orderType,
//                                              @RequestParam(value = "orderProcess", required = false) Integer orderProcess,
//                                              @RequestParam(value = "startDuration", required = false) String startDuration,
//                                              @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//        Page<BuyGoods> page = null;
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
//            Map<String, String> sortMap = new HashMap<String, String>();
//            pageable = this.nativePageable(request, pageable, sortMap);
//            page = buyGoodsRepository.findAllBy(buySeqNo, memberSeqNo, isReviewExist,
//                    pageSeqNo, process, goodsSeqNo,
//                    orderType, orderProcess,
//                    startTime, endTime, pageable);
//
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("/buyGoods", e);
//        }
//    }
//
//    @GetMapping(value = baseUri + "/buyGoods/detail/onlySupplyGoodsByPageSeqNo")
//    public Map<String, Object> getBuyGoodsListByPageSeqNoOnlySupplyGoods(Session session, HttpServletRequest request, Pageable pageable,
//                                                                         @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                                                         @RequestParam(value = "process", required = false) Integer process,
//                                                                         @RequestParam(value = "startDuration", required = false) String startDuration,
//                                                                         @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        Page<BuyGoodsDetail> page = null;
//        try {
//
//            Map<String, String> sortMap = new HashMap<String, String>();
//            if (request.getParameter("sort") == null) {
//                sortMap.put("#SORT#", "seq_no");
//            }
//            pageable = this.nativePageable(request, pageable, sortMap);
//            page = buyGoodsService.getBuyGoodsListByPageSeqNoOnlySupplyGoods(pageSeqNo, startDuration, endDuration, process, pageable);
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("[GET]/buyGoods/detail", "select ERROR");
//        }
//
//    }
//
//    @GetMapping(value = baseUri + "/buyGoods/detail/supplyPageSeqNo")
//    public Map<String, Object> getBuyGoodsListBySupplyPageSeqNo(Session session, HttpServletRequest request, Pageable pageable,
//                                                                         @RequestParam(value = "supplyPageSeqNo", required = false) Long supplyPageSeqNo,
//                                                                         @RequestParam(value = "process", required = false) Integer process,
//                                                                         @RequestParam(value = "startDuration", required = false) String startDuration,
//                                                                         @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        Page<BuyGoodsDetail> page = null;
//        try {
//
//            Map<String, String> sortMap = new HashMap<String, String>();
//            if (request.getParameter("sort") == null) {
//                sortMap.put("#SORT#", "seq_no");
//            }
//            pageable = this.nativePageable(request, pageable, sortMap);
//            page = buyGoodsService.getBuyGoodsListBySupplyPageSeqNo(supplyPageSeqNo, startDuration, endDuration, process, pageable);
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("[GET]/buyGoods/detail", "select ERROR");
//        }
//
//    }
//
//    @GetMapping(value = baseUri + "/buyGoods/detail/onlySupplyGoodsByGoodsPriceSeqNo")
//    public Map<String, Object> getBuyGoodsListByGoodsPriceSeqNoOnlySupplyGoods(Session session, HttpServletRequest request, Pageable pageable,
//                                                                               @RequestParam(value = "goodsPriceSeqNo", required = false) Long goodsPriceSeqNo,
//                                                                               @RequestParam(value = "process", required = false) Integer process,
//                                                                               @RequestParam(value = "startDuration", required = false) String startDuration,
//                                                                               @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        Page<BuyGoodsDetail> page = null;
//        try {
//
//            Map<String, String> sortMap = new HashMap<String, String>();
//            if (request.getParameter("sort") == null) {
//                sortMap.put("#SORT#", "seq_no");
//            }
//            pageable = this.nativePageable(request, pageable, sortMap);
//            page = buyGoodsService.getBuyGoodsListByGoodsPriceSeqNoOnlySupplyGoods(goodsPriceSeqNo, startDuration, endDuration, process, pageable);
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("[GET]/buyGoods/detail", "select ERROR");
//        }
//
//    }
//
//    @GetMapping(value = baseUri + "/buyGoods/detail")
//    public Map<String, Object> selectBuyGoodsDetail(Session session, HttpServletRequest request, Pageable pageable,
//                                                    @RequestParam(value = "seqNo", required = false) Long seqNo,
//                                                    @RequestParam(value = "buySeqNo", required = false) Long buySeqNo,
//                                                    @RequestParam(value = "goodsSeqNo", required = false) Long goodsSeqNo,
//                                                    @RequestParam(value = "goodsPriceSeqNo", required = false) Long goodsPriceSeqNo,
//                                                    @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                                    @RequestParam(value = "type", required = false) Integer type,
//                                                    @RequestParam(value = "isHotdeal", required = false) Boolean isHotdeal,
//                                                    @RequestParam(value = "isPlus", required = false) Boolean isPlus,
//                                                    @RequestParam(value = "isReviewExist", required = false) Boolean isReviewExist,
//                                                    @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                                    @RequestParam(value = "process", required = false) Integer process,
//                                                    @RequestParam(value = "orderType", required = false) Integer orderType,
//                                                    @RequestParam(value = "orderProcess", required = false) Integer orderProcess,
//                                                    @RequestParam(value = "startDuration", required = false) String startDuration,
//                                                    @RequestParam(value = "endDuration", required = false) String endDuration,
//                                                    @RequestParam(value = "price", required = false) Integer price) throws ResultCodeException {
//
//
//        if (seqNo != null) {
//            return result(Const.E_SUCCESS, "row", buyGoodsDetailRepository.findBySeqNo(seqNo));
//        } else if (isHotdeal != null || isPlus != null) {
//            if (isHotdeal == null && isPlus == true) {
//                isHotdeal = false;
//            } else if (isPlus == null && isHotdeal == true) {
//                isPlus = false;
//            }
//        }
//
//        Page<BuyGoodsDetail> page = null;
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
//            Map<String, String> sortMap = new HashMap<String, String>();
//            if (request.getParameter("sort") == null) {
//                sortMap.put("#SORT#", "seq_no");
//            }
//            if (price == null) {
//                price = 0;
//            }
//
//            if (price == 0) {
//                pageable = this.nativePageable(request, pageable, sortMap);
//                page = buyGoodsDetailRepository.findAllByWith(seqNo, buySeqNo, memberSeqNo, type,
//                        isHotdeal, isPlus, isReviewExist,
//                        pageSeqNo, process, goodsSeqNo, goodsPriceSeqNo,
//                        orderType, orderProcess,
//                        startTime, endTime, pageable);
//            } else {
//                Float money = buyGoodsDetailRepository.findAllByWithPrice(seqNo, buySeqNo, memberSeqNo, type,
//                        isHotdeal, isPlus, isReviewExist,
//                        pageSeqNo, process, goodsSeqNo, goodsPriceSeqNo,
//                        orderType, orderProcess,
//                        startTime, endTime);
//                return result(Const.E_SUCCESS, "row", money);
//            }
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("[GET]/buyGoods/detail", "select ERROR");
//        }
//    }
//
//    @SkipSessionCheck
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buyGoods/count")
//    public Map<String, Object> countBuyGoods(Session session,
//                                             @RequestParam(value = "buySeqNo", required = false) Long buySeqNo,
//                                             @RequestParam(value = "goodsSeqNo", required = false) Long goodsSeqNo,
//                                             @RequestParam(value = "goodsPriceSeqNo", required = false) Long goodsPriceSeqNo,
//                                             @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                             @RequestParam(value = "type", required = false) Integer type,
//                                             @RequestParam(value = "isHotdeal", required = false) Boolean isHotdeal,
//                                             @RequestParam(value = "isPlus", required = false) Boolean isPlus,
//                                             @RequestParam(value = "isReviewExist", required = false) Boolean isReviewExist,
//                                             @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                             @RequestParam(value = "process", required = false) Integer process,
//                                             @RequestParam(value = "orderType", required = false) Integer orderType,
//                                             @RequestParam(value = "orderProcess", required = false) Integer orderProcess,
//                                             @RequestParam(value = "startDuration", required = false) String startDuration,
//                                             @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        if (isHotdeal != null || isPlus != null) {
//            if (isHotdeal == null && isPlus == true) {
//                isHotdeal = false;
//            } else if (isPlus == null && isHotdeal == true) {
//                isPlus = false;
//            }
//        }
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
//            count = buyGoodsRepository.countAllBy(buySeqNo, memberSeqNo, type,
//                    isHotdeal, isPlus, isReviewExist,
//                    pageSeqNo, process, goodsSeqNo, goodsPriceSeqNo,
//                    orderType, orderProcess,
//                    startTime, endTime);
//
//            return result(Const.E_SUCCESS, "row", new Count(count));
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("[GET]/buyGoods/count", "select count ERROR");
//        }
//    }
//
//    @SkipSessionCheck
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buyGoods/price")
//    public Map<String, Object> priceBuyGoods(Session session,
//                                             @RequestParam(value = "buySeqNo", required = false) Long buySeqNo,
//                                             @RequestParam(value = "goodsSeqNo", required = false) Long goodsSeqNo,
//                                             @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                             @RequestParam(value = "type", required = false) Integer type,
//                                             @RequestParam(value = "isHotdeal", required = false) Boolean isHotdeal,
//                                             @RequestParam(value = "isPlus", required = false) Boolean isPlus,
//                                             @RequestParam(value = "isReviewExist", required = false) Boolean isReviewExist,
//                                             @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                             @RequestParam(value = "process", required = false) Integer process,
//                                             @RequestParam(value = "orderType", required = false) Integer orderType,
//                                             @RequestParam(value = "orderProcess", required = false) Integer orderProcess,
//                                             @RequestParam(value = "startDuration", required = false) String startDuration,
//                                             @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        if (isHotdeal != null || isPlus != null) {
//            if (isHotdeal == null && isPlus == true) {
//                isHotdeal = false;
//            } else if (isPlus == null && isHotdeal == true) {
//                isPlus = false;
//            }
//        }
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
//
//            price = buyGoodsRepository.priceAllBy(buySeqNo, memberSeqNo, type,
//                    isHotdeal, isPlus, isReviewExist,
//                    pageSeqNo, process, goodsSeqNo,
//                    orderType, orderProcess,
//                    startTime, endTime);
//
//            return result(Const.E_SUCCESS, "row", new Price(price));
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("[GET]/buyGoods/price", "select price ERROR");
//        }
//    }
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buyGoods/completeCountByGoodsSeqNo")
//    public Map<String, Object> countBuyGoods(Session session,
//                                             @RequestParam(value = "goodsSeqNo", required = false) Long goodsSeqNo) throws ResultCodeException {
//
//        Integer count = null;
//        try {
//
//            count = buyGoodsRepository.countByGoodsSeqNoAndProcess(goodsSeqNo, 3);
//
//            return result(Const.E_SUCCESS, "row", new Count(count));
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("select.buyGoods", e);
//        }
//    }
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buyGoods/buyCount")
//    public Map<String, Object> getBuyCount(Session session, @RequestParam(value = "goodsSeqNo") Long goodsSeqNo) throws ResultCodeException {
//        Integer count = null;
//        try {
//
//            count = buyGoodsService.getBuyCount(session, goodsSeqNo);
//            if (count == null) {
//                count = 0;
//            }
//
//            return result(Const.E_SUCCESS, "row", new Count(count));
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("buyGoods/buyCount", e);
//        }
//    }
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buyGoods/priceSupplySales")
//    public Map<String, Object> priceSupplySales(Session session, @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                              @RequestParam(value = "supplyPageSeqNo", required = false) Long supplyPageSeqNo,
//                                              @RequestParam(value = "startDuration", required = false) String startDuration,
//                                              @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//        Float price = null;
//        try {
//
//            price = buyGoodsService.priceSupplySales(memberSeqNo, supplyPageSeqNo, startDuration, endDuration);
//            if (price == null) {
//                price = 0f;
//            }
//
//            return result(Const.E_SUCCESS, "row", price);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("buyGoods/buyCount", e);
//        }
//    }
//
//    @CrossOrigin
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buyGoods/countSupplySales")
//    public Map<String, Object> countSupplySales(Session session, @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                              @RequestParam(value = "supplyPageSeqNo", required = false) Long supplyPageSeqNo,
//                                              @RequestParam(value = "startDuration", required = false) String startDuration,
//                                              @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//        Integer count = null;
//        try {
//
//            count = buyGoodsService.countSupplySales(memberSeqNo, supplyPageSeqNo, startDuration, endDuration);
//            if (count == null) {
//                count = 0;
//            }
//
//            return result(Const.E_SUCCESS, "row", count);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidBuyException("buyGoods/buyCount", e);
//        }
//    }
//
//    public static void main(String args[]) {
//
//        Message message = new Message();
//        Object[] objs = {(Object) "1"};
//        System.out.println(message.getMessage("buyGoods.process.name", objs));
//    }
//}
