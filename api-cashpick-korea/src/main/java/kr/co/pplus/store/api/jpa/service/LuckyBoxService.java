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
import org.springframework.transaction.annotation.Isolation;
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
public class LuckyBoxService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(LuckyBoxService.class);

    @Autowired
    LuckyBoxRepository luckyBoxRepository;

    @Autowired
    LuckyBoxEntryRepository luckyBoxEntryRepository;

    @Autowired
    LuckyBoxProductGroupItemRepository luckyBoxProductGroupItemRepository;

    @Autowired
    LuckyBoxPurchaseRepository luckyBoxPurchaseRepository;

    @Autowired
    LuckyBoxPurchaseItemRepository luckyBoxPurchaseItemRepository;

    @Autowired
    LuckyBoxPurchaseItemCustomRepository luckyBoxPurchaseItemCustomRepository;

    @Autowired
    LuckyBoxPurchaseItemOnlyRepository luckyBoxPurchaseItemOnlyRepository;

    @Autowired
    LuckyBoxPurchaseWithItemRepository luckyBoxPurchaseWithItemRepository;

    @Autowired
    LuckyBoxPurchaseCancelRepository luckyBoxPurchaseCancelRepository;

    @Autowired
    LuckyBoxDeliveryPurchaseRepository luckyBoxDeliveryPurchaseRepository;

    @Autowired
    LuckyBoxDeliveryRepository luckyBoxDeliveryRepository;

    @Autowired
    LuckyBoxPurchaseItemOptionRepository luckyBoxPurchaseItemOptionRepository;

    @Autowired
    ProductDeliveryRepository productDeliveryRepository;

    @Autowired
    LuckyBoxReplyRepository luckyBoxReplyRepository;

    @Autowired
    LuckyBoxReplyOnlyRepository luckyBoxReplyOnlyRepository;

    @Autowired
    LuckyBoxReviewRepository luckyBoxReviewRepository;

    @Autowired
    LuckyBoxReviewDetailRepository luckyBoxReviewDetailRepository;

    @Autowired
    LuckyBoxReviewImageRepository luckyBoxReviewImageRepository;

    @Autowired
    private AmazonSQSSenderImpl amazonSQSSender;

    @Autowired
    ReapPayService reapPayService;

    @Autowired
    PointService pointService;

    @Autowired
    CashService cashService;

    @Autowired
    BolService bolService;

    @Autowired
    MemberService memberService;

    @Autowired
    ProductService productService;

    @Autowired
    CommonService commonService;

    @Value("${STORE.TYPE}")
    String storeType;

    private static final String BASE_URL = "http://info.sweettracker.co.kr/";

    public List<LuckyBox> getLuckyBoxList() {
        return luckyBoxRepository.findAllByStatusOrderByArrayAsc("active");
    }

    public Page<LuckyBoxProductGroupItem> getLuckyBoxProductList(String groupSeqNo, Pageable pageable) {
        String[] groupSeqNos = groupSeqNo.split(",");
        List<Long> groupList = new ArrayList<>();
        for (String seqNo : groupSeqNos) {
            groupList.add(Long.valueOf(seqNo));
        }
        return luckyBoxProductGroupItemRepository.findAllByLuckyBoxProductGroupSeqNoInOrderByPriceDesc(groupList, pageable);
    }

    public LuckyBoxPurchase getLuckyBoxPurchase(Long seqNo) {
        return luckyBoxPurchaseRepository.findBySeqNo(seqNo);
    }

    public LuckyBoxDeliveryPurchase getLuckyBoxDeliveryPurchase(Long seqNo) {
        return luckyBoxDeliveryPurchaseRepository.findBySeqNo(seqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public LuckyBoxPurchase saveLuckyBoxPurchase(LuckyBoxPurchase luckyBoxPurchase) throws ResultCodeException {

        if (luckyBoxPurchase.getUsePoint() == null) {
            luckyBoxPurchase.setUsePoint(0);
        }

        Member member = memberService.getMemberBySeqNo(luckyBoxPurchase.getMemberSeqNo());

        if (luckyBoxPurchase.getUsePoint() > 0 && luckyBoxPurchase.getUsePoint() > member.getPoint()) {
            throw new LackCostException("point lack");
        }

        if (luckyBoxPurchase.getUsePoint() > luckyBoxPurchase.getPrice()) {
            luckyBoxPurchase.setUsePoint(luckyBoxPurchase.getPrice().intValue());
            luckyBoxPurchase.setPgPrice(0);
        }

        luckyBoxPurchase.setPgPrice(luckyBoxPurchase.getPrice().intValue() - luckyBoxPurchase.getUsePoint());

        if (luckyBoxPurchase.getUsePoint() == luckyBoxPurchase.getPrice().intValue()) {
            luckyBoxPurchase.setPaymentMethod("point");
        }


        String dateStr = AppUtil.localDatetimeNowString();

        try {
            luckyBoxPurchase.setOrderNo(StoreUtil.getRandomOrderId());
        } catch (Exception e) {
            logger.error(e.toString());
        }

        luckyBoxPurchase.setCancelPrice(0f);
        luckyBoxPurchase.setCancelQuantity(0);
        luckyBoxPurchase.setStatus(1);
        luckyBoxPurchase.setRemainPrice(luckyBoxPurchase.getPrice());
        luckyBoxPurchase.setRegDatetime(dateStr);
        luckyBoxPurchase.setChangeStatusDatetime(dateStr);
        luckyBoxPurchase.setSalesType("delivery");
        if (luckyBoxPurchase.getPaymentMethod().equals("point")) {

            luckyBoxPurchase.setPaymentDatetime(dateStr);
            luckyBoxPurchase.setChangeStatusDatetime(dateStr);
            luckyBoxPurchase.setStatus(2);
        }

        luckyBoxPurchase = luckyBoxPurchaseRepository.save(luckyBoxPurchase);


        if (luckyBoxPurchase.getPaymentMethod().equals("point")) {

            LuckyBox luckyBox = luckyBoxRepository.findBySeqNo(luckyBoxPurchase.getLuckyBoxSeqNo());


            for (int i = 0; i < luckyBoxPurchase.getQuantity(); i++) {
                LuckyBoxPurchaseItemOnly luckyBoxPurchaseItem = new LuckyBoxPurchaseItemOnly();
                luckyBoxPurchaseItem.setLuckyBoxSeqNo(luckyBoxPurchase.getLuckyBoxSeqNo());
                luckyBoxPurchaseItem.setLuckyBoxPurchaseSeqNo(luckyBoxPurchase.getSeqNo());
                luckyBoxPurchaseItem.setMemberSeqNo(luckyBoxPurchase.getMemberSeqNo());
                luckyBoxPurchaseItem.setLuckyBoxTitle(luckyBoxPurchase.getTitle());
                luckyBoxPurchaseItem.setPaymentMethod(luckyBoxPurchase.getPaymentMethod());
                luckyBoxPurchaseItem.setPrice(luckyBoxPurchase.getUnitPrice());
                luckyBoxPurchaseItem.setStatus(2);
                luckyBoxPurchaseItem.setIsOpen(false);
                luckyBoxPurchaseItem.setRegDatetime(dateStr);
                luckyBoxPurchaseItem.setTempMember(false);
                luckyBoxPurchaseItem.setPaymentDatetime(dateStr);
                luckyBoxPurchaseItem.setRefundBol(luckyBox.getRefundBol());
                luckyBoxPurchaseItemOnlyRepository.save(luckyBoxPurchaseItem);
            }

            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(luckyBoxPurchase.getMemberSeqNo());
            pointHistory.setType("used");
            pointHistory.setPoint(luckyBoxPurchase.getPrice());
            pointHistory.setSubject(luckyBoxPurchase.getTitle() + " 결제");
            pointService.updatePoint(luckyBoxPurchase.getMemberSeqNo(), pointHistory);
        }


        return luckyBoxPurchase;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void paymentLuckyBoxPurchase(HttpServletRequest request) throws ResultCodeException {

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

            LuckyBoxPurchase luckyBoxPurchase = luckyBoxPurchaseRepository.findByOrderNo(orderSeq);
            if (luckyBoxPurchase == null || !luckyBoxPurchase.getStatus().equals(1)) {
                throw new NotPermissionException();
            }

            luckyBoxPurchase.setStatus(2);

            String paymentDate = appDt + " " + appTm;
            luckyBoxPurchase.setPaymentDatetime(paymentDate);
            luckyBoxPurchase.setChangeStatusDatetime(paymentDate);
            luckyBoxPurchase.setPayResponseApprovalNo(appNo);
            luckyBoxPurchase.setPayResponseCardId(issCd);
            luckyBoxPurchase.setPayResponseCardNm(issNm);
            luckyBoxPurchase.setPayResponseCardNo(cardNum);
            luckyBoxPurchase.setPayResponseCertYn(true);
            luckyBoxPurchase.setPayResponseCode(resultStatus);
            luckyBoxPurchase.setPayResponseInstallment(installment);
            luckyBoxPurchase.setPayResponseOrderNo(orderSeq);
            luckyBoxPurchase.setPayResponsePayDate(appDt);
            luckyBoxPurchase.setPayResponsePayTime(appTm);
            luckyBoxPurchase.setPayResponsePayType("card");
            luckyBoxPurchase.setPayResponseProductType("R");
            luckyBoxPurchase.setPayResponseSellMm(installment);
            if (storeType.equals("PROD")) {
                luckyBoxPurchase.setPayResponseTestYn(false);
            } else {
                luckyBoxPurchase.setPayResponseTestYn(true);
            }

            luckyBoxPurchase.setPayResponseTranSeq(tranSeq);
            luckyBoxPurchase.setPayResponseZerofeeYn(false);
            luckyBoxPurchase = luckyBoxPurchaseRepository.save(luckyBoxPurchase);

            LuckyBox luckyBox = luckyBoxRepository.findBySeqNo(luckyBoxPurchase.getLuckyBoxSeqNo());

            for (int i = 0; i < luckyBoxPurchase.getQuantity(); i++) {
                LuckyBoxPurchaseItemOnly luckyBoxPurchaseItem = new LuckyBoxPurchaseItemOnly();
                luckyBoxPurchaseItem.setLuckyBoxSeqNo(luckyBoxPurchase.getLuckyBoxSeqNo());
                luckyBoxPurchaseItem.setLuckyBoxPurchaseSeqNo(luckyBoxPurchase.getSeqNo());
                luckyBoxPurchaseItem.setMemberSeqNo(luckyBoxPurchase.getMemberSeqNo());
                luckyBoxPurchaseItem.setLuckyBoxTitle(luckyBoxPurchase.getTitle());
                luckyBoxPurchaseItem.setPaymentMethod(luckyBoxPurchase.getPaymentMethod());
                luckyBoxPurchaseItem.setPrice(luckyBoxPurchase.getUnitPrice());
                luckyBoxPurchaseItem.setStatus(2);
                luckyBoxPurchaseItem.setIsOpen(false);
                luckyBoxPurchaseItem.setRegDatetime(paymentDate);
                luckyBoxPurchaseItem.setTempMember(false);
                luckyBoxPurchaseItem.setPaymentDatetime(paymentDate);
                luckyBoxPurchaseItem.setLuckyBoxPayResponseTranSeq(tranSeq);
                luckyBoxPurchaseItem.setRefundBol(luckyBox.getRefundBol());
                luckyBoxPurchaseItemOnlyRepository.save(luckyBoxPurchaseItem);
            }

            if (luckyBoxPurchase.getUsePoint() > 0) {
                PointHistory pointHistory = new PointHistory();
                pointHistory.setMemberSeqNo(luckyBoxPurchase.getMemberSeqNo());
                pointHistory.setType("used");
                pointHistory.setPoint(luckyBoxPurchase.getUsePoint().floatValue());
                pointHistory.setSubject(luckyBoxPurchase.getTitle() + " 결제");
                pointService.updatePoint(luckyBoxPurchase.getMemberSeqNo(), pointHistory);
            }
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void paymentBillkeyLuckyBoxPurchase(Long seqNo, String token, String installment) throws ResultCodeException {

        LuckyBoxPurchase luckyBoxPurchase = luckyBoxPurchaseRepository.findBySeqNo(seqNo);

        if (luckyBoxPurchase == null || !luckyBoxPurchase.getStatus().equals(1)) {
            throw new NotPermissionException();
        }

        if(luckyBoxPurchase.getPgPrice() == null){
            luckyBoxPurchase.setPgPrice(luckyBoxPurchase.getPrice().intValue());
        }

        ReapPayBillKeyData reapPayBillKeyData = reapPayService.billkeypay(null, luckyBoxPurchase, null, null, null, token, installment);

        if (reapPayBillKeyData == null) {
            throw new InvalidBuyException();
        }

        luckyBoxPurchase.setStatus(2);

        String paymentDate = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = sdf.parse(reapPayBillKeyData.getBillkeytradeDateTime());

            paymentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } catch (Exception e) {
            logger.error(e.toString());
            paymentDate = AppUtil.localDatetimeNowString();
        }


        luckyBoxPurchase.setPaymentDatetime(paymentDate);
        luckyBoxPurchase.setChangeStatusDatetime(paymentDate);
        luckyBoxPurchase.setPayResponseApprovalNo(reapPayBillKeyData.getBillkeyapprovalNumb());
        luckyBoxPurchase.setPayResponseCardId(reapPayBillKeyData.getBillkeypurchaseCardType());
        luckyBoxPurchase.setPayResponseCardNm(reapPayBillKeyData.getBillkeypurchaseCardName());
        luckyBoxPurchase.setPayResponseCardNo(reapPayBillKeyData.getBillkeymaskedCardNumb());
        luckyBoxPurchase.setPayResponseCertYn(true);
        luckyBoxPurchase.setPayResponseCode(reapPayBillKeyData.getBillkeyrespCode());
        luckyBoxPurchase.setPayResponseInstallment(reapPayBillKeyData.getBillkeyinstallment());
        luckyBoxPurchase.setPayResponseOrderNo(luckyBoxPurchase.getOrderNo());
        luckyBoxPurchase.setPayResponsePayDate(paymentDate.split(" ")[0]);
        luckyBoxPurchase.setPayResponsePayTime(paymentDate.split(" ")[1]);
        luckyBoxPurchase.setPayResponsePayType("easy");
        luckyBoxPurchase.setPayResponseProductType("R");
        luckyBoxPurchase.setPayResponseSellMm(reapPayBillKeyData.getBillkeyinstallment());
        if (storeType.equals("PROD")) {
            luckyBoxPurchase.setPayResponseTestYn(false);
        } else {
            luckyBoxPurchase.setPayResponseTestYn(true);
        }

        luckyBoxPurchase.setPayResponseTranSeq(reapPayBillKeyData.getBillkeyTranseq());
        luckyBoxPurchase.setPayResponseZerofeeYn(false);
        luckyBoxPurchase = luckyBoxPurchaseRepository.save(luckyBoxPurchase);

        LuckyBox luckyBox = luckyBoxRepository.findBySeqNo(luckyBoxPurchase.getLuckyBoxSeqNo());

        for (int i = 0; i < luckyBoxPurchase.getQuantity(); i++) {
            LuckyBoxPurchaseItemOnly luckyBoxPurchaseItem = new LuckyBoxPurchaseItemOnly();
            luckyBoxPurchaseItem.setLuckyBoxSeqNo(luckyBoxPurchase.getLuckyBoxSeqNo());
            luckyBoxPurchaseItem.setLuckyBoxPurchaseSeqNo(luckyBoxPurchase.getSeqNo());
            luckyBoxPurchaseItem.setMemberSeqNo(luckyBoxPurchase.getMemberSeqNo());
            luckyBoxPurchaseItem.setLuckyBoxTitle(luckyBoxPurchase.getTitle());
            luckyBoxPurchaseItem.setPaymentMethod(luckyBoxPurchase.getPaymentMethod());
            luckyBoxPurchaseItem.setPrice(luckyBoxPurchase.getUnitPrice());
            luckyBoxPurchaseItem.setStatus(2);
            luckyBoxPurchaseItem.setIsOpen(false);
            luckyBoxPurchaseItem.setRegDatetime(paymentDate);
            luckyBoxPurchaseItem.setTempMember(false);
            luckyBoxPurchaseItem.setPaymentDatetime(paymentDate);
            luckyBoxPurchaseItem.setLuckyBoxPayResponseTranSeq(reapPayBillKeyData.getBillkeyTranseq());
            luckyBoxPurchaseItem.setRefundBol(luckyBox.getRefundBol());
            luckyBoxPurchaseItemOnlyRepository.save(luckyBoxPurchaseItem);
        }

        if (luckyBoxPurchase.getUsePoint() > 0) {
            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(luckyBoxPurchase.getMemberSeqNo());
            pointHistory.setType("used");
            pointHistory.setPoint(luckyBoxPurchase.getUsePoint().floatValue());
            pointHistory.setSubject(luckyBoxPurchase.getTitle() + " 결제");
            pointService.updatePoint(luckyBoxPurchase.getMemberSeqNo(), pointHistory);
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void cancelLuckyBox(User user, Long luckyBoxPurchaseSeqNo) throws ResultCodeException {

        String dateStr = AppUtil.localDatetimeNowString();

        LuckyBoxPurchase luckyBoxPurchase = luckyBoxPurchaseRepository.findBySeqNo(luckyBoxPurchaseSeqNo);
        List<LuckyBoxPurchaseItemOnly> itemList = luckyBoxPurchaseItemOnlyRepository.findAllByLuckyBoxPurchaseSeqNo(luckyBoxPurchaseSeqNo);
        if (!user.getNo().equals(luckyBoxPurchase.getMemberSeqNo())) {
            throw new NotPermissionException();
        }

        if (luckyBoxPurchase.getStatus() != 2) {
            throw new NotPermissionException();
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            calendar.setTime(sdf.parse(luckyBoxPurchase.getPaymentDatetime()));

        } catch (ParseException e) {
            logger.error(e.toString());
            throw new UnknownException();
        }

        calendar.add(Calendar.DAY_OF_MONTH, 7);

        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
            throw new ExpiredException();
        }

        for (LuckyBoxPurchaseItemOnly item : itemList) {
            if (item.getIsOpen()) {
                throw new NotPermissionException();
            }
        }

        if (luckyBoxPurchase.getPaymentMethod().equals("point")) {
            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(luckyBoxPurchase.getMemberSeqNo());
            pointHistory.setType("charge");
            pointHistory.setPoint(luckyBoxPurchase.getPrice());
            pointHistory.setSubject(luckyBoxPurchase.getTitle() + " 구매 취소");
            pointService.updatePoint(luckyBoxPurchase.getMemberSeqNo(), pointHistory);

            for (LuckyBoxPurchaseItemOnly item : itemList) {
                item.setStatus(3);
                item.setCancelDatetime(dateStr);
            }

        } else {
            long seqNo = 1L;
            if (luckyBoxPurchase.getPaymentMethod().equals("card")) {
                seqNo = 1L;
            } else if (luckyBoxPurchase.getPaymentMethod().equals("easy")) {
                seqNo = 2L;
            }

            ReapPayCancelData data = reapPayService.cancel(luckyBoxPurchase.getPayResponseTranSeq(), seqNo);
            if (data == null) {
                throw new CancelFailException();
            }

            for (LuckyBoxPurchaseItemOnly item : itemList) {
                item.setStatus(3);
                item.setCancelDatetime(dateStr);

                LuckyBoxPurchaseCancel cancel = new LuckyBoxPurchaseCancel();
                cancel.setLuckyBoxPurchaseSeqNo(item.getLuckyBoxPurchaseSeqNo());
                cancel.setLuckyBoxPurchaseItemSeqNo(item.getSeqNo());
                cancel.setPayResponseCode(data.getPayResponseCode());
                cancel.setPayResponseMsg(data.getPayResponseMsg());
                cancel.setPayResponsePayDate(data.getPayResponsePayDate());
                cancel.setPayResponsePayTime(data.getPayResponsePayTime());
                cancel.setPayResponseAmt(data.getPayResponseAmt());
//            cancel.setPayResponsePgSeq(data.getPayResponsePgSeq());

                cancel.setPayResponseApprovalYMDHMS(data.getPayResponseApprovalYMDHMS());
                cancel.setPayResponseApprovalNo(data.getPayResponseApprovalNo());

                cancel.setPayResponseTranSeq(data.getPayResponseTranSeq());
                luckyBoxPurchaseCancelRepository.save(cancel);
            }

            if (luckyBoxPurchase.getUsePoint() > 0) {
                PointHistory pointHistory = new PointHistory();
                pointHistory.setMemberSeqNo(luckyBoxPurchase.getMemberSeqNo());
                pointHistory.setType("charge");
                pointHistory.setPoint(luckyBoxPurchase.getUsePoint().floatValue());
                pointHistory.setSubject(luckyBoxPurchase.getTitle() + " 구매 취소");
                pointService.updatePoint(luckyBoxPurchase.getMemberSeqNo(), pointHistory);
            }
        }

        luckyBoxPurchase.setStatus(3);
        luckyBoxPurchase.setCancelQuantity(luckyBoxPurchase.getQuantity());
        luckyBoxPurchase.setCancelPrice(luckyBoxPurchase.getPrice());
        luckyBoxPurchase.setChangeStatusDatetime(dateStr);
        luckyBoxPurchase.setRemainPrice(0f);
        luckyBoxPurchaseRepository.save(luckyBoxPurchase);

        luckyBoxPurchaseItemOnlyRepository.saveAll(itemList);

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void expiredLuckyBoxPurchaseItem() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String expireDate = sdf.format(calendar.getTime());

        String dateStr = AppUtil.localDatetimeNowString();
        List<LuckyBoxPurchaseItemOnly> list = luckyBoxPurchaseItemOnlyRepository.findAllByStatusAndIsOpenAndOpenDatetimeLessThanEqualAndDeliveryStatusIsNull(2, true, expireDate);
        for (LuckyBoxPurchaseItemOnly item : list) {
            item.setStatus(4);
            item.setExchangeDatetime(dateStr);
            item = luckyBoxPurchaseItemOnlyRepository.save(item);

            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(item.getMemberSeqNo());
            pointHistory.setType("charge");
            pointHistory.setPoint(item.getPrice());
            pointHistory.setSubject("랜덤박스 포인트 환급");
            pointService.updatePoint(item.getMemberSeqNo(), pointHistory);

        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void cashBackItem(User user, Long luckyBoxPurchaseItemSeqNo, String type) throws ResultCodeException {
        LuckyBoxPurchaseItemOnly luckyBoxPurchaseItem = luckyBoxPurchaseItemOnlyRepository.findBySeqNo(luckyBoxPurchaseItemSeqNo);

        if (luckyBoxPurchaseItem == null) {
            throw new NotFoundException();
        }

        if (!user.getNo().equals(luckyBoxPurchaseItem.getMemberSeqNo())) {
            throw new NotPermissionException();
        }

        if (luckyBoxPurchaseItem.getStatus() != 2 || !luckyBoxPurchaseItem.getIsOpen() || luckyBoxPurchaseItem.getDeliveryStatus() != null) {
            throw new NotPermissionException();
        }


        luckyBoxPurchaseItem.setExchangeDatetime(AppUtil.localDatetimeNowString());



        if(type.equals("point")){
            luckyBoxPurchaseItem.setStatus(4);
            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(luckyBoxPurchaseItem.getMemberSeqNo());
            pointHistory.setType("charge");
            pointHistory.setPoint(luckyBoxPurchaseItem.getPrice());
            pointHistory.setSubject("랜덤박스 포인트 교환");
            pointService.updatePoint(luckyBoxPurchaseItem.getMemberSeqNo(), pointHistory);
        }else if(type.equals("cash")){
            luckyBoxPurchaseItem.setStatus(4);
            CashHistory cashHistory = new CashHistory();
            cashHistory.setMemberSeqNo(luckyBoxPurchaseItem.getMemberSeqNo());
            cashHistory.setType("charge");
            cashHistory.setSecondaryType("member");
            cashHistory.setCash(luckyBoxPurchaseItem.getPrice());
            cashHistory.setSubject("럭키박스 캐시 교환");
            cashService.updateCash(luckyBoxPurchaseItem.getMemberSeqNo(), cashHistory);
        }else if(type.equals("bol")){
            luckyBoxPurchaseItem.setStatus(5);
            BolHistory bolHistory = new BolHistory();
            bolHistory.setAmount(luckyBoxPurchaseItem.getRefundBol().floatValue());
            bolHistory.setMemberSeqNo(luckyBoxPurchaseItem.getMemberSeqNo());
            bolHistory.setSubject("랜덤박스 럭키볼 교환");
            bolHistory.setPrimaryType("increase");
            bolHistory.setSecondaryType("exchange");
            bolHistory.setTargetType("member");
            bolHistory.setTargetSeqNo(luckyBoxPurchaseItem.getMemberSeqNo());
            bolService.increaseBol(luckyBoxPurchaseItem.getMemberSeqNo(), bolHistory);
        }

        luckyBoxPurchaseItem = luckyBoxPurchaseItemOnlyRepository.save(luckyBoxPurchaseItem);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void updateImpression(User user, Long luckyBoxPurchaseItemSeqNo, String impression) throws ResultCodeException {
        LuckyBoxPurchaseItemOnly luckyBoxPurchaseItem = luckyBoxPurchaseItemOnlyRepository.findBySeqNo(luckyBoxPurchaseItemSeqNo);

        if (luckyBoxPurchaseItem == null) {
            throw new NotFoundException();
        }

        if (!user.getNo().equals(luckyBoxPurchaseItem.getMemberSeqNo())) {
            throw new NotPermissionException();
        }

        luckyBoxPurchaseItem.setImpression(impression);
        luckyBoxPurchaseItemOnlyRepository.save(luckyBoxPurchaseItem);
    }

    public Integer getCountNotOpenLuckyBoxPurchaseItem(Session session) {
        return luckyBoxPurchaseItemRepository.countByMemberSeqNoAndStatusAndIsOpen(session.getNo(), 2, false);
    }

    public List<LuckyBoxPurchaseItem> getNotOpenLuckyBoxPurchaseItemList(Session session) {
        return luckyBoxPurchaseItemRepository.findAllByMemberSeqNoAndStatusAndIsOpenOrderBySeqNoDesc(session.getNo(), 2, false);
    }

    public List<LuckyBoxPurchaseItem> getNotOpenLuckyBoxPurchaseItemListByLuckyBoxPurchaseSeqNo(Session session, Long luckyBoxPurchaseSeqNo) {
        return luckyBoxPurchaseItemRepository.findAllByMemberSeqNoAndStatusAndIsOpenAndLuckyBoxPurchaseSeqNoOrderBySeqNoDesc(session.getNo(), 2, false, luckyBoxPurchaseSeqNo);
    }

    public Boolean isCancelableLuckyBox(Long luckyBoxPurchaseSeqNo) {
        Integer count = luckyBoxPurchaseItemRepository.countByLuckyBoxDeliveryPurchaseSeqNoAndIsOpen(luckyBoxPurchaseSeqNo, true);
        return (count == 0);
    }

    public List<LuckyBoxPurchaseWithItem> getNotOpenLuckyBoxPurchaseList(Session session) {
        return luckyBoxPurchaseWithItemRepository.findAllBy(session.getNo());
    }

    public Page<LuckyBoxPurchaseItem> getOpenLuckyBoxPurchaseItemList(Session session, Pageable pageable) {

        List<Integer> statusList = new ArrayList<>();
        statusList.add(2);
        statusList.add(4);

        return luckyBoxPurchaseItemRepository.findAllByMemberSeqNoAndStatusInAndIsOpenOrderByOpenDatetimeDesc(session.getNo(), statusList, true, pageable);

    }

    public Page<LuckyBoxPurchaseItem> getTotalLuckyBoxPurchaseItemList(Pageable pageable) {

        List<Integer> statusList = new ArrayList<>();
        statusList.add(2);
        return luckyBoxPurchaseItemRepository.findAllByStatusInAndIsOpenAndSeqNoGreaterThanEqualAndDeliveryStatusIsNotNullOrderBySeqNoDesc(statusList, true, 112L, pageable);

    }

    public Page<LuckyBoxPurchaseItem> getTotalLuckyPurchaseItemList(Pageable pageable) {

        List<Integer> statusList = new ArrayList<>();
        statusList.add(2);
        return luckyBoxPurchaseItemRepository.findAllByStatusInAndIsOpenAndSeqNoGreaterThanEqualAndDeliveryStatusIsNotNullOrderBySeqNoDesc(statusList, true, 112L, pageable);

    }

    public void openLuckyBoxPurchaseItem(SqsModel model) {
        try {
            amazonSQSSender.sendMessage(model);
        } catch (JsonProcessingException e) {
            logger.error(e.toString());
        }
    }

    public LuckyBoxPurchaseItem confirmLuckyBoxPurchaseItem(Long luckyBoxPurchaseItemSeqNo) {
        LuckyBoxPurchaseItem luckyBoxPurchaseItem = luckyBoxPurchaseItemRepository.findBySeqNo(luckyBoxPurchaseItemSeqNo);
        if (luckyBoxPurchaseItem != null && luckyBoxPurchaseItem.getIsOpen()) {
            return luckyBoxPurchaseItem;
        }
        return null;
    }

    public LuckyBoxPurchaseItem getLuckyBoxPurchaseItem(Long seqNo) {
        return luckyBoxPurchaseItemRepository.findBySeqNo(seqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public LuckyBoxDeliveryPurchase saveLuckyBoxDeliveryPurchase(LuckyBoxDeliveryPurchase luckyBoxDeliveryPurchase) throws ResultCodeException {

        if (luckyBoxDeliveryPurchase.getUsePoint() == null) {
            luckyBoxDeliveryPurchase.setUsePoint(0);
            luckyBoxDeliveryPurchase.setPgPrice(luckyBoxDeliveryPurchase.getPrice().intValue());
        }

        LuckyBoxPurchaseItemOption luckyBoxPurchaseItemOption = luckyBoxDeliveryPurchase.getSelectOption();
        LuckyBoxDelivery luckyBoxDelivery = luckyBoxDeliveryPurchase.getSelectDelivery();


        if (luckyBoxPurchaseItemOption != null) {
            luckyBoxPurchaseItemOption = luckyBoxPurchaseItemOptionRepository.save(luckyBoxPurchaseItemOption);
        }

        luckyBoxDelivery = luckyBoxDeliveryRepository.save(luckyBoxDelivery);

        String dateStr = AppUtil.localDatetimeNowString();

        try {
            luckyBoxDeliveryPurchase.setOrderNo(StoreUtil.getRandomOrderId());
        } catch (Exception e) {
            logger.error(e.toString());
        }
        luckyBoxDeliveryPurchase.setRegDatetime(dateStr);

        LuckyBoxPurchaseItemOnly luckyBoxPurchaseItem = luckyBoxPurchaseItemOnlyRepository.findBySeqNo(luckyBoxDeliveryPurchase.getLuckyBoxPurchaseItemSeqNo());

        if(luckyBoxPurchaseItem.getStatus() != 2){
            throw new NotPermissionException();
        }

        List<ProductDelivery> productDeliveryList = productDeliveryRepository.findAllByProductSeqNo(luckyBoxPurchaseItem.getProductSeqNo());
        if (productDeliveryList != null && productDeliveryList.size() > 0) {
            luckyBoxPurchaseItem.setProductDeliverySeqNo(productDeliveryList.get(0).getSeqNo());
        }

        luckyBoxPurchaseItem.setDeliveryFee((luckyBoxDelivery.getDeliveryFee() + luckyBoxDelivery.getDeliveryAddFee1() + luckyBoxDelivery.getDeliveryAddFee2()));

        if (luckyBoxPurchaseItemOption != null) {
            luckyBoxPurchaseItem.setOptionPrice(luckyBoxPurchaseItemOption.getPrice());
            String depth = luckyBoxPurchaseItemOption.getDepth1();
            if (!AppUtil.isEmpty(luckyBoxPurchaseItemOption.getDepth2())) {
                depth += ("/" + luckyBoxPurchaseItemOption.getDepth2());
            }
            luckyBoxPurchaseItem.setOptionName(depth);
        }

        luckyBoxPurchaseItem.setDeliveryPaymentPrice(luckyBoxDeliveryPurchase.getPrice());
        luckyBoxPurchaseItem.setLuckyboxDeliverySeqNo(luckyBoxDelivery.getSeqNo());
        luckyBoxPurchaseItem.setExchangeDatetime(dateStr);

        if (luckyBoxDeliveryPurchase.getUsePoint() > 0) {
            Member member = memberService.getMemberBySeqNo(luckyBoxDeliveryPurchase.getMemberSeqNo());
            if (luckyBoxDeliveryPurchase.getUsePoint() > member.getPoint()) {
                throw new LackCostException();
            }
        }

        if (luckyBoxDeliveryPurchase.getPaymentMethod().equals("point") || luckyBoxDeliveryPurchase.getPrice().intValue() == luckyBoxDeliveryPurchase.getUsePoint()) {

            luckyBoxDeliveryPurchase.setPaymentMethod("point");
            luckyBoxDeliveryPurchase.setPaymentDatetime(dateStr);


            luckyBoxDeliveryPurchase.setStatus(2);
//            luckyBoxDeliveryPurchase.setPaymentDatetime(dateStr);

            luckyBoxPurchaseItem.setDeliveryPayStatus(2);
            luckyBoxPurchaseItem.setDeliveryStatus(0);
            luckyBoxPurchaseItem.setExchangeDatetime(dateStr);

            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(luckyBoxDeliveryPurchase.getMemberSeqNo());
            pointHistory.setType("used");
            pointHistory.setPoint(luckyBoxDeliveryPurchase.getPrice());
            pointHistory.setSubject("배송비 결제");
            pointService.updatePoint(luckyBoxDeliveryPurchase.getMemberSeqNo(), pointHistory);

        } else {
            luckyBoxDeliveryPurchase.setStatus(1);
            luckyBoxPurchaseItem.setDeliveryPayStatus(1);
        }
        luckyBoxDeliveryPurchase = luckyBoxDeliveryPurchaseRepository.save(luckyBoxDeliveryPurchase);
        luckyBoxPurchaseItem.setLuckyBoxDeliveryPurchaseSeqNo(luckyBoxDeliveryPurchase.getSeqNo());
        luckyBoxPurchaseItem = luckyBoxPurchaseItemOnlyRepository.save(luckyBoxPurchaseItem);

        return luckyBoxDeliveryPurchase;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void paymentLuckyBoxDeliveryPurchase(HttpServletRequest request) throws ResultCodeException {

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

            LuckyBoxDeliveryPurchase luckyBoxDeliveryPurchase = luckyBoxDeliveryPurchaseRepository.findByOrderNo(orderSeq);

            if (luckyBoxDeliveryPurchase == null || !luckyBoxDeliveryPurchase.getStatus().equals(1)) {
                throw new NotPermissionException();
            }

            luckyBoxDeliveryPurchase.setStatus(2);

            String paymentDate = appDt + " " + appTm;
            luckyBoxDeliveryPurchase.setPaymentDatetime(paymentDate);
            luckyBoxDeliveryPurchase.setPayResponseApprovalNo(appNo);
            luckyBoxDeliveryPurchase.setPayResponseCardId(issCd);
            luckyBoxDeliveryPurchase.setPayResponseCardNm(issNm);
            luckyBoxDeliveryPurchase.setPayResponseCardNo(cardNum);
            luckyBoxDeliveryPurchase.setPayResponseCertYn(true);
            luckyBoxDeliveryPurchase.setPayResponseCode(resultStatus);
            luckyBoxDeliveryPurchase.setPayResponseInstallment(installment);
            luckyBoxDeliveryPurchase.setPayResponseOrderNo(orderSeq);
            luckyBoxDeliveryPurchase.setPayResponsePayDate(appDt);
            luckyBoxDeliveryPurchase.setPayResponsePayTime(appTm);
            luckyBoxDeliveryPurchase.setPayResponsePayType("card");
            luckyBoxDeliveryPurchase.setPayResponseProductType("R");
            luckyBoxDeliveryPurchase.setPayResponseSellMm(installment);
            if (storeType.equals("PROD")) {
                luckyBoxDeliveryPurchase.setPayResponseTestYn(false);
            } else {
                luckyBoxDeliveryPurchase.setPayResponseTestYn(true);
            }

            luckyBoxDeliveryPurchase.setPayResponseTranSeq(tranSeq);
            luckyBoxDeliveryPurchase.setPayResponseZerofeeYn(false);
            luckyBoxDeliveryPurchase = luckyBoxDeliveryPurchaseRepository.save(luckyBoxDeliveryPurchase);

            if (luckyBoxDeliveryPurchase.getUsePoint() != null && luckyBoxDeliveryPurchase.getUsePoint() > 0) {
                PointHistory pointHistory = new PointHistory();
                pointHistory.setMemberSeqNo(luckyBoxDeliveryPurchase.getMemberSeqNo());
                pointHistory.setType("used");
                pointHistory.setPoint(luckyBoxDeliveryPurchase.getUsePoint().floatValue());
                pointHistory.setSubject("배송비 결제");
                pointService.updatePoint(luckyBoxDeliveryPurchase.getMemberSeqNo(), pointHistory);
            }

            LuckyBoxPurchaseItemOnly luckyBoxPurchaseItem = luckyBoxPurchaseItemOnlyRepository.findBySeqNo(luckyBoxDeliveryPurchase.getLuckyBoxPurchaseItemSeqNo());
            luckyBoxPurchaseItem.setDeliveryPayStatus(2);
            luckyBoxPurchaseItem.setDeliveryStatus(0);
            luckyBoxPurchaseItem.setExchangeDatetime(paymentDate);
            luckyBoxPurchaseItemOnlyRepository.save(luckyBoxPurchaseItem);
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void paymentBillkeyLuckyBoxDeliveryPurchase(Long seqNo, String token, String installment) throws ResultCodeException {

        LuckyBoxDeliveryPurchase luckyBoxDeliveryPurchase = luckyBoxDeliveryPurchaseRepository.findBySeqNo(seqNo);

        if (luckyBoxDeliveryPurchase == null || !luckyBoxDeliveryPurchase.getStatus().equals(1)) {
            throw new NotPermissionException();
        }

        ReapPayBillKeyData reapPayBillKeyData = reapPayService.billkeypay(null, null, luckyBoxDeliveryPurchase, null, null, token, installment);

        if (reapPayBillKeyData == null) {
            throw new InvalidBuyException();
        }

        luckyBoxDeliveryPurchase.setStatus(2);

        String paymentDate = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = sdf.parse(reapPayBillKeyData.getBillkeytradeDateTime());

            paymentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } catch (Exception e) {
            logger.error(e.toString());
            paymentDate = AppUtil.localDatetimeNowString();
        }

        luckyBoxDeliveryPurchase.setPaymentDatetime(paymentDate);
        luckyBoxDeliveryPurchase.setPayResponseApprovalNo(reapPayBillKeyData.getBillkeyapprovalNumb());
        luckyBoxDeliveryPurchase.setPayResponseCardId(reapPayBillKeyData.getBillkeypurchaseCardType());
        luckyBoxDeliveryPurchase.setPayResponseCardNm(reapPayBillKeyData.getBillkeyissuerCardName());
        luckyBoxDeliveryPurchase.setPayResponseCardNo(reapPayBillKeyData.getBillkeymaskedCardNumb());
        luckyBoxDeliveryPurchase.setPayResponseCertYn(true);
        luckyBoxDeliveryPurchase.setPayResponseCode(reapPayBillKeyData.getBillkeyrespCode());
        luckyBoxDeliveryPurchase.setPayResponseInstallment(installment);
        luckyBoxDeliveryPurchase.setPayResponseOrderNo(luckyBoxDeliveryPurchase.getOrderNo());
        luckyBoxDeliveryPurchase.setPayResponsePayDate(paymentDate.split(" ")[0]);
        luckyBoxDeliveryPurchase.setPayResponsePayTime(paymentDate.split(" ")[1]);
        luckyBoxDeliveryPurchase.setPayResponsePayType("card");
        luckyBoxDeliveryPurchase.setPayResponseProductType("R");
        luckyBoxDeliveryPurchase.setPayResponseSellMm(installment);
        if (storeType.equals("PROD")) {
            luckyBoxDeliveryPurchase.setPayResponseTestYn(false);
        } else {
            luckyBoxDeliveryPurchase.setPayResponseTestYn(true);
        }

        luckyBoxDeliveryPurchase.setPayResponseTranSeq(reapPayBillKeyData.getBillkeyTranseq());
        luckyBoxDeliveryPurchase.setPayResponseZerofeeYn(false);
        luckyBoxDeliveryPurchase = luckyBoxDeliveryPurchaseRepository.save(luckyBoxDeliveryPurchase);

        if (luckyBoxDeliveryPurchase.getUsePoint() != null && luckyBoxDeliveryPurchase.getUsePoint() > 0) {
            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(luckyBoxDeliveryPurchase.getMemberSeqNo());
            pointHistory.setType("used");
            pointHistory.setPoint(luckyBoxDeliveryPurchase.getUsePoint().floatValue());
            pointHistory.setSubject("배송비 결제");
            pointService.updatePoint(luckyBoxDeliveryPurchase.getMemberSeqNo(), pointHistory);
        }

        LuckyBoxPurchaseItemOnly luckyBoxPurchaseItem = luckyBoxPurchaseItemOnlyRepository.findBySeqNo(luckyBoxDeliveryPurchase.getLuckyBoxPurchaseItemSeqNo());
        luckyBoxPurchaseItem.setDeliveryPayStatus(2);
        luckyBoxPurchaseItem.setDeliveryStatus(0);
        luckyBoxPurchaseItem.setExchangeDatetime(paymentDate);
        luckyBoxPurchaseItemOnlyRepository.save(luckyBoxPurchaseItem);

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertLuckyBoxReply(LuckyBoxReplyOnly reply) throws ResultCodeException {

        reply.setReply(Filtering.filter(reply.getReply()));

        String dateStr = AppUtil.localDatetimeNowString();
        reply.setSeqNo(null);
        reply.setRegDatetime(dateStr);
        reply.setModDatetime(dateStr);
        reply.setStatus(1);
        reply = luckyBoxReplyOnlyRepository.save(reply);
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateLuckyBoxReply(LuckyBoxReplyOnly reply) throws ResultCodeException {

        reply.setReply(Filtering.filter(reply.getReply()));

        String dateStr = AppUtil.localDatetimeNowString();
        reply.setModDatetime(dateStr);
        reply.setStatus(1);
        reply = luckyBoxReplyOnlyRepository.save(reply);
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteLuckyBoxReply(User user, Long seqNo) throws ResultCodeException {
        LuckyBoxReplyOnly reply = luckyBoxReplyOnlyRepository.findBySeqNo(seqNo);
        if (!reply.getMemberSeqNo().equals(user.getNo())) {
            throw new NotPermissionException();
        }
        reply.setStatus(-999);
        luckyBoxReplyOnlyRepository.save(reply);
        return Const.E_SUCCESS;
    }

    public Page<LuckyBoxReply> getLuckyBoxReplyListByLuckyBoxPurchaseItemSeqNo(Long luckyBoxPurchaseItemSeqNo, Pageable pageable) {
        return luckyBoxReplyRepository.findAllByLuckyBoxPurchaseItemSeqNoAndStatusOrderBySeqNoAsc(luckyBoxPurchaseItemSeqNo, 1, pageable);
    }

    public Page<LuckyBoxReply> getLuckyBoxReplyListByLuckyBoxReviewSeqNo(Long luckyBoxReviewSeqNo, Pageable pageable) {
        return luckyBoxReplyRepository.findAllByLuckyBoxReviewSeqNoAndStatusOrderBySeqNoAsc(luckyBoxReviewSeqNo, 1, pageable);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertLuckyBoxReview(LuckyBoxReview luckyBoxReview) throws ResultCodeException {

        try {

            luckyBoxReview.setReview(Filtering.filter(luckyBoxReview.getReview()));

            List<LuckyBoxReviewImage> imageList = luckyBoxReview.getImageList();
            String dateStr = AppUtil.localDatetimeNowString();
            luckyBoxReview.setSeqNo(null);
            luckyBoxReview.setRegDatetime(dateStr);
            luckyBoxReview.setModDatetime(dateStr);
            luckyBoxReview.setStatus(1);
            luckyBoxReview = luckyBoxReviewRepository.save(luckyBoxReview);

            if (imageList != null && imageList.size() > 0) {

                for (LuckyBoxReviewImage image : imageList) {
                    image.setLuckyBoxReviewSeqNo(luckyBoxReview.getSeqNo());
                    image.setType("thumbnail");
                    luckyBoxReviewImageRepository.save(image);
                }
            }

            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(luckyBoxReview.getMemberSeqNo());
            pointHistory.setType("charge");
            pointHistory.setPoint(100f);
            pointHistory.setSubject("럭키박스 리뷰작성");
            pointService.updatePoint(luckyBoxReview.getMemberSeqNo(), pointHistory);

            return Const.E_SUCCESS;
        } catch (Exception e) {
            logger.error(e.toString());
            throw new InvalidCashException();
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateLuckyBoxReview(LuckyBoxReview luckyBoxReview) throws ResultCodeException {

        try {

            if (luckyBoxReview.getSeqNo() == null) {
                throw new InvalidEventRevewException("eventReview.seq_no cannot be null");
            }

            luckyBoxReview.setReview(Filtering.filter(luckyBoxReview.getReview()));

            List<LuckyBoxReviewImage> imageList = luckyBoxReview.getImageList();
            String dateStr = AppUtil.localDatetimeNowString();
            luckyBoxReview.setModDatetime(dateStr);
            luckyBoxReview.setStatus(1);
            luckyBoxReview = luckyBoxReviewRepository.save(luckyBoxReview);

            luckyBoxReviewImageRepository.deleteAllByLuckyBoxReviewSeqNo(luckyBoxReview.getSeqNo());

            if (imageList != null && imageList.size() > 0) {

                for (LuckyBoxReviewImage image : imageList) {
                    image.setLuckyBoxReviewSeqNo(luckyBoxReview.getSeqNo());
                    image.setType("thumbnail");
                    luckyBoxReviewImageRepository.save(image);
                }
            }
            return Const.E_SUCCESS;
        } catch (Exception e) {
            logger.error(e.toString());
            throw new InvalidCashException();
        }
    }

    public Page<LuckyBoxReviewDetail> getLuckyBoxReviewList(User user, Pageable pageable) {
        if (user == null) {
            return luckyBoxReviewDetailRepository.findAllBy(null, pageable);
        }
        return luckyBoxReviewDetailRepository.findAllBy(user.getNo(), pageable);
    }

    public Page<LuckyBoxReviewDetail> getMyLuckyBoxReviewList(User user, Pageable pageable) {
        return luckyBoxReviewDetailRepository.findAllMy(user.getNo(), pageable);
    }

    public LuckyBoxReviewDetail getLuckyBoxReview(User user, Long seqNo) {
        if (user == null) {
            return luckyBoxReviewDetailRepository.findBySeqNo(seqNo, null);
        }
        return luckyBoxReviewDetailRepository.findBySeqNo(seqNo, user.getNo());
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deliveryComplete() {

        try {
            List<LuckyBoxPurchaseItemOnly> itemList = luckyBoxPurchaseItemOnlyRepository.findAllByDeliveryStatus(2);

            String dateStr = AppUtil.localDatetimeNowString();
            Map<String, String> params;
            String apiKey = commonService.getShippingKey();
            for (LuckyBoxPurchaseItemOnly item : itemList) {
                LuckyBoxDelivery luckyBoxDelivery = luckyBoxDeliveryRepository.findBySeqNo(item.getLuckyboxDeliverySeqNo());
                if (AppUtil.isEmpty(luckyBoxDelivery.getShippingCompanyCode()) || org.springframework.util.StringUtils.isEmpty(luckyBoxDelivery.getTransportNumber())) {
                    continue;
                }


                params = new HashMap<>();
                params.put("t_key", apiKey);
                params.put("t_code", luckyBoxDelivery.getShippingCompanyCode());
                params.put("t_invoice", luckyBoxDelivery.getTransportNumber());
                String res = getRequest(BASE_URL + "api/v1/trackingInfo", params, "UTF-8", 30000, 30000);
                logger.debug("tracking result = " + res);
                Gson gson = new Gson();
                JsonObject jsonObj = gson.fromJson(res, JsonElement.class).getAsJsonObject();

                JsonElement completeElement = jsonObj.get("completeYN");
                if (completeElement != null) {
                    String completeYN = completeElement.getAsString();
                    if (completeYN.equals("Y")) {
                        luckyBoxDelivery.setDeliveryCompleteDatetime(dateStr);
                        luckyBoxDeliveryRepository.save(luckyBoxDelivery);

                        item.setDeliveryStatus(3);
                        item.setCompleteDatetime(dateStr);
                        luckyBoxPurchaseItemOnlyRepository.save(item);
                    }
                }
            }
        } catch (Exception e) {
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

        logger.debug("url : " + urlStr);

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
