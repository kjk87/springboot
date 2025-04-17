package kr.co.pplus.store.api.jpa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.model.reappay.ReapPayBillKeyData;
import kr.co.pplus.store.api.jpa.model.reappay.ReapPayCancelData;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.mvc.service.CommonService;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.util.Filtering;
import kr.co.pplus.store.util.StoreUtil;
import kr.co.pplus.store.util.aws.AmazonSQSSenderImpl;
import kr.co.pplus.store.util.aws.SqsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class LuckyPickService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(LuckyPickService.class);

    @Autowired
    LuckyPickRepository luckyPickRepository;

    @Autowired
    LuckyPickPurchaseRepository luckyPickPurchaseRepository;

    @Autowired
    LuckyPickPurchaseItemOnlyRepository luckyPickPurchaseItemOnlyRepository;

    @Autowired
    LuckyPickPurchaseItemRepository luckyPickPurchaseItemRepository;

    @Autowired
    LuckyPickPurchaseWithItemRepository luckyPickPurchaseWithItemRepository;

    @Autowired
    LuckyPickPurchaseCancelRepository luckyPickPurchaseCancelRepository;

    @Autowired
    LuckyPickDeliveryPurchaseRepository luckyPickDeliveryPurchaseRepository;

    @Autowired
    LuckyPickDeliveryRepository luckyPickDeliveryRepository;

    @Autowired
    LuckyPickPurchaseItemOptionRepository luckyPickPurchaseItemOptionRepository;

    @Autowired
    ProductDeliveryRepository productDeliveryRepository;

    @Autowired
    LuckyPickReplyRepository luckyPickReplyRepository;

    @Autowired
    LuckyPickReplyOnlyRepository luckyPickReplyOnlyRepository;

    @Autowired
    LuckyPickReviewRepository luckyPickReviewRepository;

    @Autowired
    LuckyPickReviewDetailRepository luckyPickReviewDetailRepository;

    @Autowired
    LuckyPickReviewImageRepository luckyPickReviewImageRepository;

    @Autowired
    private AmazonSQSSenderImpl amazonSQSSender;

    @Autowired
    ReapPayService reapPayService;

    @Autowired
    PointService pointService;

    @Autowired
    MemberService memberService;

    @Autowired
    ProductService productService;

    @Autowired
    CommonService commonService;

    @Value("${STORE.TYPE}")
    String storeType;

    private static final String BASE_URL = "http://info.sweettracker.co.kr/";

    public Page<LuckyPick> getLuckyPickList(Pageable pageable) {
        String dateStr = AppUtil.localDatetimeNowString();
        return luckyPickRepository.findAllByStatusAndAndroidAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualOrderByArrayAsc("active", true, dateStr, dateStr, pageable);
    }


    public LuckyPickPurchase getLuckyPickPurchase(Long seqNo) {
        return luckyPickPurchaseRepository.findBySeqNo(seqNo);
    }

    public LuckyPickDeliveryPurchase getLuckyPickDeliveryPurchase(Long seqNo){
        return luckyPickDeliveryPurchaseRepository.findBySeqNo(seqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public LuckyPickPurchase saveLuckyPickPurchase(LuckyPickPurchase luckyPickPurchase) throws ResultCodeException {

        String dateStr = AppUtil.localDatetimeNowString();

        try {
            luckyPickPurchase.setOrderNo(StoreUtil.getRandomOrderId());
        } catch (Exception e) {
            logger.error(e.toString());
        }

        luckyPickPurchase.setCancelPrice(0f);
        luckyPickPurchase.setCancelQuantity(0);
        luckyPickPurchase.setStatus(1);
        luckyPickPurchase.setRemainPrice(luckyPickPurchase.getPrice());
        luckyPickPurchase.setRegDatetime(dateStr);
        luckyPickPurchase.setChangeStatusDatetime(dateStr);
        luckyPickPurchase.setSalesType("delivery");
        luckyPickPurchase = luckyPickPurchaseRepository.save(luckyPickPurchase);


        return luckyPickPurchase;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void paymentLuckyPickPurchase(HttpServletRequest request) throws ResultCodeException {

        String resultStatus = request.getParameter("resultStatus");//승인결과(0000: 성공, 나머지: 실패)
        if (resultStatus.equals("0000")) {
            String orderSeq = request.getParameter("orderSeq");
            String tranSeq = request.getParameter("tranSeq");
            String tranCate = request.getParameter("tranCate");
            String cardNum = request.getParameter("cardNum");
            String appDt = request.getParameter("appDt");
            String appTm = request.getParameter("appTm");
            String appNo = request.getParameter("appNo");
            String totAmt = request.getParameter("totAmt");
            String supplyAmt = request.getParameter("supplyAmt");
            String vatAmt = request.getParameter("vatAmt");
            String tranDt = request.getParameter("tranDt");//yyyyMMdd
            String tranTm = request.getParameter("tranTm");//HHmmss
            String installment = request.getParameter("installment");
            String issCd = request.getParameter("issCd");//카드사코드
            String issNm = request.getParameter("issNm");//카드사이름
            String cnclReason = request.getParameter("cnclReason");
            String orgTranSeq = request.getParameter("orgTranSeq");

            LuckyPickPurchase luckyPickPurchase = luckyPickPurchaseRepository.findByOrderNo(orderSeq);

            if(luckyPickPurchase == null || !luckyPickPurchase.getStatus().equals(1)){
                throw new NotPermissionException();
            }

            luckyPickPurchase.setStatus(2);

            String paymentDate = appDt + " " + appTm;
            luckyPickPurchase.setPaymentDatetime(paymentDate);
            luckyPickPurchase.setChangeStatusDatetime(paymentDate);
            luckyPickPurchase.setPayResponseApprovalNo(appNo);
            luckyPickPurchase.setPayResponseCardId(issCd);
            luckyPickPurchase.setPayResponseCardNm(issNm);
            luckyPickPurchase.setPayResponseCardNo(cardNum);
            luckyPickPurchase.setPayResponseCertYn(true);
            luckyPickPurchase.setPayResponseCode(resultStatus);
            luckyPickPurchase.setPayResponseInstallment(installment);
            luckyPickPurchase.setPayResponseOrderNo(orderSeq);
            luckyPickPurchase.setPayResponsePayDate(appDt);
            luckyPickPurchase.setPayResponsePayTime(appTm);
            luckyPickPurchase.setPayResponsePayType("card");
            luckyPickPurchase.setPayResponseProductType("R");
            luckyPickPurchase.setPayResponseSellMm(installment);
            if (storeType.equals("PROD")) {
                luckyPickPurchase.setPayResponseTestYn(false);
            } else {
                luckyPickPurchase.setPayResponseTestYn(true);
            }

            luckyPickPurchase.setPayResponseTranSeq(tranSeq);
            luckyPickPurchase.setPayResponseZerofeeYn(false);
            luckyPickPurchase = luckyPickPurchaseRepository.save(luckyPickPurchase);

            for (int i = 0; i < luckyPickPurchase.getQuantity(); i++) {
                LuckyPickPurchaseItemOnly luckyPickPurchaseItem = new LuckyPickPurchaseItemOnly();
                luckyPickPurchaseItem.setLuckyPickSeqNo(luckyPickPurchase.getLuckyPickSeqNo());
                luckyPickPurchaseItem.setLuckyPickPurchaseSeqNo(luckyPickPurchase.getSeqNo());
                luckyPickPurchaseItem.setMemberSeqNo(luckyPickPurchase.getMemberSeqNo());
                luckyPickPurchaseItem.setLuckyPickTitle(luckyPickPurchase.getTitle());
                luckyPickPurchaseItem.setPaymentMethod(luckyPickPurchase.getPaymentMethod());
                luckyPickPurchaseItem.setPrice(luckyPickPurchase.getUnitPrice());
                luckyPickPurchaseItem.setStatus(2);
                luckyPickPurchaseItem.setIsOpen(false);
                luckyPickPurchaseItem.setRegDatetime(paymentDate);
                luckyPickPurchaseItem.setTempMember(false);
                luckyPickPurchaseItem.setPaymentDatetime(paymentDate);
                luckyPickPurchaseItem.setLuckyPickPayResponseTranSeq(tranSeq);
                luckyPickPurchaseItemOnlyRepository.save(luckyPickPurchaseItem);
            }
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void paymentBillkeyLuckyPickPurchase(Long seqNo, String token, String installment) throws ResultCodeException {

        LuckyPickPurchase luckyPickPurchase = luckyPickPurchaseRepository.findBySeqNo(seqNo);

        if(luckyPickPurchase == null || !luckyPickPurchase.getStatus().equals(1)){
            throw new NotPermissionException();
        }

        ReapPayBillKeyData reapPayBillKeyData = reapPayService.billkeypay(null, null, null, luckyPickPurchase, null, token, installment);

        if(reapPayBillKeyData == null){
            throw new InvalidBuyException();
        }

        luckyPickPurchase.setStatus(2);

        String paymentDate = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = sdf.parse(reapPayBillKeyData.getBillkeytradeDateTime());

            paymentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        }catch (Exception e){
            logger.error(e.toString());
            paymentDate = AppUtil.localDatetimeNowString();
        }


        luckyPickPurchase.setPaymentDatetime(paymentDate);
        luckyPickPurchase.setChangeStatusDatetime(paymentDate);
        luckyPickPurchase.setPayResponseApprovalNo(reapPayBillKeyData.getBillkeyapprovalNumb());
        luckyPickPurchase.setPayResponseCardId(reapPayBillKeyData.getBillkeypurchaseCardType());
        luckyPickPurchase.setPayResponseCardNm(reapPayBillKeyData.getBillkeypurchaseCardName());
        luckyPickPurchase.setPayResponseCardNo(reapPayBillKeyData.getBillkeymaskedCardNumb());
        luckyPickPurchase.setPayResponseCertYn(true);
        luckyPickPurchase.setPayResponseCode(reapPayBillKeyData.getBillkeyrespCode());
        luckyPickPurchase.setPayResponseInstallment(reapPayBillKeyData.getBillkeyinstallment());
        luckyPickPurchase.setPayResponseOrderNo(luckyPickPurchase.getOrderNo());
        luckyPickPurchase.setPayResponsePayDate(paymentDate.split(" ")[0]);
        luckyPickPurchase.setPayResponsePayTime(paymentDate.split(" ")[1]);
        luckyPickPurchase.setPayResponsePayType("easy");
        luckyPickPurchase.setPayResponseProductType("R");
        luckyPickPurchase.setPayResponseSellMm(reapPayBillKeyData.getBillkeyinstallment());
        if (storeType.equals("PROD")) {
            luckyPickPurchase.setPayResponseTestYn(false);
        } else {
            luckyPickPurchase.setPayResponseTestYn(true);
        }

        luckyPickPurchase.setPayResponseTranSeq(reapPayBillKeyData.getBillkeyTranseq());
        luckyPickPurchase.setPayResponseZerofeeYn(false);
        luckyPickPurchase = luckyPickPurchaseRepository.save(luckyPickPurchase);

        for (int i = 0; i < luckyPickPurchase.getQuantity(); i++) {
            LuckyPickPurchaseItemOnly luckyPickPurchaseItem = new LuckyPickPurchaseItemOnly();
            luckyPickPurchaseItem.setLuckyPickSeqNo(luckyPickPurchase.getLuckyPickSeqNo());
            luckyPickPurchaseItem.setLuckyPickPurchaseSeqNo(luckyPickPurchase.getSeqNo());
            luckyPickPurchaseItem.setMemberSeqNo(luckyPickPurchase.getMemberSeqNo());
            luckyPickPurchaseItem.setLuckyPickTitle(luckyPickPurchase.getTitle());
            luckyPickPurchaseItem.setPaymentMethod(luckyPickPurchase.getPaymentMethod());
            luckyPickPurchaseItem.setPrice(luckyPickPurchase.getUnitPrice());
            luckyPickPurchaseItem.setStatus(2);
            luckyPickPurchaseItem.setIsOpen(false);
            luckyPickPurchaseItem.setRegDatetime(paymentDate);
            luckyPickPurchaseItem.setTempMember(false);
            luckyPickPurchaseItem.setPaymentDatetime(paymentDate);
            luckyPickPurchaseItem.setLuckyPickPayResponseTranSeq(reapPayBillKeyData.getBillkeyTranseq());
            luckyPickPurchaseItemOnlyRepository.save(luckyPickPurchaseItem);
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void cancelLuckyPick(User user, Long luckyPickPurchaseSeqNo) throws ResultCodeException {
        LuckyPickPurchase luckyPickPurchase = luckyPickPurchaseRepository.findBySeqNo(luckyPickPurchaseSeqNo);
        List<LuckyPickPurchaseItemOnly> itemList = luckyPickPurchaseItemOnlyRepository.findAllByLuckyPickPurchaseSeqNo(luckyPickPurchaseSeqNo);
        if (!user.getNo().equals(luckyPickPurchase.getMemberSeqNo())) {
            throw new NotPermissionException();
        }

        if (luckyPickPurchase.getStatus() != 2) {
            throw new NotPermissionException();
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            calendar.setTime(sdf.parse(luckyPickPurchase.getPaymentDatetime()));

        }catch (ParseException e){
            logger.error(e.toString());
            throw new UnknownException();
        }

        calendar.add(Calendar.DAY_OF_MONTH, 7);

        if(System.currentTimeMillis() > calendar.getTimeInMillis()){
            throw new ExpiredException();
        }

        for (LuckyPickPurchaseItemOnly item : itemList) {
            if (item.getIsOpen()) {
                throw new NotPermissionException();
            }
        }

        Long seqNo = 1L;
        if(!storeType.equals("PROD")){
            if(luckyPickPurchase.getPaymentMethod().equals("card")){
                seqNo = 1L;
            }else if(luckyPickPurchase.getPaymentMethod().equals("easy")){
                seqNo = 2L;
            }
        }


        ReapPayCancelData data = reapPayService.cancel(luckyPickPurchase.getPayResponseTranSeq(), seqNo);
        if (data == null) {
            throw new CancelFailException();
        }

        String dateStr = AppUtil.localDatetimeNowString();

        luckyPickPurchase.setStatus(3);
        luckyPickPurchase.setCancelQuantity(luckyPickPurchase.getQuantity());
        luckyPickPurchase.setCancelPrice(luckyPickPurchase.getPrice());
        luckyPickPurchase.setChangeStatusDatetime(dateStr);
        luckyPickPurchase.setRemainPrice(0f);
        luckyPickPurchaseRepository.save(luckyPickPurchase);
        for (LuckyPickPurchaseItemOnly item : itemList) {
            item.setStatus(3);
            item.setCancelDatetime(dateStr);

            LuckyPickPurchaseCancel cancel = new LuckyPickPurchaseCancel();
            cancel.setLuckyPickPurchaseSeqNo(item.getLuckyPickPurchaseSeqNo());
            cancel.setLuckyPickPurchaseItemSeqNo(item.getSeqNo());
            cancel.setPayResponseCode(data.getPayResponseCode());
            cancel.setPayResponseMsg(data.getPayResponseMsg());
            cancel.setPayResponsePayDate(data.getPayResponsePayDate());
            cancel.setPayResponsePayTime(data.getPayResponsePayTime());
            cancel.setPayResponseAmt(data.getPayResponseAmt());
//            cancel.setPayResponsePgSeq(data.getPayResponsePgSeq());

            cancel.setPayResponseApprovalYMDHMS(data.getPayResponseApprovalYMDHMS());
            cancel.setPayResponseApprovalNo(data.getPayResponseApprovalNo());

            cancel.setPayResponseTranSeq(data.getPayResponseTranSeq());
            luckyPickPurchaseCancelRepository.save(cancel);
        }

        luckyPickPurchaseItemOnlyRepository.saveAll(itemList);

    }

    public void expiredLuckyPickPurchaseItem(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String expireDate = sdf.format(calendar.getTime());

        String dateStr = AppUtil.localDatetimeNowString();
        List<LuckyPickPurchaseItemOnly> list = luckyPickPurchaseItemOnlyRepository.findAllByStatusAndIsOpenAndOpenDatetimeLessThanEqual(2, true, expireDate);
        for(LuckyPickPurchaseItemOnly item : list){
            item.setStatus(4);
            item.setExchangeDatetime(dateStr);
            item = luckyPickPurchaseItemOnlyRepository.save(item);

            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(item.getMemberSeqNo());
            pointHistory.setType("charge");
            pointHistory.setPoint(item.getPrice());
            pointHistory.setSubject("럭키픽 캐시 환급");
            pointService.updatePoint(item.getMemberSeqNo(), pointHistory);

        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void cashBackItem(User user, Long luckyPickPurchaseItemSeqNo) throws ResultCodeException {
        LuckyPickPurchaseItemOnly luckyPickPurchaseItem = luckyPickPurchaseItemOnlyRepository.findBySeqNo(luckyPickPurchaseItemSeqNo);

        if(luckyPickPurchaseItem == null){
            throw new NotFoundException();
        }

        if(!user.getNo().equals(luckyPickPurchaseItem.getMemberSeqNo())){
            throw new NotPermissionException();
        }

        if(luckyPickPurchaseItem.getStatus() != 2 || !luckyPickPurchaseItem.getIsOpen() || luckyPickPurchaseItem.getDeliveryStatus() != null){
            throw new NotPermissionException();
        }

        luckyPickPurchaseItem.setStatus(4);
        luckyPickPurchaseItem.setExchangeDatetime(AppUtil.localDatetimeNowString());

        luckyPickPurchaseItem = luckyPickPurchaseItemOnlyRepository.save(luckyPickPurchaseItem);

        PointHistory pointHistory = new PointHistory();
        pointHistory.setMemberSeqNo(luckyPickPurchaseItem.getMemberSeqNo());
        pointHistory.setType("charge");
        pointHistory.setPoint(luckyPickPurchaseItem.getPrice());
        pointHistory.setSubject("럭키픽 캐시 환급");
        pointService.updatePoint(luckyPickPurchaseItem.getMemberSeqNo(), pointHistory);

    }

    public Integer getCountNotOpenLuckyPickPurchaseItem(Session session) {
        return luckyPickPurchaseItemRepository.countByMemberSeqNoAndStatusAndIsOpen(session.getNo(), 2, false);
    }


    public List<LuckyPickPurchaseItem> getNotOpenLuckyPickPurchaseItemListByLuckyPickPurchaseSeqNo(Session session, Long luckyPickPurchaseSeqNo) {
        return luckyPickPurchaseItemRepository.findAllByMemberSeqNoAndStatusAndIsOpenAndLuckyPickPurchaseSeqNoOrderBySeqNoDesc(session.getNo(), 2, false, luckyPickPurchaseSeqNo);
    }

    public List<LuckyPickPurchaseWithItem> getNotOpenLuckyPickPurchaseList(Session session) {
        return luckyPickPurchaseWithItemRepository.findAllBy(session.getNo());
    }

    public Page<LuckyPickPurchaseItem> getOpenLuckyPickPurchaseItemList(Session session, Pageable pageable) {

        List<Integer> statusList = new ArrayList<>();
        statusList.add(2);
//        statusList.add(4);

        return luckyPickPurchaseItemRepository.findAllByMemberSeqNoAndStatusInAndIsOpenOrderByOpenDatetimeDesc(session.getNo(), statusList, true, pageable);

    }

    public Page<LuckyPickPurchaseItem> getTotalLuckyPickPurchaseItemList(Pageable pageable) {

        List<Integer> statusList = new ArrayList<>();
        statusList.add(2);
//        statusList.add(4);

        return luckyPickPurchaseItemRepository.findAllByStatusInAndIsOpenOrderBySeqNoDesc(statusList, true, pageable);

    }

    public void openLuckyPickPurchaseItem(SqsModel model) {
        try {
            amazonSQSSender.sendMessage(model);
        } catch (JsonProcessingException e) {
            logger.error(e.toString());
        }
    }

    public LuckyPickPurchaseItem confirmLuckyPickPurchaseItem(Long luckyPickPurchaseItemSeqNo) {
        LuckyPickPurchaseItem luckyPickPurchaseItem = luckyPickPurchaseItemRepository.findBySeqNo(luckyPickPurchaseItemSeqNo);
        if (luckyPickPurchaseItem != null && luckyPickPurchaseItem.getIsOpen()) {//status == 4면 미당첨
            return luckyPickPurchaseItem;
        }
        return null;
    }

    public LuckyPickPurchaseItem getLuckyPickPurchaseItem(Long seqNo){
        return luckyPickPurchaseItemRepository.findBySeqNo(seqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public LuckyPickDeliveryPurchase saveLuckyPickDeliveryPurchase(LuckyPickDeliveryPurchase luckyPickDeliveryPurchase) throws ResultCodeException {

        if(luckyPickDeliveryPurchase.getUsePoint() == null){
            luckyPickDeliveryPurchase.setUsePoint(0);
            luckyPickDeliveryPurchase.setPgPrice(luckyPickDeliveryPurchase.getPrice().intValue());
        }

        LuckyPickPurchaseItemOption luckyPickPurchaseItemOption = luckyPickDeliveryPurchase.getSelectOption();
        LuckyPickDelivery luckyPickDelivery = luckyPickDeliveryPurchase.getSelectDelivery();

        if(luckyPickPurchaseItemOption != null){
            luckyPickPurchaseItemOption = luckyPickPurchaseItemOptionRepository.save(luckyPickPurchaseItemOption);
        }

        luckyPickDelivery = luckyPickDeliveryRepository.save(luckyPickDelivery);

        String dateStr = AppUtil.localDatetimeNowString();

        try {
            luckyPickDeliveryPurchase.setOrderNo(StoreUtil.getRandomOrderId());
        } catch (Exception e) {
            logger.error(e.toString());
        }
        luckyPickDeliveryPurchase.setRegDatetime(dateStr);

        LuckyPickPurchaseItemOnly luckyPickPurchaseItem = luckyPickPurchaseItemOnlyRepository.findBySeqNo(luckyPickDeliveryPurchase.getLuckyPickPurchaseItemSeqNo());
        List<ProductDelivery> productDeliveryList = productDeliveryRepository.findAllByProductSeqNo(luckyPickPurchaseItem.getProductSeqNo());
        if(productDeliveryList != null && productDeliveryList.size() > 0){
            luckyPickPurchaseItem.setProductDeliverySeqNo(productDeliveryList.get(0).getSeqNo());
        }

        luckyPickPurchaseItem.setDeliveryFee((luckyPickDelivery.getDeliveryFee() + luckyPickDelivery.getDeliveryAddFee1() + luckyPickDelivery.getDeliveryAddFee2()));

        if(luckyPickPurchaseItemOption != null){
            luckyPickPurchaseItem.setOptionPrice(luckyPickPurchaseItemOption.getPrice());
            String depth = luckyPickPurchaseItemOption.getDepth1();
            if(!AppUtil.isEmpty(luckyPickPurchaseItemOption.getDepth2())){
                depth += ("/"+luckyPickPurchaseItemOption.getDepth2());
            }
            luckyPickPurchaseItem.setOptionName(depth);
        }

        luckyPickPurchaseItem.setDeliveryPaymentPrice(luckyPickDeliveryPurchase.getPrice());
        luckyPickPurchaseItem.setLuckyPickDeliverySeqNo(luckyPickDelivery.getSeqNo());
        luckyPickPurchaseItem.setExchangeDatetime(dateStr);

        if(luckyPickDeliveryPurchase.getUsePoint() > 0){
            Member member = memberService.getMemberBySeqNo(luckyPickDeliveryPurchase.getMemberSeqNo());
            if(luckyPickDeliveryPurchase.getUsePoint() > member.getPoint()){
                throw new LackCostException();
            }
        }

        if(luckyPickDeliveryPurchase.getPaymentMethod().equals("point") || luckyPickDeliveryPurchase.getPrice().intValue() == luckyPickDeliveryPurchase.getUsePoint()){

            luckyPickDeliveryPurchase.setPaymentMethod("point");
            luckyPickDeliveryPurchase.setPaymentDatetime(dateStr);

            luckyPickDeliveryPurchase.setStatus(2);

            luckyPickPurchaseItem.setDeliveryPayStatus(2);
            luckyPickPurchaseItem.setDeliveryStatus(1);
            luckyPickPurchaseItem.setExchangeDatetime(dateStr);

            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(luckyPickDeliveryPurchase.getMemberSeqNo());
            pointHistory.setType("used");
            pointHistory.setPoint(luckyPickDeliveryPurchase.getPrice());
            pointHistory.setSubject("배송비 결제");
            pointService.updatePoint(luckyPickDeliveryPurchase.getMemberSeqNo(), pointHistory);

        }else{
            luckyPickDeliveryPurchase.setStatus(1);
            luckyPickPurchaseItem.setDeliveryPayStatus(1);
        }
        luckyPickDeliveryPurchase = luckyPickDeliveryPurchaseRepository.save(luckyPickDeliveryPurchase);
        luckyPickPurchaseItem.setLuckyPickDeliveryPurchaseSeqNo(luckyPickDeliveryPurchase.getSeqNo());
        luckyPickPurchaseItem = luckyPickPurchaseItemOnlyRepository.save(luckyPickPurchaseItem);

        return luckyPickDeliveryPurchase;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void paymentLuckyPickDeliveryPurchase(HttpServletRequest request) throws ResultCodeException {

        String resultStatus = request.getParameter("resultStatus");//승인결과(0000: 성공, 나머지: 실패)
        if (resultStatus.equals("0000")) {
            String orderSeq = request.getParameter("orderSeq");
            String tranSeq = request.getParameter("tranSeq");
            String tranCate = request.getParameter("tranCate");
            String cardNum = request.getParameter("cardNum");
            String appDt = request.getParameter("appDt");
            String appTm = request.getParameter("appTm");
            String appNo = request.getParameter("appNo");
            String totAmt = request.getParameter("totAmt");
            String supplyAmt = request.getParameter("supplyAmt");
            String vatAmt = request.getParameter("vatAmt");
            String tranDt = request.getParameter("tranDt");//yyyyMMdd
            String tranTm = request.getParameter("tranTm");//HHmmss
            String installment = request.getParameter("installment");
            String issCd = request.getParameter("issCd");//카드사코드
            String issNm = request.getParameter("issNm");//카드사이름
            String cnclReason = request.getParameter("cnclReason");
            String orgTranSeq = request.getParameter("orgTranSeq");

            LuckyPickDeliveryPurchase luckyPickDeliveryPurchase = luckyPickDeliveryPurchaseRepository.findByOrderNo(orderSeq);

            if(luckyPickDeliveryPurchase == null || !luckyPickDeliveryPurchase.getStatus().equals(1)){
                throw new NotPermissionException();
            }

            luckyPickDeliveryPurchase.setStatus(2);

            String paymentDate = appDt + " " + appTm;
            luckyPickDeliveryPurchase.setPaymentDatetime(paymentDate);
            luckyPickDeliveryPurchase.setPayResponseApprovalNo(appNo);
            luckyPickDeliveryPurchase.setPayResponseCardId(issCd);
            luckyPickDeliveryPurchase.setPayResponseCardNm(issNm);
            luckyPickDeliveryPurchase.setPayResponseCardNo(cardNum);
            luckyPickDeliveryPurchase.setPayResponseCertYn(true);
            luckyPickDeliveryPurchase.setPayResponseCode(resultStatus);
            luckyPickDeliveryPurchase.setPayResponseInstallment(installment);
            luckyPickDeliveryPurchase.setPayResponseOrderNo(orderSeq);
            luckyPickDeliveryPurchase.setPayResponsePayDate(appDt);
            luckyPickDeliveryPurchase.setPayResponsePayTime(appTm);
            luckyPickDeliveryPurchase.setPayResponsePayType("card");
            luckyPickDeliveryPurchase.setPayResponseProductType("R");
            luckyPickDeliveryPurchase.setPayResponseSellMm(installment);
            if (storeType.equals("PROD")) {
                luckyPickDeliveryPurchase.setPayResponseTestYn(false);
            } else {
                luckyPickDeliveryPurchase.setPayResponseTestYn(true);
            }

            luckyPickDeliveryPurchase.setPayResponseTranSeq(tranSeq);
            luckyPickDeliveryPurchase.setPayResponseZerofeeYn(false);
            luckyPickDeliveryPurchase = luckyPickDeliveryPurchaseRepository.save(luckyPickDeliveryPurchase);

            if(luckyPickDeliveryPurchase.getUsePoint() != null && luckyPickDeliveryPurchase.getUsePoint() > 0){
                PointHistory pointHistory = new PointHistory();
                pointHistory.setMemberSeqNo(luckyPickDeliveryPurchase.getMemberSeqNo());
                pointHistory.setType("used");
                pointHistory.setPoint(luckyPickDeliveryPurchase.getUsePoint().floatValue());
                pointHistory.setSubject("배송비 결제");
                pointService.updatePoint(luckyPickDeliveryPurchase.getMemberSeqNo(), pointHistory);
            }

            LuckyPickPurchaseItemOnly luckyPickPurchaseItem = luckyPickPurchaseItemOnlyRepository.findBySeqNo(luckyPickDeliveryPurchase.getLuckyPickPurchaseItemSeqNo());
            luckyPickPurchaseItem.setDeliveryPayStatus(2);
            luckyPickPurchaseItem.setDeliveryStatus(1);
            luckyPickPurchaseItem.setExchangeDatetime(paymentDate);
            luckyPickPurchaseItemOnlyRepository.save(luckyPickPurchaseItem);
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void paymentBillkeyLuckyPickDeliveryPurchase(Long seqNo, String token, String installment) throws ResultCodeException {

        LuckyPickDeliveryPurchase luckyPickDeliveryPurchase = luckyPickDeliveryPurchaseRepository.findBySeqNo(seqNo);

        if(luckyPickDeliveryPurchase == null || !luckyPickDeliveryPurchase.getStatus().equals(1)){
            throw new NotPermissionException();
        }

        ReapPayBillKeyData reapPayBillKeyData = reapPayService.billkeypay(null, null, null, null, luckyPickDeliveryPurchase, token, installment);

        if(reapPayBillKeyData == null){
            throw new InvalidBuyException();
        }

        luckyPickDeliveryPurchase.setStatus(2);

        String paymentDate = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = sdf.parse(reapPayBillKeyData.getBillkeytradeDateTime());

            paymentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        }catch (Exception e){
            logger.error(e.toString());
            paymentDate = AppUtil.localDatetimeNowString();
        }

        luckyPickDeliveryPurchase.setPaymentDatetime(paymentDate);
        luckyPickDeliveryPurchase.setPayResponseApprovalNo(reapPayBillKeyData.getBillkeyapprovalNumb());
        luckyPickDeliveryPurchase.setPayResponseCardId(reapPayBillKeyData.getBillkeypurchaseCardType());
        luckyPickDeliveryPurchase.setPayResponseCardNm(reapPayBillKeyData.getBillkeyissuerCardName());
        luckyPickDeliveryPurchase.setPayResponseCardNo(reapPayBillKeyData.getBillkeymaskedCardNumb());
        luckyPickDeliveryPurchase.setPayResponseCertYn(true);
        luckyPickDeliveryPurchase.setPayResponseCode(reapPayBillKeyData.getBillkeyrespCode());
        luckyPickDeliveryPurchase.setPayResponseInstallment(installment);
        luckyPickDeliveryPurchase.setPayResponseOrderNo(luckyPickDeliveryPurchase.getOrderNo());
        luckyPickDeliveryPurchase.setPayResponsePayDate(paymentDate.split(" ")[0]);
        luckyPickDeliveryPurchase.setPayResponsePayTime(paymentDate.split(" ")[1]);
        luckyPickDeliveryPurchase.setPayResponsePayType("card");
        luckyPickDeliveryPurchase.setPayResponseProductType("R");
        luckyPickDeliveryPurchase.setPayResponseSellMm(installment);
        if (storeType.equals("PROD")) {
            luckyPickDeliveryPurchase.setPayResponseTestYn(false);
        } else {
            luckyPickDeliveryPurchase.setPayResponseTestYn(true);
        }

        luckyPickDeliveryPurchase.setPayResponseTranSeq(reapPayBillKeyData.getBillkeyTranseq());
        luckyPickDeliveryPurchase.setPayResponseZerofeeYn(false);
        luckyPickDeliveryPurchase = luckyPickDeliveryPurchaseRepository.save(luckyPickDeliveryPurchase);

        if(luckyPickDeliveryPurchase.getUsePoint() != null && luckyPickDeliveryPurchase.getUsePoint() > 0){
            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(luckyPickDeliveryPurchase.getMemberSeqNo());
            pointHistory.setType("used");
            pointHistory.setPoint(luckyPickDeliveryPurchase.getUsePoint().floatValue());
            pointHistory.setSubject("배송비 결제");
            pointService.updatePoint(luckyPickDeliveryPurchase.getMemberSeqNo(), pointHistory);
        }

        LuckyPickPurchaseItemOnly luckyPickPurchaseItem = luckyPickPurchaseItemOnlyRepository.findBySeqNo(luckyPickDeliveryPurchase.getLuckyPickPurchaseItemSeqNo());
        luckyPickPurchaseItem.setDeliveryPayStatus(2);
        luckyPickPurchaseItem.setDeliveryStatus(1);
        luckyPickPurchaseItem.setExchangeDatetime(paymentDate);
        luckyPickPurchaseItemOnlyRepository.save(luckyPickPurchaseItem);

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertLuckyPickReply(LuckyPickReplyOnly reply) throws ResultCodeException {

        reply.setReply(Filtering.filter(reply.getReply()));

        String dateStr = AppUtil.localDatetimeNowString();
        reply.setSeqNo(null);
        reply.setRegDatetime(dateStr);
        reply.setModDatetime(dateStr);
        reply.setStatus(1);
        reply = luckyPickReplyOnlyRepository.save(reply);
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateLuckyPickReply(LuckyPickReplyOnly reply) throws ResultCodeException {

        reply.setReply(Filtering.filter(reply.getReply()));

        String dateStr = AppUtil.localDatetimeNowString();
        reply.setModDatetime(dateStr);
        reply.setStatus(1);
        reply = luckyPickReplyOnlyRepository.save(reply);
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteLuckyPickReply(User user, Long seqNo) throws ResultCodeException {
        LuckyPickReplyOnly reply = luckyPickReplyOnlyRepository.findBySeqNo(seqNo);
        if(!reply.getMemberSeqNo().equals(user.getNo())){
            throw new NotPermissionException();
        }
        reply.setStatus(-999);
        luckyPickReplyOnlyRepository.save(reply);
        return Const.E_SUCCESS;
    }

    public Page<LuckyPickReply> getLuckyPickReplyListByLuckyPickPurchaseItemSeqNo(Long luckyPickPurchaseItemSeqNo, Pageable pageable){
        return luckyPickReplyRepository.findAllByLuckyPickPurchaseItemSeqNoAndStatusOrderBySeqNoAsc(luckyPickPurchaseItemSeqNo, 1, pageable);
    }

    public Page<LuckyPickReply> getLuckyPickReplyListByLuckyPickReviewSeqNo(Long luckyPickReviewSeqNo, Pageable pageable){
        return luckyPickReplyRepository.findAllByLuckyPickReviewSeqNoAndStatusOrderBySeqNoAsc(luckyPickReviewSeqNo, 1, pageable);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertLuckyPickReview(LuckyPickReview luckyPickReview) throws ResultCodeException {

        try {

            luckyPickReview.setReview(Filtering.filter(luckyPickReview.getReview()));

            List<LuckyPickReviewImage> imageList = luckyPickReview.getImageList();
            String dateStr = AppUtil.localDatetimeNowString();
            luckyPickReview.setSeqNo(null);
            luckyPickReview.setRegDatetime(dateStr);
            luckyPickReview.setModDatetime(dateStr);
            luckyPickReview.setStatus(1);
            luckyPickReview = luckyPickReviewRepository.save(luckyPickReview);

            if (imageList != null && imageList.size() > 0) {

                for (LuckyPickReviewImage image : imageList) {
                    image.setLuckyPickReviewSeqNo(luckyPickReview.getSeqNo());
                    image.setType("thumbnail");
                    luckyPickReviewImageRepository.save(image);
                }
            }

            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(luckyPickReview.getMemberSeqNo());
            pointHistory.setType("charge");
            pointHistory.setPoint(100f);
            pointHistory.setSubject("럭키픽 리뷰작성");
            pointService.updatePoint(luckyPickReview.getMemberSeqNo(), pointHistory);

            return Const.E_SUCCESS;
        } catch (Exception e) {
            logger.error(e.toString());
            throw new InvalidCashException();
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateLuckyPickReview(LuckyPickReview luckyPickReview) throws ResultCodeException {

        try {

            if (luckyPickReview.getSeqNo() == null) {
                throw new InvalidEventRevewException("eventReview.seq_no cannot be null");
            }

            luckyPickReview.setReview(Filtering.filter(luckyPickReview.getReview()));

            List<LuckyPickReviewImage> imageList = luckyPickReview.getImageList();
            String dateStr = AppUtil.localDatetimeNowString();
            luckyPickReview.setModDatetime(dateStr);
            luckyPickReview.setStatus(1);
            luckyPickReview = luckyPickReviewRepository.save(luckyPickReview);

            luckyPickReviewImageRepository.deleteAllByLuckyPickReviewSeqNo(luckyPickReview.getSeqNo());

            if (imageList != null && imageList.size() > 0) {

                for (LuckyPickReviewImage image : imageList) {
                    image.setLuckyPickReviewSeqNo(luckyPickReview.getSeqNo());
                    image.setType("thumbnail");
                    luckyPickReviewImageRepository.save(image);
                }
            }
            return Const.E_SUCCESS;
        } catch (Exception e) {
            logger.error(e.toString());
            throw new InvalidCashException();
        }
    }

    public Page<LuckyPickReviewDetail> getLuckyPickReviewList(User user, Pageable pageable){
        if(user == null){
            return luckyPickReviewDetailRepository.findAllBy(null, pageable);
        }
        return luckyPickReviewDetailRepository.findAllBy(user.getNo(), pageable);
    }

    public Page<LuckyPickReviewDetail> getMyLuckyPickReviewList(User user, Pageable pageable){
        return luckyPickReviewDetailRepository.findAllMy(user.getNo(), pageable);
    }

    public LuckyPickReviewDetail getLuckyPickReview(User user, Long seqNo){
        if(user == null){
            return luckyPickReviewDetailRepository.findBySeqNo(seqNo, null);
        }
        return luckyPickReviewDetailRepository.findBySeqNo(seqNo, user.getNo());
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deliveryComplete(){

        try {
            List<LuckyPickPurchaseItemOnly> itemList = luckyPickPurchaseItemOnlyRepository.findAllByDeliveryStatus(2);

            String dateStr = AppUtil.localDatetimeNowString();
            Map<String, String> params;
            String apiKey = commonService.getShippingKey();
            for(LuckyPickPurchaseItemOnly item : itemList){
                LuckyPickDelivery luckyPickDelivery = luckyPickDeliveryRepository.findBySeqNo(item.getLuckyPickDeliverySeqNo());
                if(AppUtil.isEmpty(luckyPickDelivery.getShippingCompanyCode())|| org.springframework.util.StringUtils.isEmpty(luckyPickDelivery.getTransportNumber())){
                    continue;
                }


                params = new HashMap<>();
                params.put("t_key", apiKey);
                params.put("t_code", luckyPickDelivery.getShippingCompanyCode());
                params.put("t_invoice", luckyPickDelivery.getTransportNumber());
                String res = getRequest(BASE_URL + "api/v1/trackingInfo", params, "UTF-8", 30000, 30000);
                logger.debug("tracking result = "+res);
                Gson gson = new Gson() ;
                JsonObject jsonObj = gson.fromJson (res, JsonElement.class).getAsJsonObject();

                JsonElement completeElement = jsonObj.get("completeYN");
                if(completeElement != null){
                    String completeYN = completeElement.getAsString();
                    if(completeYN.equals("Y")){
                        luckyPickDelivery.setDeliveryCompleteDatetime(dateStr);
                        luckyPickDeliveryRepository.save(luckyPickDelivery);

                        item.setDeliveryStatus(3);
                        item.setCompleteDatetime(dateStr);
                        luckyPickPurchaseItemOnlyRepository.save(item);
                    }
                }
            }
        }catch (Exception e){
            logger.error(e.toString());
        }

    }

    public String getRequest(String urlStr, Map<String, String> params, String charset, int connectionTimeout, int readTimeout) throws Exception {
        OutputStream os = null;
        HttpURLConnection conn = null;
        URL url = null;
        PrintWriter writer = null;

        if (params != null) {
            StringBuffer buf = new StringBuffer();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    buf.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), charset));
                    first = false;
                } else {
                    buf.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), charset));
                }
            }

            if (!first) {
                urlStr += "?" + buf.toString();
            }
        }

        logger.debug("url : "+urlStr);

        url = new URL(urlStr);
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(connectionTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setUseCaches(false);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
        StringBuffer sb = new StringBuffer();
        int read = 0;
        char[] buf = new char[1024];
        while ((read = br.read(buf)) > 0) {
            sb.append(buf, 0, read);
        }
        br.close();

        return sb.toString();
    }
}
