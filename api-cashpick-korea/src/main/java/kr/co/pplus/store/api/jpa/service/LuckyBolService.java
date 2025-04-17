package kr.co.pplus.store.api.jpa.service;

import com.google.gson.JsonObject;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.model.reappay.ReapPayBillKeyData;
import kr.co.pplus.store.api.jpa.model.reappay.ReapPayCancelData;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.util.Filtering;
import kr.co.pplus.store.util.RedisUtil;
import kr.co.pplus.store.util.StoreUtil;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class LuckyBolService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(LuckyBolService.class);

    @Autowired
    LuckyBolRepository luckyBolRepository;

    @Autowired
    LuckyBolPurchaseRepository luckyBolPurchaseRepository;

    @Autowired
    LuckyBolProductRepository luckyBolProductRepository;

    @Autowired
    LuckyBolNumberRepository luckyBolNumberRepository;

    @Autowired
    LuckyBolGiftRepository luckyBolGiftRepository;

    @Autowired
    LuckyBolWinRepository luckyBolWinRepository;

    @Autowired
    LuckyBolWinOnlyRepository luckyBolWinOnlyRepository;

    @Autowired
    LuckyBolReplyRepository luckyBolReplyRepository;

    @Autowired
    LuckyBolReplyOnlyRepository luckyBolReplyOnlyRepository;

    @Autowired
    ReapPayService reapPayService;

    @Autowired
    GiftishowService giftishowService;

    @Autowired
    BolService bolService;

    @Autowired
    MemberService memberService;

    @Value("${STORE.REDIS_PREFIX}")
    String REDIS_PREFIX = "pplus-";

    @Value("${STORE.TYPE}")
    String storeType = "STAGE";

    public LuckyBol getActiveLuckyBol() {
        String dateStr = AppUtil.localDatetimeNowString();
        List<String> statusList = new ArrayList<>();
        statusList.add("active");
        return luckyBolRepository.findFirstByStatusInAndEngageTypeAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualOrderBySeqNoAsc(statusList, "bol", dateStr, dateStr);
    }

    public LuckyBol getConcludeLuckyBol() {
        String dateStr = AppUtil.localDatetimeNowString();
        List<String> statusList = new ArrayList<>();
        statusList.add("conclude");
        statusList.add("pending");
        return luckyBolRepository.findFirstByStatusInAndEngageTypeAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualOrderBySeqNoAsc(statusList, "bol", dateStr, dateStr);
    }

    public List<LuckyBol> getActiveLuckyBolList() {
        String dateStr = AppUtil.localDatetimeNowString();
        List<String> statusList = new ArrayList<>();
        statusList.add("active");
        statusList.add("conclude");
        statusList.add("pending");
        return luckyBolRepository.findAllByStatusInAndEngageTypeAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqual(statusList, "purchase", dateStr, dateStr);
    }

    public Page<LuckyBol> getAnnouncedLuckyBolList(Pageable pageable) {

        List<String> statusList = new ArrayList<>();
        statusList.add("completed");

        return luckyBolRepository.findAllByStatusInOrderBySeqNoDesc(statusList, pageable);
    }

    public LuckyBol getLuckyBol(Long seqNo) {
        return luckyBolRepository.findBySeqNo(seqNo);
    }

    public LuckyBolPurchase getLuckyBolPurchase(Long seqNo) {
        return luckyBolPurchaseRepository.findBySeqNo(seqNo);
    }

    public LuckyBolPurchase getLuckyBolPurchaseByPurchaseSeqNo(Long purchaseSeqNo) {
        return luckyBolPurchaseRepository.findByPurchaseSeqNo(purchaseSeqNo);
    }

    public Map<String, String> getLuckyBolColor() {

        Map<String, String> map = new HashMap<>();
        map.put("A", "빨강,#FF2020");
        map.put("B", "노랑,#FFC700");
        map.put("C", "하늘,#76BDFF");
        map.put("D", "주황,#FF5C00");
        map.put("E", "파랑,#00168B");
        map.put("F", "보라,#5F05A5");
        map.put("G", "분홍,#FF6CF0");
        map.put("H", "회색,#666666");
        map.put("I", "검정,#000000");
        map.put("J", "갈색,#874413");
        return map;
    }

    public String getJoinNumber(Long luckyBolSeqNo) {

        String winNumber = luckyBolPurchaseRepository.findWinNumber(luckyBolSeqNo);

        if (winNumber == null) {
            winNumber = "";
        }

        StringBuilder builder = new StringBuilder(winNumber);

        String pattern = REDIS_PREFIX + "luckyBol" + luckyBolSeqNo;

        List<String> keyList = RedisUtil.getInstance().getKeys(pattern);

        if (keyList != null) {
            logger.error("keys.size() : " + keyList.size());

            for (String key : keyList) {
                logger.error("key : " + key);
                String value = RedisUtil.getInstance().hGet(key, "luckyBolNumber");
                builder.append(":" + value);
            }

            builder.append(":");
        }

        return builder.toString();
    }

    public boolean checkValidNumber(Long luckyBolSeqNo, String number) {

        boolean isPossible = true;

        String uniqueKey = luckyBolSeqNo + "|" + number;

        LuckyBolNumber luckyBolNumber = luckyBolNumberRepository.findByUniqueKey(uniqueKey);
        if (luckyBolNumber.getUsed()) {
            return false;
        }

        String key = REDIS_PREFIX + "luckyBol" + luckyBolSeqNo + number;

        if (RedisUtil.getInstance().hGet(key, "luckyBolNumber") == null) {
            RedisUtil.getInstance().hSet(key, "luckyBolNumber", number);
            RedisUtil.getInstance().redisConnectionExpire(key, 600);
        } else {
            isPossible = false;
        }

        return isPossible;
    }

    public String selectRandomNumber(Long luckyBolSeqNo) {

        long remainCount = luckyBolNumberRepository.countByLuckyBolSeqNoAndUsed(luckyBolSeqNo, false);

        if (remainCount <= 100) {
            List<LuckyBolNumber> luckyBolNumberList = luckyBolNumberRepository.findAllByLuckyBolSeqNoAndUsed(luckyBolSeqNo, false);
            Collections.shuffle(luckyBolNumberList);

            for (LuckyBolNumber luckyBolNumber : luckyBolNumberList) {

                String key = REDIS_PREFIX + "luckyBol" + luckyBolSeqNo + luckyBolNumber.getWinNumber();

                if (RedisUtil.getInstance().hGet(key, "luckyBolNumber") == null) {
                    RedisUtil.getInstance().hSet(key, "luckyBolNumber", luckyBolNumber.getWinNumber());
                    RedisUtil.getInstance().redisConnectionExpire(key, 600);
                    return luckyBolNumber.getWinNumber();
                }
            }

            return "";

        } else {
            LuckyBolNumber luckyBolNumber = null;

            boolean selected = false;

            do {
                luckyBolNumber = luckyBolNumberRepository.findRandom(luckyBolSeqNo);

                String key = REDIS_PREFIX + "luckyBol" + luckyBolSeqNo + luckyBolNumber.getWinNumber();

                if (RedisUtil.getInstance().hGet(key, "luckyBolNumber") == null) {
                    RedisUtil.getInstance().hSet(key, "luckyBolNumber", luckyBolNumber.getWinNumber());
                    RedisUtil.getInstance().redisConnectionExpire(key, 600);
                    selected = true;
                }

            } while (!selected);


            return luckyBolNumber.getWinNumber();
        }
    }

    public void deleteSelectNumber(Long luckyBolSeqNo, String number) {

        String[] numbers = number.split(",");
        for (String winNumber : numbers) {
            String key = REDIS_PREFIX + "luckyBol" + luckyBolSeqNo + winNumber;
            RedisUtil.getInstance().hDel(key, "luckyBolNumber");
        }
    }

    public List<Integer> getLuckyBolProductGroup(Long luckyBolSeqNo) {
        return luckyBolProductRepository.findDistinctExchangePrice(luckyBolSeqNo);
    }

    public Page<LuckyBolProduct> getLuckyBolProductList(Long luckyBolSeqNo, Integer exchangePrice, Pageable pageable) {
        return luckyBolProductRepository.findAllByLuckyBolSeqNoAndExchangePrice(luckyBolSeqNo, exchangePrice, pageable);
    }

    public Page<LuckyBolGift> getLuckyBolGiftList(Long luckyBolSeqNo, Pageable pageable) {
        return luckyBolGiftRepository.findAllByLuckyBolSeqNo(luckyBolSeqNo, pageable);
    }

    public LuckyBolProduct getLuckyBolDelegateProduct(Long luckyBolSeqNo) {
        return luckyBolProductRepository.findFirstByLuckyBolSeqNoOrderByDelegateDesc(luckyBolSeqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public LuckyBolPurchase saveLuckyBolPurchase(User user, LuckyBolPurchase luckyBolPurchase) throws ResultCodeException {

        if (!user.getNo().equals(luckyBolPurchase.getMemberSeqNo())) {
            throw new NotPermissionException();
        }


        String[] winNumbers = luckyBolPurchase.getWinNumber().split(":");
        List<String> uniqueKeyList = new ArrayList<>();
        for (String number : winNumbers) {
            if (!AppUtil.isEmpty(number)) {
                String uniqueKey = luckyBolPurchase.getLuckyBolSeqNo() + "|" + number;
                uniqueKeyList.add(uniqueKey);
            }
        }

        boolean exist = luckyBolNumberRepository.existsByUniqueKeyInAndUsed(uniqueKeyList, true);
        if (exist) {
            throw new AlreadyExistsException();
        }

        LuckyBol luckyBol = getLuckyBol(luckyBolPurchase.getLuckyBolSeqNo());

        String dateStr = AppUtil.localDatetimeNowString();

        try {
            luckyBolPurchase.setOrderId(StoreUtil.getRandomOrderId());
        } catch (Exception e) {
            logger.error(e.toString());
        }


        luckyBolPurchase.setEngagedCount(uniqueKeyList.size());
        luckyBolPurchase.setEngageType(luckyBol.getEngageType());

        if (luckyBol.getEngageType().equals("purchase")) {
            luckyBolPurchase.setEngagedPrice(luckyBol.getEngagePrice() * uniqueKeyList.size());
            luckyBolPurchase.setPg("ReapPay");
            luckyBolPurchase.setStatus("before");
        } else if (luckyBol.getEngageType().equals("bol")) {
            luckyBolPurchase.setEngagedPrice(luckyBol.getEngageBol() * uniqueKeyList.size());


            Member member = memberService.getMemberBySeqNo(luckyBolPurchase.getMemberSeqNo());
            if (luckyBolPurchase.getEngagedPrice() > member.getBol()) {
                throw new LackCostException();
            }

            luckyBolPurchase.setPg("bol");
            luckyBolPurchase.setPayMethod("bol");
            luckyBolPurchase.setStatus("active");

            luckyBolNumberRepository.updateLuckyBolNumberUse(uniqueKeyList);

            long remainCount = luckyBolNumberRepository.countByLuckyBolSeqNoAndUsed(luckyBolPurchase.getLuckyBolSeqNo(), false);
            if (remainCount == 0) {
                luckyBolRepository.updateLuckyBolStatus(luckyBolPurchase.getLuckyBolSeqNo(), "conclude");
            }

            BolHistory bolHistory = new BolHistory();
            bolHistory.setAmount(luckyBolPurchase.getEngagedPrice().floatValue());
            bolHistory.setMemberSeqNo(luckyBolPurchase.getMemberSeqNo());
            bolHistory.setSubject(luckyBol.getTitle() + " 응모");
            bolHistory.setPrimaryType("decrease");
            bolHistory.setSecondaryType("joinEvent");
            bolHistory.setTargetType("member");
            bolHistory.setTargetSeqNo(luckyBolPurchase.getMemberSeqNo());
//            bolHistory.setHistoryProp(new HashMap<String, Object>());
//            bolHistory.getHistoryProp().put("적립유형", "이벤트 당첨후기 작성");

            bolService.decreaseBol(luckyBolPurchase.getMemberSeqNo(), bolHistory);
        }


        luckyBolPurchase.setRegDatetime(dateStr);


        luckyBolPurchase = luckyBolPurchaseRepository.save(luckyBolPurchase);
        return luckyBolPurchase;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void paymentLuckyBolPurchase(HttpServletRequest request) throws ResultCodeException {
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

            LuckyBolPurchase luckyBolPurchase = luckyBolPurchaseRepository.findByOrderId(orderSeq);

            String paymentDate = appDt + " " + appTm;
            luckyBolPurchase.setModDatetime(paymentDate);
            luckyBolPurchase.setPayResponseApprovalNo(appNo);
            luckyBolPurchase.setPayResponseCardId(issCd);
            luckyBolPurchase.setPayResponseCardNm(issNm);
            luckyBolPurchase.setPayResponseCardNo(cardNum);
            luckyBolPurchase.setPayResponseCertYn(true);
            luckyBolPurchase.setPayResponseCode(resultStatus);
            luckyBolPurchase.setPayResponseInstallment(installment);
            luckyBolPurchase.setPayResponseOrderNo(orderSeq);
            luckyBolPurchase.setPayResponsePayDate(appDt);
            luckyBolPurchase.setPayResponsePayTime(appTm);
            luckyBolPurchase.setPayResponsePayType("card");
            luckyBolPurchase.setPayResponseProductType("R");
            luckyBolPurchase.setPayResponseSellMm(installment);
            if (storeType.equals("PROD")) {
                luckyBolPurchase.setPayResponseTestYn(false);
            } else {
                luckyBolPurchase.setPayResponseTestYn(true);
            }

            luckyBolPurchase.setPayResponseTranSeq(tranSeq);
            luckyBolPurchase.setPayResponseZerofeeYn(false);

            luckyBolPurchase.setPgTranId(tranSeq);
            luckyBolPurchase.setExpireDatetime(AppUtil.localDatetimeNowPlusDayString(1));

            String[] winNumbers = luckyBolPurchase.getWinNumber().split(":");
            List<String> uniqueKeyList = new ArrayList<>();
            for (String number : winNumbers) {
                if (!AppUtil.isEmpty(number)) {
                    String uniqueKey = luckyBolPurchase.getLuckyBolSeqNo() + "|" + number;
                    uniqueKeyList.add(uniqueKey);
                }
            }

            boolean exist = luckyBolNumberRepository.existsByUniqueKeyInAndUsed(uniqueKeyList, true);
            if (exist) {
                ReapPayCancelData data = reapPayService.cancel(tranSeq, 1L);
                luckyBolPurchase.setStatus("fail");
                luckyBolPurchase = luckyBolPurchaseRepository.save(luckyBolPurchase);

            } else {
                luckyBolPurchase.setStatus("active");

                luckyBolPurchase = luckyBolPurchaseRepository.save(luckyBolPurchase);


                luckyBolNumberRepository.updateLuckyBolNumberUse(uniqueKeyList);

                long remainCount = luckyBolNumberRepository.countByLuckyBolSeqNoAndUsed(luckyBolPurchase.getLuckyBolSeqNo(), false);
                if (remainCount == 0) {
                    luckyBolRepository.updateLuckyBolStatus(luckyBolPurchase.getLuckyBolSeqNo(), "conclude");
                }
            }
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void paymentBillKeyLuckyBolPurchase(Long seqNo, String token, String installment) throws ResultCodeException {


        LuckyBolPurchase luckyBolPurchase = luckyBolPurchaseRepository.findBySeqNo(seqNo);

        String[] winNumbers = luckyBolPurchase.getWinNumber().split(":");
        List<String> uniqueKeyList = new ArrayList<>();
        for (String number : winNumbers) {
            if (!AppUtil.isEmpty(number)) {
                String uniqueKey = luckyBolPurchase.getLuckyBolSeqNo() + "|" + number;
                uniqueKeyList.add(uniqueKey);
            }
        }

        boolean exist = luckyBolNumberRepository.existsByUniqueKeyInAndUsed(uniqueKeyList, true);
        if (exist) {
            throw new AlreadyExistsException();
        }

        ReapPayBillKeyData reapPayBillKeyData = reapPayService.billkeypayLuckyBol(luckyBolPurchase, token, installment);

        if (reapPayBillKeyData == null) {
            throw new InvalidBuyException();
        }

        String paymentDate = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = sdf.parse(reapPayBillKeyData.getBillkeytradeDateTime());

            paymentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } catch (Exception e) {
            logger.error(e.toString());
            paymentDate = AppUtil.localDatetimeNowString();
        }

        luckyBolPurchase.setStatus("active"); // 결제완료
        luckyBolPurchase.setPayResponseApprovalNo(reapPayBillKeyData.getBillkeyapprovalNumb());
        luckyBolPurchase.setPayResponseCardId(reapPayBillKeyData.getBillkeypurchaseCardType());
        luckyBolPurchase.setPayResponseCardNm(reapPayBillKeyData.getBillkeyissuerCardName());
        luckyBolPurchase.setPayResponseCardNo(reapPayBillKeyData.getBillkeymaskedCardNumb());
        luckyBolPurchase.setPayResponseCertYn(true);
        luckyBolPurchase.setPayResponseCode(reapPayBillKeyData.getBillkeyrespCode());
        luckyBolPurchase.setPayResponseInstallment(installment);
        luckyBolPurchase.setPayResponseOrderNo(luckyBolPurchase.getOrderId());
        luckyBolPurchase.setPayResponsePayDate(paymentDate.split(" ")[0]);
        luckyBolPurchase.setPayResponsePayTime(paymentDate.split(" ")[1]);
        luckyBolPurchase.setPayResponsePayType("easy");
        luckyBolPurchase.setPayResponseProductType("R");
        luckyBolPurchase.setPayResponseSellMm(installment);
        if (storeType.equals("PROD")) {
            luckyBolPurchase.setPayResponseTestYn(false);
        } else {
            luckyBolPurchase.setPayResponseTestYn(true);
        }

        luckyBolPurchase.setPayResponseTranSeq(reapPayBillKeyData.getBillkeyTranseq());
        luckyBolPurchase.setPayResponseZerofeeYn(false);

        luckyBolPurchase.setModDatetime(paymentDate);
        luckyBolPurchase.setPgTranId(luckyBolPurchase.getPayResponseTranSeq());
        luckyBolPurchase.setExpireDatetime(AppUtil.localDatetimeNowPlusDayString(1));

        luckyBolPurchase = luckyBolPurchaseRepository.save(luckyBolPurchase);

//        String[] winNumbers = luckyBolPurchase.getWinNumber().split(":");
//        List<String> uniqueKeyList = new ArrayList<>();
//        for (String winNumber : winNumbers) {
//            if (!AppUtil.isEmpty(winNumber)) {
//                String uniqueKey = luckyBolPurchase.getLuckyBolSeqNo() + "|" + winNumber;
//                uniqueKeyList.add(uniqueKey);
//            }
//        }

        luckyBolNumberRepository.updateLuckyBolNumberUse(uniqueKeyList);

        long remainCount = luckyBolNumberRepository.countByLuckyBolSeqNoAndUsed(luckyBolPurchase.getLuckyBolSeqNo(), false);
        if (remainCount == 0) {
            luckyBolRepository.updateLuckyBolStatus(luckyBolPurchase.getLuckyBolSeqNo(), "conclude");
        }
    }

    public Page<LuckyBolPurchase> getLuckyBolPurchaseListByLuckyBolSeqNo(Long memberSeqNo, Long luckyBolSeqNo, Pageable pageable) {
        return luckyBolPurchaseRepository.findAllByMemberSeqNoAndLuckyBolSeqNoAndStatusOrderBySeqNoDesc(memberSeqNo, luckyBolSeqNo, "active", pageable);
    }

    public Page<LuckyBolPurchase> getLuckyBolPurchaseList(Long memberSeqNo, Pageable pageable) {
        return luckyBolPurchaseRepository.findAllByMemberSeqNoAndStatusOrderBySeqNoDesc(memberSeqNo, "active", pageable);
    }

    public List<LuckyBolPurchase> getUnUseLuckyBolPurchaseList(Long memberSeqNo) {
        return luckyBolPurchaseRepository.findAllByMemberSeqNoAndStatusAndEngageTypeAndPurchaseSeqNoIsNullOrderBySeqNoDesc(memberSeqNo, "active", "purchase");
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updatePurchaseSeqNo(Purchase purchase) {
        luckyBolPurchaseRepository.updatePurchaseSeqNo(purchase.getLuckyBolPurchaseSeqNo(), purchase.getSeqNo());
    }


    public List<LuckyBolWin> getLuckyBolWinListByLuckyBolSeqNo(Long luckyBolSeqNo) {
        return luckyBolWinRepository.findAllByLuckyBolSeqNoAndStatusOrderByGiftGradeAsc(luckyBolSeqNo, "active");
    }

    public List<LuckyBolWin> getLuckyBolMyWinList(Long memberSeqNo, Long luckyBolSeqNo) {
        return luckyBolWinRepository.findAllByLuckyBolSeqNoAndStatusAndMemberSeqNo(luckyBolSeqNo, "active", memberSeqNo);
    }

    public Page<LuckyBolWin> getLuckyBolWinList(Pageable pageable) {
        return luckyBolWinRepository.findAllByStatus("active", pageable);
    }

    public LuckyBolWin getLuckyBolWin(Long seqNo) {
        return luckyBolWinRepository.findBySeqNo(seqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void updateLuckyBolWinImpression(User user, Long seqNo, String impression) throws ResultCodeException {

        if (AppUtil.isEmpty(Filtering.filter(impression))) {
            throw new NotPermissionException();
        }

        LuckyBolWinOnly luckyBolWin = luckyBolWinOnlyRepository.findBySeqNo(seqNo);

        if (AppUtil.isEmpty(luckyBolWin.getImpression())) {
            LuckyBolGift luckyBolGift = luckyBolGiftRepository.findBySeqNo(luckyBolWin.getLuckyBolGiftSeqNo());
            if (luckyBolGift.getType().equals("mobileGift") && luckyBolGift.getAutoSend() != null && luckyBolGift.getAutoSend() && luckyBolGift.getGiftishowSeqNo() != null) {

                Giftishow giftishow = giftishowService.getGiftishow(luckyBolGift.getGiftishowSeqNo());

                String trId = giftishowService.getTrId();
                String mobile = user.getMobile().replace("luckyball##", "").replace("biz##", "");
                JsonObject resultObject = null;
                resultObject = giftishowService.send(giftishow.getGoodsCode(), trId, "캐시픽 이벤트 당첨", "캐시픽 이벤트 당첨을 축하드립니다. \n경품에 당첨되신 기념으로  구글플레이스토어에\n리뷰를 남겨주시면 캐시픽에 큰 힘이됩니다.\n\nhttps://play.google.com/store/apps/details?id=com.pplus.luckybol", mobile);

                if (resultObject != null) {
                    String orderNo = resultObject.get("orderNo").getAsString();
                    luckyBolWin.setGiftTrId(trId);
                    luckyBolWin.setGiftOrderNo(orderNo);
                    luckyBolWin.setGiftMobileNumber(mobile);
                } else {
                    throw new GiftishowException();
                }
            }
        }

        luckyBolWin.setImpression(Filtering.filter(impression));
        luckyBolWinOnlyRepository.save(luckyBolWin);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertLuckyBolReply(LuckyBolReplyOnly reply) throws ResultCodeException {

        reply.setReply(Filtering.filter(reply.getReply()));

        String dateStr = AppUtil.localDatetimeNowString();
        reply.setSeqNo(null);
        reply.setRegDatetime(dateStr);
        reply.setModDatetime(dateStr);
        reply.setStatus(1);
        reply = luckyBolReplyOnlyRepository.save(reply);
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateLuckyBolReply(LuckyBolReplyOnly reply) throws ResultCodeException {

        reply.setReply(Filtering.filter(reply.getReply()));

        String dateStr = AppUtil.localDatetimeNowString();
        reply.setModDatetime(dateStr);
        reply.setStatus(1);
        reply = luckyBolReplyOnlyRepository.save(reply);
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteLuckyBolReply(User user, Long seqNo) throws ResultCodeException {
        LuckyBolReplyOnly reply = luckyBolReplyOnlyRepository.findBySeqNo(seqNo);
        if (!reply.getMemberSeqNo().equals(user.getNo())) {
            throw new NotPermissionException();
        }
        reply.setStatus(-999);
        luckyBolReplyOnlyRepository.save(reply);
        return Const.E_SUCCESS;
    }

    public Page<LuckyBolReply> getLuckyBolReplyListByLuckyBolWinSeqNo(Long luckyBolWinSeqNo, Pageable pageable) {
        return luckyBolReplyRepository.findAllByLuckyBolWinSeqNoAndStatusOrderBySeqNoAsc(luckyBolWinSeqNo, 1, pageable);
    }

    public Page<LuckyBolReply> getLuckyBolReplyListByLuckyBolReviewSeqNo(Long luckyBolReviewSeqNo, Pageable pageable) {
        return luckyBolReplyRepository.findAllByLuckyBolReviewSeqNoAndStatusOrderBySeqNoAsc(luckyBolReviewSeqNo, 1, pageable);
    }
}
