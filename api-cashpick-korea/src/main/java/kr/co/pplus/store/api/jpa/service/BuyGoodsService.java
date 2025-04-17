//package kr.co.pplus.store.api.jpa.service;
//
//import kr.co.pplus.store.api.jpa.model.*;
//import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkPayCommonResponse;
//import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkPayDecideRequest;
//import kr.co.pplus.store.api.jpa.repository.*;
//import kr.co.pplus.store.api.util.AppUtil;
//import kr.co.pplus.store.exception.InvalidBuyException;
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.mvc.service.CashBolService;
//import kr.co.pplus.store.mvc.service.RootService;
//import kr.co.pplus.store.type.model.User;
//import kr.co.pplus.store.util.FTLinkPayApi;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//
//@Service
//@Transactional(transactionManager = "jpaTransactionManager")
//public class BuyGoodsService extends RootService {
//    private final static Logger logger = LoggerFactory.getLogger(BuyGoodsService.class);
//
//    @Autowired
//    private BuyGoodsRepository buyGoodsRepository;
//
//    @Autowired
//    BuyGoodsDetailRepository buyGoodsDetailRepository;
//
//    @Autowired
//    BuyGoodsOptionRepository buyGoodsOptionRepository;
//
//    @Autowired
//    LpngCallbackRepository lpngCallbackRepository;
//
//    @Autowired
//    PageRepository pageRepository;
//
//    @Autowired
//    BolService bolService;
//
//    @Autowired
//    CashBolService cashBolSvc;
//
//    @Autowired
//    BuyCustomerRepository buyCustomerRepository;
//
//    public org.springframework.data.domain.Page<BuyGoodsDetail> getBuyGoodsListByPageSeqNoOnlySupplyGoods(Long pageSeqNo, String startDuration, String endDuration, Integer process, Pageable pageable) {
//
//        Date startTime = null;
//        Date endTime = null;
//        try {
//            //ToDo : 나라별 시간대 Zone 추가 필요...
//            if (startDuration != null) {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                startTime = Date.from(zdt.toInstant());
//            }
//        } catch (Exception e) {
//
//        }
//
//        try {
//            //ToDo : 나라별 시간대 Zone 추가 필요...
//            if (endDuration != null) {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                endTime = Date.from(zdt.toInstant());
//            }
//        } catch (Exception e) {
//
//        }
//
//        return buyGoodsDetailRepository.findAllByPageSeqNoOnlySupplyGoods(pageSeqNo, startTime, endTime, process, pageable);
//    }
//
//    public org.springframework.data.domain.Page<BuyGoodsDetail> getBuyGoodsListBySupplyPageSeqNo(Long supplyPageSeqNo, String startDuration, String endDuration, Integer process, Pageable pageable) {
//
//        Date startTime = null;
//        Date endTime = null;
//        try {
//            //ToDo : 나라별 시간대 Zone 추가 필요...
//            if (startDuration != null) {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                startTime = Date.from(zdt.toInstant());
//            }
//        } catch (Exception e) {
//
//        }
//
//        try {
//            //ToDo : 나라별 시간대 Zone 추가 필요...
//            if (endDuration != null) {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                endTime = Date.from(zdt.toInstant());
//            }
//        } catch (Exception e) {
//
//        }
//
//        return buyGoodsDetailRepository.findAllBySupplyPageSeqNo(supplyPageSeqNo, startTime, endTime, process, pageable);
//    }
//
//    public org.springframework.data.domain.Page<BuyGoodsDetail> getBuyGoodsListByGoodsPriceSeqNoOnlySupplyGoods(Long goodsPriceSeqNo, String startDuration, String endDuration, Integer process, Pageable pageable) {
//
//        Date startTime = null;
//        Date endTime = null;
//        try {
//            //ToDo : 나라별 시간대 Zone 추가 필요...
//            if (startDuration != null) {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                startTime = Date.from(zdt.toInstant());
//            }
//        } catch (Exception e) {
//
//        }
//
//        try {
//            //ToDo : 나라별 시간대 Zone 추가 필요...
//            if (endDuration != null) {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                endTime = Date.from(zdt.toInstant());
//            }
//        } catch (Exception e) {
//
//        }
//
//        return buyGoodsDetailRepository.findAllByGoodsPriceSeqNoOnlySupplyGoods(goodsPriceSeqNo, startTime, endTime, process, pageable);
//    }
//
//    public Integer getBuyCount(User user, Long goodsSeqNo) {
//        return buyGoodsRepository.countBuyGoodsByGoodsSeqNoAndMemberSeqNo(user.getNo(), goodsSeqNo);
//    }
//
//    public List<BuyGoodsOption> getBuyGoodsOptionList(Long seqNo) {
//        return buyGoodsOptionRepository.findAllByBuyGoodsSeqNo(seqNo);
//    }
//
//    public List<BuyGoods> getShippingList() {
//        return buyGoodsRepository.findAllByOrderProcessAndOrderType(OrderProcess.DELIVERY.getProcess(), OrderType.SHIPPING_ORDER.getType());
//    }
//
//    public Float priceSupplySales(Long memberSeqNo, Long supplyPageSeqNo, String startDuration, String endDuration) {
//
//        Date startTime = null;
//        Date endTime = null;
//        try {
//            //ToDo : 나라별 시간대 Zone 추가 필요...
//            if (startDuration != null) {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                startTime = Date.from(zdt.toInstant());
//            }
//        } catch (Exception e) {
//
//        }
//
//        try {
//            //ToDo : 나라별 시간대 Zone 추가 필요...
//            if (endDuration != null) {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                endTime = Date.from(zdt.toInstant());
//            }
//        } catch (Exception e) {
//
//        }
//
//        return buyGoodsRepository.sumSupplyPrice(memberSeqNo, supplyPageSeqNo, startTime, endTime);
//    }
//
//    public Integer countSupplySales(Long memberSeqNo, Long supplyPageSeqNo, String startDuration, String endDuration) {
//
//        Date startTime = null;
//        Date endTime = null;
//        try {
//            //ToDo : 나라별 시간대 Zone 추가 필요...
//            if (startDuration != null) {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//                startTime = Date.from(zdt.toInstant());
//            }
//        } catch (Exception e) {
//
//        }
//
//        try {
//            //ToDo : 나라별 시간대 Zone 추가 필요...
//            if (endDuration != null) {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//                ZonedDateTime zdt = ZonedDateTime.parse(endDuration, formatter);
//                endTime = Date.from(zdt.toInstant());
//            }
//        } catch (Exception e) {
//
//        }
//
//        return buyGoodsRepository.countSupplySales(memberSeqNo, supplyPageSeqNo, startTime, endTime);
//    }
//
//    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public void updateTransportNumber(BuyGoods buyGoods) {
//        String dateStr = AppUtil.localDatetimeNowString();
//
//        BuyGoods saved = buyGoodsRepository.findBySeqNo(buyGoods.getSeqNo());
//
//        saved.setTransportNumber(buyGoods.getTransportNumber());
//        saved.setShippingCompany(buyGoods.getShippingCompany());
//        saved.setShippingCompanyCode(buyGoods.getShippingCompanyCode());
//        saved.setModDatetime(dateStr);
//        if (saved.getOrderProcess() == OrderProcess.CONFIRM.getProcess() || saved.getOrderProcess() == OrderProcess.WAIT.getProcess()) {
//            saved.setDeliveryStartDatetime(dateStr);
//            saved.setOrderProcess(OrderProcess.DELIVERY.getProcess());
//        }
//
//        buyGoodsRepository.save(saved);
//    }
//
//    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public void save(BuyGoods buyGoods) {
//        buyGoodsRepository.save(buyGoods);
//    }
//
//    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public void updateDeliveryCompleteBySeqNo(Long seqNo) {
//        String dateStr = AppUtil.localDatetimeNowString();
//        buyGoodsRepository.updateDeliveryCompleteBySeqNo(OrderProcess.DELIVERY_COMPLETE.getProcess(), seqNo, dateStr);
//    }
//
//    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public void updateCompleteBySeqNo(Long seqNo) throws ResultCodeException {
//        try {
//            String dateStr = AppUtil.localDatetimeNowString();
//            BuyGoods buyGoods = buyGoodsRepository.findBySeqNo(seqNo);
//            requestDecide(buyGoods, dateStr);
//        } catch (Exception e) {
//            throw new InvalidBuyException("updateCompleteBySeqNo", e);
//        }
//    }
//
//    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public void setCompleteBuyGoodsList() throws ResultCodeException {
//
//        try {
//            String dateStr = AppUtil.localDatetimeNowString();
//            List<BuyGoods> buyGoodsList = buyGoodsRepository.findAllNeedCompleteList();
//            for (BuyGoods buyGoods : buyGoodsList) {
//                requestDecide(buyGoods, dateStr);
//            }
//
//        } catch (Exception e) {
//            throw new InvalidBuyException("CompleteBuyGoodsList", e);
//        }
//
//    }
//
//    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public void requestDecide(BuyGoods buyGoods, String dateStr) throws Exception {
//        kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(buyGoods.getPageSeqNo());
//        LpngCallback callback = lpngCallbackRepository.findAllByBuySeqNo(buyGoods.getBuySeqNo());
//        FTLinkPayDecideRequest ftLinkPayDecideRequest = new FTLinkPayDecideRequest();
//        if (callback.getLpngOrderNo() != null) {
//            ftLinkPayDecideRequest.setOrderno(callback.getLpngOrderNo());
//        }
//        ftLinkPayDecideRequest.setShopcode(page.getShopCode());
//        ftLinkPayDecideRequest.setAPPRTRXID(callback.getPgTranId());
//        FTLinkPayCommonResponse res = FTLinkPayApi.payDecideRequest(ftLinkPayDecideRequest);
//
//        if (res.getErrcode().equals("0000") || res.getErrcode().equals("00") || res.getErrcode().equals("90")) {
//            buyGoodsRepository.updateBuyCompleteBySeqNo(OrderProcess.COMPLETE.getProcess(), buyGoods.getSeqNo(), dateStr);
//
//            if (buyGoods.getMemberSeqNo() != null) {
//                Integer point = buyGoods.getSavedPoint();
//                if (point != null && point > 0 && !buyGoods.getIsPaymentPoint()) {
//
//                    BolHistory bolHistory = new BolHistory();
//                    bolHistory.setAmount(Long.valueOf(point));
//                    bolHistory.setMemberSeqNo(buyGoods.getMemberSeqNo());
//                    bolHistory.setSubject("상품구매 적립");
//                    bolHistory.setPrimaryType("increase");
//                    bolHistory.setSecondaryType("buy");
//                    bolHistory.setTargetType("member");
//                    bolHistory.setTargetSeqNo(buyGoods.getMemberSeqNo());
//                    bolHistory.setHistoryProp(new HashMap<String, Object>());
//                    bolHistory.getHistoryProp().put("지급처", page.getName());
//                    bolHistory.getHistoryProp().put("적립유형", "구매확정(" + buyGoods.getTitle() + ")");
//
//                    bolService.increaseBol(buyGoods.getMemberSeqNo(), bolHistory);
//
//                    buyGoodsRepository.updateBuyGoodsPaymentPointBySeqNo(buyGoods.getSeqNo(), dateStr, 1);
//
//                }
//
//                BuyCustomer buyCustomer = buyCustomerRepository.findByMemberSeqNoAndPageSeqNo(buyGoods.getMemberSeqNo(), buyGoods.getPageSeqNo());
//                if (buyCustomer != null) {
//                    buyCustomer.setBuyCount(buyCustomer.getBuyCount());
//                    buyCustomer.setLastBuyDatetime(dateStr);
//                } else {
//                    buyCustomer = new BuyCustomer();
//                    buyCustomer.setMemberSeqNo(buyGoods.getMemberSeqNo());
//                    buyCustomer.setPageSeqNo(buyGoods.getPageSeqNo());
//                    buyCustomer.setBuyCount(1);
//                    buyCustomer.setLastBuyDatetime(dateStr);
//                }
//                buyCustomerRepository.save(buyCustomer);
//            }
//
//        }
//
//    }
//
//    public Integer getCountReadBuyGoods(Long pageSeqNo) {
//        List<Integer> orderProcessList = new ArrayList<>();
//        orderProcessList.add(OrderProcess.WAIT.getProcess());
//        orderProcessList.add(OrderProcess.CONFIRM.getProcess());
//        return buyGoodsRepository.countAllByPageSeqNoAndProcessAndOrderTypeAndOrderProcessIn(pageSeqNo, 1, 3, orderProcessList);
//    }
//
//}
